package mod.grimmauld.discordchat;

import com.mojang.brigadier.CommandDispatcher;
import mod.grimmauld.discordchat.commands.ReloadBotCommand;
import mod.grimmauld.discordchat.commands.StopBotCommand;
import mod.grimmauld.discordchat.commands.TellDiscordCommand;
import mod.grimmauld.discordchat.message_queue.ChatEventMessage;
import mod.grimmauld.discordchat.message_queue.DiscordMessageQueue;
import mod.grimmauld.discordchat.util.ThreadHelper;
import net.minecraft.advancements.Advancement;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameRules;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;


@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = BuildConfig.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EventListener {
	private EventListener() {
	}

	@SubscribeEvent
	public static void onServerStarted(FMLServerStartedEvent event) {
		DiscordChat.BOT_INSTANCE.relaunchBot();
		DiscordChat.BOT_INSTANCE.ifJDAPresent(jda -> {
			try {
				jda.awaitReady();
			} catch (InterruptedException e) {
				DiscordChat.LOGGER.error("Error while waiting for JDA to be ready: {}", e.getMessage());
			}
		});
		DiscordMessageQueue.INSTANCE.queue("Server started", DiscordChat.LOGGER::warn);
	}

	@SubscribeEvent
	public static void onServerStopped(FMLServerStoppedEvent event) {
		DiscordMessageQueue.INSTANCE.queue("Server shutting down...", DiscordChat.LOGGER::warn);
		ThreadHelper.runAfter(() -> DiscordChat.BOT_INSTANCE.ifPresent(DiscordBot::shutdown), 3600000);
	}

	@SubscribeEvent
	public static void serverStarted(FMLServerStartingEvent event) {
		DiscordChat.SERVER_INSTANCE.connect(event::getServer);
		CommandDispatcher<CommandSource> commandDispatcher = event.getServer().getCommands().getDispatcher();
		commandDispatcher.register(Commands.literal(BuildConfig.MODID).then(ReloadBotCommand.register()));
		commandDispatcher.register(Commands.literal(BuildConfig.MODID).then(TellDiscordCommand.register()));
		commandDispatcher.register(Commands.literal(BuildConfig.MODID).then(StopBotCommand.register()));
	}

	@SubscribeEvent
	public static void chatEvent(ServerChatEvent event) {
		DiscordMessageQueue.INSTANCE.queue(new ChatEventMessage(event));
	}

	@SubscribeEvent
	public static void playerJoinEvent(PlayerEvent.PlayerLoggedInEvent event) {
		DiscordMessageQueue.INSTANCE.queue(event.getPlayer().getName().getString() + " joined the game", DiscordChat.LOGGER::warn);
	}

	@SubscribeEvent
	public static void playerLoggedOutEvent(PlayerEvent.PlayerLoggedOutEvent event) {
		DiscordMessageQueue.INSTANCE.queue(event.getPlayer().getName().getString() + " left the game", DiscordChat.LOGGER::warn);
	}

	@SubscribeEvent
	public static void playerDieEvent(LivingDeathEvent event) {
		if (!(event.getEntity() instanceof ServerPlayerEntity) || !event.getEntity().level.getGameRules().getBoolean(GameRules.RULE_SHOWDEATHMESSAGES))
			return;
		DiscordMessageQueue.INSTANCE.queue(((ServerPlayerEntity) event.getEntity()).getCombatTracker().getDeathMessage().getString(), DiscordChat.LOGGER::warn);
	}

	@SubscribeEvent
	public static void advancementEvent(AdvancementEvent event) {
		Advancement advancement = event.getAdvancement();
		if (advancement.getDisplay() != null && advancement.getDisplay().shouldAnnounceChat() && event.getPlayer().level.getGameRules().getBoolean(GameRules.RULE_ANNOUNCE_ADVANCEMENTS)) {
			DiscordMessageQueue.INSTANCE.queue(new TranslationTextComponent("chat.type.advancement." + advancement.getDisplay().getFrame().getName(), event.getPlayer().getDisplayName(), advancement.getChatComponent()).getString().replace("[", "**[").replace("]", "]**"), DiscordChat.LOGGER::warn);
		}
	}
}
