package ru.alezhu.frontol_log_convert;

import java.time.LocalDateTime;

public class Discount {

	Discount(final Item item) {
		this.Item = item;
	}

	public int Code;
	public int Type;
	private double Discount;
	private double Summ;
	private final Item Item;
	public int ActNr;
	public LocalDateTime DateTime;
	public DicsountCard Card;
	private boolean NotInDates;

	public double getSumm(final boolean recalc) {
		if (recalc || this.Summ == 0) {
			final double finalDiscount = this.Item.getFinalDiscount(recalc);
			final double summDiscount = this.Item.getDiscountSumm(false);
			final double discount = finalDiscount / summDiscount * this.Discount;
			// discount = Math.round(discount * 1000);
			// this.Summ = Math.round(this.Item.NativeSumm * discount) / 100000.0;
			this.Summ = this.Item.NativeSumm * discount / 100.0;
		}
		return this.Summ;
	}

	public double getSumm() {
		return this.getSumm(true);
	}

	public double getDiscount() {
		return this.Discount;
	}

	public void setDiscount(final double discount) {
		this.Discount = discount;
	}

	public boolean isNotInDates() {
		return this.NotInDates;
	}

	public void setNotInDates(final boolean notInDates) {
		this.NotInDates = notInDates;
	}

}
