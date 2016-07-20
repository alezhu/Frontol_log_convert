package ru.alezhu.frontol_log_convert.frontol.tables.condtions;

public class OrCondition extends BinaryCondition {

	public OrCondition(final ICondition condition1, final ICondition condition2) {
		super("OR", condition1, condition2);
	}

}
