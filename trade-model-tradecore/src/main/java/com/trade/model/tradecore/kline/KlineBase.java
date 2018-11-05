package com.trade.model.tradecore.kline;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;

public class KlineBase implements Serializable {
	private static final long serialVersionUID = 5249880237962261319L;

	/** =============== field =============== */
	/**
	 * 股票 k线 ID
	 */
	private int klineID;

	/**
	 * 股票ID
	 */
	private long stockID;

	/**
	 * 开盘价
	 */
	private float open;

	/**
	 * 收盘价
	 */
	private float close;

	/**
	 * 最高价
	 */
	private float high;

	/**
	 * 最低价
	 */
	private float low;

	/**
	 * 成交量
	 */
	private int volume;

	/**
	 * 成交额
	 */
	private float turnover;

	/**
	 * 换手率
	 */
	private float turnoverRate;

	/**
	 * 涨跌幅
	 */
	private float changeRate;

	/**
	 * 昨收价
	 */
	private float lastClose;

	/**
	 * =============== get/set ===============
	 */
	public int getKlineID() {
		return klineID;
	}

	public void setKlineID(int klineID) {
		this.klineID = klineID;
	}

	public long getStockID() {
		return stockID;
	}

	public void setStockID(long stockID) {
		this.stockID = stockID;
	}

	public float getOpen() {
		return open;
	}

	public void setOpen(float open) {
		this.open = open;
	}

	public float getClose() {
		return close;
	}

	public void setClose(float close) {
		this.close = close;
	}

	public float getHigh() {
		return high;
	}

	public void setHigh(float high) {
		this.high = high;
	}

	public float getLow() {
		return low;
	}

	public void setLow(float low) {
		this.low = low;
	}

	public int getVolume() {
		return volume;
	}

	public void setVolume(int volume) {
		this.volume = volume;
	}

	public float getTurnover() {
		return turnover;
	}

	public void setTurnover(float turnover) {
		this.turnover = turnover;
	}

	public float getTurnoverRate() {
		return turnoverRate;
	}

	public void setTurnoverRate(float turnoverRate) {
		this.turnoverRate = turnoverRate;
	}

	public float getChangeRate() {
		return changeRate;
	}

	public void setChangeRate(float changeRate) {
		this.changeRate = changeRate;
	}

	public float getLastClose() {
		return lastClose;
	}

	public void setLastClose(float lastClose) {
		this.lastClose = lastClose;
	}

	/**
	 * =============== toString() ===============
	 */
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
