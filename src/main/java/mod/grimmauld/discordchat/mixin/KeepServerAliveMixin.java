package mod.grimmauld.discordchat.mixin;

import mod.grimmauld.discordchat.Config;
import mod.grimmauld.discordchat.DiscordChat;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class KeepServerAliveMixin {
	@Inject(method = "runServer", at = @At("RETURN"))
	public void onStopServer(CallbackInfo ci) {
		if (!(((Object) this) instanceof DedicatedServer)) {
			return;
		}

		while (!DiscordChat.CAN_KILL_PROCESS.get()) {
			try {
				Thread.sleep(Config.ASYNC_TASK_CHECK_INTEVAL.get());
			} catch (InterruptedException e) {
				DiscordChat.LOGGER.error("Error while keeping server thread alive: {}", e.getMessage());
			}
		}
	}
}
