package com.trade.common.infrastructure.util.timeout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

public abstract class AbstractTimeoutMethod {

	// Logger
	protected static final Logger logger = LoggerFactory.getLogger(AbstractTimeoutMethod.class);

	// 线程池
	private static final ExecutorService s_executorPool = Executors.newCachedThreadPool();

	/**
	 * 执行操作，超时直接返回
	 *
	 * @param milliseconds
	 * @return
	 */
	protected String execute(int milliseconds) {
		String result = "";

		Callable<String> call = new Callable<String>() {
			public String call() throws Exception {
				return execute();
			}
		};

		try {
			Future<String> future = s_executorPool.submit(call);
			result = future.get(milliseconds, TimeUnit.MILLISECONDS);
		} catch (TimeoutException ex) {
			result = "";
		} catch (Exception e) {
			result = "";
		}

		return result;
	}

	/**
	 * 具体执行操作过程
	 */
	protected abstract String execute();
}
