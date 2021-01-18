package mod.grimmauld.discordchat.discordCommand;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.doc.standard.CommandInfo;
import com.jagrosh.jdautilities.examples.doc.Author;
import mod.grimmauld.discordchat.DiscordBot;
import mod.grimmauld.discordchat.DiscordChat;
import mod.grimmauld.discordchat.commands.GrimmCommand;
import mod.grimmauld.discordchat.util.CommandSourceRedirectedOutput;

@CommandInfo(name = {CommandTPS.NAME})
@Author("Grimmauld")
public class RunCommand extends GrimmCommand {
	public static final String NAME = "run";

	public RunCommand() {
		super(NAME, true);
	}

	@Override
	protected void executeChecked(CommandEvent event) {
		StringBuilder builder = new StringBuilder();
		DiscordChat.SERVER_INSTANCE.getCommandManager().handleCommand(CommandSourceRedirectedOutput.of(DiscordChat.SERVER_INSTANCE.getCommandSource().withPermissionLevel(DiscordBot.isOp(event.getMember()) ? 2 : 0),
			text -> builder.append(text.getString()).append("\n")), event.getMessage().getContentStripped().replaceFirst("([^ ])* ", ""));

		event.getChannel().sendMessage(builder.toString()).submit();
	}
}
