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
			from.getPosition(),
			from.getRotation(),
			from.getLevel(),
			from.permissionLevel,
			from.getTextName(),
			from.getDisplayName(),
			from.getServer(),
			from.getEntity(),
			from.silent,
			from.consumer,
			from.getAnchor());
	}

	public CommandSourceRedirectedOutput withHook(Consumer<ITextComponent> feedbackHook) {
		return new CommandSourceRedirectedOutput(feedbackHook,
			this.source,
			this.getPosition(),
			this.getRotation(),
			this.getLevel(),
			this.permissionLevel,
			this.getTextName(),
			this.getDisplayName(),
			this.getServer(),
			this.getEntity(),
			this.silent,
			this.consumer,
			this.getAnchor());
	}

	public CommandSourceRedirectedOutput withName(String name) {
		return new CommandSourceRedirectedOutput(this.feedbackHook,
			this.source,
			this.getPosition(),
			this.getRotation(),
			this.getLevel(),
			this.permissionLevel,
			name,
			new StringTextComponent(name),
			this.getServer(),
			this.getEntity(),
			this.silent,
			this.consumer,
			this.getAnchor());
	}

	@Override
	public void sendSuccess(ITextComponent message, boolean allowLogging) {
		feedbackHook.accept(message);
		super.sendSuccess(message, allowLogging);
	}

	@Override
	public void sendFailure(ITextComponent message) {
		feedbackHook.accept(message);
		super.sendFailure(message);
	}
}
