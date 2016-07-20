package ru.alezhu.frontol_log_convert.frontol.tables;

import ru.alezhu.frontol_log_convert.frontol.db.BaseDb;
import ru.alezhu.frontol_log_convert.frontol.tables.base.BaseRow;
import ru.alezhu.frontol_log_convert.frontol.tables.base.BaseTable4Key;
import ru.alezhu.frontol_log_convert.frontol.tables.base.KeyDescriptionInteger;

import java.util.List;

public class RareChangeConditionsTable
		extends BaseTable4Key<Integer, Integer, Integer, Integer, RareChangeConditionsTable.Row> {
	/**
	 *
	 */
	private static final String TABLE_NAME = "RARECHANGECONDITIONS";

	public RareChangeConditionsTable(final BaseDb db) {
		super(db, TABLE_NAME, new KeyDescriptionInteger<>("ID", (row) -> row.ID, true),
				new KeyDescriptionInteger<>("ACTIVITIESID", (row) -> row.ACTIVITIESID, false),
				new KeyDescriptionInteger<>("DCE_MODULE_ID", (row) -> row.DCE_MODULE_ID, false),
				new KeyDescriptionInteger<>("GRPCCARDID", (row) -> row.GRPCCARDID, false), Row::new);
		// TODO Auto-generated constructor stub
	}

	public static class Row extends BaseRow {
		// ID FID /*Integer NOT NULL*/,
		public int ID;
		// ACTIVITIESID FREF /*Integer */,
		public int ACTIVITIESID;
		// CONDITIONTYPE FINT /*Integer */,
		// CLIENTID FREF /*Integer */,
		// CLIENTCLASSIFIERID FREF /*Integer */,
		// SEX FINT /*Integer */,
		// CCARDCLASSIFIERID FREF /*Integer */,
		// HALLID FREF /*Integer */,
		// HALLPLACEID FREF /*Integer */,
		// ENTERPRISEID FREF /*Integer */,
		// ENTERPRISECLASSIFIERID FREF /*Integer */,
		// PAYMENTID FREF /*Integer */,
		// DCE_MODULE_ID FID /*Integer NOT NULL*/,
		public int DCE_MODULE_ID;
		// GRPCCARDID FINT /*Integer */,
		public int GRPCCARDID;
	}

	@Override
	public List<Row> getList(final Integer id, final Integer activityId, final Integer dce_moduleId,
			final Integer grpCCardId) {
		return super.getList(id, activityId, dce_moduleId, grpCCardId);
	}

	@Override
	public Row get(final Integer id, final Integer activityId, final Integer dce_moduleId, final Integer grpCCardId) {
		return super.get(id, activityId, dce_moduleId, grpCCardId);
	}

}
