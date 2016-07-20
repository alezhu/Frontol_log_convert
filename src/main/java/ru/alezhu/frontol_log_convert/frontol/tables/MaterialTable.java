package ru.alezhu.frontol_log_convert.frontol.tables;

import ru.alezhu.frontol_log_convert.frontol.db.BaseDb;
import ru.alezhu.frontol_log_convert.frontol.tables.base.BaseRow;
import ru.alezhu.frontol_log_convert.frontol.tables.base.BaseTable3Key;
import ru.alezhu.frontol_log_convert.frontol.tables.base.KeyDescriptionInteger;
import ru.alezhu.frontol_log_convert.frontol.tables.base.KeyDescriptionString;

public class MaterialTable extends BaseTable3Key<Integer, Integer, String, MaterialTable.Row> {

	private static final String TABLE_NAME = "SPRT";

	public MaterialTable(final BaseDb db) {
		super(db, TABLE_NAME, new KeyDescriptionInteger<>("ID", (row) -> row.ID, true),
				new KeyDescriptionInteger<>("CODE", (row) -> row.CODE, false),
				new KeyDescriptionString<Row>("MARK", (row) -> row.MARK, false), Row::new);
	}

	public static class Row extends BaseRow {
		// ID IDENT /*Integer NOT NULL*/,
		public int ID;
		// CODE FINT /*Integer */ NOT NULL,
		public int CODE;
		// MARK FVARCHAR20 /*Varchar(20) */,
		public String MARK;
		// PARENTID FINT /*Integer */ NOT NULL,
		public int PARENTID;
		// NAME FVARCHAR100 /*Varchar(100) */,
		// TEXT FVARCHAR100 /*Varchar(100) */,
		// MINPRICE FNUM15_2 /*Double precision */,
		// MAXDISCOUNT FNUM15_2 /*Double precision */,
		public Double MAXDISCOUNT;
		// ASPECTSCHEMEID FINT /*Integer */,
		// ISWARE FINT /*Integer */,
		public int ISWARE;
		// HIERLEVEL FINT /*Integer */,
		// LIFE FDATETIME /*TIMESTAMP */,
		// FLAGS FINT /*Integer */,
		// SERIES FVARCHAR100 /*Varchar(100) */,
		// CERTIFICAT FVARCHAR100 /*Varchar(100) */,
		// TAXGROUPID FINT /*Integer */,
		// SCALELPDEVID FINT /*Integer */,
		// SCALELPLOADED FLAG /*Integer NOT NULL*/,
		// SCALELPWARECODE FINT /*Integer */,
		// ASPECTTYPE FINT /*Integer */,
		// PICTURE FBLOB1024 /*Blob SUB_TYPE 0 */,
		// DESCRIPTION FBLOBTEXT /*Blob SUB_TYPE 1 */,
		// QUANTITYPREC FNUM15_3 /*Double precision */,
		// PRICELABELID FINT /*Integer */,
		// DELETED FLAG /*Integer NOT NULL*/,
		// PRICELBPRINTED FLAG /*Integer NOT NULL*/,
		// REWARDTYPE FINT /*Integer */,
		// REWARDVAL FNUM15_3 /*Double precision */ NOT NULL,
		// ECRDEPARTMENT FINT /*Integer */,
		// CHNG FINT64 /*Numeric(18,0) */,
		// BDOCODE FINT /*Integer */,
		// OWNERBDO FINT /*Integer */,
		// INSCHNG FINT64 /*Numeric(18,0) */,
		// GTD FVARCHAR100 /*Varchar(100) */,
		// PRINTGROUPCLOSE FINT /*Integer */,
		// PRINTGROUPCOPY FINT /*Integer */,
		// PRINTGROUPPRECHEQUE FINT /*Integer */,
		// PRINTGROUPSTAMP FINT /*Integer */,
		// LPMSGNUM FINT /*Integer */,
		// VISUALSELECT FINT /*Integer */ DEFAULT 0,
		// MINWEIGHT FINT /*Integer */ DEFAULT 0,
		// MAXWEIGHT FINT /*Integer */ DEFAULT 0,
		// DIRECTSCALELPMSG FBLOBTEXT /*Blob SUB_TYPE 1 */,
		// TARIFFSERVICEID FINT /*Integer */,
		// ALCOVOLUME FNUM15_3 /*Double precision */,
		// ALCOTYPECODE FINT /*Integer */,
		// ISALCO FLAG /*Integer NOT NULL*/,
		// WITHEXCISESTAMP FLAG /*Integer NOT NULL*/,
		// ALCOCONTENT FNUM15_3 /*Double precision */,
		// final static public Row Empty = new Row();
	}

	@Override
	public Row get(final Integer wareid, final Integer matid, final String matnr) {
		return super.get(wareid, matid, matnr);
	}

	// @Override
	// protected ICondition buildCondition(final List<CompositeKey> keyList) {
	// final ICondition condition = super.buildCondition(keyList);
	//
	// return new AndCondition(condition, new EqualCondition("ISWARE", "1"));
	// }

}
