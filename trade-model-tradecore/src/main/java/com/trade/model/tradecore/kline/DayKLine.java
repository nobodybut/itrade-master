package com.trade.model.tradecore.kline;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.time.LocalDate;

public class DayKLine extends KLineBase implements Serializable {
	private static final long serialVersionUID = 3425086008497988083L;

	/** =============== field =============== */
	/**
	 * K线日期
	 */
	private LocalDate date;

	/**
	 * =============== get/set ===============
	 */
	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	/**
	 * =============== toString() ===============
	 */
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
