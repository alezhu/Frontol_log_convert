package ru.alezhu.frontol_log_convert.frontol.tables.condtions;

@SuppressWarnings("unused")
public class GrowCondition extends CompareCondition {
	public GrowCondition(final Object value1, final Object value2) {
		super(">", value1, value2);
	}

}
