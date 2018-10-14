package com.itrade.common.infrastructure.util.date;

import com.itrade.common.infrastructure.util.math.CustomMathUtils;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.Instant;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Slf4j
public class TimeZoneUtils {

	// 相关常量
	public static final String TIME_ZONE_ID_SHANGHAI = "Asia/Shanghai";

	/**
	 * 获取两个时区 之间的时间差
	 *
	 * @param timeZoneIDFrom
	 * @param timeZoneIDTo
	 * @return
	 */
	public static long calMillisBetweenTwoTimeZone(String timeZoneIDFrom, String timeZoneIDTo) {
		LocalDateTime now = LocalDateTime.now();
		ZonedDateTime fromZonedDateTime = now.atZone(ZoneId.of(timeZoneIDFrom));
		ZonedDateTime toZonedDateTime = now.atZone(ZoneId.of(timeZoneIDTo));

		return Duration.between(fromZonedDateTime, toZonedDateTime).toMinutes();
	}

	/**
	 * 获取时区和Asia/Shanghai 之间的时差(小时)
	 *
	 * @param timeZoneID
	 * @return
	 */
	public static double calTimeDifferenceWithBeijing_Hours(String timeZoneID) {
		return CustomMathUtils.calDoubleDivideRound(calMillisBetweenTwoTimeZone(timeZoneID, TIME_ZONE_ID_SHANGHAI), 60, 1);
	}

	/**
	 * 获取时区Instant
	 *
	 * @param dateTime
	 * @param timeZoneID
	 * @return
	 */
	public static Instant getInstantByDateTimeAndZone(String dateTime, String timeZoneID) {
		try {
			ZoneId zoneId = ZoneId.of(timeZoneID);

			LocalDateTime localDateTime = CustomDateParseUtils.parseDateTime(dateTime);
			ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, zoneId);

			return new Instant(zonedDateTime.toInstant().toEpochMilli());
		} catch (Exception ex) {
			log.error(String.format("MethodName=%s ,dateTime=%s ,timeZoneID=%s ,ex=%s", Thread.currentThread().getStackTrace()[1].getMethodName(), dateTime, timeZoneID, ex.toString()));
		}

		return new Instant(LocalDateTime.now());
	}
}
