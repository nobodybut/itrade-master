package com.trade.biz.dal.tradecore.impl;

import com.google.common.collect.Maps;
import com.trade.biz.dal.base.TradeCoreBaseDao;
import com.trade.biz.dal.tradecore.QuantTradeActualDao;
import com.trade.common.infrastructure.util.json.CustomJSONUtils;
import com.trade.common.infrastructure.util.logger.LogInfoUtils;
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
	public List<QuantTradeActual> queryListByBuyDate(LocalDate actualBuyTradeDate) {
		Map<String, Object> paramMap = Maps.newHashMap();
		paramMap.put("actualBuyTradeDate", actualBuyTradeDate);
		return this.getSqlSessionTemplate().selectList("QuantTradeActualMapper.queryListByBuyDate", paramMap);
	}

	@Override
	public List<QuantTradeActual> queryListByStockIDAndBuyDate(long stockID, LocalDate actualBuyTradeDate) {
		Map<String, Object> paramMap = Maps.newHashMap();
		paramMap.put("stockID", stockID);
		paramMap.put("actualBuyTradeDate", actualBuyTradeDate);
		return this.getSqlSessionTemplate().selectList("QuantTradeActualMapper.queryListByStockIDAndBuyDate", paramMap);
	}

	@Override
	public List<QuantTradeActual> queryListNotSellTradeActual(long stockID) {
		return this.getSqlSessionTemplate().selectList("QuantTradeActualMapper.queryListNotSellTradeActual", stockID);
	}

	@Override
	public QuantTradeActual queryByBuyTradeActualParam(int tradePlannedID, float actualBuyPrice, int actualBuyVolume, LocalDate actualBuyTradeDate, LocalTime actualBuyTradeTime) {
		Map<String, Object> paramMap = Maps.newHashMap();
		paramMap.put("tradePlannedID", tradePlannedID);
		paramMap.put("actualBuyPrice", actualBuyPrice);
		paramMap.put("actualBuyVolume", actualBuyVolume);
		paramMap.put("actualBuyTradeDate", actualBuyTradeDate);
		paramMap.put("actualBuyTradeTime", actualBuyTradeTime);
		return this.getSqlSessionTemplate().selectOne("QuantTradeActualMapper.queryByBuyTradeActualParam", paramMap);
	}

	@Override
	public void insertOrUpdateBuyTradeActual(QuantTradeActual buyTradeActual) {
		QuantTradeActual item = queryByBuyTradeActualParam(buyTradeActual.getTradePlannedID(), buyTradeActual.getActualBuyPrice(), buyTradeActual.getActualBuyVolume(), buyTradeActual.getActualBuyTradeDate(), buyTradeActual.getActualBuyTradeTime());
		if (item == null) {
			insertBuyTradeActual(buyTradeActual);
		} else {
			buyTradeActual.setTradeActualID(item.getTradeActualID());
			updateBuyTradeActual(buyTradeActual);
		}
	}

	private void insertBuyTradeActual(QuantTradeActual buyTradeActual) {
		try {
			if (buyTradeActual != null) {
				this.getSqlSessionTemplate().insert("QuantTradeActualMapper.insertBuyTradeActual", buyTradeActual);
			}
		} catch (Throwable ex) {
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LOGGER.error(String.format(LogInfoUtils.HAS_DATA_TMPL, methodName, CustomJSONUtils.toJSONString(buyTradeActual)), ex);
		}
	}

	private void updateBuyTradeActual(QuantTradeActual buyTradeActual) {
		try {
			if (buyTradeActual != null) {
				this.getSqlSessionTemplate().update("QuantTradeActualMapper.updateBuyTradeActual", buyTradeActual);
			}
		} catch (Throwable ex) {
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LOGGER.error(String.format(LogInfoUtils.HAS_DATA_TMPL, methodName, CustomJSONUtils.toJSONString(buyTradeActual)), ex);
		}
	}

	@Override
	public void updateSellTradeActual(QuantTradeActual sellTradeActual) {
		try {
			if (sellTradeActual != null) {
				this.getSqlSessionTemplate().update("QuantTradeActualMapper.updateSellTradeActual", sellTradeActual);
			}
		} catch (Throwable ex) {
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LOGGER.error(String.format(LogInfoUtils.HAS_DATA_TMPL, methodName, CustomJSONUtils.toJSONString(sellTradeActual)), ex);
		}
	}
}
