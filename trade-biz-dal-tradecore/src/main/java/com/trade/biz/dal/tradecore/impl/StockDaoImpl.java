package com.trade.biz.dal.tradecore.impl;

import com.google.common.collect.Maps;
import com.trade.biz.dal.base.TradeCoreBaseDao;
import com.trade.biz.dal.tradecore.StockDao;
import com.trade.common.infrastructure.util.json.CustomJSONUtils;
import com.trade.common.infrastructure.util.logger.LogInfoUtils;
import com.trade.model.tradecore.stock.Stock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class StockDaoImpl extends TradeCoreBaseDao implements StockDao {

	// 日志记录
	private static final Logger LOGGER = LoggerFactory.getLogger(StockDaoImpl.class);

	@Override
	public List<Stock> queryListByMarketID(int marketID) {
		return this.getSqlSessionTemplate().selectList("StockMapper.queryListByMarketID", marketID);
	}

	@Override
	public List<Stock> queryListByMarketIDAndExchangeID(int marketID, int exchangeID) {
		Map<String, Object> paramMap = Maps.newHashMap();
		paramMap.put("marketID", marketID);
		paramMap.put("exchangeID", exchangeID);
		return this.getSqlSessionTemplate().selectList("StockMapper.queryListByMarketIDAndExchangeID", paramMap);
	}

	@Override
	public List<Stock> queryListByPlateID(int plateID) {
		return this.getSqlSessionTemplate().selectList("StockMapper.queryListByPlateID", plateID);
	}

	@Override
	public Stock queryByStockID(long stockID) {
		return this.getSqlSessionTemplate().selectOne("StockMapper.queryByStockID", stockID);
	}

	@Override
	public void insertOrUpdate(Stock data) {
		Stock item = queryByStockID(data.getStockID());
		if (item == null) {
			insert(data);
		} else {
			data.setStockID(item.getStockID());
			update(data);
		}
	}

	private void insert(Stock data) {
		try {
			if (data != null) {
				this.getSqlSessionTemplate().insert("StockMapper.insert", data);
			}
		} catch (Throwable ex) {
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LOGGER.error(String.format(LogInfoUtils.HAS_DATA_TMPL, methodName, CustomJSONUtils.toJSONString(data)), ex);
		}
	}

	public void update(Stock data) {
		try {
			if (data != null) {
				this.getSqlSessionTemplate().update("StockMapper.update", data);
			}
		} catch (Throwable ex) {
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LOGGER.error(String.format(LogInfoUtils.HAS_DATA_TMPL, methodName, CustomJSONUtils.toJSONString(data)), ex);
		}
	}

	@Override
	public void updateIsDelete(long stockID) {
		try {
			Map<String, Object> paramMap = Maps.newHashMap();
			paramMap.put("stockID", stockID);
			paramMap.put("isDelete", 1);
			this.getSqlSessionTemplate().delete("StockMapper.updateIsDelete", paramMap);
		} catch (Exception ex) {
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LOGGER.error(String.format(LogInfoUtils.HAS_DATA_TMPL, methodName, stockID), ex);
		}
	}
}
