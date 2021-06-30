package mod.grimmauld.discordchat.message_queue;

import mod.grimmauld.discordchat.Config;
import mod.grimmauld.discordchat.DiscordChat;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class PlainTextMessage implements IMessage {

	private final String content;
	private @Nullable
	final Consumer<String> errorHook;

	public PlainTextMessage(String content, @Nullable Consumer<String> errorHook) {
		this.content = content;
		this.errorHook = errorHook;
	}

	private static int handleError(String errorMsg, @Nullable Consumer<String> handler) {
		if (handler != null)
			handler.accept(errorMsg);
		return 1;
	}

	@Override
	public void send() {
		if (content.isEmpty())
			return;

		DiscordChat.BOT_INSTANCE.ifJDAPresent(jda -> {
			String channelId = Config.REDIRECT_CHANNEL_ID.get();
			if (channelId.isEmpty())
				return handleError("Channel Id may not be empty!", errorHook);

			MessageChannel channel = jda.getTextChannelById(channelId);
			if (channel == null)
				return handleError("Channel " + channelId + " can't be found", errorHook);

			String msgChop = content;
			while (msgChop.length() > 1990) {
				int resp = sendPartial(channel, msgChop.substring(0, 1990));
				if (resp != 0)
					return resp;
				msgChop = msgChop.substring(1990);
			}

			return sendPartial(channel, msgChop);
		}).orElseGet(() -> handleError("Can not send message to discord: jda not initialized", errorHook));
	}

	private int sendPartial(MessageChannel channel, String msg) {
		CompletableFuture<Message> resp = channel.sendMessage(msg).submit();
		try {
			resp.get();
		} catch (InterruptedException | ExecutionException e) {
			return handleError("Error waiting for discord to send the message: " + e, errorHook);
		}
		return 0;
	}
}
