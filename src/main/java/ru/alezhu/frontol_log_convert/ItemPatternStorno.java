package ru.alezhu.frontol_log_convert;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ItemPatternStorno extends ItemPatternBase {

	Pattern pattern;

	public ItemPatternStorno() {
		// Сторно "212656637, 1000026143, FR. KLUBNIKA CHARLI 400GR"
		this.pattern = Pattern.compile("^Сторно\\s+\"(?<matid>\\d+),\\s+(?<matnr>\\d+),");
	}

	@Override
	public boolean process(final LogParserContext context) {
		final Matcher matcher = this.pattern.matcher(context.logRow.Action);
		if (matcher.find()) {
			final int matid = Integer.parseInt(matcher.group("matid"));
			final List<Item> items = this.findItems(context, matid);
			switch (items.size()) {
			case 0:
				// ����� �������.
				break;
			case 1:
				items.get(0).Storned = true;
				break;
			default:
				// Collections.reverse(items);
				if (context.lastDocument.MayBeStorned == null) {
					context.lastDocument.MayBeStorned = items;
				} else {
					context.lastDocument.MayBeStorned.addAll(items);
				}
				break;
			}

			context.lastDocument.getPayments().clear();
			context.lastDocument.StornedPayments.get().clear();
			return true;
		}
		return false;
	}
}
