package com.trade.common.infrastructure.util.phantomjs;

import com.gargoylesoftware.htmlunit.WebClient;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

public class WebClientFactory extends BasePooledObjectFactory<WebClient> {

	@Override
	public WebClient create() {
		return new WebClient();
	}

	/**
	 * Use the default PooledObject implementation.
	 */
	@Override
	public PooledObject<WebClient> wrap(WebClient webClient) {
		return new DefaultPooledObject<>(webClient);
	}

	/**
	 * When an object is returned to the pool, clear the webClient.
	 */
	@Override
	public void passivateObject(PooledObject<WebClient> pooledObject) {
		pooledObject.getObject().close();
	}
}