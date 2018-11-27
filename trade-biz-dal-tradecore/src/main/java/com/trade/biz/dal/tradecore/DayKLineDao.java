package com.trade.biz.dal.tradecore;


import com.trade.model.tradecore.kline.DayKLine;

import java.time.LocalDate;
import java.util.List;

public interface DayKLineDao {

	/**
	 * 读取全部数据列表
	 *
	 * @return
	 */
	List<DayKLine> queryListAll();

	/**
	 * 根据 stockID 读取数据列表
	 *
	 * @param stockID
	 * @return
	 */
	List<DayKLine> queryListByStockID(long stockID);

	/**
	 * 根据 stockID、dates 读取数据列表
	 *
	 * @param stockID
	 * @param dates
	 * @return
	 */
	List<DayKLine> queryListByStockIDAndDates(long stockID, List<LocalDate> dates);

	/**
	 * 根据 stockID、date 读取数据
	 *
	 * @param stockID
	 * @param date
	 * @return
	 */
	DayKLine queryByStockIDAndDate(long stockID, LocalDate date);

	/**
	 * 根据 kLineID 读取对象
	 *
	 * @param kLineID
	 * @return
	 */
	DayKLine queryByKLineID(long kLineID);

	/**
	 * 写入或更新 单个对象
	 *
	 * @param data
	 * @return
	 */
	void insertOrUpdate(DayKLine data);

	/**
	 * 删除 单个对象
	 *
	 * @param kLineID
	 */
	void delete(long kLineID);
}
