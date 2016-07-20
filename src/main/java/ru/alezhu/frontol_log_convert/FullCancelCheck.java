package ru.alezhu.frontol_log_convert;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FullCancelCheck extends ItemPatternAbstract {

	Pattern pattern;

	public FullCancelCheck() {
		// Отмена чека в ККМ " , без ск. 218910,00, ск. поз. 0,00, ск. док.
		// 0,00, окр. 0,00 = 218910,00"
		// Отмена чека в ККМ " , без ск. 0,00, ск. поз. 0,00, ск. док. 0,00,
		// окр. 0,00 = 0,00"

		this.pattern = Pattern.compile("^Отмена чека в ККМ.+=\\s+(?<summ>\\d+),(?<dec>\\d+)");
	}

	@Override
	public boolean process(final LogParserContext context) {
		final Matcher matcher = this.pattern.matcher(context.logRow.Action);
		if (matcher.find()) {
			final int summ = Integer.parseInt(matcher.group("summ"));
			final int dec = Integer.parseInt(matcher.group("dec"));
			if (summ == 0 && dec == 0) {
				context.lastDocument.EndDateTime = context.logRow.DateTime.toLocalDateTime();
				// context.lastDocument.Closed = true;
				context.lastDocument.FullCancelled = true;
				return true;
			}
		}
		return false;
	}
}

