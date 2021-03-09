package mod.grimmauld.discordchat.discordcommand;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import mod.grimmauld.discordchat.Config;
import net.minecraft.util.LazyValue;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("unused")
public class AllDiscordCommands {
	public static final Command TPS_COMMAND = new TpsCommand();
	public static final Command WHITELIST_COMMAND = new WhitelistCommand();
	public static final Command RUN_COMMAND = new RunCommand();
	public static final Command LIST_COMMAND = new ListCommand();
	public static final Command IP_COMMAND = new IPCommand();
	public static final Command ENTITY_LIST_COMMAND = new EntityListCommand();
	private static final Set<Command> commands = new HashSet<>();
	private static final CommandClientBuilder builder = new CommandClientBuilder();
	private static LazyValue<CommandClient> client;

	static {
		builder.setOwnerId("533668542562828311");
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
