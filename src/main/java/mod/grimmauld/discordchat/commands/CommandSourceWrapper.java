package mod.grimmauld.discordchat.commands;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.command.ICommandSource;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CommandSourceWrapper implements ICommandSource {

	private final ICommandSource commandSource;
	private Supplier<Boolean> shouldReceiveFeedback;
	private Supplier<Boolean> shouldReceiveErrors;
	private Supplier<Boolean> allowLogging;

	private CommandSourceWrapper(ICommandSource commandSource) {
		this.commandSource = commandSource;
		shouldReceiveFeedback = commandSource::shouldReceiveFeedback;
		shouldReceiveErrors = commandSource::shouldReceiveErrors;
		allowLogging = commandSource::allowLogging;
	}

	public static CommandSourceWrapper of(ICommandSource source) {
		return source instanceof CommandSourceWrapper ? (CommandSourceWrapper) source : new CommandSourceWrapper(source);
	}

	@Override
	public void sendMessage(ITextComponent component, UUID senderUUID) {
		commandSource.sendMessage(component, senderUUID);
	}

	@Override
	public boolean shouldReceiveFeedback() {
		return shouldReceiveFeedback.get();
	}

	@Override
	public boolean shouldReceiveErrors() {
		return shouldReceiveErrors.get();
	}

	@Override
	public boolean allowLogging() {
		return allowLogging.get();
	}

	public CommandSourceWrapper withShouldReceiveFeedback(boolean shouldReceiveFeedback) {
		this.shouldReceiveFeedback = () -> shouldReceiveFeedback;
		return this;
	}

	public CommandSourceWrapper withShouldReceiveErrors(boolean shouldReceiveErrors) {
		this.shouldReceiveErrors = () -> shouldReceiveErrors;
		return this;
	}

	public CommandSourceWrapper withAllowLogging(boolean allowLogging) {
		this.allowLogging = () -> allowLogging;
		return this;
	}

	public CommandSourceWrapper withShouldReceiveFeedback(Supplier<Boolean> shouldReceiveFeedback) {
		this.shouldReceiveFeedback = shouldReceiveFeedback;
		return this;
	}

	public CommandSourceWrapper withShouldReceiveErrors(Supplier<Boolean> shouldReceiveErrors) {
		this.shouldReceiveErrors = shouldReceiveErrors;
		return this;
	}

	public CommandSourceWrapper withAllowLogging(Supplier<Boolean> allowLogging) {
		this.allowLogging = allowLogging;
		return this;
	}
}
