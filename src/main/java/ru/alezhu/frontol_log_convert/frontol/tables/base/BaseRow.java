package ru.alezhu.frontol_log_convert.frontol.tables.base;

import java.lang.reflect.Field;

public abstract class BaseRow {
	// static final public BaseRow Empty = new BaseRow();
	private Integer HashCode;

	@Override
	public int hashCode() {
		if (this.HashCode == null) {
			this.HashCode = this.calcHash();
		}
		return this.HashCode;
	}

	private int calcHash() {
		final int prime = 31;
		int result = 1;
		final Class<? extends BaseRow> rowClass = this.getClass();
		final Field[] publicFields = rowClass.getFields();
		for (final Field field : publicFields) {
			try {
				final Object value = field.get(this);
				if (value != null) {
					result = result * prime + value.hashCode();
				}
			} catch (final IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

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
		final BaseRow other = (BaseRow) obj;
		return this.hashCode() == other.hashCode();
	}
}
