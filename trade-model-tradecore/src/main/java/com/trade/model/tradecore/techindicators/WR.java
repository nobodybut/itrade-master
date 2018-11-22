package com.trade.model.tradecore.techindicators;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;

/**
 * 技术指标：WR
 */
public class WR implements Serializable {
	private static final long serialVersionUID = -4406999709291396258L;

	/**
	 * WR初始化值。当小于等于此值时，WR指标无效
	 */
	public static final double WR_INIT_VALUE = -100;

	/** =============== field =============== */
	/**
	 * 6天的WR指标
	 */
	private double wr6;

	/**
	 * 10天的WR指标
	 */
	private double wr10;

	/**
	 * =============== constructor ===============
	 */
	public WR() {
		this(WR_INIT_VALUE, WR_INIT_VALUE);
	}

	public WR(double wr6, double wr10) {
		this.wr6 = wr6;
		this.wr10 = wr10;
	}

	/**
	 * =============== get/set ===============
	 */
	public double getWr6() {
		return wr6;
	}

	public void setWr6(double wr6) {
		this.wr6 = wr6;
	}

	public double getWr10() {
		return wr10;
	}

	public void setWr10(double wr10) {
		this.wr10 = wr10;
	}

	/**
	 * =============== toString() ===============
	 */
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
