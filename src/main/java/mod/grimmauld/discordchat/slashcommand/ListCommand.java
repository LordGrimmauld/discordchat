package mod.grimmauld.discordchat.slashcommand;

import mod.grimmauld.discordchat.DiscordChat;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.minecraft.util.INameable;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class ListCommand extends GrimmSlashCommand {

	public ListCommand(@Nullable String help, boolean global) {
		super(help, global);
	}

	@Override
	protected void executeChecked(SlashCommandEvent event) {
		DiscordChat.SERVER_INSTANCE.runIfPresent(server -> {
			StringBuilder builder = new StringBuilder();
			EmbedBuilder eb = new EmbedBuilder();
			eb.setTitle("Players on Server");
			List<? extends INameable> players = server.getPlayerList().getPlayers();
			players.forEach(p -> builder.append(p.getDisplayName().getString()).append("\n"));
			eb.addField("Server has " + players.size() + " players online", builder.toString(), true);
			sendEmbedResponse(event, eb, true);
			return true;
		}).orElseGet(() -> sendNoServerResponse(event));
	}
}
