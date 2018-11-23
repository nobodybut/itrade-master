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
	 * 公司中文名称
	 */
	private String name;

	/**
	 * 公司英文名称
	 */
	private String enName;

	/**
	 * 股票流通市值
	 */
	private long marketValue;

	/**
	 * 股票市盈率
	 */
	private float earnings;

	/**
	 * 是否已删除
	 */
	private boolean isDelete;

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

	public String getEnName() {
		return enName;
	}

	public void setEnName(String enName) {
		this.enName = enName;
	}

	public long getMarketValue() {
		return marketValue;
	}

	public void setMarketValue(long marketValue) {
		this.marketValue = marketValue;
	}

	public float getEarnings() {
		return earnings;
	}

	public void setEarnings(float earnings) {
		this.earnings = earnings;
	}

	public boolean isDelete() {
		return isDelete;
	}

	public void setDelete(boolean delete) {
		isDelete = delete;
	}

	/**
	 * =============== toString() ===============
	 */
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
