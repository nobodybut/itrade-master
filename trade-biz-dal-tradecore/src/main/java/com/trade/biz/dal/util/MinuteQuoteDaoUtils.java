package com.trade.biz.dal.util;

import com.trade.common.infrastructure.util.date.CustomDateFormatUtils;
import com.trade.model.tradecore.minutequote.MinuteQuote;

import java.time.LocalDate;
import java.time.LocalTime;

public class MinuteQuoteDaoUtils {

	/**
	 * 计算分钟线唯一标示key
	 *
	 * @param minuteQuote
	 * @return
	 */
	public static String calMinuteQuoteUniqueKey(MinuteQuote minuteQuote) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(minuteQuote.getStockID());
		stringBuilder.append("^");
		stringBuilder.append(CustomDateFormatUtils.formatDate(minuteQuote.getDate()));
		stringBuilder.append("^");
		stringBuilder.append(CustomDateFormatUtils.formatTime_HHmm(minuteQuote.getTime()));

		return stringBuilder.toString();
	}

	/**
	 * 计算分钟线唯一标示key
	 *
	 * @param stockID
	 * @param date
	 * @param time
	 * @return
	 */
	public static String calMinuteQuoteUniqueKey(long stockID, LocalDate date, LocalTime time) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(stockID);
		stringBuilder.append("^");
		stringBuilder.append(CustomDateFormatUtils.formatDate(date));
		stringBuilder.append("^");
		stringBuilder.append(CustomDateFormatUtils.formatTime_HHmm(time));

		return stringBuilder.toString();
	}
}
