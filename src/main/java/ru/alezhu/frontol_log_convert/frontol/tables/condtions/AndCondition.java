package ru.alezhu.frontol_log_convert.frontol.tables.condtions;

public class AndCondition extends BinaryCondition {

    public AndCondition(final ICondition condition1, final ICondition condition2) {
        super("AND", condition1, condition2);
    }

}
