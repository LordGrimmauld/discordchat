package mod.grimmauld.discordchat.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import mod.grimmauld.discordchat.Config;
import mod.grimmauld.discordchat.DiscordChat;
import mod.grimmauld.discordchat.util.DiscordMessageQueue;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.ComponentArgument;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponentUtils;

public class TellDiscordCommand {
	public static ArgumentBuilder<CommandSource, ?> register() {
		return Commands.literal("telldiscord")
			.requires((p_198820_0_) -> p_198820_0_.hasPermissionLevel(2))
			.then(Commands.argument("message", ComponentArgument.component())
				.executes((commandContext) -> {
				if (DiscordChat.INSTANCE == null || DiscordChat.INSTANCE.jda == null) {
					commandContext.getSource().sendErrorMessage(new StringTextComponent("Can not send message to discord: jda not initialized"));
					return 1;
				}
				String channelId = Config.REDIRECT_CHANNEL_ID.get();
				if (channelId.isEmpty()) {
					DiscordChat.LOGGER.error("Channel Id may not be empty!");
					commandContext.getSource().sendErrorMessage(new StringTextComponent("Channel Id may not be empty!"));
					return 1;
				}
				MessageChannel channel = DiscordChat.INSTANCE.jda.getTextChannelById(channelId);
				if (channel == null) {
					commandContext.getSource().sendErrorMessage(new StringTextComponent("Channel " + channelId + " can't be found"));
					return 1;
				}
				DiscordMessageQueue.INSTANCE.queue(TextComponentUtils.updateForEntity(commandContext.getSource(), ComponentArgument.getComponent(commandContext, "message"), commandContext.getSource().getEntity(), 0).getString(), s -> commandContext.getSource().sendErrorMessage(new StringTextComponent(s)));
				return 0;
			}));
	}
}
