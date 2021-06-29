package mod.grimmauld.discordchat;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

import java.nio.file.Path;

@Mod.EventBusSubscriber(modid = BuildConfig.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
	public static final ForgeConfigSpec COMMON_CONFIG;
	public static final ForgeConfigSpec.ConfigValue<String> PREFIX;
	public static final ForgeConfigSpec.ConfigValue<String> TOKEN;
	public static final ForgeConfigSpec.ConfigValue<String> OP_ROLE_NAME;
	public static final ForgeConfigSpec.ConfigValue<String> REDIRECT_CHANNEL_ID;
	public static final ForgeConfigSpec.ConfigValue<String> CRASH_CHANNEL_ID;
	public static final ForgeConfigSpec.IntValue PROJECT_ID;
	public static final ForgeConfigSpec.ConfigValue<String> WEBHOOK_URL;

	static {
		ForgeConfigSpec.Builder commonBuilder = new ForgeConfigSpec.Builder();

		commonBuilder.comment("General settings").push("general");
		TOKEN = commonBuilder.comment("Discord Bot token").define("token", "");
		PROJECT_ID = commonBuilder.comment("Modpack Curseforge project ID").defineInRange("id", 0, 0, Integer.MAX_VALUE);
		PREFIX = commonBuilder.comment("Discord Bot Prefix").define("prefix", "/");
		OP_ROLE_NAME = commonBuilder.comment("Name of the role empowering people to use operator commands").define("role", "op");
		REDIRECT_CHANNEL_ID = commonBuilder.comment("Channel to redirect messages to and from the server").define("channel", "");
		CRASH_CHANNEL_ID = commonBuilder.comment("Channel to redirect crashes from the server").define("crashchannel", "");
		WEBHOOK_URL = commonBuilder.comment("Webhook url for fancy player messages").define("webhookurl", "");
		commonBuilder.pop();

		COMMON_CONFIG = commonBuilder.build();
	}

	private Config() {
	}

	public static void loadConfig(ForgeConfigSpec spec, Path path) {
		final CommentedFileConfig configData = CommentedFileConfig.builder(path).sync().autosave().writingMode(WritingMode.REPLACE).build();
		configData.load();
		spec.setConfig(configData);
		DiscordChat.BOT_INSTANCE.relaunchBot();
	}

	@SubscribeEvent
	public static void onConfigReloadLoad(ModConfig.Reloading event) {
		DiscordChat.BOT_INSTANCE.relaunchBot();
	}
}
