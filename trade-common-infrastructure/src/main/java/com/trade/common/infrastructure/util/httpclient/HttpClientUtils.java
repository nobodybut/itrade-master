package com.trade.common.infrastructure.util.httpclient;

import com.google.common.base.Strings;
import com.trade.common.infrastructure.util.collection.CustomListUtils;
import com.trade.common.infrastructure.util.collection.KeyValuePair;
import com.trade.common.infrastructure.util.enums.ProxyServerSupplierEnum;
import com.trade.common.infrastructure.util.proxyserver.ProxyServerStatusUtils;
import com.trade.common.infrastructure.util.proxyserver.ProxyServerSupplierUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.http.*;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpClientUtils {

	// Logger
	protected static final Logger s_logger = LoggerFactory.getLogger(HttpClientUtils.class);

	// 相关变量
	private static PoolingHttpClientConnectionManager s_connectionManager = null;
	private static ConnectionKeepAliveStrategy s_connectionKeepAliveStrategy = null;
	private static Header[] s_headers = null;

	// 相关常量
	public static final String ERR_MESSAGE_TMPL = "getHTML错误，url=[%s]，postData=[%s]，timeout=[%s]，proxyServer=[%s] ...";

	// TimeOut时长
	public static final int minTimeOutMilis = 30000;
	public static final int maxTimeOutMilis = 60000;

	// 特殊 http 头
	public static final Map<String, String> xmlHttpHeader = new HashMap<String, String>() {
		{
			put("X-Requested-With", "XMLHttpRequest");
		}
	};

	public static final Map<String, String> appJsonHttpHeader = new HashMap<String, String>() {
		{
			put("Content-Type", "application/json");
		}
	};

	public static final Map<String, String> brAcceptEncodingHttpHeader = new HashMap<String, String>() {
		{
			put("Accept-Encoding", "gzip,deflate,sdch,br");
		}
	};

	public static final Map<String, String> upgradeInsecureRequestsHttpHeader = new HashMap<String, String>() {
		{
			put("Upgrade-Insecure-Requests", "1");
		}
	};

	static {
		// ========== s_httpclient ==========
		createPoolingHttpClientConnectionManager();
		createConnectionKeepAliveStrategy();

		// ========== s_headers ==========
		s_headers = new Header[6];
		s_headers[0] = new BasicHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		s_headers[1] = new BasicHeader("Accept-Charset", "GB2312,utf-8;q=0.7,*;q=0.7");
		s_headers[2] = new BasicHeader("Accept-Language", "zh,zh-CN;q=0.8,en-US;q=0.6,en;q=0.4");
		s_headers[3] = new BasicHeader("Accept-Encoding", "gzip,deflate,sdch"); // gzip,deflate,sdch,br
		s_headers[4] = new BasicHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36");
		s_headers[5] = new BasicHeader("Connection", "keep-alive");
	}

	/**
	 * 创建 PoolingHttpClientConnectionManager
	 *
	 * @return
	 */
	public static PoolingHttpClientConnectionManager createPoolingHttpClientConnectionManager() {
		if (s_connectionManager == null) {
			PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
			cm.setMaxTotal(2000);
			cm.setDefaultMaxPerRoute(200);
			cm.setMaxPerRoute(new HttpRoute(new HttpHost("locahost", 80)), 50);
			s_connectionManager = cm;
		}

		return s_connectionManager;
	}

	/**
	 * 从线程池中获取 CloseableHttpClient
	 *
	 * @return
	 */
	public static CloseableHttpClient getHttpClient() {
		return HttpClients.custom().setConnectionManager(s_connectionManager).setConnectionManagerShared(true).setKeepAliveStrategy(s_connectionKeepAliveStrategy).build();
	}

	/**
	 * 从线程池中获取 CloseableHttpClient
	 *
	 * @param cookieStore
	 * @return
	 */
	public static CloseableHttpClient getHttpClient(CookieStore cookieStore) {
		return HttpClients.custom().setDefaultCookieStore(cookieStore).setConnectionManager(s_connectionManager).setConnectionManagerShared(true).setKeepAliveStrategy(s_connectionKeepAliveStrategy).build();
	}

	/**
	 * 创建 ConnectionKeepAliveStrategy
	 *
	 * @return
	 */
	public static ConnectionKeepAliveStrategy createConnectionKeepAliveStrategy() {
		if (s_connectionKeepAliveStrategy == null) {
			s_connectionKeepAliveStrategy = new ConnectionKeepAliveStrategy() {
				public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
					// Honor 'keep-alive' header
					HeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
					while (it.hasNext()) {
						HeaderElement he = it.nextElement();
						String param = he.getName();
						String value = he.getValue();
						if (value != null && param.equalsIgnoreCase("timeout")) {
							try {
								return Long.parseLong(value) * 1000;
							} catch (NumberFormatException ignore) {
							}
						}
					}

//				HttpHost target = (HttpHost) context.getAttribute(
//						HttpClientContext.HTTP_TARGET_HOST);
//				if ("www.naughty-server.com".equalsIgnoreCase(target.getHostName())) {
//					// Keep alive for 5 seconds only
//					return 5 * 1000;
//				} else {
//					// otherwise keep alive for 30 seconds
//					return 30 * 1000;
//				}

					return 30 * 1000;
				}
			};
		}

		return s_connectionKeepAliveStrategy;
	}

	/**
	 * 抓取目标页面源代码
	 *
	 * @param url
	 * @return
	 */
	public static String getHTML(final String url) {
		return doGetHTML(getHttpClient(), url, "", Consts.UTF_8, 0, "", "", null, null, true, "", null, "", "");
	}

	/**
	 * 抓取目标页面源代码
	 *
	 * @param url
	 * @param encode
	 * @return
	 */
	public static String getHTML(final String url, final String encode) {
		return doGetHTML(getHttpClient(), url, "", Charset.forName(encode), 0, "", "", null, null, true, "", null, "", "");
	}

	/**
	 * 抓取目标页面源代码
	 *
	 * @param url
	 * @param postData
	 * @param encode
	 * @return
	 */
	public static String getHTML(final String url, final String postData, final String encode) {
		return doGetHTML(getHttpClient(), url, postData, Charset.forName(encode), 0, "", "", null, null, true, "", null, "", "");
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
	public static String getHTML(final String url, final String postData, final String encode, final int timeout) {
		return doGetHTML(getHttpClient(), url, postData, Charset.forName(encode), timeout, "", "", null, null, true, "", null, "", "");
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
	public static String getHTML(final String url, final String postData, final String encode, final int timeout, String name, byte[] fileByte, String contentType, String fileName) {
		return doGetHTML(getHttpClient(), url, postData, Charset.forName(encode), timeout, "", "", null, null, true, name, fileByte, contentType, fileName);
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
	public static String getHTML(final String url, final String postData, final String encode, final String proxyServer) {
		return doGetHTML(getHttpClient(), url, postData, Charset.forName(encode), 0, proxyServer, "", null, null, true, "", null, "", "");
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
	public static String getHTML(final String url, final String postData, final String encode, final int timeout, final String proxyServer) {
		return doGetHTML(getHttpClient(), url, postData, Charset.forName(encode), timeout, proxyServer, "", null, null, true, "", null, "", "");
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
	public static String getHTML(final String url, final String postData, final String encode, final int timeout, final String proxyServer, final String refererUrl) {
		return doGetHTML(getHttpClient(), url, postData, Charset.forName(encode), timeout, proxyServer, refererUrl, null, null, true, "", null, "", "");
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
	public static String getHTML(final String url, final String postData, final String encode, final int timeout, final String proxyServer, final String refererUrl, final Map<String, String> additionalHeaders) {
		return doGetHTML(getHttpClient(), url, postData, Charset.forName(encode), timeout, proxyServer, refererUrl, additionalHeaders, null, true, "", null, "", "");
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
	public static String getHTML(final String url, final String postData, final String encode, final int timeout, final String proxyServer, final String refererUrl, final Map<String, String> additionalHeaders, boolean isInitBrowserHeader) {
		return doGetHTML(getHttpClient(), url, postData, Charset.forName(encode), timeout, proxyServer, refererUrl, additionalHeaders, null, isInitBrowserHeader, "", null, "", "");
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
	public static String getHTML(final String url, final String postData, final String encode, final int timeout, final String proxyServer, final String refererUrl, final List<Header> responseHeaders) {
		return doGetHTML(getHttpClient(), url, postData, Charset.forName(encode), timeout, proxyServer, refererUrl, null, responseHeaders, true, "", null, "", "");
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
	public static String getHTML(final String url, final String postData, final String encode, final int timeout, final String proxyServer, final String refererUrl, final List<Header> responseHeaders, boolean isInitBrowserHeader) {
		return doGetHTML(getHttpClient(), url, postData, Charset.forName(encode), timeout, proxyServer, refererUrl, null, responseHeaders, isInitBrowserHeader, "", null, "", "");
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
	public static String getHTML(final String url, final String postData, final String encode, final int timeout, final String proxyServer, final String refererUrl, final Map<String, String> additionalHeaders, List<Header> responseHeaders) {
		return doGetHTML(getHttpClient(), url, postData, Charset.forName(encode), timeout, proxyServer, refererUrl, additionalHeaders, responseHeaders, true, "", null, "", "");
	}

//	/**
//	 * 抓取目标页面源代码
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
//	public static byte[] getBytes(final String url, final String postData, final String encode, final int timeout, final String proxyServer, final String refererUrl, final Map<String, String> additionalHeaders, List<Header> responseHeaders) {
//		return doGetBytes(getHttpClient(), url, postData, Charset.forName(encode), timeout, proxyServer, refererUrl, additionalHeaders, responseHeaders, true);
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
	public static String getHTML(final String url, final String postData, final String encode, final int timeout, final String proxyServer, final String refererUrl, final Map<String, String> additionalHeaders, List<Header> responseHeaders, boolean isInitBrowserHeader) {
		return doGetHTML(getHttpClient(), url, postData, Charset.forName(encode), timeout, proxyServer, refererUrl, additionalHeaders, responseHeaders, isInitBrowserHeader, "", null, "", "");
	}

	/**
	 * 执行抓取目标页面源代码
	 *
	 * @param client
	 * @param postUrl
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
	@SuppressWarnings("deprecation")
	public static String doGetHTML(CloseableHttpClient client, final String postUrl, final String postData, final Charset encode, final int timeout,
	                               final String proxyServer, final String refererUrl, final Map<String, String> additionalHeaders,
	                               List<Header> responseHeaders, boolean isInitBrowserHeader, String name, byte[] fileByte, String contentType, String fileName) {
		String result = "";

		// 计算相关变量
		LocalTime startTime = LocalTime.now();
		boolean isPost = !Strings.isNullOrEmpty(postData);
		boolean hasProxy = !Strings.isNullOrEmpty(proxyServer);
		boolean hasAdditionalHeaders = additionalHeaders != null && additionalHeaders.size() > 0;
		KeyValuePair<ProxyServerSupplierEnum, String> proxyServerKV = ProxyServerSupplierUtils.parseProxyServerKV(proxyServer);
		String curProxyServer = proxyServerKV.getValue();
		String[] arrProxy = calProxyServer(hasProxy, curProxyServer);
		int connTimeOut = (timeout > 0) ? timeout : 30000;
		HttpRequestBase httpRequest = null;
		boolean isRemoveProxyServer = false;

		try {
			// 生成 HttpRequest，并抓取数据
			httpRequest = (isPost) ? new HttpPost(postUrl) : new HttpGet(postUrl);
			if (!isPost) {
				httpRequest.getParams().setParameter("http.protocol.allow-circular-redirects", true);
			}

			// 设置其他 Header
			if (isInitBrowserHeader) {
				if (hasAdditionalHeaders) {
					for (Header header : s_headers) {
						if (!additionalHeaders.containsKey(header.getName())) {
							httpRequest.setHeader(header);
						}
					}
				} else {
					httpRequest.setHeaders(s_headers);
				}
			}
			if (!refererUrl.equals("")) {
				httpRequest.setHeader("Referer", refererUrl);
			}
			if (hasProxy) {
				httpRequest.setHeader("X_FORWARDED_FOR", curProxyServer);
			}
			if (hasAdditionalHeaders) {
				for (Map.Entry<String, String> entry : additionalHeaders.entrySet()) {
					httpRequest.setHeader(entry.getKey(), entry.getValue());
				}
			}

			// 设置 RequestConfig
			HttpHost proxy = (hasProxy && arrProxy != null) ? new HttpHost(arrProxy[0], NumberUtils.toInt(arrProxy[1])) : null;
			httpRequest.setConfig(RequestConfig.custom().setProxy(proxy).setSocketTimeout(connTimeOut).setConnectTimeout(connTimeOut).setConnectionRequestTimeout(connTimeOut).setExpectContinueEnabled(false).build());

			// 设置 POST 数据
			if (isPost) {
				if (fileByte != null && fileByte.length > 0) {
					MultipartEntityBuilder muliEntity = MultipartEntityBuilder.create();

					//处理文件
					muliEntity.addBinaryBody(name, fileByte, ContentType.create(contentType), fileName);

					//处理其他post参数
					String[] paramArr = postData.split("&");
					for (String param : paramArr) {
						if (param.contains("=")) {
							String[] nameTexts = param.split("=");
							if (nameTexts.length == 2) {
								muliEntity.addTextBody(nameTexts[0], nameTexts[1]);
							}
						}
					}

					HttpEntity entity = muliEntity.build();
					((HttpPost) httpRequest).setEntity(entity);

				} else {
					StringEntity reqEntity = new StringEntity(postData, encode);
					reqEntity.setContentType((additionalHeaders != null && additionalHeaders.containsKey("Content-Type")) ? additionalHeaders.get("Content-Type") : "application/x-www-form-urlencoded");
					((HttpPost) httpRequest).setEntity(reqEntity);
				}
			}

			// 获取 HttpResponse
			CloseableHttpResponse response = client.execute(httpRequest);
			try {
				// 处理 responseHeaders 返回数据
				if (responseHeaders != null) {
					responseHeaders.addAll(CustomListUtils.arrayToList(response.getAllHeaders()));
				}
				// 执行请求
				HttpEntity entity = response.getEntity(); // 获取响应实体
				try {
					if (entity != null) {
						// 处理 Gzip
						Header header = entity.getContentEncoding();
						if (header != null) {
							HeaderElement[] codecs = header.getElements();
							int size = codecs.length;
							for (int i = 0; i < size; i++) {
								if (codecs[i].getName().equalsIgnoreCase("gzip")) {
									response.setEntity(new GzipDecompressingEntity(entity));
								}
							}
						}

						// 处理返回数据
						result = EntityUtils.toString(entity, encode);
					}
				} finally {
					EntityUtils.consumeQuietly(entity);
				}
			} finally {
				if (response != null) {
					response.close();
				}
			}
		} catch (NoRouteToHostException ex) {
			if (ex != null) {
				isRemoveProxyServer = true;
				String errorLogInfo = calErrorLogInfo(postUrl, timeout, curProxyServer, ex.toString());
				if (!Strings.isNullOrEmpty(errorLogInfo)) {
					s_logger.error("NoRouteToHostException:" + errorLogInfo);
				}
			}
		} catch (ConnectTimeoutException ex) {
			if (ex != null) {
				isRemoveProxyServer = true;
				String errorLogInfo = calErrorLogInfo(postUrl, timeout, curProxyServer, ex.toString());
				if (!Strings.isNullOrEmpty(errorLogInfo)) {
					s_logger.error("ConnectTimeoutException:" + errorLogInfo);
				}
			}
		} catch (SocketTimeoutException ex) {
			if (ex != null) {
				isRemoveProxyServer = true;
				String errorLogInfo = calErrorLogInfo(postUrl, timeout, curProxyServer, ex.toString());
				if (!Strings.isNullOrEmpty(errorLogInfo)) {
					s_logger.error("SocketTimeoutException:" + errorLogInfo);
				}
			}
		} catch (HttpHostConnectException ex) {
			if (ex != null) {
				isRemoveProxyServer = true;
				String errorLogInfo = calErrorLogInfo(postUrl, timeout, curProxyServer, ex.toString());
				if (!Strings.isNullOrEmpty(errorLogInfo)) {
					s_logger.error("HttpHostConnectException:" + errorLogInfo);
				}
			}
		} catch (UnsupportedEncodingException ex) {
			if (ex != null) {
				String errorLogInfo = calErrorLogInfo(postUrl, timeout, curProxyServer, ex.toString());
				if (!Strings.isNullOrEmpty(errorLogInfo)) {
					s_logger.error("UnsupportedEncodingException:" + errorLogInfo);
				}
			}
		} catch (Exception ex) {
			if (ex != null) {
				String errorLogInfo = calErrorLogInfo(postUrl, timeout, curProxyServer, ex.toString());
				if (!Strings.isNullOrEmpty(errorLogInfo)) {
					s_logger.error("Exception:" + errorLogInfo);
				}
			}
		} finally {
			if (httpRequest != null) {
				httpRequest.releaseConnection();
			}
		}

		//记录proxy状态
		ProxyServerStatusUtils.recordProxyStatus(postUrl, proxyServer, isRemoveProxyServer, startTime, s_logger);

		return result;
	}

	/**
	 * 计算 ProxyServer
	 *
	 * @param haveProxy
	 * @param proxyServer
	 * @return
	 */
	private static String[] calProxyServer(boolean haveProxy, String proxyServer) {
		String[] result = null;

		if (haveProxy) {
			result = StringUtils.split(proxyServer, ":");
			if (result.length != 2) {
				result = null;
			}
		}

		return result;
	}

	/**
	 * 创建出错时的错误信息
	 *
	 * @param postUrl
	 * @param timeout
	 * @param proxyServer
	 * @param exString
	 * @return
	 */
	public static String calErrorLogInfo(final String postUrl, final int timeout, final String proxyServer, final String exString) {
		if (!exString.contains("Connect to " + proxyServer)) {
			StringBuilder sBuilder = new StringBuilder();

			sBuilder.append("postUrl=\"");
			sBuilder.append(postUrl);
			sBuilder.append("\", timeout=");
			sBuilder.append(String.valueOf(timeout));
			sBuilder.append(", proxyServer=\"");
			sBuilder.append(proxyServer);
			sBuilder.append("\"");

			if (!Strings.isNullOrEmpty(exString)) {
				sBuilder.append(", exString=\"");
				sBuilder.append(exString);
				sBuilder.append("\"");
			}

			return sBuilder.toString();
		} else {
			return "";
		}
	}

	/**
	 * 获取 Http Response Header 某一项的值
	 *
	 * @param responseHeaders
	 * @param headerName
	 * @return
	 */
	public static String getHttpResponseHeader(List<Header> responseHeaders, String headerName) {
		for (Header responseHeader : responseHeaders) {
			if (responseHeader.getName().equals(headerName)) {
				return responseHeader.getValue();
			}
		}

		return "";
	}

	/**
	 * 获取 Http Response Header Cookie
	 *
	 * @param responseHeaders
	 * @return
	 */
	public static String getHttpResponseHeaderCookie(List<Header> responseHeaders) {
		StringBuilder stringBuilder = new StringBuilder();
		for (Header responseHeader : responseHeaders) {
			if (responseHeader.getName().equals("Set-Cookie")) {
				stringBuilder.append(responseHeader.getValue());
				stringBuilder.append(";");
			}
		}

		return stringBuilder.toString();
	}

	/**
	 * 获取 Http Response Header Location的值
	 *
	 * @param responseHeaders
	 * @return
	 */
	public static String getHttpResponseHeaderLocation(List<Header> responseHeaders) {
		return getHttpResponseHeader(responseHeaders, "Location");
	}
}