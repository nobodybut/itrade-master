package com.trade.common.infrastructure.util.date;

import com.trade.common.infrastructure.util.string.CustomStringUtils;

import java.time.*;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static java.time.temporal.TemporalAdjusters.firstDayOfNextMonth;
import static java.time.temporal.TemporalAdjusters.nextOrSame;

public class CustomDateUtils {

	// 默认包含一些 pattern
	public static final String yyyy_MM_dd = "yyyy-MM-dd";
	public static final String yyyy_MM_dd_HH_mm_ss = "yyyy-MM-dd HH:mm:ss";
	public static final String yyyy_MM_dd_HH_mm = "yyyy-MM-dd HH:mm";

	public static final String yyyyMMdd = "yyyyMMdd";
	public static final String yyyy_MM_dd_en = "yyyy/MM/dd";

	public static final String MM_dd_yyyy = "MM/dd/yyyy";
	public static final String MM_dd_yyyy_HH_mm_ss = "MM/dd/yyyy HH:mm:ss";
	public static final String MM_dd_yyyy_HH_mm = "MM/dd/yyyy HH:mm";

	public static final String dd_MM_yyyy = "dd/MM/yyyy";
	public static final String dd_MM_yyyy_HH_mm_ss = "dd/MM/yyyy HH:mm:ss";
	public static final String dd_MM_yyyy_HH_mm = "dd/MM/yyyy HH:mm";

	public static final String HH_mm_ss_SSS = "HH:mm:ss,SSS";
	public static final String HH_mm_ss = "HH:mm:ss";
	public static final String HH_mm = "HH:mm";

	/**
	 * 将 LocalDate 转换为 Date
	 *
	 * @param localDate
	 * @return
	 */
	public static Date toDate(LocalDate localDate) {
		return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
	}

	/**
	 * 将 LocalDateTime 转换为 Date
	 *
	 * @param localDateTime
	 * @return
	 */
	public static Date toDate(LocalDateTime localDateTime) {
		return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
	}

	/**
	 * 取得最小日期（1901-01-01）
	 *
	 * @return
	 */
	public static LocalDate getMinimalDate() {
		return LocalDate.of(1901, 01, 01);
	}

	/**
	 * 取得最小日期时间（1901-01-01 00:00:00）
	 *
	 * @return
	 */
	public static LocalDateTime getMinimalDateTime() {
		return LocalDate.of(1901, 01, 01).atStartOfDay();
	}

	/**
	 * 是否为最小日期（1901-01-01）
	 *
	 * @return
	 */
	public static boolean isMinimalDate(LocalDate localDate) {
		return localDate.getYear() == 1901 && localDate.getMonthValue() == 1 && localDate.getDayOfMonth() == 1;
	}

	/**
	 * 是否为最小日期（1901-01-01）
	 *
	 * @return
	 */
	public static boolean isMinimalDateTime(LocalDateTime localDateTime) {
		return localDateTime.getYear() == 1901 && localDateTime.getMonthValue() == 1 && localDateTime.getDayOfMonth() == 1;
	}

	/**
	 * 判断某个日期是否在两个日期时间之间（LocalDateTime，等于也算）
	 *
	 * @param dateTime
	 * @param beforeDateTime
	 * @param afterDateTime
	 * @return
	 */
	public static boolean isBetweenTwoDateTimes(LocalDateTime dateTime, LocalDateTime beforeDateTime, LocalDateTime afterDateTime) {
		return (dateTime.isAfter(beforeDateTime) || dateTime.equals(beforeDateTime)) && (dateTime.isBefore(afterDateTime) || dateTime.equals(afterDateTime));
	}

	/**
	 * 判断某个日期是否在两个日期之间（LocalDate，等于也算）
	 *
	 * @param date
	 * @param beforeDate
	 * @param afterDate
	 * @return
	 */
	public static boolean isBetweenTwoDates(LocalDate date, LocalDate beforeDate, LocalDate afterDate) {
		return (date.isAfter(beforeDate) || date.equals(beforeDate)) && (date.isBefore(afterDate) || date.equals(afterDate));
	}

	/**
	 * 判断某个时间点是否在两个时间点之间（LocalTime，等于也算）
	 *
	 * @param time
	 * @param time1
	 * @param time2
	 * @return
	 */
	public static boolean isBetweenTwoTimes(LocalTime time, LocalTime time1, LocalTime time2) {
		return (time.isAfter(time1) || time.equals(time1)) && (time.isBefore(time2) || time.equals(time2));
	}

	/**
	 * 判断某个时间点是否在两个时间点之间（字符串，等于也算）
	 *
	 * @param timeStr
	 * @param time1Str
	 * @param time2Str
	 * @return
	 */
	public static boolean isBetweenTwoTimes(String timeStr, String time1Str, String time2Str) {
		LocalTime time = CustomDateParseUtils.parseTime(timeStr);
		LocalTime time1 = CustomDateParseUtils.parseTime(time1Str);
		LocalTime time2 = CustomDateParseUtils.parseTime(time2Str);

		return isBetweenTwoTimes(time, time1, time2);
	}

	/**
	 * 判断某个时间点是否某个时间点之后（字符串）
	 *
	 * @param timeStr1
	 * @param timeStr2
	 * @return
	 */
	public static boolean time1IsAfterTime2(String timeStr1, String timeStr2) {
		LocalTime time1 = CustomDateParseUtils.parseTime(timeStr1);
		LocalTime time2 = CustomDateParseUtils.parseTime(timeStr2);

		return time1.isAfter(time2);
	}

	/**
	 * 判断 date1 是否大于等于 date2
	 *
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static boolean isAfterOrEquals(LocalDate date1, LocalDate date2) {
		if (date1 == null || date2 == null) {
			return false;
		}

		return date1.equals(date2) || date1.isAfter(date2);
	}

	/**
	 * 判断 date1 是否小于等于 date2
	 *
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static boolean isBeforeOrEquals(LocalDate date1, LocalDate date2) {
		if (date1 == null || date2 == null) {
			return false;
		}

		return date1.equals(date2) || date1.isBefore(date2);
	}

	/**
	 * 计算两个 LocalDateTime 之间的时间间隔对象
	 *
	 * @param startDateTime
	 * @param endDateTime
	 * @return
	 */
	public static Duration getDurationBetween(LocalDateTime startDateTime, LocalDateTime endDateTime) {
		return Duration.between(startDateTime, endDateTime);
	}

	/**
	 * 计算两个 LocalDate 之间的时间间隔对象
	 *
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static Duration getDurationBetween(LocalDate startDate, LocalDate endDate) {
		return Duration.between(startDate.atStartOfDay(), endDate.atStartOfDay());
	}

	/**
	 * 计算两个 LocalTime 之间的时间间隔对象
	 *
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static Duration getDurationBetween(LocalTime startTime, LocalTime endTime) {
		return Duration.between(startTime, endTime);
	}

	/**
	 * 计算指定日期属于当前年份的第几周
	 *
	 * @param localDate
	 * @return
	 */
	public static int getWeekOfWeekyear(LocalDate localDate) {
		Calendar c = new GregorianCalendar();
		c.setFirstDayOfWeek(Calendar.MONDAY);
		c.setMinimalDaysInFirstWeek(1);
		c.setTime(CustomDateUtils.toDate(localDate));

		return c.get(Calendar.WEEK_OF_YEAR);
	}

	/**
	 * 计算指定日期属于当前月份的第几周
	 *
	 * @param localDate
	 * @return
	 */
	public static int getWeekOfMonth(LocalDate localDate) {
		Calendar c = new GregorianCalendar();
		c.setFirstDayOfWeek(Calendar.MONDAY);
		c.setMinimalDaysInFirstWeek(1);
		c.setTime(CustomDateUtils.toDate(localDate));

		return c.get(Calendar.WEEK_OF_MONTH);
	}

	/**
	 * 计算某日期距 1970-1-1 的毫秒数
	 *
	 * @param localDateTime
	 * @return
	 */
	public static long getDateTimeMillisecond(LocalDateTime localDateTime) {
		return Duration.between(LocalDate.of(1970, 1, 1).atStartOfDay(), localDateTime).toMillis();
	}

	/**
	 * 计算某日期距 1970-1-1 的毫秒数（返回字符串）
	 *
	 * @param localDateTime
	 * @return
	 */
	public static String getDateTimeMillisecondStr(LocalDateTime localDateTime) {
		return String.valueOf(getDateTimeMillisecond(localDateTime));
	}

	/**
	 * 计算某日期距 1970-1-1 的秒数
	 *
	 * @param localDateTime
	 * @return
	 */
	public static long getDateTimeSecond(LocalDateTime localDateTime) {
		return Duration.between(LocalDate.of(1970, 1, 1).atStartOfDay(), localDateTime).getSeconds();
	}

	/**
	 * 计算某日期距 1970-1-1 的秒数（返回字符串）
	 *
	 * @param localDateTime
	 * @return
	 */
	public static String getDateTimeSecondStr(LocalDateTime localDateTime) {
		return String.valueOf(getDateTimeSecond(localDateTime));
	}

	/**
	 * 计算从 2009-1-1 到指定时间经历的秒数
	 *
	 * @param localDateTime
	 * @return
	 */
	public static long getDateTimeTicks(LocalDateTime localDateTime) {
		return Duration.between(LocalDate.of(2009, 1, 1).atStartOfDay(), localDateTime).getSeconds();
	}

	/**
	 * 计算从 2009-1-1 到指定时间经历的秒数（返回字符串）
	 *
	 * @param localDateTime
	 * @return
	 */
	public static String getDateTimeTicksStr(LocalDateTime localDateTime) {
		return String.valueOf(getDateTimeTicks(localDateTime));
	}

	/**
	 * 计算住宿相关总天数（日期时间相减）
	 *
	 * @param checkOutDate
	 * @param checkInDate
	 * @return
	 */
	public static int getTotalStayDays(LocalDate checkInDate, LocalDate checkOutDate) {
		return (int) getDurationBetween(checkInDate, checkOutDate).toDays();
	}

	/**
	 * 计算整体行程相关总天数（日期时间相减），此总天数没有考虑航班到达时间问题
	 *
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static int getTotalTravelDays(LocalDate startDate, LocalDate endDate) {
		return (int) getDurationBetween(startDate, endDate).toDays() + 1;
	}

	/**
	 * 转换中文字符串日期为英文日期字符串
	 *
	 * @param dateStr
	 * @return
	 */
	public static String toENDateString(String dateStr) {
		return CustomDateParseUtils.correctDateStr(dateStr.replace("年", "-").replace("月", "-").replace("日", ""));
	}

	/**
	 * 转换周几为中文
	 *
	 * @param dayOfWeek
	 * @return
	 */
	public static String dayOfWeekToCN(DayOfWeek dayOfWeek) {
		return dayOfWeekToCN(dayOfWeek.getValue());
	}

	/**
	 * 转换周几为中文
	 *
	 * @param dayOfWeekValue
	 * @return
	 */
	public static String dayOfWeekToCN(int dayOfWeekValue) {
		switch (dayOfWeekValue) {
			case 1:
				return "周一";
			case 2:
				return "周二";
			case 3:
				return "周三";
			case 4:
				return "周四";
			case 5:
				return "周五";
			case 6:
				return "周六";
			case 7:
				return "周日";
		}

		return "";
	}

	/**
	 * 转换Date为LocalDateTime
	 *
	 * @param date
	 * @return
	 */
	public static LocalDateTime dateToLocalDateTime(Date date) {
		return LocalDateTime.of(date.getYear() + 1900, date.getMonth() + 1, date.getDate(), date.getHours(), date.getMinutes(), date.getSeconds());
	}

	/**
	 * 计算当天时间到某个指定日期之间的剩余秒数
	 *
	 * @param createTime
	 * @param date
	 * @return
	 */
	public static int calRemainderSeconds(LocalDateTime createTime, LocalDate date) {
		return (int) CustomDateUtils.getDurationBetween(createTime, date.plusDays(1).atStartOfDay()).getSeconds();
	}

	/**
	 * 获取下个月的第一个星期几
	 *
	 * @param dayOfWeek
	 * @return
	 */
	public static LocalDate getNextMonthFirstDayOfWeek(int dayOfWeek) {
		return LocalDate.now().with(firstDayOfNextMonth()).with(nextOrSame(DayOfWeek.of(dayOfWeek)));
	}

	/**
	 * 转换4个字符的时间字符串为5个字符的时间字符串
	 *
	 * @param time
	 * @return
	 */
	public static String getLocalTimeStrByFourChar(String time) {
		if (time.length() == 4) {
			return CustomStringUtils.substringCS(time, 0, 2) + ":" + CustomStringUtils.substringCS(time, 2, 2);
		}

		return "";
	}

	/**
	 * 转换4个字符的时间字符串为5个字符的时间对象
	 *
	 * @param time
	 * @return
	 */
	public static LocalTime getLocalTimeByFourChar(String time) {
		String timeStr = getLocalTimeStrByFourChar(time);
		if (timeStr.length() == 5) {
			return CustomDateParseUtils.parseTime(timeStr);
		}

		return LocalTime.MIN;
	}

	public static void main(String[] args) {
		LocalDateTime ldt = dateToLocalDateTime(new Date());
		System.out.println(ldt);
	}
}