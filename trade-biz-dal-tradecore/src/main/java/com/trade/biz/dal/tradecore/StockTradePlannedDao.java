package com.trade.biz.dal.tradecore;

import com.trade.model.tradecore.stocktrade.StockTradePlanned;

import java.time.LocalDate;
import java.util.List;

public interface StockTradePlannedDao {

	/**
	 * 根据 plannedTradeDate 读取数据列表
	 *
	 * @param plannedTradeDate
	 * @return
	 */
	List<StockTradePlanned> queryListByDate(LocalDate plannedTradeDate);

	/**
	 * 根据 stockID 读取数据列表
	 *
	 * @param stockID
	 * @return
	 */
	List<StockTradePlanned> queryListByStockID(long stockID);

	/**
	 * 根据 stockID、plannedTradeDate 读取数据
	 *
	 * @param stockID
	 * @param plannedTradeDate
	 * @return
	 */
	StockTradePlanned queryByStockIDAndDate(long stockID, LocalDate plannedTradeDate);

	/**
	 * 写入或更新 单个对象
	 *
	 * @param data
	 */
	void insertOrUpdate(StockTradePlanned data);
}
