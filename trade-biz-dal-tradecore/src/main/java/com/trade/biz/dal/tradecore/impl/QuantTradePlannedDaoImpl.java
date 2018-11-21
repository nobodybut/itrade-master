package com.trade.biz.dal.tradecore.impl;

import com.google.common.collect.Maps;
import com.trade.biz.dal.base.TradeCoreBaseDao;
import com.trade.biz.dal.tradecore.QuantTradePlannedDao;
import com.trade.common.infrastructure.util.json.CustomJSONUtils;
import com.trade.common.infrastructure.util.logger.LogInfoUtils;
import com.trade.model.tradecore.quanttrade.QuantTradePlanned;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class QuantTradePlannedDaoImpl extends TradeCoreBaseDao implements QuantTradePlannedDao {

	// 日志记录
	private static final Logger LOGGER = LoggerFactory.getLogger(QuantTradePlannedDaoImpl.class);

	@Override
	public List<QuantTradePlanned> queryListByDate(LocalDate plannedTradeDate) {
		Map<String, Object> paramMap = Maps.newHashMap();
		paramMap.put("plannedTradeDate", plannedTradeDate);
		List<QuantTradePlanned> result = this.getSqlSessionTemplate().selectList("QuantTradePlannedMapper.queryListByDate", paramMap);
		result.sort(Comparator.comparing(QuantTradePlanned::getPlannedScore, Comparator.reverseOrder()));

		return result;
	}

	@Override
	public List<QuantTradePlanned> queryListByStockID(long stockID) {
		List<QuantTradePlanned> result = this.getSqlSessionTemplate().selectList("QuantTradePlannedMapper.queryListByStockID", stockID);
		result.sort(Comparator.comparing(QuantTradePlanned::getPlannedScore, Comparator.reverseOrder()));

		return result;
	}

	@Override
	public QuantTradePlanned queryByStockIDAndDate(long stockID, LocalDate plannedTradeDate) {
		Map<String, Object> paramMap = Maps.newHashMap();
		paramMap.put("stockID", stockID);
		paramMap.put("plannedTradeDate", plannedTradeDate);
		return this.getSqlSessionTemplate().selectOne("QuantTradePlannedMapper.queryByStockIDAndDate", paramMap);
	}

	@Override
	public void insertOrUpdate(QuantTradePlanned data) {
		QuantTradePlanned item = queryByStockIDAndDate(data.getStockID(), data.getPlannedTradeDate());
		if (item == null) {
			insert(data);
		} else {
			data.setTradePlannedID(item.getTradePlannedID());
			update(data);
		}
	}

	private void insert(QuantTradePlanned data) {
		try {
			if (data != null) {
				this.getSqlSessionTemplate().insert("QuantTradePlannedMapper.insert", data);
			}
		} catch (Throwable ex) {
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LOGGER.error(String.format(LogInfoUtils.HAS_DATA_TMPL, methodName, CustomJSONUtils.toJSONString(data)), ex);
		}
	}

	private void update(QuantTradePlanned data) {
		try {
			if (data != null) {
				this.getSqlSessionTemplate().update("QuantTradePlannedMapper.update", data);
			}
		} catch (Throwable ex) {
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LOGGER.error(String.format(LogInfoUtils.HAS_DATA_TMPL, methodName, CustomJSONUtils.toJSONString(data)), ex);
		}
	}
}
