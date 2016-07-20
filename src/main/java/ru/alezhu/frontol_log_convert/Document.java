package ru.alezhu.frontol_log_convert;

import ru.alezhu.frontol_log_convert.frontol.Database;
import ru.alezhu.frontol_log_convert.frontol.Lazy;
import ru.alezhu.frontol_log_convert.frontol.db.MainDb;
import ru.alezhu.frontol_log_convert.frontol.tables.MaterialTable;
import ru.alezhu.frontol_log_convert.frontol.tables.MaterialTable.Row;

import java.time.LocalDateTime;
import java.util.*;
import java.util.Map.Entry;

public class Document {
	public int DocId;
	public int WorkstationID;
	public int Smena;
	public int Kassir;
	public LocalDateTime StartDateTime;
	public LocalDateTime EndDateTime;
	private double Count;
	public double RoundSumm;
	public double PayedSumm;
	private double NativeSumm;
	public int DocType; // 0 - sell, 1 - return ??
	private List<Item> Items;
	private List<Payment> Payments;
	public boolean Closed;
	public DicsountCard Card;
	private String FullDocId;
	public boolean FullCancelled;
	public boolean SmenaClosing;
	public List<Item> MayBeStorned;
	public Lazy<List<Payment>> StornedPayments = new Lazy<>(LinkedList::new);

	public List<Item> getItems() {
		if (this.Items == null) {
			this.Items = new LinkedList<>();
		}
		return this.Items;
	}

	public List<Payment> getPayments() {
		if (this.Payments == null) {
			this.Payments = new LinkedList<>();
		}
		return this.Payments;
	}

	public String getFullDocId() {
		if (this.FullDocId == null) {
			this.FullDocId = String.join("/", String.valueOf(this.WorkstationID), String.valueOf(this.Smena),
					String.valueOf(this.DocId));
		}
		return this.FullDocId;
	}

	public double getNativeSumm() {
		return this.getNativeSumm(true);
	}

	public double getNativeSumm(final boolean recalc) {
		if (recalc || this.NativeSumm == 0) {
			this.calcNativeSummAndCount();
		}
		return this.NativeSumm;

	}

	private void calcNativeSummAndCount() {
		this.NativeSumm = 0;
		this.Count = 0;
		if (this.Items != null) {
			this.Items.forEach((item) -> {
				if (!item.Storned) {
					if (this.MayBeStorned == null || !this.MayBeStorned.contains(item)) {
						this.NativeSumm += item.NativeSumm;
						this.Count += item.Count;
					}
				}
			});
		}
	}

	public double getCount() {
		return this.getCount(true);
	}

	public double getCount(final boolean recalc) {
		if (recalc || this.Count == 0) {
			this.calcNativeSummAndCount();
		}
		return this.Count;
	}

	public double getPayedSumm() {
		return this.getPayedSumm(true);
	}

	public double getPayedSumm(final boolean recalc) {
		if (recalc || this.PayedSumm == 0) {
			this.PayedSumm = 0;
			if (this.Payments != null) {
				for (final Payment payment : this.Payments) {
					final double summ = payment.ToPay + payment.Balance - payment.Refund;
					if (this.PayedSumm == 0) {
						this.PayedSumm = summ;
					} else if (Math.abs(this.PayedSumm - summ) > 0.01) {
						System.err.println("Несовпадает суммарная сумма платежа по документу: " + this.DocId);
					}
				}
			}
		}
		return this.PayedSumm;
	}

	public void addDiscountFromDiscountCard(final Database database) {
		if (this.Items == null || this.Card == null || this.Card.Value == 0) {
			return;
		}
		// final double percent = 1 - this.Card.Value / 100.0;
		for (final Item item : this.Items) {
			if (!item.Storned) {
				final Discount discount = new Discount(item);
				discount.ActNr = this.Card.MarketAction;
				discount.Code = this.Card.Code;
				discount.Type = this.Card.Type;
				discount.DateTime = this.Card.Datetime;
				discount.Card = this.Card;
				if (this.Card.MaterialGroupDiscount == null) {
					discount.setDiscount(this.Card.Value);
				} else {
					// Надо искать скидку в группе товаров данного товара
					final MainDb mainDb = database.MainDb.get();
					final MaterialTable materialTable = mainDb.MaterialTable.get();

					int wareId = database.getWareId(item.MatId);
					while (wareId > 0) {
						final Double percent = this.Card.MaterialGroupDiscount.get(wareId);
						if (percent != null) {
							discount.setDiscount(Math.round(percent));
							break;
						}
						final Row row = materialTable.get(wareId, null, null);
						wareId = row.PARENTID;
					}

				}
				item.addDiscount(discount);
			}
		}
	}

	public boolean checkAllDiscountApply(final boolean internal) {
		if (this.Items == null || this.FullCancelled) {
			return true;
		}

		final double payedSumm = this.getPayedSumm();
		if (payedSumm == 0) {
			if (!internal) {
				System.err.println("По документу " + this.DocId + " оплата не зафиксирована");
			}
			return true;
		}
		double payedItemsSumm = 0;
		for (final Item item : this.Items) {
			if (!item.Storned) {
				final double itemPayed = item.getTotalSumm(true);
				payedItemsSumm += itemPayed;
			}
		}
		if (Math.abs(payedItemsSumm - payedSumm) < 0.10) {
			// Все нормально со скидками
			return true;
		}
		// А вот если сюда попали значит что-то пошло не так. Скорее всего
		// применились скидки, которые по датам не должны были.
		payedItemsSumm = 0;
		// Собираем все не входящие в даты скидки
		final List<Map.Entry<Item, Discount>> all = new LinkedList<>();
		final List<Map.Entry<Item, Discount>> solve = new LinkedList<>();
		for (final Item item : this.Items) {
			if (!item.Storned) {
				if (item.hasDiscount()) {
					boolean found = false;
					final List<Discount> discounList = item.getDiscounList();
					for (final Discount discount : discounList) {
						if (discount.isNotInDates()) {
							found = true;
							break;
						}
					}
					if (found) {
						//noinspection Convert2streamapi
						for (final Discount discount : discounList) {
							all.add(new AbstractMap.SimpleEntry<>(item, discount));
						}
						payedItemsSumm += item.NativeSumm;
					} else {
						final double itemPayed = item.getTotalSumm(false);
						payedItemsSumm += itemPayed;
					}
				} else {
					payedItemsSumm += item.NativeSumm;
				}
			}
		}
		if (all.size() > 0) {
			// final double targetDiscount = payedItemsSumm - payedSumm;
			if (this.findSolve(all, solve, -1, payedSumm, payedItemsSumm)) {
				// Нашли решение со скидками
				for (final Entry<Item, Discount> entry : solve) {
					entry.getValue().setNotInDates(false);
					entry.getKey().getTotalSumm(true);
				}
				return true;
			} else if (!internal) {
				System.err.println("Решение по документу " + this.DocId + " не найдено");
			}
		}
		return false;

	}

	private boolean findSolve(final List<Entry<Item, Discount>> all, final List<Entry<Item, Discount>> solve,
							  final int indexStart, final double targerSumm, final double currSumm) {
		if (indexStart >= all.size()) {
			return false;
		}
		int index = indexStart + 1;
		final ListIterator<Entry<Item, Discount>> iterator = all.listIterator(index);
		while (iterator.hasNext()) {
			final Entry<Item, Discount> entry = iterator.next();
			final Discount discount = entry.getValue();
			final boolean save = discount.isNotInDates();
			discount.setNotInDates(false);
			final double discountSumm = discount.getSumm();
			final double summ = currSumm - discountSumm;
			if (Math.abs(targerSumm - summ) <= 0.10) {
				// found
				solve.add(entry);
				return true;
			}
			if (this.findSolve(all, solve, index, targerSumm, summ)) {
				solve.add(entry);
				return true;
			}
			index++;
			discount.setNotInDates(save);
		}

		return false;
	}


	public void close(final Database database) {

		this.Closed = true;

		if (this.Card != null && this.Card.Value != null) {
			// Если скидка есть - добавляем ее в позиции
			this.addDiscountFromDiscountCard(database);
		}

		if (!this.SmenaClosing) {
			this.solveMayBeStorned();
			this.checkAllDiscountApply(false);
		}

	}

	private void solveMayBeStorned() {
		if (this.MayBeStorned == null || this.MayBeStorned.size() == 0) {
			return;
		}

		final Set<Item> mbs = new HashSet<>(this.MayBeStorned);
		final ArrayList<Item> all = new ArrayList<>(mbs);
		all.sort((o1, o2) -> -1 * Integer.compare(o1.Id, o2.Id));
		final List<Item> solve = new LinkedList<>();
		if (this.findWhoIsStorned(all, solve, -1)) {
			this.MayBeStorned = null;
		}
	}

	private boolean findWhoIsStorned(final List<Item> all, final List<Item> solve, final int indexStart) {
		if (indexStart >= all.size()) {
			return false;
		}
		int index = indexStart + 1;
		final ListIterator<Item> iterator = all.listIterator(index);
		while (iterator.hasNext()) {
			final Item item = iterator.next();
			item.Storned = true;
			if (this.checkAllDiscountApply(true)) {
				// Итоговая сумма со всеми скидками совпала - нашли решение
				solve.add(item);
				return true;

			}
			if (this.findWhoIsStorned(all, solve, index)) {
				solve.add(item);
				return true;
			}
			item.Storned = false;
			index++;
		}

		return false;
	}

	public double getPayedSummByItems() {
		double result = 0.0;
		if (this.Items != null) {
			for (final Item item : this.Items) {
				if (!item.Storned) {
					result += item.getTotalSumm();
				}
			}
		}
		return result;
	}

}
