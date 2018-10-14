package com.itrade.common.infrastructure.util.logger;

import com.itrade.common.infrastructure.util.date.CustomDateFormatUtils;

import java.time.LocalDateTime;

public class LogInfoUtils {

	public static final String NO_DATA_TMPL = "%s, ";
	public static final String HAS_DATA_TMPL = "%s, [%s]";

	/**
	 * 生成任务开始执行日志信息
	 *
	 * @return
	 */
	public static String createPerformStartInfo() {
		return createPerformStartInfo("");
	}

	/**
	 * 生成任务开始执行日志信息
	 *
	 * @param methodName
	 * @return
	 */
	public static String createPerformStartInfo(String methodName) {
		return String.format("%s 开始执行，开始时间: %s。", methodName, CustomDateFormatUtils.formatDateTime(LocalDateTime.now()));
	}

	/**
	 * 生成任务执行成功日志信息
	 *
	 * @param startTimeMillis
	 * @return
	 */
	public static String createPerformSuccessInfo(long startTimeMillis) {
		return createPerformSuccessInfo("", startTimeMillis);
	}

	/**
	 * 生成任务执行成功日志信息
	 *
	 * @param methodName
	 * @param startTimeMillis
	 * @return
	 */
	public static String createPerformSuccessInfo(String methodName, long startTimeMillis) {
		return String.format("%s 执行成功，总耗时：%s 毫秒，成功时间：%s。", methodName, System.currentTimeMillis() - startTimeMillis, CustomDateFormatUtils.formatDateTime(LocalDateTime.now()));
	}

	/**
	 * 生成任务执行成功日志信息（写入执行成功参考数据）
	 *
	 * @param startTimeMillis
	 * @param logData
	 * @return
	 */
	public static String createPerformSuccessInfo(long startTimeMillis, String logData) {
		return createPerformSuccessInfo("", startTimeMillis, logData);
	}

	/**
	 * 生成任务执行成功日志信息（写入执行成功参考数据）
	 *
	 * @param methodName
	 * @param startTimeMillis
	 * @param logData
	 * @return
	 */
	public static String createPerformSuccessInfo(String methodName, long startTimeMillis, String logData) {
		return String.format("%s 执行成功，总耗时：%s 毫秒，成功时间：%s，参考数据：%s。", methodName, System.currentTimeMillis() - startTimeMillis, CustomDateFormatUtils.formatDateTime(LocalDateTime.now()), logData);
	}

	/**
	 * 生成任务执行失败日志信息
	 *
	 * @param startTimeMillis
	 * @return
	 */
	public static String createPerformErrorInfo(long startTimeMillis) {
		return createPerformErrorInfo("", startTimeMillis);
	}

	/**
	 * 生成任务执行失败日志信息
	 *
	 * @param methodName
	 * @param startTimeMillis
	 * @return
	 */
	public static String createPerformErrorInfo(String methodName, long startTimeMillis) {
		return String.format("%s 执行失败，总耗时：%s 毫秒，失败时间：%s。", methodName, System.currentTimeMillis() - startTimeMillis, CustomDateFormatUtils.formatDateTime(LocalDateTime.now()));
	}
}