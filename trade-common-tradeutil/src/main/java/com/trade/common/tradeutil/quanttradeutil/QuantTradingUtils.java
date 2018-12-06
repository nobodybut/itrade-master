package com.trade.common.tradeutil.quanttradeutil;

import com.trade.common.infrastructure.util.math.CustomMathUtils;
import com.trade.common.infrastructure.util.math.CustomNumberUtils;
import com.trade.common.infrastructure.util.string.CustomStringUtils;
import com.trade.model.tradecore.quanttrading.QuantTrading;

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

	/**
	 * 从 json 中解析出价格数据
	 *
	 * @param json
	 * @param rootName
	 * @return
	 */
	public static float getPriceFromJson(String json, String rootName) {
		return CustomNumberUtils.toFloat(CustomStringUtils.substringBetween(json, String.format("\"%s\"", rootName), ",", "\"", "\""));
	}
}
