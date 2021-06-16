package mod.grimmauld.discordchat.slashcommand;

import com.therandomlabs.curseapi.CurseAPI;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.file.CurseFile;
import com.therandomlabs.curseapi.file.CurseFiles;
import mod.grimmauld.discordchat.Config;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import okhttp3.HttpUrl;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class PackCommand extends GrimmSlashCommand {
	public PackCommand(@Nullable String help, boolean global) {
		super(help, global);
	}

	@Override
	protected void executeChecked(SlashCommandEvent event) throws CurseException {
		int id = Config.PROJECT_ID.get();
		sendResponse(event, CurseAPI.project(id)
			.orElseThrow(() -> new CurseException("Hmmmmmm.... That URL doesn't seem to exist"))
			.refreshFiles()
			.stream()
			.sorted(CurseFiles.SORT_BY_NEWEST)
			/*
			.filter(CurseModpack.class::isInstance)
			.map(CurseModpack.class::cast)
			.filter(pack -> pack.mcVersion().versionString().equals(MinecraftVersion.tryDetectVersion().getName()))
			 */
			.map(CurseFile::downloadURL)
			.map(HttpUrl::toString)
			.findFirst()
			.orElse("Could not find any file"), true);

	}
}
