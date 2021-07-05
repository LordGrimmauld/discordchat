package mod.grimmauld.discordchat.mixin;

import mod.grimmauld.discordchat.DiscordChat;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraftforge.server.console.TerminalHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(DedicatedServer.class)
public class TerminalHandlerNoCrashMixin {
	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraftforge/server/console/TerminalHandler;handleCommands(Lnet/minecraft/server/dedicated/DedicatedServer;)Z", remap = false),
		method = "Lnet/minecraft/server/dedicated/DedicatedServer;initServer()Z")
	private static boolean onHandleCommands(DedicatedServer server) {
		try {
			return TerminalHandler.handleCommands(server);
		} catch (Exception e) {
			DiscordChat.LOGGER.error("Error terminating forge console reader (proceeding anyways): {}", e.getMessage());
		}
		return false;
	}
}
