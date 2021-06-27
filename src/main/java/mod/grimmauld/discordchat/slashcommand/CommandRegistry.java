package mod.grimmauld.discordchat.slashcommand;

import mod.grimmauld.discordchat.slashcommand.compat.spark.SparkCommand;
import net.minecraft.util.LazyValue;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

import javax.annotation.Nullable;

import static mod.grimmauld.discordchat.DiscordChat.asId;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommandRegistry {
	public static final LazyValue<IForgeRegistry<GrimmSlashCommand>> COMMAND_REGISTRY = new LazyValue<>(() ->
		new RegistryBuilder<GrimmSlashCommand>().setName(asId("discord_commands")).setType(GrimmSlashCommand.class).create()
	);

	@SuppressWarnings("unused")
	@Nullable
	public static final GrimmSlashCommand
		TPS_COMMAND = register("tps", new GrimmSlashCommand.Builder<>(TpsCommand::new).global().withHelp("Get the current server tps")),
		WHITELIST_COMMAND = register("whitelist", new GrimmSlashCommand.Builder<>(WhitelistCommand::new).global().withHelp("Opens a whitelist request to operators. Operators can accept by checkmark.")),
		ENTITYLIST_COMMAND = register("entitylist", new GrimmSlashCommand.Builder<>(EntityListCommand::new).withHelp("Lists entities to find lag hot spots")),
		RUN_COMMAND = register("run", new GrimmSlashCommand.Builder<>(RunCommand::new).withHelp("Executes a given minecraft command. Depending on the command this might require an op role.")),
		LIST_COMMAND = register("list", new GrimmSlashCommand.Builder<>(ListCommand::new).global().withHelp("Displays a list of all players currently on the server.")),
		LATEST_COMMAND = register("latest", new GrimmSlashCommand.Builder<>(LatestCommand::new).withHelp("get the current latest log")),
		CRASH_COMMAND = register("crash", new GrimmSlashCommand.Builder<>(CrashCommand::new).withHelp("get the latest crash log")),
		PACK_COMMAND = register("pack", new GrimmSlashCommand.Builder<>(PackCommand::new).global().withHelp("get the latest curseforge pack download")),
		CTLOG_COMMAND = register("ctlog", new GrimmSlashCommand.Builder<>(CtlogCommand::new).withHelp("get the current crafttweaker log")),
		SPARK_COMMAND = register("spark", new GrimmSlashCommand.Builder<>(() -> SparkCommand::new).withHelp("Profile the current server load").withCondition(ModList.get().isLoaded("spark"))),
		VERSION_COMMAND = register("version", new GrimmSlashCommand.Builder<>(VersionCommand::new).global().withHelp("Get the current version of discord chat integration")),
		IP_COMMAND = register("ip", new GrimmSlashCommand.Builder<>(IPCommand::new).global().withHelp("Get the server IP"));

	private CommandRegistry() {
	}

	@SubscribeEvent
	public static void onNewRegistry(RegistryEvent.NewRegistry event) {
		COMMAND_REGISTRY.get();
	}


	@Nullable
	private static GrimmSlashCommand register(String id, GrimmSlashCommand.Builder<? extends GrimmSlashCommand> commandBuilder) {
		GrimmSlashCommand command = commandBuilder.build(asId(id));
		if (command == null)
			return null;
		COMMAND_REGISTRY.get().register(command);
		return command;
	}
}
