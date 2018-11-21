package com.trade.biz.dal.tradecore.impl;

import com.google.common.collect.Maps;
import com.trade.biz.dal.base.TradeCoreBaseDao;
import com.trade.biz.dal.tradecore.StockTradePlannedDao;
import com.trade.common.infrastructure.util.json.CustomJSONUtils;
import com.trade.common.infrastructure.util.logger.LogInfoUtils;
import com.trade.model.tradecore.stocktrade.StockTradePlanned;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class StockTradePlannedDaoImpl extends TradeCoreBaseDao implements StockTradePlannedDao {

	// 日志记录
	private static final Logger LOGGER = LoggerFactory.getLogger(StockTradePlannedDaoImpl.class);

	@Override
	public List<StockTradePlanned> queryListByDate(LocalDate plannedTradeDate) {
		Map<String, Object> paramMap = Maps.newHashMap();
		paramMap.put("plannedTradeDate", plannedTradeDate);
		List<StockTradePlanned> result = this.getSqlSessionTemplate().selectList("StockTradePlannedMapper.queryListByDate", paramMap);
		result.sort(Comparator.comparing(StockTradePlanned::getPlannedScore, Comparator.reverseOrder()));

		return result;
	}

	@Override
	public List<StockTradePlanned> queryListByStockID(long stockID) {
		List<StockTradePlanned> result = this.getSqlSessionTemplate().selectList("StockTradePlannedMapper.queryListByStockID", stockID);
		result.sort(Comparator.comparing(StockTradePlanned::getPlannedScore, Comparator.reverseOrder()));

		return result;
	}

	@Override
	public StockTradePlanned queryByStockIDAndDate(long stockID, LocalDate plannedTradeDate) {
		Map<String, Object> paramMap = Maps.newHashMap();
		paramMap.put("stockID", stockID);
		paramMap.put("plannedTradeDate", plannedTradeDate);
		return this.getSqlSessionTemplate().selectOne("StockTradePlannedMapper.queryByStockIDAndDate", paramMap);
	}

	@Override
	public void insertOrUpdate(StockTradePlanned data) {
		StockTradePlanned item = queryByStockIDAndDate(data.getStockID(), data.getPlannedTradeDate());
		if (item == null) {
			insert(data);
		} else {
			data.setTradePlannedID(item.getTradePlannedID());
			update(data);
		}
	}

	private void insert(StockTradePlanned data) {
		try {
			if (data != null) {
				this.getSqlSessionTemplate().insert("StockTradePlannedMapper.insert", data);
			}
		} catch (Throwable ex) {
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LOGGER.error(String.format(LogInfoUtils.HAS_DATA_TMPL, methodName, CustomJSONUtils.toJSONString(data)), ex);
		}
	}

	private void update(StockTradePlanned data) {
		try {
			if (data != null) {
				this.getSqlSessionTemplate().update("StockTradePlannedMapper.update", data);
			}
		} catch (Throwable ex) {
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LOGGER.error(String.format(LogInfoUtils.HAS_DATA_TMPL, methodName, CustomJSONUtils.toJSONString(data)), ex);
		}
	}
}
