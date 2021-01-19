package mod.grimmauld.discordchat;

import mod.grimmauld.discordchat.discordCommand.AllDiscordCommands;
import mod.grimmauld.discordchat.discordCommand.WhitelistCommand;
import mod.grimmauld.discordchat.util.CommandSourceRedirectedOutput;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DiscordBot extends ListenerAdapter {
	@Nullable
	public final JDA jda;

	public DiscordBot(String token) {
		@Nullable JDA tmpJDA;
		try {


			tmpJDA = JDABuilder.createDefault(token)
				.addEventListeners(AllDiscordCommands.getCommandClient())
				.addEventListeners(this)
				.build();
			// tmpJDA.awaitReady();
			DiscordChat.LOGGER.debug("launched discord bot");


		} catch (Exception e) {
			DiscordChat.LOGGER.error("could not launch discord bot: " + e);
			tmpJDA = null;
		}
		jda = tmpJDA;
	}

	public static boolean isOp(Member member) {
		return !member.getUser().isBot() && member.getRoles().stream().anyMatch(role -> role.getName().equals(Config.OP_ROLE_NAME.get()));
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		Message msg = event.getMessage();

		if (!msg.getChannel().getId().equals(Config.REDIRECT_CHANNEL_ID.get()) || msg.getContentRaw().startsWith(Config.PREFIX.get()) || msg.getAuthor().isBot() || DiscordChat.SERVER_INSTANCE == null)
			return;

		if (!msg.getContentStripped().isEmpty())
			DiscordChat.SERVER_INSTANCE.getPlayerList().sendMessage(new StringTextComponent("[" + TextFormatting.GOLD + "D " + TextFormatting.AQUA + msg.getAuthor().getName() + TextFormatting.WHITE + "] " + msg.getContentStripped()));
		msg.getAttachments().forEach(attachment -> DiscordChat.SERVER_INSTANCE.getPlayerList().sendMessage(new StringTextComponent("[" + TextFormatting.GOLD + "D " + TextFormatting.AQUA + msg.getAuthor().getName() + TextFormatting.WHITE + "] Uploaded a file: ").appendSibling(new StringTextComponent(attachment.getUrl()).applyTextStyle(TextFormatting.BLUE).applyTextStyle(TextFormatting.UNDERLINE).applyTextStyle(style -> style.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, attachment.getUrl()))))));
	}

	public void shutdown() {
		if (jda != null)
			jda.shutdownNow();
	}

	@Override
	public void onGuildMessageReactionAdd(@NotNull GuildMessageReactionAddEvent event) {
		String content = event.retrieveMessage().complete().getContentRaw();
		if (!content.startsWith(Config.PREFIX.get() + WhitelistCommand.NAME) || !event.getReactionEmote().getName().equals("\u2705") || !isOp(event.getMember()) || DiscordChat.SERVER_INSTANCE == null)
			return;
		String[] playerName = content.split(" ");

		if (playerName.length < 2) {
			event.getChannel().sendMessage("Can't whitelist player: Can't extract player name").submit();
			return;
		}

		StringBuilder builder = new StringBuilder();
		DiscordChat.SERVER_INSTANCE.getCommandManager().handleCommand(CommandSourceRedirectedOutput.of(DiscordChat.SERVER_INSTANCE.getCommandSource(),
			text -> builder.append(text.getString()).append("\n")), "whitelist add " + playerName[1]);

		event.getChannel().sendMessage(builder.toString()).submit();
	}
}
