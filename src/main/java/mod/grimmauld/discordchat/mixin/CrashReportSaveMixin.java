package mod.grimmauld.discordchat.mixin;

import mod.grimmauld.discordchat.Config;
import mod.grimmauld.discordchat.DiscordChat;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.minecraft.crash.CrashReport;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Mixin(MinecraftServer.class)
public class CrashReportSaveMixin {
	@Inject(at = @At("RETURN"), method = "onServerCrash")
	private void onOnServerCrash(CrashReport crash, CallbackInfo ci) {
		if (crash == null || crash.saveFile == null)
			return;

		DiscordChat.BOT_INSTANCE.ifJDAPresent(jda -> {
			String channelId = Config.REDIRECT_CHANNEL_ID.get();
			if (channelId.isEmpty())
				return handleError("Channel Id may not be empty!");

			MessageChannel channel = jda.getTextChannelById(channelId);
			if (channel == null)
				return handleError("Channel " + channelId + " can't be found");

			CompletableFuture<Message> resp = channel.sendFile(crash.saveFile).submit();
			try {
				resp.get();
			} catch (InterruptedException | ExecutionException e) {
				return handleError("Error waiting for discord to send the message: " + e);
			}
			return 0;
		}).orElseGet(() -> handleError("Can not send message to discord: jda not initialized"));
	}

	private static int handleError(String s) {
		DiscordChat.LOGGER.error(s);
		return 1;
	}
}
