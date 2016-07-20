package ru.alezhu.frontol_log_convert;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

public class DocumentHandler implements IDocumentHandler {
	private BufferedWriter writer = null;
	private int curLine;
	private final DateTimeFormatter dateFormatter;
	private final DateTimeFormatter timeFormatter;
	private final String EmptyString;
	private final DecimalFormat decimalFormat;
	private final DecimalFormat decimalFormatDiscount;
	private final DecimalFormat decimalFormatCount;

	public DocumentHandler(final StartConfig config) throws IOException {
		this.writer = new BufferedWriter(new FileWriter(config.LogFilePath));
		this.curLine = (config.StartLine == 0) ? 1 : config.StartLine;
		this.dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
		this.timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
		this.decimalFormat = new DecimalFormat("#.##");
		this.decimalFormatDiscount = new DecimalFormat("#.####");
		// this.decimalFormatDiscount = new DecimalFormat("#.##");
		this.decimalFormatCount = new DecimalFormat("#.###");
		this.EmptyString = "";

		this.writer.write("#");
		this.writer.newLine();
		this.writer.write("1");
		this.writer.newLine();
		this.writer.write("30");
		this.writer.newLine();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		if (this.writer != null) {
			this.writer.close();
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see ru.alezhu.frontol_log_convert.IDocumentHandler#process(ru.alezhu.frontol_log_convert.Document)
	 */
	@Override
	public void process(final Document document) {
		final List<String> data = new LinkedList<>();
		if (!document.SmenaClosing) {

			this.curLine += this.writeStartDocument(this.curLine, document, data);
			document.getItems().forEach((item) -> {
				this.curLine += this.writeItem(this.curLine, document, item, data);
			});
			if (!document.FullCancelled) {
				this.curLine += this.writePayments(this.curLine, document, data);
			}

			this.curLine += this.writeEndDocument(this.curLine, document, data);
		} else {
			this.curLine += this.writeCloseSmena(this.curLine, document, data);
		}
		data.forEach((line) -> {
			try {
				this.writer.write(line);
				this.writer.newLine();
			} catch (final Exception e) {
				e.printStackTrace();
			}
		});
		try {
			this.writer.flush();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	private String eanOut(final String ean) {
		// return ean.replaceFirst("^0+", "");
		return ean;
	}

	private int writeCloseSmena(final int startLine, final Document document, final List<String> data) {
		final List<String> row = new LinkedList<>();
		this.writeRowStart(document, row, startLine, document.StartDateTime, "61");
		row.add(this.EmptyString);
		// CutID
		row.add(String.valueOf(document.Smena));
		// Price
		final String summ = this.decimalFormat.format(document.RoundSumm);
		row.add(summ);
		// Meins
		row.add("0");
		// RoundSumm
		row.add(summ);
		// Type
		row.add(String.valueOf(document.DocType + 1));
		// Smena
		row.add(String.valueOf(document.Smena));
		// FinalPrice
		row.add("0");
		// TotalSumm
		row.add("0");
		// PrtGrp
		row.add("0");
		// Matnr
		row.add(this.EmptyString);
		// EAN
		row.add(this.EmptyString);
		// NativeSumm
		row.add(summ);
		// KKM
		row.add(this.EmptyString);
		// Const
		row.add("0");
		// DocType
		row.add(String.valueOf(document.DocType));
		// Comm
		row.add("0");
		// Const
		row.add(this.EmptyString);
		// FullDocId
		row.add(this.EmptyString);
		// Werks
		row.add("1");
		//
		for (int i = 0; i < 16; i++) {
			row.add(this.EmptyString);
		}
		data.add(String.join(";", row));

		return 2; // +63
	}

	private int writePayments(final int startLine, final Document document, final List<String> data) {
		int result = 0;
		String code = "41";
		final List<String> row = new LinkedList<>();
		double payed = 0;
		LocalDateTime last = null;
		Payment refund = null;
		final List<Payment> payments = document.getPayments();

		for (final Payment payment : payments) {
			if (payment.Balance > 0 || payment.Refund > 0) {
				code = "40";
			}
			if (payment.DateTime != null) {
				last = payment.DateTime;

				if (payment.Refund > 0) {
					// Сдача
					refund = new Payment();
					refund.ToPay = -payment.Refund;
					refund.Type = payment.Type;
				}
			}
			payment.ToPay -= payed;
			payed += payment.ToPay;

		}
		if (refund != null) {
			payments.add(refund);
		}

		for (final Payment payment : payments) {
			payment.DateTime = last;
			row.clear();
			this.makePaymentRow(startLine + result, document, payment, row, code);
			String rowStr = String.join(";", row);
			data.add(rowStr);
			result++;
			row.set(0, String.valueOf(startLine + result));
			row.set(3, "43");
			rowStr = String.join(";", row);
			data.add(rowStr);
			result++;

		}

		return result;
	}

	/**
	 *
	 */
	private void makePaymentRow(final int startLine, final Document document, final Payment payment,
			final List<String> row, final String code) {
		this.writeRowStart(document, row, startLine, payment.DateTime, code);

		// MatID
		row.add(this.EmptyString);
		// PaymentType
		row.add(String.valueOf(payment.Type));
		// Price
		row.add("0");
		// Value
		final String payed = this.decimalFormat.format(payment.ToPay);
		row.add(payed);
		// Summ
		row.add(payed);
		// Type
		row.add(String.valueOf(document.DocType));
		// Smena
		row.add(String.valueOf(document.Smena));
		// FinalPrice
		row.add("0");
		// TotalSumm
		row.add("0");
		// PrtGrp
		row.add("0");
		// Matnr
		row.add(this.EmptyString);
		// EAN
		row.add("1");
		// NativeSumm
		row.add(this.EmptyString);
		// KKM
		row.add("0");
		// Const
		row.add("0");
		// DocType
		row.add((document.DocType == 0) ? "1" : "2");
		// Comm
		row.add(this.EmptyString);
		// Const
		row.add(this.EmptyString);
		// FullDocId
		row.add(document.getFullDocId());
		// Werks
		row.add("1");
		// Const
		row.add(this.EmptyString);
		// Const
		row.add("0");
		//
		for (int i = 0; i < 8; i++) {
			row.add(this.EmptyString);
		}
	}

	private int writeDiscount(final int startLine, final Document document, final Item item, final Discount discount,
			final List<String> data) {
		final List<String> row = new LinkedList<>();

		this.writeRowStart(document, row, startLine, item.DateTime, "17");

		// MatID
		if (discount.Card != null && !discount.Card.Barcode.isEmpty()) {
			row.add(discount.Card.Barcode);
		} else {
			row.add(this.EmptyString);
		}
		// CutID
		row.add(this.EmptyString);
		// Type
		row.add(String.valueOf(discount.Type));
		// Value
		row.add(String.valueOf(this.decimalFormat.format(discount.getDiscount())));
		// Summ
		row.add(String.valueOf(this.decimalFormatDiscount.format(discount.getSumm())));
		// Type
		row.add("0"); // ??
		// Smena
		row.add(String.valueOf(document.Smena));
		// ACTIVITI.CODE
		row.add(String.valueOf(discount.Code));
		// MarketAct
		row.add(String.valueOf(discount.ActNr));
		// PrtGrp
		row.add("0");
		//
		for (int i = 0; i < 5; i++) {
			row.add(this.EmptyString);
		}
		// DocType
		row.add((document.DocType == 0) ? "1" : "2");
		// Comm
		row.add(this.EmptyString);
		// Const
		row.add(this.EmptyString);
		// FullDocId
		row.add(document.getFullDocId());
		// Werks
		row.add("1");
		//
		for (int i = 0; i < 10; i++) {
			row.add(this.EmptyString);
		}

		data.add(String.join(";", row));

		return 1;
	}

	private void writeItemEx(final int startLine, final Document document, final Item item, final List<String> data,
			final boolean storno) {
		final List<String> row = new LinkedList<>();
		final double mul = (storno) ? -1 : 1;

		this.writeRowStart(document, row, startLine, item.DateTime, (storno) ? "12" : "11");

		// MatId
		row.add(String.valueOf(item.MatId));
		// CutID
		row.add(this.EmptyString);
		// Price
		row.add(this.decimalFormat.format(item.Price));
		// Meins
		row.add(this.decimalFormatCount.format(item.Count * mul));
		// RoundSumm
		final String nativeSumm = this.decimalFormat.format(item.getRoundSumm() * mul);
		row.add(nativeSumm);
		// Type
		row.add(String.valueOf(document.DocType)); // ??
		// Smena
		row.add(String.valueOf(document.Smena));
		// FinalPrice
		row.add(this.decimalFormat.format(item.getFinalPrice()));
		// TotalSumm
		row.add(this.decimalFormat.format(item.getTotalSumm() * mul));
		// PrtGrp
		row.add("0");
		// Matnr
		row.add(String.valueOf(item.Matnr));
		// EAN
		row.add(this.eanOut(item.EAN));
		// NativeSumm
		row.add(nativeSumm);
		// KKM
		row.add("0");
		// Const
		row.add("0");
		// DocType
		row.add((document.DocType == 0) ? "1" : "2");
		// Comm
		row.add("0");
		// Const
		row.add(this.EmptyString);
		// FullDocId
		row.add(document.getFullDocId());
		// Werks
		row.add("1");
		// EmpId
		row.add("0");
		//
		for (int i = 0; i < 6; i++) {
			row.add(this.EmptyString);
		}
		// Code
		row.add("0");
		//
		for (int i = 0; i < 4; i++) {
			row.add(this.EmptyString);
		}
		//
		row.add((storno) ? this.EmptyString : "0");
		//
		for (int i = 0; i < 3; i++) {
			row.add(this.EmptyString);
		}

		data.add(String.join(";", row));
	}

	private int writeItem(final int startLine, final Document document, final Item item, final List<String> data) {
		int result;

		result = 1;
		this.writeItemEx(startLine, document, item, data, false);
		if (item.Storned) {
			this.writeItemEx(startLine + result, document, item, data, true);
			result++;
		} else {
			final List<Discount> discounList = item.getDiscounList();
			if (discounList.size() > 1) {
				discounList.sort((o1, o2) -> o1.DateTime.compareTo(o2.DateTime));
			}
			for (final Discount discount : discounList) {
				if (!discount.isNotInDates()) {
					result += this.writeDiscount(startLine + result, document, item, discount, data);
				}
			}
		}

		return result;

	}

	private int writeStartDocument(final int startLine, final Document document, final List<String> data) {
		final List<String> row = new LinkedList<>();
		this.writeRowStart(document, row, startLine, document.StartDateTime, "42");
		// MatID
		if (document.Card != null && document.Card.Barcode != null) {
			row.add(document.Card.Barcode);
		} else {
			row.add(this.EmptyString);
		}
		// CutID
		row.add(this.EmptyString);
		// Price
		row.add(this.EmptyString);
		// Meins
		row.add(this.decimalFormatCount.format(document.getCount()));
		// RoundSumm
		final String payedSumm = this.decimalFormat.format(document.getPayedSumm(false));
		row.add(payedSumm);
		// Type
		row.add(String.valueOf(document.DocType));
		// Smena
		row.add(String.valueOf(document.Smena));
		// FinalPrice
		row.add("0");
		// TotalSumm
		row.add(payedSumm);
		// PrtGrp
		row.add("0");
		// Matnr
		row.add(this.EmptyString);
		// EAN
		row.add(this.EmptyString);
		// NativeSumm
		final String nativeSumm = this.decimalFormat.format(document.getNativeSumm(false));
		row.add(nativeSumm);
		// KKM
		row.add("0");
		// Const
		row.add("0");
		// DocType
		row.add((document.DocType == 0) ? "1" : "2");
		// Comm
		row.add("0");
		// Const
		row.add(this.EmptyString);
		// FullDocId
		row.add(document.getFullDocId());
		// Werks
		row.add("1");
		// EmpId
		row.add("0");
		//
		for (int i = 0; i < 8; i++) {
			row.add(this.EmptyString);
		}
		//
		row.add("0");
		//
		row.add("0");
		//
		for (int i = 0; i < 5; i++) {
			row.add(this.EmptyString);
		}

		data.add(String.join(";", row));

		return 1;
	}

	private void writeRowStart(final Document document, final List<String> row, final int line,
			final LocalDateTime dateTime, final String operation) {
		// LINE
		row.add(String.valueOf(line));
		if (dateTime != null) {
			// Date
			row.add(dateTime.toLocalDate().format(this.dateFormatter));
			// Time
			row.add(dateTime.toLocalTime().format(this.timeFormatter));
		} else {
			row.add(this.EmptyString);
			row.add(this.EmptyString);
		}
		// Trans
		row.add(operation);
		// Worstation
		row.add(String.valueOf(document.WorkstationID));
		// DocID
		row.add(String.valueOf(document.DocId));
		// Cashman
		row.add(String.valueOf(document.Kassir));
	}

	private int writeEndDocument(final int startLine, final Document document, final List<String> data) {
		final List<String> row = new LinkedList<>();

		this.writeRowStart(document, row, startLine, document.EndDateTime, (document.FullCancelled) ? "56" : "55");

		// MatID
		if (document.Card != null && document.Card.Barcode != null) {
			row.add(document.Card.Barcode);
		} else {
			row.add(this.EmptyString);
		}
		// CutID
		row.add(!document.FullCancelled ? this.EmptyString : "0");
		// Price
		row.add("0");
		// Meins
		row.add(this.decimalFormatCount.format(document.getCount()));
		// RoundSumm
		final String payedSumm = this.decimalFormat.format(document.getPayedSumm(false));
		row.add(payedSumm);
		// Type
		row.add(String.valueOf(document.DocType));
		// Smena
		row.add(String.valueOf(document.Smena));
		// FinalPrice
		row.add("0");
		// TotalSumm
		row.add(payedSumm);
		// PrtGrp
		row.add("0");
		// Matnr
		row.add(document.FullCancelled ? this.EmptyString : "0");
		// EAN
		row.add(this.EmptyString);
		// NativeSumm
		final String nativeSumm = this.decimalFormat.format(document.getNativeSumm(false));
		row.add(nativeSumm);
		// KKM
		row.add("0");
		// Const
		row.add("0");
		// DocType
		row.add((document.DocType == 0) ? "1" : "2");
		// Comm
		row.add("0");
		// Const
		row.add(this.EmptyString);
		// FullDocId
		row.add(document.getFullDocId());
		// Werks
		row.add("1");
		// EmpId
		row.add("0");
		//
		for (int i = 0; i < 6; i++) {
			row.add(this.EmptyString);
		}
		// Code
		row.add(document.FullCancelled ? this.EmptyString : "0");
		//
		row.add(this.EmptyString);
		//
		row.add(document.FullCancelled ? this.EmptyString : "0");
		//
		row.add(document.FullCancelled ? this.EmptyString : "0");
		//
		for (int i = 0; i < 5; i++) {
			row.add(this.EmptyString);
		}

		data.add(String.join(";", row));

		return 1;
	}

}
