package mod.grimmauld.discordchat.util;

import mod.grimmauld.discordchat.DiscordChat;
import net.minecraft.util.LazyValue;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class IPUtil {

	private static final LazyValue<String> IP = new LazyValue<>(IPUtil::loadIp);

	@Nullable
	public static String getIP() {
		String ip = IP.getValue();
		int port = DiscordChat.SERVER_INSTANCE == null ? -1 : DiscordChat.SERVER_INSTANCE.getServerPort();
		if (ip == null)
			return null;
		return IP.getValue() + ":" + port;
	}

	@Nullable
	private static String loadIp() {
		try {
			URL whatismyip = new URL("http://checkip.amazonaws.com");
			BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
			return in.readLine();
		} catch (Exception e) {
			DiscordChat.LOGGER.error("Can't query IP address: ", e);
			return null;
		}
	}

	private IPUtil() {
	}
}
