package com.itrade.common.infrastructure.util.proxyserver;

import com.google.common.base.Strings;
import com.itrade.common.infrastructure.util.collection.KeyValuePair;
import com.itrade.common.infrastructure.util.date.CustomDateFormatUtils;
import com.itrade.common.infrastructure.util.enums.ProxyServerSupplierEnum;
import com.itrade.common.infrastructure.util.httpclient.OkHttpClientUtils;
import com.itrade.common.infrastructure.util.string.CustomStringUtils;
import org.slf4j.Logger;

import java.time.LocalTime;

public class ProxyServerStatusUtils {

	/**
	 * 记录proxy状态
	 *
	 * @param postUrl
	 * @param proxyServer
	 * @param isRemoveProxyServer
	 * @param s_logger
	 */
	public static void recordProxyStatus(String postUrl, String proxyServer, boolean isRemoveProxyServer, LocalTime startTime, Logger s_logger) {
		KeyValuePair<ProxyServerSupplierEnum, String> proxyServerKV = ProxyServerSupplierUtils.parseProxyServerKV(proxyServer);

		// 如果使用了代理服务器，并出现某些种类的异常，则需要从全局缓存中删除此条代理服务器
		if (!Strings.isNullOrEmpty(proxyServerKV.getValue())) {
			String byDomain = calDomainByUrl(postUrl);

			if (isRemoveProxyServer) {
				// 记录代理服务器错误数量
				OkHttpClientUtils.getHTML(String.format("http://solution-apis.jindouyun.com/proxyServerService/delProxyServer?proxyServerSupplier=%s&domain=%s&proxyServer=%s", proxyServerKV.getKey().ordinal(), byDomain, proxyServer));

				// 记录代理服务器错误日志
				if (!postUrl.contains("baidu")) {
					s_logger.info(String.format("[ProxyServerDelete] %s, %s, %s, %s", proxyServerKV.getValue(), proxyServerKV.getKey(), byDomain, CustomDateFormatUtils.formatTime_HHmmssSSS(startTime)));
				}

			} else {
				// 记录代理服务器正确数量
				OkHttpClientUtils.getHTML(String.format("http://solution-apis.jindouyun.com/proxyServerService/incrSuccessProxyServer?proxyServerSupplier=%s&domain=%s", proxyServerKV.getKey().ordinal(), byDomain));
			}
		}
	}

	/**
	 * 根据 postUrl 计算 domain 数据
	 *
	 * @param postUrl
	 * @return
	 */
	public static String calDomainByUrl(String postUrl) {
		return (postUrl.startsWith("https")) ? CustomStringUtils.substringBetween(postUrl, "https://", "/") : CustomStringUtils.substringBetween(postUrl, "http://", "/");
	}
}
