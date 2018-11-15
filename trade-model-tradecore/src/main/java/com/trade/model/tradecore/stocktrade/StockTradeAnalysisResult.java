package com.trade.model.tradecore.stocktrade;

import com.trade.model.tradecore.enums.TradeStatusEnum;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 股票交易分析结果数据
 */
public class StockTradeAnalysisResult implements Serializable {
	private static final long serialVersionUID = -1579584795811043190L;

	/**
	 * 私有构造函数，防止代码中用 new 的方式创建对象（请统一使用当前类的 createDataModel 方法创建对象）
	 */
	protected StockTradeAnalysisResult() {
	}

	/**
	 * 创建数据对象（代码规范：如有新增的字段，请同时修改此方法的参数）
	 *
	 * @param stockID
	 * @param tradeStatus
	 * @param tradeDate
	 * @param plannedBuyPrice
	 * @param plannedSellPrice
	 * @param plannedProfitAmount
	 * @param plannedLossAmount
	 * @param actualBuyPrice
	 * @param actualSellPrice
	 * @param actualTradeVolume
	 * @param profitOrLessAmount
	 * @param profitOrLessRate
	 * @param actualTradeStartTime
	 * @param actualTradeEndTime
	 * @param touchProfitTimes
	 * @param touchLossTimes
	 * @param reduceProfitRateMultiple
	 * @return
	 */
	public static StockTradeAnalysisResult createDataModel(long stockID,
	                                                       TradeStatusEnum tradeStatus,
	                                                       LocalDate tradeDate,
	                                                       float plannedBuyPrice,
	                                                       float plannedSellPrice,
	                                                       float plannedProfitAmount,
	                                                       float plannedLossAmount,
	                                                       float actualBuyPrice,
	                                                       float actualSellPrice,
	                                                       int actualTradeVolume,
	                                                       float profitOrLessAmount,
	                                                       float profitOrLessRate,
	                                                       LocalTime actualTradeStartTime,
	                                                       LocalTime actualTradeEndTime,
	                                                       int touchProfitTimes,
	                                                       int touchLossTimes,
	                                                       int reduceProfitRateMultiple) {
		StockTradeAnalysisResult result = new StockTradeAnalysisResult();
		result.setStockID(stockID);
		result.setTradeStatus(tradeStatus);
		result.setTradeDate(tradeDate);
		result.setPlannedBuyPrice(plannedBuyPrice);
		result.setPlannedSellPrice(plannedSellPrice);
		result.setPlannedProfitAmount(plannedProfitAmount);
		result.setPlannedLossAmount(plannedLossAmount);
		result.setActualBuyPrice(actualBuyPrice);
		result.setActualSellPrice(actualSellPrice);
		result.setActualTradeVolume(actualTradeVolume);
		result.setProfitOrLessAmount(profitOrLessAmount);
		result.setProfitOrLessRate(profitOrLessRate);
		result.setActualTradeStartTime(actualTradeStartTime);
		result.setActualTradeEndTime(actualTradeEndTime);
		result.setTouchProfitTimes(touchProfitTimes);
		result.setTouchLossTimes(touchLossTimes);
		result.setReduceProfitRateMultiple(reduceProfitRateMultiple);
		result.setSmallVolume(false);

		return result;
	}

	/**
	 * 创建无交易数据对象（代码规范：如有新增的字段，请同时修改此方法的参数）
	 *
	 * @param stockID
	 * @param smallVolume
	 * @return
	 */
	public static StockTradeAnalysisResult createNoTradeDataModel(long stockID, boolean smallVolume) {
		StockTradeAnalysisResult result = new StockTradeAnalysisResult();
		result.setStockID(stockID);
		result.setTradeStatus(TradeStatusEnum.NO_TRADE);
		result.setSmallVolume(smallVolume);

		return result;
	}

	/** =============== field =============== */
	/**
	 * 股票ID
	 */
	private long stockID;

	/**
	 * 交易状态
	 */
	private TradeStatusEnum tradeStatus;

	/**
	 * 交易日期
	 */
	private LocalDate tradeDate;

	/**
	 * 计划买入价/赎回价
	 */
	private float plannedBuyPrice;

	/**
	 * 计划卖出价/卖空价
	 */
	private float plannedSellPrice;

	/**
	 * 计划盈利金额
	 */
	private float plannedProfitAmount;

	/**
	 * 计划亏损金额
	 */
	private float plannedLossAmount;

	/**
	 * 买入价格
	 */
	private float actualBuyPrice;

	/**
	 * 卖出价格
	 */
	private float actualSellPrice;

	/**
	 * 成交数量
	 */
	private int actualTradeVolume;

	/**
	 * 盈亏总金额
	 */
	private float profitOrLessAmount;

	/**
	 * 盈亏比例
	 */
	private float profitOrLessRate;

	/**
	 * 实际交易开始时间
	 */
	private LocalTime actualTradeStartTime;

	/**
	 * 实际交易结束时间
	 */
	private LocalTime actualTradeEndTime;

	/**
	 * 到达盈利点次数
	 */
	private int touchProfitTimes = 0;

	/**
	 * 到达亏损点次数
	 */
	private int touchLossTimes = 0;

	/**
	 * 降低利润率和亏损值的倍数
	 */
	private int reduceProfitRateMultiple;

	/**
	 * 是否为较少的交易量
	 */
	private boolean smallVolume;

	/**
	 * =============== get/set ===============
	 */
	public long getStockID() {
		return stockID;
	}

	public void setStockID(long stockID) {
		this.stockID = stockID;
	}

	public TradeStatusEnum getTradeStatus() {
		return tradeStatus;
	}

	public void setTradeStatus(TradeStatusEnum tradeStatus) {
		this.tradeStatus = tradeStatus;
	}

	public LocalDate getTradeDate() {
		return tradeDate;
	}

	public void setTradeDate(LocalDate tradeDate) {
		this.tradeDate = tradeDate;
	}

	public float getPlannedBuyPrice() {
		return plannedBuyPrice;
	}

	public void setPlannedBuyPrice(float plannedBuyPrice) {
		this.plannedBuyPrice = plannedBuyPrice;
	}

	public float getPlannedSellPrice() {
		return plannedSellPrice;
	}

	public void setPlannedSellPrice(float plannedSellPrice) {
		this.plannedSellPrice = plannedSellPrice;
	}

	public float getPlannedProfitAmount() {
		return plannedProfitAmount;
	}

	public void setPlannedProfitAmount(float plannedProfitAmount) {
		this.plannedProfitAmount = plannedProfitAmount;
	}

	public float getPlannedLossAmount() {
		return plannedLossAmount;
	}

	public void setPlannedLossAmount(float plannedLossAmount) {
		this.plannedLossAmount = plannedLossAmount;
	}

	public float getActualBuyPrice() {
		return actualBuyPrice;
	}

	public void setActualBuyPrice(float actualBuyPrice) {
		this.actualBuyPrice = actualBuyPrice;
	}

	public float getActualSellPrice() {
		return actualSellPrice;
	}

	public void setActualSellPrice(float actualSellPrice) {
		this.actualSellPrice = actualSellPrice;
	}

	public int getActualTradeVolume() {
		return actualTradeVolume;
	}

	public void setActualTradeVolume(int actualTradeVolume) {
		this.actualTradeVolume = actualTradeVolume;
	}

	public float getProfitOrLessAmount() {
		return profitOrLessAmount;
	}

	public void setProfitOrLessAmount(float profitOrLessAmount) {
		this.profitOrLessAmount = profitOrLessAmount;
	}

	public float getProfitOrLessRate() {
		return profitOrLessRate;
	}

	public void setProfitOrLessRate(float profitOrLessRate) {
		this.profitOrLessRate = profitOrLessRate;
	}

	public LocalTime getActualTradeStartTime() {
		return actualTradeStartTime;
	}

	public void setActualTradeStartTime(LocalTime actualTradeStartTime) {
		this.actualTradeStartTime = actualTradeStartTime;
	}

	public LocalTime getActualTradeEndTime() {
		return actualTradeEndTime;
	}

	public void setActualTradeEndTime(LocalTime actualTradeEndTime) {
		this.actualTradeEndTime = actualTradeEndTime;
	}

	public int getTouchProfitTimes() {
		return touchProfitTimes;
	}

	public void setTouchProfitTimes(int touchProfitTimes) {
		this.touchProfitTimes = touchProfitTimes;
	}

	public int getTouchLossTimes() {
		return touchLossTimes;
	}

	public void setTouchLossTimes(int touchLossTimes) {
		this.touchLossTimes = touchLossTimes;
	}

	public int getReduceProfitRateMultiple() {
		return reduceProfitRateMultiple;
	}

	public void setReduceProfitRateMultiple(int reduceProfitRateMultiple) {
		this.reduceProfitRateMultiple = reduceProfitRateMultiple;
	}

	public boolean isSmallVolume() {
		return smallVolume;
	}

	public void setSmallVolume(boolean smallVolume) {
		this.smallVolume = smallVolume;
	}

	/**
	 * =============== readonly ===============
	 */
	public float getTotalTradeAmount() {
		int totalAmount = 0;

		if (actualTradeVolume > 0) {
			if ((tradeStatus == TradeStatusEnum.BUY_SUCCESS_SELL_SUCCESS || tradeStatus == TradeStatusEnum.BUY_SUCCESS_SELL_FAIL) && actualBuyPrice != 0) {
				totalAmount += actualBuyPrice * actualTradeVolume;
			}

			if ((tradeStatus == TradeStatusEnum.SELL_SUCCESS_BUY_SUCCESS || tradeStatus == TradeStatusEnum.SELL_SUCCESS_BUY_FAIL) && actualSellPrice != 0) {
				totalAmount += actualSellPrice * actualTradeVolume;
			}
		}

		return totalAmount;
	}

	/**
	 * =============== toString() ===============
	 */
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
