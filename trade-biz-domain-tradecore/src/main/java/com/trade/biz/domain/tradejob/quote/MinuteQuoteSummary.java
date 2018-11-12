package com.trade.biz.domain.tradejob.quote;

import com.google.common.collect.Lists;
import com.trade.biz.dal.tradecore.StockDao;
import com.trade.biz.dal.tradedrds.MinuteQuoteDao;
import com.trade.biz.domain.tradejob.kline.DayKlineSummary;
import com.trade.common.infrastructure.util.collection.CustomListMathUtils;
import com.trade.common.infrastructure.util.date.CustomDateParseUtils;
import com.trade.common.infrastructure.util.date.CustomDateUtils;
import com.trade.common.infrastructure.util.httpclient.HttpClientUtils;
import com.trade.common.infrastructure.util.logger.LogInfoUtils;
import com.trade.common.infrastructure.util.math.CustomNumberUtils;
import com.trade.common.infrastructure.util.refout.RefDouble;
import com.trade.common.infrastructure.util.string.CustomStringUtils;
import com.trade.model.tradecore.enums.StockPlateEnum;
import com.trade.model.tradecore.kline.DayKline;
import com.trade.model.tradecore.quote.MinuteQuote;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class MinuteQuoteSummary {

	// 相关常量 - 1
	private static boolean OPEN_SHORT_SELLING = false;
	private static float SELL_OUT_PROFIT_RATE = 0.007F; // 卖出比例 0.004F;
	private static float STOP_LOSS_PROFIT_RATE = 0.02F; // 止损比例 0.02F;

	// 相关常量 - 2
	private static int PLATE_ID = StockPlateEnum.NASDAQ.getPlateID();
	private static LocalDate TARGET_TRADE_DATE = LocalDate.of(2018, 10, 26);

	// 依赖注入
	@Resource
	private MinuteQuoteDao minuteQuoteDao;

	@Resource
	private StockDao stockDao;

	@Resource
	private DayKlineSummary dayKlineSummary;

	public void execute() {
		List<Integer> dealResult = Lists.newArrayList();
		List<Integer> nodealResult = Lists.newArrayList();
		double totalAmount = 0;

		List<Long> stockIDs = stockDao.queryStockIDsByPlateID(PLATE_ID);
		for (long stockID : stockIDs) {
			RefDouble refStockPrice = new RefDouble();
			int money = (int) performMinuteQuoteMoney(stockID, refStockPrice);
			if (money == -1 || money == -2) {
				nodealResult.add(money);
			} else if (money != 0) {
				totalAmount += refStockPrice.getRef();
				dealResult.add(money);
			}
		}
		int sum = CustomListMathUtils.calListIntegerSum(dealResult);
		double resultAmount = totalAmount;
	}


	public double performMinuteQuoteMoney(long stockID, RefDouble refStockPrice) {
		boolean isBuyStock = false;
		boolean isSellStock = false;

		try {
			// 获取当天的全部分钟线数据
			List<MinuteQuote> minuteQuotes = minuteQuoteDao.queryListByStockIDAndDate(stockID, TARGET_TRADE_DATE);

			// 获取前一天的日数据，并计算当天的买入点和卖出点
			DayKline predayData = getDayKline(stockID, TARGET_TRADE_DATE.plusDays(-1));
			DayKline todayData = getDayKline(stockID, TARGET_TRADE_DATE);
			if (predayData == null || todayData == null) {
				return 0;
			}

			int deviationAmount = (int) ((predayData.getHigh() - predayData.getLow()) * 0.4);
			double todayBuyPrice = 0; // todayData.getOpenPrice() - deviationAmount;
			double todaySellPrice = 0; // todayData.getOpenPrice() + deviationAmount;

			// 按照 规则(1001) 计算当天交易的盈亏情况
			int touchMakeMoneyTimes = 0;
			int touchLossMoneyTimes = 0;
			double makeMoneyAmount = todayData.getOpen() * SELL_OUT_PROFIT_RATE;
			double lossMoneyAmount = todayData.getOpen() * STOP_LOSS_PROFIT_RATE;

			for (MinuteQuote minuteQuote : minuteQuotes) {
				// 价格为0直接跳过
				if (minuteQuote.getPrice() <= 0) {
					continue;
				}

				// 前半小时和后半小时不交易
				if (minuteQuote.getTime().isBefore(CustomDateParseUtils.parseTime("10:00"))
						|| minuteQuote.getTime().isAfter(CustomDateParseUtils.parseTime("16:00"))) {
					continue;
				} else {
					// 半小时后设置当天的买入价和卖空价
					if (todayBuyPrice == 0 || todaySellPrice == 0) {
						todayBuyPrice = minuteQuote.getPrice() - deviationAmount;
						todaySellPrice = minuteQuote.getPrice() + deviationAmount;
					}

					// 最后半小时降低利润率和亏损值，保证当天出货
					if (minuteQuote.getTime().isAfter(CustomDateParseUtils.parseTime("15:30"))) {
						makeMoneyAmount = makeMoneyAmount / 4;
						lossMoneyAmount = lossMoneyAmount / 4;
					}
				}

				// 处理买入和卖空操作
				if (!isBuyStock && !isSellStock) {
					if (minuteQuote.getPrice() <= todayBuyPrice) {
						// 买入处理
						isBuyStock = true;
					} else if (OPEN_SHORT_SELLING && (minuteQuote.getPrice() >= todaySellPrice)) {
						// 卖空处理
						isSellStock = true;
					}
				} else {

					if (isBuyStock) {
						// 已经买入了，处理卖出
						boolean isMakeMoney = (minuteQuote.getPrice() - makeMoneyAmount) >= todayBuyPrice;
						boolean isLossMoney = (minuteQuote.getPrice() + lossMoneyAmount) <= todayBuyPrice;
						if (isMakeMoney) {
							touchMakeMoneyTimes++;
						}
						if (isLossMoney) {
							touchLossMoneyTimes++;
						}

						if (isMakeMoney || (isLossMoney && touchLossMoneyTimes > 1)) {
							refStockPrice.setRef(minuteQuote.getPrice());
							return minuteQuote.getPrice() - todayBuyPrice;
						} else {
							continue;
						}
					} else if (isSellStock) {
						// 已经卖空了，处理赎回
						boolean isMakeMoney = (minuteQuote.getPrice() + makeMoneyAmount) <= todaySellPrice;
						boolean isLossMoney = (minuteQuote.getPrice() - lossMoneyAmount) >= todaySellPrice;
						if (isMakeMoney) {
							touchMakeMoneyTimes++;
						}
						if (isLossMoney) {
							touchLossMoneyTimes++;
						}

						if (isMakeMoney || (isLossMoney && touchLossMoneyTimes > 1)) {
							refStockPrice.setRef(minuteQuote.getPrice());
							return todaySellPrice - minuteQuote.getPrice();
						} else {
							continue;
						}
					}
				}
			}
		} catch (Exception ex) {
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			String logData = String.format("stockID=%s", stockID);
			log.error(String.format(LogInfoUtils.HAS_DATA_TMPL, methodName, logData), ex);
		}

		if (!isBuyStock && !isSellStock) {
			return 0;
		} else {
			if (isBuyStock) {
				return -1;
			} else {
				return -2;
			}
		}
	}

	private DayKline getDayKline(long stockID, LocalDate tradeDate) {
		String html = HttpClientUtils.getHTML(String.format("https://www.futunn.com/quote/kline-v2?security_id=%s&type=2&from=&_=%s", stockID, String.valueOf(System.currentTimeMillis()))); // APPL
		String usefulCode = CustomStringUtils.substringBetween(html, "\"list\":", "]");
		String[] klineCodes = CustomStringUtils.substringsBetween(usefulCode, "{", "}");
		for (int i = klineCodes.length - 1; i >= 0; i--) {
			long dateMills = CustomNumberUtils.toLong(dayKlineSummary.getKlineValue(klineCodes[i], "k")) * 1000;
			LocalDateTime dateTime = CustomDateUtils.dateToLocalDateTime(new Date(dateMills));
			if (dateTime.toLocalDate().equals(tradeDate)) {
				return dayKlineSummary.createDayKline(klineCodes[i]);
			}
		}

		return null;
	}
}
