package com.trade.model.tradecore.stock;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class Stock implements Serializable {
	private static final long serialVersionUID = -2757807534164104567L;

	/**
	 * 股票ID
	 */
	private long stockID;

	/**
	 * 股票市场ID
	 */
	private int marketID;

	/**
	 * 交易所ID
	 */
	private int exchangeID;

	/**
	 * 交易平台ID
	 */
	private int plateID;

	/**
	 * 股票代码
	 */
	private String code;

	/**
	 * 股票名称
	 */
	private String name;
}
