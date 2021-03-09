package mod.grimmauld.discordchat.util;

import com.mojang.brigadier.ResultConsumer;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ICommandSource;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
public class CommandSourceRedirectedOutput extends CommandSource {
	private final Consumer<ITextComponent> feedbackHook;

	protected CommandSourceRedirectedOutput(Consumer<ITextComponent> feedbackHook, ICommandSource sourceIn, Vec3d posIn, Vec2f rotationIn, ServerWorld worldIn, int permissionLevelIn, String nameIn, ITextComponent displayNameIn, MinecraftServer serverIn, @Nullable Entity entityIn, boolean feedbackDisabledIn, ResultConsumer<CommandSource> resultConsumerIn, EntityAnchorArgument.Type entityAnchorTypeIn) {
		super(sourceIn, posIn, rotationIn, worldIn, permissionLevelIn, nameIn, displayNameIn, serverIn, entityIn, feedbackDisabledIn, resultConsumerIn, entityAnchorTypeIn);
		this.feedbackHook = feedbackHook;
	}

	public static CommandSourceRedirectedOutput of(CommandSource from, Consumer<ITextComponent> feedbackHook) {
		return from instanceof CommandSourceRedirectedOutput ? (CommandSourceRedirectedOutput) from : new CommandSourceRedirectedOutput(feedbackHook, from.source, from.getPos(), from.getRotation(), from.getWorld(), from.permissionLevel, from.getName(), from.getDisplayName(), from.getServer(), from.getEntity(), from.feedbackDisabled, from.resultConsumer, from.getEntityAnchorType());
	}

	@Override
	public void sendFeedback(ITextComponent message, boolean allowLogging) {
		feedbackHook.accept(message);
		super.sendFeedback(message, allowLogging);
	}

	@Override
	public void sendErrorMessage(ITextComponent message) {
		feedbackHook.accept(message);
		super.sendErrorMessage(message);
	}
}
