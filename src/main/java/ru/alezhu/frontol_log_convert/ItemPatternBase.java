package ru.alezhu.frontol_log_convert;

import ru.alezhu.frontol_log_convert.frontol.db.MainDb;
import ru.alezhu.frontol_log_convert.frontol.tables.ActivitiesTable;
import ru.alezhu.frontol_log_convert.frontol.tables.DCEModuleTable;
import ru.alezhu.frontol_log_convert.frontol.tables.MarketActTable;
import ru.alezhu.frontol_log_convert.frontol.tables.WareDiscountTable;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

abstract class ItemPatternBase extends ItemPatternAbstract {
	private Map<Integer, DiscountInfo> MatIdDiscount;
	private Map<Integer, DiscountInfo> DceDiscount;
	private Map<Integer, DiscountInfo> ActivityDiscount;

	@SuppressWarnings("WeakerAccess")
	private static class DiscountInfo {
		public int Code;
		public int Type;
		public double Discount;
		public int ActNr;
		public Timestamp begin;
		public Timestamp end;

		final static public DiscountInfo Empty = new DiscountInfo();
	}

	void findDiscount(final LogParserContext context, final Item item) {

		DiscountInfo discountInfo = this.getMatIdDiscount().get(item.MatId);
		if (discountInfo == null) {
			discountInfo = DiscountInfo.Empty;
			final int wareId = context.database.getWareId(item.MatId, item.Matnr);
			final MainDb mainDb = context.database.MainDb.get();
			final WareDiscountTable.Row wareDiscount = context.database.getWareDiscount(0, wareId);
			if (wareDiscount != null && wareDiscount.DCE_MODULE_ID != 0) {
				discountInfo = this.getDceDiscount().get(wareDiscount.DCE_MODULE_ID);
				if (discountInfo == null) {
					discountInfo = DiscountInfo.Empty;
					final DCEModuleTable.Row dce_moduleRow = mainDb.DCEModuleTable.get().get(wareDiscount.DCE_MODULE_ID,
							null);
					if (dce_moduleRow != null && dce_moduleRow.STOCK_ID != 0) {
						discountInfo = this.getActivityDiscount().get(dce_moduleRow.STOCK_ID);
						if (discountInfo == null) {
							discountInfo = DiscountInfo.Empty;
							final ActivitiesTable.Row activitiesRow = mainDb.ActivitiesTable.get()
									.get(dce_moduleRow.STOCK_ID);
							if (activitiesRow != null && activitiesRow.MARKETACTID != 0) {
								final MarketActTable.Row marketActRow = mainDb.MarketActTable.get()
										.get(activitiesRow.MARKETACTID);
								if (marketActRow != null) {
									discountInfo = new DiscountInfo();
									discountInfo.Code = marketActRow.CODE;
									discountInfo.Type = marketActRow.TYPE;
									discountInfo.Discount = Double.parseDouble(wareDiscount.DISCOUNT);
									discountInfo.ActNr = activitiesRow.CODE;
									discountInfo.begin = marketActRow.DATETIMEBEG;
									discountInfo.end = marketActRow.DATETIMEEND;
								}
							}
							this.getActivityDiscount().put(dce_moduleRow.STOCK_ID, discountInfo);
						}
					}
					this.getDceDiscount().put(wareDiscount.DCE_MODULE_ID, discountInfo);
				}
			}
			this.getMatIdDiscount().put(item.MatId, discountInfo);
		}
		if (discountInfo != DiscountInfo.Empty && discountInfo != null) {
			final Timestamp now = Timestamp.valueOf(item.DateTime);
			final Discount discount = new Discount(item);
			discount.ActNr = discountInfo.ActNr;
			discount.Code = discountInfo.Code;
			discount.Type = discountInfo.Type;
			discount.setDiscount(discountInfo.Discount);
			discount.DateTime = item.DateTime;
			if (discountInfo.begin.after(now) || discountInfo.end.before(now)) {
				discount.setNotInDates(true);
			}
			item.addDiscount(discount);

		}

	}

	List<Item> findItems(final LogParserContext context, final int matId) {
		final List<Item> result = new LinkedList<>();
		final List<Item> items = context.lastDocument.getItems();
		for (int index = items.size() - 1; index >= 0; index--) {
			final Item item = items.get(index);
			if (item.MatId == matId) {
				result.add(item);
			}
		}
		return result;
	}

	Item findItem(final LogParserContext context, final int matId) {
		final List<Item> items = this.findItems(context, matId);
		if (items.size() > 0) {
			return items.get(0);
		}
		return null;
	}

	private Map<Integer, DiscountInfo> getMatIdDiscount() {
		if (this.MatIdDiscount == null) {
			this.MatIdDiscount = new HashMap<>();
		}
		return this.MatIdDiscount;
	}

	private Map<Integer, DiscountInfo> getDceDiscount() {
		if (this.DceDiscount == null) {
			this.DceDiscount = new HashMap<>();
		}
		return this.DceDiscount;
	}

	private Map<Integer, DiscountInfo> getActivityDiscount() {
		if (this.ActivityDiscount == null) {
			this.ActivityDiscount = new HashMap<>();
		}
		return this.ActivityDiscount;
	}

}
