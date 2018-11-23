package com.trade.model.tradecore.kline;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;

public class KLineBase implements Serializable {
	private static final long serialVersionUID = 5249880237962261319L;

	/** =============== field =============== */
	/**
	 * 股票 k线 ID
	 */
	private int kLineID;

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
	private long volume;

	/**
	 * 成交额
	 */
	private long turnover;

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

	// 振　幅：1.94%

	/**
	 * KDJ 指标 json
	 */
	private String kdjJson = "";

	/**
	 * MACD 指标 json
	 */
	private String macdJson = "";

	/**
	 * WR 指标 json
	 */
	private String wrJson = "";

	/**
	 * =============== get/set ===============
	 */
	public int getkLineID() {
		return kLineID;
	}

	public void setkLineID(int kLineID) {
		this.kLineID = kLineID;
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

	public String getKdjJson() {
		return kdjJson;
	}

	public void setKdjJson(String kdjJson) {
		this.kdjJson = kdjJson;
	}

	public String getMacdJson() {
		return macdJson;
	}

	public void setMacdJson(String macdJson) {
		this.macdJson = macdJson;
	}

	public String getWrJson() {
		return wrJson;
	}

	public void setWrJson(String wrJson) {
		this.wrJson = wrJson;
	}

	/**
	 * =============== toString() ===============
	 */
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
