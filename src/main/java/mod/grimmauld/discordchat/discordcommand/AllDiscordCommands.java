package mod.grimmauld.discordchat.discordcommand;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import mod.grimmauld.discordchat.Config;
import net.minecraft.util.LazyValue;

import java.util.Arrays;
import java.util.HashSet;

@SuppressWarnings("unused")
public class AllDiscordCommands {
	private static final HashSet<Command> commands;
	private static final CommandClientBuilder builder;
	private static LazyValue<CommandClient> client;

	static {
		commands = new HashSet<>();
		builder = new CommandClientBuilder();
		builder.setOwnerId("533668542562828311");

		commands.addAll(Arrays.asList(new TpsCommand(), new WhitelistCommand(), new RunCommand(), new ListCommand(), new IPCommand(), new EntityListCommand()));

		restartCommandClient();
	}

	private AllDiscordCommands() {
	}

	public static void register(Command command) {
		if (commands.add(command))
			builder.addCommand(command);
	}

	public static CommandClient getCommandClient() {
		return client.getValue();
	}

	public static void restartCommandClient() {
		builder.setPrefix(Config.PREFIX.get());
		client = new LazyValue<>(builder::build);
	}
}
