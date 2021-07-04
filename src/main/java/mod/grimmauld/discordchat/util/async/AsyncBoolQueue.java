package mod.grimmauld.discordchat.util.async;

public class AsyncBoolQueue extends AsyncBool {
	private final AsyncBool[] queue;
	private int index = 0;

	public AsyncBoolQueue(AsyncBool... queue) {
		super(() -> false);
		this.queue = queue;
		tick();
	}

	@Override
	public AsyncTask stop() {
		for (AsyncBool asyncBool : queue) {
			asyncBool.stop();
		}
		return super.stop();
	}

	@Override
	protected void tick() {
		if (queue == null)
			return;
		while (index < queue.length) {
			queue[index].tick();
			if (queue[index].get())
				index++;
			else
				break;
		}

		if (index >= queue.length)
			this.check = () -> true;

		super.tick();
	}
}
