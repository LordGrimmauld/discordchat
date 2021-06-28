package mod.grimmauld.discordchat.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import mod.grimmauld.discordchat.MessageQueue.DiscordMessageQueue;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.ComponentArgument;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponentUtils;

public class TellDiscordCommand {
	private TellDiscordCommand() {
	}

	public static ArgumentBuilder<CommandSource, ?> register() {
		return Commands.literal("telldiscord")
			.requires(player -> player.hasPermission(2))
			.then(Commands.argument("message", ComponentArgument.textComponent())
				.executes(commandContext -> DiscordMessageQueue.INSTANCE.queue(TextComponentUtils.updateForEntity(commandContext.getSource(), ComponentArgument.getComponent(commandContext, "message"), commandContext.getSource().getEntity(), 0).getString(), s -> commandContext.getSource().sendFailure(new StringTextComponent(s)))));
	}
}
