package ru.alezhu.frontol_log_convert.frontol.tables;

import ru.alezhu.frontol_log_convert.frontol.db.BaseDb;
import ru.alezhu.frontol_log_convert.frontol.tables.base.BaseRow;
import ru.alezhu.frontol_log_convert.frontol.tables.base.BaseTable;

import java.util.List;

public class IntBarcsTable extends BaseTable<IntBarcsTable.Row> {
	private static final String TABLE = "INTBARCS";

	public IntBarcsTable(final BaseDb db) {
		super(db, TABLE, Row::new);
	}

	public static class Row extends BaseRow {
		// ID FID /*Integer NOT NULL*/,
		public int ID;
		// CODE FINT /*Integer */ NOT NULL,
		public int CODE;
		// NAME FVARCHAR100 /*Varchar(100) */,
		public String NAME;
		// PREFIXBEG FVARCHAR40 /*Varchar(40) */,
		public String PREFIXBEG;
		// PREFIXEND FVARCHAR40 /*Varchar(40) */,
		public String PREFIXEND;
		// "LENGTH" FINT /*Integer */,
		public int LENGTH;
		// DATA FVARCHAR255 /*Varchar(255) */,
		public String DATA;
		// BDOCODE FINT /*Integer */,
		// CHNG FINT64 /*Numeric(18,0) */,
		// OWNERBDO FINT /*Integer */,
		// DELETED FLAG /*Integer NOT NULL*/,
	}

	@Override
	public List<Row> getAllRows() {
		return super.getAllRows();
	}

}
