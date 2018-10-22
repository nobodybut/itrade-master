package com.trade.model.tradecore;

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
	private long id;
	private LocalDate date;
	private LocalTime time;
	private long curTime;
	private float price;
	private int volume;
	private float turnover;
	private float ratio;
}
