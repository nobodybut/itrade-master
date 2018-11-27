package com.trade.model.tradecore.quanttrade;

import com.trade.model.tradecore.enums.TradeSideEnum;
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
	 * 创建数据对象（代码规范：如有新增的字段，请同时修改此方法的参数）
	 *
	 * @param tradePlannedID
	 * @param stockID
	 * @param tradeSide
	 * @param actualPrice
	 * @param actualVolume
	 * @return
	 */
	public static QuantTradeActual createDataModel(int tradePlannedID,
	                                               long stockID,
	                                               TradeSideEnum tradeSide,
	                                               float actualPrice,
	                                               int actualVolume) {
		QuantTradeActual result = new QuantTradeActual();
		result.setTradePlannedID(tradePlannedID);
		result.setStockID(stockID);
		result.setActualTradeDate(LocalDate.now());
		result.setTradeSide(tradeSide);
		result.setActualPrice(actualPrice);
		result.setActualVolume(actualVolume);
		result.setActualTradeTime(LocalTime.now());

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
	 * 交易日期
	 */
	private LocalDate actualTradeDate;

	/**
	 * 交易类型（买入、卖出、卖空、赎回）
	 */
	private TradeSideEnum tradeSide;

	/**
	 * 实际交易价格
	 */
	private float actualPrice;

	/**
	 * 实际交易股数
	 */
	private int actualVolume;

	/**
	 * 实际交易时间点
	 */
	private LocalTime actualTradeTime;

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

	public LocalDate getActualTradeDate() {
		return actualTradeDate;
	}

	public void setActualTradeDate(LocalDate actualTradeDate) {
		this.actualTradeDate = actualTradeDate;
	}

	public TradeSideEnum getTradeSide() {
		return tradeSide;
	}

	public void setTradeSide(TradeSideEnum tradeSide) {
		this.tradeSide = tradeSide;
	}

	public float getActualPrice() {
		return actualPrice;
	}

	public void setActualPrice(float actualPrice) {
		this.actualPrice = actualPrice;
	}

	public int getActualVolume() {
		return actualVolume;
	}

	public void setActualVolume(int actualVolume) {
		this.actualVolume = actualVolume;
	}

	public LocalTime getActualTradeTime() {
		return actualTradeTime;
	}

	public void setActualTradeTime(LocalTime actualTradeTime) {
		this.actualTradeTime = actualTradeTime;
	}

	/**
	 * =============== toString() ===============
	 */
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
