package ru.alezhu.frontol_log_convert.frontol.tables;

import ru.alezhu.frontol_log_convert.frontol.db.BaseDb;
import ru.alezhu.frontol_log_convert.frontol.tables.base.BaseRow;
import ru.alezhu.frontol_log_convert.frontol.tables.base.BaseTable1Key;
import ru.alezhu.frontol_log_convert.frontol.tables.base.CompositeKey;
import ru.alezhu.frontol_log_convert.frontol.tables.base.KeyDescriptionInteger;
import ru.alezhu.frontol_log_convert.frontol.tables.condtions.AndCondition;
import ru.alezhu.frontol_log_convert.frontol.tables.condtions.EqualCondition;
import ru.alezhu.frontol_log_convert.frontol.tables.condtions.ICondition;

import java.util.List;

public class ActivitiesTable extends BaseTable1Key<Integer, ActivitiesTable.Row> {
	private static final String TABLE_NAME = "ACTIVITIES";

	public ActivitiesTable(final BaseDb db) {
		super(db, TABLE_NAME, new KeyDescriptionInteger<>("ID", (row) -> row.ID, true), Row::new);
	}

	public static class Row extends BaseRow {
		// ID FID /*Integer NOT NULL*/,
		public int ID;
		// CODE FRINT /*Integer NOT NULL*/,
		public int CODE;
		// NAME FVARCHAR255 /*Varchar(255) */,
		// TEXT FVARCHAR255 /*Varchar(255) */,
		// DELETED FLAG /*Integer NOT NULL*/,
		// CHNG FRINT64 /*Numeric(18,0) */,
		// BDOCODE FINT /*Integer */,
		// OWNERBDO FINT /*Integer */,
		// MARKETACTID FID /*Integer NOT NULL*/,
		public int MARKETACTID;
	}

	@Override
	protected ICondition buildCondition(final List<CompositeKey> keyList) {
		final ICondition condition = super.buildCondition(keyList);

		return new AndCondition(condition, new EqualCondition("DELETED", "0"));
	}

	@Override
	public Row get(final Integer id) {
		return super.get(id);
	}
}
