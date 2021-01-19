package mod.grimmauld.discordchat.discordCommand;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.examples.doc.Author;
import mod.grimmauld.discordchat.DiscordChat;
import net.minecraft.entity.player.ServerPlayerEntity;

import java.util.List;


@Author("Grimmauld")
public class ListCommand extends GrimmCommand {
	public static final String NAME = "list";

	public ListCommand() {
		super(NAME);
		help = "Displays a list of all players currently on the server.";
	}

	@Override
	protected void executeChecked(CommandEvent event) {
		List<ServerPlayerEntity> players = DiscordChat.SERVER_INSTANCE.getPlayerList().getPlayers();
		StringBuilder builder = new StringBuilder();
		builder.append("Server has ").append(players.size()).append(" players online\n\n");
		players.forEach(p -> builder.append(p.getDisplayName().getString()).append("\n"));

		event.getChannel().sendMessage(builder.toString()).submit();
	}
}
