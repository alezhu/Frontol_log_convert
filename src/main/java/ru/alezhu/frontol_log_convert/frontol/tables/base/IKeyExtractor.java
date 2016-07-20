package ru.alezhu.frontol_log_convert.frontol.tables.base;

public interface IKeyExtractor<TKey, TRow> {
	TKey extractKey(TRow row);
}
