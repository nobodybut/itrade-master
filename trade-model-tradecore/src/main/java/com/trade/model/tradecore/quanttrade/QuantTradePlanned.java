package com.trade.model.tradecore.quanttrade;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class QuantTradePlanned implements Serializable {
	private static final long serialVersionUID = -5235511179210370780L;

	/**
	 * 私有构造函数，防止代码中用 new 的方式创建对象（请统一使用当前类的 createDataModel 方法创建对象）
	 */
	protected QuantTradePlanned() {
	}

	/**
	 * 创建数据对象（代码规范：如有新增的字段，请同时修改此方法的参数）
	 *
	 * @param stockID
	 * @param plannedTradeDate
	 * @param deviationAmount
	 * @param plannedDeviationRate
	 * @param plannedSellOutProfitRate
	 * @param plannedStopLossProfitRate
	 * @param plannedScore
	 * @param createTime
	 * @param quantTradeAnalysis
	 * @return
	 */
	public static QuantTradePlanned createDataModel(long stockID,
	                                                LocalDate plannedTradeDate,
	                                                int deviationAmount,
	                                                float plannedDeviationRate,
	                                                float plannedSellOutProfitRate,
	                                                float plannedStopLossProfitRate,
	                                                float plannedScore,
	                                                LocalDateTime createTime,
	                                                QuantTradeAnalysis quantTradeAnalysis) {
		QuantTradePlanned result = new QuantTradePlanned();
		result.setStockID(stockID);
		result.setPlannedTradeDate(plannedTradeDate);
		result.setDeviationAmount(deviationAmount);
		result.setPlannedDeviationRate(plannedDeviationRate);
		result.setPlannedSellOutProfitRate(plannedSellOutProfitRate);
		result.setPlannedStopLossProfitRate(plannedStopLossProfitRate);
		result.setPlannedScore(plannedScore);
		result.setCreateTime(createTime);
		result.setQuantTradeAnalysis(quantTradeAnalysis);

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
	 * 计划交易日期
	 */
	private LocalDate plannedTradeDate;

	/**
	 * 计划当天买入点和卖出点距离开盘价的差价
	 */
	private int deviationAmount;

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
	private float plannedScore;

	/**
	 * 计划交易创建时间
	 */
	private LocalDateTime createTime;

	/**
	 * 股票交易分析结果数据（不写数据库）
	 */
	private QuantTradeAnalysis quantTradeAnalysis;

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

	public LocalDate getPlannedTradeDate() {
		return plannedTradeDate;
	}

	public void setPlannedTradeDate(LocalDate plannedTradeDate) {
		this.plannedTradeDate = plannedTradeDate;
	}

	public int getDeviationAmount() {
		return deviationAmount;
	}

	public void setDeviationAmount(int deviationAmount) {
		this.deviationAmount = deviationAmount;
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

	public float getPlannedScore() {
		return plannedScore;
	}

	public void setPlannedScore(float plannedScore) {
		this.plannedScore = plannedScore;
	}

	public LocalDateTime getCreateTime() {
		return createTime;
	}

	public void setCreateTime(LocalDateTime createTime) {
		this.createTime = createTime;
	}

	public QuantTradeAnalysis getQuantTradeAnalysis() {
		return quantTradeAnalysis;
	}

	public void setQuantTradeAnalysis(QuantTradeAnalysis quantTradeAnalysis) {
		this.quantTradeAnalysis = quantTradeAnalysis;
	}

	/**
	 * =============== toString() ===============
	 */
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
