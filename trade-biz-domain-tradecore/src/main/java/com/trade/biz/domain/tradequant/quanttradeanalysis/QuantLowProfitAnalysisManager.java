package com.trade.biz.domain.tradequant.quanttradeanalysis;

import com.google.common.collect.Lists;
import com.trade.biz.dal.tradecore.DayKLineDao;
import com.trade.biz.dal.tradecore.StockDao;
import com.trade.biz.dal.tradedrds.MinuteQuoteDao;
import com.trade.biz.domain.tradequant.quanttrading.quanttradinghandler.LowProfitTradingHandler;
import com.trade.common.infrastructure.util.collection.CustomListMathUtils;
import com.trade.common.infrastructure.util.logger.LogInfoUtils;
import com.trade.common.tradeutil.consts.QuantTradeConsts;
import com.trade.common.tradeutil.klineutil.DayKLineUtils;
import com.trade.common.tradeutil.quanttradeutil.QuantTradingUtils;
import com.trade.common.tradeutil.quanttradeutil.TradeDateUtils;
import com.trade.model.tradecore.enums.StockPlateEnum;
import com.trade.model.tradecore.enums.TradeStatusEnum;
import com.trade.model.tradecore.kline.DayKLine;
import com.trade.model.tradecore.minutequote.MinuteQuote;
import com.trade.model.tradecore.quanttrade.QuantTradeAnalysis;
import com.trade.model.tradecore.quanttrading.QuantTrading;
import com.trade.model.tradecore.stock.Stock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class QuantLowProfitAnalysisManager {

	// 相关常量
	private static int TEST_MARKET_ID = 2; // 测试股票平台ID
	private static int TEST_PLATE_ID = StockPlateEnum.NASDAQ.getPlateID(); // 测试股票平台ID
	private static int TEST_ACCOUNT_AMOUNT = 100000000; // 测试账户金额

	// 依赖注入
	@Resource
	private StockDao stockDao;

	@Resource
	private MinuteQuoteDao minuteQuoteDao;

	@Resource
	private DayKLineDao dayKLineDao;

	/**
	 * 执行指定或全部股票列表的模拟测试
	 */
	public void execute() {
		List<QuantTradeAnalysis> quantTradeAnalysisList = Lists.newArrayList();
		List<QuantTradeAnalysis> notradeQuantTradeAnalysisList = Lists.newArrayList();
		List<QuantTradeAnalysis> tradeFailQuantTradeAnalysisList = Lists.newArrayList();
		List<QuantTradeAnalysis> tradeSuccessQuantTradeAnalysisList = Lists.newArrayList();
		List<QuantTradeAnalysis> tradeProfitQuantTradeAnalysisList = Lists.newArrayList();
		List<QuantTradeAnalysis> tradeLossQuantTradeAnalysisList = Lists.newArrayList();
		int totalAmount = 0;
		int profitAmount = 0;

		// 临时修改常量
		List<String> testStockCodes = Lists.newArrayList(); // "BABA"
		LocalDate tradeDate = TradeDateUtils.calcPrevTradeDate(TradeDateUtils.getUsCurrentDate());

		List<Stock> stocks = (TEST_MARKET_ID > 0) ? stockDao.queryListByMarketID(TEST_MARKET_ID) : stockDao.queryListByPlateID(TEST_PLATE_ID);
		for (Stock stock : stocks) {
			long stockID = stock.getStockID();
			if (testStockCodes.size() > 0 && !testStockCodes.contains(stock.getCode())) {
				continue;
			}

			List<MinuteQuote> minuteQuotes = minuteQuoteDao.queryListByStockIDAndDate(stockID, tradeDate);
			if (minuteQuotes.size() > 0) {
				// 模拟单个股票按分钟线的整个交易过程及交易结果
				QuantTradeAnalysis quantTradeAnalysis = calcQuantTradeAnalysis(stock, minuteQuotes, tradeDate, TEST_ACCOUNT_AMOUNT, LowProfitTradingHandler.PLANNED_DEVIATION_RATE,
						LowProfitTradingHandler.PLANNED_SELL_OUT_PROFIT_RATE, LowProfitTradingHandler.PLANNED_STOP_LOSS_PROFIT_RATE);
				if (quantTradeAnalysis != null) {
					quantTradeAnalysisList.add(quantTradeAnalysis);
				} else {
					continue;
				}

				// 处理模拟交易统计数据
				notradeQuantTradeAnalysisList = quantTradeAnalysisList.stream().filter(x -> x.getTradeStatus() == TradeStatusEnum.NO_TRADE).collect(Collectors.toList());
				tradeFailQuantTradeAnalysisList = quantTradeAnalysisList.stream().filter(x -> x.getTradeStatus() == TradeStatusEnum.BUY_SUCCESS_SELL_FAIL || x.getTradeStatus() == TradeStatusEnum.SELL_SUCCESS_BUY_FAIL).collect(Collectors.toList());
				tradeSuccessQuantTradeAnalysisList = quantTradeAnalysisList.stream().filter(x -> x.getTradeStatus() == TradeStatusEnum.BUY_SUCCESS_SELL_SUCCESS || x.getTradeStatus() == TradeStatusEnum.SELL_SUCCESS_BUY_SUCCESS).collect(Collectors.toList());
				tradeProfitQuantTradeAnalysisList = quantTradeAnalysisList.stream().filter(x -> x.getProfitOrLessAmount() > 0).collect(Collectors.toList());
				tradeLossQuantTradeAnalysisList = quantTradeAnalysisList.stream().filter(x -> x.getProfitOrLessAmount() < 0).collect(Collectors.toList());
				totalAmount = CustomListMathUtils.calListIntTotal(quantTradeAnalysisList.stream().map(x -> (int) (x.getTotalTradeAmount() / 1000)).collect(Collectors.toList()));
				profitAmount = CustomListMathUtils.calListIntTotal(quantTradeAnalysisList.stream().map(x -> (int) (x.getProfitOrLessAmount() / 1000)).collect(Collectors.toList()));
				if (quantTradeAnalysisList.size() > 0) {
				}
			}
		}

		if (notradeQuantTradeAnalysisList.size() + tradeFailQuantTradeAnalysisList.size() + tradeSuccessQuantTradeAnalysisList.size() + tradeProfitQuantTradeAnalysisList.size() + tradeLossQuantTradeAnalysisList.size() + totalAmount + profitAmount > 0) {
			return;
		}
	}

	/**
	 * 模拟单个股票按分钟线的整个交易过程及交易结果
	 *
	 * @param stock
	 * @param minuteQuotes
	 * @param tradeDate
	 * @param accountTotalAmount
	 * @param plannedDeviationRate
	 * @param plannedSellOutProfitRate
	 * @param plannedStopLossProfitRate
	 * @return
	 */
	public QuantTradeAnalysis calcQuantTradeAnalysis(Stock stock,
	                                                 List<MinuteQuote> minuteQuotes,
	                                                 LocalDate tradeDate,
	                                                 int accountTotalAmount,
	                                                 float plannedDeviationRate,
	                                                 float plannedSellOutProfitRate,
	                                                 float plannedStopLossProfitRate) {
		QuantTrading quantTrading = new QuantTrading();
		float openPrice = 0; // 开盘价
		float plannedBuyPrice = 0; // 计划买入价/赎回价
		float plannedSellShortPrice = 0; // 计划卖出价/卖空价
		float plannedProfitAmount = 0; // 计划盈利金额
		float plannedLossAmount = 0; // 计划亏损金额

		try {
			// 获取当天、前一天、前2日的日K线数据
			DayKLine todayKLine = dayKLineDao.queryByStockIDAndDate(stock.getStockID(), tradeDate);
			DayKLine predayKLine = dayKLineDao.queryByStockIDAndDate(stock.getStockID(), TradeDateUtils.calcPrevTradeDate(tradeDate));
			DayKLine prePredayKLine = dayKLineDao.queryByStockIDAndDate(stock.getStockID(), TradeDateUtils.calcPrevTradeDate(TradeDateUtils.calcPrevTradeDate(tradeDate)));

			if (predayKLine == null || todayKLine == null) {
				return QuantTradeAnalysis.createNoTradeDataModel(stock.getStockID(), false);
			}
			boolean smallVolume = (predayKLine.getVolume() < QuantTradeConsts.PLANNED_TRADE_MIN_VOLUME
					|| todayKLine.getVolume() < QuantTradeConsts.PLANNED_TRADE_MIN_VOLUME
					|| predayKLine.getTurnover() < QuantTradeConsts.PLANNED_TRADE_MIN_TURNOVER
					|| todayKLine.getTurnover() < QuantTradeConsts.PLANNED_TRADE_MIN_TURNOVER);
			if (smallVolume) {
				return QuantTradeAnalysis.createNoTradeDataModel(stock.getStockID(), smallVolume);
			}

			// 计算计划当天买入点和卖出点距离开盘价的差价、当天的交易开始时间点、交易结束时间点
			int deviationAmount = DayKLineUtils.calcDeviationAmount(predayKLine, prePredayKLine, plannedDeviationRate);
			if (deviationAmount == 0) {
				return null;
			}

			// 测试计算实时交易价格变化趋势
			// testTradingPriceChangeTrend(minuteQuotes);

			// 测试分钟线KDJ指标计算
			// testMinuteQuotesKDJ(minuteQuotes);

			// 循环处理每个分钟线数据，进行模拟交易测试
			LowProfitTradingHandler lowProfitTradingHandler = new LowProfitTradingHandler();
			for (MinuteQuote minuteQuote : minuteQuotes) {
				// 价格为0直接跳过
				if (minuteQuote.getPrice() <= 0) {
					continue;
				}

				// 设置当天的买入价和卖空价
				if (openPrice == 0) {
					openPrice = minuteQuote.getPrice();
					plannedBuyPrice = openPrice - deviationAmount;
					plannedSellShortPrice = openPrice + deviationAmount;
					plannedProfitAmount = openPrice * plannedSellOutProfitRate;
					plannedLossAmount = openPrice * plannedStopLossProfitRate;
				}

				// 处理具体时间点的股票实时交易
				lowProfitTradingHandler.performRealTimeTrading(stock, null, minuteQuote.getTime(), openPrice, minuteQuote.getPrice(), plannedBuyPrice, plannedSellShortPrice,
						plannedProfitAmount, plannedLossAmount, accountTotalAmount, 100, quantTrading, false, null, null, null);

				// 根据实时交易状态，处理循环退出问题
				if (quantTrading.isTradingFinished()) {
					break;
				}
			}
		} catch (Exception ex) {
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			String logData = String.format("stockID=%s", stock.getStockID());
			log.error(String.format(LogInfoUtils.HAS_DATA_TMPL, methodName, logData), ex);
		}

		if (!quantTrading.isBuyStock() && !quantTrading.isSellStock()) {
			return QuantTradeAnalysis.createNoTradeDataModel(stock.getStockID(), false);
		} else if (quantTrading.getProfitOrLessAmount() != 0) {
			TradeStatusEnum tradeStatus = quantTrading.isBuyStock() ? TradeStatusEnum.BUY_SUCCESS_SELL_SUCCESS : TradeStatusEnum.SELL_SUCCESS_BUY_SUCCESS;
			float profitOrLessRate = QuantTradingUtils.calcProfitOrLessRate(quantTrading);
			return QuantTradeAnalysis.createDataModel(stock.getStockID(), tradeStatus, tradeDate, plannedBuyPrice, plannedSellShortPrice, plannedProfitAmount, plannedLossAmount,
					quantTrading.getActualBuyPrice(), quantTrading.getActualSellPrice(), quantTrading.getActualTradeVolume(), quantTrading.getProfitOrLessAmount(), profitOrLessRate,
					quantTrading.getActualTradeStartTime(), quantTrading.getActualTradeEndTime(), quantTrading.getTouchProfitTimes(), quantTrading.getTouchLossTimes(), quantTrading.getReduceProfitRateMultiple());
		} else {
			TradeStatusEnum tradeStatus = quantTrading.isBuyStock() ? TradeStatusEnum.BUY_SUCCESS_SELL_FAIL : TradeStatusEnum.SELL_SUCCESS_BUY_FAIL;
			return QuantTradeAnalysis.createDataModel(stock.getStockID(), tradeStatus, tradeDate, plannedBuyPrice, plannedSellShortPrice, plannedProfitAmount, plannedLossAmount,
					quantTrading.getActualBuyPrice(), quantTrading.getActualSellPrice(), quantTrading.getActualTradeVolume(), 0, 0,
					quantTrading.getActualTradeStartTime(), quantTrading.getActualTradeEndTime(), quantTrading.getTouchProfitTimes(), quantTrading.getTouchLossTimes(), quantTrading.getReduceProfitRateMultiple());
		}
	}

//	/**
//	 * 测试计算实时交易价格变化趋势
//	 *
//	 * @param minuteQuotes
//	 */
//	private void testTradingPriceChangeTrend(List<MinuteQuote> minuteQuotes) {
//		LowProfitTradingHandler lowProfitTradingHandler = new LowProfitTradingHandler();
//		QuantTrading quantTrading = new QuantTrading();
//		float openPrice = 0;
//
//		for (MinuteQuote minuteQuote : minuteQuotes) {
//			// 价格为0直接跳过
//			if (minuteQuote.getPrice() <= 0) {
//				continue;
//			}
//
//			// 设置当天的开盘价
//			if (openPrice == 0) {
//				openPrice = minuteQuote.getPrice();
//			}
//
//			// 计算实时交易价格变化趋势
//			lowProfitTradingHandler.calcTradingPriceChangeTrend(quantTrading, openPrice, minuteQuote.getPrice(), minuteQuote.getTime());
//		}
//
//		if (quantTrading.getOptionTypeChangeTimes().size() > 0) {
//			return;
//		}
//	}
//
//	/**
//	 * 测试分钟线KDJ指标计算
//	 *
//	 * @param minuteQuotes
//	 */
//	private void testMinuteQuotesKDJ(List<MinuteQuote> minuteQuotes) {
//		List<String> list = Lists.newArrayList();
//		MinuteQuoteKDJUtils.calcAndFillMinuteQuoteKDJ(minuteQuotes);
//		for (MinuteQuote minuteQuote : minuteQuotes) {
//			if (!Strings.isNullOrEmpty(minuteQuote.getKdjJson())) {
//				list.add(CustomDateFormatUtils.formatTime(minuteQuote.getTime()) + " " + minuteQuote.getKdjJson());
//			}
//		}
//		if (list.size() > 0) {
//			return;
//		}
//	}
}
