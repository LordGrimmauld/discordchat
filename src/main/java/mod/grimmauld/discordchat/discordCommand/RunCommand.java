package mod.grimmauld.discordchat.discordCommand;

import com.jagrosh.jdautilities.command.CommandEvent;
import mod.grimmauld.discordchat.DiscordBot;
import mod.grimmauld.discordchat.DiscordChat;
import mod.grimmauld.discordchat.util.CommandSourceRedirectedOutput;

public class RunCommand extends GrimmCommand {
	public static final String NAME = "run";

	public RunCommand() {
		super(NAME);
	}

	@Override
	protected void executeChecked(CommandEvent event) {
		StringBuilder builder = new StringBuilder();
		DiscordChat.SERVER_INSTANCE.getCommandManager().handleCommand(CommandSourceRedirectedOutput.of(DiscordChat.SERVER_INSTANCE.getCommandSource().withPermissionLevel(DiscordBot.isOp(event.getMember()) ? 2 : 0),
			text -> builder.append(text.getString()).append("\n")), event.getMessage().getContentStripped().replaceFirst("([^ ])* ", ""));
		sendResponse(event, builder.toString());
	}
}
