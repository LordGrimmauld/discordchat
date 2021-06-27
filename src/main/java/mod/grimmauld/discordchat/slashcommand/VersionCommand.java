package mod.grimmauld.discordchat.slashcommand;

import mod.grimmauld.discordchat.DiscordChat;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.jetbrains.annotations.Nullable;

public class VersionCommand extends GrimmSlashCommand {
	public VersionCommand(@Nullable String help, boolean global) {
		super(help, global);
	}

	@Override
	protected void executeChecked(SlashCommandEvent event) {
		sendResponse(event, "Discord Integration is on version " + DiscordChat.VERSION, true);
	}
}
