//package com.trade.biz.domain._backup.kline;
//
//import com.google.common.base.Strings;
//import com.google.common.collect.Lists;
//import com.google.common.collect.Maps;
//import com.trade.common.infrastructure.util.httpclient.HttpClientUtils;
//import com.trade.common.infrastructure.util.math.CustomNumberUtils;
//import com.trade.common.infrastructure.util.string.CustomStringUtils;
//import com.trade.common.tradeutil.techindicatorsutil.KDJUtils;
//import com.trade.common.tradeutil.consts.FutunnConsts;
//import com.trade.common.tradeutil.quanttradeutil.TradeDateUtils;
//import com.trade.model.tradecore.kline.DayKLine;
//import com.trade.model.tradecore.techindicators.KDJ;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//@Component
//@Slf4j
//public class DayKLineSummary {
//
//	public void execute() {
//		List<DayKLine> kLines = Lists.newArrayList();
//		String html = HttpClientUtils.getHTML(String.format(FutunnConsts.FUTUNN_DAY_KLINE_URL_TMPL, "210182", System.currentTimeMillis()));
//		String usefulCode = CustomStringUtils.substringBetween(html, "\"list\":", "]");
//		String[] kLineCodes = CustomStringUtils.substringsBetween(usefulCode, "{", "}");
//
//		float profitRate = 0.01F;
//		int profitDays = 0;
//		int lossDays = 0;
//		int noOperationDays = 0;
//		int usDiffHours = TradeDateUtils.calUsDiffHours(LocalDateTime.now());
//
//		for (int i = 1; i < kLineCodes.length; i++) {
//			DayKLine predayKLine = createDayKLine(kLineCodes[i - 1], usDiffHours);
//			DayKLine todayKLine = createDayKLine(kLineCodes[i], usDiffHours);
//			kLines.add(todayKLine);
//
//			if (predayKLine != null && todayKLine != null) {
//				double deviationAmount = (int) ((predayKLine.getHigh() - predayKLine.getLow()) * 0.4);
//				double todayBuyPrice = todayKLine.getOpen() - deviationAmount;
//				double todaySellPrice = todayKLine.getOpen() + deviationAmount;
//
//				// 处理可买入的情况
//				boolean isNoOperationDays = true;
//				if (todayKLine.getLow() <= todayBuyPrice) {
//					if ((todayKLine.getHigh() - todayKLine.getOpen() * profitRate) >= todayBuyPrice) {
//						profitDays++;
//					} else {
//						lossDays++;
//					}
//					isNoOperationDays = false;
//				}
//
//				// 处理可卖空的情况
//				if (isNoOperationDays && todayKLine.getHigh() >= todaySellPrice) {
//					if ((todayKLine.getLow() + todayKLine.getOpen() * profitRate) <= todaySellPrice) {
//						profitDays++;
//					} else {
//						lossDays++;
//					}
//					isNoOperationDays = false;
//				}
//
//				// 处理不操作的情况
//				if (isNoOperationDays) {
//					noOperationDays++;
//				}
//			}
//		}
//
//		calcKDJ(kLines);
//
//		boolean success = true;
//		if (profitDays < lossDays) {
//			success = false;
//		}
//		boolean isSuccess = success;
//	}
//
//	private void calcKDJ(List<DayKLine> allKLines) {
//		Map<LocalDate, KDJ> kdjMap = Maps.newLinkedHashMap();
//		allKLines = allKLines.stream().filter(x -> x.getDate().isAfter(LocalDate.now().minusDays(365))).collect(Collectors.toList());
//
//		double yesterdayK = 50; // 昨日K值
//		double yesterdayD = 50; // 昨日D值
//		int preN = 9;
//
//		for (int i = preN + 1; i < allKLines.size(); i++) {
//			List<DayKLine> kLines = allKLines.stream().skip(i - preN - 1).limit(preN).collect(Collectors.toList());
//			kLines.sort(Comparator.comparing(DayKLine::getDate, Comparator.reverseOrder()));
//			DayKLine todayDayKLine = kLines.get(0);
//
//			double closePrice = todayDayKLine.getClose(); // 当日收盘价
//			double minPriceNDay = calcMinPriceNDay(kLines); // N天内最低价（应对比当日的最低价）
//			double maxPriceNDay = calcMaxPriceNDay(kLines); // N天内最高价（应对比当日的最高价）
//
//			// 写入计算结果集
//			KDJ kdj = KDJUtils.calcKDJ(closePrice, minPriceNDay, maxPriceNDay, yesterdayK, yesterdayD);
//			kdjMap.put(todayDayKLine.getDate(), kdj);
//
//			// 重新复制 K、D 的值
//			yesterdayK = kdj.getK();
//			yesterdayD = kdj.getD();
//		}
//
//		int size = kdjMap.size();
//	}
//
//	private float calcMinPriceNDay(List<DayKLine> kLines) {
//		List<Float> prices = kLines.stream().map(x -> x.getLow()).collect(Collectors.toList());
//		Collections.sort(prices);
//		return prices.get(0);
//	}
//
//	private float calcMaxPriceNDay(List<DayKLine> kLines) {
//		List<Float> prices = kLines.stream().map(x -> x.getHigh()).collect(Collectors.toList());
//		Collections.sort(prices);
//		return prices.get(prices.size() - 1);
//	}
//
//	public DayKLine createDayKLine(String kLineCode, int usDiffHours) {
//		if (Strings.isNullOrEmpty(kLineCode)) {
//			return null;
//		}
//
//		DayKLine result = new DayKLine();
//		kLineCode = kLineCode.replace(" ", "") + ",";
//		result.setDate(TradeDateUtils.getDateTimeByTimeMills(CustomNumberUtils.toLong(getKLineValue(kLineCode, "k")) * 1000, usDiffHours).toLocalDate());
//		result.setOpen(CustomNumberUtils.toFloat(getKLineValue(kLineCode, "o")));
//		result.setClose(CustomNumberUtils.toFloat(getKLineValue(kLineCode, "c")));
//		result.setLow(CustomNumberUtils.toFloat(getKLineValue(kLineCode, "l")));
//		result.setHigh(CustomNumberUtils.toFloat(getKLineValue(kLineCode, "h")));
//		result.setVolume(CustomNumberUtils.toInt(getKLineValue(kLineCode, "v")));
//		result.setTurnover(CustomNumberUtils.toFloat(getKLineValue(kLineCode, "t")));
//
//		if (result.getOpen() <= 0 || result.getClose() <= 0 || result.getHigh() <= 0 || result.getLow() <= 0) {
//			result = null;
//		}
//
//		return result;
//	}
//
//	public String getKLineValue(String kLineCode, String prefix) {
//		return CustomStringUtils.substringBetween(kLineCode, String.format("\"%s\":", prefix), ",");
//	}
//}
