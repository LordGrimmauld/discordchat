package mod.grimmauld.discordchat.message_queue;

import mcp.MethodsReturnNonnullByDefault;
import mod.grimmauld.discordchat.DiscordChat;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class DiscordMessageQueue {
	public static final DiscordMessageQueue INSTANCE = new DiscordMessageQueue();
	private final BlockingQueue<IMessage> chatQueue = new LinkedBlockingQueue<>();

	public DiscordMessageQueue() {
		new Thread(() -> {
			while (true) {
				try {
					chatQueue.take().send();
				} catch (InterruptedException e) {
					DiscordChat.LOGGER.error("Error while reading message queue: {}", e.getMessage());
				}
			}
		}).start();
	}

	public int queue(String msg, @Nullable Consumer<String> errorConsumer) {
		queue(new PlainTextMessage(msg, errorConsumer));
		return 0;
	}

	public void queue(IMessage msg) {
		chatQueue.add(msg);
	}
}
