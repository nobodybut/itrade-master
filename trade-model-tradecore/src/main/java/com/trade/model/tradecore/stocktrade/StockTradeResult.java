package com.trade.model.tradecore.stocktrade;

import com.trade.model.tradecore.enums.TradeStatusEnum;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.time.LocalTime;

public class StockTradeResult implements Serializable {
	private static final long serialVersionUID = -1579584795811043190L;

	/**
	 * 私有构造函数，防止代码中用 new 的方式创建对象（请统一使用当前类的 createDataModel 方法创建对象）
	 */
	protected StockTradeResult() {
	}

	/**
	 * 创建数据对象（代码规范：如有新增的字段，请同时修改此方法的参数）
	 *
	 * @param stockID
	 * @param tradeStatus
	 * @param plannedBuyPrice
	 * @param plannedSellPrice
	 * @param plannedProfitAmount
	 * @param plannedLossAmount
	 * @param actualBuyPrice
	 * @param actualSellPrice
	 * @param actualTradeQuantity
	 * @param profitOrLessAmount
	 * @param profitOrLessRate
	 * @param actualTradeStartTime
	 * @param actualTradeEndTime
	 * @param touchProfitTimes
	 * @param touchLossTimes
	 * @param reduceProfitRateMultiple
	 * @return
	 */
	public static StockTradeResult createDataModel(long stockID,
	                                               TradeStatusEnum tradeStatus,
	                                               float plannedBuyPrice,
	                                               float plannedSellPrice,
	                                               float plannedProfitAmount,
	                                               float plannedLossAmount,
	                                               float actualBuyPrice,
	                                               float actualSellPrice,
	                                               int actualTradeQuantity,
	                                               float profitOrLessAmount,
	                                               float profitOrLessRate,
	                                               LocalTime actualTradeStartTime,
	                                               LocalTime actualTradeEndTime,
	                                               int touchProfitTimes,
	                                               int touchLossTimes,
	                                               int reduceProfitRateMultiple) {
		StockTradeResult result = new StockTradeResult();
		result.setStockID(stockID);
		result.setTradeStatus(tradeStatus);
		result.setPlannedBuyPrice(plannedBuyPrice);
		result.setPlannedSellPrice(plannedSellPrice);
		result.setPlannedProfitAmount(plannedProfitAmount);
		result.setPlannedLossAmount(plannedLossAmount);
		result.setActualBuyPrice(actualBuyPrice);
		result.setActualSellPrice(actualSellPrice);
		result.setActualTradeQuantity(actualTradeQuantity);
		result.setProfitOrLessAmount(profitOrLessAmount);
		result.setProfitOrLessRate(profitOrLessRate);
		result.setActualTradeStartTime(actualTradeStartTime);
		result.setActualTradeEndTime(actualTradeEndTime);
		result.setTouchProfitTimes(touchProfitTimes);
		result.setTouchLossTimes(touchLossTimes);
		result.setReduceProfitRateMultiple(reduceProfitRateMultiple);

		return result;
	}

	/**
	 * 创建无交易数据对象（代码规范：如有新增的字段，请同时修改此方法的参数）
	 *
	 * @param stockID
	 * @return
	 */
	public static StockTradeResult createNoTradeDataModel(long stockID) {
		StockTradeResult result = new StockTradeResult();
		result.setStockID(stockID);
		result.setTradeStatus(TradeStatusEnum.NO_TRADE);

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
	 * 购买数量
	 */
	private int actualTradeQuantity;

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

	public int getActualTradeQuantity() {
		return actualTradeQuantity;
	}

	public void setActualTradeQuantity(int actualTradeQuantity) {
		this.actualTradeQuantity = actualTradeQuantity;
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

	/**
	 * =============== readonly ===============
	 */
	public float getTotalTradeAmount() {
		int totalAmount = 0;

		if (actualTradeQuantity > 0) {
			if ((tradeStatus == TradeStatusEnum.BUY_SUCCESS_SELL_SUCCESS || tradeStatus == TradeStatusEnum.BUY_SUCCESS_SELL_FAIL) && actualBuyPrice != 0) {
				totalAmount += actualBuyPrice * actualTradeQuantity;
			}

			if ((tradeStatus == TradeStatusEnum.SELL_SUCCESS_BUY_SUCCESS || tradeStatus == TradeStatusEnum.SELL_SUCCESS_BUY_FAIL) && actualSellPrice != 0) {
				totalAmount += actualSellPrice * actualTradeQuantity;
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
