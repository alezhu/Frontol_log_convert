package ru.alezhu.frontol_log_convert;

import java.time.LocalDateTime;
import java.util.Map;

class DicsountCard {
	String Barcode;
	int Code;
	Integer Value = null;
	int MarketAction;
	int Type;
	Map<Integer, Double> MaterialGroupDiscount;
	LocalDateTime Datetime;
}
