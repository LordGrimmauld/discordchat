package mod.grimmauld.discordchat.util;

import mod.grimmauld.discordchat.DiscordChat;

public class ThreadHelper {
	private ThreadHelper() {
	}

	public static void runAfter(Runnable runnable, long ms) {
		new Thread(() -> {
			try {
				Thread.sleep(ms);
			} catch (InterruptedException ie) {
				DiscordChat.LOGGER.error("Error delaying task: {}", ie.getMessage());
			}
			runnable.run();
		}).start();

	}
}
