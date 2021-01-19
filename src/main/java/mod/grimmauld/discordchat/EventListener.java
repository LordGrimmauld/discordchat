package mod.grimmauld.discordchat;

import mod.grimmauld.discordchat.commands.AllCommands;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

public class EventListener {
	@SubscribeEvent
	public void serverStarted(FMLServerStartingEvent event) {
		DiscordChat.SERVER_INSTANCE = event.getServer();
		AllCommands.register(event.getCommandDispatcher());
	}

	@SubscribeEvent
	public void chatEvent(ServerChatEvent event) {
		if (DiscordChat.INSTANCE == null)
			return;
		if (DiscordChat.INSTANCE.jda == null)
			return;
		String channelId = Config.REDIRECT_CHANNEL_ID.get();
		if (channelId.isEmpty()) {
			DiscordChat.LOGGER.error("Channel Id may not be empty!");
			return;
		}
		MessageChannel channel = DiscordChat.INSTANCE.jda.getTextChannelById(channelId);
		if (channel == null)
			return;

		String msg = "[MC " + event.getUsername() + "] " + event.getMessage().replaceAll("@", "@ ");
		channel.sendMessage(msg).queue();

	}
}
