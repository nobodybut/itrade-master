package com.trade.biz.domain.tradejob.quote;

import com.google.common.collect.Lists;
import com.trade.biz.dal.tradecore.StockDao;
import com.trade.biz.dal.tradedrds.MinuteQuoteDao;
import com.trade.common.infrastructure.util.date.CustomDateFormatUtils;
import com.trade.common.infrastructure.util.date.CustomDateUtils;
import com.trade.common.infrastructure.util.httpclient.HttpClientUtils;
import com.trade.common.infrastructure.util.logger.LogInfoUtils;
import com.trade.common.infrastructure.util.math.CustomNumberUtils;
import com.trade.common.infrastructure.util.string.CustomStringUtils;
import com.trade.model.tradecore.quote.MinuteQuote;
import com.trade.model.tradecore.stock.Stock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class MinuteQuoteAcq {

	// 依赖注入
	@Resource
	private StockDao stockDao;

	@Resource
	private MinuteQuoteDao minuteQuoteDao;

	public void execute() {
		long startMills = System.currentTimeMillis();
		LocalDate tradeDate = LocalDate.now().plusDays(-1);

		try {
			List<Stock> stocks = stockDao.queryListByMarketID(2);
			log.info("all stock loaded! count={}, tradeDate={}", stocks.size(), CustomDateFormatUtils.formatDate(tradeDate));

			for (Stock stock : stocks) {
				List<MinuteQuote> minuteQuotes = fetchTradeDateMinuteQuotes(stock.getStockID(), tradeDate);
				for (MinuteQuote minuteQuote : minuteQuotes) {
					minuteQuoteDao.insertOrUpdate(minuteQuote);
				}

				log.info("stock {} finished!", stock.getCode());
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
	 * @return
	 */
	public List<MinuteQuote> fetchTradeDateMinuteQuotes(long stockID, LocalDate tradeDate) {
		List<MinuteQuote> result = Lists.newArrayList();

		try {
			String json = HttpClientUtils.getHTML(String.format("https://www.futunn.com/trade/quote-minute-v2?security_id=%s&_=%s", stockID, String.valueOf(System.currentTimeMillis())));
			String listJson = CustomStringUtils.substringBetween(json, "\"list\":", "]");
			String[] minuteJsons = CustomStringUtils.substringsBetween(listJson, "{", "}");
			for (String minuteJson : minuteJsons) {
				long timeMills = CustomNumberUtils.toLong(CustomStringUtils.substringBetween(minuteJson, "\"time\":", ",")) * 1000;
				LocalDateTime quoteDateTime = CustomDateUtils.dateToLocalDateTime(new Date(timeMills)).plusHours(-12);

				MinuteQuote minuteQuote = new MinuteQuote();
				minuteQuote.setStockID(stockID);
				minuteQuote.setDate(quoteDateTime.toLocalDate());
				minuteQuote.setTime(quoteDateTime.toLocalTime());
				minuteQuote.setPrice(CustomNumberUtils.toInt(CustomStringUtils.substringBetween(minuteJson, "\"price\":", ",")));
				minuteQuote.setVolume(CustomNumberUtils.toLong(CustomStringUtils.substringBetween(minuteJson, "\"volume\":", ",")));
				minuteQuote.setTurnover(CustomNumberUtils.toLong(CustomStringUtils.substringBetween(minuteJson, "\"turnover\":", ",")));
				minuteQuote.setChangeRate(CustomNumberUtils.toFloat(CustomStringUtils.substringBetween(minuteJson, "\"ratio\":\"", "\"")));

				if (minuteQuote.getDate().equals(tradeDate)) {
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
