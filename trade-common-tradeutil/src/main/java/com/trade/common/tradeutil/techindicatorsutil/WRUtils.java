package com.trade.common.tradeutil.techindicatorsutil;

public class WRUtils {

	/**
	 * 计算WR（返回值保留4位小数）
	 * <p>
	 * WR = (Hn - C) / (Hn - Ln) * 100
	 *
	 * @param closePrice   当日收盘价
	 * @param minPriceNDay N天内最低价（应对比当日的最低价）
	 * @param maxPriceNDay N天内最高价（应对比当日的最高价）
	 * @return
	 */
	public static double calcWR(double closePrice, double minPriceNDay, double maxPriceNDay) {
		return TechIndicatorsUtils.round(TechIndicatorsUtils.division((maxPriceNDay - closePrice), (maxPriceNDay - minPriceNDay)) * 100, 4);
	}
}
