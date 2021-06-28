package mod.grimmauld.discordchat.MessageQueue;

import mod.grimmauld.discordchat.DiscordChat;
import mod.grimmauld.discordchat.webhooks.Webhook;
import mod.grimmauld.discordchat.webhooks.WebhookMessage;
import net.minecraftforge.event.ServerChatEvent;

public class ChatEventMessage implements IMessage {
	private final ServerChatEvent event;

	public ChatEventMessage(ServerChatEvent event) {
		this.event = event;
	}

	@Override
	public void send() {
		String content = event.getMessage().replace("@", "@ ");
		String avatar = String.format("https://crafatar.com/avatars/%s?overlay", event.getPlayer().getUUID());

		if (!Webhook.webhookContainer.runIfPresent(hook -> hook.sendMessage(new WebhookMessage(event.getUsername(), avatar, content))).orElse(false)) {
			DiscordMessageQueue.INSTANCE.queue("**[MC " + event.getUsername() + "]** " + content, DiscordChat.LOGGER::warn);
		}
	}
}
