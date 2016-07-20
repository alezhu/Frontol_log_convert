package ru.alezhu.frontol_log_convert.frontol.db;

import ru.alezhu.frontol_log_convert.frontol.Lazy;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.LinkedList;

public class LogDb extends BaseDb {

	public LogDb(final String path, final String server) {
		super(path, server);
	}

	private final Lazy<Connection> LogConnection = new Lazy<>(this::getConnection);
	private final Lazy<Statement> LogStatement = new Lazy<>(this::getLogStatement);

	public ResultSet getResultSet(final LocalDateTime from, final LocalDateTime to) {
		ResultSet resultSet = null;
		try {
			final StringBuilder builder = new StringBuilder();
			builder.append("SELECT * FROM LOG");
			final LinkedList<String> list = new LinkedList<>();
			if (from != null) {
				list.add("DATETIME >= '" + Timestamp.valueOf(from).toString() + "'");
			}
			if (to != null) {
				list.add("DATETIME <= '" + Timestamp.valueOf(to).toString() + "'");
			}
			if (list.size() > 0) {
				builder.append(" WHERE ");
				builder.append(String.join(" AND ", list));
			}
			builder.append(" ORDER BY ID");
			resultSet = this.LogStatement.get().executeQuery(builder.toString());
		} catch (final SQLException e) {
			e.printStackTrace();
		}
		return resultSet;

	}

	private Statement getLogStatement() {
		Statement statement = null;
		try {
			statement = this.LogConnection.get().createStatement();
		} catch (final SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return statement;
	}

}
