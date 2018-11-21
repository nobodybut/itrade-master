package com.trade.biz.domain.tradequant.quanttradeanalysis;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.trade.common.infrastructure.business.conf.DebugConfigUtils;
import com.trade.common.infrastructure.util.collection.CustomListMathUtils;
import com.trade.common.infrastructure.util.math.CustomMathUtils;
import com.trade.model.tradecore.enums.OptionTypeEnum;
import com.trade.model.tradecore.minutequote.MinuteQuote;
import com.trade.model.tradecore.minutequote.MinuteQuoteRate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class QuantTradeRateHelper {

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
			float clearPriceRate = 0.10F; // (i == 0) ? 0.1F : calcAvgPriceRate(result);
			if (i == 1) {
				clearPriceRate = 0.15F;
			} else if (i == 2) {
				clearPriceRate = 0.20F;
			}
			cleaningMinuteQuoteRates(result, clearPriceRate);

			if (DebugConfigUtils.isDebug()) {
				Map<LocalTime, Float> priceChangeRateMap = testCalcPriceChangeRateMap(result);
				if (priceChangeRateMap.size() > 0) {
				}
			}
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
	 * @param clearPriceRate
	 */
	private void cleaningMinuteQuoteRates(List<MinuteQuoteRate> minuteQuoteRates, float clearPriceRate) {
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
					if (totalChangeRate * clearPriceRate >= currentChangeRate) {
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
			cleaningMinuteQuoteRates(minuteQuoteRates, clearPriceRate);
		} else {
			return;
		}
	}

	/**
	 * 计算振幅平均值
	 *
	 * @param minuteQuoteRates
	 * @return
	 */
	private float calcAvgPriceRate(List<MinuteQuoteRate> minuteQuoteRates) {
		List<Float> priceRates = Lists.newArrayList();

		List<Float> changeRates = minuteQuoteRates.stream().map(x -> Math.abs(x.getChangeRate())).distinct().collect(Collectors.toList());
		if (minuteQuoteRates.size() > 1) {
			Collections.sort(changeRates);
			float minChangeRate = changeRates.get(0) * 1.1F;
			float maxChangeRate = changeRates.get(changeRates.size() - 1) * 0.9F;
			return maxChangeRate * 0.3F;

//			for (MinuteQuoteRate minuteQuoteRate : minuteQuoteRates) {
//				float priceRate = Math.abs(minuteQuoteRate.getChangeRate());
//				if (priceRate != 0 && priceRate >= minChangeRate && priceRate <= maxChangeRate) {
//					priceRates.add(priceRate);
//				}
//
//			}
		} else {
			priceRates = changeRates;
		}

		return CustomListMathUtils.calFloatAvg(priceRates, 3);
	}

	/**
	 * 测试计算经过清洗后的振幅数据简单版（用于测试振幅计算的准确性）
	 *
	 * @param result
	 * @return
	 */
	private Map<LocalTime, Float> testCalcPriceChangeRateMap(List<MinuteQuoteRate> result) {
		Map<LocalTime, Float> mapResult = Maps.newLinkedHashMap();
		for (MinuteQuoteRate minuteQuoteRate : result) {
			mapResult.put(minuteQuoteRate.getStartTime(), minuteQuoteRate.getChangeRate());
		}

		return mapResult;
	}
}
