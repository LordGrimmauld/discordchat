package mod.grimmauld.discordchat.discordCommand;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.examples.doc.Author;
import mod.grimmauld.discordchat.util.IPUtil;

@Author("Grimmauld")
public class IPCommand extends GrimmCommand {
	public static final String NAME = "ip";

	public IPCommand() {
		super(NAME);
		help = "get server IP";
	}

	@Override
	protected void executeChecked(CommandEvent event) {
		String ip = IPUtil.getIP();
		event.getChannel().sendMessage(ip == null ? "Can't query IP" : ip).submit();
	}
}
