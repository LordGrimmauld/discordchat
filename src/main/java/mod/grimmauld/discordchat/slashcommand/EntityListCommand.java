package mod.grimmauld.discordchat.slashcommand;

import mod.grimmauld.discordchat.DiscordBot;
import mod.grimmauld.discordchat.DiscordChat;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class EntityListCommand extends GrimmSlashCommand {
	protected EntityListCommand(@Nullable String help, boolean global) {
		super(help, global);
	}

	@Override
	protected void executeChecked(SlashCommandEvent event) {
		DiscordChat.SERVER_INSTANCE.runIfPresent(server -> {
			if (!DiscordBot.isOp(event.getMember())) {
				sendLackingPermissionResponse(event);
				return false;
			}

			String et = event.getOptions().stream().filter(optionMapping -> optionMapping.getName().equals("ign") &&
				optionMapping.getType() == OptionType.STRING).map(OptionMapping::getAsString).findFirst().orElse("");

			EntityType<?> type = ForgeRegistries.ENTITIES.getValue(ResourceLocation.tryParse(et));
			if (type == null) {
				sendResponse(event, "Entity type could not be found!", true);
				return false;
			}

			EmbedBuilder eb = new EmbedBuilder();
			eb.setTitle("Entities");

			server.getAllLevels().forEach(world -> {
				StringBuilder builder = new StringBuilder();
				world.getEntities(type, entity -> true).forEach(entity -> builder.append(entity.getName().getString()).append(" at ").append((int) entity.getX()).append(" ").append((int) entity.getY()).append(" ").append((int) entity.getZ()).append("\n"));
				eb.addField("Entities in " + world.dimension().location(), builder.toString(), true);
			});
			sendEmbedResponse(event, eb, true);
			return true;
		}).orElseGet(() -> sendNoServerResponse(event));
	}

	@Override
	protected CommandData attachExtraData(CommandData data) {
		data.addOption(OptionType.STRING, "entitytype", "The entity type to get a list of");
		return super.attachExtraData(data);
	}
}
