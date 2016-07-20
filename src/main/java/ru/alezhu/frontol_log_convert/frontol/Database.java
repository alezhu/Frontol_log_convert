package ru.alezhu.frontol_log_convert.frontol;

import ru.alezhu.frontol_log_convert.frontol.db.LogDb;
import ru.alezhu.frontol_log_convert.frontol.db.MainDb;
import ru.alezhu.frontol_log_convert.frontol.tables.BarcodeTable.Row;
import ru.alezhu.frontol_log_convert.frontol.tables.MaterialTable;
import ru.alezhu.frontol_log_convert.frontol.tables.WareDiscountTable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class Database {
    private String MainDbPath;
    private String LogDbPath;
    public final Lazy<MainDb> MainDb = new Lazy<>(() -> new MainDb(this.MainDbPath, this.Server));
    private final Lazy<LogDb> LogDb = new Lazy<>(() -> new LogDb(this.LogDbPath, this.Server));
    private LocalDateTime to;
    private LocalDateTime from;
    private String Server;

    public Database(final String server, final String mainDbPath, final String logDbPath, final LocalDateTime from,
                    final LocalDateTime to) {
        this.Server = server;
        this.MainDbPath = mainDbPath;
        this.LogDbPath = logDbPath;
        this.from = from;
        this.to = to;
    }

    private Lazy<ResultSet> rs = new Lazy<>(() -> this.LogDb.get().getResultSet(this.from, this.to));

    public LogRow getNextLogRow() {
        boolean result = false;
        try {
            result = this.rs.get().next();
        } catch (final SQLException ignored) {
        }
        if (!result) {
            return null;
        }

        return LogRow.from(this.rs.get());
    }

    public String getRealEAN(final String ean) {
        try {
            return this.MainDb.get().EANConverter.get().convert(ean);
        } catch (final Exception e) {
            e.printStackTrace();
            return ean.trim();
        }
    }

    public String getEAN(final int wareId) {
        final Row row = this.MainDb.get().BarcodeTable.get().get(wareId, null);
        if (row != null) {
            return row.BARCODE;
        }
        return null;
    }

    public int getWareId(final int matId) {
        final MaterialTable.Row row = this.MainDb.get().MaterialTable.get().get(null, matId, null);
        if (row != null) {
            return row.ID;
        }
        return 0;

    }

    public int getWareId(final String EAN) {
        int result = 0;
        try {
            final Row row = this.MainDb.get().BarcodeTable.get().get(0, EAN);
            if (row != null) {
                result = row.WAREID;
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public int getWareId(final int matid, final String matnr) {
        int result = 0;
        try {
            final MaterialTable.Row row = this.MainDb.get().MaterialTable.get().get(null, matid, matnr);
            if (row != null) {
                result = row.ID;
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public WareDiscountTable.Row getWareDiscount(final int id, final int wareId) {
        try {
            return this.MainDb.get().WareDiscountTable.get().get(id, wareId, null);
        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
