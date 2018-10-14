package com.itrade.common.infrastructure.util.date;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class CustomDateFormatUtils {

	/** ================================= 日期 格式化处理 ================================= */
	/**
	 * 处理 日期 格式化为字符串（自定义 pattern）
	 *
	 * @param localDate
	 * @param pattern
	 * @return
	 */
	public static String formatDate(LocalDate localDate, String pattern) {
		return localDate.format(DateTimeFormatter.ofPattern(pattern));
	}

	/**
	 * 处理 日期 格式化为字符串（yyyy-MM-dd）
	 *
	 * @param localDate
	 * @return
	 */
	public static String formatDate(LocalDate localDate) {
		return localDate.format(DateTimeFormatter.ofPattern(CustomDateUtils.yyyy_MM_dd));
	}

	/**
	 * 处理 日期 格式化为字符串（yyyy/MM/dd）
	 *
	 * @param localDate
	 * @return
	 */
	public static String formatDateEn(LocalDate localDate) {
		return localDate.format(DateTimeFormatter.ofPattern(CustomDateUtils.yyyy_MM_dd_en));
	}

	/**
	 * 处理 日期 格式化为字符串（中文：年月日）
	 *
	 * @param localDate
	 * @return
	 */
	public static String formatDate_chinese(LocalDate localDate) {
		return String.format("%s年%s月%s日", localDate.getYear(), localDate.getMonthValue(), localDate.getDayOfMonth());
	}

	/**
	 * 处理 日期 格式化为字符串（中文：月日）
	 *
	 * @param localDate
	 * @return
	 */
	public static String formatDate_chinese_monthDay(LocalDate localDate) {
		return String.format("%s月%s日", localDate.getMonthValue(), localDate.getDayOfMonth());
	}

	/** ================================= 时间 格式化处理 ================================= */
	/**
	 * 处理 时间 格式化为字符串（自定义 pattern）
	 *
	 * @param localTime
	 * @param pattern
	 * @return
	 */
	public static String formatTime(LocalTime localTime, String pattern) {
		return localTime.format(DateTimeFormatter.ofPattern(pattern));
	}

	/**
	 * 处理 时间 格式化为字符串（HH:mm:ss）
	 *
	 * @param localTime
	 * @return
	 */
	public static String formatTime(LocalTime localTime) {
		return localTime.format(DateTimeFormatter.ofPattern(CustomDateUtils.HH_mm_ss));
	}

	/**
	 * 处理 时间 格式化为字符串（HH:mm）
	 *
	 * @param localTime
	 * @return
	 */
	public static String formatTime_HHmm(LocalTime localTime) {
		return localTime.format(DateTimeFormatter.ofPattern(CustomDateUtils.HH_mm));
	}

	/**
	 * 处理 时间 格式化为字符串（HH:mm:ss,SSS）
	 *
	 * @param localTime
	 * @return
	 */
	public static String formatTime_HHmmssSSS(LocalTime localTime) {
		return localTime.format(DateTimeFormatter.ofPattern(CustomDateUtils.HH_mm_ss_SSS));
	}

	/** ================================= 日期+时间 格式化处理 ================================= */
	/**
	 * 处理 日期+时间 格式化为字符串（自定义 pattern）
	 *
	 * @param localDateTime
	 * @param pattern
	 * @return
	 */
	public static String formatDateTime(LocalDateTime localDateTime, String pattern) {
		return localDateTime.format(DateTimeFormatter.ofPattern(pattern));
	}

	/**
	 * 处理 日期+时间 格式化为字符串（yyyy-MM-dd HH:mm:ss）
	 *
	 * @param localDateTime
	 * @return
	 */
	public static String formatDateTime(LocalDateTime localDateTime) {
		return localDateTime.format(DateTimeFormatter.ofPattern(CustomDateUtils.yyyy_MM_dd_HH_mm_ss));
	}

	/**
	 * 处理 日期+时间 格式化为字符串（yyyy-MM-dd HH:mm）
	 *
	 * @param localDateTime
	 * @return
	 */
	public static String formatDateTime_yyyyMMdd_HHmm(LocalDateTime localDateTime) {
		return localDateTime.format(DateTimeFormatter.ofPattern(CustomDateUtils.yyyy_MM_dd_HH_mm));
	}

	/**
	 * 处理 日期+时间 格式化为字符串（HH:mm）
	 *
	 * @param localDateTime
	 * @return
	 */
	public static String formatDateTime_HHmm(LocalDateTime localDateTime) {
		return localDateTime.format(DateTimeFormatter.ofPattern(CustomDateUtils.HH_mm));
	}

	/**
	 * 处理 日期+时间 格式化为字符串（HH:mm:ss）
	 *
	 * @param localDateTime
	 * @return
	 */
	public static String formatDateTime_HHmmss(LocalDateTime localDateTime) {
		return localDateTime.format(DateTimeFormatter.ofPattern(CustomDateUtils.HH_mm_ss));
	}
}
