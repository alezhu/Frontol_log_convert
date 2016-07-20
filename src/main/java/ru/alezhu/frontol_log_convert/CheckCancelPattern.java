package ru.alezhu.frontol_log_convert;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class CheckCancelPattern extends ItemPatternAbstract {
	private final Pattern pattern;

	/**
	 *
	 */
	CheckCancelPattern() {
		// Отмена чека в ККМ " , без ск. 380047,60, ск. поз. 0,00, ск. док.
		// 0,00, окр. 0,00 = 380047,60"
		this.pattern = Pattern.compile("^Отмена\\s+чека\\s+в\\s+ККМ.+=\\s*(?<summ>[0-9,]+)");

	}


	@Override
	public boolean process(final LogParserContext context) {
		final Matcher matcher = this.pattern.matcher(context.logRow.Action);
		if (matcher.find()) {
			final Double summ = Double.valueOf(matcher.group("summ").replace(',', '.'));
			if (summ > 0) {
				// context.lastDocument.getPayments().removeIf((payment) -> {
				// final boolean result = payment.ToPay == summ;
				// if (result) {
				// context.lastDocument.StornedPayments.get().add(payment);
				// }
				// return result;
				// });
				context.lastDocument.getPayments().clear();
				return true;
			}
		}
		return false;

	}

}
