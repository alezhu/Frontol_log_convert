package ru.alezhu.frontol_log_convert.frontol.tables;

import ru.alezhu.frontol_log_convert.frontol.db.BaseDb;
import ru.alezhu.frontol_log_convert.frontol.tables.base.BaseRow;
import ru.alezhu.frontol_log_convert.frontol.tables.base.BaseTable1Key;
import ru.alezhu.frontol_log_convert.frontol.tables.base.CompositeKey;
import ru.alezhu.frontol_log_convert.frontol.tables.base.KeyDescriptionInteger;
import ru.alezhu.frontol_log_convert.frontol.tables.condtions.AndCondition;
import ru.alezhu.frontol_log_convert.frontol.tables.condtions.EqualCondition;
import ru.alezhu.frontol_log_convert.frontol.tables.condtions.ICondition;

import java.sql.Timestamp;
import java.util.List;

public class MarketActTable extends BaseTable1Key<Integer, MarketActTable.Row> {
	private static final String TABLE_NAME = "MARKETACT";

	public MarketActTable(final BaseDb db) {
		super(db, TABLE_NAME, new KeyDescriptionInteger<>("ID", (row) -> row.ID, true), Row::new);
	}

	public static class Row extends BaseRow {
		// ID FID /*Integer NOT NULL*/,
		public int ID;
		// CODE FRINT /*Integer NOT NULL*/,
		public int CODE;
		// NAME FVARCHAR255 /*Varchar(255) */,
		// TEXT FVARCHAR255 /*Varchar(255) */,
		// ISFOLDER FLAG /*Integer NOT NULL*/,
		// PARENTID RREF /*Integer NOT NULL*/,
		// DESCRIPTION FBLOBTEXT /*Blob SUB_TYPE 1 */,
		// DATETIMEBEG FDATETIME /*TIMESTAMP */,
		public Timestamp DATETIMEBEG;
		// DATETIMEEND FDATETIME /*TIMESTAMP */,
		public Timestamp DATETIMEEND;
		// ACTV FLAG /*Integer NOT NULL*/,
		// PRIORITY FINT /*Integer */,
		// COLOR FINT /*Integer */,
		// DELETED FLAG /*Integer NOT NULL*/,
		// CHNG FINT64 /*Numeric(18,0) */,
		// BDOCODE FINT /*Integer */,
		// OWNERBDO FINT /*Integer */,
		// OPTIONS FINT /*Integer */,
		// TASKLINKS FBLOB1024 /*Blob SUB_TYPE 0 */,
		// RECURRENCEINFO FBLOB1024 /*Blob SUB_TYPE 0 */,
		// RECURRENCEINDEX FRINT /*Integer NOT NULL*/,
		// RESOURCEID FINT /*Integer */,
		// EVENTTYPE FRINT /*Integer NOT NULL*/,
		// TASKINDEX FINT /*Integer */,
		// TASKSTATUS FINT /*Integer */,
		// GROUPID FINT /*Integer */,
		// LOCATION FVARCHAR255 /*Varchar(255) */,
		// "MESSAGE" FVARCHAR255 /*Varchar(255) */,
		// "TYPE" FINT /*Integer */,
		public int TYPE;
		// ALWAYSEXECUTE FLAG /*Integer NOT NULL*/,
		// COLLATERULE FINT /*Integer */,
		// final static private Row Empty = new Row();
	}

	@Override
	protected ICondition buildCondition(final List<CompositeKey> keyList) {
		final ICondition condition = super.buildCondition(keyList);

		return new AndCondition(condition, new EqualCondition("ACTV", "1"));
	}

	@Override
	public Row get(final Integer id) {
		return super.get(id);
	}
}
