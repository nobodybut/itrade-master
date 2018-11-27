package com.trade.common.tradeutil.klineutil;

import com.google.common.collect.Lists;
import com.trade.common.tradeutil.quanttradeutil.TradeDateUtils;
import com.trade.model.tradecore.kline.DayKLine;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class DayKLineUtils {

	/**
	 * 计算前1个交易日的日K线数据
	 *
	 * @param dayKLines
	 * @param currentTradeDate
	 * @return
	 */
	public static DayKLine calcPrevDayKLine(List<DayKLine> dayKLines, LocalDate currentTradeDate) {
		for (int i = 1; i < 10; i++) {
			LocalDate prevTradeDate = currentTradeDate.minusDays(i);
			if (!TradeDateUtils.isUsTradeDay(prevTradeDate)) {
				continue;
			}

			List<DayKLine> prevDayKLines = dayKLines.stream().filter(x -> x.getDate().equals(prevTradeDate)).collect(Collectors.toList());
			if (prevDayKLines.size() == 1) {
				return prevDayKLines.get(0);
			}
		}

		return null;
	}

	/**
	 * 计算前N个交易日的日K线数据列表
	 *
	 * @param dayKLines
	 * @param currentTradeDate
	 * @param nDays
	 * @return
	 */
	public static List<DayKLine> calcPrevNDaysKLines(List<DayKLine> dayKLines, LocalDate currentTradeDate, int nDays) {
		List<DayKLine> result = Lists.newArrayList();

		for (int i = 1; i < nDays * 2; i++) {
			LocalDate tradeDate = currentTradeDate.minusDays(i);
			if (!TradeDateUtils.isUsTradeDay(tradeDate)) {
				continue;
			}

			List<DayKLine> prevDayKLines = dayKLines.stream().filter(x -> x.getDate().equals(tradeDate)).collect(Collectors.toList());
			if (prevDayKLines.size() == 1) {
				result.add(prevDayKLines.get(0));
			}

			if (result.size() == nDays) {
				break;
			}
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
}
