package com.trade.model.tradecore.kline;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.time.Month;

public class MonthKline extends KlineBase implements Serializable {
	private static final long serialVersionUID = 5590543129664645899L;

	/** =============== field =============== */
	/**
	 * 月份
	 */
	private Month month;

	/**
	 * =============== get/set ===============
	 */
	public Month getMonth() {
		return month;
	}

	public void setMonth(Month month) {
		this.month = month;
	}

	/**
	 * =============== toString() ===============
	 */
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
