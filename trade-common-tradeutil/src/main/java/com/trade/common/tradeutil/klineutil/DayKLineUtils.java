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
			if (!TradeDateUtils.isUsTradeDate(prevTradeDate)) {
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
			if (!TradeDateUtils.isUsTradeDate(tradeDate)) {
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
	 * 计算计划当天买入点和卖出点距离开盘价的差价（如果出现昨天的价格对比前天的价格出现缺口，则把这个缺口补到昨天的价格上）
	 *
	 * @param predayKLine
	 * @param prePredayKLine
	 * @param plannedDeviationRate
	 * @return
	 */
	public static int calcDeviationAmount(DayKLine predayKLine, DayKLine prePredayKLine, float plannedDeviationRate) {
		if (predayKLine == null || prePredayKLine == null) {
			return 0;
		}

		// 如果昨天的最高比前天的最低还低，用前天的最低算做昨天的最高
		if (predayKLine.getHigh() < prePredayKLine.getLow()) {
			predayKLine.setHigh(prePredayKLine.getLow());
		}

		// 如果昨天的最低比前天的最高还高，用前天的最高算作昨天的最低
		if (predayKLine.getLow() > prePredayKLine.getHigh()) {
			predayKLine.setLow(prePredayKLine.getHigh());
		}

		// 计算计划当天买入点和卖出点距离开盘价的差价
		return (int) ((predayKLine.getHigh() - predayKLine.getLow()) * plannedDeviationRate);
	}
}
