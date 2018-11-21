package com.trade.biz.dal.tradecore;

import com.trade.model.tradecore.enums.TradeSideEnum;
import com.trade.model.tradecore.quanttrade.QuantTradeActual;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface QuantTradeActualDao {

	/**
	 * 根据 date 读取数据列表
	 *
	 * @param date
	 * @return
	 */
	List<QuantTradeActual> queryListByDate(LocalDate date);

	/**
	 * 根据 stockID、date 读取数据列表
	 *
	 * @param stockID
	 * @param date
	 * @return
	 */
	List<QuantTradeActual> queryListByStockIDAndDate(long stockID, LocalDate date);

	/**
	 * 根据 tradePlannedID、tradeSide、actualTradeTime 读取数据
	 *
	 * @param tradePlannedID
	 * @param tradeSide
	 * @param actualTradeTime
	 * @return
	 */
	QuantTradeActual queryByTradePlannedIDAndTradeSideAndActualTradeTime(int tradePlannedID, TradeSideEnum tradeSide, LocalTime actualTradeTime);

	/**
	 * 写入或更新 单个对象
	 *
	 * @param data
	 */
	void insertOrUpdate(QuantTradeActual data);
}
