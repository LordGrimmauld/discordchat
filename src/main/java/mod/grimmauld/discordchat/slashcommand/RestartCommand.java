package mod.grimmauld.discordchat.slashcommand;

import mod.grimmauld.discordchat.Config;
import mod.grimmauld.discordchat.DiscordBot;
import mod.grimmauld.discordchat.DiscordChat;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@ParametersAreNonnullByDefault
public class RestartCommand extends GrimmSlashCommand {
	public RestartCommand(@Nullable String help, boolean global) {
		super(help, global);
	}

	@Override
	protected void executeChecked(SlashCommandEvent event) {
		if (!DiscordBot.isOp(event.getMember())) {
			sendLackingPermissionResponse(event);
			return;
		}

		Path shPath = Paths.get(Config.RESTART_SH.get()).toAbsolutePath();

		if (!Files.exists(shPath)) {
			sendResponse(event, "No file could be found in path " + shPath, true);
			return;
		}

		if (!Files.isReadable(shPath)) {
			sendResponse(event, "File specified at " + shPath + " is not readable", true);
			return;
		}

		if (!Files.isExecutable(shPath)) {
			sendResponse(event, "File specified at " + shPath + " is not executable", true);
			return;
		}

		sendResponse(event, "Server Restart started...", false);


		try {
			Runtime.getRuntime().exec(shPath.toString());
			event.getJDA().shutdown();
			DiscordChat.BOT_INSTANCE.shutdown();
			DiscordChat.SERVER_INSTANCE.ifPresent(minecraftServer -> minecraftServer.halt(true));
		} catch (IOException e) {
			sendResponse(event, "Error spawning process: " + e.getMessage(), false);
		}
	}
}
