package ru.alezhu.frontol_log_convert.frontol.tables.base;

public class KeyDescriptionString<TRow> implements IKeyDescription<String, TRow> {
	private final String field;
	private final IKeyExtractor<String, TRow> extractor;
	private final boolean primaryKey;

	public KeyDescriptionString(final String field, final IKeyExtractor<String, TRow> extractor,
			final boolean primaryKey) {
		this.field = field;
		this.extractor = extractor;
		this.primaryKey = primaryKey;

	}

	private boolean _isValid(final String value) {
		return value != null && !value.isEmpty();
	}

	@Override
	public String getFieldName() {
		return this.field;
	}

	@Override
	public String extractFromRow(final TRow row) {
		return this.extractor.extractKey(row);
	}

	@Override
	public boolean isValid(final Object value) {
		return this._isValid((String) value);
	}

	@Override
	public boolean isPrimaryKey() {
		return this.primaryKey;
	}

}
