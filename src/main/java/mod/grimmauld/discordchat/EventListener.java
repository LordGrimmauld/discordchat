package mod.grimmauld.discordchat;

import mod.grimmauld.discordchat.commands.AllCommands;
import mod.grimmauld.discordchat.util.DiscordMessageQueue;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.GameRules;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
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
	public void playerJoinEvent(PlayerEvent.PlayerLoggedInEvent event) {
		DiscordMessageQueue.INSTANCE.queue(event.getPlayer().getName().getString() + " joined the game", DiscordChat.LOGGER::warn);
	}

	@SubscribeEvent
	public void playerLoggedOutEvent(PlayerEvent.PlayerLoggedOutEvent event) {
		DiscordMessageQueue.send(event.getPlayer().getName().getString() + " left the game", Collections.singleton(DiscordChat.LOGGER::warn));
	}

	@SubscribeEvent
	public void playerDieEvent(LivingDeathEvent event) {
		if (!(event.getEntity() instanceof ServerPlayerEntity) || !event.getEntity().world.getGameRules().getBoolean(GameRules.SHOW_DEATH_MESSAGES))
			return;
		DiscordMessageQueue.send(((ServerPlayerEntity) event.getEntity()).getCombatTracker().getDeathMessage().getString(), Collections.singleton(DiscordChat.LOGGER::warn));
	}
}
