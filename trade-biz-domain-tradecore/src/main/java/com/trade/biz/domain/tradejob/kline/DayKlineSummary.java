package com.trade.biz.domain.tradejob.kline;

import com.google.common.base.Strings;
import com.trade.common.infrastructure.util.httpclient.HttpClientUtils;
import com.trade.common.infrastructure.util.math.CustomNumberUtils;
import com.trade.common.infrastructure.util.string.CustomStringUtils;
import com.trade.model.tradecore.kline.DayKline;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DayKlineSummary {

	public void execute() {
		String html = HttpClientUtils.getHTML("https://www.futunn.com/quote/kline-v2?security_id=205189&type=2&from=&_=1539191519307"); // APPL
		String usefulCode = CustomStringUtils.substringBetween(html, "\"list\":", "]");
		String[] klineCodes = CustomStringUtils.substringsBetween(usefulCode, "{", "}");

		float profitRate = 0.01F;
		int makeMoneyDays = 0;
		int loseMoneyDays = 0;
		int noOperationDays = 0;

		for (int i = 1; i < klineCodes.length; i++) {
			DayKline predayData = createDayKline(klineCodes[i - 1]);
			DayKline todayData = createDayKline(klineCodes[i]);

			if (predayData != null && todayData != null) {
				double deviationAmount = (int) ((predayData.getHigh() - predayData.getLow()) * 0.4);
				double todayBuyPrice = todayData.getOpen() - deviationAmount;
				double todaySellPrice = todayData.getOpen() + deviationAmount;

				// 处理可买入的情况
				boolean isNoOperationDays = true;
				if (todayData.getLow() <= todayBuyPrice) {
					if ((todayData.getHigh() - todayData.getOpen() * profitRate) >= todayBuyPrice) {
						makeMoneyDays++;
					} else {
						loseMoneyDays++;
					}
					isNoOperationDays = false;
				}

				// 处理可卖空的情况
				if (isNoOperationDays && todayData.getHigh() >= todaySellPrice) {
					if ((todayData.getLow() + todayData.getOpen() * profitRate) <= todaySellPrice) {
						makeMoneyDays++;
					} else {
						loseMoneyDays++;
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
		if (makeMoneyDays < loseMoneyDays) {
			success = false;
		}
		boolean isSuccess = success;
	}

	public DayKline createDayKline(String klineCode) {
		if (Strings.isNullOrEmpty(klineCode)) {
			return null;
		}

		DayKline result = new DayKline();
		klineCode = klineCode.replace(" ", "") + ",";
		result.setOpen(CustomNumberUtils.toFloat(getKlineValue(klineCode, "o")));
		result.setClose(CustomNumberUtils.toFloat(getKlineValue(klineCode, "c")));
		result.setLow(CustomNumberUtils.toFloat(getKlineValue(klineCode, "l")));
		result.setHigh(CustomNumberUtils.toFloat(getKlineValue(klineCode, "h")));
		result.setVolume(CustomNumberUtils.toInt(getKlineValue(klineCode, "v")));
		result.setTurnover(CustomNumberUtils.toFloat(getKlineValue(klineCode, "t")));

		if (result.getOpen() <= 0 || result.getClose() <= 0 || result.getHigh() <= 0 || result.getLow() <= 0) {
			result = null;
		}

		return result;
	}

	public String getKlineValue(String klineCode, String prefix) {
		return CustomStringUtils.substringBetween(klineCode, String.format("\"%s\":", prefix), ",");
	}
}
