package mod.grimmauld.discordchat.slashcommand;

import mod.grimmauld.discordchat.DiscordChat;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.*;
import java.text.DecimalFormat;

@ParametersAreNonnullByDefault
public class TpsCommand extends GrimmSlashCommand {
	private static final DecimalFormat df = new DecimalFormat("###.#");

	public TpsCommand(@Nullable String help, boolean global) {
		super(help, global);
	}

	private static long mean(long[] values) {
		long sum = 0L;
		for (long v : values)
			sum += v;
		return sum / values.length;
	}

	@Override
	protected void executeChecked(SlashCommandEvent event) {
		DiscordChat.SERVER_INSTANCE.runIfPresentAndAlive(server -> {
			double meanTickTime = mean(server.tickTimes) * 1.0E-6D;
			double meanTPS = meanTickTime <= 50 ? 20 : (1000.0 / meanTickTime);
			double x = MathHelper.clamp((meanTPS - 5) / 15, 0, 1);

			EmbedBuilder eb = new EmbedBuilder();
			eb.setTitle("Server TPS");
			eb.setColor(new Color(Math.min(255, (int) (512d * (1 - x))), Math.min(255, (int) (512d * x)), 0));


			for (RegistryKey<World> dim : server.levelKeys()) {
				long[] times = server.getTickTime(dim);
				eb.addField("Mean tick time in " + dim.getRegistryName(), df.format(times == null ? 0 : mean(times) * 1.0E-6D) + "ms", true);
			}
			eb.addBlankField(false);
			eb.addField("Mean tick time", df.format(meanTickTime) + "ms", true);
			eb.addField("Mean TPS", df.format(meanTPS), true);
			sendEmbedResponse(event, eb, true);
			return true;
		}).orElseGet(() -> sendNoServerResponse(event));
	}
}
