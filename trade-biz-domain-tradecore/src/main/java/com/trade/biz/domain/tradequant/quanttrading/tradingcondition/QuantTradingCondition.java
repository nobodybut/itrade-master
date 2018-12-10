package com.trade.biz.domain.tradequant.quanttrading.tradingcondition;

import com.trade.model.tradecore.enums.TradeSideEnum;
import com.trade.model.tradecore.quanttrade.QuantTradePlanned;
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
	 * @param quantTradePlanned
	 * @param tradePlannedCount
	 * @return
	 */
	public static QuantTradingCondition createDataModel(TradeSideEnum tradeSide,
	                                                    Stock stock,
	                                                    QuantTradePlanned quantTradePlanned,
	                                                    int tradePlannedCount) {
		QuantTradingCondition result = new QuantTradingCondition();
		result.setTradeSide(tradeSide);
		result.setStock(stock);
		result.setQuantTradePlanned(quantTradePlanned);
		result.setTradePlannedCount(tradePlannedCount);

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
	 * 当天的股票交易计划
	 */
	private QuantTradePlanned quantTradePlanned;

	/**
	 * 当天的股票交易计划总数量
	 */
	private int tradePlannedCount;
}
