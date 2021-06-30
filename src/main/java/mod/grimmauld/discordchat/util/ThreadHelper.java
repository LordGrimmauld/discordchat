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

	public static void runWithTimeout(Runnable task, long ms) {
		Thread thread = new Thread(task);
		thread.start();

		long end = System.currentTimeMillis() + ms;

		while (System.currentTimeMillis() < end) {
			if (!thread.isAlive())
				return;
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				return;
			}
		}
	}
}
