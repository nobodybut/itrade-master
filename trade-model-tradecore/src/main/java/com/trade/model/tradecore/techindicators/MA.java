package com.trade.model.tradecore.techindicators;


import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;

/**
 * 技术指标：MA移动平均线(均线)
 */
public class MA implements Serializable {
	private static final long serialVersionUID = 8745645187661822106L;

	/**
	 * MA初始化值。当小于等于此值时，MA指标无效
	 */
	public static final double MA_INIT_VALUE = 0;

	/** =============== field =============== */
	/**
	 * 5日均线
	 */
	private double ma5;

	/**
	 * 10日均线
	 */
	private double ma10;

	/**
	 * 20日均线
	 */
	private double ma20;

	/**
	 * 30日均线
	 */
	private double ma30;

	/**
	 * 60日均线
	 */
	private double ma60;

	/**
	 * 108日均线
	 */
	private double ma108;

	/**
	 * =============== constructor ===============
	 */
	public MA() {
		this(MA_INIT_VALUE, MA_INIT_VALUE, MA_INIT_VALUE, MA_INIT_VALUE, MA_INIT_VALUE, MA_INIT_VALUE);
	}

	public MA(double ma5, double ma10, double ma20, double ma30, double ma60, double ma108) {
		this.ma5 = ma5;
		this.ma10 = ma10;
		this.ma20 = ma20;
		this.ma30 = ma30;
		this.ma60 = ma60;
		this.ma108 = ma108;
	}

	/**
	 * =============== get/set ===============
	 */
	public double getMa5() {
		return ma5;
	}

	public void setMa5(double ma5) {
		this.ma5 = ma5;
	}

	public double getMa10() {
		return ma10;
	}

	public void setMa10(double ma10) {
		this.ma10 = ma10;
	}

	public double getMa20() {
		return ma20;
	}

	public void setMa20(double ma20) {
		this.ma20 = ma20;
	}

	public double getMa30() {
		return ma30;
	}

	public void setMa30(double ma30) {
		this.ma30 = ma30;
	}

	public double getMa60() {
		return ma60;
	}

	public void setMa60(double ma60) {
		this.ma60 = ma60;
	}

	public double getMa108() {
		return ma108;
	}

	public void setMa108(double ma108) {
		this.ma108 = ma108;
	}

	/**
	 * =============== toString() ===============
	 */
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
