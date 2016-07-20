package ru.alezhu.frontol_log_convert.frontol.tables.base;

import java.util.HashMap;
import java.util.Map;

class TableIndexCache<TKey, TPrimaryKey, TRow extends BaseRow> {

	private class ValueCache {
		boolean Complete;
		private Map<TPrimaryKey, TRow> map;

		Map<TPrimaryKey, TRow> getMap() {
			if (this.map == null) {
				this.map = new HashMap<>();
			}
			return this.map;
		}
	}

	private Map<TKey, ValueCache> map;

	private Map<TKey, ValueCache> getMap() {
		if (this.map == null) {
			this.map = new HashMap<>();
		}
		return this.map;
	}

	private ValueCache getValueCache(final TKey value) {
		final Map<TKey, TableIndexCache<TKey, TPrimaryKey, TRow>.ValueCache> map = this.getMap();
		TableIndexCache<TKey, TPrimaryKey, TRow>.ValueCache result = map.get(value);
		if (result == null) {
			result = new ValueCache();
			map.put(value, result);
		}
		return result;

	}

	void setValueComplete(final TKey value) {
		this.getValueCache(value).Complete = true;
	}

	boolean isValueComplete(final TKey value) {
		return this.getValueCache(value).Complete;
	}

	public Map<TPrimaryKey, TRow> getList(final TKey value) {
		return this.getValueCache(value).getMap();
	}

}
