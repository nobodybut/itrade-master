package com.trade.biz.dal.tradedrds.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.trade.biz.dal.base.TradeDrdsBaseDao;
import com.trade.biz.dal.tradedrds.MinuteQuoteDao;
import com.trade.biz.dal.util.MinuteQuoteDaoUtils;
import com.trade.common.infrastructure.util.json.CustomJSONUtils;
import com.trade.common.infrastructure.util.logger.LogInfoUtils;
import com.trade.model.tradecore.minutequote.MinuteQuote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class MinuteQuoteDaoImpl extends TradeDrdsBaseDao implements MinuteQuoteDao {

	// 日志记录
	private static final Logger LOGGER = LoggerFactory.getLogger(MinuteQuoteDaoImpl.class);

	@Override
	public List<MinuteQuote> queryListByDate(LocalDate date) {
		Map<String, Object> paramMap = Maps.newHashMap();
		paramMap.put("date", date);
		List<MinuteQuote> result = this.getSqlSessionTemplate().selectList("MinuteQuoteMapper.queryListByDate", paramMap);
		result.sort(Comparator.comparing(MinuteQuote::getTime));

		return result;
	}

	@Override
	public Set<String> queryUniqueKeysByDate(LocalDate date) {
		Set<String> result = Sets.newHashSet();

		Map<String, Object> paramMap = Maps.newHashMap();
		paramMap.put("date", date);
		List<MinuteQuote> minuteQuotes = this.getSqlSessionTemplate().selectList("MinuteQuoteMapper.queryUniqueKeysByDate", paramMap);
		minuteQuotes.sort(Comparator.comparing(MinuteQuote::getTime));

		for (MinuteQuote minuteQuote : minuteQuotes) {
			result.add(MinuteQuoteDaoUtils.calMinuteQuoteUniqueKey(minuteQuote.getStockID(), date, minuteQuote.getTime()));
		}

		return result;
	}

	@Override
	public List<MinuteQuote> queryListByStockIDAndDate(long stockID, LocalDate date) {
		Map<String, Object> paramMap = Maps.newHashMap();
		paramMap.put("stockID", stockID);
		paramMap.put("date", date);
		List<MinuteQuote> result = this.getSqlSessionTemplate().selectList("MinuteQuoteMapper.queryListByStockIDAndDate", paramMap);
		result.sort(Comparator.comparing(MinuteQuote::getTime));

		return result;
	}

	@Override
	public LinkedHashMap<LocalDate, List<MinuteQuote>> queryListMapByStockIDAndDate(long stockID, List<LocalDate> dates) {
		LinkedHashMap<LocalDate, List<MinuteQuote>> resultMap = Maps.newLinkedHashMap();

		Map<String, Object> paramMap = Maps.newHashMap();
		paramMap.put("stockID", stockID);
		paramMap.put("dates", dates);
		List<MinuteQuote> minuteQuotes = this.getSqlSessionTemplate().selectList("MinuteQuoteMapper.queryListByStockIDAndDates", paramMap);

		for (MinuteQuote minuteQuote : minuteQuotes) {
			if (!resultMap.containsKey(minuteQuote.getDate())) {
				resultMap.put(minuteQuote.getDate(), Lists.newArrayList());
			}
			resultMap.get(minuteQuote.getDate()).add(minuteQuote);
		}

		for (Map.Entry<LocalDate, List<MinuteQuote>> entry : resultMap.entrySet()) {
			entry.getValue().sort(Comparator.comparing(MinuteQuote::getTime));
		}

		return resultMap;
	}

	@Override
	public MinuteQuote queryByStockIDAndDateTime(long stockID, LocalDate date, LocalTime time) {
		Map<String, Object> paramMap = Maps.newHashMap();
		paramMap.put("stockID", stockID);
		paramMap.put("date", date);
		paramMap.put("time", time);
		return this.getSqlSessionTemplate().selectOne("MinuteQuoteMapper.queryByStockIDAndDateTime", paramMap);
	}

	@Override
	public MinuteQuote queryByMinuteQuoteID(long minuteQuoteID) {
		return this.getSqlSessionTemplate().selectOne("MinuteQuoteMapper.queryByMinuteQuoteID", minuteQuoteID);
	}

	@Override
	public void insertOrUpdate(MinuteQuote data) {
		MinuteQuote item = queryByStockIDAndDateTime(data.getStockID(), data.getDate(), data.getTime());
		if (item == null) {
			insert(data);
		} else {
			data.setMinuteQuoteID(item.getMinuteQuoteID());
			update(data);
		}
	}

	@Override
	public void insert(MinuteQuote data) {
		try {
			if (data != null) {
				this.getSqlSessionTemplate().insert("MinuteQuoteMapper.insert", data);
			}
		} catch (Throwable ex) {
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LOGGER.error(String.format(LogInfoUtils.HAS_DATA_TMPL, methodName, CustomJSONUtils.toJSONString(data)), ex);
		}
	}

	@Override
	public void update(MinuteQuote data) {
		try {
			if (data != null) {
				this.getSqlSessionTemplate().update("MinuteQuoteMapper.update", data);
			}
		} catch (Throwable ex) {
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LOGGER.error(String.format(LogInfoUtils.HAS_DATA_TMPL, methodName, CustomJSONUtils.toJSONString(data)), ex);
		}
	}
}
