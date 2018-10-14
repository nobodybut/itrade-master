package com.trade.common.infrastructure.util.httpclient;

import com.google.common.collect.Lists;
import com.trade.common.infrastructure.util.collection.CustomListUtils;
import com.trade.common.infrastructure.util.logger.LogInfoUtils;
import com.trade.common.infrastructure.util.math.CustomMathUtils;
import com.trade.common.infrastructure.util.paging.PagingUtils;
import org.slf4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class MultiHttpClientUtils {

	/**
	 * 同时处理多个 Http 请求，并返回 String 结果（同样的 PostUrl, 不同的 PostData）
	 *
	 * @param httpClientInstance
	 * @param postUrl
	 * @param postDataList
	 * @param encoding
	 * @param timeOut
	 * @param proxyServers
	 * @param executorPool
	 * @param logger
	 * @return
	 */
	public static String getResultStringByPostDataList(final HttpClientInstance httpClientInstance, final String postUrl, final List<String> postDataList, final String encoding, final int timeOut,
	                                                   final List<String> proxyServers, final ExecutorService executorPool, final Logger logger) {
		return CustomListUtils.listToString(getResultListByPostDataList(httpClientInstance, postUrl, postDataList, encoding, timeOut, proxyServers, "", executorPool, logger, null, 0, true));
	}

	/**
	 * 同时处理多个 Http 请求，并返回 String 结果（同样的 PostUrl, 不同的 PostData）
	 *
	 * @param httpClientInstance
	 * @param postUrl
	 * @param postDataList
	 * @param encoding
	 * @param timeOut
	 * @param proxyServers
	 * @param executorPool
	 * @param logger
	 * @param pageSize
	 * @return
	 */
	public static String getResultStringByPostDataList(final HttpClientInstance httpClientInstance, final String postUrl, final List<String> postDataList, final String encoding, final int timeOut,
	                                                   final List<String> proxyServers, final ExecutorService executorPool, final Logger logger, final int pageSize) {
		return CustomListUtils.listToString(getResultListByPostDataList(httpClientInstance, postUrl, postDataList, encoding, timeOut, proxyServers, "", executorPool, logger, null, pageSize, true));
	}

	/**
	 * 同时处理多个 Http 请求，并返回 List 结果（同样的 PostUrl, 不同的 PostData）
	 *
	 * @param httpClientInstance
	 * @param postUrl
	 * @param postDataList
	 * @param encoding
	 * @param timeOut
	 * @param proxyServers
	 * @param executorPool
	 * @param logger
	 * @return
	 */
	public static List<String> getResultListByPostDataList(final HttpClientInstance httpClientInstance, final String postUrl, final List<String> postDataList, final String encoding,
	                                                       final int timeOut, final List<String> proxyServers, final ExecutorService executorPool, final Logger logger) {
		return getResultListByPostDataList(httpClientInstance, postUrl, postDataList, encoding, timeOut, proxyServers, "", executorPool, logger, null, 0, true);
	}

	/**
	 * 同时处理多个 Http 请求，并返回 List 结果（同样的 PostUrl, 不同的 PostData）
	 *
	 * @param httpClientInstance
	 * @param postUrl
	 * @param postDataList
	 * @param encoding
	 * @param timeOut
	 * @param proxyServers
	 * @param executorPool
	 * @param logger
	 * @param pageSize
	 * @return
	 */
	public static List<String> getResultListByPostDataList(final HttpClientInstance httpClientInstance, final String postUrl, final List<String> postDataList, final String encoding,
	                                                       final int timeOut, final List<String> proxyServers, final ExecutorService executorPool, final Logger logger, final int pageSize) {
		return getResultListByPostDataList(httpClientInstance, postUrl, postDataList, encoding, timeOut, proxyServers, "", executorPool, logger, null, pageSize, true);
	}

	/**
	 * 同时处理多个 Http 请求，并返回 String 结果（同样的 PostUrl, 不同的 PostData）
	 *
	 * @param httpClientInstance
	 * @param postUrl
	 * @param postDataList
	 * @param encoding
	 * @param timeOut
	 * @param proxyServers
	 * @param refererUrl
	 * @param executorPool
	 * @param logger
	 * @return
	 */
	public static String getResultStringByPostDataList(final HttpClientInstance httpClientInstance, final String postUrl, final List<String> postDataList, final String encoding, final int timeOut,
	                                                   final List<String> proxyServers, final String refererUrl, final ExecutorService executorPool, final Logger logger) {
		return CustomListUtils.listToString(getResultListByPostDataList(httpClientInstance, postUrl, postDataList, encoding, timeOut, proxyServers, refererUrl, executorPool, logger, null, 0, true));
	}

	/**
	 * 同时处理多个 Http 请求，并返回 String 结果（同样的 PostUrl, 不同的 PostData）
	 *
	 * @param httpClientInstance
	 * @param postUrl
	 * @param postDataList
	 * @param encoding
	 * @param timeOut
	 * @param proxyServers
	 * @param refererUrl
	 * @param executorPool
	 * @param logger
	 * @param pageSize
	 * @return
	 */
	public static String getResultStringByPostDataList(final HttpClientInstance httpClientInstance, final String postUrl, final List<String> postDataList, final String encoding, final int timeOut,
	                                                   final List<String> proxyServers, final String refererUrl, final ExecutorService executorPool, final Logger logger, final int pageSize) {
		return CustomListUtils.listToString(getResultListByPostDataList(httpClientInstance, postUrl, postDataList, encoding, timeOut, proxyServers, refererUrl, executorPool, logger, null, pageSize, true));
	}

	/**
	 * 同时处理多个 Http 请求，并返回 List 结果（同样的 PostUrl, 不同的 PostData）
	 *
	 * @param httpClientInstance
	 * @param postUrl
	 * @param postDataList
	 * @param encoding
	 * @param timeOut
	 * @param proxyServers
	 * @param refererUrl
	 * @param executorPool
	 * @param logger
	 * @return
	 */
	public static List<String> getResultListByPostDataList(final HttpClientInstance httpClientInstance, final String postUrl, final List<String> postDataList, final String encoding,
	                                                       final int timeOut, final List<String> proxyServers, final String refererUrl, final ExecutorService executorPool, final Logger logger) {
		return getResultListByPostDataList(httpClientInstance, postUrl, postDataList, encoding, timeOut, proxyServers, refererUrl, executorPool, logger, null, 0, true);
	}

	/**
	 * 同时处理多个 Http 请求，并返回 List 结果（同样的 PostUrl, 不同的 PostData）
	 *
	 * @param httpClientInstance
	 * @param postUrl
	 * @param postDataList
	 * @param encoding
	 * @param timeOut
	 * @param proxyServers
	 * @param refererUrl
	 * @param executorPool
	 * @param logger
	 * @param pageSize
	 * @return
	 */
	public static List<String> getResultListByPostDataList(final HttpClientInstance httpClientInstance, final String postUrl, final List<String> postDataList, final String encoding,
	                                                       final int timeOut, final List<String> proxyServers, final String refererUrl, final ExecutorService executorPool, final Logger logger, final int pageSize) {
		return getResultListByPostDataList(httpClientInstance, postUrl, postDataList, encoding, timeOut, proxyServers, refererUrl, executorPool, logger, null, pageSize, true);
	}

	/**
	 * 同时处理多个 Http 请求，并返回 List 结果（同样的 PostUrl, 不同的 PostData）
	 *
	 * @param httpClientInstance
	 * @param postUrl
	 * @param postDataList
	 * @param encoding
	 * @param timeOut
	 * @param proxyServers
	 * @param refererUrl
	 * @param executorPool
	 * @param logger
	 * @param additionalHeaders
	 * @return
	 */
	public static List<String> getResultListByPostDataList(final HttpClientInstance httpClientInstance, final String postUrl, final List<String> postDataList, final String encoding,
	                                                       final int timeOut, final List<String> proxyServers, final String refererUrl, final ExecutorService executorPool, final Logger logger, final Map<String, String> additionalHeaders) {
		List<String> result = Lists.newArrayList();

		try {
			// 计算 Callable 线程列表
			List<Callable<String>> tasks = Lists.newArrayList();
			for (final String postData : postDataList) {
				tasks.add(new Callable<String>() {
					public String call() throws Exception {
						return new HttpClientTimeOutInstance(httpClientInstance, postUrl, postData, encoding, timeOut, getRandomProxyServer(proxyServers), refererUrl, additionalHeaders).getHTML();
					}
				});
			}

			// 执行 Callable 线程，取得结果
			List<Future<String>> futures = executorPool.invokeAll(tasks);
			for (Future<String> future : futures) {
				result.add(future.get());
			}
		} catch (Throwable ex) {
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			logger.error(String.format(LogInfoUtils.NO_DATA_TMPL, methodName), ex);
		}

		return result;
	}

	/**
	 * 同时处理多个 Http 请求，并返回 List 结果（同样的 PostUrl, 不同的 PostData）
	 *
	 * @param httpClientInstance
	 * @param postUrl
	 * @param postDataList
	 * @param encoding
	 * @param timeOut
	 * @param proxyServers
	 * @param refererUrl
	 * @param executorPool
	 * @param logger
	 * @param additionalHeaders
	 * @param pageSize
	 * @return
	 */
	public static List<String> getResultListByPostDataList(final HttpClientInstance httpClientInstance, final String postUrl, final List<String> postDataList, final String encoding,
	                                                       final int timeOut, final List<String> proxyServers, final String refererUrl, final ExecutorService executorPool, final Logger logger, final Map<String, String> additionalHeaders, final int pageSize, final boolean onlyHtml) {
		List<String> result = Lists.newArrayList();

		try {
			// 计算 Callable 线程列表
			List<Callable<String>> tasks = Lists.newArrayList();
			for (final String postData : postDataList) {
				tasks.add(new Callable<String>() {
					public String call() throws Exception {
						if (onlyHtml) {
							return new HttpClientTimeOutInstance(httpClientInstance, postUrl, postData, encoding, timeOut, getRandomProxyServer(proxyServers), refererUrl, additionalHeaders).getHTML();
						} else {
							return String.format("%s^^^%s^^^%s", postUrl, postData, new HttpClientTimeOutInstance(httpClientInstance, postUrl, postData, encoding, timeOut, getRandomProxyServer(proxyServers), refererUrl, additionalHeaders).getHTML());
						}
					}
				});
			}
			int pageSizeLocal;
			if (pageSize > 0) {
				pageSizeLocal = pageSize;
			} else {
				pageSizeLocal = (tasks.size() > 0) ? tasks.size() : 1;
			}

			List<List<Callable<String>>> taskPageList = PagingUtils.splitToPagingLists(tasks, pageSizeLocal);

			for (List<Callable<String>> taskPage : taskPageList) {
				// 执行 Callable 线程，取得结果
				List<Future<String>> futures = executorPool.invokeAll(taskPage);
				for (Future<String> future : futures) {
					result.add(future.get());
				}
			}

		} catch (Throwable ex) {
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			logger.error(String.format(LogInfoUtils.NO_DATA_TMPL, methodName), ex);
		}

		return result;
	}

	/**
	 * 同时处理多个 Http 请求，并返回 String 结果（不同的 PostUrl, 无 PostData）
	 *
	 * @param httpClientInstance
	 * @param postUrlList
	 * @param encoding
	 * @param timeOut
	 * @param proxyServers
	 * @param executorPool
	 * @param logger
	 * @return
	 */
	public static String getResultStringByPostUrlList(final HttpClientInstance httpClientInstance, final List<String> postUrlList, final String encoding, final int timeOut,
	                                                  final List<String> proxyServers, final ExecutorService executorPool, final Logger logger) {
		return CustomListUtils.listToString(getResultListByPostUrlList(httpClientInstance, postUrlList, encoding, timeOut, proxyServers, "", executorPool, logger, null, 0, true));
	}

	/**
	 * 同时处理多个 Http 请求，并返回 String 结果（不同的 PostUrl, 无 PostData）
	 *
	 * @param httpClientInstance
	 * @param postUrlList
	 * @param encoding
	 * @param timeOut
	 * @param proxyServers
	 * @param executorPool
	 * @param logger
	 * @param pageSize
	 * @return
	 */
	public static String getResultStringByPostUrlList(final HttpClientInstance httpClientInstance, final List<String> postUrlList, final String encoding, final int timeOut,
	                                                  final List<String> proxyServers, final ExecutorService executorPool, final Logger logger, final int pageSize) {
		return CustomListUtils.listToString(getResultListByPostUrlList(httpClientInstance, postUrlList, encoding, timeOut, proxyServers, "", executorPool, logger, null, pageSize, true));
	}

	/**
	 * 同时处理多个 Http 请求，并返回 List 结果（不同的 PostUrl, 无 PostData）
	 *
	 * @param httpClientInstance
	 * @param postUrlList
	 * @param encoding
	 * @param timeOut
	 * @param proxyServers
	 * @param executorPool
	 * @param logger
	 * @return
	 */
	public static List<String> getResultListByPostUrlList(final HttpClientInstance httpClientInstance, final List<String> postUrlList, final String encoding, final int timeOut,
	                                                      final List<String> proxyServers, final ExecutorService executorPool, final Logger logger) {
		return getResultListByPostUrlList(httpClientInstance, postUrlList, encoding, timeOut, proxyServers, "", executorPool, logger, null, 0, true);
	}

	/**
	 * 同时处理多个 Http 请求，并返回 List 结果（不同的 PostUrl, 无 PostData）
	 *
	 * @param httpClientInstance
	 * @param postUrlList
	 * @param encoding
	 * @param timeOut
	 * @param proxyServers
	 * @param executorPool
	 * @param logger
	 * @param pageSize
	 * @return
	 */
	public static List<String> getResultListByPostUrlList(final HttpClientInstance httpClientInstance, final List<String> postUrlList, final String encoding, final int timeOut,
	                                                      final List<String> proxyServers, final ExecutorService executorPool, final Logger logger, final int pageSize) {
		return getResultListByPostUrlList(httpClientInstance, postUrlList, encoding, timeOut, proxyServers, "", executorPool, logger, null, pageSize, true);
	}

	/**
	 * 同时处理多个 Http 请求，并返回 String 结果（不同的 PostUrl, 无 PostData）
	 *
	 * @param httpClientInstance
	 * @param postUrlList
	 * @param encoding
	 * @param timeOut
	 * @param proxyServers
	 * @param refererUrl
	 * @param executorPool
	 * @param logger
	 * @return
	 */
	public static String getResultStringByPostUrlList(final HttpClientInstance httpClientInstance, final List<String> postUrlList, final String encoding, final int timeOut,
	                                                  final List<String> proxyServers, final String refererUrl, final ExecutorService executorPool, final Logger logger) {
		return CustomListUtils.listToString(getResultListByPostUrlList(httpClientInstance, postUrlList, encoding, timeOut, proxyServers, refererUrl, executorPool, logger, null, 0, true));
	}

	/**
	 * 同时处理多个 Http 请求，并返回 String 结果（不同的 PostUrl, 无 PostData）
	 *
	 * @param httpClientInstance
	 * @param postUrlList
	 * @param encoding
	 * @param timeOut
	 * @param proxyServers
	 * @param refererUrl
	 * @param executorPool
	 * @param logger
	 * @param pageSize
	 * @return
	 */
	public static String getResultStringByPostUrlList(final HttpClientInstance httpClientInstance, final List<String> postUrlList, final String encoding, final int timeOut,
	                                                  final List<String> proxyServers, final String refererUrl, final ExecutorService executorPool, final Logger logger, final int pageSize) {
		return CustomListUtils.listToString(getResultListByPostUrlList(httpClientInstance, postUrlList, encoding, timeOut, proxyServers, refererUrl, executorPool, logger, null, pageSize, true));
	}

	/**
	 * 同时处理多个 Http 请求，并返回 List 结果（不同的 PostUrl, 无 PostData）
	 *
	 * @param httpClientInstance
	 * @param postUrlList
	 * @param encoding
	 * @param timeOut
	 * @param proxyServers
	 * @param refererUrl
	 * @param executorPool
	 * @param logger
	 * @return
	 */
	public static List<String> getResultListByPostUrlList(final HttpClientInstance httpClientInstance, final List<String> postUrlList, final String encoding, final int timeOut,
	                                                      final List<String> proxyServers, final String refererUrl, final ExecutorService executorPool, final Logger logger) {
		return getResultListByPostUrlList(httpClientInstance, postUrlList, encoding, timeOut, proxyServers, refererUrl, executorPool, logger, null, 0, true);
	}


	/**
	 * 同时处理多个 Http 请求，并返回 List 结果（不同的 PostUrl, 无 PostData）
	 *
	 * @param httpClientInstance
	 * @param postUrlList
	 * @param encoding
	 * @param timeOut
	 * @param proxyServers
	 * @param refererUrl
	 * @param executorPool
	 * @param logger
	 * @param pageSize
	 * @return
	 */
	public static List<String> getResultListByPostUrlList(final HttpClientInstance httpClientInstance, final List<String> postUrlList, final String encoding, final int timeOut,
	                                                      final List<String> proxyServers, final String refererUrl, final ExecutorService executorPool, final Logger logger, final int pageSize) {
		return getResultListByPostUrlList(httpClientInstance, postUrlList, encoding, timeOut, proxyServers, refererUrl, executorPool, logger, null, pageSize, true);
	}

	/**
	 * 同时处理多个 Http 请求，并返回 List 结果（不同的 PostUrl, 无 PostData）
	 *
	 * @param httpClientInstance
	 * @param postUrlList
	 * @param encoding
	 * @param timeOut
	 * @param proxyServers
	 * @param refererUrl
	 * @param executorPool
	 * @param logger
	 * @param additionalHeaders
	 * @return
	 */
	public static List<String> getResultListByPostUrlList(final HttpClientInstance httpClientInstance, final List<String> postUrlList, final String encoding, final int timeOut,
	                                                      final List<String> proxyServers, final String refererUrl, final ExecutorService executorPool, final Logger logger, final Map<String, String> additionalHeaders) {
		List<String> result = Lists.newArrayList();

		try {
			// 计算 Callable 线程列表
			List<Callable<String>> tasks = Lists.newArrayList();
			for (final String postUrl : postUrlList) {
				tasks.add(new Callable<String>() {
					public String call() throws Exception {
						return new HttpClientTimeOutInstance(httpClientInstance, postUrl, "", encoding, timeOut, getRandomProxyServer(proxyServers), refererUrl, additionalHeaders).getHTML();
					}
				});
			}

			// 执行 Callable 线程，取得结果
			List<Future<String>> futures = executorPool.invokeAll(tasks);
			for (Future<String> future : futures) {
				result.add(future.get());
			}
		} catch (Throwable ex) {
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			logger.error(String.format(LogInfoUtils.NO_DATA_TMPL, methodName), ex);
		}

		return result;
	}

	/**
	 * 同时处理多个 Http 请求(分页)，并返回 List 结果（不同的 PostUrl, 无 PostData）
	 *
	 * @param httpClientInstance
	 * @param postUrlList
	 * @param encoding
	 * @param timeOut
	 * @param proxyServers
	 * @param refererUrl
	 * @param executorPool
	 * @param logger
	 * @param additionalHeaders
	 * @param pageSize
	 * @return
	 */
	public static List<String> getResultListByPostUrlList(final HttpClientInstance httpClientInstance, final List<String> postUrlList, final String encoding, final int timeOut,
	                                                      final List<String> proxyServers, final String refererUrl, final ExecutorService executorPool, final Logger logger, final Map<String, String> additionalHeaders, final int pageSize, final boolean onlyHtml) {
		List<String> result = Lists.newArrayList();

		try {
			// 计算 Callable 线程列表
			List<Callable<String>> tasks = Lists.newArrayList();
			for (final String postUrl : postUrlList) {
				tasks.add(new Callable<String>() {
					public String call() throws Exception {
						if (onlyHtml) {
							return new HttpClientTimeOutInstance(httpClientInstance, postUrl, "", encoding, timeOut, getRandomProxyServer(proxyServers), refererUrl, additionalHeaders).getHTML();
						} else {
							return String.format("%s^^^%s^^^%s", postUrl, "", new HttpClientTimeOutInstance(httpClientInstance, postUrl, "", encoding, timeOut, getRandomProxyServer(proxyServers), refererUrl, additionalHeaders).getHTML());
						}
					}
				});
			}
			int pageSizeLocal;
			if (pageSize > 0) {
				pageSizeLocal = pageSize;
			} else {
				pageSizeLocal = (tasks.size() > 0) ? tasks.size() : 1;
			}

			List<List<Callable<String>>> taskPageList = PagingUtils.splitToPagingLists(tasks, pageSizeLocal);

			for (List<Callable<String>> taskPage : taskPageList) {
				// 执行 Callable 线程，取得结果
				List<Future<String>> futures = executorPool.invokeAll(taskPage);
				for (Future<String> future : futures) {
					result.add(future.get());
				}
			}
		} catch (Throwable ex) {
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			logger.error(String.format(LogInfoUtils.NO_DATA_TMPL, methodName), ex);
		}

		return result;
	}

	/**
	 * 随机获取一个代理服务器
	 *
	 * @param proxyServers
	 * @return
	 */
	private static String getRandomProxyServer(List<String> proxyServers) {
		if (proxyServers.size() > 0) {
			return proxyServers.get(CustomMathUtils.calRandomInteger(proxyServers.size()));
		}

		return "";
	}
}
