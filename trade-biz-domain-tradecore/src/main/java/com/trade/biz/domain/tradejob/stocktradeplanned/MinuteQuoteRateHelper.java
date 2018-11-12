package com.trade.biz.domain.tradejob.stocktradeplanned;

import com.google.common.collect.Lists;
import com.trade.common.infrastructure.util.math.CustomMathUtils;
import com.trade.model.tradecore.enums.OptionTypeEnum;
import com.trade.model.tradecore.quote.MinuteQuote;
import com.trade.model.tradecore.quote.MinuteQuoteRate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Component
@Slf4j
public class MinuteQuoteRateHelper {

	/**
	 * 根据股票每天的全部分钟线数据，计算股票每日交易经过清洗后的振幅数据（合并小的交易振幅数据）
	 *
	 * @param minuteQuotes
	 * @param date
	 * @return
	 */
	public List<MinuteQuoteRate> calcMinuteQuoteRates(List<MinuteQuote> minuteQuotes, LocalDate date) {
		//  根据股票每天的全部分钟线数据，计算股票每日交易全部振幅数据
		List<MinuteQuoteRate> result = calcAllMinuteQuoteRates(minuteQuotes, date);

		// 多次清洗股票每日交易经的振幅数据（合并小的交易振幅数据）
		for (int i = 0; i < 3; i++) {
			cleaningMinuteQuoteRates(result, 0.1F);
		}

		return result;
	}

	/**
	 * 根据股票每天的全部分钟线数据，计算股票每日交易全部振幅数据
	 *
	 * @param minuteQuotes
	 * @param date
	 * @return
	 */
	private List<MinuteQuoteRate> calcAllMinuteQuoteRates(List<MinuteQuote> minuteQuotes, LocalDate date) {
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

					// 根据当前趋势计算并添加 minuteQuoteRate 数据
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
					// 没有趋势方向时，需要首先计算趋势方向
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

	/**
	 * 清洗股票每日交易经的振幅数据（合并小的交易振幅数据）
	 *
	 * @param minuteQuoteRates
	 * @param clearPriceProportion
	 * @return
	 */
	private void cleaningMinuteQuoteRates(List<MinuteQuoteRate> minuteQuoteRates, float clearPriceProportion) {
		List<Integer> readyToDelIndexs = Lists.newArrayList();
		for (int index = 1; index < minuteQuoteRates.size() - 1; index++) {
			MinuteQuoteRate preMinuteQuoteRate = minuteQuoteRates.get(index - 1);
			MinuteQuoteRate currentMinuteQuoteRate = minuteQuoteRates.get(index);
			MinuteQuoteRate nextMinuteQuoteRate = minuteQuoteRates.get(index + 1);

			if (preMinuteQuoteRate != null && currentMinuteQuoteRate != null && nextMinuteQuoteRate != null) {
				if (preMinuteQuoteRate.getOptionType() == nextMinuteQuoteRate.getOptionType()
						&& preMinuteQuoteRate.getOptionType() != currentMinuteQuoteRate.getOptionType()) {
					float totalChangeRate = Math.abs(preMinuteQuoteRate.getChangeRate() + nextMinuteQuoteRate.getChangeRate());
					float currentChangeRate = Math.abs(currentMinuteQuoteRate.getChangeRate());
					if (totalChangeRate * clearPriceProportion >= currentChangeRate) {
						// 删除当前 minuteQuoteRate 数据
						readyToDelIndexs = Lists.newArrayList(index, index + 1);

						// 合并后2个 minuteQuoteRate 数据到 preMinuteQuoteRate 上
						preMinuteQuoteRate.setEndTime(nextMinuteQuoteRate.getEndTime());
						preMinuteQuoteRate.setOptionType(nextMinuteQuoteRate.getOptionType());
						preMinuteQuoteRate.setCurrentPrice(nextMinuteQuoteRate.getCurrentPrice());
						preMinuteQuoteRate.setVolume(preMinuteQuoteRate.getVolume() + currentMinuteQuoteRate.getVolume() + nextMinuteQuoteRate.getVolume());
						preMinuteQuoteRate.setTurnover(preMinuteQuoteRate.getTurnover() + currentMinuteQuoteRate.getTurnover() + nextMinuteQuoteRate.getTurnover());
						preMinuteQuoteRate.setChangeRate(preMinuteQuoteRate.getChangeRate() + currentMinuteQuoteRate.getChangeRate() + nextMinuteQuoteRate.getChangeRate());

						break;
					}
				}
			}
		}

		// 判断是否还有待清洗数据，如果有则继续清洗，如果没有则退出递归循环
		if (readyToDelIndexs.size() > 0) {
			for (int index = minuteQuoteRates.size() - 1; index >= 0; index--) {
				if (readyToDelIndexs.contains(index)) {
					minuteQuoteRates.remove(index);
				}
			}
			cleaningMinuteQuoteRates(minuteQuoteRates, clearPriceProportion);
		} else {
			return;
		}
	}
}
