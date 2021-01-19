package mod.grimmauld.discordchat;

import mod.grimmauld.discordchat.commands.AllCommands;
import mod.grimmauld.discordchat.util.DiscordMessageQueue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

import java.util.Collections;


@SuppressWarnings("unused")
public class EventListener {
	private int tickSyncCycle = Config.SYNC_RATE.get();

	@SubscribeEvent
	public void serverStarted(FMLServerStartingEvent event) {
		DiscordChat.SERVER_INSTANCE = event.getServer();
		AllCommands.register(event.getCommandDispatcher());
	}

	@SubscribeEvent
	public void chatEvent(ServerChatEvent event) {
		DiscordMessageQueue.INSTANCE.queue("**[MC " + event.getUsername() + "]** " + event.getMessage().replaceAll("@", "@ "), DiscordChat.LOGGER::warn);
	}

	@SubscribeEvent
	public void serverTickEvent(TickEvent.ServerTickEvent event) {
		tickSyncCycle--;
		if (tickSyncCycle == 0) {
			resetSyncCycle();
			DiscordMessageQueue.INSTANCE.send();
		}
	}

	public void resetSyncCycle() {
		int syncRate = Config.SYNC_RATE.get();
		tickSyncCycle = syncRate > 0 ? syncRate : -1;
	}

	@SubscribeEvent
	public void playerJoinEvent(EntityJoinWorldEvent event) {
		if (event.getWorld().isRemote)
			return;
		Entity e = event.getEntity();
		if (!(e instanceof PlayerEntity))
			return;
		DiscordMessageQueue.INSTANCE.queue(e.getName().getString() + " joined the game", DiscordChat.LOGGER::warn);
	}

	@SubscribeEvent
	public void playerLoggedOutEvent(PlayerEvent.PlayerLoggedOutEvent event) {
		DiscordMessageQueue.INSTANCE.send(event.getPlayer().getName().getString() + " left the game", Collections.singleton(DiscordChat.LOGGER::warn));
	}
}
