package mod.grimmauld.discordchat.discordcommand;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.examples.doc.Author;
import mod.grimmauld.discordchat.DiscordBot;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;
import java.nio.file.Paths;

@Author("Grimmauld")
public class LatestCommand extends GrimmCommand {
	public static final String NAME = "latest";
	private final Path logFile = FMLPaths.GAMEDIR.get().resolve(Paths.get("logs", "latest.log"));

	protected LatestCommand() {
		super(NAME);
		help = "get the latest log";
	}

	@Override
	protected void executeChecked(CommandEvent event) {
		if (!DiscordBot.isOp(event.getMember())) {
			sendResponse(event, "You need operator role to run this command!");
			return;
		}

		event.getMember().getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendFile(logFile.toFile()).submit());
	}
}
