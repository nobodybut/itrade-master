package com.trade.model.tradecore.enums;

public enum StockPlateEnum {

	/**
	 * 环球指数
	 */
	GLOBAL(200201),

	/**
	 * 纽交所
	 */
	NYSE(200301),

	/**
	 * 纳斯达克
	 */
	NASDAQ(200302),

	/**
	 * 美交所
	 */
	ASE(200303),

	/**
	 * 中概股
	 */
	CHINA(200304),

	/**
	 * 明星股
	 */
	STAR(200305);

	private int plateID;

	StockPlateEnum(int plateID) {
		this.plateID = plateID;
	}

	public int getPlateID() {
		return plateID;
	}
}
