package mod.grimmauld.discordchat.util;

import mcp.MethodsReturnNonnullByDefault;
import mod.grimmauld.discordchat.Config;
import mod.grimmauld.discordchat.DiscordChat;
import mod.grimmauld.discordchat.webhooks.Webhook;
import mod.grimmauld.discordchat.webhooks.WebhookMessage;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.minecraftforge.event.ServerChatEvent;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class DiscordMessageQueue {
	public static final DiscordMessageQueue INSTANCE = new DiscordMessageQueue();
	private final Set<Consumer<String>> errorHooks = new HashSet<>();
	private final Collection<ServerChatEvent> chatQueue = new ArrayList<>();
	private StringBuilder builder = new StringBuilder();

	private static int send(String msg, Collection<Consumer<String>> handlers, boolean waitForResponse) {
		if (msg.isEmpty())
			return 1;

		return DiscordChat.BOT_INSTANCE.ifJDAPresent(jda -> {
			String channelId = Config.REDIRECT_CHANNEL_ID.get();
			if (channelId.isEmpty())
				return handleError("Channel Id may not be empty!", handlers);

			MessageChannel channel = jda.getTextChannelById(channelId);
			if (channel == null)
				return handleError("Channel " + channelId + " can't be found", handlers);

			CompletableFuture<Message> resp = channel.sendMessage(msg).submit();
			if (waitForResponse) {
				try {
					resp.get();
				} catch (InterruptedException | ExecutionException e) {
					return handleError("Error waiting for discord to send the message: " + e, handlers);
				}
			}
			return 0;
		}).orElseGet(() -> handleError("Can not send message to discord: jda not initialized", handlers));
	}

	private static int handleError(String errorMsg, Collection<Consumer<String>> handlers) {
		handlers.forEach(stringConsumer -> {
			if (stringConsumer != null)
				stringConsumer.accept(errorMsg);
		});
		return 1;
	}

	public void send(boolean waitSend) {
		if (builder.toString().isEmpty() && chatQueue.isEmpty())
			return;

		new Thread(() -> {
			chatQueue.forEach(event -> {
				String content = event.getMessage().replace("@", "@ ");
				String avatar = String.format("https://crafatar.com/avatars/%s?overlay", event.getPlayer().getUUID());

				if (!Webhook.webhookContainer.runIfPresent(hook -> hook.sendMessage(new WebhookMessage(event.getUsername(), avatar, content))).orElse(false)) {
					queue("**[MC " + event.getUsername() + "]** " + content, DiscordChat.LOGGER::warn);
				}
			});
			chatQueue.clear();

			if (send(builder.toString(), errorHooks, waitSend) == 0) {
				builder = new StringBuilder();
				errorHooks.clear();
			}
		}).start();
	}

	public int queue(String msg, @Nullable Consumer<String> errorConsumer) {
		if (Config.SYNC_RATE.get() == 0)
			return send(msg, Collections.singletonList(errorConsumer), false);
		if (builder.length() + msg.length() > 2000)
			send(false);
		builder.append(msg).append("\n");
		if (errorConsumer != null)
			errorHooks.add(errorConsumer);
		return 0;
	}

	public void queue(ServerChatEvent event) {
		chatQueue.add(event);
	}
}
