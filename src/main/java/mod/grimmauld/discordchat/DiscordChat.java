package mod.grimmauld.discordchat;

import mod.grimmauld.discordchat.util.DiscordBotContainer;
import mod.grimmauld.discordchat.util.LazyOptionalContainer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(DiscordChat.MODID)
public class DiscordChat {
	public static final String MODID = "discordchat";
	public static final String VERSION = "0.1.0";
	public static final Logger LOGGER = LogManager.getLogger(MODID);
	public static final DiscordBotContainer BOT_INSTANCE = new DiscordBotContainer();
	public static final LazyOptionalContainer<MinecraftServer> SERVER_INSTANCE = new LazyOptionalContainer<>();

	public DiscordChat() {
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG);
		Config.loadConfig(Config.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve(MODID + "-common.toml"));
	}

	public static ResourceLocation asId(String path) {
		return new ResourceLocation(MODID, path);
	}
}
