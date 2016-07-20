package ru.alezhu.frontol_log_convert.frontol.tables.base;

import ru.alezhu.frontol_log_convert.frontol.IFactory;
import ru.alezhu.frontol_log_convert.frontol.db.BaseDb;

import java.util.LinkedList;
import java.util.List;

public class BaseTable3Key<TPrimaryKey, TKey2, TKey3, TRow extends BaseRow>
		extends BaseTable2Key<TPrimaryKey, TKey2, TRow> {
	@SuppressWarnings("WeakerAccess")
	protected final IKeyDescription<TKey3, TRow> key3Description;

	public BaseTable3Key(final BaseDb db, final String tableName,
			final IKeyDescription<TPrimaryKey, TRow> key1Description,
			final IKeyDescription<TKey2, TRow> key2Description, final IKeyDescription<TKey3, TRow> key3Description,
			final IFactory<TRow> rowFactory) {
		super(db, tableName, key1Description, key2Description, rowFactory);
		this.key3Description = key3Description;

		super.addKeyDescription(key3Description);
	}

	protected TRow get(final TPrimaryKey key1, final TKey2 key2, final TKey3 key3) {
		TRow result = null;

		final List<TRow> list = this.getList(key1, key2, key3);
		if (list != null && list.size() > 0) {
			result = list.get(0);
		} else {
			// System.err.println("������ �� �������");
		}

		if (result == this.EmptyRow) {
			return null;
		}
		return result;
	}

	protected List<TRow> getList(final TPrimaryKey key1, final TKey2 key2, final TKey3 key3) {
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
		keyList.add(compositeKey);

		return super.getList(keyList);
	}

}
