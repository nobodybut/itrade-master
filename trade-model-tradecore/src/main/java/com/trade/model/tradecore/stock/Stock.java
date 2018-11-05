package com.trade.model.tradecore.stock;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;

public class Stock implements Serializable {
	private static final long serialVersionUID = -2757807534164104567L;

	/** =============== field =============== */
	/**
	 * 股票ID
	 */
	private long stockID;

	/**
	 * 股票市场ID
	 */
	private int marketID;

	/**
	 * 交易所ID
	 */
	private int exchangeID;

	/**
	 * 交易平台ID（环球指数 200201、纽交所 200301、纳斯达克 200302、美交所 200303、中概股 200304、明星股 200305）
	 */
	private int plateID;

	/**
	 * 股票代码
	 */
	private String code;

	/**
	 * 股票名称
	 */
	private String name;

	/**
	 * =============== get/set ===============
	 */
	public long getStockID() {
		return stockID;
	}

	public void setStockID(long stockID) {
		this.stockID = stockID;
	}

	public int getMarketID() {
		return marketID;
	}

	public void setMarketID(int marketID) {
		this.marketID = marketID;
	}

	public int getExchangeID() {
		return exchangeID;
	}

	public void setExchangeID(int exchangeID) {
		this.exchangeID = exchangeID;
	}

	public int getPlateID() {
		return plateID;
	}

	public void setPlateID(int plateID) {
		this.plateID = plateID;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * =============== toString() ===============
	 */
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
