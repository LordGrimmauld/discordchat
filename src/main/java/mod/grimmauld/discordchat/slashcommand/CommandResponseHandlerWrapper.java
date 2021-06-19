package mod.grimmauld.discordchat.slashcommand;

import me.lucko.spark.common.SparkPlatform;
import me.lucko.spark.common.command.CommandResponseHandler;
import me.lucko.spark.common.command.sender.CommandSender;
import me.lucko.spark.lib.adventure.text.Component;
import me.lucko.spark.lib.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minecraft.util.text.ITextComponent;

public class CommandResponseHandlerWrapper extends CommandResponseHandler {
	private final StringBuilder outputCollection;

	public CommandResponseHandlerWrapper(StringBuilder outputCollection, SparkPlatform platform, CommandSender sender) {
		super(platform, sender);
		this.outputCollection = outputCollection;
	}

	public void reply(Component message) {
		outputCollection.append(ITextComponent.Serializer.fromJson(GsonComponentSerializer.gson().serialize(message)).getString()).append("\n");
	}

	public void broadcast(Component message) {
		this.reply(message);
	}
}
