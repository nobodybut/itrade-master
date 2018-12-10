package com.trade.model.tradecore.quanttrading;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalTime;

@ToString
@Getter
@Setter
public class QuantTrading implements Serializable {
	private static final long serialVersionUID = 6382353334189862919L;

	/**
	 * 是否已买入股票
	 */
	private boolean isBuyStock;

	/**
	 * 是否已卖空股票
	 */
	private boolean isSellStock;


	/**
	 * 实际首次交易是否成功
	 */
	private boolean actualTradeStartSuccess;

	/**
	 * 实际首次交易时间
	 */
	private LocalTime actualTradeStartTime;

	/**
	 * 实际买入价/赎回价
	 */
	private float actualBuyPrice;

	/**
	 * 实际交易股票数量
	 */
	private int actualTradeVolume;

	/**
	 * 股票实际交易ID
	 */
	private int tradeActualID;


	/**
	 * 实际结束交易是否成功
	 */
	private boolean actualTradeEndSuccess;

	/**
	 * 实际结束交易时间
	 */
	private LocalTime actualTradeEndTime;

	/**
	 * 实际卖出价/卖空价
	 */
	private float actualSellPrice;

	/**
	 * 盈亏总金额（美元 * 1000）
	 */
	private float profitOrLessAmount;

	/**
	 * 盈亏比例（百分之N）
	 */
	private float profitOrLessRate;


	/**
	 * 到达盈利点次数
	 */
	private int touchProfitTimes;

	/**
	 * 到达亏损点次数
	 */
	private int touchLossTimes;

	/**
	 * 削减过的计划盈利/亏损比例
	 */
	private int reduceProfitRateMultiple;


	/**
	 * 是否结束交易
	 */
	private boolean isTradingFinished;
}