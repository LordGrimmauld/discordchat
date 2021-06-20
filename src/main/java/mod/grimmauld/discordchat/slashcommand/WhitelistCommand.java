package mod.grimmauld.discordchat.slashcommand;

import mod.grimmauld.discordchat.Config;
import mod.grimmauld.discordchat.DiscordChat;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ExecutionException;

public class WhitelistCommand extends GrimmSlashCommand {
	public WhitelistCommand(@Nullable String help, boolean global) {
		super(help, global);
	}

	@Override
	protected void executeChecked(SlashCommandEvent event) {
		event.reply("Your whitelist request is now waiting for Admin approval").setEphemeral(true).submit();
		event.getOptions().stream().filter(optionMapping -> optionMapping.getName().equals("ign") &&
			optionMapping.getType() == OptionType.STRING).map(OptionMapping::getAsString).forEach(ign -> {
			try {
				event.getChannel().sendMessage(Config.PREFIX.get() + getName() + " " + ign).submit().get().addReaction("\u2705").submit();
			} catch (InterruptedException | ExecutionException e) {
				DiscordChat.LOGGER.error("Could not submit whitelist request for {}: {}", ign, e);
			}
		});
	}

	@Override
	protected CommandData attachExtraData(CommandData data) {
		data.addOption(OptionType.STRING, "ign", "Your in-game-name to whitelist", true);
		return super.attachExtraData(data);
	}
}
