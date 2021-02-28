package mod.grimmauld.discordchat.discordcommand;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.examples.doc.Author;
import mod.grimmauld.discordchat.DiscordChat;
import net.dv8tion.jda.api.EmbedBuilder;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.dimension.DimensionType;

import java.awt.*;
import java.text.DecimalFormat;


@Author("Grimmauld")
public class TpsCommand extends GrimmCommand {
	public static final String NAME = "tps";
	private static final DecimalFormat df = new DecimalFormat("###.#");

	public TpsCommand() {
		super(NAME);
		help = "Displays minecraft server tps.";
	}

	private static long mean(long[] values) {
		long sum = 0L;
		for (long v : values)
			sum += v;
		return sum / values.length;
	}

	@Override
	protected void executeChecked(CommandEvent event) {
		double meanTickTime = mean(DiscordChat.SERVER_INSTANCE.tickTimeArray) * 1.0E-6D;
		double meanTPS = meanTickTime <= 50 ? 20 : (1000.0 / meanTickTime);
		double x = MathHelper.clamp((meanTPS - 5) / 15, 0, 1);

		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle("Server TPS");
		eb.setColor(new Color(Math.min(255, (int) (512d * (1 - x))), Math.min(255, (int) (512d * x)), 0));

		for (DimensionType dim : DimensionType.getAll()) {
			long[] times = DiscordChat.SERVER_INSTANCE.getTickTime(dim);
			eb.addField("Mean tick time in " + dim.getRegistryName(), df.format(times == null ? 0 : mean(times) * 1.0E-6D) + "ms", true);
		}
		eb.addBlankField(false);
		eb.addField("Mean tick time", df.format(meanTickTime) + "ms", true);
		eb.addField("Mean TPS", df.format(meanTPS), true);
		event.getChannel().sendMessage(eb.build()).submit();
	}
}
