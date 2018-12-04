package com.trade.biz.domain.tradequant.quanttrading;

import com.trade.biz.dal.tradecore.QuantTradeActualDao;
import com.trade.biz.domain.tradequant.futu.FutunnAccountHelper;
import com.trade.biz.domain.tradequant.futu.FutunnTradingHelper;
import com.trade.common.infrastructure.util.date.CustomDateUtils;
import com.trade.common.infrastructure.util.httpclient.HttpClientUtils;
import com.trade.common.infrastructure.util.json.CustomJSONUtils;
import com.trade.common.infrastructure.util.logger.LogInfoUtils;
import com.trade.common.infrastructure.util.math.CustomNumberUtils;
import com.trade.common.infrastructure.util.string.CustomStringUtils;
import com.trade.common.tradeutil.consts.FutunnConsts;
import com.trade.common.tradeutil.quanttradeutil.QuantTradingUtils;
import com.trade.common.tradeutil.quanttradeutil.TradeDateUtils;
import com.trade.model.tradecore.enums.TradeSideEnum;
import com.trade.model.tradecore.quanttrade.QuantTradeActual;
import com.trade.model.tradecore.quanttrade.QuantTradePlanned;
import com.trade.model.tradecore.quanttrade.QuantTrading;
import com.trade.model.tradecore.stock.Stock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

public class QuantRealtimeTrading {

	// 日志记录
	private static final Logger LOGGER = LoggerFactory.getLogger(QuantRealtimeTrading.class);

	// 相关常量
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

	/**
	 * 处理单个股票交易计划实时交易
	 *
	 * @param stock
	 * @param quantTradePlanned
	 * @param tradePlannedCount
	 * @param futunnAccountHelper
	 * @param futunnTradingHelper
	 * @param quantTradeActualDao
	 */
	public void execute(Stock stock,
	                    QuantTradePlanned quantTradePlanned,
	                    int tradePlannedCount,
	                    FutunnAccountHelper futunnAccountHelper,
	                    FutunnTradingHelper futunnTradingHelper,
	                    QuantTradeActualDao quantTradeActualDao) {
		// 定义相关变量
		QuantTrading quantTrading = new QuantTrading();
		float plannedBuyPrice = 0;
		float plannedSellPrice = 0;
		float plannedProfitAmount = 0;
		float plannedLossAmount = 0;

		// 读取FUTU账户剩余金额
		int accountTotalAmount = futunnAccountHelper.getAccountTotalAmount();

		while (true) {
			try {
				// 先判断是否在交易时间段内
				LocalTime currentTime = TradeDateUtils.getUsCurrentTime();
				if (!TradeDateUtils.isUsTradeTime(currentTime)) {
					TimeUnit.MILLISECONDS.sleep(1000);
					continue;
				}

				// 通过接口获取实时报价数据
				String json = HttpClientUtils.getHTML(String.format(FutunnConsts.FUTUNN_QUOTE_BASIC_URL_TMPL, quantTradePlanned.getStockID(), String.valueOf(System.currentTimeMillis())));
				float currentPrice = getPriceFromJson(json, "price") * 1000;
				float openPrice = getPriceFromJson(json, "open_price") * 1000;
				float highestPrice = getPriceFromJson(json, "highest_price") * 1000;
				float lowestPrice = getPriceFromJson(json, "lowest_price") * 1000;
				if (currentPrice > 0 && openPrice > 0 && highestPrice > 0 && lowestPrice > 0) {
					// 初始化计划买入/卖空价格、计划盈利/亏损金额
					if (plannedBuyPrice == 0 || plannedSellPrice == 0 || plannedProfitAmount == 0 || plannedLossAmount == 0) {
						plannedBuyPrice = openPrice - quantTradePlanned.getDeviationAmount();
						plannedSellPrice = openPrice + quantTradePlanned.getDeviationAmount();
						plannedProfitAmount = openPrice * quantTradePlanned.getPlannedSellOutProfitRate();
						plannedLossAmount = openPrice * quantTradePlanned.getPlannedStopLossProfitRate();
					}

					// 处理具体时间点的股票实时交易
					currentTime = TradeDateUtils.getUsCurrentTime();
					performRealTimeTrading(stock.getStockID(), stock.getCode(), quantTradePlanned.getTradePlannedID(), currentTime, currentPrice, plannedBuyPrice, plannedSellPrice,
							plannedProfitAmount, plannedLossAmount, accountTotalAmount, tradePlannedCount, quantTrading, true, futunnTradingHelper, quantTradeActualDao);

					// 根据实时交易状态，处理循环退出问题
					if (quantTrading.isTradingFinished()) {
						break;
					}
				}

				// 间隔500毫秒
				TimeUnit.MILLISECONDS.sleep(500);
			} catch (Exception ex) {
				String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
				String logData = String.format("quantTradePlanned=%s", CustomJSONUtils.toJSONObjectWithFieldName(quantTradePlanned));
				LOGGER.error(String.format(LogInfoUtils.HAS_DATA_TMPL, methodName, logData), ex);
				return;
			}
		}
	}

	/**
	 * 从 json 中解析出价格数据
	 *
	 * @param json
	 * @param rootName
	 * @return
	 */
	private float getPriceFromJson(String json, String rootName) {
		return CustomNumberUtils.toFloat(CustomStringUtils.substringBetween(json, String.format("\"%s\"", rootName), ",", "\"", "\""));
	}

	/**
	 * 处理具体时间点的股票实时交易
	 *
	 * @param stockID
	 * @param stockCode
	 * @param tradePlannedID
	 * @param currentTime
	 * @param currentPrice
	 * @param plannedBuyPrice
	 * @param plannedSellPrice
	 * @param plannedProfitAmount
	 * @param plannedLossAmount
	 * @param accountTotalAmount
	 * @param tradePlannedCount
	 * @param quantTrading
	 * @param isRealTrade
	 * @param futunnTradingHelper
	 * @param quantTradeActualDao
	 */
	public void performRealTimeTrading(long stockID,
	                                   String stockCode,
	                                   int tradePlannedID,
	                                   LocalTime currentTime,
	                                   float currentPrice,
	                                   float plannedBuyPrice,
	                                   float plannedSellPrice,
	                                   float plannedProfitAmount,
	                                   float plannedLossAmount,
	                                   int accountTotalAmount,
	                                   int tradePlannedCount,
	                                   QuantTrading quantTrading,
	                                   boolean isRealTrade,
	                                   FutunnTradingHelper futunnTradingHelper,
	                                   QuantTradeActualDao quantTradeActualDao) {
		// 刚开盘一小段时间不交易
		if (currentTime.isBefore(TradeDateUtils.US_TRADE_DAY_START_TIME.plusMinutes(OPENING_STOP_TRADE_MINUTES))) {
			return;
		}

		// 计算剩余交易的分钟数、买入/卖出交易成功后已过去的分钟数
		int remainderTradeMinutes = (int) CustomDateUtils.getDurationBetween(currentTime, TradeDateUtils.US_TRADE_DAY_END_TIME).toMinutes();
		int tradePassedMinutes = (quantTrading.getActualTradeStartTime() != null) ? (int) CustomDateUtils.getDurationBetween(quantTrading.getActualTradeStartTime(), currentTime).toMinutes() : 0;

		// 每天最后的交易时间段特殊处理利润率和亏损值
		boolean isReduceProfitRate = false; // 是否削减过计划盈利/亏损比例
		if (!isReduceProfitRate) {
			if (remainderTradeMinutes < CLOSING_ADVANCE_TRADE_NORMAL_MINUTES) {
				// 最后一小时左右降低利润率和亏损值，保证当天出货
				isReduceProfitRate = true;
				quantTrading.setReduceProfitRateMultiple(REDUCE_PROFIT_RATE_NORMAL_MULTIPLE);
			} else if (remainderTradeMinutes < CLOSING_ADVANCE_TRADE_LAST_MINUTES) {
				// 最后半小时左右继续降低利润率和亏损值，保证当天出货
				isReduceProfitRate = true;
				quantTrading.setReduceProfitRateMultiple(REDUCE_PROFIT_RATE_LAST_MULTIPLE);
			} else if (quantTrading.getTouchLossTimes() > TOUCH_LOSS_MAX_TIMES
					|| (quantTrading.getTouchLossTimes() > TOUCH_LOSS_MIN_TIMES && tradePassedMinutes > TRADE_PASSED_MIN_MINUTES)) {
				// 如果买入/卖出交易成功后，已过去了规定的分钟数还没有卖出，则降低利润率和亏损值，保证及时出货
				isReduceProfitRate = true;
				quantTrading.setReduceProfitRateMultiple(REDUCE_PROFIT_RATE_LAST_MULTIPLE);
			}

			if (isReduceProfitRate) {
				plannedProfitAmount = plannedProfitAmount / quantTrading.getReduceProfitRateMultiple();
				plannedLossAmount = plannedLossAmount / quantTrading.getReduceProfitRateMultiple();
			}
		}

		// 处理买入和卖空操作
		if (!quantTrading.isBuyStock() && !quantTrading.isSellStock()) {
			if (remainderTradeMinutes >= CLOSING_STOP_BUY_MINUTES) {
				if (currentPrice <= plannedBuyPrice) {
					// 计算可买入数量
					int actualTradeVolume = calcActualTradeVolume(currentPrice, accountTotalAmount, tradePlannedCount);

					// 处理真实买入交易
					if (isRealTrade) {
						boolean actualTradeStartSuccess = futunnTradingHelper.stockTrading(stockID, stockCode, TradeSideEnum.BUY, currentPrice / 1000, actualTradeVolume);
						quantTrading.setActualTradeStartSuccess(actualTradeStartSuccess);
					}

					// 写入买入交易状态数据
					if (!isRealTrade || quantTrading.isActualTradeStartSuccess()) {
						// 更新交易状态数据
						quantTrading.setBuyStock(true);
						quantTrading.setActualBuyPrice(currentPrice);
						quantTrading.setActualTradeStartTime(currentTime);
						quantTrading.setActualTradeVolume(actualTradeVolume);

						// 如果模拟买入交易成功，则将交易数据写入数据库
						if (isRealTrade) {
							QuantTradeActual buyTradeActual = QuantTradeActual.createActualBuyDataModel(tradePlannedID, stockID, stockCode, false,
									currentPrice, actualTradeVolume, "", TradeDateUtils.getUsCurrentDate(), TradeDateUtils.getUsCurrentTime());
							quantTradeActualDao.insertOrUpdateBuyTradeActual(buyTradeActual);
							quantTrading.setTradeActualID(buyTradeActual.getTradeActualID());
						}
					}
				} else if (OPEN_SHORT_SELLING && (currentPrice >= plannedSellPrice)) {
					// 计算可卖空数量
					int actualTradeVolume = calcActualTradeVolume(currentPrice, accountTotalAmount, tradePlannedCount);

					// 处理真实卖空交易（模拟交易暂时没有卖空操作功能）
					if (isRealTrade) {
						quantTrading.setActualTradeStartSuccess(true);
					}

					// 处理卖空交易状态数据
					if (!isRealTrade || quantTrading.isActualTradeStartSuccess()) {
						// 更新交易状态数据
						quantTrading.setSellStock(true);
						quantTrading.setActualSellPrice(currentPrice);
						quantTrading.setActualTradeStartTime(currentTime);
						quantTrading.setActualTradeVolume(actualTradeVolume);

						// 如果模拟卖空交易成功，则将交易数据更新到数据库
						if (isRealTrade) {
						}
					}
				}
			}
		} else {
			if (quantTrading.isBuyStock()) {
				// 已经买入了，处理卖出
				boolean isProfit = (currentPrice - plannedProfitAmount) >= quantTrading.getActualBuyPrice();
				boolean isLoss = (currentPrice + plannedLossAmount) <= quantTrading.getActualBuyPrice();
				if (isProfit) {
					quantTrading.setTouchProfitTimes(quantTrading.getTouchProfitTimes() + 1);
				}
				if (isLoss) {
					quantTrading.setTouchLossTimes(quantTrading.getTouchLossTimes() + 1);
				}

				// 已经确定到达盈利指标，或第二次亏损值到达指标，则处理卖出
				boolean profitSuccess = (isProfit && quantTrading.getTouchProfitTimes() > 0);
				boolean lossSuccess = (isLoss && quantTrading.getTouchLossTimes() > TO_SELL_TOUCH_LOSS_TIMES);
				if (profitSuccess || lossSuccess) {
					// 处理真实卖出交易
					if (isRealTrade) {
						boolean actualTradeSuccess = futunnTradingHelper.stockTrading(stockID, stockCode, TradeSideEnum.SELL, currentPrice / 1000, quantTrading.getActualTradeVolume());
						quantTrading.setActualTradeEndSuccess(actualTradeSuccess);
					}

					// 处理卖出交易状态数据
					if (!isRealTrade || quantTrading.isActualTradeEndSuccess()) {
						// 更新交易状态数据
						quantTrading.setActualSellPrice(currentPrice);
						quantTrading.setProfitOrLessAmount(QuantTradingUtils.calcProfitOrLessAmount(quantTrading));
						quantTrading.setProfitOrLessRate(QuantTradingUtils.calcProfitOrLessRate(quantTrading));
						quantTrading.setActualTradeEndTime(currentTime);
						quantTrading.setTradingFinished(true);

						// 如果模拟卖出交易成功，则将交易数据更新到数据库
						if (isRealTrade) {
							QuantTradeActual sellTradeActual = QuantTradeActual.createActualSellDataModel(quantTrading.getTradeActualID(), currentPrice, quantTrading.getActualTradeVolume(), "",
									TradeDateUtils.getUsCurrentDate(), TradeDateUtils.getUsCurrentTime(), quantTrading.getProfitOrLessAmount(), quantTrading.getProfitOrLessRate(),
									quantTrading.getTouchProfitTimes(), quantTrading.getTouchLossTimes(), quantTrading.getReduceProfitRateMultiple());
							quantTradeActualDao.updateSellTradeActual(sellTradeActual);
						}
					}
				}
			} else if (quantTrading.isSellStock()) {
				// 已经卖空了，处理赎回
				boolean isProfit = (currentPrice + plannedProfitAmount) <= quantTrading.getActualSellPrice();
				boolean isLoss = (currentPrice - plannedLossAmount) >= quantTrading.getActualSellPrice();
				if (isProfit) {
					quantTrading.setTouchProfitTimes(quantTrading.getTouchProfitTimes() + 1);
				}
				if (isLoss) {
					quantTrading.setTouchLossTimes(quantTrading.getTouchLossTimes() + 1);
				}

				// 已经确定到达盈利指标，或第二次亏损值到达指标，则处理赎回
				boolean profitSuccess = (isProfit && quantTrading.getTouchProfitTimes() > 0);
				boolean lossSuccess = (isLoss && quantTrading.getTouchLossTimes() > TO_SELL_TOUCH_LOSS_TIMES);
				if (profitSuccess || lossSuccess) {
					// 处理真实卖空赎回交易（模拟交易暂时没有卖空操作功能）
					if (isRealTrade) {
						quantTrading.setActualTradeEndSuccess(true);
					}

					// 处理卖空赎回交易状态数据
					if (!isRealTrade || quantTrading.isActualTradeEndSuccess()) {
						// 更新交易状态数据
						quantTrading.setActualBuyPrice(currentPrice);
						quantTrading.setProfitOrLessAmount(QuantTradingUtils.calcProfitOrLessAmount(quantTrading));
						quantTrading.setProfitOrLessRate(QuantTradingUtils.calcProfitOrLessRate(quantTrading));
						quantTrading.setActualTradeEndTime(currentTime);
						quantTrading.setTradingFinished(true);

						// 如果模拟赎回交易成功，则将交易数据更新到数据库
						if (isRealTrade) {
						}
					}
				}
			}
		}
	}

	/**
	 * 根据每份交易金额，计算可购买的股票数量
	 *
	 * @param actualTradePrice
	 * @param accountTotalAmount
	 * @param tradePlannedCount
	 * @return
	 */
	private int calcActualTradeVolume(float actualTradePrice, int accountTotalAmount, int tradePlannedCount) {
		int singleStockAmount = accountTotalAmount / tradePlannedCount;
		int result = (int) (singleStockAmount / actualTradePrice);
		float modPrice = singleStockAmount % actualTradePrice;
		if (modPrice > actualTradePrice / 2) {
			result++;
		}

		return result;
	}
}
