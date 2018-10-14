package com.itrade.common.infrastructure.util.dao;

import com.alibaba.fastjson.annotation.JSONField;
import com.itrade.common.infrastructure.util.enums.CRUDStatusEnum;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;

public class CRUDStatus implements Serializable {

	/** =============== field =============== */
	/**
	 * statusEnum
	 */
	private CRUDStatusEnum statusEnum;

	/**
	 * generatedID
	 */
	private int generatedID;

	/**
	 * =============== get/set ===============
	 */
	@JSONField(name = "1")
	public CRUDStatusEnum getStatusEnum() {
		return statusEnum;
	}

	@JSONField(name = "1")
	public void setStatusEnum(CRUDStatusEnum statusEnum) {
		this.statusEnum = statusEnum;
	}

	@JSONField(name = "2")
	public int getGeneratedID() {
		return generatedID;
	}

	@JSONField(name = "2")
	public void setGeneratedID(int generatedID) {
		this.generatedID = generatedID;
	}

	/**
	 * =============== toString() ===============
	 */
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
