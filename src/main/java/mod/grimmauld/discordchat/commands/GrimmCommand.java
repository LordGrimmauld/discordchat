package mod.grimmauld.discordchat.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import mod.grimmauld.discordchat.Config;
import mod.grimmauld.discordchat.DiscordChat;
import mod.grimmauld.discordchat.discordCommand.AllDiscordCommands;

public  abstract class GrimmCommand extends Command {
	private final boolean needsServer;
	private final boolean global;


	public GrimmCommand(String name, boolean needsServer, boolean global) {
		this.name = name;
		this.needsServer = needsServer;
		this.global = global;
		AllDiscordCommands.register(this);
	}

	public GrimmCommand(String name) {
		this(name, true, false);
	}

	@Override
	protected void execute(CommandEvent event) {
		if (!(global || event.getChannel().getId().equals(Config.REDIRECT_CHANNEL_ID.get())))
			return;

		if (needsServer && DiscordChat.SERVER_INSTANCE == null) {
			event.getChannel().sendMessage("Failed to communicate with server").submit();
			return;
		}
		executeChecked(event);
	}

	protected abstract void executeChecked(CommandEvent event);

	protected void sendResponse(CommandEvent event, String msg) {
		if (msg.isEmpty())
			return;
		event.getChannel().sendMessage(msg).submit();
	}
}
