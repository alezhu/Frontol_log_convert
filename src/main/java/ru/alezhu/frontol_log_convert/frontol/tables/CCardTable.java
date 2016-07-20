package ru.alezhu.frontol_log_convert.frontol.tables;

import ru.alezhu.frontol_log_convert.frontol.db.BaseDb;
import ru.alezhu.frontol_log_convert.frontol.tables.base.*;
import ru.alezhu.frontol_log_convert.frontol.tables.condtions.AndCondition;
import ru.alezhu.frontol_log_convert.frontol.tables.condtions.EqualCondition;
import ru.alezhu.frontol_log_convert.frontol.tables.condtions.ICondition;

import java.util.List;

public class CCardTable extends BaseTable2Key<Integer, String, CCardTable.Row> {

	private static final String TABLE_NAME = "CCARD";

	public CCardTable(final BaseDb db) {
		super(db, TABLE_NAME, new KeyDescriptionInteger<>("ID", (row) -> row.ID, true),
				new KeyDescriptionString<>("VAL", (row) -> row.VAL, false), Row::new);
	}

	public static class Row extends BaseRow {
		// ID FID /*Integer NOT NULL*/,
		public int ID;
		// CODE FRINT /*Integer NOT NULL*/,
		public int CODE;
		// GRPCCARDID RREF /*Integer NOT NULL*/,
		public int GRPCCARDID;
		// VAL FVARCHAR50 /*Varchar(50) */,
		public String VAL;
		// EXPDATEBEG FDATE /*Date */,
		// EXPDATEEND FDATE /*Date */,
		// STATE FLAG /*Integer NOT NULL*/,
		// DELETED FLAG /*Integer NOT NULL*/,
		// CHNG FINT64 /*Numeric(18,0) */,
		// BDOCODE FINT /*Integer */,
		// OWNERBDO FINT /*Integer */,
		// INSCHNG FINT64 /*Numeric(18,0) */,
		// CHNGFM FINT64 /*Numeric(18,0) */,
		// ISFROMFM FRSMALLINT /*Smallint NOT NULL*/ DEFAULT 0,
	}

	@Override
	protected ICondition buildCondition(final List<CompositeKey> keyList) {
		final ICondition condition = super.buildCondition(keyList);

		return new AndCondition(condition, new EqualCondition("DELETED", "0"));
	}

	@Override
	public Row get(final Integer id, final String barcode) {
		return super.get(id, barcode);
	}
}
