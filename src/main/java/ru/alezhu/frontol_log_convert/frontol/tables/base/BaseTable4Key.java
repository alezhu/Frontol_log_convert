package ru.alezhu.frontol_log_convert.frontol.tables.base;

import ru.alezhu.frontol_log_convert.frontol.IFactory;
import ru.alezhu.frontol_log_convert.frontol.db.BaseDb;

import java.util.LinkedList;
import java.util.List;

public class BaseTable4Key<TPrimaryKey, TKey2, TKey3, TKey4, TRow extends BaseRow>
		extends BaseTable3Key<TPrimaryKey, TKey2, TKey3, TRow> {

	@SuppressWarnings("WeakerAccess")
	protected final IKeyDescription<TKey4, TRow> key4Description;

	public BaseTable4Key(final BaseDb db, final String tableName,
			final IKeyDescription<TPrimaryKey, TRow> key1Description,
			final IKeyDescription<TKey2, TRow> key2Description, final IKeyDescription<TKey3, TRow> key3Description,
			final IKeyDescription<TKey4, TRow> key4Description, final IFactory<TRow> rowFactory) {
		super(db, tableName, key1Description, key2Description, key3Description, rowFactory);
		this.key4Description = key4Description;

		super.addKeyDescription(key4Description);
	}

	protected TRow get(final TPrimaryKey key1, final TKey2 key2, final TKey3 key3, final TKey4 key4) {
		TRow result = null;

		final List<TRow> list = this.getList(key1, key2, key3);
		if (list.size() > 0) {
			result = list.get(0);
		}

		if (result == this.EmptyRow) {
			return null;
		}
		return result;
	}

	protected List<TRow> getList(final TPrimaryKey key1, final TKey2 key2, final TKey3 key3, final TKey4 key4) {
		final List<CompositeKey> keyList = new LinkedList<>();
		final CompositeKey compositeKey = new CompositeKey();
		if (this.keyDescription.isValid(key1)) {
			compositeKey.addValue(this.keyDescription.getFieldName(), key1);
		}
		if (this.key2Description.isValid(key2)) {
			compositeKey.addValue(this.key2Description.getFieldName(), key2);
		}
		if (this.key3Description.isValid(key3)) {
			compositeKey.addValue(this.key3Description.getFieldName(), key3);
		}
		if (this.key4Description.isValid(key4)) {
			compositeKey.addValue(this.key4Description.getFieldName(), key4);
		}
		keyList.add(compositeKey);

		return super.getList(keyList);
	}

}
