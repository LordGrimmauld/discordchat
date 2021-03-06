package mod.grimmauld.discordchat.util;

import mod.grimmauld.discordchat.DiscordBot;
import net.dv8tion.jda.api.JDA;
import net.minecraftforge.common.util.NonNullSupplier;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class DiscordBotContainer {
	@Nullable
	private DiscordBot bot = null;

	@Nullable
	private NonNullSupplier<DiscordBot> botSupplier;

	public void connectBot(NonNullSupplier<DiscordBot> botSupplier) {
		this.invalidate();
		this.botSupplier = botSupplier;
	}

	public void invalidate() {
		this.ifPresent(DiscordBot::shutdown);
		bot = null;
		botSupplier = null;
	}

	public <T> Optional<T> ifPresent(Function<DiscordBot, T> action) {
		if (bot == null) {
			if (botSupplier != null) {
				bot = botSupplier.get();
			} else {
				return Optional.empty();
			}
		}
		return Optional.of(action.apply(bot));
	}


	public <T> Optional<T> ifJDAPresent(Function<JDA, T> action) {
		if (bot == null) {
			if (botSupplier != null) {
				bot = botSupplier.get();
			} else {
				return Optional.empty();
			}
		}
		if (bot.jda == null)
			return Optional.empty();
		JDA.Status status = bot.jda.getStatus();
		if (status == JDA.Status.SHUTDOWN || status == JDA.Status.SHUTTING_DOWN)
			return Optional.empty();
		return Optional.of(action.apply(bot.jda));
	}

	public boolean ifPresent(Consumer<DiscordBot> action) {
		return ifPresent(bot -> {
			action.accept(bot);
			return true;
		}).orElse(false);
	}

	public boolean ifJDAPresent(Consumer<JDA> action) {
		return ifJDAPresent(jda -> {
			action.accept(jda);
			return true;
		}).orElse(false);
	}
}
