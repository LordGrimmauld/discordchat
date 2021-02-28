package mod.grimmauld.discordchat.discordcommand;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.examples.doc.Author;

@Author("Grimmauld")
public class WhitelistCommand extends GrimmCommand {
	public static final String NAME = "whitelist";

	public WhitelistCommand() {
		super(NAME, false, true);
		help = "Opens a whitelist request to operators. Operators can accept by checkmark.";
	}

	@Override
	protected void executeChecked(CommandEvent event) {
		event.getMessage().addReaction("\u2705").submit();
	}
}
