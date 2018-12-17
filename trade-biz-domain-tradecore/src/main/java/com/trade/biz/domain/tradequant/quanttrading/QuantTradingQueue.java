package com.trade.biz.domain.tradequant.quanttrading;

import com.google.common.collect.Sets;
import com.trade.biz.domain.trademodel.QuantTradingCondition;
import com.trade.common.infrastructure.util.json.CustomJSONUtils;
import com.trade.common.infrastructure.util.logger.LogInfoUtils;
import com.trade.model.tradecore.enums.TradeSideEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Component
public class QuantTradingQueue {

	// 日志记录
	private static final Logger LOGGER = LoggerFactory.getLogger(QuantTradingQueue.class);

	// 股票实时交易阻塞队列、股票实时交易加入前检查数组
	private static final BlockingQueue<QuantTradingCondition> s_buyTradingConditionBlockingQueue = new LinkedBlockingQueue<>();
	private static final BlockingQueue<QuantTradingCondition> s_sellTradingConditionBlockingQueue = new LinkedBlockingQueue<>();
	private static final BlockingQueue<QuantTradingCondition> s_sellShortTradingConditionBlockingQueue = new LinkedBlockingQueue<>();
	private static final BlockingQueue<QuantTradingCondition> s_buyBackTradingConditionBlockingQueue = new LinkedBlockingQueue<>();
	private static final Set<Long> s_buyTradingStockIDsCheckList = Sets.newHashSet();
	private static final Set<Long> s_sellTradingStockIDsCheckList = Sets.newHashSet();
	private static final Set<Long> s_sellShortTradingStockIDsCheckList = Sets.newHashSet();
	private static final Set<Long> s_buyBackTradingStockIDsCheckList = Sets.newHashSet();

	/**
	 * 取出股票实时交易条件（阻塞）
	 *
	 * @param tradeSide
	 * @return
	 */
	public QuantTradingCondition pollQuantTradingCondition(TradeSideEnum tradeSide) {
		try {
			return getTradingConditionBlockingQueue(tradeSide).poll(10, TimeUnit.SECONDS);
		} catch (Exception ex) {
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			String logData = String.valueOf(tradeSide.ordinal());
			LOGGER.error(String.format(LogInfoUtils.HAS_DATA_TMPL, methodName, logData), ex);
		}

		return null;
	}

	/**
	 * 加入股票实时交易条件
	 *
	 * @param quantTradingCondition
	 */
	public void offerQuantTradingCondition(QuantTradingCondition quantTradingCondition) {
		try {
			Set<Long> tradingStockIDsCheckList = getTradingStockIDsCheckList(quantTradingCondition.getTradeSide());
			if (!tradingStockIDsCheckList.contains(quantTradingCondition.getStock().getStockID())) {
				tradingStockIDsCheckList.add(quantTradingCondition.getStock().getStockID());
				getTradingConditionBlockingQueue(quantTradingCondition.getTradeSide()).offer(quantTradingCondition, 1, TimeUnit.SECONDS);
			}
		} catch (Exception ex) {
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			String logData = CustomJSONUtils.toJSONString(quantTradingCondition);
			LOGGER.error(String.format(LogInfoUtils.HAS_DATA_TMPL, methodName, logData), ex);
		}
	}

	/**
	 * 从股票实时交易加入前检查数组中删除单个股票ID
	 *
	 * @param tradeSide
	 * @param stockID
	 */
	public void removeStockIDFromCheckList(TradeSideEnum tradeSide, long stockID) {
		try {
			Set<Long> tradingStockIDsCheckList = getTradingStockIDsCheckList(tradeSide);
			if (tradingStockIDsCheckList.contains(stockID)) {
				tradingStockIDsCheckList.remove(stockID);
			}
		} catch (Exception ex) {
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			String logData = String.format("tradeSide=%s, stockID=%s", tradeSide.ordinal(), stockID);
			LOGGER.error(String.format(LogInfoUtils.HAS_DATA_TMPL, methodName, logData), ex);
		}
	}

	/**
	 * 清空所有股票实时交易阻塞队列、股票实时交易加入前检查数组
	 */
	public void clearAllQuantTradingQueue() {
		try {
			s_buyTradingConditionBlockingQueue.clear();
			s_sellTradingConditionBlockingQueue.clear();
			s_sellShortTradingConditionBlockingQueue.clear();
			s_buyBackTradingConditionBlockingQueue.clear();

			s_buyTradingStockIDsCheckList.clear();
			s_sellTradingStockIDsCheckList.clear();
			s_sellShortTradingStockIDsCheckList.clear();
			s_buyBackTradingStockIDsCheckList.clear();
		} catch (Exception ex) {
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LOGGER.error(String.format(LogInfoUtils.NO_DATA_TMPL, methodName), ex);
		}
	}

	/**
	 * 根据 交易类型 获取股票实时交易阻塞队列
	 *
	 * @param tradeSide
	 * @return
	 */
	private BlockingQueue<QuantTradingCondition> getTradingConditionBlockingQueue(TradeSideEnum tradeSide) {
		switch (tradeSide) {
			case BUY:
				return s_buyTradingConditionBlockingQueue;
			case SELL:
				return s_sellTradingConditionBlockingQueue;
			case SELL_SHORT:
				return s_sellShortTradingConditionBlockingQueue;
			case BUY_BACK:
				return s_buyBackTradingConditionBlockingQueue;
			default:
				return new LinkedBlockingQueue<>();
		}
	}

	/**
	 * 根据 交易类型 股票实时交易加入前检查数组
	 *
	 * @param tradeSide
	 * @return
	 */
	private Set<Long> getTradingStockIDsCheckList(TradeSideEnum tradeSide) {
		switch (tradeSide) {
			case BUY:
				return s_buyTradingStockIDsCheckList;
			case SELL:
				return s_sellTradingStockIDsCheckList;
			case SELL_SHORT:
				return s_sellShortTradingStockIDsCheckList;
			case BUY_BACK:
				return s_buyBackTradingStockIDsCheckList;
			default:
				return Sets.newHashSet();
		}
	}
}
