package com.trade.common.infrastructure.util.httpclient;

import com.google.common.base.Strings;
import com.trade.common.infrastructure.util.proxyserver.ProxyServerStatusUtils;
import okhttp3.*;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.time.LocalTime;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class OkHttpClientUtils {

	// Logger
	protected static final Logger s_logger = LoggerFactory.getLogger(OkHttpClientUtils.class);

	// 相关常量
	public static final String mediaTypeJson = "application/json";

	// s_okHttpClient
	private static final OkHttpClient s_okHttpClient;

	static {
		s_okHttpClient = new OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).readTimeout(180, TimeUnit.SECONDS).build();
	}

	/**
	 * 抓取目标页面源代码（GET）
	 *
	 * @param url
	 * @return
	 */
	public static String getHTML(String url) {
		return doGetHTML(s_okHttpClient, url, null, "", "", null, true);
	}

	public static String postJson(String postUrl, String json) {
		String result = "";

		Request.Builder builder = new Request.Builder();
		builder.url(postUrl);

		//设置 postData
		if (json != null) {
			builder.post(RequestBody.create(MediaType.parse("application/json"), json));
		}

		try {
			Request request = builder.build();
			Response response = s_okHttpClient.newCall(request).execute();

			if (response.isSuccessful()) {
				result = response.body().string();
			}

		} catch (SocketTimeoutException ex) {
			if (ex != null) {
				s_logger.error("ConnectTimeoutException:" + calErrorLogInfo(postUrl, null, ex.toString()));
			}
		} catch (ConnectTimeoutException ex) {
			if (ex != null) {
				s_logger.error("ConnectTimeoutException:" + calErrorLogInfo(postUrl, null, ex.toString()));
			}
		} catch (HttpHostConnectException ex) {
			if (ex != null) {
				s_logger.error("HttpHostConnectException:" + calErrorLogInfo(postUrl, null, ex.toString()));
			}
		} catch (Exception ex) {
			s_logger.error("HttpHostConnectException:" + calErrorLogInfo(postUrl, null, ex.toString()));
		}

		return result;
	}

	/**
	 * 抓取目标页面源代码
	 *
	 * @param url
	 * @param postData
	 * @param proxyServer
	 * @param refererUrl
	 * @param additionalHeaders
	 * @return
	 */
	public static String getHTML(String url, RequestBody postData, String proxyServer, String refererUrl, Map<String, String> additionalHeaders) {
		return OkHttpClientUtils.doGetHTML(s_okHttpClient, url, postData, proxyServer, refererUrl, additionalHeaders, true);
	}

	/**
	 * 执行抓取目标页面源代码
	 *
	 * @param okHttpClient
	 * @param postUrl
	 * @param postData
	 * @param proxyServer
	 * @param refererUrl
	 * @param additionalHeaders
	 * @param isInitBrowserHeader
	 * @return
	 */
	public static String doGetHTML(OkHttpClient okHttpClient, final String postUrl, final RequestBody postData,
	                               final String proxyServer, final String refererUrl, final Map<String, String> additionalHeaders,
	                               boolean isInitBrowserHeader) {
		String result = "";

		// 计算相关变量
		LocalTime startTime = LocalTime.now();
		boolean hasAdditionalHeaders = additionalHeaders != null && additionalHeaders.size() > 0;
		boolean isRemoveProxyServer = false;

		Request.Builder builder = new Request.Builder();
		builder.url(postUrl);

		//设置 postData
		if (postData != null) {
			builder.post(postData);
		}

		// 设置其他 Header
		if (isInitBrowserHeader) {
//			for (Header header : HttpClientUtils.s_headers) {
//				builder.addHeader(header.getName(), header.getValue());
//			}
		}

		if (hasAdditionalHeaders) {
			for (Map.Entry<String, String> entry : additionalHeaders.entrySet()) {
				builder.addHeader(entry.getKey(), entry.getValue());
			}
		}

		if (!refererUrl.equals("")) {
			builder.addHeader("Referer", refererUrl);
		}

		try {
			Request request = builder.build();
			Response response = okHttpClient.newCall(request).execute();

			if (response.isSuccessful()) {
				result = response.body().string();
			}

		} catch (SocketTimeoutException ex) {
			if (ex != null) {
				isRemoveProxyServer = true;
				s_logger.error("ConnectTimeoutException:" + calErrorLogInfo(postUrl, proxyServer, ex.toString()));
			}
		} catch (ConnectTimeoutException ex) {
			if (ex != null) {
				isRemoveProxyServer = true;
				s_logger.error("ConnectTimeoutException:" + calErrorLogInfo(postUrl, proxyServer, ex.toString()));
			}
		} catch (HttpHostConnectException ex) {
			if (ex != null) {
				isRemoveProxyServer = true;
				s_logger.error("HttpHostConnectException:" + calErrorLogInfo(postUrl, proxyServer, ex.toString()));
			}
		} catch (Exception ex) {
			s_logger.error("HttpHostConnectException:" + calErrorLogInfo(postUrl, proxyServer, ex.toString()));
		}

		//记录proxy状态
		ProxyServerStatusUtils.recordProxyStatus(postUrl, proxyServer, isRemoveProxyServer, startTime, s_logger);

		return result;
	}

	public static InputStream getStream(final String url) {
		Request.Builder builder = new Request.Builder();
		builder.url(url);

		try {
			Request request = builder.build();
			Response response = s_okHttpClient.newCall(request).execute();

			if (response.isSuccessful()) {
				return response.body().byteStream();
			}
		} catch (Exception ex) {
			s_logger.error("HttpHostConnectException:" + url);
		}
		return null;
	}

	/**
	 * 创建 RequestBody
	 *
	 * @param mediaType
	 * @param postData
	 * @return
	 */
	public static RequestBody buildRequestBody(String mediaType, String postData) {
		return RequestBody.create(MediaType.parse(mediaType), postData);
	}

	/**
	 * 创建出错时的错误信息
	 *
	 * @param postUrl
	 * @param proxyServer
	 * @param exString
	 * @return
	 */
	public static String calErrorLogInfo(final String postUrl, final String proxyServer, final String exString) {
		StringBuilder sBuilder = new StringBuilder();

		sBuilder.append("postUrl=\"");
		sBuilder.append(postUrl);
		sBuilder.append("\", proxyServer=\"");
		sBuilder.append(proxyServer);
		sBuilder.append("\"");

		if (!Strings.isNullOrEmpty(exString)) {
			sBuilder.append(", exString=\"");
			sBuilder.append(exString);
			sBuilder.append("\"");
		}

		return sBuilder.toString();
	}

	/**
	 * OkHttpClient downloadFile
	 *
	 * @param saveFullPath
	 * @param downloadUrl
	 */
	public static void downloadFile(String saveFullPath, String downloadUrl) {
		try {
			Request request = new Request.Builder().url(downloadUrl).build();
			Response response = s_okHttpClient.newCall(request).execute();

			if (!response.isSuccessful()) {
				s_logger.error(String.format("Failed to download file: response=%s", response));
				return;
			}

			File f = new File(saveFullPath);
			if (!f.getParentFile().exists()) {
				f.getParentFile().mkdirs();
			}

			FileOutputStream fos = new FileOutputStream(saveFullPath);
			fos.write(response.body().bytes());
			fos.close();
		} catch (Exception ex) {
			s_logger.error(String.format("downloadFileSync error=%s", ex.toString()));
		}
	}

	/**
	 * OkHttpClient downloadFileBytes
	 *
	 * @param downloadUrl
	 */
	public static byte[] downloadFileBytes(String downloadUrl) {
		byte[] result = null;
		try {
			Request request = new Request.Builder().url(downloadUrl).build();
			Response response = s_okHttpClient.newCall(request).execute();

			if (!response.isSuccessful()) {
				s_logger.error(String.format("Failed to download file: response=%s", response));
				return null;
			}

			result = response.body().bytes();
		} catch (Exception ex) {
			s_logger.error(String.format("downloadFileSync error=%s", ex.toString()));
		}

		return result;
	}
}
