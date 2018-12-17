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
	 * @param tradeDate
	 * @param predayKLineJson
	 * @param prePredayKLineJson
	 * @param plannedScore
	 * @return
	 */
	public static QuantTradePlanned createDataModel(long stockID,
	                                                String stockCode,
	                                                LocalDate tradeDate,
	                                                String predayKLineJson,
	                                                String prePredayKLineJson,
	                                                float plannedScore) {
		QuantTradePlanned result = new QuantTradePlanned();
		result.setStockID(stockID);
		result.setStockCode(stockCode);
		result.setTradeDate(tradeDate);
		result.setPredayKLineJson(predayKLineJson);
		result.setPrePredayKLineJson(prePredayKLineJson);
		result.setPlannedScore(plannedScore);
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
	private String stockCode = "";

	/**
	 * 计划交易日期
	 */
	private LocalDate tradeDate;

	/**
	 * 昨天的日K线数据 json
	 */
	private String predayKLineJson = "";

	/**
	 * 前天的日K线数据 json
	 */
	private String prePredayKLineJson = "";

	/**
	 * 计划交易综合评分
	 */
	private float plannedScore;

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

	public LocalDate getTradeDate() {
		return tradeDate;
	}

	public void setTradeDate(LocalDate tradeDate) {
		this.tradeDate = tradeDate;
	}

	public String getPredayKLineJson() {
		return predayKLineJson;
	}

	public void setPredayKLineJson(String predayKLineJson) {
		this.predayKLineJson = predayKLineJson;
	}

	public String getPrePredayKLineJson() {
		return prePredayKLineJson;
	}

	public void setPrePredayKLineJson(String prePredayKLineJson) {
		this.prePredayKLineJson = prePredayKLineJson;
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

	/**
	 * =============== toString() ===============
	 */
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
