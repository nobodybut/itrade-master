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
	 * 交易平台ID（环球指数 200201、纽交所 200301、纳斯达克 200302、美交所 200303、中概股 200304、明星股 200305）
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
