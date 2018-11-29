package com.trade.biz.domain.tradequant.quanttrading;

import com.trade.biz.dal.tradecore.QuantTradeActualDao;
import com.trade.biz.dal.tradecore.QuantTradePlannedDao;
import com.trade.biz.dal.tradecore.StockDao;
import com.trade.biz.domain.tradequant.futu.FutunnAccountHelper;
import com.trade.biz.domain.tradequant.futu.FutunnTradingHelper;
import com.trade.common.tradeutil.consts.QuantTradeConsts;
import com.trade.common.tradeutil.quanttradeutil.TradeDateUtils;
import com.trade.model.tradecore.quanttrade.QuantTradePlanned;
import com.trade.model.tradecore.stock.Stock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Component
@Slf4j
public class QuantTradingManager {

	// 线程池
	private final ExecutorService EXECUTOR_POOL = Executors.newCachedThreadPool();

	// 依赖注入
	@Resource
	private FutunnAccountHelper futunnAccountHelper;

	@Resource
	private FutunnTradingHelper futunnTradingHelper;

	@Resource
	private StockDao stockDao;

	@Resource
	private QuantTradePlannedDao quantTradePlannedDao;

	@Resource
	private QuantTradeActualDao quantTradeActualDao;

	public void execute() {
		// 读取当前日期的股票交易计划数据列表
		LocalDate tradeDate = TradeDateUtils.getUsCurrentDate();
		List<QuantTradePlanned> quantTradePlanneds = quantTradePlannedDao.queryListByDate(tradeDate);
		if (quantTradePlanneds.size() > QuantTradeConsts.PLANNED_TRADE_STOCK_MAX_COUNT) {
			quantTradePlanneds = quantTradePlanneds.stream().limit(QuantTradeConsts.PLANNED_TRADE_STOCK_MAX_COUNT).collect(Collectors.toList());
		}
		int tradePlannedCount = quantTradePlanneds.size();

		// 每个股票交易计划开一个单独的线程进行实时交易处理
		for (QuantTradePlanned quantTradePlanned : quantTradePlanneds) {
			if (quantTradePlanned.getPlannedTradeDate().equals(tradeDate)) {
				Stock stock = stockDao.queryByStockID(quantTradePlanned.getStockID());
				EXECUTOR_POOL.execute(() -> new QuantRealtimeTrading().execute(stock, quantTradePlanned, tradePlannedCount, futunnAccountHelper, futunnTradingHelper, quantTradeActualDao));
			}
		}
	}
}
