package com.trade.biz.domain.tradequant.quanttrading.stokerulehandler;

public abstract class AbstractStockRuleHandler {

	/**
	 * 处理具体的股票规则
	 *
	 * @return
	 */
	public abstract boolean execute();

	/**
	 * 写文件日志
	 */
	public void writeFileLog() {

	}

	/**
	 * 写数据库日志
	 */
	public void writeDBLog() {

	}
}
