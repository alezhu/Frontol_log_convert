package ru.alezhu.frontol_log_convert.frontol.tables.condtions;

public class ValueCondition implements ICondition {
	private final Object value;

	public ValueCondition(final Object value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return this.value.toString();
	}
}
