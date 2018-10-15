package com.trade.biz.domain.tradejob.kline;

import com.google.common.base.Strings;
import com.trade.common.infrastructure.util.httpclient.HttpClientUtils;
import com.trade.common.infrastructure.util.math.CustomNumberUtils;
import com.trade.common.infrastructure.util.string.CustomStringUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.time.LocalDate;

@Component
@Slf4j
public class KlineDataSummary {

	public void execute() {
		String html = HttpClientUtils.getHTML("https://www.futunn.com/quote/kline-v2?security_id=205189&type=2&from=&_=1539191519307"); // APPL
		String usefulCode = CustomStringUtils.substringBetween(html, "\"list\":", "]");
		String[] dayKLines = CustomStringUtils.substringsBetween(usefulCode, "{", "}");

		float profitRate = 0.01F;
		int makeMoneyDays = 0;
		int loseMoneyDays = 0;
		int noOperationDays = 0;

		for (int i = 1; i < dayKLines.length; i++) {
			KLine predayKLine = createKLine(dayKLines[i - 1]);
			KLine todayKLine = createKLine(dayKLines[i]);

			if (predayKLine != null && todayKLine != null) {
				int deviationAmount = (int) ((predayKLine.getHighestPrice() - predayKLine.getLowestPrice()) * 0.4);
				int todayBuyPrice = todayKLine.getOpenPrice() - deviationAmount;
				int todaySellPrice = todayKLine.getOpenPrice() + deviationAmount;

				// 处理可买入的情况
				boolean isNoOperationDays = true;
				if (todayKLine.getLowestPrice() <= todayBuyPrice) {
					if ((todayKLine.getHighestPrice() - todayKLine.getOpenPrice() * profitRate) >= todayBuyPrice) {
						makeMoneyDays++;
					} else {
						loseMoneyDays++;
					}
					isNoOperationDays = false;
				}

				// 处理可卖空的情况
				if (isNoOperationDays && todayKLine.getHighestPrice() >= todaySellPrice) {
					if ((todayKLine.getLowestPrice() + todayKLine.getOpenPrice() * profitRate) <= todaySellPrice) {
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

	private KLine createKLine(String klineStr) {
		if (Strings.isNullOrEmpty(klineStr)) {
			return null;
		}

		KLine result = new KLine();
		klineStr = klineStr.replace(" ", "") + ",";
		result.setOpenPrice(calKLineValue(klineStr, "o"));
		result.setClosePrice(calKLineValue(klineStr, "c"));
		result.setLowestPrice(calKLineValue(klineStr, "l"));
		result.setHighestPrice(calKLineValue(klineStr, "h"));
		result.setTotalVolumes(calKLineValue(klineStr, "v"));
		result.setTotalAmount(calKLineValue(klineStr, "t"));

		if (result.getOpenPrice() <= 0 || result.getClosePrice() <= 0 || result.getHighestPrice() <= 0 || result.getLowestPrice() <= 0) {
			result = null;
		}

		return result;
	}

	private int calKLineValue(String klineStr, String prefix) {
		return CustomNumberUtils.toInt(CustomStringUtils.substringBetween(klineStr, String.format("\"%s\":", prefix), ","));
	}

	@Getter
	@Setter
	public class KLine implements Serializable {
		private LocalDate date;
		private int openPrice;
		private int closePrice;
		private int lowestPrice;
		private int highestPrice;
		private int totalVolumes;
		private int totalAmount;
	}
}
