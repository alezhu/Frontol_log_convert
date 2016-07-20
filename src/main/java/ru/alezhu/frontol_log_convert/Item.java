package ru.alezhu.frontol_log_convert;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

public class Item {
	public int Id;
	public int MatId;
	public String Matnr;
	public String EAN;
	public double Count;
	public double Price;
	private double FinalPrice;
	public double NativeSumm;
	// private double RoundSumm;
	private double TotalSumm;
	public int TypeKKM;
	public boolean Storned;
	public LocalDateTime DateTime;
	private List<Discount> discountList;
	public Double MaxDiscount;
	private double DiscountSumm;
	private double FinalDiscount;

	public List<Discount> getDiscounList() {
		if (this.discountList == null) {
			this.discountList = new LinkedList<>();
		}
		return this.discountList;
	}

	public double getRoundSumm() {
		return this.NativeSumm;
	}

	public double getDiscountSumm() {
		return this.getDiscountSumm(true);
	}

	public double getDiscountSumm(final boolean recalc) {
		if (recalc || this.DiscountSumm == 0) {
			this.DiscountSumm = 0;
			if (this.discountList != null && !this.Storned) {
				for (final Discount discount : this.discountList) {
					if (!discount.isNotInDates()) {
						this.DiscountSumm += discount.getDiscount();
					}
				}
			}
		}
		return this.DiscountSumm;
	}

	@SuppressWarnings("unused")
	public double getFinalDiscount() {
		return this.getFinalDiscount(true);
	}

	public double getFinalDiscount(final boolean recalc) {
		if (recalc || this.FinalDiscount == 0) {
			this.FinalDiscount = this.getDiscountSumm();
			if (this.MaxDiscount != null) {
				this.FinalDiscount = Math.min(this.FinalDiscount, this.MaxDiscount);
			}
		}
		return this.FinalDiscount;
	}

	public double getTotalSumm(final boolean recalc) {
		if (recalc || this.TotalSumm == 0) {
			this.TotalSumm = 0;
			final double maxDiscount = this.getFinalDiscount(recalc);
			double discountValue = this.NativeSumm * maxDiscount / 100;
			discountValue = Math.round(discountValue * 100) / 100.0;
			this.TotalSumm = this.NativeSumm - discountValue;

		}
		return this.TotalSumm;

	}

	public double getTotalSumm() {
		return this.getTotalSumm(true);
	}

	public double getFinalPrice() {
		return this.getFinalPrice(true);
	}

	public double getFinalPrice(final boolean recalc) {
		if (recalc || this.FinalPrice == 0) {
			// this.FinalPrice = (Math.round(this.getTotalSumm(recalc)) * 100) /
			// 100 / this.Count;
			this.FinalPrice = (Math.round(this.getTotalSumm(recalc) * 100)) / 100.0 / this.Count;
		}
		return this.FinalPrice;
	}

	public boolean hasDiscount() {
		return this.discountList != null && !this.discountList.isEmpty();
	}

	public void addDiscount(final Discount discount) {
		final List<Discount> list = this.getDiscounList();
		list.add(discount);
	}

}
