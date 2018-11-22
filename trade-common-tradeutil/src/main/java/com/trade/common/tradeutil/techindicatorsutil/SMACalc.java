package com.trade.common.tradeutil.techindicatorsutil;

import java.util.List;

public interface SMACalc<O> {

	/**
	 * 在计算开始前，统一获取要被计算的数天数据。
	 * 注：只执行一次
	 *
	 * @param dayCount 获取多少天的数据（一般从今天向后取数据）
	 * @return
	 */
	List<O> calcSMABefore(int dayCount);

	/**
	 * 返回计算SMA中，某一天的计算结果
	 *
	 * @param dayNo   第几天的序号。最小下标为1。
	 * @param dayData 第几天的数据对象。
	 * @return
	 */
	double calcSMA(int dayNo, O dayData);

}
