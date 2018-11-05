package com.trade.model.tradecore.stocktrade;

import com.trade.model.tradecore.enums.TradeSideEnum;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

public class StockTradeActual implements Serializable {
	private static final long serialVersionUID = 8002361160092201620L;

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
	private LocalDate date;

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
	private LocalTime actualTime;

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

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
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

	public LocalTime getActualTime() {
		return actualTime;
	}

	public void setActualTime(LocalTime actualTime) {
		this.actualTime = actualTime;
	}

	/**
	 * =============== toString() ===============
	 */
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
