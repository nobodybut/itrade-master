package com.trade.model.tradecore.kline;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class KlineBase implements Serializable {
	private static final long serialVersionUID = 5249880237962261319L;

	/**
	 * 股票 k线 ID
	 */
	private int klineID;

	/**
	 * 股票ID
	 */
	private long stockID;

	/**
	 * 开盘价
	 */
	private float open;

	/**
	 * 收盘价
	 */
	private float close;

	/**
	 * 最高价
	 */
	private float high;

	/**
	 * 最低价
	 */
	private float low;

	/**
	 * 成交量
	 */
	private int volume;

	/**
	 * 成交额
	 */
	private float turnover;

	/**
	 * 换手率
	 */
	private float turnoverRate;

	/**
	 * 涨跌幅
	 */
	private float changeRate;

	/**
	 * 昨收价
	 */
	private float lastClose;

}
