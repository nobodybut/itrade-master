package com.trade.biz.domain._backup.kline;

import com.google.common.base.Strings;
import com.trade.common.infrastructure.util.httpclient.HttpClientUtils;
import com.trade.common.infrastructure.util.math.CustomNumberUtils;
import com.trade.common.infrastructure.util.string.CustomStringUtils;
import com.trade.common.tradeutil.consts.FutunnConsts;
import com.trade.model.tradecore.kline.DayKLine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DayKLineSummary {

	public void execute() {
		String html = HttpClientUtils.getHTML(String.format(FutunnConsts.FUTUNN_DAY_KLINE_URL_TMPL, "205189", System.currentTimeMillis())); // APPL
		String usefulCode = CustomStringUtils.substringBetween(html, "\"list\":", "]");
		String[] klineCodes = CustomStringUtils.substringsBetween(usefulCode, "{", "}");

		float profitRate = 0.01F;
		int profitDays = 0;
		int lossDays = 0;
		int noOperationDays = 0;

		for (int i = 1; i < klineCodes.length; i++) {
			DayKLine predayKLine = createDayKLine(klineCodes[i - 1]);
			DayKLine todayKLine = createDayKLine(klineCodes[i]);

			if (predayKLine != null && todayKLine != null) {
				double deviationAmount = (int) ((predayKLine.getHigh() - predayKLine.getLow()) * 0.4);
				double todayBuyPrice = todayKLine.getOpen() - deviationAmount;
				double todaySellPrice = todayKLine.getOpen() + deviationAmount;

				// 处理可买入的情况
				boolean isNoOperationDays = true;
				if (todayKLine.getLow() <= todayBuyPrice) {
					if ((todayKLine.getHigh() - todayKLine.getOpen() * profitRate) >= todayBuyPrice) {
						profitDays++;
					} else {
						lossDays++;
					}
					isNoOperationDays = false;
				}

				// 处理可卖空的情况
				if (isNoOperationDays && todayKLine.getHigh() >= todaySellPrice) {
					if ((todayKLine.getLow() + todayKLine.getOpen() * profitRate) <= todaySellPrice) {
						profitDays++;
					} else {
						lossDays++;
					}
					isNoOperationDays = false;
				}

				// 处理不操作的情况
				if (isNoOperationDays) {
					noOperationDays++;
				}
			}
		}

		boolean success = true;
		if (profitDays < lossDays) {
			success = false;
		}
		boolean isSuccess = success;
	}

	public DayKLine createDayKLine(String klineCode) {
		if (Strings.isNullOrEmpty(klineCode)) {
			return null;
		}

		DayKLine result = new DayKLine();
		klineCode = klineCode.replace(" ", "") + ",";
		result.setOpen(CustomNumberUtils.toFloat(getKLineValue(klineCode, "o")));
		result.setClose(CustomNumberUtils.toFloat(getKLineValue(klineCode, "c")));
		result.setLow(CustomNumberUtils.toFloat(getKLineValue(klineCode, "l")));
		result.setHigh(CustomNumberUtils.toFloat(getKLineValue(klineCode, "h")));
		result.setVolume(CustomNumberUtils.toInt(getKLineValue(klineCode, "v")));
		result.setTurnover(CustomNumberUtils.toFloat(getKLineValue(klineCode, "t")));

		if (result.getOpen() <= 0 || result.getClose() <= 0 || result.getHigh() <= 0 || result.getLow() <= 0) {
			result = null;
		}

		return result;
	}

	public String getKLineValue(String klineCode, String prefix) {
		return CustomStringUtils.substringBetween(klineCode, String.format("\"%s\":", prefix), ",");
	}
}
