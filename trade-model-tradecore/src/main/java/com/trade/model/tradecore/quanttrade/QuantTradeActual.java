package com.trade.model.tradecore.quanttrade;

import com.trade.model.tradecore.enums.TradingHandlerTypeEnum;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

public class QuantTradeActual implements Serializable {
	private static final long serialVersionUID = -3445653778401175337L;

	/**
	 * 私有构造函数，防止代码中用 new 的方式创建对象（请统一使用当前类的 createDataModel 方法创建对象）
	 */
	protected QuantTradeActual() {
	}

	/**
	 * 创建 买入/卖空 数据对象（代码规范：如有新增的字段，请同时修改此方法的参数）
	 *
	 * @param tradePlannedID
	 * @param stockID
	 * @param stockCode
	 * @param tradingHandlerType
	 * @param sellShort
	 * @param actualBuyPrice
	 * @param actualBuyVolume
	 * @param actualBuyKdjJson
	 * @param actualBuyTradeDate
	 * @param actualBuyTradeTime
	 * @return
	 */
	public static QuantTradeActual createActualBuyDataModel(int tradePlannedID,
	                                                        long stockID,
	                                                        String stockCode,
	                                                        TradingHandlerTypeEnum tradingHandlerType,
	                                                        boolean sellShort,
	                                                        float actualBuyPrice,
	                                                        int actualBuyVolume,
	                                                        String actualBuyKdjJson,
	                                                        LocalDate actualBuyTradeDate,
	                                                        LocalTime actualBuyTradeTime) {
		QuantTradeActual result = new QuantTradeActual();
		result.setTradePlannedID(tradePlannedID);
		result.setStockID(stockID);
		result.setStockCode(stockCode);
		result.setTradingHandlerType(tradingHandlerType);
		result.setSellShort(sellShort);
		result.setActualBuyPrice(actualBuyPrice);
		result.setActualBuyVolume(actualBuyVolume);
		result.setActualBuyKdjJson(actualBuyKdjJson);
		result.setActualBuyTradeDate(actualBuyTradeDate);
		result.setActualBuyTradeTime(actualBuyTradeTime);

		return result;
	}

	/**
	 * 创建 卖出/赎回 数据对象（代码规范：如有新增的字段，请同时修改此方法的参数）
	 *
	 * @param tradeActualID
	 * @param actualSellPrice
	 * @param actualSellVolume
	 * @param actualSellKdjJson
	 * @param actualSellTradeDate
	 * @param actualSellTradeTime
	 * @param profitOrLessAmount
	 * @param profitOrLessRate
	 * @param touchProfitTimes
	 * @param touchLossTimes
	 * @param reduceProfitRateMultiple
	 * @return
	 */
	public static QuantTradeActual createActualSellDataModel(int tradeActualID,
	                                                         float actualSellPrice,
	                                                         int actualSellVolume,
	                                                         String actualSellKdjJson,
	                                                         LocalDate actualSellTradeDate,
	                                                         LocalTime actualSellTradeTime,
	                                                         float profitOrLessAmount,
	                                                         float profitOrLessRate,
	                                                         int touchProfitTimes,
	                                                         int touchLossTimes,
	                                                         int reduceProfitRateMultiple) {
		QuantTradeActual result = new QuantTradeActual();
		result.setTradeActualID(tradeActualID);
		result.setActualSellPrice(actualSellPrice);
		result.setActualSellVolume(actualSellVolume);
		result.setActualSellKdjJson(actualSellKdjJson);
		result.setActualSellTradeDate(actualSellTradeDate);
		result.setActualSellTradeTime(actualSellTradeTime);
		result.setProfitOrLessAmount(profitOrLessAmount);
		result.setProfitOrLessRate(profitOrLessRate);
		result.setTouchProfitTimes(touchProfitTimes);
		result.setTouchLossTimes(touchLossTimes);
		result.setReduceProfitRateMultiple(reduceProfitRateMultiple);

		return result;
	}


	/** =============== field =============== */
	/**
	 * 股票实际交易ID
	 */
	private int tradeActualID;

	/**
	 * 股票计划交易ID
	 */
	private int tradePlannedID;

	/**
	 * 股票ID
	 */
	private long stockID;

	/**
	 * 股票代码
	 */
	private String stockCode = "";

	/**
	 * 对应的实时交易规则处理器
	 */
	private TradingHandlerTypeEnum tradingHandlerType;

	/**
	 * 是否为卖空交易
	 */
	private boolean sellShort;

	/**
	 * 实际 买入/卖空 交易价格
	 */
	private float actualBuyPrice;

	/**
	 * 实际 买入/卖空 交易股数
	 */
	private int actualBuyVolume;

	/**
	 * 实际 买入/卖空 交易时的分钟线 KDJ 指标 json 数据
	 */
	private String actualBuyKdjJson = "";

	/**
	 * 实际 买入/卖空 交易日期
	 */
	private LocalDate actualBuyTradeDate;

	/**
	 * 实际 买入/卖空 交易时间
	 */
	private LocalTime actualBuyTradeTime;

	/**
	 * 实际 卖出/赎回 交易价格
	 */
	private float actualSellPrice;

	/**
	 * 实际 卖出/赎回 交易股数
	 */
	private int actualSellVolume;

	/**
	 * 实际 卖出/赎回 交易时的分钟线 KDJ 指标 json 数据
	 */
	private String actualSellKdjJson = "";

	/**
	 * 实际 卖出/赎回 交易日期
	 */
	private LocalDate actualSellTradeDate;

	/**
	 * 实际 卖出/赎回 交易时间
	 */
	private LocalTime actualSellTradeTime;

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
	 * 降低利润率和亏损值的倍数
	 */
	private int reduceProfitRateMultiple;

	/**
	 * =============== get/set ===============
	 */
	public int getTradeActualID() {
		return tradeActualID;
	}

	public void setTradeActualID(int tradeActualID) {
		this.tradeActualID = tradeActualID;
	}

	public int getTradePlannedID() {
		return tradePlannedID;
	}

	public void setTradePlannedID(int tradePlannedID) {
		this.tradePlannedID = tradePlannedID;
	}

	public long getStockID() {
		return stockID;
	}

	public void setStockID(long stockID) {
		this.stockID = stockID;
	}

	public String getStockCode() {
		return stockCode;
	}

	public void setStockCode(String stockCode) {
		this.stockCode = stockCode;
	}

	public TradingHandlerTypeEnum getTradingHandlerType() {
		return tradingHandlerType;
	}

	public void setTradingHandlerType(TradingHandlerTypeEnum tradingHandlerType) {
		this.tradingHandlerType = tradingHandlerType;
	}

	public boolean isSellShort() {
		return sellShort;
	}

	public void setSellShort(boolean sellShort) {
		this.sellShort = sellShort;
	}

	public float getActualBuyPrice() {
		return actualBuyPrice;
	}

	public void setActualBuyPrice(float actualBuyPrice) {
		this.actualBuyPrice = actualBuyPrice;
	}

	public int getActualBuyVolume() {
		return actualBuyVolume;
	}

	public void setActualBuyVolume(int actualBuyVolume) {
		this.actualBuyVolume = actualBuyVolume;
	}

	public String getActualBuyKdjJson() {
		return actualBuyKdjJson;
	}

	public void setActualBuyKdjJson(String actualBuyKdjJson) {
		this.actualBuyKdjJson = actualBuyKdjJson;
	}

	public LocalDate getActualBuyTradeDate() {
		return actualBuyTradeDate;
	}

	public void setActualBuyTradeDate(LocalDate actualBuyTradeDate) {
		this.actualBuyTradeDate = actualBuyTradeDate;
	}

	public LocalTime getActualBuyTradeTime() {
		return actualBuyTradeTime;
	}

	public void setActualBuyTradeTime(LocalTime actualBuyTradeTime) {
		this.actualBuyTradeTime = actualBuyTradeTime;
	}

	public float getActualSellPrice() {
		return actualSellPrice;
	}

	public void setActualSellPrice(float actualSellPrice) {
		this.actualSellPrice = actualSellPrice;
	}

	public int getActualSellVolume() {
		return actualSellVolume;
	}

	public void setActualSellVolume(int actualSellVolume) {
		this.actualSellVolume = actualSellVolume;
	}

	public String getActualSellKdjJson() {
		return actualSellKdjJson;
	}

	public void setActualSellKdjJson(String actualSellKdjJson) {
		this.actualSellKdjJson = actualSellKdjJson;
	}

	public LocalDate getActualSellTradeDate() {
		return actualSellTradeDate;
	}

	public void setActualSellTradeDate(LocalDate actualSellTradeDate) {
		this.actualSellTradeDate = actualSellTradeDate;
	}

	public LocalTime getActualSellTradeTime() {
		return actualSellTradeTime;
	}

	public void setActualSellTradeTime(LocalTime actualSellTradeTime) {
		this.actualSellTradeTime = actualSellTradeTime;
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
	 * =============== toString() ===============
	 */
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
