package com.trade.biz.domain.tradequant.quanttradeanalysis;

import com.google.common.collect.Lists;
import com.trade.biz.dal.tradecore.StockDao;
import com.trade.biz.dal.tradedrds.MinuteQuoteDao;
import com.trade.biz.domain.tradequant.quanttradeplanned.QuantTradePlannedManager;
import com.trade.biz.domain.tradequant.quanttrading.QuantTradingManager;
import com.trade.common.infrastructure.util.collection.CustomListMathUtils;
import com.trade.common.infrastructure.util.logger.LogInfoUtils;
import com.trade.common.infrastructure.util.math.CustomMathUtils;
import com.trade.common.tradeutil.klineutil.KLineUtils;
import com.trade.common.tradeutil.quanttradeutil.TradeDateUtils;
import com.trade.model.tradecore.enums.StockPlateEnum;
import com.trade.model.tradecore.enums.TradeStatusEnum;
import com.trade.model.tradecore.kline.DayKLine;
import com.trade.model.tradecore.minutequote.MinuteQuote;
import com.trade.model.tradecore.quanttrade.QuantTradeAnalysis;
import com.trade.model.tradecore.quanttrade.QuantTrading;
import com.trade.model.tradecore.stock.Stock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class QuantTradeAnalysisManager {

	// 相关常量 - 2
	private static int TEST_MARKET_ID = 2; // 测试股票平台ID
	private static int TEST_PLATE_ID = StockPlateEnum.NASDAQ.getPlateID(); // 测试股票平台ID
	private static LocalDate TEST_TRADE_DATE = TradeDateUtils.getUSCurrentDate().minusDays(1); // 测试交易日期
	private static int TEST_ACCOUNT_AMOUNT = 100000000; // 测试账户金额
	private static float PLANNED_DEVIATION_RATE = 0.4F; // 计划价格偏离比例
	private static float PLANNED_SELL_OUT_PROFIT_RATE = 0.004F; // 计划卖出/赎回占开盘价的比例
	private static float PLANNED_STOP_LOSS_PROFIT_RATE = 0.02F; // 计划止损占开盘价的比例

	// 依赖注入
	@Resource
	private MinuteQuoteDao minuteQuoteDao;

	@Resource
	private StockDao stockDao;

	@Resource
	private QuantTradingManager quantTradingManager;

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
		List<String> testStockCodes = Lists.newArrayList();
		// TEST_TRADE_DATE = TEST_TRADE_DATE.minusDays(5);

		List<Stock> stocks = (TEST_MARKET_ID > 0) ? stockDao.queryListByMarketID(TEST_MARKET_ID) : stockDao.queryListByPlateID(TEST_PLATE_ID);
		for (Stock stock : stocks) {
			long stockID = stock.getStockID();
			if (testStockCodes.size() > 0 && !testStockCodes.contains(stock.getCode())) {
				continue;
			}

			List<MinuteQuote> minuteQuotes = minuteQuoteDao.queryListByStockIDAndDate(stockID, TEST_TRADE_DATE);
			if (minuteQuotes.size() > 0) {
				// 模拟单个股票按分钟线的整个交易过程及交易结果
				QuantTradeAnalysis quantTradeAnalysis = calcQuantTradeAnalysis(stockID, stock.getCode(), minuteQuotes, TEST_TRADE_DATE, TEST_ACCOUNT_AMOUNT, PLANNED_DEVIATION_RATE, PLANNED_SELL_OUT_PROFIT_RATE, PLANNED_STOP_LOSS_PROFIT_RATE);
				quantTradeAnalysisList.add(quantTradeAnalysis);

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
	 * @param stockID
	 * @param stockCode
	 * @param minuteQuotes
	 * @param tradeDate
	 * @param accountTotalAmount
	 * @param plannedDeviationRate
	 * @param plannedSellOutProfitRate
	 * @param plannedStopLossProfitRate
	 * @return
	 */
	public QuantTradeAnalysis calcQuantTradeAnalysis(long stockID,
	                                                 String stockCode,
	                                                 List<MinuteQuote> minuteQuotes,
	                                                 LocalDate tradeDate,
	                                                 int accountTotalAmount,
	                                                 float plannedDeviationRate,
	                                                 float plannedSellOutProfitRate,
	                                                 float plannedStopLossProfitRate) {
		QuantTrading quantTrading = new QuantTrading();
		float plannedBuyPrice = 0; // 计划买入价/赎回价
		float plannedSellPrice = 0; // 计划卖出价/卖空价
		float plannedProfitAmount = 0; // 计划盈利金额
		float plannedLossAmount = 0; // 计划亏损金额

		try {
			// 获取前一天和当天的日K线数据
			List<MinuteQuote> preMinuteQuotes = minuteQuoteDao.queryListByStockIDAndDate(stockID, TradeDateUtils.calcPrevTradeDate(tradeDate));
			DayKLine predayKLine = KLineUtils.calcDayKLine(preMinuteQuotes);
			DayKLine todayKLine = KLineUtils.calcDayKLine(minuteQuotes);

			if (predayKLine == null || todayKLine == null) {
				return QuantTradeAnalysis.createNoTradeDataModel(stockID, false);
			}
			boolean smallVolume = (predayKLine.getVolume() < QuantTradePlannedManager.STOCK_DAY_TRADE_MIN_VOLUME || todayKLine.getVolume() < QuantTradePlannedManager.STOCK_DAY_TRADE_MIN_VOLUME);
			if (smallVolume) {
				return QuantTradeAnalysis.createNoTradeDataModel(stockID, smallVolume);
			}

			// 计算计划当天买入点和卖出点距离开盘价的差价、当天的交易开始时间点、交易结束时间点
			int deviationAmount = KLineUtils.calDeviationAmount(predayKLine, plannedDeviationRate);

			// 循环处理每个分钟线数据
			for (MinuteQuote minuteQuote : minuteQuotes) {
				// 价格为0直接跳过
				if (minuteQuote.getPrice() <= 0) {
					continue;
				}

				// 设置当天的买入价和卖空价
				if (plannedBuyPrice == 0 || plannedSellPrice == 0 || plannedProfitAmount == 0 || plannedLossAmount == 0) {
					plannedBuyPrice = minuteQuote.getPrice() - deviationAmount;
					plannedSellPrice = minuteQuote.getPrice() + deviationAmount;
					plannedProfitAmount = minuteQuote.getPrice() * plannedSellOutProfitRate;
					plannedLossAmount = minuteQuote.getPrice() * plannedStopLossProfitRate;
				}

				// 处理具体时间点的股票实时交易
				quantTradingManager.performRealTimeTrading(stockCode, minuteQuote.getTime(), minuteQuote.getPrice(), plannedBuyPrice, plannedSellPrice,
						plannedProfitAmount, plannedLossAmount, accountTotalAmount, quantTrading, false);

				// 根据实时交易状态，处理循环退出问题
				if (quantTrading.isTradingEnding()) {
					break;
				}
			}
		} catch (Exception ex) {
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			String logData = String.format("stockID=%s", stockID);
			log.error(String.format(LogInfoUtils.HAS_DATA_TMPL, methodName, logData), ex);
		}

		if (!quantTrading.isBuyStock() && !quantTrading.isSellStock()) {
			return QuantTradeAnalysis.createNoTradeDataModel(stockID, false);
		} else if (quantTrading.getProfitOrLessAmount() != 0) {
			TradeStatusEnum tradeStatus = quantTrading.isBuyStock() ? TradeStatusEnum.BUY_SUCCESS_SELL_SUCCESS : TradeStatusEnum.SELL_SUCCESS_BUY_SUCCESS;
			float profitOrLessRate = quantTrading.isBuyStock() ? CustomMathUtils.round((quantTrading.getActualSellPrice() - quantTrading.getActualBuyPrice()) / quantTrading.getActualSellPrice(), 5) * 100
					: CustomMathUtils.round((quantTrading.getActualSellPrice() - quantTrading.getActualBuyPrice()) / quantTrading.getActualBuyPrice(), 5) * 100;
			return QuantTradeAnalysis.createDataModel(stockID, tradeStatus, tradeDate, plannedBuyPrice, plannedSellPrice, plannedProfitAmount, plannedLossAmount,
					quantTrading.getActualBuyPrice(), quantTrading.getActualSellPrice(), quantTrading.getActualTradeVolume(), quantTrading.getProfitOrLessAmount(), profitOrLessRate,
					quantTrading.getActualTradeStartTime(), quantTrading.getActualTradeEndTime(), quantTrading.getTouchProfitTimes(), quantTrading.getTouchLossTimes(), quantTrading.getReduceProfitRateMultiple());
		} else {
			TradeStatusEnum tradeStatus = quantTrading.isBuyStock() ? TradeStatusEnum.BUY_SUCCESS_SELL_FAIL : TradeStatusEnum.SELL_SUCCESS_BUY_FAIL;
			return QuantTradeAnalysis.createDataModel(stockID, tradeStatus, tradeDate, plannedBuyPrice, plannedSellPrice, plannedProfitAmount, plannedLossAmount,
					quantTrading.getActualBuyPrice(), quantTrading.getActualSellPrice(), quantTrading.getActualTradeVolume(), 0, 0,
					quantTrading.getActualTradeStartTime(), quantTrading.getActualTradeEndTime(), quantTrading.getTouchProfitTimes(), quantTrading.getTouchLossTimes(), quantTrading.getReduceProfitRateMultiple());
		}
	}
}
