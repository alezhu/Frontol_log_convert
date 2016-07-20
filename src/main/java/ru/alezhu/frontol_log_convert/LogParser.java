package ru.alezhu.frontol_log_convert;

import ru.alezhu.frontol_log_convert.frontol.Database;
import ru.alezhu.frontol_log_convert.frontol.Lazy;
import ru.alezhu.frontol_log_convert.frontol.LogRow;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogParser {
	private Pattern DOC_DATA_32_2324;
	private final LogParserContext context;
	private List<ItemPatternAbstract> ItemPatterns;

	public LogParser(final Database database) {
		this.context = new LogParserContext();
		this.setDatabase(database);
	}

	public Database getDatabase() {
		return this.context.database;
	}

	private void setDatabase(final Database database) {
		this.context.database = database;
	}

	public void Execute(final IDocumentHandler handler) {
		this.context.Handler = handler;
		while ((this.context.logRow = this.context.database.getNextLogRow()) != null) {
			this.context.stack.push(this.context.logRow);

			if (this.context.logRow.DocNumber != 0) {
				this.context.lastDocument = this.getDocument(this.context.logRow.DocNumber);
			}

			switch (this.context.logRow.State) {
				case 32:
					switch (this.context.logRow.FuncId) {
						case 324:
							this.createDocument(this.context.logRow);

							break;
						case 5001:
							// ШК
							String ean = this.clearEan(this.context.logRow.Action);
							this.context.lastEAN = this.context.database.getRealEAN(ean);
							this.context.lastEANDateTime = this.context.logRow.DateTime.toLocalDateTime();
							break;
						case 205:
							// ШК
							ean = this.clearEanInSquare(this.context.logRow.Action);
							this.context.lastEAN = this.context.database.getRealEAN(ean);
							this.context.lastEANDateTime = this.context.logRow.DateTime.toLocalDateTime();
							break;

						default:
							break;
					}
					break;
				case 672:
					switch (this.context.logRow.FuncId) {
						case 0:
							this.processItem();
							break;
						default:
							break;
					}
					break;
				case 160:
					switch (this.context.logRow.FuncId) {
						case 0:
							this.processItem();
							break;
						case 205:
							// ШК
							this.context.lastEAN = this.context.logRow.Action.replace('[', ' ').replace(']', ' ').trim();
							this.context.lastEANDateTime = this.context.logRow.DateTime.toLocalDateTime();
							break;
						case 5001:
							// ШК
							final String ean = this.clearEan(this.context.logRow.Action);
							this.context.lastEAN = this.context.database.getRealEAN(ean);
							this.context.lastEANDateTime = this.context.logRow.DateTime.toLocalDateTime();
							break;
						default:
							break;
					}
					break;
				case 1184:
					switch (this.context.logRow.FuncId) {
						case 0:
							this.processPayment();
							break;
						case 208:
							this.cancelLastPayment();
							break;
						case 320:
							if (this.context.lastDocument != null) {
								this.context.lastDocument.EndDateTime = this.context.logRow.DateTime.toLocalDateTime();
							}
							break;

						default:
							break;
					}
					break;
				case 22:
					switch (this.context.logRow.FuncId) {
						case 0:
							this.closeSmena();
							break;
						default:
							break;
					}
					break;
				default:
					switch (this.context.logRow.FuncId) {
						case 324:
							this.createDocument(this.context.logRow);
							break;
						default:
							break;
					}
					break;

			}
		}
	}

	/**
	 *
	 */
	private void cancelLastPayment() {
		final List<Payment> payments = this.context.lastDocument.getPayments();
		final int size = payments.size();
		if (size > 0) {
			payments.remove(size - 1);
			// if(context.lastDocument.StornedPayments.Instanced()) {
			//
			// }
		}
	}

	private String clearEanInSquare(final String data) {
		int start = data.indexOf('[');
		if (start >= 0) {
			start++;
			final int end = data.indexOf(']', start);
			if (end > start) {
				return data.substring(start, end - 1);
			}
		}
		return data;
	}

	private String clearEan(final String data) {
		String ean = data.trim();
		final int index = ean.indexOf(' ');
		if (index > 0) {
			ean = ean.substring(0, index);
		}
		return ean;
	}

	/**
	 *
	 */
	private void closeSmena() {
		if (this.context.logRow.Action.contains("Начало закрытия смены")) {

			final Document lastDocument = this.context.lastDocument;
			this.context.lastDocument = null;
			for (final Document document : this.context.OpenDocs.values()) {
				this.closeDocument(document);
			}
			this.context.OpenDocs.clear();
			if (lastDocument != null) {
				this.closeDocument(lastDocument);
			}

			final Document smenaDocument = this.getSmenaDocument();
			smenaDocument.StartDateTime = smenaDocument.EndDateTime = this.context.logRow.DateTime.toLocalDateTime();
			smenaDocument.Kassir = this.context.logRow.UserCode;
			// Закрываем смены
			if (this.context.Handler != null) {
				this.context.Handler.process(smenaDocument);
			}
			this.context.smenaDoc = null;
		}

	}

	/**
	 *
	 */
	private Document getSmenaDocument() {
		if (this.context.smenaDoc == null) {
			this.context.smenaDoc = new Document();
			this.context.smenaDoc.DocType = 9;
			this.context.smenaDoc.SmenaClosing = true;
		}
		return this.context.smenaDoc;
	}

	private final Lazy<Pattern> PaymentPattern = new Lazy<>(() -> Pattern.compile(
			"^Оплата\\s+\"Код опл.\\s+(?<code>\\d+).+Сумма\\s+к\\s+опл\\.\\s+(?<pay>[0-9,]+),.+Сдача\\s+(?<refund>[0-9,]+)\""));

	private final Lazy<Pattern> PartPaymentPattern = new Lazy<>(() -> Pattern.compile(
			"^Оплата\\s+\"Код опл.\\s+(?<code>\\d+).+Сумма\\s+к\\s+опл\\.\\s+(?<pay>[0-9,]+),.+Остаток\\s+(?<balance>[0-9,]+)\""));

	private final Lazy<CardPattern> _CardPattern = new Lazy<>(CardPattern::new);

	private final Lazy<CheckCancelPattern> _CheckCancelPattern = new Lazy<>(CheckCancelPattern::new);

	private void processPayment() {
		int Type;
		Matcher matcher = this.PaymentPattern.get().matcher(this.context.logRow.Action);
		if (matcher.find()) {
			Type = 1;
		} else {
			matcher = this.PartPaymentPattern.get().matcher(this.context.logRow.Action);
			if (matcher.find()) {
				Type = 2;
			} else if (this._CardPattern.get().process(this.context)) {
				return;
			} else if (this._CheckCancelPattern.get().process(this.context)) {
				return;
			} else {
				if (this.context.logRow.Action.contains("Запись кассовых транзакций")) {
					final LogRow row = this.context.stack.get(this.context.stack.size() - 2);
					if (row.FuncId == 320 && row.State == 1184) {
						this.closeDocument(this.context.logRow.DocNumber);
					}
				} else if (this.context.logRow.Action.contains("Отмена чека в ККМ")) {

				}
				return;
			}
		}

		final Payment payment = new Payment();
		// payment.DateTime = this.context.logRow.DateTime.toLocalDateTime();
		payment.ToPay = Double.parseDouble(matcher.group("pay").replace(',', '.'));
		payment.Type = Integer.parseInt(matcher.group("code"));
		switch (Type) {
			case 1:
				payment.Refund = Double.parseDouble(matcher.group("refund").replace(',', '.'));
				payment.DateTime = this.context.logRow.DateTime.toLocalDateTime();
				break;
			case 2:
				payment.Balance = Double.parseDouble(matcher.group("balance").replace(',', '.'));
				break;

			default:
				break;
		}
		this.context.lastDocument.getPayments().add(payment);
	}

	private void closeDocument(final Document document) {
		if (document != null && !document.Closed) {

			if (document.Card != null) {
				CardPattern.findDiscountCardInfo(this.context.database, document, document.Card);
			}
			document.close(this.context.database);

			// Закрываем чек
			if (this.context.Handler != null) {
				this.context.Handler.process(document);
			}

			final Document smenaDocument = this.getSmenaDocument();
			if (smenaDocument.WorkstationID == 0) {
				smenaDocument.WorkstationID = document.WorkstationID;
			}
			if (smenaDocument.Smena == 0) {
				smenaDocument.Smena = document.Smena;
			}
			// if (smenaDocument.WorkstationID != result.WorkstationID || smenaDocument.Smena != result.Smena) {
			if (smenaDocument.Smena != document.Smena && !document.FullCancelled) {
				// Закрываем смену
				if (this.context.Handler != null) {
					// this.context.Handler.process(smenaDocument);
				}
				// this.context.smenaDoc = null;
			} else {
				smenaDocument.DocId = document.DocId + 1;
				smenaDocument.RoundSumm += document.getPayedSumm(false);
			}

		}
		// this.context.stack.clear();
	}

	private Document closeDocument(final int docNumber) {
		final Document document = this.getDocument(docNumber);
		if (document != null) {
			this.closeDocument(document);
		}
		return document;
	}

	private void pauseDocument(final Document document) {
		if (document != null) {
			if (!document.Closed) {
				if (document.FullCancelled) {
					this.closeDocument(document.DocId);
				} else {
					this.context.OpenDocs.putIfAbsent(document.DocId, document);
				}
			}
		}
		this.context.lastDocument = null;
	}

	private Document resumeDocument(final int docId) {
		if (this.context.lastDocument != null) {
			if (this.context.lastDocument.DocId == docId) {
				return this.context.lastDocument;
			}
			this.pauseDocument(this.context.lastDocument);
		}
		this.context.lastDocument = this.context.OpenDocs.get(docId);
		if (this.context.lastDocument != null) {
			this.context.OpenDocs.remove(docId);
		}
		return this.context.lastDocument;

	}

	private void openDocument(final Document document) {
		if (this.context.lastDocument != document) {
			this.pauseDocument(this.context.lastDocument);
		}
		this.context.lastDocument = document;
	}

	private Document getDocument(final int docNumber) {
		Document result = this.resumeDocument(docNumber);
		if (result == null) {
			result = new Document();
			result.DocId = docNumber;
			this.openDocument(result);
		}
		return result;
	}

	private void processItem() {
		if (this.context.logRow.Action.contains("Запись кассовых транзакций")) {
			this.context.lastDocument.EndDateTime = this.context.logRow.DateTime.toLocalDateTime();
			if (this.context.lastDocument.FullCancelled) {
				this.closeDocument(this.context.logRow.DocNumber);
				this.context.stack.clear();
				return;
			}
			final LogRow row = this.context.stack.get(this.context.stack.size() - 2);
			if (row.State == 160 && row.FuncId == 320) {
				final List<Payment> payments = this.context.lastDocument.getPayments();

				if (payments.size() == 0 && this.context.lastDocument.StornedPayments.Instanced()) {
					final String summStr = row.Action.replace("[", "").replace("]", "").replace(",", ".")
							.replaceAll(" ", "").trim();
					final double summ = Double.valueOf(summStr);
					for (final Payment payment : this.context.lastDocument.StornedPayments.get()) {
						if (payment.ToPay == summ) {
							payments.add(payment);
							break;
						}
					}
				}
				this.closeDocument(this.context.logRow.DocNumber);
				this.context.stack.clear();
			}
			return;
		}

		final String barcode = "Штрихкод";
		int start = this.context.logRow.Action.indexOf(barcode);
		if (start >= 0) {
			start += barcode.length();
			final int end = this.context.logRow.Action.indexOf(',', start);
			if (end > start) {
				this.context.lastEAN = this.context.database
						.getRealEAN(this.context.logRow.Action.substring(start, end));
			}
			return;
		}

		start = this.context.logRow.Action.indexOf("Товар со штрихкодом");
		if (start >= 0) {
			if (this.context.logRow.Action.indexOf("не найден!", start + 20) > start) {
				this.context.lastEAN = null;
			}
			return;
		}

		if (this.ItemPatterns == null) {
			this.buildItemPatterns();
		}
		for (final ItemPatternAbstract pattern : this.ItemPatterns) {
			if (pattern.process(this.context)) {
				this.context.lastEAN = null;
				return;
			}
		}
	}

	private void buildItemPatterns() {
		this.ItemPatterns = new LinkedList<>();
		this.ItemPatterns.add(new ItemPatternRegistration());
		this.ItemPatterns.add(new ItemPatternEdit());
		this.ItemPatterns.add(new ItemPatternStorno());
		this.ItemPatterns.add(new CardPattern());
		this.ItemPatterns.add(new CheckCancelPattern());
		this.ItemPatterns.add(new FullCancelCheck());
	}

	private void createDocument(final LogRow row) {

		if (this.DOC_DATA_32_2324 == null) {
			this.DOC_DATA_32_2324 = Pattern.compile("^\\s*(?<ws>\\d+)\\s.*№(?<docid>\\d+)\\s+/\\s+(?<smena>\\d+)");
		}
		final Matcher matcher = this.DOC_DATA_32_2324.matcher(row.Action);
		if (matcher.find()) {
			final Document document = this.getDocument(Integer.parseInt(matcher.group("docid")));
			document.WorkstationID = Integer.valueOf(matcher.group("ws"));
			document.Smena = Integer.valueOf(matcher.group("smena"));
			// result.StartDateTime = row.DateTime.toLocalDateTime();
			document.Kassir = row.UserCode;
		}
	}
}
