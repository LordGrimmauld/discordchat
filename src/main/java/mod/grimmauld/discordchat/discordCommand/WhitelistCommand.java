package mod.grimmauld.discordchat.discordCommand;

import com.jagrosh.jdautilities.command.CommandEvent;
import mod.grimmauld.discordchat.commands.GrimmCommand;

public class WhitelistCommand extends GrimmCommand {
	public static final String NAME = "whitelist";

	public WhitelistCommand() {
		super(NAME, false, true);
	}

	@Override
	protected void executeChecked(CommandEvent event) {
		event.getMessage().addReaction("\u2705").submit();
	}
}
