package mod.grimmauld.discordchat.discordCommand;

import com.jagrosh.jdautilities.command.CommandEvent;
import mod.grimmauld.discordchat.DiscordChat;
import mod.grimmauld.discordchat.commands.GrimmCommand;
import net.minecraft.entity.player.ServerPlayerEntity;

import java.util.List;

public class ListCommand extends GrimmCommand {
	public static final String NAME = "list";

	public ListCommand() {
		super(NAME);
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
