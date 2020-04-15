package dev.kirin.example.api.helper.jwt.util;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class TimeUtil {
	private static final TimeZone DEFAULT_TIMEZONE = TimeZone.getTimeZone("UTC");
	
	public static final long now() {
		return Calendar.getInstance(DEFAULT_TIMEZONE).getTimeInMillis();
	}
	
	public static final long after(int unit, int value) {
		return after(now(), unit, value);
	}
	
	public static final long after(long time, int unit, int value) {
		GregorianCalendar cal = new GregorianCalendar(DEFAULT_TIMEZONE);
		cal.setTimeInMillis(time);
		cal.add(unit, value);
		return cal.getTimeInMillis();
	}
	
	public static final long before(int unit, int value) {
		return before(now(), unit, value);
	}
	
	public static final long before(long time, int unit, int value) {
		GregorianCalendar cal = new GregorianCalendar(DEFAULT_TIMEZONE);
		cal.setTimeInMillis(time);
		cal.add(unit, value * -1);
		return cal.getTimeInMillis();
	}
}
