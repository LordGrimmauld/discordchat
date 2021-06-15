package mod.grimmauld.discordchat.slashcommand;

import mod.grimmauld.discordchat.DiscordBot;
import mod.grimmauld.discordchat.DiscordChat;
import mod.grimmauld.discordchat.util.CommandSourceRedirectedOutput;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class RunCommand extends GrimmSlashCommand {
	public RunCommand(@Nullable String help, boolean global) {
		super(help, global);
	}

	@Override
	protected void executeChecked(SlashCommandEvent event) {
		DiscordChat.SERVER_INSTANCE.runIfPresent(server -> {
			StringBuilder builder = new StringBuilder();
			String cmd = event.getOptions().stream().filter(optionMapping -> optionMapping.getName().equals("cmd") &&
				optionMapping.getType() == OptionType.STRING).map(OptionMapping::getAsString).findFirst().orElse("");

			server.getCommands().performCommand(CommandSourceRedirectedOutput.of(server.createCommandSourceStack()
				.withPermission(DiscordBot.isOp(event.getMember()) ? 2 : 0))
				.withName(event.getMember().getUser().getName())
				.withHook(text -> builder.append(text.getString()).append("\n")), cmd);
			sendResponse(event, builder.toString(), true);
			return true;
		}).orElseGet(() -> sendNoServerResponse(event));
	}

	@Override
	protected CommandData attachExtraData(CommandData data) {
		data.addOption(OptionType.STRING, "cmd", "The minecraft command to run");
		return super.attachExtraData(data);
	}
}
