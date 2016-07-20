package ru.alezhu.frontol_log_convert.frontol.tables.base;

import ru.alezhu.frontol_log_convert.frontol.IFactory;
import ru.alezhu.frontol_log_convert.frontol.db.BaseDb;

import java.util.LinkedList;
import java.util.List;

public class BaseTable2Key<TPrimaryKey, TKey2, TRow extends BaseRow> extends BaseTable1Key<TPrimaryKey, TRow> {
	@SuppressWarnings("WeakerAccess")
	protected final IKeyDescription<TKey2, TRow> key2Description;

	public BaseTable2Key(final BaseDb db, final String tableName,
			final IKeyDescription<TPrimaryKey, TRow> key1Description,
			final IKeyDescription<TKey2, TRow> key2Description, final IFactory<TRow> rowFactory) {
		super(db, tableName, key1Description, rowFactory);
		this.key2Description = key2Description;

		super.addKeyDescription(key2Description);
	}

	protected TRow get(final TPrimaryKey key1, final TKey2 key2) {
		TRow result = null;

		final List<TRow> list = this.getList(key1, key2);
		if (list.size() > 0) {
			result = list.get(0);
		}

		if (result == this.EmptyRow) {
			return null;
		}
		return result;
	}

	protected List<TRow> getList(final TPrimaryKey key1, final TKey2 key2) {

		final List<CompositeKey> keyList = new LinkedList<>();
		final CompositeKey compositeKey = new CompositeKey();
		if (this.keyDescription.isValid(key1)) {
			compositeKey.addValue(this.keyDescription.getFieldName(), key1);
		}
		if (this.key2Description.isValid(key2)) {
			compositeKey.addValue(this.key2Description.getFieldName(), key2);
		}
		keyList.add(compositeKey);

		return super.getList(keyList);
	}

}
