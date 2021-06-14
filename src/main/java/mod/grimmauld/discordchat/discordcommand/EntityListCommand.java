package mod.grimmauld.discordchat.discordcommand;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.examples.doc.Author;
import mod.grimmauld.discordchat.DiscordBot;
import mod.grimmauld.discordchat.DiscordChat;
import net.dv8tion.jda.api.EmbedBuilder;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;


@Author("Grimmauld")
public class EntityListCommand extends GrimmCommand {
	public static final String NAME = "entitylist";

	public EntityListCommand() {
		super(NAME);
		help = "Lists entities to find lag hot spots";
	}

	@Override
	protected void executeChecked(CommandEvent event) {
		DiscordChat.SERVER_INSTANCE.runIfPresent(server -> {
			if (!DiscordBot.isOp(event.getMember())) {
				sendResponse(event, "You need operator role to run this command!");
				return false;
			}

			EntityType<?> type = ForgeRegistries.ENTITIES.getValue(ResourceLocation.tryParse(event.getMessage().getContentStripped().replaceFirst("([^ ])* ", "")));
			if (type == null) {
				sendResponse(event, "Entity type could not be found!");
				return false;
			}

			EmbedBuilder eb = new EmbedBuilder();
			eb.setTitle("Entities");

			server.getAllLevels().forEach(world -> {
				StringBuilder builder = new StringBuilder();
				world.getEntities(type, entity -> true).forEach(entity -> builder.append(entity.getName().getString()).append(" at ").append((int) entity.getX()).append(" ").append((int) entity.getY()).append(" ").append((int) entity.getZ()).append("\n"));
				eb.addField("Entities in " + world.dimension().location(), builder.toString(), true);
			});
			event.getChannel().sendMessage(eb.build()).submit();
			return true;
		}).orElseGet(() -> sendNoServerResponse(event));
	}
}
