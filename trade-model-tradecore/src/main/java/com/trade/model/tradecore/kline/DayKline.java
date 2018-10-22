package com.trade.model.tradecore.kline;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@ToString
public class DayKline extends KlineBase implements Serializable {
	private static final long serialVersionUID = 3425086008497988083L;

	/**
	 * K线日期
	 */
	private LocalDate date;
}
