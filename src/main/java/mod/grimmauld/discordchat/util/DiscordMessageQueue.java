package mod.grimmauld.discordchat.util;

import mcp.MethodsReturnNonnullByDefault;
import mod.grimmauld.discordchat.Config;
import mod.grimmauld.discordchat.DiscordChat;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
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

	private static int send(String msg, Collection<Consumer<String>> handlers) {
		if (msg.isEmpty())
			return 1;

		return DiscordChat.BOT_INSTANCE.ifJDAPresent(jda -> {
			String channelId = Config.REDIRECT_CHANNEL_ID.get();
			if (channelId.isEmpty())
				return handleError("Channel Id may not be empty!", handlers);

			MessageChannel channel = jda.getTextChannelById(channelId);
			if (channel == null)
				return handleError("Channel " + channelId + " can't be found", handlers);

			channel.sendMessage(msg).submit();
			return 0;
		}).orElseGet(() -> handleError("Can not send message to discord: jda not initialized", handlers));
	}

	public int queue(String msg, @Nullable Consumer<String> errorConsumer) {
		if (Config.SYNC_RATE.get() == 0)
			return send(msg, Collections.singletonList(errorConsumer));
		if (builder.length() + msg.length() > 2000)
			send();
		builder.append(msg).append("\n");
		if (errorConsumer != null)
			errorHooks.add(errorConsumer);
		return 0;
	}

	private static int handleError(String errorMsg, Collection<Consumer<String>> handlers) {
		handlers.forEach(stringConsumer -> {
			if (stringConsumer != null)
				stringConsumer.accept(errorMsg);
		});
		return 1;
	}
}
