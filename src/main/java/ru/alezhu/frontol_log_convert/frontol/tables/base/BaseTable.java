package ru.alezhu.frontol_log_convert.frontol.tables.base;

import ru.alezhu.frontol_log_convert.frontol.IFactory;
import ru.alezhu.frontol_log_convert.frontol.db.BaseDb;
import ru.alezhu.frontol_log_convert.frontol.tables.condtions.EqualCondition;
import ru.alezhu.frontol_log_convert.frontol.tables.condtions.ICondition;
import ru.alezhu.frontol_log_convert.frontol.tables.condtions.MultiCondition;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

@SuppressWarnings("WeakerAccess")
public abstract class BaseTable<TRow extends BaseRow> {
	protected final String TableName;
	private final BaseDb DataBase;
	protected List<String> RowFields;
	protected Map<String, IKeyDescription<?, TRow>> Keys;
	protected IKeyDescription<?, TRow> PrimaryKey;
	protected List<TRow> AllRows;
	private Map<String, TableIndexCache<Object, Object, TRow>> Cache;
	protected final TRow EmptyRow;
	private final IFactory<TRow> factory;

	public BaseTable(final BaseDb db, final String tableName, final IFactory<TRow> factory) {
		this.DataBase = db;
		this.TableName = tableName;
		this.factory = factory;
		this.EmptyRow = factory.create();
	}

	protected Map<String, IKeyDescription<?, TRow>> getKeyDescriptions() {
		if (this.Keys == null) {
			this.Keys = new HashMap<>();
		}
		return this.Keys;
	}

	protected void addKeyDescription(final IKeyDescription<?, TRow> keyDescription) {
		this.getKeyDescriptions().put(keyDescription.getFieldName(), keyDescription);
		if (keyDescription.isPrimaryKey()) {
			this.PrimaryKey = keyDescription;
		}
	}

	protected List<String> getFieldsForRow(final Class<?> rowClass) {
		if (this.RowFields == null) {
			this.RowFields = new LinkedList<>();
			for (final Field field : rowClass.getFields()) {
				this.RowFields.add(field.getName());
			}
		}
		return this.RowFields;
	}

	protected ICondition buildCondition(final CompositeKey keys) {
		final List<ICondition> list = new LinkedList<>();
		final Map<String, ?> map = keys.getKeys();
		final Map<String, IKeyDescription<?, TRow>> keyDescriptions = this.getKeyDescriptions();

		map.forEach((key, value) -> {
			final IKeyDescription<?, TRow> keyDescription = keyDescriptions.get(key);
			if (keyDescription != null && keyDescription.isValid(value)) {
				list.add(new EqualCondition(key, value));
			}
		});

		switch (list.size()) {
			case 0:
				return null;
			case 1:
				return list.get(0);
			default:
				return new MultiCondition("AND", list);
		}
	}

	protected ICondition buildCondition(final List<CompositeKey> keyList) {
		final List<ICondition> list = new LinkedList<>();
		for (final CompositeKey compositeKey : keyList) {
			final ICondition condition = this.buildCondition(compositeKey);
			if (condition != null) {
				list.add(condition);
			}
		}
		if (list.size() == 0) {
			return null;
		}

		return new MultiCondition("OR", list);
	}

	protected List<TRow> getList(final List<CompositeKey> keyList) {

		this.normalizeKeyList(keyList);
		if (keyList == null || keyList.size() == 0) {
			return null;
		}
		final List<TRow> result = new LinkedList<>();
		final List<CompositeKey> toSelect = new LinkedList<>();

		final Set<String> ignoreKeys = new HashSet<>();

		for (final CompositeKey compositeKey : keyList) {
			ignoreKeys.clear();
			final Map<String, ?> keys = compositeKey.getKeys();
			if (this.PrimaryKey != null) {
				// Если задан первичный ключ пробуем искать в кэше по нему
				final String keyFieldName = this.PrimaryKey.getFieldName();
				ignoreKeys.add(keyFieldName);
				final Object keyValue = keys.get(keyFieldName);
				if (this.PrimaryKey.isValid(keyValue)) {
					// Если значение первичного ключа в условии допустимое
					final TableIndexCache<Object, Object, TRow> keyCache = this.getKeyCache(keyFieldName);
					final Map<Object, TRow> map = keyCache.getList(keyValue);
					final TRow row = map.get(keyValue);
					if (row != null) {
						// Запись нашли - выбирать из БД не надо
						if (row != this.EmptyRow && this.isRowValid4CompositeKey(row, compositeKey, ignoreKeys)) {
							// если не пустая запись и подходит по другим ключам
							// - добавляем в результат
							result.add(row);
						}
					} else {
						// Запись не нашли - чтобы не было в других условиях -
						// надо выбирать из БД
						toSelect.add(compositeKey);
					}
					continue;
				} else {
					// если значение недопустимое - выкинем его вообще
					keys.remove(keyFieldName);
				}
			}
			// сюда мы попадаем если первичный ключ для таблицы не задан либо
			// если в текущем композитном ключе значение для первичного ключа не
			// задано. Ищем по вторичным ключам
			final Map<String, IKeyDescription<?, TRow>> keyDescriptions = this.getKeyDescriptions();
			for (final Map.Entry<String, ?> keyEntry : keys.entrySet()) {
				final String key = keyEntry.getKey();
				ignoreKeys.add(key);
				final IKeyDescription<?, TRow> keyDescription = keyDescriptions.get(key);
				final Object value = keyEntry.getValue();
				if (keyDescription.isValid(value)) {
					// Значение ключа допустимое
					final TableIndexCache<Object, Object, TRow> keyCache = this.getKeyCache(key);
					if (keyCache.isValueComplete(value)) {
						// если по этому значению была полная выброрка (т.е.
						// только по этому значению)
						// больше выбирать не надо - добавляем все строки
						// которые проходят по остальным ключам
						final Map<Object, TRow> list = keyCache.getList(value);
						//noinspection Convert2streamapi
						for (final TRow row : list.values()) {
							if (row != this.EmptyRow && this.isRowValid4CompositeKey(row, compositeKey, ignoreKeys)) {
								result.add(row);
							}
						}
					} else {
						// иначе выбираем , возможно повторно, но писать
						// кэширование по набору значений ключей - это увольте,
						// итак уже "космос"
						toSelect.add(compositeKey);
						break;// Из Цикла по ключам выходим, чтобы не было
						// дублей
					}
				} else {
					// если значение недопустимое - выкинем его вообще
					keys.remove(key);
				}
			}

		}

		// если есть что выбирать - выбираем
		if (toSelect.size() > 0) {
			final ICondition condition = this.buildCondition(toSelect);
			final List<TRow> selected = this.select(condition);
			if (selected != null) {
				result.addAll(selected);
				this.add2Cache(keyList, selected);
			} else {
				this.add2CacheEmptyRow(keyList);
			}
		}

		return result;
	}

	private void normalizeKeyList(final List<CompositeKey> keyList) {
		final Map<String, IKeyDescription<?, TRow>> keyDescriptions = this.getKeyDescriptions();

		keyList.removeIf(compositeKey -> {
			final Map<String, ?> keys = compositeKey.getKeys();
			for (final Entry<String, ?> entry : keys.entrySet()) {
				final IKeyDescription<?, TRow> keyDescription = keyDescriptions.get(entry.getKey());
				if (!keyDescription.isValid(entry.getValue())) {
					keys.remove(entry.getKey());
				}
			}
			return keys.size() == 0;
		});

	}

	private void add2CacheEmptyRow(final List<CompositeKey> keyList) {
		final List<TRow> rows = Collections.singletonList(this.EmptyRow);
		keyList.forEach((compositeKey) -> this.add2Cache(compositeKey, rows, false));
	}

	private void add2Cache(final List<CompositeKey> keyList, final List<TRow> rowList) {
		final Map<CompositeKey, List<TRow>> links = this.getLinks(keyList, rowList);

		links.forEach((compositeKey, rows) -> this.add2Cache(compositeKey, rows, false));
	}

	private void add2Cache(final CompositeKey compositeKey, final List<TRow> rows, final boolean forceComplete) {
		final Map<String, ?> keys = compositeKey.getKeys();
		final boolean isComplete = forceComplete || keys.size() == 1;

		keys.entrySet().forEach(entry -> {
			final String keyFieldName = entry.getKey();
			final TableIndexCache<Object, Object, TRow> keyCache = this.getKeyCache(keyFieldName);
			final Map<Object, TRow> list = keyCache.getList(entry.getValue());
			if (isComplete) {
				keyCache.setValueComplete(entry.getValue());
			}
			for (final TRow row : rows) {
				if (this.PrimaryKey != null) {
					list.putIfAbsent(this.PrimaryKey.extractFromRow(row), row);
				} else {
					list.putIfAbsent(row.hashCode(), row);
				}
			}
		});
	}

	private void add2Cache(final List<TRow> rows, final boolean complete) {
		final Map<String, IKeyDescription<?, TRow>> keyDescriptions = this.getKeyDescriptions();
		rows.forEach((row) -> {
			Object primaryKeyValue;
			if (this.PrimaryKey != null) {
				primaryKeyValue = this.PrimaryKey.extractFromRow(row);
			} else {
				primaryKeyValue = row.hashCode();
			}
			keyDescriptions.forEach((keyName, desc) -> {
				final TableIndexCache<Object, Object, TRow> keyCache = this.getKeyCache(keyName);
				final Object value = desc.extractFromRow(row);
				final Map<Object, TRow> list = keyCache.getList(value);
				list.putIfAbsent(primaryKeyValue, row);
				if (complete) {
					keyCache.setValueComplete(value);
				}
			});
		});
	}

	private Map<CompositeKey, List<TRow>> getLinks(final List<CompositeKey> keyList, final List<TRow> rows) {
		final Map<CompositeKey, List<TRow>> result = new HashMap<>();

		for (final CompositeKey compositeKey : keyList) {
			final List<TRow> list = new LinkedList<>();
			result.put(compositeKey, list);
			list.addAll(rows.stream().filter(row -> row != this.EmptyRow && this.isRowValid4CompositeKey(row, compositeKey, null)).collect(Collectors.toList()));
		}

		return result;

	}

	private boolean isRowValid4CompositeKey(final TRow row, final CompositeKey compositeKey,
											final Set<String> ignoreKey) {
		final Map<String, ?> map = compositeKey.getKeys();
		final Map<String, IKeyDescription<?, TRow>> keyDescriptions = this.getKeyDescriptions();
		for (final Entry<String, IKeyDescription<?, TRow>> keyEntry : keyDescriptions.entrySet()) {
			final String keyName = keyEntry.getKey();
			if (ignoreKey == null || !ignoreKey.contains(keyName)) {
				final Object keyValue = map.get(keyName);
				final IKeyDescription<?, TRow> desc = keyEntry.getValue();
				if (desc.isValid(keyValue)) {
					final Object rowValue = desc.extractFromRow(row);
					if (!rowValue.equals(keyValue)) {
						return false;
					}
				}
			}
		}
		return true;
	}

	protected List<TRow> select(final ICondition where) {
		final List<TRow> result = new LinkedList<>();

		final Class<? extends BaseRow> rowClass = this.EmptyRow.getClass();
		final List<String> fieldNames = this.getFieldsForRow(rowClass);
		String whereString = null;
		try {
			final Statement statement = this.getDataBase().getConnection().createStatement();
			final StringBuilder sb = new StringBuilder();
			sb.append("SELECT ");
			sb.append(String.join(", ", fieldNames));
			sb.append(" FROM ");
			sb.append(this.TableName);
			if (where != null) {
				whereString = where.toString();

				if (whereString != null && !whereString.isEmpty()) {
					sb.append(" WHERE ");
					sb.append(where);
				}
			}
			final ResultSet rs = statement.executeQuery(sb.toString());

			while (rs.next()) {
				TRow row;
				try {
					row = this.factory.create();
					for (final String fieldName : fieldNames) {
						try {
							final Object value = rs.getObject(fieldName);
							if (value != null) {
								rowClass.getField(fieldName).set(row, value);
							}
						} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException
								| SecurityException e) {
							e.printStackTrace();
						}
					}
					result.add(row);
				} catch (final Exception e1) {
					e1.printStackTrace();
				}

			}
		} catch (final SQLException e) {
			System.err.println(whereString);
			e.printStackTrace();
		}
		return result;
	}

	protected List<TRow> getAllRows() {
		if (this.AllRows == null) {
			this.AllRows = this.select(null);
			this.add2Cache(this.AllRows, true);
		}
		return this.AllRows;
	}

	protected Map<String, TableIndexCache<Object, Object, TRow>> getCache() {
		if (this.Cache == null) {
			this.Cache = new HashMap<>();
		}
		return this.Cache;
	}

	protected TableIndexCache<Object, Object, TRow> getKeyCache(final String key) {
		final Map<String, TableIndexCache<Object, Object, TRow>> cache = this.getCache();
		TableIndexCache<Object, Object, TRow> result = cache.get(key);
		if (result == null) {
			result = new TableIndexCache<>();
			cache.put(key, result);
		}
		return result;
	}

	public BaseDb getDataBase() {
		return DataBase;
	}

}
