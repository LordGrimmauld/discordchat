package mod.grimmauld.discordchat.slashcommand;

import me.lucko.spark.common.command.sender.CommandSender;
import me.lucko.spark.lib.adventure.text.Component;
import me.lucko.spark.lib.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minecraft.util.text.ITextComponent;

import java.util.UUID;

public class DiscordCommandSender implements CommandSender {
	private static final UUID uuid = UUID.fromString("65072ccd-f530-4dd0-a11c-fe2986a53ca6");

	private final StringBuilder outputCollection;

	private final String name;

	public DiscordCommandSender(StringBuilder outputCollection, String name) {
		this.name = name;
		this.outputCollection = outputCollection;
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
	}

	@Override
	public boolean hasPermission(String s) {
		return true;
	}
}
