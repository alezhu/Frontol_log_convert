package ru.alezhu.frontol_log_convert.frontol.tables;

import ru.alezhu.frontol_log_convert.frontol.db.BaseDb;
import ru.alezhu.frontol_log_convert.frontol.tables.base.BaseRow;
import ru.alezhu.frontol_log_convert.frontol.tables.base.BaseTable3Key;
import ru.alezhu.frontol_log_convert.frontol.tables.base.KeyDescriptionInteger;

import java.util.List;

public class FreqChangeConditionsTable extends BaseTable3Key<Integer, Integer, Integer, FreqChangeConditionsTable.Row> {
	/**
	 *
	 */
	private static final String TABLE_NAME = "FREQCHANGECONDITIONS";

	public FreqChangeConditionsTable(final BaseDb db) {
		super(db, TABLE_NAME, new KeyDescriptionInteger<>("ID", (row) -> row.ID, true),
				new KeyDescriptionInteger<>("ACTIVITIESID", (row) -> row.ACTIVITIESID, false),
				new KeyDescriptionInteger<>("DCE_MODULE_ID", (row) -> row.DCE_MODULE_ID, false), Row::new);
		// TODO Auto-generated constructor stub
	}

	public static class Row extends BaseRow {
		// ID FID /*Integer NOT NULL*/,
		public int ID;
		// ACTIVITIESID FREF /*Integer */,
		public int ACTIVITIESID;
		// CONDITIONTYPE FINT /*Integer */,
		public int CONDITIONTYPE;
		// TIMEBEG FTIME /*Time */,
		// TIMEEND FTIME /*Time */,
		// DATEBEG FDATE /*Date */,
		// DATEEND FDATE /*Date */,
		// INMONDAY FINT /*Integer */,
		// INTUESDAY FINT /*Integer */,
		// INWEDNESDAY FINT /*Integer */,
		// INTHURSDAY FINT /*Integer */,
		// INFRIDAY FINT /*Integer */,
		// INSATURDAY FINT /*Integer */,
		// INSUNDAY FINT /*Integer */,
		// SUMMREC FNUM /*Double precision */,
		public Double SUMMREC;
		// COMPSUMMREC FCOMPARISON /*Integer */,
		public Integer COMPSUMMREC;
		// POSCOUNTREC FNUM /*Double precision */,
		// COMPPOSCOUNTREC FNUM /*Double precision */,
		// FUNCNAME FVARCHAR255 /*Varchar(255) */,
		// COUNTERTYPEID FREF /*Integer */,
		// COMPCOUNTER FCOMPARISON /*Integer */,
		// VALUECOUNTER FNUM /*Double precision */,
		// DCE_MODULE_ID FID /*Integer NOT NULL*/,
		public int DCE_MODULE_ID;
		// WAREID FREF /*Integer */,
		// SUMMWARE FNUM /*Double precision */,
		// QUANTITYWARE FNUM /*Double precision */,
		// COMPSUMMWARE FCOMPARISON /*Integer */,
		// COMPQUANTITYWARE FCOMPARISON /*Integer */,
		// WARECLASSIFIERID FREF /*Integer */,
		// SUMMWARECLASSIFIER FNUM /*Double precision */,
		// QUANTITYWARECLASSIFIER FNUM /*Double precision */,
		// COMPSUMMWARECLASSIFIER FCOMPARISON /*Integer */,
		// COMPQUANTITYWARECLASSIFIER FCOMPARISON /*Integer */,
		// COUNTERGRPCARDID FREF /*Integer */,
		// COUNTERCODE FINT /*Integer */,
		// COUNTERSPECIDENTPARAM FINT /*Integer */, /*Уточняющий параметр
		// идентификации счетчика. Может принимать значения:
		// NULL/0 - данные модуля не заданы; 1 - все (краткий режим
		// идентификации); 2 - счетчик должен быть глобальным; 3 - привязанным к
		// клиенту;
		// 4 - привязанным к клиентской карте некоторого вида, 5 - с
		// определенным кодом.*/
		// COUNTREC FNUM /*Double precision */,
		// COMPCOUNTREC FCOMPARISON /*Integer */,
		// REGDAYBEG FINT /*Integer */,
		// REGDAYEND FINT /*Integer */,
		// REGMONTHBEG FINT /*Integer */,
		// REGMONTHEND FINT /*Integer */,
		// REGYEARBEG FINT /*Integer */,
		// REGYEAREND FINT /*Integer */,
		// REGDATETYPE FINT /*Integer */,
		// REGDATEEVERYYEAR FINT /*Integer */,
		// MEMDAYBEG FINT /*Integer */,
		// MEMDAYEND FINT /*Integer */,
		// MEMMONTHBEG FINT /*Integer */,
		// MEMMONTHEND FINT /*Integer */,
		// MEMYEARBEG FINT /*Integer */,
		// MEMYEAREND FINT /*Integer */,
		// MEMDATETYPE FINT /*Integer */,
		// MEMDATEEVERYYEAR FINT /*Integer */,
		// BIRTHDAYBEG FINT /*Integer */,
		// BIRTHDAYEND FINT /*Integer */,
		// BIRTHMONTHBEG FINT /*Integer */,
		// BIRTHMONTHEND FINT /*Integer */,
		// BIRTHYEARBEG FINT /*Integer */,
		// BIRTHYEAREND FINT /*Integer */,
		// BIRTHDATETYPE FINT /*Integer */,
		// BIRTHDATEEVERYYEAR FINT /*Integer */,
	}

	@Override
	public List<Row> getList(final Integer id, final Integer activityId, final Integer dce_moduleId) {
		return super.getList(id, activityId, dce_moduleId);
	}

	@Override
	public Row get(final Integer id, final Integer activityId, final Integer dce_moduleId) {
		return super.get(id, activityId, dce_moduleId);
	}
}
