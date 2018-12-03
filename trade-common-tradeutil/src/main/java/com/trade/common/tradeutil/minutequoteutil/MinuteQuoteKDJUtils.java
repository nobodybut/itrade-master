package com.trade.common.tradeutil.minutequoteutil;

import com.trade.common.infrastructure.util.json.CustomJSONUtils;
import com.trade.common.tradeutil.techindicatorsutil.KDJUtils;
import com.trade.model.tradecore.minutequote.MinuteQuote;
import com.trade.model.tradecore.techindicators.KDJ;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MinuteQuoteKDJUtils {

	/**
	 * 集中计算分钟线 KDJ 技术指标数据、昨日收盘价、涨跌幅
	 *
	 * @param allMinuteQuotes
	 */
	public static void calcAndFillMinuteQuoteKDJ(List<MinuteQuote> allMinuteQuotes) {
		double yesterdayK = 50; // 昨日K值
		double yesterdayD = 50; // 昨日D值

		for (int i = KDJUtils.PREV_MINUTES_N + 1; i <= allMinuteQuotes.size() + 1; i++) {
			// 计算跳过的数量
			int skipCount = i - KDJUtils.PREV_MINUTES_N - 1;
			if (skipCount < 0) {
				continue;
			}

			// 获取当前日期和前 KDJUtils.PREV_MINUTES_N 天的 K线数据
			List<MinuteQuote> minuteQuotes = allMinuteQuotes.stream().skip(skipCount).limit(KDJUtils.PREV_MINUTES_N).collect(Collectors.toList());
			if (minuteQuotes.size() != KDJUtils.PREV_MINUTES_N) {
				return;
			}

			// 排序及获取当天的K线数据
			minuteQuotes.sort(Comparator.comparing(MinuteQuote::getTime, Comparator.reverseOrder()));
			MinuteQuote currentMinuteQuote = minuteQuotes.get(0);

			// 计算 当日收盘价、N天内最低价（应对比当日的最低价）、N天内最高价（应对比当日的最高价），并计算 KDJ 结果，并写入 todayDayKLine 内
			float closePrice = currentMinuteQuote.getPrice();
			float minPriceNDay = calcMinPriceNDay(minuteQuotes);
			float maxPriceNDay = calcMaxPriceNDay(minuteQuotes);
			KDJ kdj = KDJUtils.calcKDJ(closePrice, minPriceNDay, maxPriceNDay, yesterdayK, yesterdayD);
			currentMinuteQuote.setKdjJson(CustomJSONUtils.toJSONString(kdj));

			// 重新赋值 K、D 数据
			yesterdayK = kdj.getK();
			yesterdayD = kdj.getD();
		}
	}

	/**
	 * 计算 N 天内最低价
	 *
	 * @param kLines
	 * @return
	 */
	private static float calcMinPriceNDay(List<MinuteQuote> kLines) {
		List<Float> prices = kLines.stream().map(x -> x.getPrice()).collect(Collectors.toList());
		Collections.sort(prices);
		return prices.get(0);
	}

	/**
	 * 计算 N 天内最高价
	 *
	 * @param kLines
	 * @return
	 */
	private static float calcMaxPriceNDay(List<MinuteQuote> kLines) {
		List<Float> prices = kLines.stream().map(x -> x.getPrice()).collect(Collectors.toList());
		Collections.sort(prices);
		return prices.get(prices.size() - 1);
	}
}
