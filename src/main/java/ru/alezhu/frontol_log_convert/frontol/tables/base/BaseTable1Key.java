package ru.alezhu.frontol_log_convert.frontol.tables.base;

import ru.alezhu.frontol_log_convert.frontol.IFactory;
import ru.alezhu.frontol_log_convert.frontol.db.BaseDb;

import java.util.LinkedList;
import java.util.List;

public class BaseTable1Key<TPrimaryKey, TRow extends BaseRow> extends BaseTable<TRow> {

	@SuppressWarnings("WeakerAccess")
	protected final IKeyDescription<TPrimaryKey, TRow> keyDescription;

	public BaseTable1Key(final BaseDb db, final String tableName,
			final IKeyDescription<TPrimaryKey, TRow> keyDescription, final IFactory<TRow> rowFactory) {
		super(db, tableName, rowFactory);
		this.keyDescription = keyDescription;
		super.addKeyDescription(keyDescription);
	}

	protected TRow get(final TPrimaryKey key) {
		TRow result = null;

		final List<TRow> list = this.getList(key);
		if (list.size() > 0) {
			result = list.get(0);
		}

		if (result == this.EmptyRow) {
			return null;
		}
		return result;
	}

	protected List<TRow> getList(final TPrimaryKey key) {
		final List<CompositeKey> keyList = new LinkedList<>();
		final CompositeKey compositeKey = new CompositeKey();
		if (this.keyDescription.isValid(key)) {
			compositeKey.addValue(this.keyDescription.getFieldName(), key);
		}
		keyList.add(compositeKey);

		return super.getList(keyList);
	}

}
