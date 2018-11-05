package com.trade.biz.dal.tradecore;

import com.trade.model.tradecore.stocktrade.StockTradePlanned;

import java.time.LocalDate;
import java.util.List;

public interface StockTradePlannedDao {

	/**
	 * 根据 date 读取数据列表
	 *
	 * @param date
	 * @return
	 */
	List<StockTradePlanned> queryListByDate(LocalDate date);

	/**
	 * 根据 stockID 读取数据列表
	 *
	 * @param stockID
	 * @return
	 */
	List<StockTradePlanned> queryListByStockID(long stockID);

	/**
	 * 根据 stockID、date 读取数据
	 *
	 * @param stockID
	 * @param date
	 * @return
	 */
	StockTradePlanned queryByStockIDAndDate(long stockID, LocalDate date);

	/**
	 * 写入或更新 单个对象
	 *
	 * @param data
	 */
	void insertOrUpdate(StockTradePlanned data);
}
