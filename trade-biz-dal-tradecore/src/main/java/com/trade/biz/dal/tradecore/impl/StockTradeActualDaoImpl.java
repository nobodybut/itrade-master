package com.trade.biz.dal.tradecore.impl;

import com.google.common.collect.Maps;
import com.trade.biz.dal.base.TradeCoreBaseDao;
import com.trade.biz.dal.tradecore.StockTradeActualDao;
import com.trade.common.infrastructure.util.json.CustomJSONUtils;
import com.trade.common.infrastructure.util.logger.LogInfoUtils;
import com.trade.model.tradecore.enums.TradeSideEnum;
import com.trade.model.tradecore.stocktrade.StockTradeActual;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public class StockTradeActualDaoImpl extends TradeCoreBaseDao implements StockTradeActualDao {

	// 日志记录
	private static final Logger LOGGER = LoggerFactory.getLogger(StockTradeActualDaoImpl.class);

	@Override
	public List<StockTradeActual> queryListByDate(LocalDate date) {
		Map<String, Object> paramMap = Maps.newHashMap();
		paramMap.put("date", date);
		return this.getSqlSessionTemplate().selectList("StockTradeActualMapper.queryListByDate", paramMap);
	}

	@Override
	public List<StockTradeActual> queryListByStockIDAndDate(long stockID, LocalDate date) {
		Map<String, Object> paramMap = Maps.newHashMap();
		paramMap.put("stockID", stockID);
		paramMap.put("date", date);
		return this.getSqlSessionTemplate().selectList("StockTradeActualMapper.queryListByStockIDAndDate", paramMap);
	}

	@Override
	public StockTradeActual queryByTradePlannedIDAndTradeSideAndActualTime(int tradePlannedID, TradeSideEnum tradeSide, LocalTime actualTime) {
		Map<String, Object> paramMap = Maps.newHashMap();
		paramMap.put("tradePlannedID", tradePlannedID);
		paramMap.put("tradeSide", tradeSide.ordinal());
		paramMap.put("actualTime", actualTime);
		return this.getSqlSessionTemplate().selectOne("StockTradeActualMapper.queryByTradePlannedIDAndTradeSideAndActualTime", paramMap);
	}

	@Override
	public void insertOrUpdate(StockTradeActual data) {
		StockTradeActual item = queryByTradePlannedIDAndTradeSideAndActualTime(data.getTradePlannedID(), data.getTradeSide(), data.getActualTime());
		if (item == null) {
			insert(data);
		} else {
			data.setTradePlannedID(item.getTradePlannedID());
			update(data);
		}
	}

	private void insert(StockTradeActual data) {
		try {
			if (data != null) {
				this.getSqlSessionTemplate().insert("StockTradeActualMapper.insert", data);
			}
		} catch (Throwable ex) {
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LOGGER.error(String.format(LogInfoUtils.HAS_DATA_TMPL, methodName, CustomJSONUtils.toJSONString(data)), ex);
		}
	}

	private void update(StockTradeActual data) {
		try {
			if (data != null) {
				this.getSqlSessionTemplate().update("StockTradeActualMapper.update", data);
			}
		} catch (Throwable ex) {
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LOGGER.error(String.format(LogInfoUtils.HAS_DATA_TMPL, methodName, CustomJSONUtils.toJSONString(data)), ex);
		}
	}
}