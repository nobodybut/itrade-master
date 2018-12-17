package com.trade.biz.domain.trademodel;

import com.trade.model.tradecore.enums.TradeSideEnum;
import com.trade.model.tradecore.enums.TradingHandlerTypeEnum;
import com.trade.model.tradecore.quanttrade.QuantTradePlanned;
import com.trade.model.tradecore.quanttrading.QuantTrading;
import com.trade.model.tradecore.stock.Stock;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@ToString
@Getter
@Setter
public class QuantTradingCondition implements Serializable {
	private static final long serialVersionUID = 771635790669496411L;

	/**
	 * 私有构造函数，防止代码中用 new 的方式创建对象（请统一使用当前类的 createDataModel 方法创建对象）
	 */
	protected QuantTradingCondition() {
	}

	/**
	 * 创建数据对象（代码规范：如有新增的字段，请同时修改此方法的参数）
	 *
	 * @param tradeSide
	 * @param stock
	 * @param tradingHandlerType
	 * @param quantTradePlanned
	 * @param tradePlannedCount
	 * @param quantTrading
	 * @return
	 */
	public static QuantTradingCondition createDataModel(TradeSideEnum tradeSide,
	                                                    Stock stock,
	                                                    TradingHandlerTypeEnum tradingHandlerType,
	                                                    QuantTradePlanned quantTradePlanned,
	                                                    int tradePlannedCount,
	                                                    QuantTrading quantTrading) {
		QuantTradingCondition result = new QuantTradingCondition();
		result.setTradeSide(tradeSide);
		result.setStock(stock);
		result.setTradingHandlerType(tradingHandlerType);
		result.setQuantTradePlanned(quantTradePlanned);
		result.setTradePlannedCount(tradePlannedCount);
		result.setQuantTrading(quantTrading);

		return result;
	}

	/**
	 * 交易类别枚举
	 */
	private TradeSideEnum tradeSide;

	/**
	 * 股票对象
	 */
	private Stock stock;

	/**
	 * 对应的实时交易规则处理器
	 */
	private TradingHandlerTypeEnum tradingHandlerType;

	/**
	 * 当天的股票交易计划
	 */
	private QuantTradePlanned quantTradePlanned;

	/**
	 * 当天的股票交易计划总数量
	 */
	private int tradePlannedCount;

	/**
	 * 首次交易的 quantTrading 数据
	 */
	private QuantTrading quantTrading;
}
