package com.trade.model.tradecore.techindicators;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;

/**
 * 技术指标：MACD
 */
public class MACD implements Serializable {
	private static final long serialVersionUID = -4653711105577860553L;

	/**
	 * MACD初始化值。当小于等于此值时，MACD指标无效
	 */
	public static final double MACD_INIT_VALUE = -999999;

	/** =============== field =============== */
	/**
	 * 快速移动平均值：12日EMA
	 */
	private double ema12;

	/**
	 * 慢速移动平均值：26日EMA
	 */
	private double ema26;

	/**
	 * 快速线：差离值(DIF)
	 */
	private double dif;

	/**
	 * 慢速线：离差平均值：9日移动平均值：9日EMA
	 */
	private double dea;

	/**
	 * MACD柱
	 */
	private double bar;

	/**
	 * this为昨天的MACD指标，superMACD为今天的MACD指标
	 */
	private MACD superMACD;

	/**
	 * =============== constructor ===============
	 */
	public MACD() {
		this(MACD_INIT_VALUE, MACD_INIT_VALUE, MACD_INIT_VALUE, MACD_INIT_VALUE, MACD_INIT_VALUE);
	}

	public MACD(double ema12, double ema26, double dif, double dea, double bar) {
		this.ema12 = ema12;
		this.ema26 = ema26;
		this.dif = dif;
		this.dea = dea;
		this.bar = bar;
	}

	/**
	 * =============== get/set ===============
	 */
	public double getEma12() {
		return ema12;
	}

	public void setEma12(double ema12) {
		this.ema12 = ema12;
	}

	public double getEma26() {
		return ema26;
	}

	public void setEma26(double ema26) {
		this.ema26 = ema26;
	}

	public double getDif() {
		return dif;
	}

	public void setDif(double dif) {
		this.dif = dif;
	}

	public double getDea() {
		return dea;
	}

	public void setDea(double dea) {
		this.dea = dea;
	}

	public double getBar() {
		return bar;
	}

	public void setBar(double bar) {
		this.bar = bar;
	}

	public MACD getSuperMACD() {
		return superMACD;
	}

	public void setSuperMACD(MACD superMACD) {
		this.superMACD = superMACD;
	}

	/**
	 * =============== toString() ===============
	 */
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
