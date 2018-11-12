package com.trade.biz.domain.tradejob.stocktradeplanned;

import com.google.common.collect.Lists;
import com.trade.common.infrastructure.util.math.CustomMathUtils;
import com.trade.model.tradecore.enums.OptionTypeEnum;
import com.trade.model.tradecore.quote.MinuteQuote;
import com.trade.model.tradecore.quote.MinuteQuoteRate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Component
public class MinuteQuoteRateHelper {

	/**
	 * 根据股票每天的全部分钟线数据，计算股票每日交易振幅数据（合并小的交易振幅数据）
	 *
	 * @param minuteQuotes
	 * @param date
	 * @return
	 */
	public List<MinuteQuoteRate> calcMinuteQuoteRates(List<MinuteQuote> minuteQuotes, LocalDate date) {
		List<MinuteQuoteRate> result = Lists.newArrayList();

		if (minuteQuotes.size() > 0) {
			OptionTypeEnum optionType = OptionTypeEnum.ALL;
			LocalTime startTime = minuteQuotes.get(0).getTime();
			float originalPrice = minuteQuotes.get(0).getPrice();
			float currentPrice = minuteQuotes.get(0).getPrice();
			long volume = minuteQuotes.get(0).getVolume();
			long turnover = minuteQuotes.get(0).getTurnover();

			for (int i = 0; i < minuteQuotes.size(); i++) {
				MinuteQuote minuteQuote = minuteQuotes.get(i);
				if (i == 0) {
					continue;
				}

				if (optionType != OptionTypeEnum.ALL) {
					// 计算是否维持当前趋势
					boolean maintainCurrentTrends = (optionType == OptionTypeEnum.CALL) ? (minuteQuote.getPrice() >= currentPrice) : (minuteQuote.getPrice() <= currentPrice);

					if (maintainCurrentTrends) {
						// 维持当前趋势
						currentPrice = minuteQuote.getPrice();
						volume += minuteQuote.getVolume();
						turnover += minuteQuote.getTurnover();
					} else {
						// 趋势反转，计算 minuteQuoteRate 数据，并添加入最终结果
						float changeRate = CustomMathUtils.round(((currentPrice - originalPrice) / currentPrice), 5) * 100;
						MinuteQuoteRate minuteQuoteRate = MinuteQuoteRate.createDataModel(minuteQuote.getStockID(), date, startTime, minuteQuote.getTime(),
								optionType, originalPrice, currentPrice, volume, turnover, changeRate);
						result.add(minuteQuoteRate);

						// 重置各项初始化数据
						optionType = (optionType == OptionTypeEnum.CALL) ? OptionTypeEnum.PUT : OptionTypeEnum.CALL;
						startTime = minuteQuote.getTime();
						originalPrice = currentPrice;
						currentPrice = minuteQuote.getPrice();
						volume = minuteQuote.getVolume();
						turnover = minuteQuote.getTurnover();
					}
				} else {
					if (minuteQuote.getPrice() != currentPrice) {
						optionType = (minuteQuote.getPrice() > currentPrice) ? OptionTypeEnum.CALL : OptionTypeEnum.PUT;
					}
					currentPrice = minuteQuote.getPrice();
					volume += minuteQuote.getVolume();
					turnover += minuteQuote.getTurnover();
				}
			}
		}

		return result;
	}
}
