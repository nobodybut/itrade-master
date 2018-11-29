package com.trade.biz.dal.tradecore;

import com.trade.model.tradecore.quanttrade.QuantTradeActual;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface QuantTradeActualDao {

	/**
	 * 根据 actualBuyTradeDate 读取所有交易数据列表
	 *
	 * @param actualBuyTradeDate
	 * @return
	 */
	List<QuantTradeActual> queryListByBuyDate(LocalDate actualBuyTradeDate);

	/**
	 * 根据 stockID、actualBuyTradeDate 读取所有交易数据列表
	 *
	 * @param stockID
	 * @param actualBuyTradeDate
	 * @return
	 */
	List<QuantTradeActual> queryListByStockIDAndBuyDate(long stockID, LocalDate actualBuyTradeDate);

	/**
	 * 根据 stockID 读取未成功交易的数据列表
	 *
	 * @param stockID
	 * @return
	 */
	List<QuantTradeActual> queryListNotSellTradeActual(long stockID);

	/**
	 * 根据 买入/卖空 参数读取数据
	 *
	 * @param tradePlannedID
	 * @param actualBuyPrice
	 * @param actualBuyVolume
	 * @param actualBuyTradeDate
	 * @param actualBuyTradeTime
	 * @return
	 */
	QuantTradeActual queryByBuyTradeActualParam(int tradePlannedID, float actualBuyPrice, int actualBuyVolume, LocalDate actualBuyTradeDate, LocalTime actualBuyTradeTime);

	/**
	 * 写入或更新 买入/卖空 单个对象
	 *
	 * @param buyTradeActual
	 */
	void insertOrUpdateBuyTradeActual(QuantTradeActual buyTradeActual);

	/**
	 * 更新 卖出/赎回 单个对象
	 *
	 * @param sellTradeActual
	 */
	void updateSellTradeActual(QuantTradeActual sellTradeActual);
}
