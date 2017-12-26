package com.rekoe.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Helper {

	public static Date getTimeZoneDate() {
		return getCalendarByTimeZone("GMT+8").getTime();
	}

	public static Date getTimeZoneDate(Date date) {
		return getCalendarByTimeZone("GMT+8", date).getTime();
	}

	public static int getWeekDay() {
		return getCalendarByTimeZone("GMT+8").get(Calendar.DAY_OF_WEEK);
	}

	public static Calendar getCalendarByTimeZone(String timeZone, Date time) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(TimeZone.getTimeZone(timeZone));
		calendar.setTime(time);
		Calendar calendar2 = Calendar.getInstance();
		calendar2.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
		return calendar2;
	}

	public static Calendar getCalendarByTimeZone(String timeZone) {
		Date date = new Date(System.currentTimeMillis());
		return getCalendarByTimeZone(timeZone, date);
	}

	public static Date getTimeZoneDate2() {
		Date date = new Date(System.currentTimeMillis()); // 2014-1-31 21:20:50
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		calendar.setTime(date);
		Calendar calendar2 = Calendar.getInstance();
		calendar2.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
		return calendar2.getTime();
	}

}
