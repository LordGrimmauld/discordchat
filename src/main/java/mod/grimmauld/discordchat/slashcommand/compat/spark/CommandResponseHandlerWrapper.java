package mod.grimmauld.discordchat.slashcommand.compat.spark;

import me.lucko.spark.common.SparkPlatform;
import me.lucko.spark.common.command.CommandResponseHandler;
import me.lucko.spark.common.command.sender.CommandSender;
import me.lucko.spark.lib.adventure.text.Component;
import me.lucko.spark.lib.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minecraft.util.text.ITextComponent;

import java.util.function.Consumer;

public class CommandResponseHandlerWrapper extends CommandResponseHandler {
	private final StringBuilder outputCollection;
	private final Consumer<StringBuilder> onChanged;

	public CommandResponseHandlerWrapper(StringBuilder outputCollection, SparkPlatform platform, CommandSender sender, Consumer<StringBuilder> onChanged) {
		super(platform, sender);
		this.outputCollection = outputCollection;
		this.onChanged = onChanged;
	}

	public void reply(Component message) {
		outputCollection.append(ITextComponent.Serializer.fromJson(GsonComponentSerializer.gson().serialize(message)).getString()).append("\n");
		onChanged.accept(outputCollection);
	}

	public void broadcast(Component message) {
		this.reply(message);
	}
}
