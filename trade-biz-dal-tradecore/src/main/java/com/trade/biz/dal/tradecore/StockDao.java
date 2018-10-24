package com.trade.biz.dal.tradecore;

import com.trade.model.tradecore.stock.Stock;

import java.util.List;

public interface StockDao {

	/**
	 * 根据 marketID 读取数据列表
	 *
	 * @param marketID
	 * @return
	 */
	List<Stock> queryListByMarketID(int marketID);

	/**
	 * 根据 marketID + exchangeID 读取数据列表
	 *
	 * @param marketID
	 * @param exchangeID
	 * @return
	 */
	List<Stock> queryListByMarketIDAndExchangeID(int marketID, int exchangeID);

	/**
	 * 根据 plateID 读取数据列表
	 *
	 * @param plateID
	 * @return
	 */
	List<Stock> queryListByPlateID(int plateID);

	/**
	 * 根据 stockID 读取对象
	 *
	 * @param stockID
	 * @return
	 */
	Stock queryByStockID(long stockID);

	/**
	 * 写入或更新 单个对象
	 *
	 * @param data
	 * @return
	 */
	void insertOrUpdate(Stock data);

	/**
	 * 标识删除 单个对象
	 *
	 * @param stockID
	 */
	void updateIsDelete(long stockID);
}
