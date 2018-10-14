package com.itrade.common.infrastructure.util.date;

import com.itrade.common.infrastructure.util.logger.LogInfoUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class CustomDateParseUtils {

	/**
	 * 日志记录
	 **/
	private final static Logger s_logger = LoggerFactory.getLogger(CustomDateParseUtils.class);

	/** ================================= 日期 数据解析处理 ================================= */
	/**
	 * 解析 日期字符串 为 LocalDate 类型的日期数据
	 *
	 * @param dateStr
	 * @return
	 */
	public static LocalDate parseDate(String dateStr) {
		return LocalDate.parse(correctDateStr(dateStr), DateTimeFormatter.ofPattern(CustomDateUtils.yyyy_MM_dd));
	}

	/** ================================= 时间 数据解析处理 ================================= */
	/**
	 * 解析 时间字符串 为 LocalTime 类型的时间数据
	 *
	 * @param timeStr
	 * @return
	 */
	public static LocalTime parseTime(String timeStr) {
		if (timeStr.startsWith("24:")) {
			timeStr = "00:" + timeStr.substring(3);
		}

		if (timeStr.contains("：")) {
			timeStr = timeStr.replace("：", ":");
		}

		return LocalTime.parse(correctTimeStr(timeStr), DateTimeFormatter.ofPattern(CustomDateUtils.HH_mm_ss));
	}

	/** ================================= 日期+时间 数据解析处理 ================================= */
	/**
	 * 解析 日期时间字符串 为 LocalDateTime 类型的时间数据
	 *
	 * @param dateTimeStr
	 * @return
	 */
	public static LocalDateTime parseDateTime(String dateTimeStr) {
		try {
			dateTimeStr = correctDateTimeStr(dateTimeStr);
			if (dateTimeStr.contains("-") && dateTimeStr.contains(":")) {
				return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern(CustomDateUtils.yyyy_MM_dd_HH_mm_ss));
			} else {
				if (dateTimeStr.contains("-")) {
					return parseDateTimeByDate(dateTimeStr);
				} else if (dateTimeStr.contains(":")) {
					return parseDateTimeByTime(dateTimeStr);
				}
			}
		} catch (Exception ex) {
			s_logger.error(String.format(LogInfoUtils.HAS_DATA_TMPL, Thread.currentThread().getStackTrace()[1].getMethodName(), dateTimeStr));
		}

		return CustomDateUtils.getMinimalDateTime();
	}

	/**
	 * 解析 日期字符串 + 时间字符串 为 LocalDateTime 类型的时间数据
	 *
	 * @param dateStr
	 * @param timeStr
	 * @return
	 */
	public static LocalDateTime parseDateTime(String dateStr, String timeStr) {
		return LocalDateTime.of(parseDate(dateStr), parseTime(timeStr));
	}

	/**
	 * 解析 日期字符串 为 LocalDateTime 类型的时间数据
	 *
	 * @param dateStr
	 * @return
	 */
	public static LocalDateTime parseDateTimeByDate(String dateStr) {
		return parseDate(dateStr).atStartOfDay();
	}

	/**
	 * 解析 时间字符串 为 LocalDateTime 类型的时间数据，日期为当天
	 *
	 * @param timeStr
	 * @return
	 */
	public static LocalDateTime parseDateTimeByTime(String timeStr) {
		return LocalDateTime.of(LocalDate.now(), parseTime(timeStr));
	}

	/** ================================= 数据解析处理 帮助方法 ================================= */
	/**
	 * 处理日期字符串为标准格式修正问题
	 *
	 * @param dateStr
	 * @return
	 */
	public static String correctDateStr(String dateStr) {
		if (dateStr.contains("/")) {
			dateStr = dateStr.replace("/", "-");
		}

		if (dateStr.length() == 10) {
			return dateStr;
		} else {
			StringBuilder sBuilder = new StringBuilder();

			String[] arrDate = StringUtils.split(dateStr, "-");
			if (arrDate.length == 3) {
				sBuilder.append(addPreZeroToStr(arrDate[0]));
				sBuilder.append("-");
				sBuilder.append(addPreZeroToStr(arrDate[1]));
				sBuilder.append("-");
				sBuilder.append(addPreZeroToStr(arrDate[2]));
			} else {
				sBuilder.append(dateStr);
			}

			return sBuilder.toString();
		}
	}

	/**
	 * 处理时间字符串为标准格式修正问题
	 *
	 * @param timeStr
	 * @return
	 */
	public static String correctTimeStr(String timeStr) {
		if (timeStr.length() == 8 && timeStr.contains(":")) {
			return timeStr;
		} else {
			StringBuilder sBuilder = new StringBuilder();

			String[] arrTime = StringUtils.split(timeStr, ":");
			if (arrTime.length == 3) {
				sBuilder.append(addPreZeroToStr(arrTime[0]));
				sBuilder.append(":");
				sBuilder.append(addPreZeroToStr(arrTime[1]));
				sBuilder.append(":");
				sBuilder.append(addPreZeroToStr(arrTime[2]));
			} else if (arrTime.length == 2) {
				sBuilder.append(addPreZeroToStr(arrTime[0]));
				sBuilder.append(":");
				sBuilder.append(addPreZeroToStr(arrTime[1]));
				sBuilder.append(":00");
			} else {
				sBuilder.append(timeStr);
			}

			return sBuilder.toString();
		}
	}

	/**
	 * 处理日期+时间字符串为标准格式修正问题
	 *
	 * @param dateTimeStr
	 * @return
	 */
	public static String correctDateTimeStr(String dateTimeStr) {
		if (dateTimeStr.contains("T")) {
			dateTimeStr = dateTimeStr.replace("T", " ");
		}

		String[] arrDateTime = StringUtils.split(dateTimeStr, " ");
		if (arrDateTime.length == 2) {
			StringBuilder sBuilder = new StringBuilder();
			sBuilder.append(correctDateStr(arrDateTime[0]));
			sBuilder.append(" ");
			sBuilder.append(correctTimeStr(arrDateTime[1]));

			return sBuilder.toString();
		}

		return dateTimeStr;
	}

	/**
	 * 处理日期、时间某些位置字符串为一位数字的问题
	 *
	 * @param str
	 * @return
	 */
	public static String addPreZeroToStr(String str) {
		if (str.length() == 1) {
			return "0" + str;
		}

		return str;
	}
}
