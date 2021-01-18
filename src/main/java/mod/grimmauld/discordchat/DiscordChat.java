package mod.grimmauld.discordchat;

import mod.grimmauld.discordchat.discordCommand.AllDiscordCommands;
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
    public static DiscordBot INSTANCE = null;
    public static MinecraftServer SERVER_INSTANCE = null;

    public static int relaunchBot(String token) {
        if (INSTANCE != null)
            INSTANCE.shutdown();
        INSTANCE = new DiscordBot(token);
        AllDiscordCommands.restartCommandClient();
        return 1;
    }

    public DiscordChat() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG);
        MinecraftForge.EVENT_BUS.register(new EventListener());
        FMLJavaModLoadingContext.get().getModEventBus().addListener(Config::onConfigReloadLoad);
        Config.loadConfig(Config.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve(MODID + "-common.toml"));
    }
}
