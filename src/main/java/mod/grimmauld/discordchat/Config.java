package mod.grimmauld.discordchat;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

import java.nio.file.Path;

public class Config {
	public static final ForgeConfigSpec COMMON_CONFIG;
	public static final ForgeConfigSpec.ConfigValue<String> PREFIX;
	public static final ForgeConfigSpec.ConfigValue<String> TOKEN;
	public static final ForgeConfigSpec.ConfigValue<String> OP_ROLE_NAME;
	public static final ForgeConfigSpec.ConfigValue<String> REDIRECT_CHANNEL_ID;
	public static final ForgeConfigSpec.IntValue SYNC_RATE;

	static {
		ForgeConfigSpec.Builder commonBuilder = new ForgeConfigSpec.Builder();

		commonBuilder.comment("General settings").push("general");
		TOKEN = commonBuilder.comment("Discord Bot token").define("token", "");
		PREFIX = commonBuilder.comment("Discord Bot Prefix").define("prefix", "/");
		OP_ROLE_NAME = commonBuilder.comment("Name of the role empowering people to use operator commands").define("role", "op");
		REDIRECT_CHANNEL_ID = commonBuilder.comment("Channel to redirect messages to and from the server").define("channel", "");
		SYNC_RATE = commonBuilder.comment("Discord sync rate (MC > discord)").defineInRange("sync", 30, 0, 1000);
		commonBuilder.pop();

		COMMON_CONFIG = commonBuilder.build();
	}

	public static void loadConfig(ForgeConfigSpec spec, Path path) {
		final CommentedFileConfig configData = CommentedFileConfig.builder(path).sync().autosave().writingMode(WritingMode.REPLACE).build();
		configData.load();
		spec.setConfig(configData);
		DiscordChat.relaunchBot();
	}

	public static void onConfigReloadLoad(ModConfig.Reloading event) {
		DiscordChat.relaunchBot();
	}

	private Config() {
	}
}
