package mod.grimmauld.discordchat.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import mod.grimmauld.discordchat.DiscordChat;
import mod.grimmauld.discordchat.discordCommand.AllDiscordCommands;

public  abstract class GrimmCommand extends Command {
	private final boolean needsServer;

	public GrimmCommand(String name, boolean needsServer) {
		this.name = name;
		this.needsServer = needsServer;
		AllDiscordCommands.register(this);
	}

	public GrimmCommand(String name) {
		this(name, true);
	}

	@Override
	protected void execute(CommandEvent event) {
		if (needsServer && DiscordChat.SERVER_INSTANCE == null) {
			event.getChannel().sendMessage("Failed to communicate with server").submit();
			return;
		}
		executeChecked(event);
	}

	protected abstract void executeChecked(CommandEvent event);
}
