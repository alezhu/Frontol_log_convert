package ru.alezhu.frontol_log_convert;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTime {
	private LocalDateTime _DateTime;
	private String _value;
	
	public DateTime(String value) {
		this._value = value;
	}
	
	public String getDate() {
		return _value.substring(0, this._value.indexOf(' '));
	}
	
	public String getTime() {
		return _value.substring(this._value.indexOf(' ')+1);
	}
	
	public LocalDateTime getDateTime() {
		if (null == this._DateTime) {
			this._DateTime = LocalDateTime.parse(this._value, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss") );
		}
		return this._DateTime;
	}
}
