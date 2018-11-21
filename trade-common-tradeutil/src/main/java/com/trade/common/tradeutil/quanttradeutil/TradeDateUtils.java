package com.trade.common.tradeutil.quanttradeutil;

import com.google.common.collect.Lists;

import java.time.*;
import java.util.List;

public class TradeDateUtils {

	// 常量定义
	private static final ZoneId ZONE_US_EASTERN = ZoneId.of("US/Eastern");
	private static final ZoneId ZONE_ASIA_SHANGHAI = ZoneId.of("Asia/Shanghai");
	public static final LocalTime US_TRADE_DAY_START_TIME = LocalTime.of(9, 30); // 美股每天的开始交易时间
	public static final LocalTime US_TRADE_DAY_END_TIME = LocalTime.of(16, 0); // 美股每天的结束交易时间

	/**
	 * 获取美国当前日期
	 *
	 * @return
	 */
	public static LocalDate getUSCurrentDate() {
		return getUSDateTime(LocalDateTime.now()).toLocalDate();
	}

	/**
	 * 获取美国当前时间
	 *
	 * @return
	 */
	public static LocalTime getUSCurrentTime() {
		return getUSDateTime(LocalDateTime.now()).toLocalTime();
	}

	/**
	 * 获取美国当前日期时间
	 *
	 * @return
	 */
	public static LocalDateTime getUSCurrentDateTime() {
		return getUSDateTime(LocalDateTime.now());
	}

	/**
	 * 将国内日期转换为美国的日期时间
	 *
	 * @param dateTime
	 * @return
	 */
	private static LocalDateTime getUSDateTime(LocalDateTime dateTime) {
		int usDiffHours = calUSDiffHours(dateTime);
		return dateTime.plusHours(usDiffHours);
	}

	/**
	 * 计算服务器时间与美国时差
	 *
	 * @param dateTime
	 * @return
	 */
	public static int calUSDiffHours(LocalDateTime dateTime) {
		LocalDateTime startOfDay = dateTime.toLocalDate().atStartOfDay();
		return (ZonedDateTime.of(startOfDay, ZONE_US_EASTERN).getOffset().getTotalSeconds() - ZonedDateTime.of(startOfDay, ZONE_ASIA_SHANGHAI).getOffset().getTotalSeconds()) / 3600;
	}

	/**
	 * 是否是美股夏令时
	 *
	 * @return
	 */
	public static boolean isUSSummerTime() {
		return calUSDiffHours(LocalDateTime.now()) == -12;
	}

	/**
	 * 计算前N个交易日期列表
	 *
	 * @param localDate
	 * @param tradeDayCount
	 * @return
	 */
	public static List<LocalDate> calcPrevTradeDates(LocalDate localDate, int tradeDayCount) {
		return calcQuantTradeDates(localDate, tradeDayCount, true);
	}

	/**
	 * 计算前一个交易日期
	 *
	 * @param localDate
	 * @return
	 */
	public static LocalDate calcPrevTradeDate(LocalDate localDate) {
		List<LocalDate> tradeDates = calcQuantTradeDates(localDate, 1, true);
		if (tradeDates.size() == 1) {
			return tradeDates.get(0);
		}

		return null;
	}

	/**
	 * 计算后N个交易日期列表
	 *
	 * @param localDate
	 * @param tradeDayCount
	 * @return
	 */
	public static List<LocalDate> calcNextTradeDates(LocalDate localDate, int tradeDayCount) {
		return calcQuantTradeDates(localDate, tradeDayCount, false);
	}

	/**
	 * 计算后一个交易日期
	 *
	 * @param localDate
	 * @return
	 */
	public static LocalDate calcNextTradeDate(LocalDate localDate) {
		List<LocalDate> tradeDates = calcQuantTradeDates(localDate, 1, false);
		if (tradeDates.size() == 1) {
			return tradeDates.get(0);
		}

		return null;
	}

	/**
	 * 计算前N个、后N个交易日期列表
	 *
	 * @param localDate
	 * @param tradeDayCount
	 * @param isPrev
	 * @return
	 */
	private static List<LocalDate> calcQuantTradeDates(LocalDate localDate, int tradeDayCount, boolean isPrev) {
		List<LocalDate> result = Lists.newArrayList();

		for (int i = 1; i <= tradeDayCount + 2; i++) {
			LocalDate date = isPrev ? localDate.minusDays(i) : localDate.plusDays(i);
			if (date.getDayOfWeek() != DayOfWeek.SATURDAY && date.getDayOfWeek() != DayOfWeek.SUNDAY) {
				result.add(date);
			}

			if (result.size() == tradeDayCount) {
				break;
			}
		}

		return result;
	}
}
