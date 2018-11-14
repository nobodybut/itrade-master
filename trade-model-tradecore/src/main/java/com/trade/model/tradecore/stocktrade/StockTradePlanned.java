package com.trade.model.tradecore.stocktrade;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class StockTradePlanned implements Serializable {
	private static final long serialVersionUID = 8364775439536883884L;

	/**
	 * 私有构造函数，防止代码中用 new 的方式创建对象（请统一使用当前类的 createDataModel 方法创建对象）
	 */
	protected StockTradePlanned() {
	}

	/**
	 * 创建数据对象（代码规范：如有新增的字段，请同时修改此方法的参数）
	 *
	 * @param stockID
	 * @param date
	 * @param plannedBuyPrice
	 * @param plannedSellPrice
	 * @param plannedVolume
	 * @param plannedDeviationRate
	 * @param plannedSellOutProfitRate
	 * @param plannedStopLossProfitRate
	 * @param plannedScore
	 * @param createTime
	 * @return
	 */
	public static StockTradePlanned createDataModel(long stockID,
	                                                LocalDate date,
	                                                float plannedBuyPrice,
	                                                float plannedSellPrice,
	                                                int plannedVolume,
	                                                float plannedDeviationRate,
	                                                float plannedSellOutProfitRate,
	                                                float plannedStopLossProfitRate,
	                                                double plannedScore,
	                                                LocalDateTime createTime) {
		StockTradePlanned result = new StockTradePlanned();
		result.setStockID(stockID);
		result.setDate(date);
		result.setPlannedBuyPrice(plannedBuyPrice);
		result.setPlannedSellPrice(plannedSellPrice);
		result.setPlannedVolume(plannedVolume);
		result.setPlannedDeviationRate(plannedDeviationRate);
		result.setPlannedSellOutProfitRate(plannedSellOutProfitRate);
		result.setPlannedStopLossProfitRate(plannedStopLossProfitRate);
		result.setPlannedScore(plannedScore);
		result.setCreateTime(createTime);

		return result;
	}

	/** =============== field =============== */
	/**
	 * 股票计划交易ID
	 */
	private int tradePlannedID;

	/**
	 * 股票ID
	 */
	private long stockID;

	/**
	 * 交易日期
	 */
	private LocalDate date;

	/**
	 * 计划买入价格
	 */
	private float plannedBuyPrice;

	/**
	 * 计划卖出价格
	 */
	private float plannedSellPrice;

	/**
	 * 计划买入/卖出股数
	 */
	private int plannedVolume;

	/**
	 * 计划价格偏离比例（默认：0.4F）
	 */
	private float plannedDeviationRate;

	/**
	 * 计划卖出/赎回占开盘价的比例
	 */
	private float plannedSellOutProfitRate;

	/**
	 * 计划止损占开盘价的比例
	 */
	private float plannedStopLossProfitRate;

	/**
	 * 计划交易综合评分
	 */
	private double plannedScore;

	/**
	 * 计划交易创建时间
	 */
	private LocalDateTime createTime;

	/**
	 * =============== get/set ===============
	 */
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

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
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

	public int getPlannedVolume() {
		return plannedVolume;
	}

	public void setPlannedVolume(int plannedVolume) {
		this.plannedVolume = plannedVolume;
	}

	public float getPlannedDeviationRate() {
		return plannedDeviationRate;
	}

	public void setPlannedDeviationRate(float plannedDeviationRate) {
		this.plannedDeviationRate = plannedDeviationRate;
	}

	public float getPlannedSellOutProfitRate() {
		return plannedSellOutProfitRate;
	}

	public void setPlannedSellOutProfitRate(float plannedSellOutProfitRate) {
		this.plannedSellOutProfitRate = plannedSellOutProfitRate;
	}

	public float getPlannedStopLossProfitRate() {
		return plannedStopLossProfitRate;
	}

	public void setPlannedStopLossProfitRate(float plannedStopLossProfitRate) {
		this.plannedStopLossProfitRate = plannedStopLossProfitRate;
	}

	public double getPlannedScore() {
		return plannedScore;
	}

	public void setPlannedScore(double plannedScore) {
		this.plannedScore = plannedScore;
	}

	public LocalDateTime getCreateTime() {
		return createTime;
	}

	public void setCreateTime(LocalDateTime createTime) {
		this.createTime = createTime;
	}

	/**
	 * =============== toString() ===============
	 */
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
