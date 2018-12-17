package com.trade.common.tradeutil.consts;

public class QuantTradeConsts {

	public static final int PLANNED_TRADE_MIN_VOLUME = 500000; // 股票每日最小成交量（小于此成交量配置的股票不进行操作）
	public static final long PLANNED_TRADE_MIN_TURNOVER = 5000000000L; // 股票每日最小成交金额（小于此成交金额配置的股票不进行操作）
	public static final int PLANNED_TRADE_STOCK_MAX_COUNT = 25;  // 最多选择多少只待购买股票
	public static final int PLANNED_KLINE_PRE_N_DAYS = 30; // 计划过程中选择今天之前多少天的K线数据进行计算
}
