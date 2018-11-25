package com.trade.common.tradeutil.klineutil;

import com.trade.common.infrastructure.util.collection.CustomListMathUtils;
import com.trade.model.tradecore.kline.DayKLine;
import com.trade.model.tradecore.minutequote.MinuteQuote;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class KLineUtils {

	/**
	 * 根据分钟线列表，计算每日K线数据
	 *
	 * @param minuteQuotes
	 * @return
	 */
	public static DayKLine calcDayKLine(List<MinuteQuote> minuteQuotes) {
		DayKLine result = null;

		if (minuteQuotes.size() >= 390) {
			result = new DayKLine();
			List<Float> prices = minuteQuotes.stream().map(x -> x.getPrice()).collect(Collectors.toList());
			Collections.sort(prices);
			float high = prices.get(prices.size() - 1);
			float low = prices.get(0);
			long volume = CustomListMathUtils.calListLongTotal(minuteQuotes.stream().map(x -> x.getVolume()).collect(Collectors.toList()));

			result.setVolume(volume * 2);
			result.setHigh(high);
			result.setLow(low);
		}

		return result;
	}

	/**
	 * 计算计划当天买入点和卖出点距离开盘价的差价
	 *
	 * @param predayKLine
	 * @param plannedDeviationRate
	 * @return
	 */
	public static int calDeviationAmount(DayKLine predayKLine, float plannedDeviationRate) {
		return (int) ((predayKLine.getHigh() - predayKLine.getLow()) * plannedDeviationRate);
	}

	/**
	 * 计算当前交易日期的前一个交易日的日K线数据
	 *
	 * @param dayKLines
	 * @param currentTradeDate
	 * @return
	 */
	public static DayKLine calcPrevDayKLine(List<DayKLine> dayKLines, LocalDate currentTradeDate) {
		for (int i = 1; i < 10; i++) {
			LocalDate prevTradeDate = currentTradeDate.minusDays(i);
			DayOfWeek prevDayOfWeek = prevTradeDate.getDayOfWeek();
			if (prevDayOfWeek == DayOfWeek.SATURDAY || prevDayOfWeek == DayOfWeek.SUNDAY) {
				continue;
			}

			List<DayKLine> prevDayKLines = dayKLines.stream().filter(x -> x.getDate().equals(prevTradeDate)).collect(Collectors.toList());
			if (prevDayKLines.size() == 1) {
				return prevDayKLines.get(0);
			}
		}

		return null;
	}
}
