package com.trade.model.tradecore.quote;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@ToString
public class MinuteQuote implements Serializable {
	private static final long serialVersionUID = 7609338314332181569L;

	/**
	 * minuteQuoteID
	 */
	private long minuteQuoteID;

	/**
	 * 股票ID
	 */
	private long stockID;

	/**
	 * 所属日期 (转换回 unix time：dateTime.plusHours(-12).toInstant(ZoneOffset.of("-4")).toEpochMilli())
	 */
	private LocalDate date;

	/**
	 * 具体时间（分钟级别）
	 */
	private LocalTime time;

	/**
	 * 价格
	 */
	private float price;

	/**
	 * 成交量
	 */
	private long volume;

	/**
	 * 成交额
	 */
	private long turnover;

	/**
	 * 涨跌幅
	 */
	private float changeRate;
}
