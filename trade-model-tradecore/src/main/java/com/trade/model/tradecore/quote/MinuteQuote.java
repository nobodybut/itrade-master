package com.trade.model.tradecore.quote;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

public class MinuteQuote implements Serializable {
	private static final long serialVersionUID = 7609338314332181569L;

	/** =============== field =============== */
	/**
	 * minuteQuoteID
	 */
	private long minuteQuoteID;

	/**
	 * 股票ID
	 */
	private long stockID;

	/**
	 * 所属日期 (转换回 unix time：dateTime.plusHours(-12).toInstant(ZoneOffset.of("-4")).toEpochMilli())
	 */
	private LocalDate date;

	/**
	 * 具体时间（分钟级别）
	 */
	private LocalTime time;

	/**
	 * 价格
	 */
	private float price;

	/**
	 * 成交量
	 */
	private long volume;

	/**
	 * 成交额
	 */
	private long turnover;

	/**
	 * 涨跌幅
	 */
	private float changeRate;

	/**
	 * =============== get/set ===============
	 */
	public long getMinuteQuoteID() {
		return minuteQuoteID;
	}

	public void setMinuteQuoteID(long minuteQuoteID) {
		this.minuteQuoteID = minuteQuoteID;
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

	public LocalTime getTime() {
		return time;
	}

	public void setTime(LocalTime time) {
		this.time = time;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public long getVolume() {
		return volume;
	}

	public void setVolume(long volume) {
		this.volume = volume;
	}

	public long getTurnover() {
		return turnover;
	}

	public void setTurnover(long turnover) {
		this.turnover = turnover;
	}

	public float getChangeRate() {
		return changeRate;
	}

	public void setChangeRate(float changeRate) {
		this.changeRate = changeRate;
	}

	/**
	 * =============== toString() ===============
	 */
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
