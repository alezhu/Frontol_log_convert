package ru.alezhu.frontol_log_convert.frontol.tables.condtions;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class MultiCondition implements ICondition {
	private final List<ICondition> list;
	private final String operation;

	public MultiCondition(final String operation, final List<ICondition> list) {
		this.operation = operation;
		this.list = list;
	}

	MultiCondition(final String operation, final ICondition... conditions) {
		this.operation = operation;
		this.list = new LinkedList<>();
		Collections.addAll(this.list, conditions);
	}

	@Override
	public String toString() {
		if (this.list == null || this.list.size() == 0) {
			return "";
		}
		final List<String> strings = new LinkedList<>();
		for (final ICondition condition : this.list) {
			final String string = condition.toString();
			if (string != null && !string.isEmpty()) {
				strings.add(string);
			}
		}
		switch (strings.size()) {
		case 0:
			return "";
		case 1:
			return strings.get(0);
		default:
			return "(" + String.join(" " + this.operation + " ", strings) + ")";
		}

	}
}
