package mod.grimmauld.discordchat.discordcommand;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.examples.doc.Author;
import mod.grimmauld.discordchat.DiscordBot;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.file.*;
import java.util.Comparator;

@Author("Grimmauld")
public class CrashCommand extends GrimmCommand {
	public static final String NAME = "crash";
	private static final Path crashReports = FMLPaths.GAMEDIR.get().resolve(Paths.get("crash-reports"));
	private static final PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**/crash-*.txt");

	protected CrashCommand() {
		super(NAME);
		help = "get the latest crash log";
	}

	@Override
	protected void executeChecked(CommandEvent event) {
		if (!DiscordBot.isOp(event.getMember())) {
			sendResponse(event, "You need operator role to run this command!");
			return;
		}

		try {
			Files.list(crashReports)
				.filter(matcher::matches)
				.max(Comparator.naturalOrder())
				.ifPresent(path -> event.getMember().getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendFile(path.toFile()).submit()));
		} catch (IOException e) {
			sendResponse(event, "Something went wrong accessing the logs: " + e);
		}
	}
}
