package mod.grimmauld.discordchat.discordCommand;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.doc.standard.CommandInfo;
import com.jagrosh.jdautilities.examples.doc.Author;
import mod.grimmauld.discordchat.commands.GrimmCommand;


@CommandInfo(name = {CommandTPS.NAME})
@Author("Grimmauld")
public class WhitelistCommand extends GrimmCommand {
	public static final String NAME = "whitelist";

	public WhitelistCommand() {
		super(NAME, false);
	}

	@Override
	protected void executeChecked(CommandEvent event) {
		event.getMessage().addReaction("\u2705").submit();
	}
}
