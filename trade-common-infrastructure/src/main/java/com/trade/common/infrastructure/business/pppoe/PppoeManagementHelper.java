package com.trade.common.infrastructure.business.pppoe;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.trade.common.infrastructure.util.collection.CustomListUtils;
import com.trade.common.infrastructure.util.host.HostUtils;
import com.trade.common.infrastructure.util.httpclient.HttpClientInstance;
import com.trade.common.infrastructure.util.httpclient.HttpClientUtils;
import com.trade.common.infrastructure.util.logger.LogInfoUtils;
import com.trade.common.infrastructure.util.pppoe.PPPOEManagementUtils;
import com.trade.common.infrastructure.util.string.CustomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class PppoeManagementHelper {

	// 日志记录
	private static final Logger s_logger = LoggerFactory.getLogger(PppoeManagementHelper.class);

	// 锁对象
	private static final Object s_lockObj = new Object();

	// 相关变量
	private static LocalDateTime s_lastPPPOETime = LocalDateTime.now();
	private static int s_PPPOEIntervalSeconds = 60;
	private static boolean isRedailing = false;

	/**
	 * 重新拨号换IP
	 *
	 * @param isWriteIPLog
	 * @return
	 */
	public boolean startRedial(boolean isWriteIPLog) {
		boolean result = true;

		try {
			if (checkPPPOEIntervalTime()) {
				synchronized (s_lockObj) {
					if (checkPPPOEIntervalTime()) {
						// 开始拨号
						if (isWriteIPLog) {
							String oldIPStatus = HostUtils.getPublicNetworkIP();

							isRedailing = true;
							PPPOEManagementUtils.smartDial(3, 4);
							isRedailing = false;

							s_logger.info(CustomStringUtils.replaceEnter(String.format("startRedial SUCCESS! oldIPStatus=%s, newIPStatus=%s", oldIPStatus, HostUtils.getPublicNetworkIP())));
						} else {
							isRedailing = true;
							PPPOEManagementUtils.smartDial(3, 4);
							isRedailing = false;

							s_logger.info("startRedial SUCCESS!");
						}

						// 更新最后拨号时间
						s_lastPPPOETime = LocalDateTime.now();
					}
				}
			}
		} catch (Exception ex) {
			result = false;
			s_logger.error(String.format(LogInfoUtils.NO_DATA_TMPL, Thread.currentThread().getStackTrace()[1].getMethodName()), ex);
		}

		return result;
	}

	/**
	 * 检查拨号间隔时间
	 *
	 * @return
	 */
	private boolean checkPPPOEIntervalTime() {
		return LocalDateTime.now().isAfter(s_lastPPPOETime.plusSeconds(s_PPPOEIntervalSeconds));
	}

	/**
	 * 检查是否正在拨号，如果正在拨号则等待一段时间
	 *
	 * @return
	 */
	public boolean isNetworkAvailable() {
		while (isRedailing) {
			try {
				TimeUnit.MILLISECONDS.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		return true;
	}

	/** ============== 请求参数中不包含 httpClientInstance 数据 ============== */
	/**
	 * 读取页面HTML（包含请求被拦截后，重新拨号逻辑）
	 *
	 * @param postUrl
	 * @param postData
	 * @param ignoreEmptyHTML
	 * @return
	 */
	public String getHTML(String postUrl, String postData, boolean ignoreEmptyHTML) {
		return getHTML(null, postUrl, postData, 60000, "", "", ignoreEmptyHTML);
	}

	/**
	 * 读取页面HTML（包含请求被拦截后，重新拨号逻辑）
	 *
	 * @param postUrl
	 * @param postData
	 * @param timeout
	 * @param ignoreEmptyHTML
	 * @return
	 */
	public String getHTML(String postUrl, String postData, int timeout, boolean ignoreEmptyHTML) {
		return getHTML(null, postUrl, postData, timeout, "", "", ignoreEmptyHTML);
	}

	/**
	 * 读取页面HTML（包含请求被拦截后，重新拨号逻辑）
	 *
	 * @param postUrl
	 * @param postData
	 * @param refUrl
	 * @param ignoreEmptyHTML
	 * @return
	 */
	public String getHTML(String postUrl, String postData, String refUrl, boolean ignoreEmptyHTML) {
		return getHTML(null, postUrl, postData, 60000, refUrl, "", ignoreEmptyHTML);
	}

	/**
	 * 读取页面HTML（包含请求被拦截后，重新拨号逻辑）
	 *
	 * @param postUrl
	 * @param postData
	 * @param refUrl
	 * @param blockInfo
	 * @param ignoreEmptyHTML
	 * @return
	 */
	public String getHTML(String postUrl, String postData, String refUrl, String blockInfo, boolean ignoreEmptyHTML) {
		return getHTML(null, postUrl, postData, 60000, refUrl, blockInfo, ignoreEmptyHTML);
	}

	/**
	 * 读取页面HTML（包含请求被拦截后，重新拨号逻辑）
	 *
	 * @param postUrl
	 * @param postData
	 * @param timeout
	 * @param refUrl
	 * @param blockInfo
	 * @param ignoreEmptyHTML
	 * @return
	 */
	public String getHTML(String postUrl, String postData, int timeout, String refUrl, String blockInfo, boolean ignoreEmptyHTML) {
		return getHTML(null, postUrl, postData, timeout, refUrl, blockInfo, ignoreEmptyHTML);
	}

	/**
	 * 读取页面HTML（包含请求被拦截后，重新拨号逻辑）
	 *
	 * @param postUrl
	 * @param postData
	 * @param timeout
	 * @param refUrl
	 * @param blockInfos
	 * @param ignoreEmptyHTML
	 * @return
	 */
	public String getHTML(String postUrl, String postData, int timeout, String refUrl, List<String> blockInfos, boolean ignoreEmptyHTML) {
		return getHTML(null, postUrl, postData, timeout, refUrl, Maps.newHashMap(), blockInfos, ignoreEmptyHTML);
	}

	/**
	 * 读取页面HTML（包含请求被拦截后，重新拨号逻辑）
	 *
	 * @param postUrl
	 * @param postData
	 * @param timeout
	 * @param refUrl
	 * @param additionalHeaders
	 * @param blockInfos
	 * @param ignoreEmptyHTML
	 * @return
	 */
	public String getHTML(String postUrl, String postData, int timeout, String refUrl, Map<String, String> additionalHeaders, List<String> blockInfos, boolean ignoreEmptyHTML) {
		return getHTML(null, postUrl, postData, timeout, refUrl, additionalHeaders, blockInfos, ignoreEmptyHTML);
	}

	/** ============== 请求参数中包含 httpClientInstance 数据 ============== */
	/**
	 * 读取页面HTML（包含请求被拦截后，重新拨号逻辑）
	 *
	 * @param httpClientInstance
	 * @param postUrl
	 * @param postData
	 * @param ignoreEmptyHTML
	 * @return
	 */
	public String getHTML(HttpClientInstance httpClientInstance, String postUrl, String postData, boolean ignoreEmptyHTML) {
		return getHTML(httpClientInstance, postUrl, postData, 60000, "", "", ignoreEmptyHTML);
	}

	/**
	 * 读取页面HTML（包含请求被拦截后，重新拨号逻辑）
	 *
	 * @param httpClientInstance
	 * @param postUrl
	 * @param postData
	 * @param timeout
	 * @param ignoreEmptyHTML
	 * @return
	 */
	public String getHTML(HttpClientInstance httpClientInstance, String postUrl, String postData, int timeout, boolean ignoreEmptyHTML) {
		return getHTML(httpClientInstance, postUrl, postData, timeout, "", "", ignoreEmptyHTML);
	}

	/**
	 * 读取页面HTML（包含请求被拦截后，重新拨号逻辑）
	 *
	 * @param httpClientInstance
	 * @param postUrl
	 * @param postData
	 * @param refUrl
	 * @param ignoreEmptyHTML
	 * @return
	 */
	public String getHTML(HttpClientInstance httpClientInstance, String postUrl, String postData, String refUrl, boolean ignoreEmptyHTML) {
		return getHTML(httpClientInstance, postUrl, postData, 60000, refUrl, "", ignoreEmptyHTML);
	}

	/**
	 * 读取页面HTML（包含请求被拦截后，重新拨号逻辑）
	 *
	 * @param httpClientInstance
	 * @param postUrl
	 * @param postData
	 * @param refUrl
	 * @param blockInfo
	 * @param ignoreEmptyHTML
	 * @return
	 */
	public String getHTML(HttpClientInstance httpClientInstance, String postUrl, String postData, String refUrl, String blockInfo, boolean ignoreEmptyHTML) {
		return getHTML(httpClientInstance, postUrl, postData, 60000, refUrl, blockInfo, ignoreEmptyHTML);
	}

	/**
	 * 读取页面HTML（包含请求被拦截后，重新拨号逻辑）
	 *
	 * @param httpClientInstance
	 * @param postUrl
	 * @param postData
	 * @param timeout
	 * @param refUrl
	 * @param blockInfo
	 * @param ignoreEmptyHTML
	 * @return
	 */
	public String getHTML(HttpClientInstance httpClientInstance, String postUrl, String postData, int timeout, String refUrl, String blockInfo, boolean ignoreEmptyHTML) {
		return getHTML(httpClientInstance, postUrl, postData, timeout, refUrl, Maps.newHashMap(), !Strings.isNullOrEmpty(blockInfo) ? Lists.newArrayList(blockInfo) : Lists.newArrayList(), ignoreEmptyHTML);
	}

	/**
	 * 读取页面HTML（包含请求被拦截后，重新拨号逻辑）
	 *
	 * @param httpClientInstance
	 * @param postUrl
	 * @param postData
	 * @param timeout
	 * @param refUrl
	 * @param blockInfos
	 * @param ignoreEmptyHTML
	 * @return
	 */
	public String getHTML(HttpClientInstance httpClientInstance, String postUrl, String postData, int timeout, String refUrl, List<String> blockInfos, boolean ignoreEmptyHTML) {
		return getHTML(httpClientInstance, postUrl, postData, timeout, refUrl, Maps.newHashMap(), blockInfos, ignoreEmptyHTML);
	}

	/**
	 * 读取页面HTML（包含请求被拦截后，重新拨号逻辑）
	 *
	 * @param httpClientInstance
	 * @param postUrl
	 * @param postData
	 * @param timeout
	 * @param refUrl
	 * @param additionalHeaders
	 * @param blockInfos
	 * @param ignoreEmptyHTML
	 * @return
	 */
	public String getHTML(HttpClientInstance httpClientInstance, String postUrl, String postData, int timeout, String refUrl, Map<String, String> additionalHeaders, List<String> blockInfos, boolean ignoreEmptyHTML) {
		String result = "";

		if (!Strings.isNullOrEmpty(postUrl)) {
			boolean isError = false;

			for (int i = 0; i < 2; i++) {
				try {
					// 获取页面代码
					if (isNetworkAvailable()) {
						if (httpClientInstance != null) {
							result = httpClientInstance.getHTML(postUrl, postData, "utf-8", timeout, "", refUrl, additionalHeaders);
						} else {
							result = HttpClientUtils.getHTML(postUrl, postData, "utf-8", timeout, "", refUrl, additionalHeaders);
						}
					}

					// 判断页面代码是否有效
					if (!ignoreEmptyHTML && Strings.isNullOrEmpty(result)) {
						isError = true;
					}

					if (checkWebSiteIsBlocked(result, blockInfos)) {
						isError = true;
						s_logger.info(String.format(LogInfoUtils.HAS_DATA_TMPL, Thread.currentThread().getStackTrace()[1].getMethodName(), CustomListUtils.listToString(blockInfos, "|")));
					}
				} catch (Exception ex) {
					isError = true;

					String logInfo = String.format("getHTML FAIL! postUrl=%s, postData=%s", postUrl, postData);
					s_logger.error(String.format(LogInfoUtils.HAS_DATA_TMPL, Thread.currentThread().getStackTrace()[1].getMethodName(), logInfo), ex);
				}

				// 出现错误后, 重新拨号换IP
				if (isError) {
					startRedial(false);
				} else if (!Strings.isNullOrEmpty(result)) {
					break;
				}
			}
		}

		return result;
	}

	/**
	 * 检查页面代码，判断当前当前请求已经已经被对方网站屏蔽
	 *
	 * @param pageCode
	 * @param blockInfos
	 * @return
	 */
	private boolean checkWebSiteIsBlocked(String pageCode, List<String> blockInfos) {
		for (String blockInfo : blockInfos) {
			if (!Strings.isNullOrEmpty(blockInfo) && pageCode.contains(blockInfo)) {
				return true;
			}
		}

		return false;
	}
}
