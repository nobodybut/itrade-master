package com.trade.common.tradeutil.techindicatorsutil;

import com.trade.model.tradecore.techindicators.KDJ;

/**
 * 技术指标：KDJ 算法实现
 */
public class KDJUtils {

	/**
	 * 计算KDJ（整体返回）（返回值保留4位小数）
	 *
	 * @param closePrice   当日收盘价
	 * @param minPriceNDay N天内最低价（应对比当日的最低价）
	 * @param maxPriceNDay N天内最高价（应对比当日的最高价）
	 * @param yesterdayK   昨日K值
	 * @param yesterdayD   昨日D值
	 * @return
	 */
	public static KDJ calcKDJ(double closePrice, double minPriceNDay, double maxPriceNDay, double yesterdayK, double yesterdayD) {
		double v_RSV = calcRSV(closePrice, minPriceNDay, maxPriceNDay);
		double v_K = calcK(v_RSV, yesterdayK);
		double v_D = calcD(v_K, yesterdayD);
		double v_J = calcJ(v_K, v_D);

		return new KDJ(TechIndicatorsUtils.round(v_K, 4), TechIndicatorsUtils.round(v_D, 4), TechIndicatorsUtils.round(v_J, 4));
	}

	/**
	 * 计算RSV：未成熟随机值
	 * <p>
	 * RSV = ((当日收盘价 - N天内最低价) / (N天内最高价 - N天内最低价)) * 100
	 * <p>
	 * N天：一般指9天
	 *
	 * @param closePrice   当日收盘价
	 * @param minPriceNDay N天内最低价（应对比当日的最低价）
	 * @param maxPriceNDay N天内最高价（应对比当日的最高价）
	 * @return
	 */
	private static double calcRSV(double closePrice, double minPriceNDay, double maxPriceNDay) {
		return TechIndicatorsUtils.division(closePrice - minPriceNDay, maxPriceNDay - minPriceNDay) * 100;
	}

	/**
	 * 逆向计算N天内最低价
	 *
	 * @param closePrice   当日收盘价
	 * @param maxPriceNDay N天内最高价
	 * @param rsv          未成熟随机值
	 * @return
	 */
	private static double inverseCalcMinPriceNDay(double closePrice, double maxPriceNDay, double rsv) {
		return ((rsv / 100) * maxPriceNDay - closePrice) / ((rsv / 100) - 1);
	}

	/**
	 * 逆向计算RSV
	 *
	 * @param k          今日K值
	 * @param yesterdayK 昨日K值
	 * @return
	 */
	private static double inverseCalcRSV(double k, double yesterdayK) {
		return (k - (yesterdayK * 2 / 3)) * 3;
	}

	/**
	 * 计算K值
	 * <p>
	 * 今日K值 = (昨日K值 * 2 / 3) + (今日RSV / 3)
	 *
	 * @param rsv        未成熟随机值
	 * @param yesterdayK 昨日K值
	 * @return
	 */
	private static double calcK(double rsv, double yesterdayK) {
		return (yesterdayK * 2 / 3) + (rsv / 3);
	}

	/**
	 * 计算D值
	 * <p>
	 * 今日D值 = (昨日D值 * 2 / 3) + (今日K值 / 3)
	 *
	 * @param k          今日K值
	 * @param yesterdayD 昨日D值
	 * @return
	 */
	private static double calcD(double k, double yesterdayD) {
		return (yesterdayD * 2 / 3) + (k / 3);
	}

	/**
	 * 计算J值
	 * <p>
	 * J = 3 * K - 2 * D
	 *
	 * @param k 今日K值
	 * @param d 今日D值
	 * @return
	 */
	private static double calcJ(double k, double d) {
		return 3 * k - 2 * d;
	}
}
