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
	 * @param plannedTime
	 * @param plannedScore
	 * @return
	 */
	public static StockTradePlanned createDataModel(long stockID,
	                                                LocalDate date,
	                                                float plannedBuyPrice,
	                                                float plannedSellPrice,
	                                                int plannedVolume,
	                                                LocalDateTime plannedTime,
	                                                double plannedScore) {
		StockTradePlanned result = new StockTradePlanned();
		result.setStockID(stockID);
		result.setDate(date);
		result.setPlannedBuyPrice(plannedBuyPrice);
		result.setPlannedSellPrice(plannedSellPrice);
		result.setPlannedVolume(plannedVolume);
		result.setPlannedTime(plannedTime);
		result.setPlannedScore(plannedScore);

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
	 * 计划交易创建时间
	 */
	private LocalDateTime plannedTime;

	/**
	 * 计划交易综合评分
	 */
	private double plannedScore;

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

	public LocalDateTime getPlannedTime() {
		return plannedTime;
	}

	public void setPlannedTime(LocalDateTime plannedTime) {
		this.plannedTime = plannedTime;
	}

	public double getPlannedScore() {
		return plannedScore;
	}

	public void setPlannedScore(double plannedScore) {
		this.plannedScore = plannedScore;
	}

	/**
	 * =============== toString() ===============
	 */
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
