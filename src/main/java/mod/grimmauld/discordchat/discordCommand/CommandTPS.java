package mod.grimmauld.discordchat.discordCommand;

import com.jagrosh.jdautilities.command.CommandEvent;
import mod.grimmauld.discordchat.DiscordChat;
import mod.grimmauld.discordchat.commands.GrimmCommand;
import net.minecraft.world.dimension.DimensionType;

import java.text.DecimalFormat;

public class CommandTPS extends GrimmCommand {
	public static final String NAME = "tps";
	private static final DecimalFormat df = new DecimalFormat("###.#");

	public CommandTPS() {
		super(NAME);
	}

	private static long mean(long[] values) {
		long sum = 0L;
		for (long v : values)
			sum += v;
		return sum / values.length;
	}

	@Override
	protected void executeChecked(CommandEvent event) {
		StringBuilder builder = new StringBuilder();
		for (DimensionType dim : DimensionType.getAll()) {
			long[] times = DiscordChat.SERVER_INSTANCE.getTickTime(dim);
			double worldTickTime = times == null ? 0 : mean(times) * 1.0E-6D;
			builder.append("Mean tick time in ").append(dim.getRegistryName()).append(" is ").append(df.format(worldTickTime)).append("ms\n");
		}

		double meanTickTime = mean(DiscordChat.SERVER_INSTANCE.tickTimeArray) * 1.0E-6D;
		double meanTPS = Math.min(1000.0 / meanTickTime, 20);
		builder.append("\nMean tick time on Server is ").append(df.format(meanTickTime)).append("ms\n");
		builder.append("Mean TPS on Server is ").append(df.format(meanTPS)).append("\n");
		event.getChannel().sendMessage(builder.toString()).submit();
	}
}
