package ru.alezhu.frontol_log_convert.frontol;

import ru.alezhu.frontol_log_convert.frontol.tables.base.BaseRow;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class LogRow extends BaseRow {
	public int ID;
	public Timestamp DateTime;
	public int UserCode;
	public int State;
	public int FuncId;
	public String Action;
	public String Categ;
	public int PosNumber;
	public int DocNumber;

	static LogRow from(final ResultSet rs) {
		final LogRow result = new LogRow();
		try {
			result.ID = rs.getInt("ID");
			result.DateTime = rs.getTimestamp("DATETIME");
			result.UserCode = rs.getInt("USERCODE");
			result.State = rs.getInt("STATE");
			result.FuncId = rs.getInt("FUNCID");
			result.Action = rs.getString("ACTION");
			result.Categ = rs.getString("CATEG");
			result.PosNumber = rs.getInt("POSNUMBER");
			final String str = rs.getString("DOCNUMBER");
			if (str != null && !str.isEmpty()) {
				result.DocNumber = Integer.valueOf(str);
			}
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
}
