package com.trade.common.infrastructure.util.phantomjs;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.google.common.base.Strings;
import com.trade.common.infrastructure.util.httpclient.HttpClientUtils;
import com.trade.common.infrastructure.util.math.CustomNumberUtils;
import com.trade.common.infrastructure.util.proxyserver.ProxyServerStatusUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.conn.HttpHostConnectException;

import java.io.IOException;
import java.net.URL;
import java.time.LocalTime;
import java.util.Map;

@Slf4j
public class WebClientUtils {

	/**
	 * webClient获取html
	 *
	 * @param postUrl
	 * @param postData
	 * @param timeout
	 * @param proxyServer
	 * @return
	 */
	public static String getHtml(final WebClient webClient, final String postUrl, final String postData, final int timeout, final String proxyServer) {
		return doGetHtml(webClient, postUrl, postData, timeout, proxyServer, null);
	}

	/**
	 * webClient获取html
	 *
	 * @param webClient
	 * @param postUrl
	 * @param postData
	 * @param timeout
	 * @param proxyServer
	 * @param additionalHeaders
	 * @return
	 */
	public static String doGetHtml(final WebClient webClient, final String postUrl, final String postData, final int timeout, final String proxyServer, final Map<String, String> additionalHeaders) {
		String result = "";

		// 计算相关变量
		boolean isRemoveProxyServer = false;
		LocalTime startTime = LocalTime.now();

		// 设置 参数
		setWebClientParameters(webClient, timeout, proxyServer);

		try {
			HttpMethod httpMethod = HttpMethod.GET;
			if (!Strings.isNullOrEmpty(postData)) {
				httpMethod = HttpMethod.POST;
			}

			WebRequest webRequest = new WebRequest(new URL(postUrl), httpMethod);
			if (!Strings.isNullOrEmpty(postData)) {
				webRequest.setRequestBody(postData);
			}

			// 设置header
			if (additionalHeaders != null && additionalHeaders.size() > 0) {
				for (Map.Entry<String, String> entry : additionalHeaders.entrySet()) {
					webRequest.setAdditionalHeader(entry.getKey(), entry.getValue());
				}
			}

			HtmlPage htmlPage = webClient.getPage(webRequest);
			result = htmlPage.asXml();
		} catch (HttpHostConnectException ex) {
			isRemoveProxyServer = true;
			if (ex != null) {
				isRemoveProxyServer = true;
				String errorLogInfo = HttpClientUtils.calErrorLogInfo(postUrl, timeout, proxyServer, ex.toString());
				if (!Strings.isNullOrEmpty(errorLogInfo)) {
					log.error("HttpHostConnectException:" + errorLogInfo);
				}
			}
		} catch (IOException ex) {
			if (ex != null) {
				String errorLogInfo = HttpClientUtils.calErrorLogInfo(postUrl, timeout, proxyServer, ex.toString());
				if (!Strings.isNullOrEmpty(errorLogInfo)) {
					log.error("IOException:" + errorLogInfo);
				}
			}
		} catch (Exception ex) {
			if (ex != null) {
				String errorLogInfo = HttpClientUtils.calErrorLogInfo(postUrl, timeout, proxyServer, ex.toString());
				if (!Strings.isNullOrEmpty(errorLogInfo)) {
					log.error("Exception:" + errorLogInfo);
				}
			}
		} finally {
			webClient.close();
		}

		//记录proxy状态
		ProxyServerStatusUtils.recordProxyStatus(postUrl, proxyServer, isRemoveProxyServer, startTime, log);

		return result;
	}

	/**
	 * 设置 webClient 参数
	 *
	 * @param webClient
	 * @param timeout
	 */
	public static void setWebClientParameters(WebClient webClient, int timeout, String proxyServer) {
		webClient.getOptions().setTimeout(timeout);

		//设置webClient的相关参数
		webClient.getOptions().setCssEnabled(false);
		webClient.getOptions().setDownloadImages(false);
		// 开启cookie管理
		webClient.getCookieManager().setCookiesEnabled(true);
		// 开启js解析
		webClient.getOptions().setJavaScriptEnabled(true);
		// 当出现Http error时，程序不抛异常继续执行
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
		// 防止js语法错误抛出异常
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		// 默认是false, 设置为true的话不让你的浏览行为被记录
		webClient.getOptions().setDoNotTrackEnabled(false);
		// 设置Ajax异步处理控制器即启用Ajax支持
		webClient.setAjaxController(new NicelyResynchronizingAjaxController());
		// 等待js执行
		webClient.waitForBackgroundJavaScript(60 * 1000);

		// 设置代理
		setProxy(webClient, proxyServer);
	}

	/**
	 * 设置代理
	 *
	 * @param proxyServer
	 * @param webClient
	 */
	public static void setProxy(WebClient webClient, String proxyServer) {
		if (!Strings.isNullOrEmpty(proxyServer)) {
			if (proxyServer.contains("^")) {
				proxyServer = proxyServer.substring(proxyServer.indexOf("^") + 1);
			}

			String[] proxyArr = proxyServer.split(":");

			if (proxyArr.length == 2) {
				webClient.getOptions().setProxyConfig(new ProxyConfig(proxyArr[0], CustomNumberUtils.toInt(proxyArr[1])));
			}
		}
	}

	/**
	 * 根据是否移动端获取 chrome browser 对象
	 *
	 * @param isMobileBrowser
	 * @return
	 */
	public static BrowserVersion buildChromeBrowser(boolean isMobileBrowser) {
		if (isMobileBrowser) {
			BrowserVersion.BrowserVersionBuilder myChromeBuilder = new BrowserVersion.BrowserVersionBuilder(BrowserVersion.CHROME);
			myChromeBuilder.setUserAgent("Mozilla/5.0 (iPhone; CPU iPhone OS 11_0 like Mac OS X) AppleWebKit/604.1.38 (KHTML, like Gecko) Version/11.0 Mobile/15A372 Safari/604.1");
			return myChromeBuilder.build();
		} else {
			return BrowserVersion.CHROME;
		}
	}
}