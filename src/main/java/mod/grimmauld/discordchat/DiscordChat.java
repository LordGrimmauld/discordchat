package mod.grimmauld.discordchat;

import mod.grimmauld.discordchat.discordcommand.AllDiscordCommands;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(DiscordChat.MODID)
public class DiscordChat {
	public static final String MODID = "discordchat";
	public static final Logger LOGGER = LogManager.getLogger(MODID);
	private static final EventListener listener = new EventListener();
	public static DiscordBot BOT_INSTANCE = null;
	public static MinecraftServer SERVER_INSTANCE = null;

	public DiscordChat() {
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG);
		MinecraftForge.EVENT_BUS.register(listener);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(Config::onConfigReloadLoad);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(EventListener::onServerStarted);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(EventListener::onServerStarted);
		Config.loadConfig(Config.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve(MODID + "-common.toml"));
	}

	public static int relaunchBot(String token) {
		if (BOT_INSTANCE != null)
			BOT_INSTANCE.shutdown();
		BOT_INSTANCE = new DiscordBot(token);
		AllDiscordCommands.restartCommandClient();
		listener.resetSyncCycle();
		return 1;
	}
}
