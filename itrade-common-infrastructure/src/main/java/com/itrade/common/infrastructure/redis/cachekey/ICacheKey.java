package com.itrade.common.infrastructure.redis.cachekey;

public interface ICacheKey {
	
	/**
	 * Cache Key 字符串
	 * 
	 * @return
	 */
	public String toString();

	/**
	 * 绝对过期时间（秒），-1 代表永不过期
	 * 
	 * @return
	 */
	public int getExpirationTime();
}
