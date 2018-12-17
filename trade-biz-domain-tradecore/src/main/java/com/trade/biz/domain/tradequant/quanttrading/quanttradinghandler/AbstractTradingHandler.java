package com.trade.biz.domain.tradequant.quanttrading.quanttradinghandler;

import com.trade.biz.dal.tradecore.QuantTradeActualDao;
import com.trade.biz.domain.trademodel.QuantTradingCondition;
import com.trade.model.tradecore.enums.TradingHandlerTypeEnum;
import com.trade.biz.domain.tradequant.futu.FutunnAccountHelper;
import com.trade.biz.domain.tradequant.futu.FutunnCreateOrderHelper;
import com.trade.biz.domain.tradequant.quanttrading.QuantTradingQueue;

public abstract class AbstractTradingHandler {

	/**
	 * 处理具体的股票规则
	 *
	 * @param quantTradingCondition
	 * @param futunnAccountHelper
	 * @param futunnCreateOrderHelper
	 * @param quantTradeActualDao
	 * @param quantTradingQueue
	 */
	public abstract void execute(QuantTradingCondition quantTradingCondition,
	                             FutunnAccountHelper futunnAccountHelper,
	                             FutunnCreateOrderHelper futunnCreateOrderHelper,
	                             QuantTradeActualDao quantTradeActualDao,
	                             QuantTradingQueue quantTradingQueue);

	/**
	 * 获取当前对应的实时交易规则处理器
	 *
	 * @return
	 */
	public abstract TradingHandlerTypeEnum getTradingHandlerType();
}
