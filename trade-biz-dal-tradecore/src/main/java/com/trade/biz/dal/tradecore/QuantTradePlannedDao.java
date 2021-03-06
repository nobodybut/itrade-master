package com.trade.biz.dal.tradecore;

import com.trade.model.tradecore.quanttrade.QuantTradePlanned;

import java.time.LocalDate;
import java.util.List;

public interface QuantTradePlannedDao {

	/**
	 * 根据 tradeDate 读取数据列表
	 *
	 * @param tradeDate
	 * @return
	 */
	List<QuantTradePlanned> queryListByDate(LocalDate tradeDate);

	/**
	 * 根据 stockID 读取数据列表
	 *
	 * @param stockID
	 * @return
	 */
	List<QuantTradePlanned> queryListByStockID(long stockID);

	/**
	 * 根据 tradePlannedID 读取数据
	 *
	 * @param tradePlannedID
	 * @return
	 */
	QuantTradePlanned queryByTradePlannedID(int tradePlannedID);

	/**
	 * 根据 stockID、tradeDate 读取数据
	 *
	 * @param stockID
	 * @param tradeDate
	 * @return
	 */
	QuantTradePlanned queryByStockIDAndDate(long stockID, LocalDate tradeDate);

	/**
	 * 写入或更新 单个对象
	 *
	 * @param data
	 */
	void insertOrUpdate(QuantTradePlanned data);
}
