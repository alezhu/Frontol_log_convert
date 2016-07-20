package ru.alezhu.frontol_log_convert.frontol.tables.base;

public interface IKeyDescription<TKey, TRow> {
	boolean isValid(Object value);

	String getFieldName();

	TKey extractFromRow(TRow row);

	boolean isPrimaryKey();
}
