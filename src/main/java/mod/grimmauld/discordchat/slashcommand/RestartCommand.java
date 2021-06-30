package mod.grimmauld.discordchat.slashcommand;

import mod.grimmauld.discordchat.Config;
import mod.grimmauld.discordchat.DiscordBot;
import mod.grimmauld.discordchat.DiscordChat;
import mod.grimmauld.discordchat.util.ThreadHelper;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.minecraft.world.storage.SaveFormat;
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
			DiscordChat.SERVER_INSTANCE.ifPresent(minecraftServer -> {
				SaveFormat.LevelSave lockManager = minecraftServer.storageSource;

				ThreadHelper.runWithTimeout(minecraftServer::close, 10000);
				if (minecraftServer.getRunningThread().isAlive())
					minecraftServer.getRunningThread().stop();

				if (lockManager.lock.isValid()) {
					try {
						lockManager.close();
					} catch (IOException e) {
						sendResponse(event, "Error unlocking save file: " + e.getMessage(), false);
					}
				}
			});
			Runtime.getRuntime().exec(shPath.toString());
			event.getJDA().shutdown();
			DiscordChat.BOT_INSTANCE.shutdown();
		} catch (IOException e) {
			sendResponse(event, "Error spawning process: " + e.getMessage(), false);
		}
	}
}
