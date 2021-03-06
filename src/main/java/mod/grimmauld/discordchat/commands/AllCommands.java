package mod.grimmauld.discordchat.commands;

import com.mojang.brigadier.CommandDispatcher;
import mod.grimmauld.discordchat.DiscordChat;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public class AllCommands {
	public static void register(CommandDispatcher<CommandSource> commandDispatcher) {
		commandDispatcher.register(Commands.literal(DiscordChat.MODID).then(ReloadBotCommand.register()));
		commandDispatcher.register(Commands.literal(DiscordChat.MODID).then(TellDiscordCommand.register()));
		commandDispatcher.register(Commands.literal(DiscordChat.MODID).then(StopBotCommand.register()));
	}

	private AllCommands() {
	}
}
