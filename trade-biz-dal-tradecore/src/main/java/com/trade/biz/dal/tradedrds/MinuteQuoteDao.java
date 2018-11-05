package com.trade.biz.dal.tradedrds;


import com.trade.model.tradecore.quote.MinuteQuote;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

public interface MinuteQuoteDao {

	/**
	 * 根据 date 读取数据列表
	 *
	 * @param date
	 * @return
	 */
	List<MinuteQuote> queryListByDate(LocalDate date);

	/**
	 * 根据 date 读取分钟线唯一标示key数据列表
	 *
	 * @param date
	 * @return
	 */
	Set<String> queryUniqueKeysByDate(LocalDate date);

	/**
	 * 根据 stockID、date 读取数据列表
	 *
	 * @param stockID
	 * @param date
	 * @return
	 */
	List<MinuteQuote> queryListByStockIDAndDate(long stockID, LocalDate date);

	/**
	 * 根据 stockID、dates 读取数据列表 map
	 *
	 * @param stockID
	 * @param dates
	 * @return
	 */
	LinkedHashMap<LocalDate, List<MinuteQuote>> queryListMapByStockIDAndDate(long stockID, List<LocalDate> dates);

	/**
	 * 根据 stockID、date、time 读取数据
	 *
	 * @param stockID
	 * @param date
	 * @param time
	 * @return
	 */
	MinuteQuote queryByStockIDAndDateTime(long stockID, LocalDate date, LocalTime time);

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
	 */
	void insertOrUpdate(MinuteQuote data);

	/**
	 * 写入 单个对象
	 *
	 * @param data
	 */
	void insert(MinuteQuote data);

	/**
	 * 更新 单个对象
	 *
	 * @param data
	 */
	void update(MinuteQuote data);
}
