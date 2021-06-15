package mod.grimmauld.discordchat.slashcommand;


import mcp.MethodsReturnNonnullByDefault;
import mod.grimmauld.discordchat.Config;
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

@MethodsReturnNonnullByDefault
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

		executeChecked(event);
	}

	protected abstract void executeChecked(SlashCommandEvent event);

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

	public static final class Builder<T extends GrimmSlashCommand> {
		private final BiFunction<String, Boolean, T> commandSupplier;
		private boolean global = false;

		@Nullable
		private String help = null;

		public Builder(BiFunction<String, Boolean, T> commandSupplier) {
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

		public GrimmSlashCommand build(ResourceLocation resourceLocation) {
			return commandSupplier.apply(help, global).setRegistryName(resourceLocation);
		}
	}
}
