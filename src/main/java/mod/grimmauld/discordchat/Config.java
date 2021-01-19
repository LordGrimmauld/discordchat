package mod.grimmauld.discordchat;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

import java.nio.file.Path;

public class Config {
	public static ForgeConfigSpec COMMON_CONFIG;
	public static ForgeConfigSpec.ConfigValue<String> PREFIX;
	public static ForgeConfigSpec.ConfigValue<String> TOKEN;
	public static ForgeConfigSpec.ConfigValue<String> OP_ROLE_NAME;
	public static ForgeConfigSpec.ConfigValue<String> REDIRECT_CHANNEL_ID;
	public static ForgeConfigSpec.IntValue SYNC_RATE;

	static {
		ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();

		COMMON_BUILDER.comment("General settings").push("general");
		setupGeneralConfig(COMMON_BUILDER);
		COMMON_BUILDER.pop();

		COMMON_CONFIG = COMMON_BUILDER.build();
	}

	private static void setupGeneralConfig(ForgeConfigSpec.Builder common_builder) {
		TOKEN = common_builder.comment("Discord Bot token").define("token", "");
		PREFIX = common_builder.comment("Discord Bot Prefix").define("prefix", "/");
		OP_ROLE_NAME = common_builder.comment("Name of the role empowering people to use operator commands").define("role", "op");
		REDIRECT_CHANNEL_ID = common_builder.comment("Channel to redirect messages to and from the server").define("channel", "");
		SYNC_RATE = common_builder.comment("Discord sync rate (MC > discord)").defineInRange("sync", 30, 0, 1000);
	}

	public static void loadConfig(ForgeConfigSpec spec, Path path) {
		final CommentedFileConfig configData = CommentedFileConfig.builder(path).sync().autosave().writingMode(WritingMode.REPLACE).build();
		configData.load();
		spec.setConfig(configData);
		DiscordChat.relaunchBot(TOKEN.get());
	}

	public static void onConfigReloadLoad(ModConfig.Reloading event) {
		DiscordChat.relaunchBot(TOKEN.get());
	}
}
