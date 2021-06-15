package mod.grimmauld.discordchat.slashcommand;

import mod.grimmauld.discordchat.DiscordBot;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.minecraftforge.fml.loading.FMLPaths;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.nio.file.Paths;

public class LatestCommand extends GrimmSlashCommand {
	private final Path logFile = FMLPaths.GAMEDIR.get().resolve(Paths.get("logs", "latest.log"));

	public LatestCommand(@Nullable String help, boolean global) {
		super(help, global);
	}

	@Override
	protected void executeChecked(SlashCommandEvent event) {
		if (!DiscordBot.isOp(event.getMember())) {
			sendLackingPermissionResponse(event);
			return;
		}
		sendResponse(event, "Sending log to PM, make sure to have those enabled", true);
		event.getMember().getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendFile(logFile.toFile()).submit());
	}
}
