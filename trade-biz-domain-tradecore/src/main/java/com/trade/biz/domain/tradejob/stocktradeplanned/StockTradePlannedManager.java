package com.trade.biz.domain.tradejob.stocktradeplanned;

import com.google.common.collect.Lists;
import com.trade.biz.dal.tradecore.StockDao;
import com.trade.biz.dal.tradecore.StockTradePlannedDao;
import com.trade.biz.dal.tradedrds.MinuteQuoteDao;
import com.trade.biz.domain.tradejob.futu.FutunnAccountHelper;
import com.trade.common.infrastructure.util.date.CustomDateFormatUtils;
import com.trade.common.infrastructure.util.date.CustomDateUtils;
import com.trade.common.infrastructure.util.logger.LogInfoUtils;
import com.trade.common.tradeutil.klineutil.KLineUtils;
import com.trade.common.tradeutil.stocktradeutil.StockTradeDateUtils;
import com.trade.common.tradeutil.stockutil.TradeDateUtils;
import com.trade.model.tradecore.enums.StockPlateEnum;
import com.trade.model.tradecore.enums.TradeStatusEnum;
import com.trade.model.tradecore.kline.DayKLine;
import com.trade.model.tradecore.quote.MinuteQuote;
import com.trade.model.tradecore.stock.Stock;
import com.trade.model.tradecore.stocktrade.StockTradeAnalysisResult;
import com.trade.model.tradecore.stocktrade.StockTradePlanned;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class StockTradePlannedManager {

	// 相关常量
	private static LocalDate PLANNED_TRADE_DATE = TradeDateUtils.getUSCurrentDate(); // 当前交易日期
	private static int PLANNED_TRADE_ANALYSIS_PRE_DATES = 5; // 分析之前股票的交易天数
	private static final int PLANNED_TRADE_MAX_COUNT = 200; // 最终选择多少只待购买股票
	private static final int BEST_TRADE_TIME_INTERVAL_MINUTES = 60; // 最佳的2次交易时间间隔（分钟数）
	private static final List<Float> PLANNED_DEVIATION_RATES = Lists.newArrayList(0.4F);
	private static final List<Float> PLANNED_SELL_OUT_PROFIT_RATES = Lists.newArrayList(0.08F, 0.06F, 0.05F, 0.04F);
	private static final List<Float> PLANNED_STOP_LOSS_PROFIT_RATES = Lists.newArrayList(0.2F);
	private static final int MIN_SENCE_STOCK_TRADE_PLANNED_COUNT = 1;
	private static final int SENCES_PLANNEDS_SKIP_TOP_N = 1; // 计算每日计划交易结果时，排除前N个方案的高分计划交易数据
	private static final int DAYS_PLANNEDS_SKIP_TOP_N = 1; // 计算最终结果时，排除前N天的高分计划交易数据

	// 依赖注入
	@Resource
	private StockDao stockDao;

	@Resource
	private MinuteQuoteDao minuteQuoteDao;

	@Resource
	private FutunnAccountHelper futunnAccountHelper;

	@Resource
	private MinuteQuoteAnalysis minuteQuoteAnalysis;

	@Resource
	private StockTradePlannedDao stockTradePlannedDao;

	/**
	 * 处理全部股票计划交易逻辑
	 */
	public void execute() {
		try {
			List<StockTradePlanned> stockTradePlanneds = Lists.newArrayList();

			// 临时更改常量
//			PLANNED_TRADE_DATE = PLANNED_TRADE_DATE.minusDays(2);
//			PLANNED_TRADE_ANALYSIS_PRE_DATES = 2;

			// 获取富途账户剩余资金金额
			int accountTotalAmount = futunnAccountHelper.fetchAccountTotalAmount();

			// 计算全部股票计划交易数据（包含计划交易评分）
			List<Stock> allStocks = stockDao.queryListByPlateID(StockPlateEnum.NASDAQ.getPlateID()); // stockDao.queryListByMarketID(2);
			for (int i = 0; i < allStocks.size(); i++) {
				Stock stock = allStocks.get(i);

				// 环球指数不处理
				if (stock.getPlateID() == StockPlateEnum.GLOBAL.getPlateID()) {
					continue;
				}

				// 循环处理每只股票的不同场景，计算得出最终的股票买入计划
				StockTradePlanned stockTradePlanned = calcStockTradePlanned(stock, accountTotalAmount);
				if (stockTradePlanned != null) {
					stockTradePlanneds.add(stockTradePlanned);
				}
			}

			// 按计划交易综合评分排序结果
			stockTradePlanneds = getNonZeroAndSortStockTradePlanneds(stockTradePlanneds);
			stockTradePlanneds = stockTradePlanneds.stream().limit(PLANNED_TRADE_MAX_COUNT).collect(Collectors.toList());

			// 把计算结果写入数据库
			for (StockTradePlanned stockTradePlanned : stockTradePlanneds) {
				stockTradePlannedDao.insertOrUpdate(stockTradePlanned);
			}

			// 记录文件日志
			log.info("StockTradePlanned SUCCESS! plannedCount={}, plannedTradeDate={}", stockTradePlanneds.size(), CustomDateFormatUtils.formatDate(PLANNED_TRADE_DATE));
		} catch (Exception ex) {
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			log.error(String.format(LogInfoUtils.NO_DATA_TMPL, methodName), ex);
		}
	}

	/**
	 * 计算单个股票计划交易数据（包含计划交易评分）
	 *
	 * @param stock
	 * @param accountTotalAmount
	 * @return
	 */
	private StockTradePlanned calcStockTradePlanned(Stock stock, int accountTotalAmount) {
		// 计算计划当天买入点和卖出点距离开盘价的差价
		List<MinuteQuote> preMinuteQuotes = minuteQuoteDao.queryListByStockIDAndDate(stock.getStockID(), StockTradeDateUtils.calcPrevTradeDate(PLANNED_TRADE_DATE));
		DayKLine predayKLine = KLineUtils.calcDayKLine(preMinuteQuotes);
		if (predayKLine == null) {
			return null;
		}

		// 循环处理 PLANNED_TRADE_ANALYSIS_PRE_DATES 个不同日期的股票模拟交易，计算出全部日期的股票计划交易数据列表
		List<StockTradePlanned> dayStockTradePlanneds = Lists.newArrayList();
		List<LocalDate> dates = StockTradeDateUtils.calcPrevTradeDates(PLANNED_TRADE_DATE, PLANNED_TRADE_ANALYSIS_PRE_DATES);
		LinkedHashMap<LocalDate, List<MinuteQuote>> minuteQuotesMap = minuteQuoteDao.queryListMapByStockIDAndDate(stock.getStockID(), dates);
		for (Map.Entry<LocalDate, List<MinuteQuote>> entry : minuteQuotesMap.entrySet()) {
			LocalDate tradeDate = entry.getKey();
			List<MinuteQuote> minuteQuotes = entry.getValue();

			// 按不同的场景配置，进行当前日期的股票模拟交易，并计算出当前日期的计划交易数据
			StockTradePlanned dayStockTradePlanned = calcDayStockTradePlanned(stock, accountTotalAmount, tradeDate, minuteQuotes, predayKLine);
			if (dayStockTradePlanned != null) {
				// 加入日期计划交易结果数据
				dayStockTradePlanneds.add(dayStockTradePlanned);
			} else {
				// 如果股票计划交易数据存在日期不全的问题，则直接返回 null
				// return null;
			}
		}

		// 如果没有计算出计划交易数据，则直接返回 null
		if (dayStockTradePlanneds.size() < DAYS_PLANNEDS_SKIP_TOP_N) {
			return null;
		}

		// 排除前 DAYS_PLANNEDS_SKIP_TOP_N 个方案的高分计划交易数据，计算该股票的最终结果时
		dayStockTradePlanneds = getNonZeroAndSortStockTradePlanneds(dayStockTradePlanneds);
		if (dayStockTradePlanneds.size() > DAYS_PLANNEDS_SKIP_TOP_N) {
			return dayStockTradePlanneds.stream().skip(DAYS_PLANNEDS_SKIP_TOP_N).findFirst().get();
		} else {
			return dayStockTradePlanneds.get(dayStockTradePlanneds.size() - 1);
		}
	}

	/**
	 * 按不同的场景配置，进行当前日期的股票模拟交易，并计算出当前日期的计划交易数据
	 *
	 * @param stock
	 * @param accountTotalAmount
	 * @param tradeDate
	 * @param minuteQuotes
	 * @param predayKLine
	 * @return
	 */
	private StockTradePlanned calcDayStockTradePlanned(Stock stock, int accountTotalAmount, LocalDate tradeDate, List<MinuteQuote> minuteQuotes, DayKLine predayKLine) {
		// 按不同的场景配置，进行当前日期的股票模拟交易，并计算出初步计划交易数据列表
		List<StockTradePlanned> senceStockTradePlanneds = Lists.newArrayList();
		for (float plannedDeviationRate : PLANNED_DEVIATION_RATES) {
			for (float plannedSellOutProfitRate : PLANNED_SELL_OUT_PROFIT_RATES) {
				for (float plannedStopLossProfitRate : PLANNED_STOP_LOSS_PROFIT_RATES) {
					// 模拟单个股票按分钟线的整个交易过程及交易结果
					StockTradeAnalysisResult stockTradeAnalysisResult = minuteQuoteAnalysis.calcStockTradeAnalysisResult(stock.getStockID(), minuteQuotes,
							tradeDate, accountTotalAmount, plannedDeviationRate, plannedSellOutProfitRate, plannedStopLossProfitRate);

					// 如果发现其中有一次交易量不够，则直接退出
					if (stockTradeAnalysisResult.isSmallVolume()) {
						return null;
					}

					// 计算 stockTradePlanned 结果数据
					if (stockTradeAnalysisResult.getTradeStatus() == TradeStatusEnum.BUY_SUCCESS_SELL_SUCCESS || stockTradeAnalysisResult.getTradeStatus() == TradeStatusEnum.SELL_SUCCESS_BUY_SUCCESS) {
						int deviationAmount = KLineUtils.calDeviationAmount(predayKLine, plannedDeviationRate);
						float plannedScore = calcPlannedScore(stockTradeAnalysisResult);

						StockTradePlanned stockTradePlanned = StockTradePlanned.createDataModel(stock.getStockID(), PLANNED_TRADE_DATE, deviationAmount, plannedDeviationRate,
								plannedSellOutProfitRate, plannedStopLossProfitRate, plannedScore, LocalDateTime.now(), stockTradeAnalysisResult);
						senceStockTradePlanneds.add(stockTradePlanned);
					}
				}
			}
		}

		// 如果没有计算出计划交易数据，则直接返回 null
		if (senceStockTradePlanneds.size() == 0) {
			return null;
		}

		// 计算盈利的交易数量吗，如果盈利的交易数量不到总数的一半，则放弃此股票的交易
		senceStockTradePlanneds = getNonZeroAndSortStockTradePlanneds(senceStockTradePlanneds);
		if (senceStockTradePlanneds.size() <= MIN_SENCE_STOCK_TRADE_PLANNED_COUNT) {
			return null;
		}

		// 排除前 SENCES_SKIP_TOP_N 个方案的高分计划交易数据，并计算该股票的每日计划交易结果
		if (senceStockTradePlanneds.size() > SENCES_PLANNEDS_SKIP_TOP_N) {
			return senceStockTradePlanneds.stream().skip(SENCES_PLANNEDS_SKIP_TOP_N).findFirst().get();
		} else {
			return senceStockTradePlanneds.get(senceStockTradePlanneds.size() - 1);
		}
	}

	/**
	 * 计算非零分的 stockTradePlanneds 数据列表
	 *
	 * @param stockTradePlanneds
	 * @return
	 */
	private List<StockTradePlanned> getNonZeroAndSortStockTradePlanneds(List<StockTradePlanned> stockTradePlanneds) {
		List<StockTradePlanned> result = stockTradePlanneds.stream().filter(x -> x.getPlannedScore() > 0).collect(Collectors.toList());
		result.sort(Comparator.comparing(StockTradePlanned::getPlannedScore, Comparator.reverseOrder()).thenComparing(StockTradePlanned::getPlannedSellOutProfitRate, Comparator.reverseOrder()));

		return result;
	}

	/**
	 * 计算计划交易方案综合评分
	 *
	 * @param stockTradeAnalysisResult
	 * @return
	 */
	private float calcPlannedScore(StockTradeAnalysisResult stockTradeAnalysisResult) {
		float result = stockTradeAnalysisResult.getProfitOrLessRate();

		// 当日亏损，则返回0分
		if (result < 0) {
			return 0;
		}

//		// 没有 买入/卖出 或 卖空/赎回 交易成功，则返回0分
//		if (stockTradeAnalysisResult.getTradeStatus() != TradeStatusEnum.BUY_SUCCESS_SELL_SUCCESS && stockTradeAnalysisResult.getTradeStatus() != TradeStatusEnum.SELL_SUCCESS_BUY_SUCCESS) {
//			return 0;
//		}

		// 2次交易间隔时间段的 +10%
		if (CustomDateUtils.getDurationBetween(stockTradeAnalysisResult.getActualTradeStartTime(), stockTradeAnalysisResult.getActualTradeEndTime()).toMinutes() <= BEST_TRADE_TIME_INTERVAL_MINUTES) {
			result = result * 1.1F;
		}

		// 到达降低利润率和亏损值的倍数时间点的 -10%
		if (stockTradeAnalysisResult.getReduceProfitRateMultiple() != 0) {
			result = result * 0.9F;
		}

		// 添加股票买入计划列表
//
//		if (stockTradePlanneds.size() == allSceneCount) {
//			stockTradePlannedMap.put(stock.getStockID(), stockTradePlanneds);
//		}

		return result;
	}
}
