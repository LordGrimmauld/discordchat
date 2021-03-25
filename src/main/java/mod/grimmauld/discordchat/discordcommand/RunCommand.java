package mod.grimmauld.discordchat.discordcommand;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.examples.doc.Author;
import mod.grimmauld.discordchat.DiscordBot;
import mod.grimmauld.discordchat.DiscordChat;
import mod.grimmauld.discordchat.util.CommandSourceRedirectedOutput;


@Author("Grimmauld")
public class RunCommand extends GrimmCommand {
	public static final String NAME = "run";

	public RunCommand() {
		super(NAME);
		help = "Executes a given minecraft command. Depending on the command this might require an op role.";
	}

	@Override
	protected void executeChecked(CommandEvent event) {
		DiscordChat.SERVER_INSTANCE.runIfPresent(server -> {
			StringBuilder builder = new StringBuilder();
			server.getCommandManager().handleCommand(CommandSourceRedirectedOutput.of(server.getCommandSource().withPermissionLevel(DiscordBot.isOp(event.getMember()) ? 2 : 0))
				.withName(event.getMember().getUser().getName())
				.withHook(text -> builder.append(text.getString()).append("\n")), event.getMessage().getContentStripped().replaceFirst("([^ ])* ", ""));
			sendResponse(event, builder.toString());
			return true;
		}).orElseGet(() -> sendNoServerResponse(event));
	}
}
