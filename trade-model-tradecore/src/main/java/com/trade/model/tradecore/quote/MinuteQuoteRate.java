package com.trade.model.tradecore.quote;

import com.trade.model.tradecore.enums.OptionTypeEnum;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 股票交易振幅数据
 */
public class MinuteQuoteRate implements Serializable {
	private static final long serialVersionUID = -8746492063509205196L;

	/**
	 * 私有构造函数，防止代码中用 new 的方式创建对象（请统一使用当前类的 createDataModel 方法创建对象）
	 */
	protected MinuteQuoteRate() {
	}

	/**
	 * 创建数据对象（代码规范：如有新增的字段，请同时修改此方法的参数）
	 *
	 * @param stockID
	 * @param date
	 * @param startTime
	 * @param endTime
	 * @param optionType
	 * @param originalPrice
	 * @param currentPrice
	 * @param volume
	 * @param turnover
	 * @param changeRate
	 * @return
	 */
	public static MinuteQuoteRate createDataModel(long stockID,
	                                              LocalDate date,
	                                              LocalTime startTime,
	                                              LocalTime endTime,
	                                              OptionTypeEnum optionType,
	                                              float originalPrice,
	                                              float currentPrice,
	                                              long volume,
	                                              long turnover,
	                                              float changeRate) {
		MinuteQuoteRate result = new MinuteQuoteRate();
		result.setStockID(stockID);
		result.setDate(date);
		result.setStartTime(startTime);
		result.setEndTime(endTime);
		result.setOptionType(optionType);
		result.setOriginalPrice(originalPrice);
		result.setCurrentPrice(currentPrice);
		result.setVolume(volume);
		result.setTurnover(turnover);
		result.setChangeRate(changeRate);

		return result;
	}

	/** =============== field =============== */
	/**
	 * 股票ID
	 */
	private long stockID;

	/**
	 * 所属日期 (转换回 unix time：dateTime.plusHours(-12).toInstant(ZoneOffset.of("-4")).toEpochMilli())
	 */
	private LocalDate date;

	/**
	 * 具体时间（分钟级别）
	 */
	private LocalTime startTime;

	/**
	 * 具体时间（分钟级别）
	 */
	private LocalTime endTime;

	/**
	 * 此轮趋势是上涨还是下跌
	 */
	private OptionTypeEnum optionType;

	/**
	 * 初始价格
	 */
	private float originalPrice;

	/**
	 * 当前价格
	 */
	private float currentPrice;

	/**
	 * 成交量
	 */
	private long volume;

	/**
	 * 成交额
	 */
	private long turnover;

	/**
	 * 涨跌幅
	 */
	private float changeRate;

	/**
	 * =============== get/set ===============
	 */
	public long getStockID() {
		return stockID;
	}

	public void setStockID(long stockID) {
		this.stockID = stockID;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public LocalTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalTime startTime) {
		this.startTime = startTime;
	}

	public LocalTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalTime endTime) {
		this.endTime = endTime;
	}

	public OptionTypeEnum getOptionType() {
		return optionType;
	}

	public void setOptionType(OptionTypeEnum optionType) {
		this.optionType = optionType;
	}

	public float getOriginalPrice() {
		return originalPrice;
	}

	public void setOriginalPrice(float originalPrice) {
		this.originalPrice = originalPrice;
	}

	public float getCurrentPrice() {
		return currentPrice;
	}

	public void setCurrentPrice(float currentPrice) {
		this.currentPrice = currentPrice;
	}

	public long getVolume() {
		return volume;
	}

	public void setVolume(long volume) {
		this.volume = volume;
	}

	public long getTurnover() {
		return turnover;
	}

	public void setTurnover(long turnover) {
		this.turnover = turnover;
	}

	public float getChangeRate() {
		return changeRate;
	}

	public void setChangeRate(float changeRate) {
		this.changeRate = changeRate;
	}

	/**
	 * =============== toString() ===============
	 */
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
