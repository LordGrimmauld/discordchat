package mod.grimmauld.discordchat.slashcommand;

import mod.grimmauld.discordchat.DiscordBot;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.minecraftforge.fml.loading.FMLPaths;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.*;
import java.util.Comparator;

public class CrashCommand extends GrimmSlashCommand {
	private static final Path crashReports = FMLPaths.GAMEDIR.get().resolve(Paths.get("crash-reports"));
	private static final PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**/crash-*.txt");

	public CrashCommand(@Nullable String help, boolean global) {
		super(help, global);
	}

	@Override
	protected void executeChecked(SlashCommandEvent event) {
		if (!DiscordBot.isOp(event.getMember())) {
			sendLackingPermissionResponse(event);
			return;
		}

		sendResponse(event, "Sending log to PM, make sure to have those enabled", true);
		try {
			Files.list(crashReports)
				.filter(matcher::matches)
				.max(Comparator.naturalOrder())
				.map(Path::toFile)
				.ifPresent(path -> event.getMember().getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendFile(path).submit()));
		} catch (IOException e) {
			sendResponse(event, "Something went wrong accessing the logs: " + e, true);
		}
	}
}
