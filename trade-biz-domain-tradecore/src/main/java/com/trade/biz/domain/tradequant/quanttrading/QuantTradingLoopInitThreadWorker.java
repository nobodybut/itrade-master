package com.trade.biz.domain.tradequant.quanttrading;

import com.trade.biz.dal.tradecore.QuantTradeActualDao;
import com.trade.biz.dal.tradecore.QuantTradePlannedDao;
import com.trade.biz.dal.tradecore.StockDao;
import com.trade.biz.domain.tradeacq.DayKLineAcq;
import com.trade.biz.domain.trademodel.QuantTradingCondition;
import com.trade.biz.domain.tradequant.quanttradeplanned.QuantTradePlannedManager;
import com.trade.common.infrastructure.util.json.CustomJSONUtils;
import com.trade.common.infrastructure.util.logger.LogInfoUtils;
import com.trade.common.tradeutil.consts.QuantTradeConsts;
import com.trade.common.tradeutil.quanttradeutil.TradeDateUtils;
import com.trade.model.tradecore.enums.TradeSideEnum;
import com.trade.model.tradecore.enums.TradingHandlerTypeEnum;
import com.trade.model.tradecore.quanttrade.QuantTradeActual;
import com.trade.model.tradecore.quanttrade.QuantTradePlanned;
import com.trade.model.tradecore.quanttrading.QuantTrading;
import com.trade.model.tradecore.stock.Stock;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 处理股票实时交易循环初始化类
 */
public class QuantTradingLoopInitThreadWorker implements Runnable {

	// 日志记录
	private static final Logger LOGGER = LoggerFactory.getLogger(QuantTradingLoopInitThreadWorker.class);

	// 相关变量
	private static boolean s_isInitQuantTradePlanned = false;
	private static boolean s_isFirstInit = true;

	// @Setters
	@Setter
	private StockDao stockDao;

	@Setter
	private QuantTradePlannedDao quantTradePlannedDao;

	@Setter
	private QuantTradeActualDao quantTradeActualDao;

	@Setter
	private QuantTradingQueue quantTradingQueue;

	@Setter
	private DayKLineAcq dayKLineAcq;

	@Setter
	private QuantTradePlannedManager quantTradePlannedManager;

	@Override
	public void run() {
		while (true) {
			try {
				// 先判断当前日期是否为交易日
				LocalDate tradeDate = TradeDateUtils.getUsCurrentDate();
				if (!TradeDateUtils.isUsTradeDate(tradeDate)) {
					TimeUnit.MINUTES.sleep(10);
					continue;
				}

				// 判断当前时间是否为美股开盘前时间，如果是则做相关处理
				if (!s_isInitQuantTradePlanned && (s_isFirstInit || TradeDateUtils.isBeforeUsTradeOpenTime())) {
					// 读取未交易成功的历史交易
					List<QuantTradeActual> notSelledQuantTradeActuals = quantTradeActualDao.queryListNotSelled();
					List<Long> notSelledStockIDs = notSelledQuantTradeActuals.stream().map(x -> x.getStockID()).distinct().collect(Collectors.toList());

					// 读取当前日期的股票交易计划数据列表
					List<QuantTradePlanned> quantTradePlanneds = quantTradePlannedDao.queryListByDate(tradeDate);
					if (quantTradePlanneds.size() > 0 && notSelledStockIDs.size() > 0) {
						quantTradePlanneds = quantTradePlanneds.stream().filter(x -> !notSelledStockIDs.contains(x.getStockID())).collect(Collectors.toList());
					}
					if (quantTradePlanneds.size() > QuantTradeConsts.PLANNED_TRADE_STOCK_MAX_COUNT) {
						quantTradePlanneds = quantTradePlanneds.stream().limit(QuantTradeConsts.PLANNED_TRADE_STOCK_MAX_COUNT).collect(Collectors.toList());
					}

					// 计算当天的股票交易数量
					int tradePlannedCount = notSelledStockIDs.size() + quantTradePlanneds.size();

					// 初始化未交易成功的历史交易到实时交易待处理队列
					initQuantTradeNotSelled(notSelledQuantTradeActuals, tradePlannedCount);

					// 初始化当日股票交易计划到实时交易待处理队列
					initQuantTradePlanned(quantTradePlanneds, tradePlannedCount, tradeDate);

					// 设置标识位
					s_isInitQuantTradePlanned = true;
					s_isFirstInit = false;

					// 记录文件日志
					LOGGER.info("initQuantTradePlanned SUCCESS!!");
				}

				// 判断当前时间是否为美股收盘后时间，如果是则做相关处理
				if (s_isInitQuantTradePlanned && TradeDateUtils.isAfterUsTradeCloseTime()) {
					// 清空本地队列、设置标识位
					quantTradingQueue.clearAllQuantTradingQueue();
					s_isInitQuantTradePlanned = false;

					// 抓取当日K线数据
					dayKLineAcq.execute();

					// 执行股票交易计划计算
					quantTradePlannedManager.execute();

					// 记录文件日志
					LOGGER.info("clearAllQuantTradingQueue + dayKLineAcq + quantTradePlanned SUCCESS!!");
				}

				// 暂停一段时间
				TimeUnit.MINUTES.sleep(10);
				continue;
			} catch (Exception ex) {
				String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
				LOGGER.error(String.format(LogInfoUtils.NO_DATA_TMPL, methodName), ex);
			}
		}
	}

	/**
	 * 初始化未交易成功的历史交易到实时交易待处理队列
	 *
	 * @param notSelledQuantTradeActuals
	 * @param tradePlannedCount
	 */
	private void initQuantTradeNotSelled(List<QuantTradeActual> notSelledQuantTradeActuals, int tradePlannedCount) {
		for (QuantTradeActual notSelledQuantTradeActual : notSelledQuantTradeActuals) {
			Stock stock = stockDao.queryByStockID(notSelledQuantTradeActual.getStockID());
			QuantTradePlanned quantTradePlanned = quantTradePlannedDao.queryByTradePlannedID(notSelledQuantTradeActual.getTradePlannedID());

			if (stock != null && quantTradePlanned != null) {
				// 写入卖出队列
				QuantTrading toSellQuantTrading = createToSellQuantTrading(notSelledQuantTradeActual);
				QuantTradingCondition quantTradingCondition = QuantTradingCondition.createDataModel(TradeSideEnum.SELL, stock, notSelledQuantTradeActual.getTradingHandlerType(), quantTradePlanned, tradePlannedCount, toSellQuantTrading);
				quantTradingQueue.offerQuantTradingCondition(quantTradingCondition);

				// 记录文件日志
				LOGGER.info("initQuantTradeNotSelled, stockCode={}, tradeSide=SELL, quantTradePlanned={}, tradePlannedCount={}",
						stock.getCode(), CustomJSONUtils.toJSONStringWithFieldName(quantTradePlanned), tradePlannedCount);
			} else {
				if (stock == null) {
					LOGGER.error("stock(id={}) is NULL!!", quantTradePlanned.getStockID());
				}
				if (quantTradePlanned == null) {
					LOGGER.error("quantTradePlanned(id={}) is NULL!!", notSelledQuantTradeActual.getTradePlannedID());
				}
			}
		}
	}

	/**
	 * 创建准备卖出交易的 quantTrading 数据
	 *
	 * @param notSelledQuantTradeActual
	 * @return
	 */
	private QuantTrading createToSellQuantTrading(QuantTradeActual notSelledQuantTradeActual) {
		QuantTrading quantTrading = new QuantTrading();
		quantTrading.setActualTradeStartSuccess(true);
		quantTrading.setBuyStock(true);
		quantTrading.setActualBuyPrice(notSelledQuantTradeActual.getActualBuyPrice());
		quantTrading.setActualTradeVolume(notSelledQuantTradeActual.getActualBuyVolume());
		quantTrading.setActualTradeStartTime(notSelledQuantTradeActual.getActualBuyTradeTime());
		quantTrading.setTradeActualID(notSelledQuantTradeActual.getTradeActualID());

		return quantTrading;
	}

	/**
	 * 初始化当日股票交易计划到实时交易待处理队列
	 *
	 * @param quantTradePlanneds
	 * @param tradePlannedCount
	 * @param tradeDate
	 */
	private void initQuantTradePlanned(List<QuantTradePlanned> quantTradePlanneds, int tradePlannedCount, LocalDate tradeDate) {
		for (QuantTradePlanned quantTradePlanned : quantTradePlanneds) {
			if (quantTradePlanned.getTradeDate().equals(tradeDate)) {
				Stock stock = stockDao.queryByStockID(quantTradePlanned.getStockID());

				if (stock != null) {
					// 写入买入队列
					QuantTradingCondition quantTradingCondition = QuantTradingCondition.createDataModel(TradeSideEnum.BUY, stock, TradingHandlerTypeEnum.LOW_PROFIT_HANDLER, quantTradePlanned, tradePlannedCount, new QuantTrading());
					quantTradingQueue.offerQuantTradingCondition(quantTradingCondition);

					// 记录文件日志
					LOGGER.info("initQuantTradePlanned, stockCode={}, tradeSide=BUY, quantTradePlanned={}, tradePlannedCount={}",
							stock.getCode(), CustomJSONUtils.toJSONStringWithFieldName(quantTradePlanned), tradePlannedCount);
				} else {
					LOGGER.error("stock({}) is NULL!!", quantTradePlanned.getStockID());
				}
			}
		}
	}
}

