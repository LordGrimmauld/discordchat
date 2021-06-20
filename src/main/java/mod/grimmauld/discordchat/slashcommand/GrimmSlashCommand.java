package mod.grimmauld.discordchat.slashcommand;


import mod.grimmauld.discordchat.Config;
import mod.grimmauld.discordchat.DiscordChat;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.BiFunction;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
public abstract class GrimmSlashCommand implements IForgeRegistryEntry<GrimmSlashCommand> {

	@Nullable
	protected final String help;
	protected final boolean global;
	protected ResourceLocation id;

	protected GrimmSlashCommand(@Nullable String help, boolean global) {
		this.help = help;
		this.global = global;
	}

	@Nonnull
	@Override
	public ResourceLocation getRegistryName() {
		return id;
	}

	@Override
	public GrimmSlashCommand setRegistryName(ResourceLocation name) {
		id = name;
		return this;
	}

	@Override
	public Class<GrimmSlashCommand> getRegistryType() {
		return GrimmSlashCommand.class;
	}

	public void execute(SlashCommandEvent event) {
		if (!(global || event.getChannel().getId().equals(Config.REDIRECT_CHANNEL_ID.get())))
			return;

		try {
			executeChecked(event);
		} catch (Exception e) {
			DiscordChat.LOGGER.error("Failed to parse command: {}", e);
			sendResponse(event, "Ooops, something went wrong! Contact an Admin or try again later i guess....", true);
		}
	}

	protected abstract void executeChecked(SlashCommandEvent event) throws Exception;

	protected void sendResponse(SlashCommandEvent event, String msg, boolean ephermal) {
		if (msg.isEmpty())
			return;

		while (msg.length() > 1990) {
			event.reply(msg.substring(0, 1990)).setEphemeral(ephermal).submit();
			msg = msg.substring(1990);
		}
		event.reply(msg).setEphemeral(ephermal).submit();
	}

	protected void sendEmbedResponse(SlashCommandEvent event, EmbedBuilder eb, boolean ephermal) {
		MessageEmbed msg = eb.build();
		if (msg.isEmpty())
			return;

		event.replyEmbeds(msg).setEphemeral(ephermal).submit();
	}

	public CommandData getCommandData() {
		return attachExtraData(new CommandData(getName(), getHelp()));
	}

	protected CommandData attachExtraData(CommandData data) {
		return data;
	}


	protected boolean sendNoServerResponse(SlashCommandEvent event) {
		sendResponse(event, "Failed to communicate with server", true);
		return false;
	}

	protected boolean sendLackingPermissionResponse(SlashCommandEvent event) {
		sendResponse(event, "You need operator role to run this command!", true);
		return false;
	}


	public String getName() {
		return id.getPath();
	}

	@Nonnull
	public String getHelp() {
		return help != null ? help : "no help can be provided";
	}

	public static class Builder<T extends GrimmSlashCommand> {
		private final Supplier<BiFunction<String, Boolean, T>> commandSupplier;
		private boolean global = false;

		@Nullable
		private String help = null;

		public Builder(BiFunction<String, Boolean, T> commandSupplier) {
			this(() -> commandSupplier);
		}

		public Builder(Supplier<BiFunction<String, Boolean, T>> commandSupplier) {
			this.commandSupplier = commandSupplier;
		}

		public Builder<T> withHelp(String help) {
			this.help = help;
			return this;
		}

		public Builder<T> global() {
			this.global = true;
			return this;
		}

		public ConditionalBuilder<T> withCondition(boolean shouldBuild) {
			ConditionalBuilder<T> builder = new ConditionalBuilder<>(commandSupplier, shouldBuild);
			if (global)
				builder.global();
			if (help != null)
				builder.withHelp(help);
			return builder;
		}

		public GrimmSlashCommand build(ResourceLocation resourceLocation) {
			return commandSupplier.get().apply(help, global).setRegistryName(resourceLocation);
		}
	}

	public static final class ConditionalBuilder<T extends GrimmSlashCommand> extends Builder<T> {

		private final boolean shouldBuild;


		private ConditionalBuilder(Supplier<BiFunction<String, Boolean, T>> commandSupplier, boolean shouldBuild) {
			super(commandSupplier);
			this.shouldBuild = shouldBuild;
		}

		@Override
		public ConditionalBuilder<T> withHelp(String help) {
			super.withHelp(help);
			return this;
		}

		@Override
		public ConditionalBuilder<T> global() {
			super.global();
			return this;
		}

		@Nullable
		@Override
		public GrimmSlashCommand build(ResourceLocation resourceLocation) {
			if (shouldBuild)
				return super.build(resourceLocation);
			return null;
		}
	}
}
