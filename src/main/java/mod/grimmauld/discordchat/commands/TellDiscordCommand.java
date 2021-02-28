package mod.grimmauld.discordchat.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import mod.grimmauld.discordchat.util.DiscordMessageQueue;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.ComponentArgument;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponentUtils;

public class TellDiscordCommand {
	public static ArgumentBuilder<CommandSource, ?> register() {
		return Commands.literal("telldiscord")
			.requires(player -> player.hasPermissionLevel(2))
			.then(Commands.argument("message", ComponentArgument.component())
				.executes(commandContext -> DiscordMessageQueue.INSTANCE.queue(TextComponentUtils.updateForEntity(commandContext.getSource(), ComponentArgument.getComponent(commandContext, "message"), commandContext.getSource().getEntity(), 0).getString(), s -> commandContext.getSource().sendErrorMessage(new StringTextComponent(s)))));
	}

	private TellDiscordCommand() {
	}
}
