package com.trade.biz.domain.tradequant.quanttrading;

import com.trade.biz.dal.tradecore.QuantTradeActualDao;
import com.trade.biz.dal.tradecore.QuantTradePlannedDao;
import com.trade.biz.dal.tradecore.StockDao;
import com.trade.biz.domain.tradequant.futu.FutunnAccountHelper;
import com.trade.biz.domain.tradequant.futu.FutunnTradingHelper;
import com.trade.common.infrastructure.util.logger.LogInfoUtils;
import com.trade.model.tradecore.enums.TradeSideEnum;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Slf4j
public class QuantTradingManager {

	// 日志记录
	private static final Logger LOGGER = LoggerFactory.getLogger(QuantTradingManager.class);

	// 线程池
	private final ExecutorService EXECUTOR_POOL = Executors.newFixedThreadPool(3);

	// 依赖注入
	@Resource
	private StockDao stockDao;

	@Resource
	private QuantTradePlannedDao quantTradePlannedDao;

	@Resource
	private QuantTradeActualDao quantTradeActualDao;

	@Resource
	private QuantTradingQueue quantTradingQueue;

	@Resource
	private FutunnAccountHelper futunnAccountHelper;

	@Resource
	private FutunnTradingHelper futunnTradingHelper;

	public void execute() {
		try {
			// 开启股票实时交易循环初始化线程
			QuantTradingLoopInitThreadWorker quantTradingLoopInitThreadWorker = new QuantTradingLoopInitThreadWorker();
			quantTradingLoopInitThreadWorker.setStockDao(stockDao);
			quantTradingLoopInitThreadWorker.setQuantTradePlannedDao(quantTradePlannedDao);
			quantTradingLoopInitThreadWorker.setQuantTradeActualDao(quantTradeActualDao);
			quantTradingLoopInitThreadWorker.setQuantTradingQueue(quantTradingQueue);
			EXECUTOR_POOL.execute(quantTradingLoopInitThreadWorker);

			// 开启买入交易线程
			QuantTradingThreadWorker buyQuantTradingThreadWorker = new QuantTradingThreadWorker();
			buyQuantTradingThreadWorker.setQuantTradingQueue(quantTradingQueue);
			buyQuantTradingThreadWorker.setFutunnAccountHelper(futunnAccountHelper);
			buyQuantTradingThreadWorker.setFutunnTradingHelper(futunnTradingHelper);
			buyQuantTradingThreadWorker.setQuantTradeActualDao(quantTradeActualDao);
			buyQuantTradingThreadWorker.setTradeSide(TradeSideEnum.BUY);
			EXECUTOR_POOL.execute(buyQuantTradingThreadWorker);

			// 开启卖出交易线程
			QuantTradingThreadWorker sellQuantTradingThreadWorker = new QuantTradingThreadWorker();
			sellQuantTradingThreadWorker.setQuantTradingQueue(quantTradingQueue);
			sellQuantTradingThreadWorker.setFutunnAccountHelper(futunnAccountHelper);
			sellQuantTradingThreadWorker.setFutunnTradingHelper(futunnTradingHelper);
			sellQuantTradingThreadWorker.setQuantTradeActualDao(quantTradeActualDao);
			sellQuantTradingThreadWorker.setTradeSide(TradeSideEnum.SELL);
			EXECUTOR_POOL.execute(sellQuantTradingThreadWorker);

			// 记录启动成功日志
			LOGGER.info("QuantTradingManager init SUCCESS!!!!");
		} catch (Exception ex) {
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LOGGER.error(String.format(LogInfoUtils.NO_DATA_TMPL, methodName), ex);
		}
	}
}
