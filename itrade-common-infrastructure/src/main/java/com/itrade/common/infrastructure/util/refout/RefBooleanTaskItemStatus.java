package com.itrade.common.infrastructure.util.refout;

public class RefBooleanTaskItemStatus {

	/**
	 * exception
	 */
	public boolean exception;

	/**
	 * accessBlock
	 */
	public boolean accessBlock;

	/**
	 * noData
	 */
	public boolean noData;

	public boolean isException() {
		return exception;
	}

	public void setException(boolean exception) {
		this.exception = exception;
	}

	public boolean isAccessBlock() {
		return accessBlock;
	}

	public void setAccessBlock(boolean accessBlock) {
		this.accessBlock = accessBlock;
	}

	public boolean isNoData() {
		return noData;
	}

	public void setNoData(boolean noData) {
		this.noData = noData;
	}
}
