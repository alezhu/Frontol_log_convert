package ru.alezhu.frontol_log_convert.frontol.tables.condtions;

class CompareCondition implements ICondition {
	private final String operator;
	private final Object value1;
	private final Object value2;

	CompareCondition(final String operator, final Object value1, final Object value2) {
		this.operator = operator;
		this.value1 = value1;
		this.value2 = value2;
	}

	@Override
	public String toString() {
		return this.value1.toString() +
				" " +
				this.operator +
				" " +
				this.value2.toString();
	}
}
