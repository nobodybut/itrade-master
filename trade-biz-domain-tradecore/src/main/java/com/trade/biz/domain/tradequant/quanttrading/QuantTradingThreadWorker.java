package com.trade.biz.domain.tradequant.quanttrading;

import com.trade.biz.dal.tradecore.QuantTradeActualDao;
import com.trade.biz.domain.tradequant.futu.FutunnAccountHelper;
import com.trade.biz.domain.tradequant.futu.FutunnCreateOrderHelper;
import com.trade.biz.domain.tradequant.quanttrading.tradingcondition.QuantTradingCondition;
import com.trade.common.infrastructure.util.logger.LogInfoUtils;
import com.trade.common.tradeutil.quanttradeutil.TradeDateUtils;
import com.trade.model.tradecore.enums.TradeSideEnum;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class QuantTradingThreadWorker implements Runnable {

	// 日志记录
	private static final Logger LOGGER = LoggerFactory.getLogger(QuantTradingThreadWorker.class);

	// 线程池
	private final ExecutorService TRADING_WORKER_EXECUTOR_POOL = Executors.newCachedThreadPool();

	// @Setters
	@Setter
	private QuantTradingQueue quantTradingQueue;

	@Setter
	private FutunnAccountHelper futunnAccountHelper;

	@Setter
	private FutunnCreateOrderHelper futunnCreateOrderHelper;

	@Setter
	private QuantTradeActualDao quantTradeActualDao;

	@Setter
	private TradeSideEnum tradeSide;

	@Override
	public void run() {
		while (true) {
			try {
				// 先判断当前日期是否为交易日的交易时间段内
				if (!TradeDateUtils.isUsTradeDate(TradeDateUtils.getUsCurrentDate()) || !TradeDateUtils.isUsTradeTime(TradeDateUtils.getUsCurrentTime())) {
					TimeUnit.SECONDS.sleep(1);
					continue;
				}

				// 从队列中读取一个实时交易条件（没有获取数据时会阻塞住线程，直到获取到数据为止）
				final QuantTradingCondition quantTradingCondition = quantTradingQueue.pollQuantTradingCondition(tradeSide);
				if (quantTradingCondition == null) {
					continue;
				}

				// 使用单独线程完成股票实时交易过程（买/卖/卖空/赎回）
				TRADING_WORKER_EXECUTOR_POOL.execute(() -> new QuantTradingThreadExecutor().execute(quantTradingCondition, futunnAccountHelper, futunnCreateOrderHelper, quantTradeActualDao, quantTradingQueue));

				// 线程暂停一段时间
				try {
					TimeUnit.MILLISECONDS.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} catch (Exception ex) {
				String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
				LOGGER.error(String.format(LogInfoUtils.NO_DATA_TMPL, methodName), ex);
			}
		}
	}
}
