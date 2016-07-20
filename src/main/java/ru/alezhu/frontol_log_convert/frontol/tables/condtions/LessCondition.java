package ru.alezhu.frontol_log_convert.frontol.tables.condtions;

public class LessCondition extends CompareCondition {

	public LessCondition(final Object value1, final Object value2) {
		super("<", value1, value2);
	}

}
