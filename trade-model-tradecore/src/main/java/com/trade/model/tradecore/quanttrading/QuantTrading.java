package com.trade.model.tradecore.quanttrading;

import com.google.common.collect.Lists;
import com.trade.model.tradecore.enums.OptionTypeEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.List;

@ToString
@Getter
@Setter
public class QuantTrading implements Serializable {
	private static final long serialVersionUID = -4535590288907497018L;

	// ==================== 交易类型标识 ====================
	/**
	 * 是否已买入股票
	 */
	private boolean isBuyStock;

	/**
	 * 是否已卖空股票
	 */
	private boolean isSellStock;

	/**
	 * 当前交易批次
	 */
	private int tradingBatch;

	// ==================== 持续涨跌幅价格数据 ====================
	/**
	 * 此轮状态是涨还是跌
	 */
	private OptionTypeEnum optionType;

	/**
	 * 此轮涨跌的最低价
	 */
	private float lowPrice;

	/**
	 * 此轮涨跌的最高价
	 */
	private float highPrice;

	/**
	 * 当前价格
	 */
	private float currentPrice;

	/**
	 * 上一次的价格
	 */
	private float prevPrice;

	/**
	 * 当前价格变动占上一轮价格变动范围
	 */
	private float changeRate;

	/**
	 * 涨跌趋势变化时间点集合
	 */
	private List<LocalTime> optionTypeChangeTimes = Lists.newArrayList();

	// ==================== 首次 买入、卖空 交易数据 ====================
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

	// ==================== 结束 卖出、赎回 交易数据 ====================
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

	// ==================== 交易过程中间数据 ====================
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

	// ==================== 是否结束交易 ====================
	/**
	 * 是否结束交易
	 */
	private boolean isTradingFinished;
}
