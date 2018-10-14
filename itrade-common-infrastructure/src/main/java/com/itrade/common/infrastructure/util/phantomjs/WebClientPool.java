package com.itrade.common.infrastructure.util.phantomjs;

import com.gargoylesoftware.htmlunit.WebClient;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.AbandonedConfig;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public class WebClientPool extends GenericObjectPool<WebClient> {

	public WebClientPool(PooledObjectFactory<WebClient> factory) {
		super(factory);
	}

	public WebClientPool(PooledObjectFactory<WebClient> factory, GenericObjectPoolConfig config) {
		super(factory, config);
	}

	public WebClientPool(PooledObjectFactory<WebClient> factory, GenericObjectPoolConfig config, AbandonedConfig abandonedConfig) {
		super(factory, config, abandonedConfig);
	}
}