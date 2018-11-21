package com.trade.biz.domain.tradejob.mocktrading;

import com.trade.biz.dal.tradecore.StockDao;
import com.trade.biz.dal.tradecore.StockTradePlannedDao;
import com.trade.biz.domain.tradejob.futu.FutunnAccountHelper;
import com.trade.biz.domain.tradejob.futu.FutunnLoginHelper;
import com.trade.biz.domain.tradejob.futu.FutunnTradingHelper;
import com.trade.common.infrastructure.util.httpclient.HttpClientUtils;
import com.trade.common.infrastructure.util.json.CustomJSONUtils;
import com.trade.common.infrastructure.util.logger.LogInfoUtils;
import com.trade.common.infrastructure.util.math.CustomNumberUtils;
import com.trade.common.infrastructure.util.string.CustomStringUtils;
import com.trade.common.tradeutil.stockutil.TradeDateUtils;
import com.trade.model.tradecore.consts.FutunnConsts;
import com.trade.model.tradecore.stocktrade.StockTradePlanned;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@Slf4j
public class MockTradingManager {

	// 线程池
	private static final int TRADE_PLANNED_COUNT = 200;
	private final ExecutorService EXECUTOR_POOL = Executors.newCachedThreadPool();

	// 依赖注入
	@Resource
	private FutunnTradingHelper futunnTradingHelper;

	@Resource
	private FutunnAccountHelper futunnAccountHelper;

	@Resource
	private FutunnLoginHelper futunnLoginHelper;

	@Resource
	private StockDao stockDao;

	@Resource
	private StockTradePlannedDao stockTradePlannedDao;

	public void execute() {
		// 读取当前日期的股票交易计划数据列表
		LocalDate tradeDate = TradeDateUtils.getUSCurrentDate();
		List<StockTradePlanned> stockTradePlanneds = stockTradePlannedDao.queryListByDate(tradeDate);
		if (stockTradePlanneds.size() > TRADE_PLANNED_COUNT) {
			stockTradePlanneds = stockTradePlanneds.stream().limit(TRADE_PLANNED_COUNT).collect(Collectors.toList());
		}

		// 每个股票交易计划开一个单独的线程进行实时交易处理
		for (StockTradePlanned stockTradePlanned : stockTradePlanneds) {
			if (stockTradePlanned.getPlannedTradeDate().equals(tradeDate)) {
				EXECUTOR_POOL.execute(() -> performStockRealtimeTrading(stockTradePlanned, tradeDate));
			}
		}
	}

	/**
	 * 处理单个股票交易计划实时交易
	 *
	 * @param stockTradePlanned
	 * @param tradeDate
	 */
	private void performStockRealtimeTrading(StockTradePlanned stockTradePlanned, LocalDate tradeDate) {
		while (true) {
			try {
				// 通过接口获取实时报价数据
				String json = HttpClientUtils.getHTML(String.format(FutunnConsts.FUTUNN_QUOTE_BASIC_URL_TMPL, stockTradePlanned.getStockID(), String.valueOf(System.currentTimeMillis())));
				float currentPrice = getPriceFromJson(json, "price");
				float openPrice = getPriceFromJson(json, "open_price");
				float highestPrice = getPriceFromJson(json, "highest_price");
				float lowestPrice = getPriceFromJson(json, "lowest_price");
				if (currentPrice > 0 && openPrice > 0 && highestPrice > 0 && lowestPrice > 0) {
					float plannedBuyPrice = openPrice - stockTradePlanned.getDeviationAmount();
					float plannedSellPrice = openPrice + stockTradePlanned.getDeviationAmount();
					float plannedProfitAmount = openPrice * stockTradePlanned.getPlannedSellOutProfitRate();
					float plannedLossAmount = openPrice * stockTradePlanned.getPlannedStopLossProfitRate();


				}

				// 间隔500毫秒
				TimeUnit.MILLISECONDS.sleep(500);
			} catch (Exception ex) {
				String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
				String logData = String.format("stockTradePlanned=%s", CustomJSONUtils.toJSONObjectWithFieldName(stockTradePlanned));
				log.error(String.format(LogInfoUtils.HAS_DATA_TMPL, methodName, logData), ex);
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
}
