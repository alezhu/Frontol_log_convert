package ru.alezhu.frontol_log_convert.frontol.db;

import org.firebirdsql.pool.FBWrappingDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class BaseDb {
	protected String Path;
	protected FBWrappingDataSource DataSource;
	private final String Server;

	public BaseDb(final String path, final String server) {
		this.Path = path;
		this.Server = (server != null && !server.isEmpty()) ? server : "localhost/3050";
		this.DataSource = new FBWrappingDataSource();
        this.DataSource.setEncoding("WIN1251");
        this.DataSource.setDatabase(this.Server + ":" + path);
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		if (this.DataSource != null) {
			synchronized (this) {
				if (this.DataSource != null) {
					this.DataSource.shutdown();
					this.DataSource = null;
				}
			}
		}
	}

	private Connection connection = null;

	public Connection getConnection() {
		if (this.connection == null) {
			try {
				this.connection = this.DataSource.getConnection("SYSDBA", "masterkey");
				this.connection.setAutoCommit(false);
			} catch (final SQLException e) {
				e.printStackTrace();
			}
		}

		return this.connection;
	}

}
