package ru.alezhu.frontol_log_convert.frontol.tables.condtions;

public class LikeCondition extends CompareCondition {

	public LikeCondition(final Object value1, final Object value2) {
		super("LIKE", value1, value2);
	}

}
