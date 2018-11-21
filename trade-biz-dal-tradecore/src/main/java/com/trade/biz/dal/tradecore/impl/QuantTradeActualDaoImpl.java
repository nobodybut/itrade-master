package com.trade.biz.dal.tradecore.impl;

import com.google.common.collect.Maps;
import com.trade.biz.dal.base.TradeCoreBaseDao;
import com.trade.biz.dal.tradecore.QuantTradeActualDao;
import com.trade.common.infrastructure.util.json.CustomJSONUtils;
import com.trade.common.infrastructure.util.logger.LogInfoUtils;
import com.trade.model.tradecore.enums.TradeSideEnum;
import com.trade.model.tradecore.quanttrade.QuantTradeActual;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public class QuantTradeActualDaoImpl extends TradeCoreBaseDao implements QuantTradeActualDao {

	// 日志记录
	private static final Logger LOGGER = LoggerFactory.getLogger(QuantTradeActualDaoImpl.class);

	@Override
	public List<QuantTradeActual> queryListByDate(LocalDate actualTradeDate) {
		Map<String, Object> paramMap = Maps.newHashMap();
		paramMap.put("actualTradeDate", actualTradeDate);
		return this.getSqlSessionTemplate().selectList("QuantTradeActualMapper.queryListByDate", paramMap);
	}

	@Override
	public List<QuantTradeActual> queryListByStockIDAndDate(long stockID, LocalDate actualTradeDate) {
		Map<String, Object> paramMap = Maps.newHashMap();
		paramMap.put("stockID", stockID);
		paramMap.put("actualTradeDate", actualTradeDate);
		return this.getSqlSessionTemplate().selectList("QuantTradeActualMapper.queryListByStockIDAndDate", paramMap);
	}

	@Override
	public QuantTradeActual queryByTradePlannedIDAndTradeSideAndActualTradeTime(int tradePlannedID, TradeSideEnum tradeSide, LocalTime actualTradeTime) {
		Map<String, Object> paramMap = Maps.newHashMap();
		paramMap.put("tradePlannedID", tradePlannedID);
		paramMap.put("tradeSide", tradeSide.ordinal());
		paramMap.put("actualTradeTime", actualTradeTime);
		return this.getSqlSessionTemplate().selectOne("QuantTradeActualMapper.queryByTradePlannedIDAndTradeSideAndActualTradeTime", paramMap);
	}

	@Override
	public void insertOrUpdate(QuantTradeActual data) {
		QuantTradeActual item = queryByTradePlannedIDAndTradeSideAndActualTradeTime(data.getTradePlannedID(), data.getTradeSide(), data.getActualTradeTime());
		if (item == null) {
			insert(data);
		} else {
			data.setTradePlannedID(item.getTradePlannedID());
			update(data);
		}
	}

	private void insert(QuantTradeActual data) {
		try {
			if (data != null) {
				this.getSqlSessionTemplate().insert("QuantTradeActualMapper.insert", data);
			}
		} catch (Throwable ex) {
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LOGGER.error(String.format(LogInfoUtils.HAS_DATA_TMPL, methodName, CustomJSONUtils.toJSONString(data)), ex);
		}
	}

	private void update(QuantTradeActual data) {
		try {
			if (data != null) {
				this.getSqlSessionTemplate().update("QuantTradeActualMapper.update", data);
			}
		} catch (Throwable ex) {
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LOGGER.error(String.format(LogInfoUtils.HAS_DATA_TMPL, methodName, CustomJSONUtils.toJSONString(data)), ex);
		}
	}
}
