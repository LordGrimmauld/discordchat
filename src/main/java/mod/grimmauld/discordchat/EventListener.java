package mod.grimmauld.discordchat;

import mod.grimmauld.discordchat.commands.AllCommands;
import mod.grimmauld.discordchat.util.DiscordMessageQueue;
import net.minecraft.advancements.Advancement;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameRules;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;


@SuppressWarnings("unused")
public class EventListener {
	private int tickSyncCycle = Config.SYNC_RATE.get();

	public static void onServerStarted(FMLServerStartedEvent event) {
		DiscordChat.relaunchBot();
	}

	public static void onServerStopped(FMLServerStoppedEvent event) {
		DiscordMessageQueue.INSTANCE.queue("Server shutting down...", DiscordChat.LOGGER::warn);
		DiscordMessageQueue.INSTANCE.send();
		DiscordChat.BOT_INSTANCE.ifPresent(DiscordBot::shutdown);
	}

	@SubscribeEvent
	public void serverStarted(FMLServerStartingEvent event) {
		DiscordChat.SERVER_INSTANCE = event.getServer();
		AllCommands.register(event.getCommandDispatcher());
	}

	@SubscribeEvent
	public void chatEvent(ServerChatEvent event) {
		DiscordMessageQueue.INSTANCE.queue("**[MC " + event.getUsername() + "]** " + event.getMessage().replace("@", "@ "), DiscordChat.LOGGER::warn);
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
		DiscordMessageQueue.INSTANCE.queue(event.getPlayer().getName().getString() + " left the game", DiscordChat.LOGGER::warn);
	}

	@SubscribeEvent
	public void playerDieEvent(LivingDeathEvent event) {
		if (!(event.getEntity() instanceof ServerPlayerEntity) || !event.getEntity().world.getGameRules().getBoolean(GameRules.SHOW_DEATH_MESSAGES))
			return;
		DiscordMessageQueue.INSTANCE.queue(((ServerPlayerEntity) event.getEntity()).getCombatTracker().getDeathMessage().getString(), DiscordChat.LOGGER::warn);
	}

	@SubscribeEvent
	public void advancementEvent(AdvancementEvent event) {
		Advancement advancement = event.getAdvancement();
		if (advancement.getDisplay() != null && advancement.getDisplay().shouldAnnounceToChat() && event.getPlayer().world.getGameRules().getBoolean(GameRules.ANNOUNCE_ADVANCEMENTS)) {
			DiscordMessageQueue.INSTANCE.queue(new TranslationTextComponent("chat.type.advancement." + advancement.getDisplay().getFrame().getName(), event.getPlayer().getDisplayName(), advancement.getDisplayText()).getString().replace("[", "**[").replace("]", "]**"), DiscordChat.LOGGER::warn);
		}
	}
}
