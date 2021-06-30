package mod.grimmauld.discordchat.slashcommand.compat.spark;

import me.lucko.spark.common.command.sender.CommandSender;
import me.lucko.spark.lib.adventure.text.Component;
import me.lucko.spark.lib.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minecraft.util.text.ITextComponent;

import java.util.UUID;
import java.util.function.Consumer;

public class DiscordCommandSender implements CommandSender {
	private static final UUID uuid = UUID.fromString("65072ccd-f530-4dd0-a11c-fe2986a53ca6");

	private final StringBuilder outputCollection;
	private final String name;
	private final Consumer<StringBuilder> onChanged;

	public DiscordCommandSender(StringBuilder outputCollection, String name, Consumer<StringBuilder> onChanged) {
		this.name = name;
		this.outputCollection = outputCollection;
		this.onChanged = onChanged;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public UUID getUniqueId() {
		return uuid;
	}

	@Override
	public void sendMessage(Component component) {
		outputCollection.append(ITextComponent.Serializer.fromJson(GsonComponentSerializer.gson().serialize(component)).getString()).append("\n");
		onChanged.accept(outputCollection);
	}

	@Override
	public boolean hasPermission(String s) {
		return true;
	}
}
