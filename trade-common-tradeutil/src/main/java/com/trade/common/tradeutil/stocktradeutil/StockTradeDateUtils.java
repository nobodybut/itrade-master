package com.trade.common.tradeutil.stocktradeutil;

import com.google.common.collect.Lists;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

public class StockTradeDateUtils {

	/**
	 * 计算前N个交易日期列表
	 *
	 * @param localDate
	 * @param tradeDayCount
	 * @return
	 */
	public static List<LocalDate> calcPrevTradeDates(LocalDate localDate, int tradeDayCount) {
		return calcStockTradeDates(localDate, tradeDayCount, true);
	}

	/**
	 * 计算前一个交易日期
	 *
	 * @param localDate
	 * @return
	 */
	public static LocalDate calcPrevTradeDate(LocalDate localDate) {
		List<LocalDate> tradeDates = calcStockTradeDates(localDate, 1, true);
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
		return calcStockTradeDates(localDate, tradeDayCount, false);
	}

	/**
	 * 计算后一个交易日期
	 *
	 * @param localDate
	 * @return
	 */
	public static LocalDate calcNextTradeDate(LocalDate localDate) {
		List<LocalDate> tradeDates = calcStockTradeDates(localDate, 1, false);
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
	private static List<LocalDate> calcStockTradeDates(LocalDate localDate, int tradeDayCount, boolean isPrev) {
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
