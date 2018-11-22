package com.trade.biz.dal.tradecore.impl;

import com.google.common.collect.Maps;
import com.trade.biz.dal.base.TradeCoreBaseDao;
import com.trade.biz.dal.tradecore.DayKLineDao;
import com.trade.common.infrastructure.util.json.CustomJSONUtils;
import com.trade.common.infrastructure.util.logger.LogInfoUtils;
import com.trade.model.tradecore.kline.DayKLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class DayKLineDaoImpl extends TradeCoreBaseDao implements DayKLineDao {

	// 日志记录
	private static final Logger LOGGER = LoggerFactory.getLogger(DayKLineDaoImpl.class);

	@Override
	public List<DayKLine> queryListAll() {
		List<DayKLine> result = this.getSqlSessionTemplate().selectList("DayKLineMapper.queryListAll");
		result.sort(Comparator.comparing(DayKLine::getDate));

		return result;
	}

	@Override
	public List<DayKLine> queryListByStockID(int stockID) {
		List<DayKLine> result = this.getSqlSessionTemplate().selectList("DayKLineMapper.queryListByStockID", stockID);
		result.sort(Comparator.comparing(DayKLine::getDate));

		return result;
	}

	@Override
	public DayKLine queryByKLineID(long kLineID) {
		return this.getSqlSessionTemplate().selectOne("DayKLineMapper.queryByKLineID", kLineID);
	}

	@Override
	public void insertOrUpdate(DayKLine data) {
		DayKLine item = queryByStockIDAndDate(data.getStockID(), data.getDate());
		if (item == null) {
			insert(data);
		} else {
			data.setkLineID(item.getkLineID());
			update(data);
		}
	}

	private DayKLine queryByStockIDAndDate(long stockID, LocalDate date) {
		Map<String, Object> paramMap = Maps.newHashMap();
		paramMap.put("stockID", stockID);
		paramMap.put("date", date);

		return this.getSqlSessionTemplate().selectOne("DayKLineMapper.queryByStockIDAndDate", paramMap);
	}

	private void insert(DayKLine data) {
		try {
			if (data != null) {
				this.getSqlSessionTemplate().insert("DayKLineMapper.insert", data);
			}
		} catch (Throwable ex) {
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LOGGER.error(String.format(LogInfoUtils.HAS_DATA_TMPL, methodName, CustomJSONUtils.toJSONString(data)), ex);
		}
	}

	private void update(DayKLine data) {
		try {
			if (data != null) {
				this.getSqlSessionTemplate().update("DayKLineMapper.update", data);
			}
		} catch (Throwable ex) {
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LOGGER.error(String.format(LogInfoUtils.HAS_DATA_TMPL, methodName, CustomJSONUtils.toJSONString(data)), ex);
		}
	}

	@Override
	public void delete(long kLineID) {
		try {
			if (kLineID > 0) {
				this.getSqlSessionTemplate().update("DayKLineMapper.delete", kLineID);
			}
		} catch (Throwable ex) {
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			LOGGER.error(String.format(LogInfoUtils.HAS_DATA_TMPL, methodName, kLineID), ex);
		}
	}
}
