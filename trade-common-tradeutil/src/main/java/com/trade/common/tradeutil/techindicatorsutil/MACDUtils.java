package com.trade.common.tradeutil.techindicatorsutil;

import com.trade.model.tradecore.techindicators.MACD;

import java.util.ArrayList;
import java.util.List;

/**
 * 技术指标：MACD 算法实现
 */
public class MACDUtils {

	/**
	 * 计算MACD（整体返回）（返回值保留四位小数）
	 *
	 * @param closePrice     今日收盘价
	 * @param yesterdayEMA12 前一日快速移动平均值EMA(12)
	 * @param yesterdayEMA26 前一日慢速移动平均值EMA(26)
	 * @param yesterdayDEA   前一日离差平均值DEA
	 * @return
	 */
	public static MACD calcMACD(double closePrice, double yesterdayEMA12, double yesterdayEMA26, double yesterdayDEA) {
		double v_EMA12 = calcEMA12(closePrice, yesterdayEMA12);
		double v_EMA26 = calcEMA26(closePrice, yesterdayEMA26);
		double v_DIF = calcDIF(v_EMA12, v_EMA26);
		double v_DEA = calcDEA(v_DIF, yesterdayDEA);
		double v_Bar = calcBar(v_DIF, v_DEA);

		return new MACD(TechIndicatorsUtils.round(v_EMA12, 4)
				, TechIndicatorsUtils.round(v_EMA26, 4)
				, TechIndicatorsUtils.round(v_DIF, 4)
				, TechIndicatorsUtils.round(v_DEA, 4)
				, TechIndicatorsUtils.round(v_Bar, 4));
	}

	/**
	 * 逆向计算：快速移动平均值：12日EMA 和 慢速移动平均值：26日EMA
	 * <p>
	 * 此方法不能十分精确的逆向计算，而是采取的穷举模式+趋向模式
	 * <p>
	 * 返回 "下标0：今天价格" 对应的EMA12、EMA26
	 *
	 * @param prices 下标0：今天价格
	 *               下标1：昨天价格
	 *               下标2：前天价格
	 * @param difs   下标0：今天DIF
	 *               下标1：昨天DIF
	 *               下标2：前天DIF
	 */
	public static void inverseCalcEMA_Trend(double[] prices, double[] difs) {
		int v_Day = 0;
		List<MACD> v_MACDs = inverseCalcEMA_Exhaustivity(prices[v_Day], difs[v_Day], difs[v_Day + 1]);

		for (v_Day = 1; v_Day < prices.length - 1; v_Day++) {
			for (int v_Index = v_MACDs.size() - 1; v_Index >= 0; v_Index--) {
				MACD v_MACD = v_MACDs.get(v_Index);

				double v_ThreeDaysAgoEMA12 = TechIndicatorsUtils.round(inverseCalcYesterdayEMA12(prices[v_Day], v_MACD.getEma12()), 4);
				double v_ThreeDaysAgoEMA26 = TechIndicatorsUtils.round(inverseCalcYesterdayEMA26(prices[v_Day], v_MACD.getEma26()), 4);
				double v_ThreeDaysAgoDIF = TechIndicatorsUtils.round(calcDIF(v_ThreeDaysAgoEMA12, v_ThreeDaysAgoEMA26), 2);

				if (v_ThreeDaysAgoDIF != difs[v_Day + 1]) {
					v_MACDs.remove(v_Index);
				} else {
					MACD v_ThreeDaysAgoMACD = new MACD(v_ThreeDaysAgoEMA12, v_ThreeDaysAgoEMA26, 0, 0, 0);
					v_ThreeDaysAgoMACD.setSuperMACD(v_MACD);
					v_MACDs.remove(v_Index);
					v_MACDs.add(v_ThreeDaysAgoMACD);
				}
			}
		}


		if (v_MACDs.size() == 1) {
			System.out.println("\n\n--");

			MACD v_MACD = v_MACDs.get(0);
			while (v_MACD.getSuperMACD() != null) {
				System.out.println("-- " + v_MACD.toString());
				v_MACD = v_MACD.getSuperMACD();
			}

			System.out.println("-- " + v_MACD.toString());
		} else {
			// TechIndicatorsUtils.print(v_MACDs);
		}
	}

	/**
	 * 逆向计算：快速移动平均值：12日EMA 和 慢速移动平均值：26日EMA
	 * <p>
	 * 此方法不能十分精确的逆向计算，而是采取的穷举模式
	 *
	 * @param closePrice
	 * @param dif
	 * @param yesterdayDIF
	 * @return
	 */
	public static List<MACD> inverseCalcEMA_Exhaustivity(double closePrice, double dif, double yesterdayDIF) {
		List<MACD> v_MACDs = new ArrayList<MACD>();
		double v_Step = 0.001D;  // 步数大小：穷举精度
		double v_PriceScope = 1;       // 穷举的价格范围幅度

		for (double v_YesterdayEMA12 = closePrice + v_PriceScope; v_YesterdayEMA12 >= closePrice - v_PriceScope; v_YesterdayEMA12 = TechIndicatorsUtils.round(v_YesterdayEMA12 - v_Step, 3)) {
			double v_EMA12 = calcEMA12(closePrice, v_YesterdayEMA12);

			for (double v_YesterdayEMA26 = closePrice + v_PriceScope; v_YesterdayEMA26 >= closePrice - v_PriceScope; v_YesterdayEMA26 = TechIndicatorsUtils.round(v_YesterdayEMA26 - v_Step, 3)) {
				double v_EMA26 = calcEMA26(closePrice, v_YesterdayEMA26);
				double v_DIF = calcDIF(v_EMA12, v_EMA26);
				double v_YesterdayDIF = calcDIF(v_YesterdayEMA12, v_YesterdayEMA26);

				if (dif == TechIndicatorsUtils.round(v_DIF, 2)) {
					if (yesterdayDIF == TechIndicatorsUtils.round(v_YesterdayDIF, 2)) {
						MACD v_YesterdayMACD = new MACD(v_YesterdayEMA12, v_YesterdayEMA26, 0, 0, 0);
						v_YesterdayMACD.setSuperMACD(new MACD(v_EMA12, v_EMA26, TechIndicatorsUtils.round(v_DIF, 4), 0, 0));

						v_MACDs.add(v_YesterdayMACD);
						System.out.println("-- YesterdayEMA12 = " + v_YesterdayEMA12 + "    YesterdayEMA26 = " + v_YesterdayEMA26);
					}
				}
			}
		}

		return v_MACDs;
	}

	/**
	 * 逆向计算：快速移动平均值（昨天的）：12日EMA
	 *
	 * @param closePrice
	 * @param ema12
	 * @return
	 */
	public static double inverseCalcYesterdayEMA12(double closePrice, double ema12) {
		return (ema12 * 13 - closePrice * 2) / 11;
	}

	/**
	 * 逆向计算：慢速移动平均值（昨天的）：26日EMA
	 *
	 * @param closePrice
	 * @param ema26
	 * @return
	 */
	public static double inverseCalcYesterdayEMA26(double closePrice, double ema26) {
		return (ema26 * 27 - closePrice * 2) / 25;
	}

	/**
	 * 快速移动平均值：12日EMA的计算
	 * <p>
	 * EMA(12) = 前一日EMA(12) * 11 / 13 + 今日收盘价 * 2 / 13
	 *
	 * @param closePrice     今日收盘价
	 * @param yesterdayEMA12 前一日EMA(12)
	 * @return
	 */
	public static double calcEMA12(double closePrice, double yesterdayEMA12) {
		return (yesterdayEMA12 * 11 + closePrice * 2) / 13;
	}

	/**
	 * 慢速移动平均值：26日EMA的计算
	 * <p>
	 * EMA(26) = 前一日EMA(26) * 25 / 27 + 今日收盘价 * 2 / 27
	 *
	 * @param closePrice     今日收盘价
	 * @param yesterdayEMA26 前一日EMA(26)
	 * @return
	 */
	public static double calcEMA26(double closePrice, double yesterdayEMA26) {
		return (yesterdayEMA26 * 25 + closePrice * 2) / 27;
	}

	/**
	 * 快速线：差离值(DIF)的计算
	 *
	 * @param ema12 12日EMA
	 * @param ema26 26日EMA
	 * @return
	 */
	public static double calcDIF(double ema12, double ema26) {
		return ema12 - ema26;
	}

	/**
	 * 慢速线：9日移动平均值：9日EMA的计算
	 * <p>
	 * 据差离值计算其9日的EMA，即离差平均值。为了不与指标原名相混淆，此值又名DEA或DEM。
	 * <p>
	 * 今日DEA = 前一日DEA * 8 / 10 + 今日DIF * 2 / 10
	 *
	 * @param dif          今日DIF
	 * @param yesterdayDEA 前一日DEA
	 * @return
	 */
	public static double calcDEA(double dif, double yesterdayDEA) {
		return yesterdayDEA * 8 / 10 + dif * 2 / 10;
	}

	/**
	 * 计算MACD柱状图(线)
	 * <p>
	 * (DIF - DEA) * 2
	 *
	 * @param dif 差离值
	 * @param dea 离差平均值
	 * @return
	 */
	public static double calcBar(double dif, double dea) {
		return (dif - dea) * 2;
	}

	public static void main(String[] i_Args) {
		double v_EMA12 = MACD.MACD_INIT_VALUE;
		double v_EMA26 = MACD.MACD_INIT_VALUE;
		double v_DIF = MACD.MACD_INIT_VALUE;
		double v_DEA = 0.17;                  // 前一天的DEA

		// 2016-07-19
		v_EMA12 = 10.5565;                    // 通过inverseCalcEMA_Trend返回的"下标0：今天价格" 对应的EMA12、EMA26
		v_EMA26 = 10.3587;
		v_DIF = TechIndicatorsUtils.round(calcDIF(v_EMA12, v_EMA26), 4);
		v_DEA = TechIndicatorsUtils.round(calcDEA(v_DIF, v_DEA), 4);

		// sh601766 2016-07-15 向后的数据
		// inverseCalcEMA_Trend(new double[]{9.35 ,9.37 ,9.43 ,9.37 ,9.23} ,new double[]{0.01 ,0.00 ,-0.01 ,-0.03 ,-0.04});

		// sz002267 2016-07-19 ~ 2016-06-24 之间的交易数据
		inverseCalcEMA_Trend(new double[]{10.68, 10.72, 10.64, 10.70, 10.74, 10.73, 10.50, 10.47, 10.66, 10.34, 10.26, 10.31, 10.19, 10.30, 10.20, 10.15, 10.07, 9.91}
				, new double[]{0.20, 0.20, 0.20, 0.20, 0.19, 0.18, 0.16, 0.15, 0.14, 0.11, 0.10, 0.10, 0.09, 0.08, 0.06, 0.05, 0.04, 0.03});
	}
}
