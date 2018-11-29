package com.trade.biz.domain.tradeacq;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.trade.biz.dal.tradecore.DayKLineDao;
import com.trade.biz.dal.tradecore.StockDao;
import com.trade.common.infrastructure.util.logger.LogInfoUtils;
import com.trade.common.tradeutil.klineutil.DayKLineKDJUtils;
import com.trade.common.tradeutil.quanttradeutil.TradeDateUtils;
import com.trade.common.tradeutil.techindicatorsutil.KDJUtils;
import com.trade.model.tradecore.kline.DayKLine;
import com.trade.model.tradecore.stock.Stock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class DayKLineAllAcq {

	// 依赖注入
	@Resource
	private StockDao stockDao;

	@Resource
	private DayKLineDao dayKLineDao;

	@Resource
	private DayKLineAcq dayKLineAcq;

	public void execute() {
		// 读取美股所有股票数据
		List<Stock> stocks = stockDao.queryListByMarketID(2);

		// 计算服务器时间与美国时差
		int usDiffHours = TradeDateUtils.calUsDiffHours(LocalDateTime.now());

		// 计算可以更新数据库数据的最小交易日期（大于此交易日期的数据都可以更新）
		LocalDate minTradeDate = LocalDate.now().minusDays(KDJUtils.MAX_TRADE_DAYS);

		// 循环处理每只股票最新一天的日K线数据
		for (Stock stock : stocks) {
			long startMills = System.currentTimeMillis();
			List<DayKLine> dayKLines = calcAllDayKLines(stock, usDiffHours, minTradeDate);
			for (DayKLine dayKLine : dayKLines) {
				dayKLineDao.insertOrUpdate(dayKLine);
			}

			log.info("stock kline update SUCCESS! stockCode={}, spendTime={}", stock.getCode(), System.currentTimeMillis() - startMills);
		}
	}

	/**
	 * 计算一只股票的日K线数据
	 *
	 * @param stock
	 * @param usDiffHours
	 * @param minTradeDate
	 * @return
	 */
	private List<DayKLine> calcAllDayKLines(Stock stock, int usDiffHours, LocalDate minTradeDate) {
		List<DayKLine> result = Lists.newArrayList();

		try {
			// 抓取并解析日K线数据
			List<DayKLine> dayKLines = dayKLineAcq.acqAndParseDayKLines(stock, usDiffHours, minTradeDate);

			// 集中计算并添加 KDJ 技术指标数据
			DayKLineKDJUtils.calcAndFillDayKLineKDJ(dayKLines);

			// 集中计算并添加 MACD 技术指标数据
			// ....

			// 计算最终结果列表
			for (DayKLine dayKLine : dayKLines) {
				if (!Strings.isNullOrEmpty(dayKLine.getKdjJson())) {
					result.add(dayKLine);
				}
			}
		} catch (Throwable ex) {
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			log.error(String.format(LogInfoUtils.NO_DATA_TMPL, methodName), ex);
		}

		return result;
	}
}
