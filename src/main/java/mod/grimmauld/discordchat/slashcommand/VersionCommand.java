package mod.grimmauld.discordchat.slashcommand;

import mod.grimmauld.discordchat.BuildConfig;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class VersionCommand extends GrimmSlashCommand {
	public VersionCommand(@Nullable String help, boolean global) {
		super(help, global);
	}

	@Override
	protected void executeChecked(SlashCommandEvent event) {
		sendResponse(event, "Discord Integration is on version " + BuildConfig.VERSION, true);
	}
}
