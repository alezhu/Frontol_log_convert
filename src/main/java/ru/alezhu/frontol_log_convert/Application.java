package ru.alezhu.frontol_log_convert;

import ru.alezhu.frontol_log_convert.frontol.Database;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Application {

	public static void main(final String[] args) {
		final StartConfig startConfig = new StartConfig();
		for (final String arg : args) {
			final String[] pair = arg.split("=");
			if (pair.length < 2) {
				continue;
			}
			final String param = pair[0].trim().replaceAll("^[-/\\\\]+", "").toLowerCase();
			final String value = pair[1];
			switch (param) {
				case "m":
					startConfig.MainDbPath = getAbsolutePath(value);
					break;
				case "l":
					startConfig.LogDbPath = getAbsolutePath(value);
					break;
				case "o":
					startConfig.LogFilePath = getAbsolutePath(value);
					break;
				case "s":
					startConfig.StartLine = Integer.parseInt(value);
					break;
				case "f":
					startConfig.LogFrom = parseDateTime(value);
					break;
				case "t":
					startConfig.LogTo = parseDateTime(value);
					break;
				case "d":
					startConfig.FireBase = value;
					break;

				default:
					break;
			}
		}

		if (startConfig.LogDbPath == null || startConfig.LogDbPath.isEmpty() || startConfig.MainDbPath == null
				|| startConfig.MainDbPath.isEmpty() || startConfig.LogFilePath == null
				|| startConfig.LogFilePath.isEmpty()) {
			System.out.println("Usage: app.exe -m=<path to maindb> -l=<path_to_logdb> -o=<path_to_output_file>");
			System.out.println("\t[-s=<start_index> -f=<process_log_from_timestamp> -t=<process_lot_to_timestamp>]");
			System.out.println("");
			System.out.println(
					"\t*TimeStamp in -F and -T parameters in format dd.MM.yyyy HH:mm:ss (eg. 01.06.2016 18:00:00)");
			System.exit(1);
		}

		try {
			checkFileExists(startConfig.MainDbPath);
			checkFileExists(startConfig.LogDbPath);
		} catch (final Exception e) {
			System.err.println(e.getMessage());
			System.exit(2);
		}
		System.out.println("Начинаем обработку\n");
		final LogParser logParser = new LogParser(new Database(startConfig.FireBase, startConfig.MainDbPath,
				startConfig.LogDbPath, startConfig.LogFrom, startConfig.LogTo));
		try {
			logParser.Execute(new DocumentHandler(startConfig));
			System.out.println("Обработка завершена");
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			System.err.println(e.getMessage());
			e.printStackTrace();
			System.exit(4);
		}
	}

	private static String getAbsolutePath(final String value) {
		final Path appPath = Paths.get(".").toAbsolutePath();
		final Path parent = appPath.getParent();
		final Path path = parent.resolve(value.replaceAll("\"", ""));

		return path.toAbsolutePath().toString();
	}

	private static LocalDateTime parseDateTime(final String value) {
		LocalDateTime result;
		try {
			result = LocalDateTime.parse(value, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
		} catch (final DateTimeParseException ex) {
			result = LocalDate.parse(value, DateTimeFormatter.ofPattern("dd.MM.yyyy")).atTime(0, 0, 0);
		}
		return result;
	}

	/**
	 * @param path
	 *            - path to file which existense is checked
	 */
	private static void checkFileExists(final String path) throws RuntimeException {
		if (!Files.exists(Paths.get(path))) {
			throw new RuntimeException("File " + path + " does not exists");
		}

	}

}
