package ru.alezhu.frontol_log_convert.frontol.tables.condtions;

public class EqualCondition extends CompareCondition {

	public EqualCondition(final Object value1, final Object value2) {
		super("=", value1, value2);
	}

}
