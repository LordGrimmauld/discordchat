package mod.grimmauld.discordchat.mixin;

import mod.grimmauld.discordchat.Config;
import mod.grimmauld.discordchat.DiscordChat;
import net.minecraft.server.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Main.class)
public class KeepServerAliveMixin {
	@Inject(method = "main", at = @At("RETURN"))
	private static void onStopServer(CallbackInfo ci) {
		while (!DiscordChat.CAN_KILL_PROCESS.get()) {
			try {
				Thread.sleep(Config.ASYNC_TASK_CHECK_INTEVAL.get());
			} catch (InterruptedException e) {
				DiscordChat.LOGGER.error("Error while keeping server thread alive: {}", e.getMessage());
			}
		}
		DiscordChat.LOGGER.info("Stopping server aliveness");
		System.exit(0);
	}
}
