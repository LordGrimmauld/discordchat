package mod.grimmauld.discordchat.util;

import com.mojang.brigadier.ResultConsumer;
import mod.grimmauld.discordchat.commands.CommandSourceWrapper;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ICommandSource;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
public class CommandSourceRedirectedOutput extends CommandSource {
	private final Consumer<ITextComponent> feedbackHook;
	public final CommandSourceWrapper sourceWrapper;

	private CommandSourceRedirectedOutput(Consumer<ITextComponent> feedbackHook, ICommandSource sourceIn, Vector3d posIn, Vector2f rotationIn, ServerWorld worldIn, int permissionLevelIn, String nameIn, ITextComponent displayNameIn, MinecraftServer serverIn, @Nullable Entity entityIn, boolean feedbackDisabledIn, ResultConsumer<CommandSource> resultConsumerIn, EntityAnchorArgument.Type entityAnchorTypeIn) {
		super(sourceIn, posIn, rotationIn, worldIn, permissionLevelIn, nameIn, displayNameIn, serverIn, entityIn, feedbackDisabledIn, resultConsumerIn, entityAnchorTypeIn);
		sourceWrapper = CommandSourceWrapper.of(sourceIn);
		this.source = sourceWrapper;
		this.feedbackHook = feedbackHook;
	}

	public static CommandSourceRedirectedOutput of(CommandSource from) {
		return new CommandSourceRedirectedOutput(text -> {},
			from.source,
			from.getPos(),
			from.getRotation(),
			from.getWorld(),
			from.permissionLevel,
			from.getName(),
			from.getDisplayName(),
			from.getServer(),
			from.getEntity(),
			from.feedbackDisabled,
			from.resultConsumer,
			from.getEntityAnchorType());
	}

	public CommandSourceRedirectedOutput withHook(Consumer<ITextComponent> feedbackHook) {
		return new CommandSourceRedirectedOutput(feedbackHook,
			this.source,
			this.getPos(),
			this.getRotation(),
			this.getWorld(),
			this.permissionLevel,
			this.getName(),
			this.getDisplayName(),
			this.getServer(),
			this.getEntity(),
			this.feedbackDisabled,
			this.resultConsumer,
			this.getEntityAnchorType());
	}

	public CommandSourceRedirectedOutput withName(String name) {
		return new CommandSourceRedirectedOutput(this.feedbackHook,
			this.source,
			this.getPos(),
			this.getRotation(),
			this.getWorld(),
			this.permissionLevel,
			name,
			new StringTextComponent(name),
			this.getServer(),
			this.getEntity(),
			this.feedbackDisabled,
			this.resultConsumer,
			this.getEntityAnchorType());
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
