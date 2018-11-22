package com.trade.model.tradecore.techindicators;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;

/**
 * 技术指标：KDJ
 */
public class KDJ implements Serializable {
	private static final long serialVersionUID = 8714518071721226842L;

	/**
	 * KDJ初始化值。当小于等于此值时，KDJ指标无效
	 */
	public static final double KDJ_INIT_VALUE = -100;

	/** =============== field =============== */
	/**
	 * KDJ指标：K值
	 */
	private double k;

	/**
	 * KDJ指标：D值
	 */
	private double d;

	/**
	 * KDJ指标：J值
	 */
	private double j;

	/**
	 * =============== constructor ===============
	 */
	public KDJ() {
		this(KDJ_INIT_VALUE, KDJ_INIT_VALUE, KDJ_INIT_VALUE);
	}

	public KDJ(double k, double d, double j) {
		this.k = k;
		this.d = d;
		this.j = j;
	}

	/**
	 * =============== get/set ===============
	 */
	public double getK() {
		return k;
	}

	public void setK(double k) {
		this.k = k;
	}

	public double getD() {
		return d;
	}

	public void setD(double d) {
		this.d = d;
	}

	public double getJ() {
		return j;
	}

	public void setJ(double j) {
		this.j = j;
	}

	/**
	 * =============== toString() ===============
	 */
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
