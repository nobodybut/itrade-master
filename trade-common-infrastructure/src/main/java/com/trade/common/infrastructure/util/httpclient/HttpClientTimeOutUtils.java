package com.trade.common.infrastructure.util.httpclient;

import org.apache.http.Header;

import java.util.List;
import java.util.Map;

public class HttpClientTimeOutUtils {

	/**
	 * 抓取目标页面源代码，严格超时时间处理
	 *
	 * @param postUrl
	 * @param postData
	 * @return
	 */
	public static String getHTML(String postUrl, String postData) {
		return getHTML(postUrl, postData, "utf-8", 15000, "");
	}

	/**
	 * 抓取目标页面源代码，严格超时时间处理
	 *
	 * @param postUrl
	 * @param postData
	 * @param proxyServer
	 * @return
	 */
	public static String getHTML(String postUrl, String postData, String proxyServer) {
		return getHTML(postUrl, postData, "utf-8", 15000, proxyServer);
	}

	/**
	 * 抓取目标页面源代码，严格超时时间处理
	 *
	 * @param postUrl
	 * @param postData
	 * @param encode
	 * @param timeoutMilliseconds
	 * @return
	 */
	public static String getHTML(String postUrl, String postData, String encode, int timeoutMilliseconds) {
		return getHTML(postUrl, postData, encode, timeoutMilliseconds, "");
	}

	/**
	 * 抓取目标页面源代码，严格超时时间处理
	 *
	 * @param postUrl
	 * @param postData
	 * @param encode
	 * @param timeoutMilliseconds
	 * @param proxyServer
	 * @return
	 */
	public static String getHTML(String postUrl, String postData, String encode, int timeoutMilliseconds, String proxyServer) {
		timeoutMilliseconds = (timeoutMilliseconds == 0) ? 15000 : timeoutMilliseconds;

		HttpClientTimeOutInstance httpClientTimeOutInstance = new HttpClientTimeOutInstance(postUrl, postData, encode, timeoutMilliseconds, proxyServer);
		return httpClientTimeOutInstance.getHTML();
	}

	/**
	 * 抓取目标页面源代码，严格超时时间处理
	 *
	 * @param postUrl
	 * @param postData
	 * @param encode
	 * @param timeoutMilliseconds
	 * @param proxyServer
	 * @param refererUrl
	 * @return
	 */
	public static String getHTML(String postUrl, String postData, String encode, int timeoutMilliseconds, String proxyServer, String refererUrl) {
		timeoutMilliseconds = (timeoutMilliseconds == 0) ? 15000 : timeoutMilliseconds;

		HttpClientTimeOutInstance httpClientTimeOutInstance = new HttpClientTimeOutInstance(postUrl, postData, encode, timeoutMilliseconds, proxyServer, refererUrl);
		return httpClientTimeOutInstance.getHTML();
	}

	/**
	 * 抓取目标页面源代码，严格超时时间处理
	 *
	 * @param postUrl
	 * @param postData
	 * @param encode
	 * @param timeoutMilliseconds
	 * @param proxyServer
	 * @param refererUrl
	 * @return
	 */
	public static String getHTML(String postUrl, String postData, String encode, int timeoutMilliseconds, String proxyServer, String refererUrl, final Map<String, String> additionalHeaders) {
		timeoutMilliseconds = (timeoutMilliseconds == 0) ? 15000 : timeoutMilliseconds;

		HttpClientTimeOutInstance httpClientTimeOutInstance = new HttpClientTimeOutInstance(postUrl, postData, encode, timeoutMilliseconds, proxyServer, refererUrl, additionalHeaders);
		return httpClientTimeOutInstance.getHTML();
	}

	/**
	 * 抓取目标页面源代码，严格超时时间处理
	 *
	 * @param httpClientInstance
	 * @param postUrl
	 * @param postData
	 * @param encode
	 * @param timeoutMilliseconds
	 * @return
	 */
	public static String getHTML(HttpClientInstance httpClientInstance, String postUrl, String postData, String encode, int timeoutMilliseconds) {
		return getHTML(httpClientInstance, postUrl, postData, encode, timeoutMilliseconds, "");
	}

	/**
	 * 抓取目标页面源代码，严格超时时间处理
	 *
	 * @param httpClientInstance
	 * @param postUrl
	 * @param postData
	 * @param encode
	 * @param timeoutMilliseconds
	 * @param proxyServer
	 * @return
	 */
	public static String getHTML(HttpClientInstance httpClientInstance, String postUrl, String postData, String encode, int timeoutMilliseconds, String proxyServer) {
		timeoutMilliseconds = (timeoutMilliseconds == 0) ? 15000 : timeoutMilliseconds;

		HttpClientTimeOutInstance httpClientTimeOutInstance = new HttpClientTimeOutInstance(httpClientInstance, postUrl, postData, encode, timeoutMilliseconds, proxyServer);
		return httpClientTimeOutInstance.getHTML();
	}

	/**
	 * 抓取目标页面源代码，严格超时时间处理
	 *
	 * @param httpClientInstance
	 * @param postUrl
	 * @param postData
	 * @param encode
	 * @param timeoutMilliseconds
	 * @param proxyServer
	 * @param refererUrl
	 * @return
	 */
	public static String getHTML(HttpClientInstance httpClientInstance, String postUrl, String postData, String encode, int timeoutMilliseconds, String proxyServer, String refererUrl) {
		timeoutMilliseconds = (timeoutMilliseconds == 0) ? 15000 : timeoutMilliseconds;

		HttpClientTimeOutInstance httpClientTimeOutInstance = new HttpClientTimeOutInstance(httpClientInstance, postUrl, postData, encode, timeoutMilliseconds, proxyServer, refererUrl);
		return httpClientTimeOutInstance.getHTML();
	}

	/**
	 * 抓取目标页面源代码，严格超时时间处理
	 *
	 * @param httpClientInstance
	 * @param postUrl
	 * @param postData
	 * @param encode
	 * @param timeoutMilliseconds
	 * @param proxyServer
	 * @param refererUrl
	 * @param additionalHeaders
	 * @return
	 */
	public static String getHTML(HttpClientInstance httpClientInstance, String postUrl, String postData, String encode, int timeoutMilliseconds, String proxyServer, String refererUrl, final Map<String, String> additionalHeaders) {
		timeoutMilliseconds = (timeoutMilliseconds == 0) ? 15000 : timeoutMilliseconds;

		HttpClientTimeOutInstance httpClientTimeOutInstance = new HttpClientTimeOutInstance(httpClientInstance, postUrl, postData, encode, timeoutMilliseconds, proxyServer, refererUrl, additionalHeaders);
		return httpClientTimeOutInstance.getHTML();
	}

	/**
	 * 抓取目标页面源代码，严格超时时间处理
	 *
	 * @param httpClientInstance
	 * @param postUrl
	 * @param postData
	 * @param encode
	 * @param timeoutMilliseconds
	 * @param proxyServer
	 * @param refererUrl
	 * @param responseHeaders
	 * @return
	 */
	public static String getHTML(HttpClientInstance httpClientInstance, String postUrl, String postData, String encode, int timeoutMilliseconds, String proxyServer, String refererUrl, final List<Header> responseHeaders) {
		timeoutMilliseconds = (timeoutMilliseconds == 0) ? 15000 : timeoutMilliseconds;

		HttpClientTimeOutInstance httpClientTimeOutInstance = new HttpClientTimeOutInstance(httpClientInstance, postUrl, postData, encode, timeoutMilliseconds, proxyServer, refererUrl, responseHeaders);
		return httpClientTimeOutInstance.getHTML();
	}

	// ============================== 抓取目标页面源代码（按次数） ==============================

	/**
	 * 抓取目标页面源代码，严格超时时间处理（按次数）
	 *
	 * @param postUrl
	 * @param postData
	 * @param encode
	 * @param timeoutMilliseconds
	 * @param proxyServer
	 * @param times
	 * @return
	 */
	public static String getHTML_Times(String postUrl, String postData, String encode, int timeoutMilliseconds, String proxyServer, int times) {
		timeoutMilliseconds = (timeoutMilliseconds == 0) ? 15000 : timeoutMilliseconds;

		HttpClientTimeOutInstance httpClientTimeOutInstance = new HttpClientTimeOutInstance(postUrl, postData, encode, timeoutMilliseconds, proxyServer);
		return httpClientTimeOutInstance.getHTML_Times(times);
	}

	/**
	 * 抓取目标页面源代码，严格超时时间处理（按次数）
	 *
	 * @param httpClientInstance
	 * @param postUrl
	 * @param postData
	 * @param encode
	 * @param timeoutMilliseconds
	 * @param proxyServer
	 * @param times
	 * @return
	 */
	public static String getHTML_Times(HttpClientInstance httpClientInstance, String postUrl, String postData, String encode, int timeoutMilliseconds, String proxyServer, int times) {
		timeoutMilliseconds = (timeoutMilliseconds == 0) ? 15000 : timeoutMilliseconds;

		HttpClientTimeOutInstance httpClientTimeOutInstance = new HttpClientTimeOutInstance(httpClientInstance, postUrl, postData, encode, timeoutMilliseconds, proxyServer);
		return httpClientTimeOutInstance.getHTML_Times(times);
	}

	/**
	 * 抓取目标页面源代码，严格超时时间处理（按次数）
	 *
	 * @param postUrl
	 * @param postData
	 * @param encode
	 * @param timeoutMilliseconds
	 * @param proxyServer
	 * @param refererUrl
	 * @param times
	 * @return
	 */
	public static String getHTML_Times(String postUrl, String postData, String encode, int timeoutMilliseconds, String proxyServer, String refererUrl, int times) {
		timeoutMilliseconds = (timeoutMilliseconds == 0) ? 15000 : timeoutMilliseconds;

		HttpClientTimeOutInstance httpClientTimeOutInstance = new HttpClientTimeOutInstance(postUrl, postData, encode, timeoutMilliseconds, proxyServer, refererUrl);
		return httpClientTimeOutInstance.getHTML_Times(times);
	}

	/**
	 * 抓取目标页面源代码，严格超时时间处理（按次数）
	 *
	 * @param httpClientInstance
	 * @param postUrl
	 * @param postData
	 * @param encode
	 * @param timeoutMilliseconds
	 * @param proxyServer
	 * @param refererUrl
	 * @param times
	 * @return
	 */
	public static String getHTML_Times(HttpClientInstance httpClientInstance, String postUrl, String postData, String encode, int timeoutMilliseconds, String proxyServer, String refererUrl, int times) {
		timeoutMilliseconds = (timeoutMilliseconds == 0) ? 15000 : timeoutMilliseconds;

		HttpClientTimeOutInstance httpClientTimeOutInstance = new HttpClientTimeOutInstance(httpClientInstance, postUrl, postData, encode, timeoutMilliseconds, proxyServer, refererUrl);
		return httpClientTimeOutInstance.getHTML_Times(times);
	}

	/**
	 * 抓取目标页面源代码，严格超时时间处理（按次数）
	 *
	 * @param httpClientInstance
	 * @param postUrl
	 * @param postData
	 * @param encode
	 * @param timeoutMilliseconds
	 * @param proxyServer
	 * @param refererUrl
	 * @param additionalHeaders
	 * @param times
	 * @return
	 */
	public static String getHTML_Times(HttpClientInstance httpClientInstance, String postUrl, String postData, String encode, int timeoutMilliseconds, String proxyServer, String refererUrl, final Map<String, String> additionalHeaders, int times) {
		timeoutMilliseconds = (timeoutMilliseconds == 0) ? 15000 : timeoutMilliseconds;

		HttpClientTimeOutInstance httpClientTimeOutInstance = new HttpClientTimeOutInstance(httpClientInstance, postUrl, postData, encode, timeoutMilliseconds, proxyServer, refererUrl, additionalHeaders);
		return httpClientTimeOutInstance.getHTML_Times(times);
	}
}
