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
	 * @param stockCode
	 * @param plannedTradeDate
	 * @param deviationAmount
	 * @param plannedDeviationRate
	 * @param plannedSellOutProfitRate
	 * @param plannedStopLossProfitRate
	 * @param plannedScore
	 * @param predayVolume
	 * @param predayTurnover
	 * @param predayTurnoverRate
	 * @param predayChangeRate
	 * @param predayKdjJson
	 * @return
	 */
	public static QuantTradePlanned createDataModel(long stockID,
	                                                String stockCode,
	                                                LocalDate plannedTradeDate,
	                                                int deviationAmount,
	                                                float plannedDeviationRate,
	                                                float plannedSellOutProfitRate,
	                                                float plannedStopLossProfitRate,
	                                                float plannedScore,
	                                                long predayVolume,
	                                                long predayTurnover,
	                                                float predayTurnoverRate,
	                                                float predayChangeRate,
	                                                String predayKdjJson) {
		QuantTradePlanned result = new QuantTradePlanned();
		result.setStockID(stockID);
		result.setStockCode(stockCode);
		result.setPlannedTradeDate(plannedTradeDate);
		result.setDeviationAmount(deviationAmount);
		result.setPlannedDeviationRate(plannedDeviationRate);
		result.setPlannedSellOutProfitRate(plannedSellOutProfitRate);
		result.setPlannedStopLossProfitRate(plannedStopLossProfitRate);
		result.setPlannedScore(plannedScore);
		result.setPredayVolume(predayVolume);
		result.setPredayTurnover(predayTurnover);
		result.setPredayTurnoverRate(predayTurnoverRate);
		result.setPredayChangeRate(predayChangeRate);
		result.setPredayKdjJson(predayKdjJson);
		result.setCreateTime(LocalDateTime.now());

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
	 * 股票代码
	 */
	private String stockCode;

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
	 * 计划交易前一日的成交量
	 */
	private long predayVolume;

	/**
	 * 计划交易前一日的成交额
	 */
	private long predayTurnover;

	/**
	 * 计划交易前一日的换手率
	 */
	private float predayTurnoverRate;

	/**
	 * 计划交易前一日的涨跌幅
	 */
	private float predayChangeRate;

	/**
	 * 计划交易前一日的 KDJ 指标 json
	 */
	private String predayKdjJson = "";

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

	public String getStockCode() {
		return stockCode;
	}

	public void setStockCode(String stockCode) {
		this.stockCode = stockCode;
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

	public long getPredayVolume() {
		return predayVolume;
	}

	public void setPredayVolume(long predayVolume) {
		this.predayVolume = predayVolume;
	}

	public long getPredayTurnover() {
		return predayTurnover;
	}

	public void setPredayTurnover(long predayTurnover) {
		this.predayTurnover = predayTurnover;
	}

	public float getPredayTurnoverRate() {
		return predayTurnoverRate;
	}

	public void setPredayTurnoverRate(float predayTurnoverRate) {
		this.predayTurnoverRate = predayTurnoverRate;
	}

	public float getPredayChangeRate() {
		return predayChangeRate;
	}

	public void setPredayChangeRate(float predayChangeRate) {
		this.predayChangeRate = predayChangeRate;
	}

	public String getPredayKdjJson() {
		return predayKdjJson;
	}

	public void setPredayKdjJson(String predayKdjJson) {
		this.predayKdjJson = predayKdjJson;
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
