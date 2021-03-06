package com.trade.common.tradeutil.quanttradeutil;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.trade.common.infrastructure.util.date.CustomDateUtils;

import java.time.*;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class TradeDateUtils {

	// 常量定义
	private static final ZoneId ZONE_US_EASTERN = ZoneId.of("US/Eastern");
	private static final ZoneId ZONE_ASIA_SHANGHAI = ZoneId.of("Asia/Shanghai");
	public static final LocalTime US_TRADE_DAY_BEFORE_OPEN_TIME = LocalTime.of(8, 30); // 美股每天的开始交易之前1小时的时间
	public static final LocalTime US_TRADE_DAY_OPEN_TIME = LocalTime.of(9, 30); // 美股每天的开始交易时间
	public static final LocalTime US_TRADE_DAY_CLOSE_TIME = LocalTime.of(16, 0); // 美股每天的结束交易时间
	public static final Set<LocalDate> US_NO_TRADE_DATES = Sets.newHashSet(
			LocalDate.of(2018, 1, 1),
			LocalDate.of(2018, 1, 15),
			LocalDate.of(2018, 2, 19),
			LocalDate.of(2018, 3, 30),
			LocalDate.of(2018, 5, 28),
			LocalDate.of(2018, 7, 4),
			LocalDate.of(2018, 9, 3),
			LocalDate.of(2018, 11, 22),
			LocalDate.of(2018, 12, 5),
			LocalDate.of(2018, 12, 25),
			LocalDate.of(2019, 1, 1));

	/**
	 * 获取美国当前日期
	 *
	 * @return
	 */
	public static LocalDate getUsCurrentDate() {
		return getUsDateTime(LocalDateTime.now()).toLocalDate();
	}

	/**
	 * 获取美国当前时间
	 *
	 * @return
	 */
	public static LocalTime getUsCurrentTime() {
		return getUsDateTime(LocalDateTime.now()).toLocalTime();
	}

	/**
	 * 获取美国当前日期时间
	 *
	 * @return
	 */
	public static LocalDateTime getUsCurrentDateTime() {
		return getUsDateTime(LocalDateTime.now());
	}

	/**
	 * 将国内日期转换为美国的日期时间
	 *
	 * @param dateTime
	 * @return
	 */
	private static LocalDateTime getUsDateTime(LocalDateTime dateTime) {
		int usDiffHours = calUsDiffHours(dateTime);
		return dateTime.plusHours(usDiffHours);
	}

	/**
	 * 计算服务器时间与美国时差
	 *
	 * @param dateTime
	 * @return
	 */
	public static int calUsDiffHours(LocalDateTime dateTime) {
		LocalDateTime startOfDay = dateTime.toLocalDate().atStartOfDay();
		return (ZonedDateTime.of(startOfDay, ZONE_US_EASTERN).getOffset().getTotalSeconds() - ZonedDateTime.of(startOfDay, ZONE_ASIA_SHANGHAI).getOffset().getTotalSeconds()) / 3600;
	}

	/**
	 * 是否是美股夏令时
	 *
	 * @return
	 */
	public static boolean isUsSummerTime() {
		return calUsDiffHours(LocalDateTime.now()) == -12;
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

		for (int i = 1; i <= tradeDayCount + 7; i++) {
			LocalDate tradeDate = isPrev ? localDate.minusDays(i) : localDate.plusDays(i);
			if (isUsTradeDate(tradeDate)) {
				result.add(tradeDate);
			}

			if (result.size() == tradeDayCount) {
				break;
			}
		}

		return result;
	}

	/**
	 * 判断指定日期是否为美股的交易日期
	 *
	 * @param tradeDate
	 * @return
	 */
	public static boolean isUsTradeDate(LocalDate tradeDate) {
		// 如果是周末，则不在交易日期
		DayOfWeek dayOfWeek = tradeDate.getDayOfWeek();
		if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
			return false;
		}

		// 处理其他非交易日期
		if (US_NO_TRADE_DATES.contains(tradeDate)) {
			return false;
		}

		return true;
	}

	/**
	 * 判断当前时间是否为美股的交易时间
	 *
	 * @return
	 */
	public static boolean isUsTradeTime() {
		return isUsTradeTime(getUsCurrentTime());
	}

	/**
	 * 判断指定时间是否为美股的交易时间
	 *
	 * @param tradeTime
	 * @return
	 */
	public static boolean isUsTradeTime(LocalTime tradeTime) {
		if ((tradeTime.equals(US_TRADE_DAY_OPEN_TIME) || tradeTime.isAfter(US_TRADE_DAY_OPEN_TIME))
				&& (tradeTime.equals(US_TRADE_DAY_CLOSE_TIME) || tradeTime.isBefore(US_TRADE_DAY_CLOSE_TIME))) {
			return true;
		}

		return false;
	}

	/**
	 * 判断当前时间是否为美股开盘前时间（08:30 - 09:30）
	 *
	 * @return
	 */
	public static boolean isBeforeUsTradeOpenTime() {
		LocalTime currentTime = getUsCurrentTime();
		return currentTime.isAfter(US_TRADE_DAY_BEFORE_OPEN_TIME) && currentTime.isBefore(US_TRADE_DAY_OPEN_TIME);
	}

	/**
	 * 判断当前时间是否为美股收盘后时间（16:00 - 08:30(+1)）
	 *
	 * @return
	 */
	public static boolean isAfterUsTradeCloseTime() {
		LocalTime currentTime = getUsCurrentTime();
		return !isUsTradeTime(currentTime) && !isBeforeUsTradeOpenTime();
	}

	/**
	 * 将 timeMills 转换为 localDateTime（需传入时差）
	 *
	 * @param timeMills
	 * @param usDiffHours
	 * @return
	 */
	public static LocalDateTime getDateTimeByTimeMills(long timeMills, int usDiffHours) {
		return CustomDateUtils.dateToLocalDateTime(new Date(timeMills)).plusHours(usDiffHours);
	}

	/**
	 * 将 timeMills 转换为 localDateTime（不传入时差）
	 *
	 * @param timeMills
	 * @return
	 */
	public static LocalDateTime getDateTimeByTimeMills(long timeMills) {
		int usDiffHours = calUsDiffHours(LocalDateTime.now());
		return CustomDateUtils.dateToLocalDateTime(new Date(timeMills)).plusHours(usDiffHours);
	}
}
