package ru.alezhu.frontol_log_convert;

import ru.alezhu.frontol_log_convert.frontol.tables.BarcodeTable;
import ru.alezhu.frontol_log_convert.frontol.tables.MaterialTable.Row;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ItemPatternRegistration extends ItemPatternBase {
	Pattern pattern;

	public ItemPatternRegistration() {
		this.pattern = Pattern.compile(
				"^Регистрация\\s+(?<count>\\S+)\\s+X\\s+(?<price>\\S+)\\s+=\\s+(?<cost>\\S+)\\s+\"(?<matid>\\d+),\\s+(?<matnr>\\d+)");
	}

	@Override
	public boolean process(final LogParserContext context) {
		final Matcher matcher = this.pattern.matcher(context.logRow.Action);
		if (matcher.find()) {
			final Item item = new Item();
			item.Id = context.logRow.ID;
			item.MatId = Integer.valueOf(matcher.group("matid"));
			item.Matnr = matcher.group("matnr");
			item.Count = Double.valueOf(matcher.group("count").replace(',', '.'));
			item.Price = Double.valueOf(matcher.group("price").replace(',', '.'));
			item.NativeSumm = Double.valueOf(matcher.group("cost").replace(',', '.'));
			final int wareId = context.database.getWareId(item.MatId);
			if (wareId != 0) {
				if (context.lastEAN != null && !context.lastEAN.isEmpty()
						&& this.isEanValidForWareId(context, context.lastEAN, wareId)) {
					item.EAN = context.lastEAN;
					// item.DateTime = context.lastEANDateTime;
				} else {
					item.EAN = context.database.getEAN(wareId);
				}
				final Row rowMaterial = context.database.MainDb.get().MaterialTable.get().get(wareId, null, null);
				if (rowMaterial != null) {
					item.MaxDiscount = rowMaterial.MAXDISCOUNT;
				}
			}
			if (item.DateTime == null) {
				item.DateTime = context.logRow.DateTime.toLocalDateTime();
			}
			item.TypeKKM = 0;

			if (context.lastDocument.StartDateTime == null) {
				context.lastDocument.StartDateTime = item.DateTime;
			}

			this.findDiscount(context, item);

			context.lastDocument.getItems().add(item);

			return true;
		}
		return false;
	}

	private boolean isEanValidForWareId(final LogParserContext context, final String ean, final int wareId) {
		final BarcodeTable barcodeTable = context.database.MainDb.get().BarcodeTable.get();
		return barcodeTable.get(wareId, ean) != null;
	}

}
