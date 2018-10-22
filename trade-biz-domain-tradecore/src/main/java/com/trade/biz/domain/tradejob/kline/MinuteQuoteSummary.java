package com.trade.biz.domain.tradejob.kline;

import com.google.common.collect.Lists;
import com.trade.common.infrastructure.util.collection.CustomListMathUtils;
import com.trade.common.infrastructure.util.date.CustomDateParseUtils;
import com.trade.common.infrastructure.util.date.CustomDateUtils;
import com.trade.common.infrastructure.util.httpclient.HttpClientUtils;
import com.trade.common.infrastructure.util.logger.LogInfoUtils;
import com.trade.common.infrastructure.util.math.CustomNumberUtils;
import com.trade.common.infrastructure.util.refout.RefDouble;
import com.trade.common.infrastructure.util.string.CustomStringUtils;
import com.trade.model.tradecore.kline.DayKline;
import com.trade.model.tradecore.MinuteQuote;
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
public class MinuteQuoteSummary {

	// 相关常量
	private static boolean OPEN_SHORT_SELLING = false;
	private static LocalDate TARGET_TRADE_DATE = LocalDate.of(2018, 10, 19);
	private static float SELL_OUT_PROFIT_RATE = 0.007F; // 卖出比例 0.004F;
	private static float STOP_LOSS_PROFIT_RATE = 0.02F; // 止损比例 0.02F;

	@Resource
	private DayKlineSummary dayKlineSummary;

	public void execute() {
		List<Integer> dealResult = Lists.newArrayList();
		List<Integer> nodealResult = Lists.newArrayList();
		double totalAmount = 0;

		List<Integer> stockCodes = initStockCodes();
		for (int stockCode : stockCodes) {
			RefDouble refStockPrice = new RefDouble();
			int money = (int) performMinuteQuoteMoney(stockCode, refStockPrice);
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


	public double performMinuteQuoteMoney(int stockCode, RefDouble refStockPrice) {
		boolean isBuyStock = false;
		boolean isSellStock = false;

		try {
			// 获取当天的全部分钟线数据
			List<MinuteQuote> minuteQuotes = Lists.newArrayList();
			String json = HttpClientUtils.getHTML(String.format("https://www.futunn.com/trade/quote-minute-v2?security_id=%s&_=%s", stockCode, String.valueOf(System.currentTimeMillis())));
			String listJson = CustomStringUtils.substringBetween(json, "\"list\":", "]");
			String[] minuteJsons = CustomStringUtils.substringsBetween(listJson, "{", "}");
			for (String minuteJson : minuteJsons) {
				MinuteQuote minuteQuote = new MinuteQuote();
				minuteQuote.setCurTime(CustomNumberUtils.toLong(CustomStringUtils.substringBetween(minuteJson, "\"time\":", ",")));
				LocalDateTime quoteDateTime = CustomDateUtils.dateToLocalDateTime(new Date(minuteQuote.getCurTime() * 1000)).plusHours(-12);
				minuteQuote.setDate(quoteDateTime.toLocalDate());
				minuteQuote.setTime(quoteDateTime.toLocalTime());
				minuteQuote.setPrice(CustomNumberUtils.toFloat(CustomStringUtils.substringBetween(minuteJson, "\"price\":", ",")));
				minuteQuote.setVolume(CustomNumberUtils.toInt(CustomStringUtils.substringBetween(minuteJson, "\"volume\":", ",")));
				minuteQuote.setTurnover(CustomNumberUtils.toFloat(CustomStringUtils.substringBetween(minuteJson, "\"turnover\":", ",")));
				minuteQuote.setRatio(CustomNumberUtils.toFloat(CustomStringUtils.substringBetween(minuteJson, "\"ratio\":\"", "\"")));

				if (minuteQuote.getDate().equals(TARGET_TRADE_DATE)) {
					minuteQuotes.add(minuteQuote);
				}
			}
			minuteQuotes.sort(Comparator.comparing(MinuteQuote::getTime));

			// 获取前一天的日数据，并计算当天的买入点和卖出点
			DayKline predayData = getDayKline(stockCode, TARGET_TRADE_DATE.plusDays(-1));
			DayKline todayData = getDayKline(stockCode, TARGET_TRADE_DATE);
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
			String logData = String.format("stockCode=%s", stockCode);
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

	private DayKline getDayKline(int stockCode, LocalDate tradeDate) {
		String html = HttpClientUtils.getHTML(String.format("https://www.futunn.com/quote/kline-v2?security_id=%s&type=2&from=&_=%s", stockCode, String.valueOf(System.currentTimeMillis()))); // APPL
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

	private List<Integer> initStockCodes() {
		List<Integer> result = Lists.newArrayList();
		result.add(210182);
		result.add(203377);
		result.add(202742);
		result.add(202218);
		result.add(205094);
		result.add(203498);
		result.add(201960);
		result.add(201785);
		result.add(202550);
		result.add(202944);
		result.add(202784);
		result.add(205417);
		result.add(201967);
		result.add(202234);
		result.add(203540);
		result.add(203463);
		result.add(205468);
		result.add(205127);
		result.add(205436);
		result.add(203052);
		result.add(203109);
		result.add(205189);
		result.add(202040);
		result.add(203173);
		result.add(203140);
		result.add(203248);
		result.add(202187);
		result.add(201504);
		result.add(202762);
		result.add(205144);
		result.add(203091);
		result.add(205761);
		result.add(205279);
		result.add(203564);
		result.add(203543);
		result.add(202736);
		result.add(205172);
		result.add(202633);
		result.add(203136);
		result.add(203445);
		result.add(202468);
		result.add(206201);
		result.add(202978);
		result.add(205683);
		result.add(202310);
		result.add(202814);
		result.add(202027);
		result.add(202560);
		result.add(205541);
		result.add(202501);
		result.add(203134);
		result.add(201637);
		result.add(203028);
		result.add(205291);
		result.add(201956);
		result.add(201588);
		result.add(202856);
		result.add(201345);
		result.add(202313);
		result.add(201721);


		return result;
	}
}
