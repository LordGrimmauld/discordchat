package mod.grimmauld.discordchat.slashcommand;

import mod.grimmauld.discordchat.DiscordBot;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.minecraftforge.fml.loading.FMLPaths;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CtlogCommand extends GrimmSlashCommand {
	private final Path logFile = FMLPaths.GAMEDIR.get().resolve(Paths.get("logs", "crafttweaker.log"));

	public CtlogCommand(@Nullable String help, boolean global) {
		super(help, global);
	}

	@Override
	protected void executeChecked(SlashCommandEvent event) {
		if (!DiscordBot.isOp(event.getMember())) {
			sendLackingPermissionResponse(event);
			return;
		}

		File log = logFile.toFile();
		if (log.exists() && log.canRead()) {
			sendResponse(event, "Sending log to PM, make sure to have those enabled", true);
			event.getMember().getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendFile(log).submit());
		} else {
			sendResponse(event, "No crafttweaker.log file found", true);
		}
	}
}
