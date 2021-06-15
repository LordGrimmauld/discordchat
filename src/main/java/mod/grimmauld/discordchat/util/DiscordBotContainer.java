package mod.grimmauld.discordchat.util;

import mod.grimmauld.discordchat.DiscordBot;
import mod.grimmauld.discordchat.EventListener;
import net.dv8tion.jda.api.JDA;

import java.util.Optional;
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

	public int relaunchBot() {
		connect(DiscordBot::new);
		EventListener.resetSyncCycle();
		return 1;
	}
}
