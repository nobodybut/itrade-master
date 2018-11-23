package com.trade.biz.domain.tradeacq;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.trade.biz.dal.tradecore.DayKLineDao;
import com.trade.biz.dal.tradecore.StockDao;
import com.trade.common.infrastructure.util.date.CustomDateUtils;
import com.trade.common.infrastructure.util.httpclient.HttpClientUtils;
import com.trade.common.infrastructure.util.json.CustomJSONUtils;
import com.trade.common.infrastructure.util.logger.LogInfoUtils;
import com.trade.common.infrastructure.util.math.CustomNumberUtils;
import com.trade.common.infrastructure.util.string.CustomStringUtils;
import com.trade.common.tradeutil.consts.FutunnConsts;
import com.trade.common.tradeutil.quanttradeutil.TradeDateUtils;
import com.trade.common.tradeutil.techindicatorsutil.KDJUtils;
import com.trade.model.tradecore.kline.DayKLine;
import com.trade.model.tradecore.stock.Stock;
import com.trade.model.tradecore.techindicators.KDJ;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class DayKLineAcq {

	// 常量定义
	private static final boolean ONLY_PROCESS_NEW_TRADE_DATE = false;

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
		LocalDate minTradeDate = ONLY_PROCESS_NEW_TRADE_DATE ? TradeDateUtils.getUsCurrentDate() : LocalDate.now().minusDays(KDJUtils.MAX_TRADE_DAYS);

		// 循环处理每只股票最新一天的日K线数据
		for (Stock stock : stocks) {
			List<DayKLine> dayKLines = calcDayKLines(stock.getStockID(), usDiffHours);
			for (DayKLine dayKLine : dayKLines) {
				if (!Strings.isNullOrEmpty(dayKLine.getKdjJson()) && CustomDateUtils.isAfterOrEquals(dayKLine.getDate(), minTradeDate)) {
					dayKLineDao.insertOrUpdate(dayKLine);
				}
			}
		}

		int a = 0;
	}

	/**
	 * 计算一只股票的日K线数据
	 *
	 * @param stockID
	 * @param usDiffHours
	 * @return
	 */
	private List<DayKLine> calcDayKLines(long stockID, int usDiffHours) {
		List<DayKLine> result = Lists.newArrayList();

		try {
			// 从接口中获取日K线JSON数据
			String html = HttpClientUtils.getHTML(String.format(FutunnConsts.FUTUNN_DAY_KLINE_URL_TMPL, stockID, System.currentTimeMillis()));
			String usefulCode = CustomStringUtils.substringBetween(html, "\"list\":", "]");
			String[] kLineCodes = CustomStringUtils.substringsBetween(usefulCode, "{", "}");

			// 计算具体的日K线数据，并写入结果
			for (String kLineCode : kLineCodes) {
				DayKLine dayKLine = parseDayKLine(stockID, kLineCode, usDiffHours);
				if (dayKLine != null) {
					result.add(dayKLine);
				}
			}

			// 集中计算并添加 KDJ 技术指标数据
			calcAndFillKDJ(result);

			// 集中计算并添加 MACD 技术指标数据
			// 。。。
		} catch (Throwable ex) {
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			log.error(String.format(LogInfoUtils.NO_DATA_TMPL, methodName), ex);
		}

		return result;
	}

	/**
	 * 从 JSON 结果中解析并创建日K线数据
	 *
	 * @param stockID
	 * @param kLineCode
	 * @param usDiffHours
	 * @return
	 */
	public DayKLine parseDayKLine(long stockID, String kLineCode, int usDiffHours) {
		// 如果 kLineCode 为空，则返回 null
		if (Strings.isNullOrEmpty(kLineCode)) {
			return null;
		}

		// 如果交易日期早于 KDJUtils.MAX_TRADE_DAYS，则返回 null
		LocalDate date = TradeDateUtils.getDateTimeByTimeMills(CustomNumberUtils.toLong(getKLineValue(kLineCode, "k")) * 1000, usDiffHours).toLocalDate();
		if (CustomDateUtils.getDurationBetween(date, LocalDate.now()).toDays() > KDJUtils.MAX_TRADE_DAYS) {
			return null;
		}

		// 重新处理 kLineCode
		kLineCode = kLineCode.replace(" ", "") + ",";

		// 创建 dayKLine 数据对象
		DayKLine result = new DayKLine();
		result.setStockID(stockID);
		result.setDate(date);
		result.setOpen(CustomNumberUtils.toFloat(getKLineValue(kLineCode, "o")));
		result.setClose(CustomNumberUtils.toFloat(getKLineValue(kLineCode, "c")));
		result.setHigh(CustomNumberUtils.toFloat(getKLineValue(kLineCode, "h")));
		result.setLow(CustomNumberUtils.toFloat(getKLineValue(kLineCode, "l")));
		result.setVolume(CustomNumberUtils.toInt(getKLineValue(kLineCode, "v")));
		result.setTurnover(CustomNumberUtils.toFloat(getKLineValue(kLineCode, "t")));

		// lastclose
		// turnoverRate
		// changeRate

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

	/**
	 * 集中计算 KDJ 技术指标数据
	 *
	 * @param allKLines
	 */
	private void calcAndFillKDJ(List<DayKLine> allKLines) {
		double yesterdayK = 50; // 昨日K值
		double yesterdayD = 50; // 昨日D值

		for (int i = KDJUtils.PREV_DAYS_N + 1; i <= allKLines.size() + 1; i++) {
			// 计算跳过的数量
			int skipCount = i - KDJUtils.PREV_DAYS_N - 1;
			if (skipCount < 0) {
				continue;
			}

			// 获取当前日期和前 KDJUtils.PREV_DAYS_N 天的 K线数据
			List<DayKLine> kLines = allKLines.stream().skip(skipCount).limit(KDJUtils.PREV_DAYS_N).collect(Collectors.toList());
			kLines.sort(Comparator.comparing(DayKLine::getDate, Comparator.reverseOrder()));
			DayKLine currentDayKLine = kLines.get(0);

			// 计算 当日收盘价、N天内最低价（应对比当日的最低价）、N天内最高价（应对比当日的最高价）
			float closePrice = currentDayKLine.getClose();
			float minPriceNDay = calcMinPriceNDay(kLines);
			float maxPriceNDay = calcMaxPriceNDay(kLines);

			// 计算 KDJ 结果，并写入 todayDayKLine 内
			KDJ kdj = KDJUtils.calcKDJ(closePrice, minPriceNDay, maxPriceNDay, yesterdayK, yesterdayD);
			currentDayKLine.setKdjJson(CustomJSONUtils.toJSONString(kdj));

			// 重新赋值 K、D 数据
			yesterdayK = kdj.getK();
			yesterdayD = kdj.getD();
		}
	}

	/**
	 * 计算 N 天内最低价
	 *
	 * @param kLines
	 * @return
	 */
	private float calcMinPriceNDay(List<DayKLine> kLines) {
		List<Float> prices = kLines.stream().map(x -> x.getLow()).collect(Collectors.toList());
		Collections.sort(prices);
		return prices.get(0);
	}

	/**
	 * 计算 N 天内最高价
	 *
	 * @param kLines
	 * @return
	 */
	private float calcMaxPriceNDay(List<DayKLine> kLines) {
		List<Float> prices = kLines.stream().map(x -> x.getHigh()).collect(Collectors.toList());
		Collections.sort(prices);
		return prices.get(prices.size() - 1);
	}
}
