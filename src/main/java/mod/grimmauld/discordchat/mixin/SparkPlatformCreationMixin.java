package mod.grimmauld.discordchat.mixin;

import me.lucko.spark.common.SparkPlatform;
import me.lucko.spark.common.SparkPlugin;
import mod.grimmauld.discordchat.slashcommand.compat.spark.SparkCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SparkPlatform.class)
public class SparkPlatformCreationMixin {
	@Inject(at = @At(value = "RETURN"), method = "<init>", remap = false, require = 1)
	private void onInstanceCreated(SparkPlugin plugin, CallbackInfo ci) {
		SparkCommand.platform = (SparkPlatform) (Object) this;
	}
}
