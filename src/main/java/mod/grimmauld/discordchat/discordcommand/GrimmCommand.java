package mod.grimmauld.discordchat.discordcommand;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.examples.doc.Author;
import mod.grimmauld.discordchat.Config;


@Author("Grimmauld")
public abstract class GrimmCommand extends Command {
	private final boolean global;


	protected GrimmCommand(String name, boolean global) {
		this.name = name;
		this.global = global;
		AllDiscordCommands.register(this);
	}

	protected GrimmCommand(String name) {
		this(name, false);
	}

	@Override
	protected void execute(CommandEvent event) {
		if (!(global || event.getChannel().getId().equals(Config.REDIRECT_CHANNEL_ID.get())))
			return;

		executeChecked(event);
	}

	protected abstract void executeChecked(CommandEvent event);

	protected void sendResponse(CommandEvent event, String msg) {
		if (msg.isEmpty())
			return;

		while (msg.length() > 1990) {
			event.getChannel().sendMessage(msg.substring(0, 1990)).submit();
			msg = msg.substring(1990);
		}
		event.getChannel().sendMessage(msg).submit();
	}

	protected boolean sendNoServerResponse(CommandEvent event) {
		event.getChannel().sendMessage("Failed to communicate with server").submit();
		return false;
	}
}
