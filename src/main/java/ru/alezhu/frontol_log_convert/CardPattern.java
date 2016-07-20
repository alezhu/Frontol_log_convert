package ru.alezhu.frontol_log_convert;

import ru.alezhu.frontol_log_convert.frontol.Database;
import ru.alezhu.frontol_log_convert.frontol.db.MainDb;
import ru.alezhu.frontol_log_convert.frontol.tables.*;
import ru.alezhu.frontol_log_convert.frontol.tables.CCardTable.Row;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class CardPattern extends ItemPatternAbstract {
	private Pattern pattern;

	CardPattern() {
		this.pattern = Pattern.compile("^Карта\\s+(?<card>\\d+)");
	}

	@Override
	public boolean process(final LogParserContext context) {
		final Matcher matcher = this.pattern.matcher(context.logRow.Action);
		if (matcher.find()) {
			context.lastDocument.Card = new DicsountCard();
			context.lastDocument.Card.Barcode = matcher.group("card");
			context.lastDocument.Card.Datetime = context.logRow.DateTime.toLocalDateTime();
			// this.findDiscountCardInfo(context, context.lastDocument.Card);
		}
		return false;
	}

	@SuppressWarnings("ConstantConditions")
	static void findDiscountCardInfo(final Database database, final Document document, final DicsountCard card) {
		final MainDb mainDb = database.MainDb.get();
		final Row ccardRow = mainDb.CCardTable.get().get(null, card.Barcode);
		if (ccardRow == null || ccardRow.GRPCCARDID == 0) {
			return;
		}

		final List<RareChangeConditionsTable.Row> rccRows = mainDb.RareChangeConditionsTable.get().getList(null, null,
				null, ccardRow.GRPCCARDID);
		if (rccRows == null || rccRows.size() == 0) {
			return;
		}

		final double nativeSumm = document.getNativeSumm();
		// final double payedItemsSumm = document.getPayedSummByItems(); // Это
		// сколько должны заплатить по позициям с учтом текущих скидок
		final FreqChangeConditionsTable fccTable = mainDb.FreqChangeConditionsTable.get();
		boolean validCondition;
		final Set<Integer> validAction = new HashSet<>();

		for (final RareChangeConditionsTable.Row rccRow : rccRows) {
			if (rccRow.ACTIVITIESID != 0) {
				final List<FreqChangeConditionsTable.Row> fccRows = fccTable.getList(null, rccRow.ACTIVITIESID, null);
				if (fccRows != null && fccRows.size() > 0) {
					validCondition = true;
					for (final FreqChangeConditionsTable.Row fccRow : fccRows) {
						switch (fccRow.COMPSUMMREC) {
							case 1:
								validCondition = validCondition && nativeSumm >= fccRow.SUMMREC;
								break;
							case 2:
								validCondition = validCondition && nativeSumm > fccRow.SUMMREC;
								break;
							case 3:
								validCondition = validCondition && nativeSumm <= fccRow.SUMMREC;
								break;
							case 4:
								validCondition = validCondition && nativeSumm < fccRow.SUMMREC;
								break;
							case 5:
								validCondition = validCondition && nativeSumm == fccRow.SUMMREC;
								break;

							default:
								break;
						}
						if (!validCondition) {
							break;
						}
					}
					if (validCondition) {
						validAction.add(rccRow.ACTIVITIESID);
					}
				}

			}
		}
		if (validAction.size() == 1) {
			final Integer activityId = validAction.iterator().next();
			final ActivitiesTable.Row activityRow = mainDb.ActivitiesTable.get().get(activityId);
			if (activityRow != null) {

				final MarketActTable.Row marketActRow = mainDb.MarketActTable.get().get(activityRow.MARKETACTID);
				if (marketActRow != null) {
					if ((marketActRow.DATETIMEBEG == null
							|| !marketActRow.DATETIMEBEG.toLocalDateTime().isAfter(document.StartDateTime))
							&& (marketActRow.DATETIMEEND == null
							|| !marketActRow.DATETIMEEND.toLocalDateTime().isBefore(document.StartDateTime))) {
						card.MarketAction = activityRow.CODE;
						card.Code = marketActRow.CODE;
						card.Type = marketActRow.TYPE;

						final double payedSummByItems = document.getPayedSummByItems();
						// Для начала считаем так но это скорее всего не верно
						// будет
						double percent = 100 * (1 - nativeSumm / payedSummByItems);

						// По Activity ищем DCE- модули
						final List<DCEModuleTable.Row> dceList = mainDb.DCEModuleTable.get().getList(null, activityId);
						for (final DCEModuleTable.Row row : dceList) {
							if (row.TYPE_CODE == 22) {
								// Берем тот что с типом 22
								// и ищем к нему товары (это реально не товары,
								// а группы)
								final List<WareDiscountTable.Row> wareList = mainDb.WareDiscountTable.get()
										.getList(null, null, row.ID);
								if (wareList != null) {
									// если что-то нашли
									switch (wareList.size()) {
										case 0:
											// ??? - ничего ен нашли странно
											break;
										case 1:
											// если нашли один - все однозначно -
											// его и берем для значения скидки в %
											percent = Double.valueOf(wareList.get(0).DISCOUNT);
											break;
										default:
											// Если нашли много - собираем
											// уникальные % скидки
											final Map<Double, List<WareDiscountTable.Row>> discountMap = new HashMap<>();
											Double discount = 0.0;
											for (final WareDiscountTable.Row rowWare : wareList) {
												discount = Double.valueOf(rowWare.DISCOUNT);
												List<WareDiscountTable.Row> list = discountMap.get(discount);
												if (list == null) {
													list = new LinkedList<>();
													discountMap.put(discount, list);
												}
												list.add(rowWare);
											}

											if (discountMap.keySet().size() == 1) {
												// Если все % скидок одинаковые -
												// отлично берем % из единственнйо
												// записи
												percent = discount;
											} else {
												// Если % разные - нужно будет
												// искать % по иерахии товаров от
												// товара до группы, на которую
												// скидка
												// Сохраняем все иерархии в карте
												card.MaterialGroupDiscount = new HashMap<>();
												for (final WareDiscountTable.Row rowWare : wareList) {
													discount = Double.valueOf(rowWare.DISCOUNT);
													card.MaterialGroupDiscount.put(rowWare.WAREID, discount);
												}
											}
											break;
									}
								}
								break;
							}
						}
						if (card.MaterialGroupDiscount == null) {
							card.Value = (int) Math.round(percent);
						}
					}
				}
			}
		} else {
			System.err.println("Ошибка поиска акций по скидочной карте: " + card.Barcode);
		}

	}

}
