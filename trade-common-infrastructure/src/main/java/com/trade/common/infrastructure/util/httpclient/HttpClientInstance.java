package com.trade.common.infrastructure.util.httpclient;

import com.google.common.base.Strings;
import com.trade.common.infrastructure.util.collection.CustomListMathUtils;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 带状态的HttpClients，必须 new HttpClientsHelper() 使用
 *
 * @author Administrator
 */
public class HttpClientInstance {

	// Logger
	private static final Logger s_logger = LoggerFactory.getLogger(HttpClientInstance.class);

	// httpclient
	private CloseableHttpClient _httpclient;

	public HttpClientInstance(CookieStore cookieStore) {
		this._httpclient = HttpClientUtils.getHttpClient(cookieStore);
	}

	public HttpClientInstance() {
		this._httpclient = HttpClientUtils.getHttpClient();
	}

	/**
	 * 抓取目标页面源代码
	 *
	 * @param url
	 * @return
	 */
	public String getHTML(final String url) {
		return HttpClientUtils.doGetHTML(_httpclient, url, "", Consts.UTF_8, 0, "", "", null, null, true, "", null, "", "");
	}

	/**
	 * 抓取目标页面源代码
	 *
	 * @param url
	 * @param encode
	 * @return
	 */
	public String getHTML(final String url, final String encode) {
		return HttpClientUtils.doGetHTML(_httpclient, url, "", Charset.forName(encode), 0, "", "", null, null, true, "", null, "", "");
	}

	/**
	 * 抓取目标页面源代码
	 *
	 * @param url
	 * @param postData
	 * @param encode
	 * @return
	 */
	public String getHTML(final String url, final String postData, final String encode) {
		return HttpClientUtils.doGetHTML(_httpclient, url, postData, Charset.forName(encode), 0, "", "", null, null, true, "", null, "", "");
	}

	/**
	 * 抓取目标页面源代码
	 *
	 * @param url
	 * @param postData
	 * @param encode
	 * @param timeout
	 * @return
	 */
	public String getHTML(final String url, final String postData, final String encode, final int timeout) {
		return HttpClientUtils.doGetHTML(_httpclient, url, postData, Charset.forName(encode), timeout, "", "", null, null, true, "", null, "", "");
	}

	/**
	 * 抓取目标页面源代码
	 *
	 * @param url
	 * @param postData
	 * @param encode
	 * @param proxyServer
	 * @return
	 */
	public String getHTML(final String url, final String postData, final String encode, final String proxyServer) {
		return HttpClientUtils.doGetHTML(_httpclient, url, postData, Charset.forName(encode), 0, proxyServer, "", null, null, true, "", null, "", "");
	}

	/**
	 * 抓取目标页面源代码
	 *
	 * @param url
	 * @param postData
	 * @param encode
	 * @param timeout
	 * @param proxyServer
	 * @return
	 */
	public String getHTML(final String url, final String postData, final String encode, final int timeout, final String proxyServer) {
		return HttpClientUtils.doGetHTML(_httpclient, url, postData, Charset.forName(encode), timeout, proxyServer, "", null, null, true, "", null, "", "");
	}

	/**
	 * 抓取目标页面源代码
	 *
	 * @param url
	 * @param postData
	 * @param encode
	 * @param timeout
	 * @param proxyServer
	 * @param refererUrl
	 * @return
	 */
	public String getHTML(final String url, final String postData, final String encode, final int timeout, final String proxyServer, final String refererUrl) {
		return HttpClientUtils.doGetHTML(_httpclient, url, postData, Charset.forName(encode), timeout, proxyServer, refererUrl, null, null, true, "", null, "", "");
	}

	/**
	 * 抓取目标页面源代码
	 *
	 * @param url
	 * @param postData
	 * @param encode
	 * @param timeout
	 * @param proxyServer
	 * @param refererUrl
	 * @param additionalHeaders
	 * @return
	 */
	public String getHTML(final String url, final String postData, final String encode, final int timeout, final String proxyServer, final String refererUrl, final Map<String, String> additionalHeaders) {
		return HttpClientUtils.doGetHTML(_httpclient, url, postData, Charset.forName(encode), timeout, proxyServer, refererUrl, additionalHeaders, null, true, "", null, "", "");
	}

	/**
	 * 抓取目标页面源代码
	 *
	 * @param url
	 * @param postData
	 * @param encode
	 * @param timeout
	 * @param proxyServer
	 * @param refererUrl
	 * @param additionalHeaders
	 * @param isInitBrowserHeader
	 * @return
	 */
	public String getHTML(final String url, final String postData, final String encode, final int timeout, final String proxyServer, final String refererUrl, final Map<String, String> additionalHeaders, boolean isInitBrowserHeader) {
		return HttpClientUtils.doGetHTML(_httpclient, url, postData, Charset.forName(encode), timeout, proxyServer, refererUrl, additionalHeaders, null, isInitBrowserHeader, "", null, "", "");
	}

	/**
	 * 抓取目标页面源代码
	 *
	 * @param url
	 * @param postData
	 * @param encode
	 * @param timeout
	 * @param proxyServer
	 * @param refererUrl
	 * @param responseHeaders
	 * @return
	 */
	public String getHTML(final String url, final String postData, final String encode, final int timeout, final String proxyServer, final String refererUrl, final List<Header> responseHeaders) {
		return HttpClientUtils.doGetHTML(_httpclient, url, postData, Charset.forName(encode), timeout, proxyServer, refererUrl, null, responseHeaders, true, "", null, "", "");
	}

	/**
	 * 抓取目标页面源代码
	 *
	 * @param url
	 * @param postData
	 * @param encode
	 * @param timeout
	 * @param proxyServer
	 * @param refererUrl
	 * @param responseHeaders
	 * @param isInitBrowserHeader
	 * @return
	 */
	public String getHTML(final String url, final String postData, final String encode, final int timeout, final String proxyServer, final String refererUrl, final List<Header> responseHeaders, boolean isInitBrowserHeader) {
		return HttpClientUtils.doGetHTML(_httpclient, url, postData, Charset.forName(encode), timeout, proxyServer, refererUrl, null, responseHeaders, isInitBrowserHeader, "", null, "", "");
	}

	/**
	 * 抓取目标页面源代码
	 *
	 * @param url
	 * @param postData
	 * @param encode
	 * @param timeout
	 * @param proxyServer
	 * @param refererUrl
	 * @param additionalHeaders
	 * @param responseHeaders
	 * @return
	 */
	public String getHTML(final String url, final String postData, final String encode, final int timeout, final String proxyServer, final String refererUrl, final Map<String, String> additionalHeaders, List<Header> responseHeaders) {
		return HttpClientUtils.doGetHTML(_httpclient, url, postData, Charset.forName(encode), timeout, proxyServer, refererUrl, additionalHeaders, responseHeaders, true, "", null, "", "");
	}

//	/**
//	 * 抓取目标页面bytes
//	 *
//	 * @param url
//	 * @param postData
//	 * @param encode
//	 * @param timeout
//	 * @param proxyServer
//	 * @param refererUrl
//	 * @param additionalHeaders
//	 * @param responseHeaders
//	 * @return
//	 */
//	public byte[] getBytes(final String url, final String postData, final String encode, final int timeout, final String proxyServer, final String refererUrl, final Map<String, String> additionalHeaders, List<Header> responseHeaders) {
//		return HttpClientUtils.doGetBytes(_httpclient, url, postData, Charset.forName(encode), timeout, proxyServer, refererUrl, additionalHeaders, responseHeaders, true);
//	}
//
//
//	/**
//	 * 抓取目标页面bytes
//	 *
//	 * @param url
//	 * @param postData
//	 * @param encode
//	 * @param timeout
//	 * @return
//	 */
//	public byte[] getBytes(final String url, final String postData, final String encode, final int timeout) {
//		return HttpClientUtils.doGetBytes(_httpclient, url, postData, Charset.forName(encode), timeout, "", "", null, null, true);
//	}

	/**
	 * 抓取目标页面源代码
	 *
	 * @param url
	 * @param postData
	 * @param encode
	 * @param timeout
	 * @param proxyServer
	 * @param refererUrl
	 * @param additionalHeaders
	 * @param responseHeaders
	 * @param isInitBrowserHeader
	 * @return
	 */
	public String getHTML(final String url, final String postData, final String encode, final int timeout, final String proxyServer, final String refererUrl, final Map<String, String> additionalHeaders, List<Header> responseHeaders, boolean isInitBrowserHeader) {
		return HttpClientUtils.doGetHTML(_httpclient, url, postData, Charset.forName(encode), timeout, proxyServer, refererUrl, additionalHeaders, responseHeaders, isInitBrowserHeader, "", null, "", "");
	}

	/******************************** 抓取目标页面源代码（按次数） ********************************/

	/**
	 * 抓取目标页面源代码（按次数）
	 *
	 * @param url
	 * @param postData
	 * @param encode
	 * @param times
	 * @return
	 */
	public String getHTML_Times(final String url, final String postData, final String encode, final int times) {
		return getHTML_Times(url, postData, encode, 0, new ArrayList<String>(), "", times);
	}

	/**
	 * 抓取目标页面源代码（按次数）
	 *
	 * @param url
	 * @param postData
	 * @param encode
	 * @param timeout
	 * @param times
	 * @return
	 */
	public String getHTML_Times(final String url, final String postData, final String encode, final int timeout, final int times) {
		return getHTML_Times(url, postData, encode, timeout, new ArrayList<String>(), "", times);
	}

	/**
	 * 抓取目标页面源代码（按次数）
	 *
	 * @param url
	 * @param postData
	 * @param encode
	 * @param timeout
	 * @param proxyServers
	 * @param times
	 * @return
	 */
	public String getHTML_Times(final String url, final String postData, final String encode, final int timeout, List<String> proxyServers, final int times) {
		return getHTML_Times(url, postData, encode, timeout, proxyServers, "", times);
	}

	/**
	 * 抓取目标页面源代码（按次数）
	 *
	 * @param url
	 * @param postData
	 * @param encode
	 * @param timeout
	 * @param proxyServers
	 * @param refererUrl
	 * @param times
	 * @return
	 */
	public String getHTML_Times(final String url, final String postData, final String encode, final int timeout, List<String> proxyServers, final String refererUrl, final int times) {
		String result = "";

		// 修正 proxyServers
		proxyServers = CustomListMathUtils.correctionList(proxyServers, times);

		// 执行操作
		try {
			for (int i = 0; i < times; i++) {
				result = getHTML(url, postData, encode, timeout, proxyServers.get(i), refererUrl);

				if (!Strings.isNullOrEmpty(result)) {
					break;
				} else {
					TimeUnit.MILLISECONDS.sleep(1000);
				}
			}
		} catch (Exception ex) {
			s_logger.error(String.format(HttpClientUtils.ERR_MESSAGE_TMPL, url, postData, timeout, proxyServers.toString()), ex);
		}

		return result;
	}

	public void close() {
		try {
			_httpclient.close();
		} catch (IOException e) {
			s_logger.error("close _httpclient", e);
		}
	}
}
