package mod.grimmauld.discordchat.discordcommand;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.examples.doc.Author;
import mod.grimmauld.discordchat.DiscordChat;
import net.dv8tion.jda.api.EmbedBuilder;
import net.minecraft.util.INameable;

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
		DiscordChat.SERVER_INSTANCE.runIfPresent(server -> {
			StringBuilder builder = new StringBuilder();
			EmbedBuilder eb = new EmbedBuilder();
			eb.setTitle("Players on Server");
			List<? extends INameable> players = server.getPlayerList().getPlayers();
			players.forEach(p -> builder.append(p.getDisplayName().getString()).append("\n"));
			eb.addField("Server has " + players.size() + " players online", builder.toString(), true);
			event.getChannel().sendMessage(eb.build()).submit();
			return true;
		}).orElseGet(() -> sendNoServerResponse(event));
	}
}
