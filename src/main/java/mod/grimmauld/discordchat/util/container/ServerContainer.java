package mod.grimmauld.discordchat.util.container;

import net.minecraft.server.MinecraftServer;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class ServerContainer extends LazyOptionalContainer<MinecraftServer> {
	public <U> Optional<U> runIfPresentAndAlive(Function<MinecraftServer, U> action) {
		if (!isPresentAndAlive())
			return Optional.empty();
		return runIfPresent(action);
	}

	public boolean isPresentAndAlive() {
		return isPresent() && runIfPresent(server -> server.isRunning() && !server.isStopped()).orElse(false);
	}

	public boolean ifPresentAndAlive(Consumer<MinecraftServer> action) {
		if (!isPresentAndAlive())
			return false;
		return ifPresent(action);
	}
}
