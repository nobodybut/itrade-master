package com.trade.biz.domain.tradequant.quanttrading.quanttradinghandler;

import com.trade.biz.dal.tradecore.QuantTradeActualDao;
import com.trade.biz.domain.trademodel.QuantTradingCondition;
import com.trade.biz.domain.tradequant.futu.FutunnAccountHelper;
import com.trade.biz.domain.tradequant.futu.FutunnCreateOrderHelper;
import com.trade.biz.domain.tradequant.quanttrading.QuantTradingQueue;
import com.trade.model.tradecore.enums.TradingHandlerTypeEnum;
import org.springframework.stereotype.Component;

/**
 * 价格缺口 实时交易规则处理器
 */
@Component
public class PriceGapTradingHandler extends AbstractTradingHandler {

	@Override
	public void execute(QuantTradingCondition quantTradingCondition,
	                    FutunnAccountHelper futunnAccountHelper,
	                    FutunnCreateOrderHelper futunnCreateOrderHelper,
	                    QuantTradeActualDao quantTradeActualDao,
	                    QuantTradingQueue quantTradingQueue) {
		// ..
		// ..
	}

	@Override
	public TradingHandlerTypeEnum getTradingHandlerType() {
		return TradingHandlerTypeEnum.PRICE_GAP_HANDLER;
	}
}
