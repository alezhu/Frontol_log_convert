package ru.alezhu.frontol_log_convert.frontol.tables;

import ru.alezhu.frontol_log_convert.frontol.db.BaseDb;
import ru.alezhu.frontol_log_convert.frontol.tables.base.BaseRow;
import ru.alezhu.frontol_log_convert.frontol.tables.base.BaseTable3Key;
import ru.alezhu.frontol_log_convert.frontol.tables.base.KeyDescriptionInteger;

import java.util.List;

public class WareDiscountTable extends BaseTable3Key<Integer, Integer, Integer, WareDiscountTable.Row> {

	private static final String TABLE_NAME = "WAREDISCOUNT";

	public WareDiscountTable(final BaseDb db) {
		super(db, TABLE_NAME, new KeyDescriptionInteger<>("ID", (row) -> row.ID, true),
				new KeyDescriptionInteger<>("WAREID", (row) -> row.WAREID, false),
				new KeyDescriptionInteger<>("DCE_MODULE_ID", (row) -> row.DCE_MODULE_ID, false), Row::new);
	}

	public static class Row extends BaseRow {
		// ID FID /*Integer NOT NULL*/,
		public int ID;
		// WAREID FINT /*Integer */,
		public int WAREID;
		// ASPECTVALUE1ID FINT /*Integer */ DEFAULT 0 NOT NULL,
		// ASPECTVALUE2ID FINT /*Integer */ DEFAULT 0 NOT NULL,
		// ASPECTVALUE3ID FINT /*Integer */ DEFAULT 0 NOT NULL,
		// ASPECTVALUE4ID FINT /*Integer */ DEFAULT 0 NOT NULL,
		// ASPECTVALUE5ID FINT /*Integer */ DEFAULT 0 NOT NULL,
		// DCE_MODULE_ID FID /*Integer NOT NULL*/,
		public int DCE_MODULE_ID;
		// DELETED FLAG /*Integer NOT NULL*/,
		// DISCOUNT FVARCHAR255 /*Varchar(255) */,
		public String DISCOUNT;
		// DISCOUNTTYPE FINT /*Integer */,
		public int DISCOUNTTYPE;
		// final static private Row Empty = new Row();

	}

	@Override
	public Row get(final Integer id, final Integer wareId, final Integer dceId) {
		return super.get(id, wareId, dceId);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * ru.alezhu.frontol_log_convert.ru.alezhu.frontol_log_convert.frontol.tables.base.BaseTable3Key#getList(java.lang.
	 * Object, java.lang.Object, java.lang.Object)
	 */
	@Override
	public List<Row> getList(final Integer id, final Integer wareId, final Integer dceId) {
		return super.getList(id, wareId, dceId);
	}

}
