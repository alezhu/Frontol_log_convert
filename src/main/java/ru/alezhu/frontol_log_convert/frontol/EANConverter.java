package ru.alezhu.frontol_log_convert.frontol;

import ru.alezhu.frontol_log_convert.frontol.db.MainDb;
import ru.alezhu.frontol_log_convert.frontol.tables.IntBarcsTable;
import ru.alezhu.frontol_log_convert.frontol.tables.IntBarcsTable.Row;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EANConverter {
	private final MainDb mainDb;
	private List<EanPattern> patterns;

	private class EanPattern {
		int prefixFrom;
		int prefixTo;
		Pattern regex;
	}

	public EANConverter(final MainDb mainDb) {
		this.mainDb = mainDb;
	}

	public String convert(final String ean) {
		if (this.patterns == null) {
			this.buildPatterns();
		}
		for (final EanPattern pattern : this.patterns) {
			final Matcher matcher = pattern.regex.matcher(ean);
			if (matcher.find()) {
				final int prefix = Integer.valueOf(matcher.group("prefix"));
				if (prefix >= pattern.prefixFrom && prefix <= pattern.prefixTo) {
					return matcher.group("ean");
				}
			}
		}
		return ean.trim();
	}

	private void buildPatterns() {
		this.patterns = new LinkedList<>();
		final List<Row> rows = new IntBarcsTable(this.mainDb).getAllRows();
		final StringBuilder stringBuilder = new StringBuilder();
		for (final IntBarcsTable.Row row : rows) {
			final String[] parts = row.DATA.split("\\|");
			stringBuilder.setLength(0);
			for (int i = 0; i < parts.length; i++) {
				final String part = parts[i];
				final String[] values = part.split(";");
				switch (Integer.parseInt(values[0])) {
				case 6:
					if (i == 0) {
						stringBuilder.append("(?<prefix>\\d{");
						stringBuilder.append(values[1]);
						stringBuilder.append("})");
					} else {
						stringBuilder.append("\\d{");
						stringBuilder.append(values[1]);
						stringBuilder.append("}");
					}
					break;
				case 2:
					stringBuilder.append("(?<ean>\\d{");
					stringBuilder.append(values[1]);
					stringBuilder.append("})");
					break;
				case 5:
					stringBuilder.append("(?<count>\\d{");
					stringBuilder.append(values[1]);
					stringBuilder.append("})");

					break;

				default:
					stringBuilder.append("\\d");

					break;
				}

			}

			final EanPattern pattern = new EanPattern();
			pattern.prefixFrom = Integer.valueOf(row.PREFIXBEG);
			pattern.prefixTo = Integer.valueOf(row.PREFIXEND);
			pattern.regex = java.util.regex.Pattern.compile(stringBuilder.toString());
			this.patterns.add(pattern);
		}
	}

}
