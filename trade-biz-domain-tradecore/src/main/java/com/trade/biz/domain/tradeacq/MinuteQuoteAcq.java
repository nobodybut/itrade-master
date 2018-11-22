package com.trade.biz.domain.tradeacq;

import com.google.common.collect.Lists;
import com.trade.biz.dal.tradecore.StockDao;
import com.trade.biz.dal.tradedrds.MinuteQuoteDao;
import com.trade.biz.dal.util.MinuteQuoteDaoUtils;
import com.trade.common.infrastructure.util.collection.CustomListMathUtils;
import com.trade.common.infrastructure.util.date.CustomDateFormatUtils;
import com.trade.common.infrastructure.util.httpclient.HttpClientUtils;
import com.trade.common.infrastructure.util.logger.LogInfoUtils;
import com.trade.common.infrastructure.util.math.CustomNumberUtils;
import com.trade.common.infrastructure.util.string.CustomStringUtils;
import com.trade.common.tradeutil.consts.FutunnConsts;
import com.trade.common.tradeutil.quanttradeutil.TradeDateUtils;
import com.trade.model.tradecore.minutequote.MinuteQuote;
import com.trade.model.tradecore.stock.Stock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Slf4j
public class MinuteQuoteAcq {

	// 线程池
	private final ExecutorService EXECUTOR_POOL = Executors.newCachedThreadPool();

	// 相关常量
	private static final int MULTITHREAD_COUNT = 10;

	// 依赖注入
	@Resource
	private StockDao stockDao;

	@Resource
	private MinuteQuoteDao minuteQuoteDao;

	public void execute() {
		long startMills = System.currentTimeMillis();
		LocalDate tradeDate = TradeDateUtils.getUsCurrentDate();

		// 任务开始时，当前日期就不是交易日期，则直接返回
		if (!TradeDateUtils.isUsTradeDay(tradeDate)) {
			return;
		}

		try {
			// 读取所有股票信息列表
			List<Stock> allStocks = stockDao.queryListByMarketID(2);
			Set<String> minuteQuoteUniqueKeys = minuteQuoteDao.queryUniqueKeysByDate(tradeDate);
			log.info("allStocks loaded! count={}, tradeDate={}", allStocks.size(), CustomDateFormatUtils.formatDate(tradeDate));

			// 计算服务器时间与美国时差
			int usDiffHours = TradeDateUtils.calUsDiffHours(LocalDateTime.now());

			// 多线程执行分钟线数据抓取
			List<List<Stock>> stocksList = CustomListMathUtils.splitToListsByListItemCount(allStocks, MULTITHREAD_COUNT);
			for (List<Stock> stocks : stocksList) {
				try {
					List<Callable<Object>> tasks = Lists.newArrayList();
					for (Stock stock : stocks) {
						tasks.add(() -> {
							List<MinuteQuote> minuteQuotes = fetchTradeDateMinuteQuotes(stock.getStockID(), tradeDate, usDiffHours);
							for (MinuteQuote minuteQuote : minuteQuotes) {
								String minuteQuoteUniqueKey = MinuteQuoteDaoUtils.calMinuteQuoteUniqueKey(minuteQuote);
								if (!minuteQuoteUniqueKeys.contains(minuteQuoteUniqueKey)) {
									minuteQuoteDao.insertOrUpdate(minuteQuote);
								}
							}

							log.info("{}({}) finished!", stock.getCode(), stock.getStockID());
							return null;
						});
					}

					EXECUTOR_POOL.invokeAll(tasks);
				} catch (Exception e) {
					log.error("part stocks acq exception!", e);
				}
			}
		} catch (Exception ex) {
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			log.error(String.format(LogInfoUtils.NO_DATA_TMPL, methodName), ex);
		}

		log.info("all stock finished! tradeDate={}, spendTime={}", CustomDateFormatUtils.formatDate(tradeDate), (System.currentTimeMillis() - startMills) / 1000);
	}

	/**
	 * 从远程服务器获取一只股票一个交易日的分钟线数据
	 *
	 * @param stockID
	 * @param tradeDate
	 * @param usDiffHours
	 * @return
	 */
	public List<MinuteQuote> fetchTradeDateMinuteQuotes(long stockID, LocalDate tradeDate, int usDiffHours) {
		List<MinuteQuote> result = Lists.newArrayList();

		try {
			String json = HttpClientUtils.getHTML(String.format(FutunnConsts.FUTUNN_QUOTE_MINUTE_URL_TMPL, stockID, String.valueOf(System.currentTimeMillis())));
			String listJson = CustomStringUtils.substringBetween(json, "\"list\":", "]");
			String[] minuteJsons = CustomStringUtils.substringsBetween(listJson, "{", "}");
			for (String minuteJson : minuteJsons) {
				long timeMills = CustomNumberUtils.toLong(CustomStringUtils.substringBetween(minuteJson, "\"time\":", ",")) * 1000;
				LocalDateTime quoteDateTime = TradeDateUtils.getDateTimeByTimeMills(timeMills, usDiffHours);

				MinuteQuote minuteQuote = new MinuteQuote();
				minuteQuote.setStockID(stockID);
				minuteQuote.setDate(quoteDateTime.toLocalDate());
				minuteQuote.setTime(quoteDateTime.toLocalTime());
				minuteQuote.setPrice(CustomNumberUtils.toFloat(CustomStringUtils.substringBetween(minuteJson, "\"price\":", ",")));
				minuteQuote.setVolume(CustomNumberUtils.toLong(CustomStringUtils.substringBetween(minuteJson, "\"volume\":", ",")));
				minuteQuote.setTurnover(CustomNumberUtils.toLong(CustomStringUtils.substringBetween(minuteJson, "\"turnover\":", ",")));
				minuteQuote.setChangeRate(CustomNumberUtils.toFloat(CustomStringUtils.substringBetween(minuteJson, "\"ratio\":\"", "\"")));

				if (minuteQuote.getDate().equals(tradeDate) && TradeDateUtils.isUsTradeTime(minuteQuote.getTime())) {
					result.add(minuteQuote);
				}
			}
			result.sort(Comparator.comparing(MinuteQuote::getTime));
		} catch (Exception ex) {
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			String logData = String.format("stockID=%s, tradeDate=%s", stockID, CustomDateFormatUtils.formatDate(tradeDate));
			log.error(String.format(LogInfoUtils.HAS_DATA_TMPL, methodName, logData), ex);
		}

		return result;
	}

//	public void execute() {
//		List<LocalDate> dates = Lists.newArrayList();
//		dates.add(LocalDate.of(2018, 11, 6));
//		dates.add(LocalDate.of(2018, 11, 7));
//		dates.add(LocalDate.of(2018, 11, 8));
//		dates.add(LocalDate.of(2018, 11, 9));
//		dates.add(LocalDate.of(2018, 11, 12));
//		dates.add(LocalDate.of(2018, 11, 13));
//		dates.add(LocalDate.of(2018, 11, 14));
//		dates.add(LocalDate.of(2018, 11, 15));
//		dates.add(LocalDate.of(2018, 11, 16));
//		dates.add(LocalDate.of(2018, 11, 19));
//		dates.add(LocalDate.of(2018, 11, 20));
//
//		for (LocalDate date : dates) {
//			clear(date);
//		}
//
//		log.info("update ALL SUCCESS!");
//	}
//
//	private void clear(LocalDate date) {
//		long startmills = System.currentTimeMillis();
//		log.info("update tradeDate STARTED! date={}", CustomDateFormatUtils.formatDate(date));
//
//		Map<Long, List<MinuteQuote>> mapResult = Maps.newHashMap();
//		List<MinuteQuote> minuteQuotes = minuteQuoteDao.queryListByDate(date);
//		minuteQuotes.sort(Comparator.comparing(MinuteQuote::getTime));
//		for (MinuteQuote minuteQuote : minuteQuotes) {
//			if (!mapResult.containsKey(minuteQuote.getStockID())) {
//				mapResult.put(minuteQuote.getStockID(), Lists.newArrayList());
//			}
//			mapResult.get(minuteQuote.getStockID()).add(minuteQuote);
//		}
//
//		for (Map.Entry<Long, List<MinuteQuote>> entry : mapResult.entrySet()) {
//			if (entry.getValue().get(0).getTime().getHour() == 10) {
//				long partStartmills = System.currentTimeMillis();
//
//				for (MinuteQuote minuteQuote : entry.getValue()) {
//					minuteQuote.setTime(minuteQuote.getTime().minusHours(1));
//					minuteQuoteDao.update(minuteQuote);
//				}
//
//				log.info("update stock SUCCESS! stockID={}, date={}, spendTime={}", entry.getKey(), CustomDateFormatUtils.formatDate(date), (System.currentTimeMillis() - partStartmills) / 1000);
//			}
//		}
//		log.info("update tradeDate SUCCESS! date={}, spendTime={}", CustomDateFormatUtils.formatDate(date), (System.currentTimeMillis() - startmills) / 1000);
//	}
}
