package mod.grimmauld.discordchat.util.async;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class AsyncBool extends AsyncTask {
	private final AtomicBoolean value = new AtomicBoolean();
	public Set<Runnable> onTurnTrue = new HashSet<>();
	public Set<Runnable> onTurnFalse = new HashSet<>();
	protected Supplier<Boolean> check;

	public AsyncBool(Supplier<Boolean> check) {
		this.check = check;
		tick();
	}

	public static AsyncBool waitFor(long ms) {
		long end = System.currentTimeMillis() + ms;
		return new AsyncBool(() -> System.currentTimeMillis() > end);
	}

	public AsyncBool or(AsyncBool bool) {
		return new AsyncBool(() -> this.check.get() || bool.check.get()) {
			@Override
			public AsyncTask stop() {
				AsyncBool.this.stop();
				bool.stop();
				return super.stop();
			}

			@Override
			protected void tick() {
				AsyncBool.this.tick();
				super.tick();
			}
		};
	}

	public AsyncBool and(AsyncBool bool) {
		return new AsyncBool(() -> this.check.get() || bool.check.get()) {
			@Override
			public AsyncTask stop() {
				AsyncBool.this.stop();
				bool.stop();
				return super.stop();
			}

			@Override
			protected void tick() {
				AsyncBool.this.tick();
				super.tick();
			}
		};
	}

	public AsyncBool invert() {
		return new AsyncBool(() -> !check.get()) {
			@Override
			public AsyncTask stop() {
				AsyncBool.this.stop();
				return super.stop();
			}

			@Override
			protected void tick() {
				AsyncBool.this.tick();
				super.tick();
			}
		};
	}

	@Override
	protected void tick() {
		boolean newValue = check.get();

		if (value.getAndSet(newValue) != newValue) {
			if (newValue)
				onTurnTrue();
			else
				onTurnFalse();
		}
		value.set(check.get());
	}

	protected void onTurnTrue() {
		onTurnTrue.forEach(Runnable::run);
	}

	protected void onTurnFalse() {
		onTurnFalse.forEach(Runnable::run);
	}

	public AsyncBool onTurnTrue(Runnable runnable) {
		onTurnTrue.add(runnable);
		return this;
	}

	public AsyncBool onTurnFalse(Runnable runnable) {
		onTurnFalse.add(runnable);
		return this;
	}

	public boolean get() {
		return value.get();
	}

	public void forceValue(boolean value) {
		check = () -> value;
		tick();
	}
}
