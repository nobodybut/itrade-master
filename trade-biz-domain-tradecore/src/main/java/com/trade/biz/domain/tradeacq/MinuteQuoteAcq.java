package com.trade.biz.domain.tradeacq;

import com.google.common.collect.Lists;
import com.trade.biz.dal.tradecore.StockDao;
import com.trade.biz.dal.tradedrds.MinuteQuoteDao;
import com.trade.biz.dal.util.MinuteQuoteDaoUtils;
import com.trade.common.infrastructure.util.collection.CustomListMathUtils;
import com.trade.common.infrastructure.util.date.CustomDateFormatUtils;
import com.trade.common.infrastructure.util.date.CustomDateUtils;
import com.trade.common.infrastructure.util.httpclient.HttpClientUtils;
import com.trade.common.infrastructure.util.logger.LogInfoUtils;
import com.trade.common.infrastructure.util.math.CustomNumberUtils;
import com.trade.common.infrastructure.util.string.CustomStringUtils;
import com.trade.common.tradeutil.quanttradeutil.TradeDateUtils;
import com.trade.model.tradecore.consts.FutunnConsts;
import com.trade.model.tradecore.minutequote.MinuteQuote;
import com.trade.model.tradecore.stock.Stock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Date;
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
		LocalDate tradeDate = TradeDateUtils.getUSCurrentDate();

		try {
			// 读取所有股票信息列表
			List<Stock> allStocks = stockDao.queryListByMarketID(2);
			Set<String> minuteQuoteUniqueKeys = minuteQuoteDao.queryUniqueKeysByDate(tradeDate);
			log.info("allStocks loaded! count={}, tradeDate={}", allStocks.size(), CustomDateFormatUtils.formatDate(tradeDate));

			// 计算服务器时间与美国时差
			int usDiffHours = TradeDateUtils.calUSDiffHours(LocalDateTime.now());

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
			LocalDate preTradeDate = tradeDate.plusDays(-1);
			String json = HttpClientUtils.getHTML(String.format(FutunnConsts.FUTUNN_QUOTE_MINUTE_URL_TMPL, stockID, String.valueOf(System.currentTimeMillis())));
			String listJson = CustomStringUtils.substringBetween(json, "\"list\":", "]");
			String[] minuteJsons = CustomStringUtils.substringsBetween(listJson, "{", "}");
			for (String minuteJson : minuteJsons) {
				long timeMills = CustomNumberUtils.toLong(CustomStringUtils.substringBetween(minuteJson, "\"time\":", ",")) * 1000;
				LocalDateTime quoteDateTime = CustomDateUtils.dateToLocalDateTime(new Date(timeMills)).plusHours(usDiffHours);

				MinuteQuote minuteQuote = new MinuteQuote();
				minuteQuote.setStockID(stockID);
				minuteQuote.setDate(quoteDateTime.toLocalDate());
				minuteQuote.setTime(quoteDateTime.toLocalTime());
				minuteQuote.setPrice(CustomNumberUtils.toFloat(CustomStringUtils.substringBetween(minuteJson, "\"price\":", ",")));
				minuteQuote.setVolume(CustomNumberUtils.toLong(CustomStringUtils.substringBetween(minuteJson, "\"volume\":", ",")));
				minuteQuote.setTurnover(CustomNumberUtils.toLong(CustomStringUtils.substringBetween(minuteJson, "\"turnover\":", ",")));
				minuteQuote.setChangeRate(CustomNumberUtils.toFloat(CustomStringUtils.substringBetween(minuteJson, "\"ratio\":\"", "\"")));

				if (minuteQuote.getDate().equals(tradeDate) || minuteQuote.getDate().equals(preTradeDate)) {
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
}
