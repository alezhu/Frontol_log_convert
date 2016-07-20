package ru.alezhu.frontol_log_convert.frontol.tables;

import ru.alezhu.frontol_log_convert.frontol.db.BaseDb;
import ru.alezhu.frontol_log_convert.frontol.tables.base.BaseRow;
import ru.alezhu.frontol_log_convert.frontol.tables.base.BaseTable2Key;
import ru.alezhu.frontol_log_convert.frontol.tables.base.KeyDescriptionInteger;

import java.util.List;

public class DCEModuleTable extends BaseTable2Key<Integer, Integer, DCEModuleTable.Row> {

	private static final String TABLE_NAME = "DCE_MODULE";

	public DCEModuleTable(final BaseDb db) {
		super(db, TABLE_NAME, new KeyDescriptionInteger<Row>("ID", (row) -> row.ID, true),
				new KeyDescriptionInteger<Row>("STOCK_ID", (row) -> row.STOCK_ID, false), Row::new);
	}

	public static class Row extends BaseRow {
		// ID FID /*Integer NOT NULL*/,
		public int ID;
		// X FINT /*Integer */,
		// Y FINT /*Integer */,
		// VIEW_TYPE FVARCHAR255 /*Varchar(255) */,
		// STOCK_ID RREF /*Integer NOT NULL*/,
		public int STOCK_ID;
		// TYPE_CODE FINT /*Integer */,
		public int TYPE_CODE;

		// final static private Row Empty = new Row();
	}

	@Override
	public List<Row> getList(final Integer id, final Integer stockId) {
		return super.getList(id, stockId);
	}

	@Override
	public Row get(final Integer id, final Integer stockId) {
		return super.get(id, stockId);
	}
}
