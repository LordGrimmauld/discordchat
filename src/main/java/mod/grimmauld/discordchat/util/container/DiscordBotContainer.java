package mod.grimmauld.discordchat.util.container;

import mod.grimmauld.discordchat.Config;
import mod.grimmauld.discordchat.DiscordBot;
import mod.grimmauld.discordchat.webhooks.Webhook;
import net.dv8tion.jda.api.JDA;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class DiscordBotContainer extends LazyOptionalContainer<DiscordBot> {

	public <U> Optional<U> ifJDAPresent(Function<JDA, U> action) {
		return runIfPresent(bot -> {
			if (bot == null || bot.jda == null)
				return Optional.<U>empty();
			JDA.Status status = bot.jda.getStatus();
			if (status == JDA.Status.SHUTDOWN || status == JDA.Status.SHUTTING_DOWN)
				return Optional.<U>empty();
			return Optional.of(action.apply(bot.jda));
		}).orElseGet(Optional::empty);
	}

	public void ifJDAPresent(Consumer<JDA> action) {
		ifPresent(bot -> {
			if (bot == null || bot.jda == null)
				return;
			JDA.Status status = bot.jda.getStatus();
			if (status == JDA.Status.SHUTDOWN || status == JDA.Status.SHUTTING_DOWN)
				return;
			action.accept(bot.jda);
		});
	}


	public int relaunchBot() {
		shutdown();
		connect(DiscordBot::new);
		Webhook.webhookContainer.connect(() -> new Webhook(Config.WEBHOOK_URL.get()));
		return 1;
	}

	public void shutdown() {
		ifJDAPresent(JDA::shutdownNow);
		invalidate();
	}
}
