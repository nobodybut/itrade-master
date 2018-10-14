package com.trade.common.infrastructure.util.pppoe;

import com.alibaba.fastjson.annotation.JSONField;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;

/**
 * Created by cc on 10/28/16.
 */
public class PPPOEStatus implements Serializable {

	/**
	 * 链接状态（1：通，0：断，－1：未知状态）
	 */
	private int linkStatus;

	private String IP;

	private String statusMessage;

	/**
	 * =============== get/set ===============
	 */
	@JSONField(name = "1")
	public int getLinkStatus() {
		return linkStatus;
	}

	@JSONField(name = "1")
	public void setLinkStatus(int linkStatus) {
		this.linkStatus = linkStatus;
	}

	@JSONField(name = "2")
	public String getIP() {
		return IP;
	}

	@JSONField(name = "2")
	public void setIP(String IP) {
		this.IP = IP;
	}

	@JSONField(name = "3")
	public String getStatusMessage() {
		return statusMessage;
	}

	@JSONField(name = "3")
	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}

	/**
	 * =============== toString() ===============
	 */
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
