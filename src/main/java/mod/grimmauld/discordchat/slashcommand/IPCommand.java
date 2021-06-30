package mod.grimmauld.discordchat.slashcommand;

import mod.grimmauld.discordchat.util.IPUtil;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class IPCommand extends GrimmSlashCommand {
	public IPCommand(@Nullable String help, boolean global) {
		super(help, global);
	}

	@Override
	protected void executeChecked(SlashCommandEvent event) {
		String ip = IPUtil.getIP();
		sendResponse(event, ip == null ? "Can't query IP" : ip, true);
	}
}
