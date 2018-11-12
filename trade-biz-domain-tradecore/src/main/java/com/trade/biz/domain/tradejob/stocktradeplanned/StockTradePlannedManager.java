package com.trade.biz.domain.tradejob.stocktradeplanned;

import com.google.common.collect.Lists;
import com.trade.biz.dal.tradecore.StockDao;
import com.trade.biz.dal.tradedrds.MinuteQuoteDao;
import com.trade.common.tradeutil.minutequoteutil.MinuteQuoteDateUtils;
import com.trade.model.tradecore.quote.MinuteQuote;
import com.trade.model.tradecore.quote.MinuteQuoteRate;
import com.trade.model.tradecore.stock.Stock;
import com.trade.model.tradecore.stocktrade.StockTradePlanned;
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
	private static final int CHECK_MINUTE_QUOTE_DAYS = 2;

	// 依赖注入
	@Resource
	private StockDao stockDao;

	@Resource
	private MinuteQuoteDao minuteQuoteDao;

	@Resource
	private MinuteQuoteRateHelper minuteQuoteRateHelper;

	/**
	 * 处理全部股票计划交易逻辑
	 */
	public void execute() {
		// 计算全部股票计划交易数据（包含计划交易评分）
		List<StockTradePlanned> stockTradePlanneds = Lists.newArrayList();
		List<Stock> allStocks = stockDao.queryListByMarketID(2);
		for (Stock stock : allStocks) {
			if (stock.getPlateID() != 200201) {
				StockTradePlanned stockTradePlanned = calcStockTradePlanned(stock);
				if (stockTradePlanned != null) {
					stockTradePlanneds.add(stockTradePlanned);
				}
			}
		}
		stockTradePlanneds.sort(Comparator.comparing(StockTradePlanned::getPlannedScore, Comparator.reverseOrder()));

		// 根据剩余资金额度，处理当天计划购买的股票及股数


	}

	/**
	 * 计算单个股票计划交易数据（包含计划交易评分）
	 *
	 * @param stock
	 * @return
	 */
	private StockTradePlanned calcStockTradePlanned(Stock stock) {
		List<LocalDate> dates = MinuteQuoteDateUtils.calcCheckMinuteQuoteDates(LocalDate.now(), CHECK_MINUTE_QUOTE_DAYS);
		LinkedHashMap<LocalDate, List<MinuteQuote>> minuteQuotesMap = minuteQuoteDao.queryListMapByStockIDAndDate(stock.getStockID(), dates);
		for (Map.Entry<LocalDate, List<MinuteQuote>> entry : minuteQuotesMap.entrySet()) {
			LocalDate date = entry.getKey();
			List<MinuteQuote> minuteQuotes = entry.getValue();

			List<MinuteQuoteRate> minuteQuoteRates = minuteQuoteRateHelper.calcMinuteQuoteRates(minuteQuotes, date);

			int a = minuteQuotes.size();

		}

		return null;
	}


	/**
	 * 获取账户剩余金额（美元）
	 *
	 * @return
	 */
	private int getAccountRemainingAmount() {
		return 90000;
	}
}
