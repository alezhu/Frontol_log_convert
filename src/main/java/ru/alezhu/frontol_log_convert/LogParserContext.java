package ru.alezhu.frontol_log_convert;

import ru.alezhu.frontol_log_convert.frontol.Database;
import ru.alezhu.frontol_log_convert.frontol.LogRow;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class LogParserContext {
	public LogRow logRow;
	public Stack<LogRow> stack = new Stack<>();
	public String lastEAN = null;
	public Document lastDocument = null;
	public Map<Integer, Document> OpenDocs = new HashMap<>();
	public Database database;
	public IDocumentHandler Handler;
	public Document smenaDoc;
	public LocalDateTime lastEANDateTime;
}
