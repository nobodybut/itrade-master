package com.trade.common.tradeutil.quanttradeutil;

import com.trade.common.infrastructure.util.math.CustomMathUtils;
import com.trade.model.tradecore.quanttrade.QuantTrading;

public class QuantTradingUtils {

	/**
	 * 计算当前交易的浮动盈亏金额
	 *
	 * @param quantTrading
	 * @return
	 */
	public static float calcProfitOrLessAmount(QuantTrading quantTrading) {
		return (quantTrading.getActualSellPrice() - quantTrading.getActualBuyPrice()) * quantTrading.getActualTradeVolume();
	}

	/**
	 * 计算当前交易的浮动盈亏比例
	 *
	 * @param quantTrading
	 * @return
	 */
	public static float calcProfitOrLessRate(QuantTrading quantTrading) {
		return quantTrading.isBuyStock() ? CustomMathUtils.round((quantTrading.getActualSellPrice() - quantTrading.getActualBuyPrice()) / quantTrading.getActualSellPrice(), 5) * 100
				: CustomMathUtils.round((quantTrading.getActualSellPrice() - quantTrading.getActualBuyPrice()) / quantTrading.getActualBuyPrice(), 5) * 100;
	}
}
