package ru.alezhu.frontol_log_convert.frontol.tables.base;

public class KeyDescriptionInteger<TRow> implements IKeyDescription<Integer, TRow> {
	private final String field;
	private final IKeyExtractor<Integer, TRow> extractor;
	private final boolean primaryKey;

	public KeyDescriptionInteger(final String field, final IKeyExtractor<Integer, TRow> extractor,
			final boolean primaryKey) {
		this.field = field;
		this.extractor = extractor;
		this.primaryKey = primaryKey;

	}

	private boolean _isValid(final Integer value) {
		return value != null && value != 0;
	}

	@Override
	public String getFieldName() {
		return this.field;
	}

	@Override
	public Integer extractFromRow(final TRow row) {
		return this.extractor.extractKey(row);
	}

	@Override
	public boolean isValid(final Object value) {
		return this._isValid((Integer) value);
	}

	@Override
	public boolean isPrimaryKey() {
		return this.primaryKey;
	}

}
