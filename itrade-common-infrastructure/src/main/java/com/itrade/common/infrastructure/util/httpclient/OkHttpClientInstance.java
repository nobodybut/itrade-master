package com.itrade.common.infrastructure.util.httpclient;

import com.google.common.base.Strings;
import com.itrade.common.infrastructure.util.collection.KeyValuePair;
import com.itrade.common.infrastructure.util.enums.ProxyServerSupplierEnum;
import com.itrade.common.infrastructure.util.math.CustomNumberUtils;
import com.itrade.common.infrastructure.util.proxyserver.ProxyServerSupplierUtils;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class OkHttpClientInstance {

	// Logger
	protected static final Logger s_logger = LoggerFactory.getLogger(OkHttpClientInstance.class);

	public static final TrustManager[] trustAllCerts = new TrustManager[]{
			new X509TrustManager() {
				@Override
				public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
				}

				@Override
				public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
				}

				@Override
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return new java.security.cert.X509Certificate[]{};
				}
			}
	};

	public static final SSLContext trustAllSslContext;

	static {
		try {
			trustAllSslContext = SSLContext.getInstance("SSL");
			trustAllSslContext.init(null, trustAllCerts, new java.security.SecureRandom());

		} catch (NoSuchAlgorithmException | KeyManagementException e) {
			throw new RuntimeException(e);
		}
	}

	public static final SSLSocketFactory trustAllSslSocketFactory = trustAllSslContext.getSocketFactory();

	// httpclient
	private OkHttpClient _okHttpclient;
	public static final ConnectionPool connectionPool = new ConnectionPool(200, 60000, TimeUnit.MILLISECONDS);

	public OkHttpClientInstance(String proxy, int timeOut) {
		this._okHttpclient = new OkHttpClient.Builder().sslSocketFactory(trustAllSslSocketFactory, (X509TrustManager) trustAllCerts[0]).hostnameVerifier(new HostnameVerifier() {
			@Override
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		}).connectionPool(connectionPool).proxy(buildProxy(proxy)).connectTimeout(timeOut, TimeUnit.MILLISECONDS).readTimeout(timeOut, TimeUnit.MILLISECONDS).build();
	}

	public OkHttpClientInstance(String proxy) {
		this._okHttpclient = new OkHttpClient.Builder().sslSocketFactory(trustAllSslSocketFactory, (X509TrustManager) trustAllCerts[0]).hostnameVerifier(new HostnameVerifier() {
			@Override
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		}).connectionPool(connectionPool).proxy(buildProxy(proxy)).connectTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build();
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
	public String getHTML(String url, RequestBody postData, String proxyServer, String refererUrl, Map<String, String> additionalHeaders) {
		return OkHttpClientUtils.doGetHTML(_okHttpclient, url, postData, proxyServer, refererUrl, additionalHeaders, true);
	}

	/**
	 * 抓取目标页面源代码
	 *
	 * @param url
	 * @return
	 */
	public String getHTML(String url) {
		return OkHttpClientUtils.doGetHTML(_okHttpclient, url, null, "", "", null, true);
	}

	/**
	 * _okHttpclient 重用新的代理
	 *
	 * @param proxy
	 */
	public void rebuildProxy(String proxy) {
		_okHttpclient = _okHttpclient.newBuilder().proxy(buildProxy(proxy)).build();
	}

	/**
	 * 创建代理
	 *
	 * @param proxyServer
	 * @return
	 */
	public static Proxy buildProxy(String proxyServer) {
		if (!Strings.isNullOrEmpty(proxyServer)) {
			KeyValuePair<ProxyServerSupplierEnum, String> proxyServerKV = ProxyServerSupplierUtils.parseProxyServerKV(proxyServer);

			String[] ipAndPort = proxyServerKV.getValue().split(":");
			if (ipAndPort.length == 2) {
				return new Proxy(Proxy.Type.HTTP, InetSocketAddress.createUnresolved(ipAndPort[0], CustomNumberUtils.toInt(ipAndPort[1])));
			}
		}
		return null;
	}
}
