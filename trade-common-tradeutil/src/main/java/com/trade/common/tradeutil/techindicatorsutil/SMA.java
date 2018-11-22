package com.trade.common.tradeutil.techindicatorsutil;

import java.util.List;

public class SMA {

	/**
	 * [M*X+(N-M)*Y')]/N
	 *
	 * @param smaCalc  X:获取某一天数值的接口
	 * @param dayCount N:计算多少天的移动平均值
	 * @param weight   M:权重
	 * @return
	 */
	public static <O> double calcSMA(SMACalc<O> smaCalc, int dayCount, double weight) {
		double v_SMA = 0D;
		double v_YesterdaySMA = 0D;
		List<O> v_Datas = smaCalc.calcSMABefore(dayCount);

		for (int v_DayNo = Math.min(dayCount, v_Datas.size()); v_DayNo >= 1; v_DayNo--) {
			double v_X = smaCalc.calcSMA(v_DayNo, v_Datas.get(v_DayNo - 1));

			v_SMA = (weight * v_X + (dayCount - weight) * v_YesterdaySMA) / dayCount;
			v_YesterdaySMA = v_SMA;
		}

		return v_SMA;
	}

}
