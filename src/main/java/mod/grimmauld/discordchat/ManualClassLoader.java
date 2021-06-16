package mod.grimmauld.discordchat;

import com.therandomlabs.curseapi.CurseAPI;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;

public class ManualClassLoader {
	static {
		DiscordChat.LOGGER.debug("Start manual class loading");
	}

	private static final Class[] classes = new Class[]{
		MessageFormatter.class,
		FormattingTuple.class,
		ErrorResponse.class,
		CurseAPI.class
	};

	private ManualClassLoader() {
	}

	public static void load() {
		// load classes
	}
}
