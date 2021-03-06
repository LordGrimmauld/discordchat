package mod.grimmauld.discordchat.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import mod.grimmauld.discordchat.DiscordChat;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public class ReloadBotCommand {

	private ReloadBotCommand() {
	}

	public static ArgumentBuilder<CommandSource, ?> register() {
		return Commands.literal("reloadBot")
			.requires(cs -> cs.hasPermissionLevel(2))
			.executes(ctx -> DiscordChat.relaunchBot());
	}
}
