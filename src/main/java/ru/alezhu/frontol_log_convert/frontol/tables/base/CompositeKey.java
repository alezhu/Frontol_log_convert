package ru.alezhu.frontol_log_convert.frontol.tables.base;

import java.util.HashMap;
import java.util.Map;

public class CompositeKey {
	private Map<String, Object> keys;

	Map<String, Object> getKeys() {
		if (this.keys == null) {
			this.keys = new HashMap<>();
		}
		return this.keys;
	}

	public void addValue(final String key, final Object value) {
		this.getKeys().put(key, value);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.keys == null) ? 0 : this.keys.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final CompositeKey other = (CompositeKey) obj;
		if (this.keys == null) {
			if (other.keys != null) {
				return false;
			}
		} else if (!this.keys.equals(other.keys)) {
			return false;
		}
		return true;
	}

}
