package com.trade.common.tradeutil.stockutil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class TradeDateUtils {

	// 常量定义
	private static final ZoneId ZONE_US_EASTERN = ZoneId.of("US/Eastern");
	private static final ZoneId ZONE_ASIA_SHANGHAI = ZoneId.of("Asia/Shanghai");

	/**
	 * 获取美国当前日期时间
	 *
	 * @return
	 */
	public static LocalDateTime getUSCurrentDateTime() {
		return getUSDateTime(LocalDateTime.now());
	}

	/**
	 * 获取美国当前日期
	 *
	 * @return
	 */
	public static LocalDate getUSCurrentDate() {
		return getUSDateTime(LocalDateTime.now()).toLocalDate();
	}

	/**
	 * 将国内日期转换为美国的日期时间
	 *
	 * @param dateTime
	 * @return
	 */
	public static LocalDateTime getUSDateTime(LocalDateTime dateTime) {
		LocalDateTime startOfDay = dateTime.toLocalDate().atStartOfDay();
		long diffSeconds = ZonedDateTime.of(startOfDay, ZONE_US_EASTERN).getOffset().getTotalSeconds() - ZonedDateTime.of(startOfDay, ZONE_ASIA_SHANGHAI).getOffset().getTotalSeconds();
		return dateTime.plusSeconds(diffSeconds);
	}
}
