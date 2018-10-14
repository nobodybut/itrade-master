package com.itrade.common.infrastructure.redis.clients;

import com.itrade.common.infrastructure.redis.jedisclient.JedisClient;
import org.springframework.stereotype.Component;

@Component
public class RedisClient extends JedisClient {

	private RedisClient() {
	}

	@Override
	protected Integer getProxyType() {
		return 1;
	}

	@Override
	protected boolean isMasterServer() {
		return true;
	}
}
