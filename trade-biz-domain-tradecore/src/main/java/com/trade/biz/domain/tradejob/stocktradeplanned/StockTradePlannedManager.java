package com.trade.biz.domain.tradejob.stocktradeplanned;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.trade.biz.dal.tradecore.StockDao;
import com.trade.biz.dal.tradedrds.MinuteQuoteDao;
import com.trade.biz.domain.tradejob.futu.FutunnAccountHelper;
import com.trade.common.tradeutil.minutequoteutil.MinuteQuoteDateUtils;
import com.trade.model.tradecore.enums.StockPlateEnum;
import com.trade.model.tradecore.quote.MinuteQuote;
import com.trade.model.tradecore.stock.Stock;
import com.trade.model.tradecore.stocktrade.StockTradePlanned;
import com.trade.model.tradecore.stocktrade.StockTradeResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class StockTradePlannedManager {

	// 相关常量
	private static final int CHECK_MINUTE_QUOTE_DAYS = 1;
	private static List<Float> PLANNED_DEVIATION_RATES = Lists.newArrayList(0.4F);
	private static List<Float> PLANNED_SELL_OUT_PROFIT_RATES = Lists.newArrayList(0.4F, 0.5F, 0.6F, 0.7F, 0.8F, 0.9F, 1.0F);
	private static List<Float> PLANNED_STOP_LOSS_PROFIT_RATES = Lists.newArrayList(0.1F, 0.2F);

	// 依赖注入
	@Resource
	private StockDao stockDao;

	@Resource
	private MinuteQuoteDao minuteQuoteDao;

	@Resource
	private FutunnAccountHelper futunnAccountHelper;

	@Resource
	private MinuteQuoteAnalysis minuteQuoteAnalysis;

	/**
	 * 处理全部股票计划交易逻辑
	 */
	public void execute() {
		// 获取富途账户剩余资金金额
		int accountTotalAmount = futunnAccountHelper.fetchAccountTotalAmount();

		// 计算全部股票计划交易数据（包含计划交易评分）
		Map<Long, List<StockTradePlanned>> stockTradePlannedMap = Maps.newLinkedHashMap();
		List<Stock> allStocks = stockDao.queryListByMarketID(2);
		for (Stock stock : allStocks) {
			// 环球指数不处理
			if (stock.getPlateID() == StockPlateEnum.GLOBAL.getPlateID()) {
				continue;
			}

//			if (!stock.getCode().equals("AAPL")) { // BABA
//				continue;
//			}


			// 循环处理每只股票的不同场景，计算得出股票买入计划列表
			List<StockTradePlanned> stockTradePlanneds = Lists.newArrayList();
			for (float plannedDeviationRate : PLANNED_DEVIATION_RATES) {
				for (float plannedSellOutProfitRate : PLANNED_SELL_OUT_PROFIT_RATES) {
					for (float plannedStopLossProfitRate : PLANNED_STOP_LOSS_PROFIT_RATES) {
						StockTradePlanned stockTradePlanned = calcStockTradePlanned(stock, accountTotalAmount, plannedDeviationRate, plannedSellOutProfitRate, plannedStopLossProfitRate);
						if (stockTradePlanned != null) {
							stockTradePlanneds.add(stockTradePlanned);
						}
					}
				}
			}

			// 添加股票买入计划列表
			stockTradePlanneds.sort(Comparator.comparing(StockTradePlanned::getPlannedScore, Comparator.reverseOrder()));
			stockTradePlannedMap.put(stock.getStockID(), stockTradePlanneds);
		}


		// 根据剩余资金额度，处理当天计划购买的股票及股数，并写入数据库
		for (Map.Entry<Long, List<StockTradePlanned>> entry : stockTradePlannedMap.entrySet()) {

		}
	}

	/**
	 * 计算单个股票计划交易数据（包含计划交易评分）
	 *
	 * @param stock
	 * @param accountTotalAmount
	 * @param plannedDeviationRate
	 * @param plannedSellOutProfitRate
	 * @param plannedStopLossProfitRate
	 * @return
	 */
	private StockTradePlanned calcStockTradePlanned(Stock stock, int accountTotalAmount, float plannedDeviationRate, float plannedSellOutProfitRate, float plannedStopLossProfitRate) {
		List<LocalDate> dates = MinuteQuoteDateUtils.calcCheckMinuteQuoteDates(LocalDate.now(), CHECK_MINUTE_QUOTE_DAYS);
		LinkedHashMap<LocalDate, List<MinuteQuote>> minuteQuotesMap = minuteQuoteDao.queryListMapByStockIDAndDate(stock.getStockID(), dates);
		for (Map.Entry<LocalDate, List<MinuteQuote>> entry : minuteQuotesMap.entrySet()) {
			LocalDate tradeDate = entry.getKey();
			List<MinuteQuote> minuteQuotes = entry.getValue();

			// 模拟单个股票按分钟线的整个交易过程及交易结果
			StockTradeResult stockTradeResult = minuteQuoteAnalysis.calcStockTradeResult(stock.getStockID(), minuteQuotes, tradeDate, accountTotalAmount, plannedDeviationRate, plannedSellOutProfitRate, plannedStopLossProfitRate);

			int a = minuteQuotes.size();

		}

		return null;
	}
}
