package mod.grimmauld.discordchat.util.async;

import mod.grimmauld.discordchat.Config;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public abstract class AsyncTask {
	private static final Set<AsyncTask> tasks = new HashSet<>();

	public static void tickAsyncTasks() {
		tasks.forEach(AsyncTask::tick);
	}

	public static void startTaskTicking() {
		Executors.newScheduledThreadPool(1).scheduleAtFixedRate(AsyncTask::tickAsyncTasks, 0, Config.ASYNC_TASK_CHECK_INTEVAL.get(), TimeUnit.MILLISECONDS);
	}

	protected abstract void tick();

	public AsyncTask start() {
		tasks.add(this);
		return this;
	}

	public AsyncTask stop() {
		tasks.remove(this);
		return this;
	}
}
