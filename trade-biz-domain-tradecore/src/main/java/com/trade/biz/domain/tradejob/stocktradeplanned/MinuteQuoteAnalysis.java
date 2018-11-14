package com.trade.biz.domain.tradejob.stocktradeplanned;

import com.google.common.collect.Lists;
import com.trade.biz.dal.tradecore.StockDao;
import com.trade.biz.dal.tradedrds.MinuteQuoteDao;
import com.trade.common.infrastructure.util.collection.CustomListMathUtils;
import com.trade.common.infrastructure.util.date.CustomDateUtils;
import com.trade.common.infrastructure.util.logger.LogInfoUtils;
import com.trade.common.infrastructure.util.math.CustomMathUtils;
import com.trade.common.tradeutil.minutequoteutil.MinuteQuoteDateUtils;
import com.trade.model.tradecore.enums.StockPlateEnum;
import com.trade.model.tradecore.enums.TradeStatusEnum;
import com.trade.model.tradecore.kline.DayKline;
import com.trade.model.tradecore.quote.MinuteQuote;
import com.trade.model.tradecore.stocktrade.StockTradeResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class MinuteQuoteAnalysis {

	// 相关常量 - 1
	private static boolean OPEN_SHORT_SELLING = false; // 是否开通卖空配置
	private static int OPENING_STOP_TRADE_MINUTES = 5; // 开盘后不做首次交易分钟数
	private static int CLOSING_STOP_BUY_MINUTES = 60; // 收盘前不做首次交易分钟数
	private static int CLOSING_ADVANCE_TRADE_NORMAL_MINUTES = 120; // 常规收盘前降低利润率、亏损率分钟数
	private static int CLOSING_ADVANCE_TRADE_LAST_MINUTES = 30; // 最后收盘前降低利润率、亏损率分钟数
	private static int REDUCE_PROFIT_RATE_NORMAL_MULTIPLE = 2; // 常规收盘前降低利润率、亏损率降低倍数
	private static int REDUCE_PROFIT_RATE_LAST_MULTIPLE = 4; // 最后收盘前降低利润率、亏损率降低倍数
	private static int TRADE_PASSED_MIN_MINUTES = 60; // 到达指定亏损次数后，最小的卖出/赎回的分钟数
	private static int TOUCH_LOSS_MIN_TIMES = 60; // 最小亏损次数（需配合 TRADE_PASSED_MIN_MINUTES 一起使用）
	private static int TOUCH_LOSS_MAX_TIMES = 120; // 最大亏损次数
	private static int TO_SELL_TOUCH_LOSS_TIMES = 0; // 卖出时到达的亏损次数
	private static int STOCK_DAY_TRADE_MIN_VOLUME = 100000; // 股票每日最小成交量（小于此成交量配置的股票不进行操作）

	// 相关常量 - 2
	private static int TEST_PLATE_ID = StockPlateEnum.NASDAQ.getPlateID(); // 测试股票平台ID
	private static int TEST_STOCK_ID = 0; // 测试股票ID，如果不配置则测试所有股票
	private static LocalDate TEST_TRADE_DATE = LocalDate.now().minusDays(5); // 测试交易日期
	private static int TEST_ACCOUNT_AMOUNT = 1000000; // 测试账户金额
	private static float DEFAULT_DEVIATION_RATE = 0.4F; // 价格偏离比例
	private static float SELL_OUT_PROFIT_RATE = 0.008F; // 卖出比例
	private static float STOP_LOSS_PROFIT_RATE = 0.02F; // 止损比例

	// 依赖注入
	@Resource
	private MinuteQuoteDao minuteQuoteDao;

	@Resource
	private StockDao stockDao;

	/**
	 * 执行指定或全部股票列表的模拟测试
	 *
	 * @param testStockIDs
	 */
	public void execute(List<Integer> testStockIDs) {
		List<StockTradeResult> stockTradeResults = Lists.newArrayList();
		List<StockTradeResult> notradeStockTradeResults = Lists.newArrayList();
		List<StockTradeResult> tradeFailStockTradeResults = Lists.newArrayList();
		List<StockTradeResult> tradeSuccessStockTradeResults = Lists.newArrayList();
		List<StockTradeResult> tradeProfitStockTradeResults = Lists.newArrayList();
		List<StockTradeResult> tradeLossStockTradeResults = Lists.newArrayList();
		int totalAmount = 0;
		int profitAmount = 0;

		LocalDate tradeDate = MinuteQuoteDateUtils.calcCheckMinuteQuoteDates(TEST_TRADE_DATE, 1).get(0);
		List<Long> stockIDs = stockDao.queryStockIDsByPlateID(TEST_PLATE_ID);
		for (long stockID : stockIDs) {
			if (TEST_STOCK_ID > 0 && stockID != TEST_STOCK_ID) {
				continue;
			}

			if (testStockIDs.size() > 0 && !testStockIDs.contains(stockID)) {
				continue;
			}

			List<MinuteQuote> minuteQuotes = minuteQuoteDao.queryListByStockIDAndDate(stockID, tradeDate);
			if (minuteQuotes.size() > 0) {
				// 模拟单个股票按分钟线的整个交易过程及交易结果
				StockTradeResult stockTradeResult = calcStockTradeResult(stockID, minuteQuotes, tradeDate, TEST_ACCOUNT_AMOUNT, DEFAULT_DEVIATION_RATE, SELL_OUT_PROFIT_RATE, STOP_LOSS_PROFIT_RATE);
				stockTradeResults.add(stockTradeResult);

				// 处理模拟交易统计数据
				notradeStockTradeResults = stockTradeResults.stream().filter(x -> x.getTradeStatus() == TradeStatusEnum.NO_TRADE).collect(Collectors.toList());
				tradeFailStockTradeResults = stockTradeResults.stream().filter(x -> x.getTradeStatus() == TradeStatusEnum.BUY_SUCCESS_SELL_FAIL || x.getTradeStatus() == TradeStatusEnum.SELL_SUCCESS_BUY_FAIL).collect(Collectors.toList());
				tradeSuccessStockTradeResults = stockTradeResults.stream().filter(x -> x.getTradeStatus() == TradeStatusEnum.BUY_SUCCESS_SELL_SUCCESS || x.getTradeStatus() == TradeStatusEnum.SELL_SUCCESS_BUY_SUCCESS).collect(Collectors.toList());
				tradeProfitStockTradeResults = stockTradeResults.stream().filter(x -> x.getProfitOrLessAmount() > 0).collect(Collectors.toList());
				tradeLossStockTradeResults = stockTradeResults.stream().filter(x -> x.getProfitOrLessAmount() < 0).collect(Collectors.toList());
				totalAmount = CustomListMathUtils.calListIntTotal(stockTradeResults.stream().map(x -> (int) (x.getTotalTradeAmount() / 1000)).collect(Collectors.toList()));
				profitAmount = CustomListMathUtils.calListIntTotal(stockTradeResults.stream().map(x -> (int) (x.getProfitOrLessAmount() / 1000)).collect(Collectors.toList()));
				if (stockTradeResults.size() > 0) {
				}
			}
		}

		if (notradeStockTradeResults.size() + tradeFailStockTradeResults.size() + tradeSuccessStockTradeResults.size() + tradeProfitStockTradeResults.size() + tradeLossStockTradeResults.size() + totalAmount + profitAmount > 0) {
			return;
		}
	}

	/**
	 * 模拟单个股票按分钟线的整个交易过程及交易结果
	 *
	 * @param stockID
	 * @param minuteQuotes
	 * @param tradeDate
	 * @param accountTotalAmount
	 * @param deviationRate
	 * @param sellOutProfitRate
	 * @param stopLossProfitRate
	 * @return
	 */
	public StockTradeResult calcStockTradeResult(long stockID,
	                                             List<MinuteQuote> minuteQuotes,
	                                             LocalDate tradeDate,
	                                             int accountTotalAmount,
	                                             float deviationRate,
	                                             float sellOutProfitRate,
	                                             float stopLossProfitRate) {
		boolean isBuyStock = false; // 是否已买入股票
		boolean isSellStock = false; // 是否已卖空股票
		float plannedBuyPrice = 0; // 计划买入价/赎回价
		float plannedSellPrice = 0; // 计划卖出价/卖空价
		float plannedProfitAmount = 0; // 计划盈利金额
		float plannedLossAmount = 0; // 计划亏损金额
		float actualBuyPrice = 0; // 实际买入价/赎回价
		float actualSellPrice = 0; // 实际卖出价/卖空价
		float profitOrLessAmount = 0; // 盈利或亏损总金额
		int actualTradeQuantity = 0; // 实际交易股票数量
		LocalTime actualTradeStartTime = null; // 实际首次交易时间
		LocalTime actualTradeEndTime = null; // 实际结束交易时间
		int touchProfitTimes = 0; // 到达盈利点次数
		int touchLossTimes = 0; // 到达亏损点次数
		int reduceProfitRateMultiple = 0; // 削减过的计划盈利/亏损比例

		try {
			// 获取前一天和当天的日K线数据
			LocalDate preTradeDate = MinuteQuoteDateUtils.calcCheckMinuteQuoteDates(tradeDate, 1).get(0);
			List<MinuteQuote> preMinuteQuotes = minuteQuoteDao.queryListByStockIDAndDate(stockID, preTradeDate);
			DayKline predayData = calcDayKLine(preMinuteQuotes);
			DayKline todayData = calcDayKLine(minuteQuotes);
			if (predayData == null || todayData == null || predayData.getVolume() < STOCK_DAY_TRADE_MIN_VOLUME || todayData.getVolume() < STOCK_DAY_TRADE_MIN_VOLUME) {
				return StockTradeResult.createNoTradeDataModel(stockID);
			}

			// 计算当天的计划买入点和卖出点距离开盘价的差价、当天的交易开始时间点、交易结束时间点
			int deviationAmount = (int) ((predayData.getHigh() - predayData.getLow()) * deviationRate);
			LocalTime dayTradeStartTime = minuteQuotes.get(0).getTime();
			LocalTime dayTradeEndTime = minuteQuotes.get(minuteQuotes.size() - 1).getTime();

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
					plannedProfitAmount = minuteQuote.getPrice() * sellOutProfitRate;
					plannedLossAmount = minuteQuote.getPrice() * stopLossProfitRate;
				}

				// 刚开盘一小段时间不交易
				if (minuteQuote.getTime().isBefore(dayTradeStartTime.plusMinutes(OPENING_STOP_TRADE_MINUTES))) {
					continue;
				}

				// 计算剩余交易的分钟数、买入/卖出交易成功后已过去的分钟数
				int remainderTradeMinutes = (int) CustomDateUtils.getDurationBetween(minuteQuote.getTime(), dayTradeEndTime).toMinutes();
				int tradePassedMinutes = (actualTradeStartTime != null) ? (int) CustomDateUtils.getDurationBetween(actualTradeStartTime, minuteQuote.getTime()).toMinutes() : 0;

				// 每天最后的交易时间段特殊处理利润率和亏损值
				boolean isReduceProfitRate = false; // 是否削减过计划盈利/亏损比例
				if (!isReduceProfitRate) {
					if (remainderTradeMinutes < CLOSING_ADVANCE_TRADE_NORMAL_MINUTES) {
						// 最后一小时左右降低利润率和亏损值，保证当天出货
						isReduceProfitRate = true;
						reduceProfitRateMultiple = REDUCE_PROFIT_RATE_NORMAL_MULTIPLE;
					} else if (remainderTradeMinutes < CLOSING_ADVANCE_TRADE_LAST_MINUTES) {
						// 最后半小时左右继续降低利润率和亏损值，保证当天出货
						isReduceProfitRate = true;
						reduceProfitRateMultiple = REDUCE_PROFIT_RATE_LAST_MULTIPLE;
					} else if (touchLossTimes > TOUCH_LOSS_MAX_TIMES
							|| (touchLossTimes > TOUCH_LOSS_MIN_TIMES && tradePassedMinutes > TRADE_PASSED_MIN_MINUTES)) {
						// 如果买入/卖出交易成功后，已过去了规定的分钟数还没有卖出，则降低利润率和亏损值，保证及时出货
						isReduceProfitRate = true;
						reduceProfitRateMultiple = REDUCE_PROFIT_RATE_LAST_MULTIPLE;
					}

					if (isReduceProfitRate) {
						plannedProfitAmount = plannedProfitAmount / reduceProfitRateMultiple;
						plannedLossAmount = plannedLossAmount / reduceProfitRateMultiple;
					}
				}

				// 处理买入和卖空操作
				if (!isBuyStock && !isSellStock) {
					if (remainderTradeMinutes >= CLOSING_STOP_BUY_MINUTES) {
						if (minuteQuote.getPrice() <= plannedBuyPrice) {
							// 买入处理
							isBuyStock = true;
							actualBuyPrice = minuteQuote.getPrice();
							actualTradeStartTime = minuteQuote.getTime();
							actualTradeQuantity = calcActualTradeQuantity(actualBuyPrice, accountTotalAmount);
						} else if (OPEN_SHORT_SELLING && (minuteQuote.getPrice() >= plannedSellPrice)) {
							// 卖空处理
							isSellStock = true;
							actualSellPrice = minuteQuote.getPrice();
							actualTradeStartTime = minuteQuote.getTime();
							actualTradeQuantity = calcActualTradeQuantity(actualSellPrice, accountTotalAmount);
						}
					}
				} else {
					if (isBuyStock) {
						// 已经买入了，处理卖出
						boolean isProfit = (minuteQuote.getPrice() - plannedProfitAmount) >= actualBuyPrice;
						boolean isLoss = (minuteQuote.getPrice() + plannedLossAmount) <= actualBuyPrice;
						if (isProfit) {
							touchProfitTimes++;
						}
						if (isLoss) {
							touchLossTimes++;
						}

						// 已经确定到达盈利指标，或第二次亏损值到达指标，则处理卖出
						boolean profitSuccess = (isProfit && touchProfitTimes > 0);
						boolean lossSuccess = (isLoss && touchLossTimes > TO_SELL_TOUCH_LOSS_TIMES);
						if (profitSuccess || lossSuccess) {
							actualSellPrice = minuteQuote.getPrice();
							profitOrLessAmount = (actualSellPrice - actualBuyPrice) * actualTradeQuantity;
							actualTradeEndTime = minuteQuote.getTime();
							break;
						} else {
							continue;
						}
					} else if (isSellStock) {
						// 已经卖空了，处理赎回
						boolean isProfit = (minuteQuote.getPrice() + plannedProfitAmount) <= actualSellPrice;
						boolean isLoss = (minuteQuote.getPrice() - plannedLossAmount) >= actualSellPrice;
						if (isProfit) {
							touchProfitTimes++;
						}
						if (isLoss) {
							touchLossTimes++;
						}

						// 已经确定到达盈利指标，或第二次亏损值到达指标，则处理赎回
						boolean profitSuccess = (isProfit && touchProfitTimes > 0);
						boolean lossSuccess = (isLoss && touchLossTimes > TO_SELL_TOUCH_LOSS_TIMES);
						if (profitSuccess || lossSuccess) {
							actualBuyPrice = minuteQuote.getPrice();
							profitOrLessAmount = (actualSellPrice - actualBuyPrice) * actualTradeQuantity;
							actualTradeEndTime = minuteQuote.getTime();
							break;
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
			return StockTradeResult.createNoTradeDataModel(stockID);
		} else if (profitOrLessAmount != 0) {
			TradeStatusEnum tradeStatus = isBuyStock ? TradeStatusEnum.BUY_SUCCESS_SELL_SUCCESS : TradeStatusEnum.SELL_SUCCESS_BUY_SUCCESS;
			float profitOrLessRate = isBuyStock ? CustomMathUtils.round((actualSellPrice - actualBuyPrice) / actualSellPrice, 5) * 100
					: CustomMathUtils.round((actualSellPrice - actualBuyPrice) / actualBuyPrice, 5) * 100;
			return StockTradeResult.createDataModel(stockID, tradeStatus, plannedBuyPrice, plannedSellPrice, plannedProfitAmount, plannedLossAmount,
					actualBuyPrice, actualSellPrice, actualTradeQuantity, profitOrLessAmount, profitOrLessRate, actualTradeStartTime, actualTradeEndTime,
					touchProfitTimes, touchLossTimes, reduceProfitRateMultiple);
		} else {
			TradeStatusEnum tradeStatus = isBuyStock ? TradeStatusEnum.BUY_SUCCESS_SELL_FAIL : TradeStatusEnum.SELL_SUCCESS_BUY_FAIL;
			return StockTradeResult.createDataModel(stockID, tradeStatus, plannedBuyPrice, plannedSellPrice, plannedProfitAmount, plannedLossAmount,
					actualBuyPrice, actualSellPrice, actualTradeQuantity, 0, 0, actualTradeStartTime, actualTradeEndTime,
					touchProfitTimes, touchLossTimes, reduceProfitRateMultiple);
		}
	}

	/**
	 * 根据每份交易金额，计算可购买的股票数量
	 *
	 * @param actualTradePrice
	 * @param accountTotalAmount
	 * @return
	 */
	private int calcActualTradeQuantity(float actualTradePrice, int accountTotalAmount) {
		int result = (int) (accountTotalAmount / actualTradePrice);
		float modPrice = accountTotalAmount % actualTradePrice;
		if (modPrice > actualTradePrice / 2) {
			result++;
		}

		return result;
	}

	/**
	 * 计算每日K线数据
	 *
	 * @param minuteQuotes
	 * @return
	 */
	private DayKline calcDayKLine(List<MinuteQuote> minuteQuotes) {
		DayKline result = null;

		if (minuteQuotes.size() >= 390) {
			result = new DayKline();
			List<Float> prices = minuteQuotes.stream().map(x -> x.getPrice()).collect(Collectors.toList());
			Collections.sort(prices);
			float high = prices.get(prices.size() - 1);
			float low = prices.get(0);
			long volume = CustomListMathUtils.calListLongTotal(minuteQuotes.stream().map(x -> x.getVolume()).collect(Collectors.toList()));

			result.setVolume(volume * 2);
			result.setHigh(high);
			result.setLow(low);
		}

		return result;
	}
}
