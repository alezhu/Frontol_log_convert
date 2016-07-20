package ru.alezhu.frontol_log_convert.frontol.tables.condtions;

public class NotCondition extends UnaryCondition {

	public NotCondition(final ICondition condition) {
		super("NOT", condition);
	}

}
