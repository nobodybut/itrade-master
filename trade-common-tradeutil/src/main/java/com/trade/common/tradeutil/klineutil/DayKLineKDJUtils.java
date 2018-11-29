package com.trade.common.tradeutil.klineutil;

import com.google.common.base.Strings;
import com.trade.common.infrastructure.util.json.CustomJSONUtils;
import com.trade.common.infrastructure.util.math.CustomMathUtils;
import com.trade.common.tradeutil.techindicatorsutil.KDJUtils;
import com.trade.model.tradecore.kline.DayKLine;
import com.trade.model.tradecore.techindicators.KDJ;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class DayKLineKDJUtils {

	/**
	 * 集中计算日K线 KDJ 技术指标数据、昨日收盘价、涨跌幅
	 *
	 * @param allKLines
	 */
	public static void calcAndFillDayKLineKDJ(List<DayKLine> allKLines) {
		double yesterdayK = 50; // 昨日K值
		double yesterdayD = 50; // 昨日D值

		for (int i = KDJUtils.PREV_DAYS_N + 1; i <= allKLines.size() + 1; i++) {
			// 计算跳过的数量
			int skipCount = i - KDJUtils.PREV_DAYS_N - 1;
			if (skipCount < 0) {
				continue;
			}

			// 获取当前日期和前 KDJUtils.PREV_DAYS_N 天的 K线数据
			List<DayKLine> kLines = allKLines.stream().skip(skipCount).limit(KDJUtils.PREV_DAYS_N).collect(Collectors.toList());
			if (kLines.size() != KDJUtils.PREV_DAYS_N) {
				return;
			}

			// 排序及获取当天的K线数据
			kLines.sort(Comparator.comparing(DayKLine::getDate, Comparator.reverseOrder()));
			DayKLine currentDayKLine = kLines.get(0);

			// 处理前一天的收盘价数据、K/D 数据
			DayKLine prevDayKLine = kLines.get(1);
			currentDayKLine.setLastClose(prevDayKLine.getClose());
			if (!Strings.isNullOrEmpty(prevDayKLine.getKdjJson())) {
				KDJ prevDayKdj = KDJUtils.parseKDJ(prevDayKLine.getKdjJson());
				if (prevDayKdj != null) {
					yesterdayK = prevDayKdj.getK();
					yesterdayD = prevDayKdj.getD();
				}
			}

			// 计算 当日收盘价、N天内最低价（应对比当日的最低价）、N天内最高价（应对比当日的最高价），并计算 KDJ 结果，并写入 todayDayKLine 内
			float closePrice = currentDayKLine.getClose();
			float minPriceNDay = calcMinPriceNDay(kLines);
			float maxPriceNDay = calcMaxPriceNDay(kLines);
			KDJ kdj = KDJUtils.calcKDJ(closePrice, minPriceNDay, maxPriceNDay, yesterdayK, yesterdayD);
			currentDayKLine.setKdjJson(CustomJSONUtils.toJSONString(kdj));

			// 计算涨跌幅（当前最新成交价（或 收盘价）-开盘参考价)÷开盘参考价×100%）
			float changeRate = CustomMathUtils.round(((currentDayKLine.getClose() - currentDayKLine.getLastClose()) / currentDayKLine.getLastClose()) * 100, 3);
			currentDayKLine.setChangeRate(changeRate);

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
	private static float calcMinPriceNDay(List<DayKLine> kLines) {
		List<Float> prices = kLines.stream().map(x -> x.getLow()).collect(Collectors.toList());
		Collections.sort(prices);
		return prices.get(0);
	}

	/**
	 * 计算 N 天内最高价
	 *
	 * @param kLines
	 * @return
	 */
	private static float calcMaxPriceNDay(List<DayKLine> kLines) {
		List<Float> prices = kLines.stream().map(x -> x.getHigh()).collect(Collectors.toList());
		Collections.sort(prices);
		return prices.get(prices.size() - 1);
	}
}
