package com.trade.common.tradeutil.consts;

public class QuantTradeConsts {

	public static final float PLANNED_DEVIATION_RATE = 0.4F; // 计划价格偏离比例（默认：0.4F）
	public static final float PLANNED_SELL_OUT_PROFIT_RATE = 0.005F; // 计划卖出/赎回占开盘价的比例
	public static final float PLANNED_STOP_LOSS_PROFIT_RATE = 0.02F; // 计划止损占开盘价的比例
	public static final int PLANNED_TRADE_MIN_VOLUME = 500000; // 股票每日最小成交量（小于此成交量配置的股票不进行操作）
	public static final long PLANNED_TRADE_MIN_TURNOVER = 5000000000L; // 股票每日最小成交金额（小于此成交金额配置的股票不进行操作）
	public static final int PLANNED_TRADE_STOCK_MAX_COUNT = 25;  // 最多选择多少只待购买股票
}
