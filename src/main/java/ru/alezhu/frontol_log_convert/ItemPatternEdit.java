package ru.alezhu.frontol_log_convert;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ItemPatternEdit extends ItemPatternBase {

	Pattern pattern;

	public ItemPatternEdit() {
		this.pattern = Pattern.compile(
				"^Редактирование\\s+(?<count>\\S+)\\s+X\\s+(?<price>\\S+)\\s+=\\s+(?<cost>\\S+)\\s+\"(?<matid>\\d+),\\s+(?<matnr>\\d+)");
	}

	@Override
	public boolean process(final LogParserContext context) {
		final Matcher matcher = this.pattern.matcher(context.logRow.Action);
		if (matcher.find()) {
			final int matid = Integer.parseInt(matcher.group("matid"));
			final Item item = this.findItem(context, matid);
			if (item != null) {
				item.Count = Double.parseDouble(matcher.group("count").replace(',', '.'));
				item.Price = Double.parseDouble(matcher.group("price").replace(',', '.'));
				item.NativeSumm = Double.parseDouble(matcher.group("cost").replace(',', '.'));
				item.Matnr = matcher.group("matnr");
				item.TypeKKM = 0;
			}

			context.lastDocument.getPayments().clear();
			context.lastDocument.StornedPayments.get().clear();

			return true;
		}
		return false;
	}

}
