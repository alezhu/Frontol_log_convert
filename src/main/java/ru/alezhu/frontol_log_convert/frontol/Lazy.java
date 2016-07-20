package ru.alezhu.frontol_log_convert.frontol;

import java.util.function.Supplier;

public class Lazy<T> {
	private T instance;
	private final Supplier<T> factory;

	public Lazy(final Supplier<T> factory) {
		this.factory = factory;
	}

	public T get() {
		if (this.instance == null) {
			try {
				this.instance = this.factory.get();
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
		return this.instance;
	}

	public boolean Instanced() {
		return this.instance != null;
	}

}
