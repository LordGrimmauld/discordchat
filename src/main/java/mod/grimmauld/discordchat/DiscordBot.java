package mod.grimmauld.discordchat;

import mod.grimmauld.discordchat.slashcommand.CommandRegistry;
import mod.grimmauld.discordchat.util.CommandSourceRedirectedOutput;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.stream.StreamSupport;

@ParametersAreNonnullByDefault
public class DiscordBot extends ListenerAdapter {
	@Nullable
	public final JDA jda;

	public DiscordBot() {
		this(Config.TOKEN.get());
	}

	public DiscordBot(String token) {
		@Nullable JDA tmpJDA;
		try {


			tmpJDA = JDABuilder.createDefault(token)
				.addEventListeners(this)
				.build();
			DiscordChat.LOGGER.debug("launched discord bot");


		} catch (Exception e) {
			DiscordChat.LOGGER.error("could not launch discord bot: ", e);
			tmpJDA = null;
		}
		jda = tmpJDA;
	}

	public static boolean isOp(@Nullable Member member) {
		return member != null && (!member.getUser().isBot() && member.getRoles().stream().anyMatch(role -> role.getName().equals(Config.OP_ROLE_NAME.get())));
	}

	@Override
	public void onSlashCommand(SlashCommandEvent event) {
		StreamSupport.stream(CommandRegistry.COMMAND_REGISTRY.get().spliterator(), false)
			.filter(grimmSlashCommand -> grimmSlashCommand.getName().equals(event.getName()))
			.forEach(grimmSlashCommand -> grimmSlashCommand.execute(event));
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		Message msg = event.getMessage();

		if (!msg.getChannel().getId().equals(Config.REDIRECT_CHANNEL_ID.get()) || msg.getContentRaw().startsWith(Config.PREFIX.get()) || msg.getAuthor().isBot() || event.getMember() == null)
			return;

		IFormattableTextComponent playerNameComponent = new StringTextComponent("[")
			.append(new StringTextComponent(msg.getAuthor().getName()).setStyle(Style.EMPTY.withColor(net.minecraft.util.text.Color.fromRgb(event.getMember().getColorRaw()))))
			.append("] ");

		if (!msg.getContentStripped().isEmpty())
			DiscordChat.SERVER_INSTANCE.ifPresentAndAlive(server -> server
				.getPlayerList()
				.getPlayers()
				.forEach(player -> player.sendMessage(playerNameComponent.copy().append(sanitize(msg.getContentStripped()))
					.withStyle(style -> style.withClickEvent(null)), player.getUUID())));
		msg.getAttachments().forEach(attachment -> DiscordChat.SERVER_INSTANCE.ifPresentAndAlive(server -> server
			.getPlayerList()
			.getPlayers()
			.stream()
			.distinct()
			.forEach(player -> player.sendMessage(
				playerNameComponent.copy().append("Uploaded a file: ")
					.append(new StringTextComponent(sanitize(attachment.getUrl()))
						.withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, attachment.getUrl())))
						.withStyle(TextFormatting.BLUE)
						.withStyle(TextFormatting.UNDERLINE)), player.getUUID()))));
	}

	public void shutdown() {
		if (jda != null && jda.getStatus() != JDA.Status.SHUTDOWN && jda.getStatus() != JDA.Status.SHUTTING_DOWN)
			jda.shutdownNow();
	}

	@Override
	public void onReady(@NotNull ReadyEvent event) {
		super.onReady(event);
		getGuild().ifPresent(guild -> CommandRegistry.COMMAND_REGISTRY.get().forEach(grimmSlashCommand -> guild.upsertCommand(grimmSlashCommand.getCommandData()).submit()));
	}

	@Override
	public void onGuildMessageReactionAdd(@NotNull GuildMessageReactionAddEvent event) {
		String content = event.retrieveMessage().complete().getContentRaw();
		if (CommandRegistry.WHITELIST_COMMAND == null) {
			event.getChannel().sendMessage("Can't whitelist player: Can't find whitelist command").submit();
			return;
		}
		if (!content.startsWith(Config.PREFIX.get() + CommandRegistry.WHITELIST_COMMAND.getName()) || !event.getReactionEmote().getName().equals("\u2705") || !isOp(event.getMember()))
			return;
		String[] playerName = content.split(" ");

		if (playerName.length < 2) {
			event.getChannel().sendMessage("Can't whitelist player: Can't extract player name").submit();
			return;
		}

		StringBuilder builder = new StringBuilder();
		DiscordChat.SERVER_INSTANCE.ifPresentAndAlive(server -> server.getCommands().performCommand(CommandSourceRedirectedOutput.of(server.createCommandSourceStack())
			.withName(event.getMember().getUser().getName())
			.withHook(text -> builder.append(text.getString()).append("\n")), "whitelist add " + playerName[1]));

		event.getChannel().sendMessage(builder.toString()).submit();
	}

	private String sanitize(String s) {
		s = org.apache.commons.lang3.StringUtils.normalizeSpace(s);

		for (int i = 0; i < s.length(); ++i) {
			if (!SharedConstants.isAllowedChatCharacter(s.charAt(i))) {
				return "ILLEGAL";
			}
		}
		return s;
	}

	private Optional<Guild> getGuild() {
		if (jda == null)
			return Optional.empty();
		String channelId = Config.REDIRECT_CHANNEL_ID.get();
		if (channelId.isEmpty())
			return Optional.empty();

		TextChannel channel = jda.getTextChannelById(channelId);
		if (channel == null)
			return Optional.empty();
		return Optional.of(channel.getGuild());
	}
}
