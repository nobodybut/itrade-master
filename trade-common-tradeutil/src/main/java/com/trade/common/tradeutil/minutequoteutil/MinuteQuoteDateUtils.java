package com.trade.common.tradeutil.minutequoteutil;

import com.google.common.collect.Lists;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

public class MinuteQuoteDateUtils {

	/**
	 * 计算检查分钟线数据日期列表
	 *
	 * @param localDate
	 * @param checkMinuteQuoteDays
	 * @return
	 */
	public static List<LocalDate> calcCheckMinuteQuoteDates(LocalDate localDate, int checkMinuteQuoteDays) {
		List<LocalDate> result = Lists.newArrayList();

		for (int i = 1; i <= checkMinuteQuoteDays + 2; i++) {
			LocalDate date = localDate.minusDays(i);
			if (date.getDayOfWeek() != DayOfWeek.SATURDAY && date.getDayOfWeek() != DayOfWeek.SUNDAY) {
				result.add(date);
			}

			if (result.size() == checkMinuteQuoteDays) {
				break;
			}
		}

		return result;
	}
}
