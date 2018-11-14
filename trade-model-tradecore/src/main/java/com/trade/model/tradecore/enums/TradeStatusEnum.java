package com.trade.model.tradecore.enums;

public enum TradeStatusEnum {

	/**
	 * 没有交易
	 */
	NO_TRADE,

	/**
	 * 买入交易成功、卖出交易成功
	 */
	BUY_SUCCESS_SELL_SUCCESS,

	/**
	 * 买入交易成功、卖出交易失败
	 */
	BUY_SUCCESS_SELL_FAIL,

	/**
	 * 卖出交易成功、赎回交易成功
	 */
	SELL_SUCCESS_BUY_SUCCESS,

	/**
	 * 卖出交易成功、赎回交易失败
	 */
	SELL_SUCCESS_BUY_FAIL,

}

