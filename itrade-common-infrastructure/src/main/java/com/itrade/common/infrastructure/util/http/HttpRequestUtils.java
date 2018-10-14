package com.itrade.common.infrastructure.util.http;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.itrade.common.infrastructure.util.enums.RequestFromEnum;
import com.itrade.common.infrastructure.util.math.CustomNumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.util.Map;

public class HttpRequestUtils {

	// logger
	private static final Logger logger = LoggerFactory.getLogger(HttpRequestUtils.class);

	/**
	 * 获取当前页URL（使用urlrewrite重写后的页面URL可以尝试由此方法获取）
	 *
	 * @param request
	 * @return
	 */
	public static String getRawUrl(HttpServletRequest request) {
		return (String) request.getAttribute("javax.servlet.forward.request_uri");
	}

	/**
	 * 从 HttpServletRequest 获取 requestFrom 参数
	 *
	 * @param request
	 * @return
	 */
	public static RequestFromEnum getRequestFrom(HttpServletRequest request) {
		RequestFromEnum result = RequestFromEnum.WEB_PC;

		String userFrom = request.getHeader("requestFrom");
		if (Strings.isNullOrEmpty(userFrom)) {
			userFrom = request.getHeader("requestfrom");
		}
		if (Strings.isNullOrEmpty(userFrom)) {
			userFrom = request.getHeader("RequestFrom");
		}
		if (Strings.isNullOrEmpty(userFrom)) {
			userFrom = request.getHeader("REQUESTFROM");
		}

		try {
			result = RequestFromEnum.values()[CustomNumberUtils.toInt(userFrom)];
		} catch (Exception e) {
			logger.error("userFrom=" + userFrom, e);
		}

		return result;
	}

	/**
	 * 从 HttpServletRequest 获取 requestIP 参数
	 *
	 * @param request
	 * @return
	 */
	public static String getRequestIP(HttpServletRequest request) {
		String result = request.getHeader("requestIP");

		if (Strings.isNullOrEmpty(result)) {
			result = request.getHeader("requestip");
		}

		if (Strings.isNullOrEmpty(result)) {
			result = request.getHeader("RequestIP");
		}

		if (Strings.isNullOrEmpty(result)) {
			result = request.getHeader("REQUESTIP");
		}

		return result;
	}

	/**
	 * 从 HttpServletRequest 获取 requestGZIP 参数
	 *
	 * @param request
	 * @return
	 */
	public static boolean getRequestGZIP(HttpServletRequest request) {
		String result = request.getHeader("requestGZIP");

		if (Strings.isNullOrEmpty(result)) {
			result = request.getHeader("requestgzip");
		}

		if (Strings.isNullOrEmpty(result)) {
			result = request.getHeader("RequestGZIP");
		}

		if (Strings.isNullOrEmpty(result)) {
			result = request.getHeader("REQUESTGZIP");
		}

		return !Strings.isNullOrEmpty(result) && result.equals("1");
	}

	/**
	 * 从 HttpServletRequest 获取 version 参数
	 *
	 * @param request
	 * @return
	 */
	public static String getVersion(HttpServletRequest request) {
		return request.getHeader("version");
	}

	/**
	 * 判断 header 中带的版本号是否为最新版本（"latest"、""、null 都为最新版本）
	 *
	 * @param version
	 * @return
	 */
	public static boolean isLatestVersion(String version) {
		if (Strings.isNullOrEmpty(version) || version.equals("latest")) {
			return true;
		}

		return false;
	}

	/**
	 * 获取当前页URL
	 *
	 * @param request
	 * @param forceHTTPS
	 * @return
	 */
	public static String getCurrentPageUrl(HttpServletRequest request, boolean forceHTTPS) {
		String requestURL = request.getRequestURL().toString();
		String queryString = request.getQueryString();

		if (forceHTTPS && requestURL.startsWith("http://")) {
			requestURL = requestURL.replace("http://", "https://");
		}

		if (queryString != null) {
			return String.format("%s?%s", requestURL, queryString);
		} else {
			return requestURL;
		}
	}

	/**
	 * 获取用户登录后的返回页面 URL
	 *
	 * @param request
	 * @return
	 */
	public static String getReturnPageUrl(HttpServletRequest request) {
		try {
			String queryString = !Strings.isNullOrEmpty(request.getQueryString()) ? "?" + request.getQueryString() : "";
			return URLEncoder.encode(request.getServletPath() + queryString, "UTF-8");
		} catch (Exception ex) {
			logger.error("getSigninReturnUrl ERROR!", ex);
		}

		return "https://www.jindouyun.com/";
	}

	/**
	 * 通过 httpGetUrl 计算 URL 参数名称、参数值数据map
	 *
	 * @param httpGetUrl
	 * @return
	 */
	public static Map<String, String> getHttpGetUrlParamNameValueMap(String httpGetUrl) {
		Map<String, String> paramMap = Maps.newLinkedHashMap();

		if (httpGetUrl.contains("?")) {
			httpGetUrl = httpGetUrl.substring(httpGetUrl.indexOf("?") + 1);
		}

		String[] paramPairStrs = httpGetUrl.split("&");
		for (String paramPairStr : paramPairStrs) {
			String[] keyValStrs = paramPairStr.split("=");
			if (keyValStrs.length == 2) {
				paramMap.put(keyValStrs[0], keyValStrs[1]);
			}
		}

		return paramMap;
	}

	/**
	 * 通过 httpGetUrl、paramName 计算 URL 具体参数值数据
	 *
	 * @param httpGetUrl
	 * @param paramName
	 * @return
	 */
	public static String getHttpGetUrlParamValue(String httpGetUrl, String paramName) {
		Map<String, String> paramMap = HttpRequestUtils.getHttpGetUrlParamNameValueMap(httpGetUrl);

		if (paramMap.containsKey(paramName)) {
			return paramMap.get(paramName);
		}

		return "";
	}
}
