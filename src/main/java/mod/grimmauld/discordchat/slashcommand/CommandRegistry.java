package mod.grimmauld.discordchat.slashcommand;

import net.minecraft.util.LazyValue;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

import static mod.grimmauld.discordchat.DiscordChat.asId;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommandRegistry {
	public static final LazyValue<IForgeRegistry<GrimmSlashCommand>> COMMAND_REGISTRY = new LazyValue<>(() ->
		new RegistryBuilder<GrimmSlashCommand>().setName(asId("discord_commands")).setType(GrimmSlashCommand.class).create()
	);
	public static final GrimmSlashCommand
		TPS_COMMAND = register("tps", new GrimmSlashCommand.Builder<>(TpsCommand::new).withHelp("Get the current server tps")),
		WHITELIST_COMMAND = register("whitelist", new GrimmSlashCommand.Builder<>(WhitelistCommand::new).withHelp("Opens a whitelist request to operators. Operators can accept by checkmark.")),
		ENTITYLIST_COMMAND = register("entitylist", new GrimmSlashCommand.Builder<>(EntityListCommand::new).withHelp("Lists entities to find lag hot spots")),
		RUN_COMMAND = register("run", new GrimmSlashCommand.Builder<>(RunCommand::new).withHelp("Executes a given minecraft command. Depending on the command this might require an op role.")),
		LIST_COMMAND = register("list", new GrimmSlashCommand.Builder<>(ListCommand::new).withHelp("Displays a list of all players currently on the server.")),
		LATEST_COMMAND = register("latest", new GrimmSlashCommand.Builder<>(LatestCommand::new).withHelp("get the latest log")),
		CRASH_COMMAND = register("crash", new GrimmSlashCommand.Builder<>(CrashCommand::new).withHelp("get the latest crash log")),
		PACK_COMMAND = register("pack", new GrimmSlashCommand.Builder<>(PackCommand::new).withHelp("get the latest curseforge pack download")),
		IP_COMMAND = register("ip", new GrimmSlashCommand.Builder<>(IPCommand::new).withHelp("Get the server IP"));

	private CommandRegistry() {
	}

	@SubscribeEvent
	public static void onNewRegistry(RegistryEvent.NewRegistry event) {
		COMMAND_REGISTRY.get();
	}


	private static GrimmSlashCommand register(String id, GrimmSlashCommand.Builder<? extends GrimmSlashCommand> commandBuilder) {
		GrimmSlashCommand command = commandBuilder.build(asId(id));
		COMMAND_REGISTRY.get().register(command);
		return command;
	}
}
