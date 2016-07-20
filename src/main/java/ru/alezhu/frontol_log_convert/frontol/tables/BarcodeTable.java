package ru.alezhu.frontol_log_convert.frontol.tables;

import ru.alezhu.frontol_log_convert.frontol.db.BaseDb;
import ru.alezhu.frontol_log_convert.frontol.tables.base.*;
import ru.alezhu.frontol_log_convert.frontol.tables.condtions.AndCondition;
import ru.alezhu.frontol_log_convert.frontol.tables.condtions.EqualCondition;
import ru.alezhu.frontol_log_convert.frontol.tables.condtions.ICondition;

import java.util.List;

public class BarcodeTable extends BaseTable2Key<Integer, String, BarcodeTable.Row> {

	private static final String TABLE_NAME = "BARCODE";

	public BarcodeTable(final BaseDb db) {
		super(db, TABLE_NAME, new KeyDescriptionInteger<>("WAREID", (row) -> row.WAREID, false),
				new KeyDescriptionString<>("BARCODE", (row) -> row.BARCODE, false), Row::new);
	}

	public static class Row extends BaseRow {
		// ID FID /*Integer NOT NULL*/,
		public int ID;
		// WAREID FINT /*Integer */ NOT NULL,
		public int WAREID;
		// BARCODE FVARCHAR40 /*Varchar(40) */ NOT NULL,
		public String BARCODE;
		// ASPECTVALUE1ID FINT /*Integer */ NOT NULL,
		// ASPECTVALUE2ID FINT /*Integer */ NOT NULL,
		// ASPECTVALUE3ID FINT /*Integer */ NOT NULL,
		// ASPECTVALUE4ID FINT /*Integer */ NOT NULL,
		// ASPECTVALUE5ID FINT /*Integer */ NOT NULL,
		// FACTOR FNUM15_3 /*Double precision */,
		// DELETED FLAG /*Integer NOT NULL*/,
		// CHNG FINT64 /*Numeric(18,0) */,
		// BDOCODE FINT /*Integer */,
		// OWNERBDO FINT /*Integer */,
		// INSCHNG FINT64 /*Numeric(18,0) */,

	}

	@Override
	protected ICondition buildCondition(final List<CompositeKey> keyList) {
		final ICondition condition = super.buildCondition(keyList);

		return new AndCondition(condition, new EqualCondition("DELETED", "0"));
	}

	@Override
	public Row get(final Integer wareId, final String barcode) {
		return super.get(wareId, barcode);
	}

}
