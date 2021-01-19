package mod.grimmauld.discordchat.util;

import mod.grimmauld.discordchat.Config;
import mod.grimmauld.discordchat.DiscordChat;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class DiscordMessageQueue {
	private final Set<Consumer<String>> errorHooks = new HashSet<>();
	public static DiscordMessageQueue INSTANCE = new DiscordMessageQueue();
	private StringBuilder builder = new StringBuilder();

	public void send() {
		if (send(builder.toString(), errorHooks) == 0) {
			builder = new StringBuilder();
			errorHooks.clear();
		}
	}

	public static int send(String msg, Collection<Consumer<String>> handlers) {
		if (msg.isEmpty())
			return 1;

		if (DiscordChat.INSTANCE == null || DiscordChat.INSTANCE.jda == null) {
			handleError("Can not send message to discord: jda not initialized", handlers);
			return 1;
		}

		String channelId = Config.REDIRECT_CHANNEL_ID.get();
		if (channelId.isEmpty()) {
			handleError("Channel Id may not be empty!", handlers);
			return 1;
		}

		MessageChannel channel = DiscordChat.INSTANCE.jda.getTextChannelById(channelId);
		if (channel == null) {
			handleError("Channel " + channelId + " can't be found", handlers);
			return 1;
		}

		channel.sendMessage(msg).submit();
		return 0;
	}

	public int queue(String msg, @Nullable Consumer<String> errorConsumer) {
		if (Config.SYNC_RATE.get() == 0) {
			send(msg, Collections.singletonList(errorConsumer));
		} else {
			builder.append(msg).append("\n");
			errorHooks.add(errorConsumer);
		}
		return 0;
	}

	private static void handleError(String errorMsg, Collection<Consumer<String>> handlers) {
		handlers.forEach(stringConsumer -> {
			if (stringConsumer != null)
				stringConsumer.accept(errorMsg);
		});
	}
}
