package mod.grimmauld.discordchat.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import mod.grimmauld.discordchat.DiscordChat;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public class StopBotCommand {
	private StopBotCommand() {
	}

	public static ArgumentBuilder<CommandSource, ?> register() {
		return Commands.literal("stopBot")
			.requires(cs -> cs.hasPermission(2))
			.executes(ctx -> {
				DiscordChat.BOT_INSTANCE.shutdown();
				return 1;
			});
	}
}
