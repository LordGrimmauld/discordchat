package mod.grimmauld.discordchat;

import mod.grimmauld.discordchat.util.async.AsyncBool;
import mod.grimmauld.discordchat.util.async.AsyncBoolQueue;
import mod.grimmauld.discordchat.util.async.AsyncEventBusListener;
import mod.grimmauld.discordchat.util.container.DiscordBotContainer;
import mod.grimmauld.discordchat.util.container.ServerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(BuildConfig.MODID)
public class DiscordChat {
	public static final Logger LOGGER = LogManager.getLogger(BuildConfig.MODID);
	public static final DiscordBotContainer BOT_INSTANCE = new DiscordBotContainer();
	public static final ServerContainer SERVER_INSTANCE = new ServerContainer();
	public static final AsyncBool CAN_KILL_PROCESS = new AsyncBoolQueue(new AsyncEventBusListener<FMLServerStoppedEvent>(),
		AsyncBool.waitFor(3600000)).onTurnTrue(() -> DiscordChat.BOT_INSTANCE.ifPresent(DiscordBot::shutdown));

	public DiscordChat() {
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG);
		Config.loadConfig(Config.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve(BuildConfig.MODID + "-common.toml"));
	}

	public static ResourceLocation asId(String path) {
		return new ResourceLocation(BuildConfig.MODID, path);
	}
}
