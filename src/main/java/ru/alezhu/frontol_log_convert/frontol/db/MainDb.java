package ru.alezhu.frontol_log_convert.frontol.db;

import ru.alezhu.frontol_log_convert.frontol.EANConverter;
import ru.alezhu.frontol_log_convert.frontol.Lazy;
import ru.alezhu.frontol_log_convert.frontol.tables.*;

public class
MainDb extends BaseDb {

	public MainDb(final String path, final String server) {
		super(path, server);
	}

	public final Lazy<EANConverter> EANConverter = new Lazy<>(() -> new EANConverter(this));
	public final Lazy<BarcodeTable> BarcodeTable = new Lazy<>(() -> new BarcodeTable(this));
	public final Lazy<MaterialTable> MaterialTable = new Lazy<>(() -> new MaterialTable(this));
	public final Lazy<WareDiscountTable> WareDiscountTable = new Lazy<>(() -> new WareDiscountTable(this));
	public final Lazy<DCEModuleTable> DCEModuleTable = new Lazy<>(() -> new DCEModuleTable(this));
	public final Lazy<MarketActTable> MarketActTable = new Lazy<>(() -> new MarketActTable(this));
	public final Lazy<ActivitiesTable> ActivitiesTable = new Lazy<>(() -> new ActivitiesTable(this));
	public final Lazy<CCardTable> CCardTable = new Lazy<>(() -> new CCardTable(this));
	public final Lazy<RareChangeConditionsTable> RareChangeConditionsTable = new Lazy<>(
			() -> new RareChangeConditionsTable(this));
	public final Lazy<FreqChangeConditionsTable> FreqChangeConditionsTable = new Lazy<>(
			() -> new FreqChangeConditionsTable(this));
}
