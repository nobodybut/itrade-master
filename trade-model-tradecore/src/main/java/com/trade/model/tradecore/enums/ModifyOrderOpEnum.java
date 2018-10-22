package com.trade.model.tradecore.enums;

public enum ModifyOrderOpEnum {

	/**
	 * 未知
	 */
	NONE,

	/**
	 * 修改订单的数量、价格
	 */
	NORMAL,

	/**
	 * 取消订单
	 */
	CANCEL,

	/**
	 * 使订单失效
	 */
	DISABLE,

	/**
	 * 使订单生效
	 */
	ENABLE,

	/**
	 * 删除订单
	 */
	DELETE,
}
