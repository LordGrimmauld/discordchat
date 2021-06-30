package mod.grimmauld.discordchat.slashcommand.compat.spark;

import mcp.MethodsReturnNonnullByDefault;
import me.lucko.spark.common.SparkPlatform;
import me.lucko.spark.common.command.Arguments;
import me.lucko.spark.common.command.CommandModule;
import me.lucko.spark.common.command.CommandResponseHandler;
import me.lucko.spark.common.command.modules.SamplerModule;
import me.lucko.spark.common.command.sender.CommandSender;
import mod.grimmauld.discordchat.DiscordChat;
import mod.grimmauld.discordchat.slashcommand.GrimmSlashCommand;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class SparkCommand extends GrimmSlashCommand {
	@Nullable
	public static SparkPlatform platform = null;

	public SparkCommand(@Nullable String help, boolean global) {
		super(help, global);
	}

	@Nullable
	private static SamplerModule getSamplerModule() throws ReflectiveOperationException {
		if (platform == null)
			return null;

		try {
			Field mediaTypeConstant = SparkPlatform.class.getDeclaredField("commandModules");
			mediaTypeConstant.setAccessible(true);
			return ((List<CommandModule>) mediaTypeConstant.get(platform))
				.stream()
				.filter(SamplerModule.class::isInstance)
				.map(SamplerModule.class::cast)
				.findFirst()
				.orElse(null);

		} catch (NoSuchFieldException | SecurityException | IllegalAccessException | ClassCastException e) {
			DiscordChat.LOGGER.error("Can't get spark media type format: {}", e.getMessage());
			throw new ReflectiveOperationException("Could not invoke spark sample: " + e.getMessage());
		}
	}

	private static void startProfiler(SamplerModule samplerModule, CommandSender sender, CommandResponseHandler resp, Arguments arguments) throws ReflectiveOperationException {
		if (platform == null)
			return;

		try {
			Method profilerStart = SamplerModule.class.getDeclaredMethod("profilerStart", SparkPlatform.class, CommandSender.class, CommandResponseHandler.class, Arguments.class);
			profilerStart.setAccessible(true);
			profilerStart.invoke(samplerModule, platform, sender, resp, arguments);
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			DiscordChat.LOGGER.error("Could not invoke spark sample: {}", e.getMessage());
			throw new ReflectiveOperationException("Could not invoke spark sample: " + e.getMessage());
		}
	}

	private static String getExceptLast(StringBuilder outputCollection) {
		return outputCollection.length() >= 1 ? outputCollection.substring(0, outputCollection.length() - 1) : "No Output";
	}

	@Override
	protected void executeChecked(SlashCommandEvent event) {
		if (platform == null) {
			sendResponse(event, "No Spark installation could be found", true);
			return;
		}


		SamplerModule samplerModule;
		try {
			samplerModule = getSamplerModule();
		} catch (ReflectiveOperationException e) {
			sendResponse(event, "No Spark Sampler module could be found: " + e.getMessage(), true);
			return;
		}
		if (samplerModule == null) {
			sendResponse(event, "No Spark Sampler module could be found", true);
			return;
		}

		StringBuilder outputCollection = new StringBuilder();

		InteractionHook process;
		try {
			process = event.reply("Starting Profiling...").setEphemeral(true).submit().get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			return;
		}


		Consumer<StringBuilder> onChanged = builder -> process.editOriginal(getExceptLast(builder)).submit();
		DiscordCommandSender sender = new DiscordCommandSender(outputCollection, event.getUser().getName(), onChanged);
		CommandResponseHandlerWrapper handler = new CommandResponseHandlerWrapper(outputCollection, platform, sender, onChanged);


		long duration = event.getOptions()
			.stream()
			.filter(optionMapping -> optionMapping.getName().equals("duration") && optionMapping.getType() == OptionType.INTEGER)
			.map(OptionMapping::getAsLong)
			.findFirst()
			.orElse(120L);

		Arguments arguments = new Arguments(Arrays.asList("--timeout", String.valueOf(duration), "--thread", "*"));


		try {
			startProfiler(samplerModule, sender, handler, arguments);
		} catch (ReflectiveOperationException e) {
			String msg = "Failed to start profiler: " + e.getMessage();
			process.editOriginal(msg).submit();
		}
	}

	@Override
	protected CommandData attachExtraData(CommandData data) {
		data.addOption(OptionType.INTEGER, "duration", "The time span over which to run the profiling [in seconds]");
		return super.attachExtraData(data);
	}
}
