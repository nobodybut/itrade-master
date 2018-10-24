package com.trade.model.tradecore.kline;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.Month;

@Getter
@Setter
@ToString
public class MonthKline extends KlineBase implements Serializable {
	private static final long serialVersionUID = 5590543129664645899L;

	/**
	 * 月份
	 */
	private Month month;
}
