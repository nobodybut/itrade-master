package com.trade.model.tradecore.enums;

public enum MarketStateEnum {

	/**
	 * 无交易,美股未开盘
	 */
	NONE,

	/**
	 * 竞价
	 */
	AUCTION,

	/**
	 * 早盘前等待开盘
	 */
	WAITING_OPEN,

	/**
	 * 早盘
	 */
	MORNING,

	/**
	 * 午间休市
	 */
	REST,

	/**
	 * 午盘
	 */
	AFTERNOON,

	/**
	 * 收盘
	 */
	CLOSED,

	/**
	 * 盘前开始
	 */
	PRE_MARKET_BEGIN,

	/**
	 * 盘前结束
	 */
	PRE_MARKET_END,

	/**
	 * 盘后开始
	 */
	AFTER_HOURS_BEGIN,

	/**
	 * 盘后结束
	 */
	AFTER_HOURS_END,

	/**
	 * 夜市开盘
	 */
	NIGHT_OPEN,

	/**
	 * 夜市收盘
	 */
	NIGHT_END,

	/**
	 * 期指日市开盘
	 */
	FUTURE_DAY_OPEN,

	/**
	 * 期指日市休市
	 */
	FUTURE_DAY_BREAK,

	/**
	 * 期指日市收盘
	 */
	FUTURE_DAY_CLOSE,

	/**
	 * 期指日市等待开盘
	 */
	FUTURE_DAY_WAIT_OPEN,

	/**
	 * 港股盘后竞价
	 */
	HK_CAS,
}
