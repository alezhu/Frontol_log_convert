package ru.alezhu.frontol_log_convert.frontol.tables.condtions;

class UnaryCondition implements ICondition {
	private final String operator;
	private final ICondition condition;

	UnaryCondition(final String operator, final ICondition condition) {
		this.operator = operator;
		this.condition = condition;

	}

	@Override
	public String toString() {
		return "(" +
				this.operator +
				" " +
				this.condition.toString() +
				")";
	}
}
