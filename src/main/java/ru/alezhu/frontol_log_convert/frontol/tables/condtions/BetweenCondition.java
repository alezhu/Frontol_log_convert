package ru.alezhu.frontol_log_convert.frontol.tables.condtions;

public class BetweenCondition implements ICondition {
	private final String field;
	private final Object low;
	private final Object high;

	public BetweenCondition(final String field, final Object low, final Object high) {
		this.field = field;
		this.low = low;
		this.high = high;

	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(this.field);
		sb.append(" BETWEEN '");
		sb.append(this.low.toString());
		sb.append("' AND '");
		sb.append(this.high.toString());
		sb.append("'");
		return sb.toString();
	}
}
