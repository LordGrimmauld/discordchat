package mod.grimmauld.discordchat;

import net.dv8tion.jda.api.requests.ErrorResponse;
import okhttp3.internal.http.UnrepeatableRequestBody;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;

public class ManualClassLoader {
	private static final Class[] classes = new Class[]{
		UnrepeatableRequestBody.class,
		MessageFormatter.class,
		FormattingTuple.class,
		ErrorResponse.class
	};

	static {
		DiscordChat.LOGGER.debug("Start manual class loading");
	}

	private ManualClassLoader() {
	}

	public static void load() {
		// load classes
	}
}
