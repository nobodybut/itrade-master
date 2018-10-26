package com.trade.biz.dal.tradedrds;


import com.trade.model.tradecore.quote.MinuteQuote;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface MinuteQuoteDao {

	/**
	 * 根据 date 读取数据列表
	 *
	 * @param date
	 * @return
	 */
	List<MinuteQuote> queryListByDate(LocalDate date);

	/**
	 * 根据 stockID、date 读取数据列表
	 *
	 * @param stockID
	 * @param date
	 * @return
	 */
	List<MinuteQuote> queryListByStockIDAndDate(long stockID, LocalDate date);

	/**
	 * 根据 stockID、date、time 读取数据
	 *
	 * @param stockID
	 * @param date
	 * @param time
	 * @return
	 */
	MinuteQuote queryListByStockIDAndDateTime(long stockID, LocalDate date, LocalTime time);

	/**
	 * 根据 minuteQuoteID 读取数据
	 *
	 * @param minuteQuoteID
	 * @return
	 */
	MinuteQuote queryByMinuteQuoteID(long minuteQuoteID);

	/**
	 * 写入或更新 单个对象
	 *
	 * @param data
	 * @return
	 */
	void insertOrUpdate(MinuteQuote data);
}
