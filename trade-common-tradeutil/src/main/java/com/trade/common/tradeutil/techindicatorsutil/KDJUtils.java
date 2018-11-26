package com.trade.common.tradeutil.techindicatorsutil;

import com.google.common.collect.Lists;
import com.trade.common.infrastructure.util.json.CustomJSONUtils;
import com.trade.common.infrastructure.util.logger.LogInfoUtils;
import com.trade.model.tradecore.kline.DayKLine;
import com.trade.model.tradecore.techindicators.KDJ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 技术指标：KDJ 算法实现
 */
public class KDJUtils {

	// 日志记录
	private static final Logger LOGGER = LoggerFactory.getLogger(KDJUtils.class);

	// 常量定义
	public static final int PREV_DAYS_N = 9;
	public static final int MAX_TRADE_DAYS = 365;

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

	/**
	 * 根据 kdjJson 解析 KDJ 对象数据
	 *
	 * @param kdjJson
	 * @return
	 */
	public static KDJ parseKDJ(String kdjJson) {
		try {
			KDJ kdj = CustomJSONUtils.parseObject(kdjJson, KDJ.class);
			return kdj;
		} catch (Exception ex) {
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			String logData = String.format("kdjJson=%s", kdjJson);
			LOGGER.error(String.format(LogInfoUtils.HAS_DATA_TMPL, methodName, logData), ex);
		}

		return null;
	}

	/**
	 * 根据 dayKLines 解析 KDJ 对象数据列表
	 *
	 * @param dayKLines
	 * @return
	 */
	public static List<KDJ> parseKDJs(List<DayKLine> dayKLines) {
		List<KDJ> result = Lists.newArrayList();

		for (DayKLine dayKLine : dayKLines) {
			KDJ kdj = parseKDJ(dayKLine.getKdjJson());
			if (kdj != null) {
				result.add(kdj);
			}
		}

		return result;
	}
}
