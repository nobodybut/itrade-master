package com.trade.common.infrastructure.redis.jedisclient;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;

public class RedisServerConfig implements Serializable {

	/** =============== field =============== */
	/**
	 * id
	 */
	private int id;

	/**
	 * name
	 */
	private String name = "";

	/**
	 * maxTotal
	 */
	private int maxTotal;

	/**
	 * maxIdle
	 */
	private int maxIdle;

	/**
	 * maxWaitMillis
	 */
	private int maxWaitMillis;

	/**
	 * serverList
	 */
	private String serverList = "";

	/**
	 * isUsed
	 */
	private boolean isUsed;

	/** =============== get/set =============== */
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getMaxTotal() {
		return maxTotal;
	}

	public void setMaxTotal(int maxTotal) {
		this.maxTotal = maxTotal;
	}

	public int getMaxIdle() {
		return maxIdle;
	}

	public void setMaxIdle(int maxIdle) {
		this.maxIdle = maxIdle;
	}

	public int getMaxWaitMillis() {
		return maxWaitMillis;
	}

	public void setMaxWaitMillis(int maxWaitMillis) {
		this.maxWaitMillis = maxWaitMillis;
	}

	public String getServerList() {
		return serverList;
	}

	public void setServerList(String serverList) {
		this.serverList = serverList;
	}

	public boolean isUsed() {
		return isUsed;
	}

	public void setUsed(boolean isUsed) {
		this.isUsed = isUsed;
	}

	/** =============== toString() =============== */
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
