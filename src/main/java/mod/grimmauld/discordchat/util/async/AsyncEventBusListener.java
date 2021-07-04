package mod.grimmauld.discordchat.util.async;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class AsyncEventBusListener<T extends Event> extends AsyncBool {
	public AsyncEventBusListener() {
		super(() -> false);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void onT(T event) {
		check = () -> true;
		tick();
	}
}
