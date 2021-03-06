package com.trade.biz.domain.tradeacq;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.trade.biz.dal.tradecore.DayKLineDao;
import com.trade.biz.dal.tradecore.StockDao;
import com.trade.common.infrastructure.util.date.CustomDateUtils;
import com.trade.common.infrastructure.util.httpclient.HttpClientUtils;
import com.trade.common.infrastructure.util.logger.LogInfoUtils;
import com.trade.common.infrastructure.util.math.CustomMathUtils;
import com.trade.common.infrastructure.util.math.CustomNumberUtils;
import com.trade.common.infrastructure.util.string.CustomStringUtils;
import com.trade.common.tradeutil.consts.FutunnConsts;
import com.trade.common.tradeutil.klineutil.DayKLineKDJUtils;
import com.trade.common.tradeutil.klineutil.DayKLineUtils;
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
public class DayKLineAcq {

	// 依赖注入
	@Resource
	private StockDao stockDao;

	@Resource
	private DayKLineDao dayKLineDao;

	public void execute() {
		// 读取美股所有股票数据
		List<Stock> stocks = stockDao.queryListByMarketID(2);

		// 计算服务器时间与美国时差
		int usDiffHours = TradeDateUtils.calUsDiffHours(LocalDateTime.now());

		// 计算可以更新数据库数据的最小交易日期（大于此交易日期的数据都可以更新）
		LocalDate minTradeDate = TradeDateUtils.getUsCurrentDate();

		// 循环处理每只股票最新一天的日K线数据
		for (Stock stock : stocks) {
			long startMills = System.currentTimeMillis();
			List<DayKLine> dayKLines = calcDayKLines(stock, usDiffHours, minTradeDate);
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
	private List<DayKLine> calcDayKLines(Stock stock, int usDiffHours, LocalDate minTradeDate) {
		List<DayKLine> result = Lists.newArrayList();

		try {
			// 抓取并解析 minTradeDate 当天及之后的日K线数据
			List<DayKLine> dayKLines = acqAndParseDayKLines(stock, usDiffHours, minTradeDate);

			// 获取 minTradeDate 前 KDJUtils.PREV_DAYS_N 天的日K线数据，并合并2类K线数据
			List<LocalDate> prevTradeDates = TradeDateUtils.calcPrevTradeDates(minTradeDate, KDJUtils.PREV_DAYS_N + 4);
			List<DayKLine> prevDayKLines = dayKLineDao.queryListByStockIDAndDates(stock.getStockID(), prevTradeDates);
			prevDayKLines = DayKLineUtils.calcPrevNDaysKLines(prevDayKLines, minTradeDate, KDJUtils.PREV_DAYS_N - 1);
			dayKLines.addAll(prevDayKLines);

			// 集中计算并添加 KDJ 技术指标数据
			DayKLineKDJUtils.calcAndFillDayKLineKDJ(dayKLines);

			// 集中计算并添加 MACD 技术指标数据
			// ....

			// 计算最终结果列表
			for (DayKLine dayKLine : dayKLines) {
				if (!Strings.isNullOrEmpty(dayKLine.getKdjJson()) && CustomDateUtils.isAfterOrEquals(dayKLine.getDate(), minTradeDate)) {
					result.add(dayKLine);
				}
			}
		} catch (Throwable ex) {
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			log.error(String.format(LogInfoUtils.NO_DATA_TMPL, methodName), ex);
		}

		return result;
	}

	/**
	 * 抓取并解析日K线数据
	 *
	 * @param stock
	 * @param usDiffHours
	 * @param minTradeDate
	 * @return
	 */
	public List<DayKLine> acqAndParseDayKLines(Stock stock, int usDiffHours, LocalDate minTradeDate) {
		List<DayKLine> result = Lists.newArrayList();

		// 从接口中获取日K线JSON数据
		String html = HttpClientUtils.getHTML(String.format(FutunnConsts.FUTUNN_DAY_KLINE_URL_TMPL, stock.getStockID(), System.currentTimeMillis()));
		String usefulCode = CustomStringUtils.substringBetween(html, "\"list\":", "]");
		String[] kLineCodes = CustomStringUtils.substringsBetween(usefulCode, "{", "}");

		// 计算具体的日K线数据，并写入结果
		for (String kLineCode : kLineCodes) {
			// 如果 kLineCode 为空，则不继续处理
			if (Strings.isNullOrEmpty(kLineCode)) {
				continue;
			}

			// 如果交易日期早于 minTradeDate，则不继续处理
			LocalDate tradeDate = TradeDateUtils.getDateTimeByTimeMills(CustomNumberUtils.toLong(getKLineValue(kLineCode, "k")) * 1000, usDiffHours).toLocalDate();
			if (tradeDate.isBefore(minTradeDate)) {
				continue;
			}

			// 从 json 结果中解析并创建日K线数据
			DayKLine dayKLine = parseDayKLine(stock, kLineCode, tradeDate);
			if (dayKLine != null) {
				result.add(dayKLine);
			}
		}

		return result;
	}

	/**
	 * 从 json 结果中解析并创建日K线数据
	 *
	 * @param stock
	 * @param kLineCode
	 * @param tradeDate
	 * @return
	 */
	public DayKLine parseDayKLine(Stock stock, String kLineCode, LocalDate tradeDate) {
		// 重新处理 kLineCode
		kLineCode = kLineCode.replace(" ", "") + ",";

		// 创建 dayKLine 数据对象
		DayKLine result = new DayKLine();
		result.setStockID(stock.getStockID());
		result.setDate(tradeDate);
		result.setOpen(CustomNumberUtils.toFloat(getKLineValue(kLineCode, "o")));
		result.setClose(CustomNumberUtils.toFloat(getKLineValue(kLineCode, "c")));
		result.setHigh(CustomNumberUtils.toFloat(getKLineValue(kLineCode, "h")));
		result.setLow(CustomNumberUtils.toFloat(getKLineValue(kLineCode, "l")));
		result.setVolume(CustomNumberUtils.toInt(getKLineValue(kLineCode, "v")));
		result.setTurnover(CustomNumberUtils.toLong(getKLineValue(kLineCode, "t")));
		result.setTurnoverRate((float) CustomMathUtils.round((((double) (result.getTurnover() / 1000) / (double) stock.getMarketValue()) * 100), 3)); // 换手率

		if (result.getOpen() <= 0 || result.getClose() <= 0 || result.getHigh() <= 0 || result.getLow() <= 0) {
			result = null;
		}

		return result;
	}

	/**
	 * 从 JSON 结果中解析某个节点具体的数值
	 *
	 * @param kLineCode
	 * @param prefix
	 * @return
	 */
	public String getKLineValue(String kLineCode, String prefix) {
		return CustomStringUtils.substringBetween(kLineCode, String.format("\"%s\":", prefix), ",");
	}
}
