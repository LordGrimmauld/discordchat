package mod.grimmauld.discordchat.util.container;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class LazyOptionalContainer<T> {

	@Nullable
	private T object = null;
	@Nullable
	private Supplier<T> supplier = null;


	public LazyOptionalContainer<T> connect(Supplier<T> supplier) {
		this.invalidate();
		this.supplier = supplier;
		return this;
	}

	public void invalidate() {
		object = null;
		supplier = null;
	}

	public <U> Optional<U> runIfPresent(Function<T, U> action) {
		if (object == null) {
			if (supplier != null) {
				set(supplier.get());
			} else {
				return Optional.empty();
			}
		}
		if (object != null)
			return Optional.of(action.apply(object));
		return Optional.empty();
	}

	public boolean isPresent() {
		return supplier != null || object != null;
	}

	public boolean ifPresent(Consumer<T> action) {
		return runIfPresent(bot -> {
			action.accept(bot);
			return true;
		}).orElse(false);
	}

	private void set(T object) {
		this.object = object;
		this.supplier = null;
	}
}
