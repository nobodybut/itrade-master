package com.trade.common.infrastructure.util.httpclient;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.trade.common.infrastructure.util.logger.LogInfoUtils;
import com.trade.common.infrastructure.util.timeout.AbstractTimeoutMethod;
import org.apache.http.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HttpClientTimeOutInstance extends AbstractTimeoutMethod {

	// Logger
	private static final Logger s_logger = LoggerFactory.getLogger(HttpClientTimeOutInstance.class);

	// 相关变量
	private HttpClientInstance _httpClientInstance;
	private String _postUrl = "";
	private String _postData = "";
	private String _encode = "";
	private int _timeout = 0;
	private String _proxyServer = "";
	private String _refererUrl = "";
	private Map<String, String> _additionalHeaders = Maps.newHashMap();
	private List<Header> _responseHeaders = Lists.newArrayList();

	public HttpClientTimeOutInstance(String postUrl, String postData, String encode, int timeout, String proxyServer) {
		super();
		this._httpClientInstance = new HttpClientInstance();
		this._postUrl = postUrl;
		this._postData = postData;
		this._encode = encode;
		this._timeout = timeout;
		this._proxyServer = proxyServer;
	}

	public HttpClientTimeOutInstance(String postUrl, String postData, String encode, int timeout, String proxyServer, String refererUrl) {
		super();
		this._httpClientInstance = new HttpClientInstance();
		this._postUrl = postUrl;
		this._postData = postData;
		this._encode = encode;
		this._timeout = timeout;
		this._proxyServer = proxyServer;
		this._refererUrl = refererUrl;
	}

	public HttpClientTimeOutInstance(String postUrl, String postData, String encode, int timeout, String proxyServer, String refererUrl, final Map<String, String> additionalHeaders) {
		super();
		this._httpClientInstance = new HttpClientInstance();
		this._postUrl = postUrl;
		this._postData = postData;
		this._encode = encode;
		this._timeout = timeout;
		this._proxyServer = proxyServer;
		this._refererUrl = refererUrl;
		this._additionalHeaders = additionalHeaders;
	}

	public HttpClientTimeOutInstance(HttpClientInstance httpClientInstance, String postUrl, String postData, String encode, int timeout, String proxyServer) {
		super();
		this._httpClientInstance = httpClientInstance;
		this._postUrl = postUrl;
		this._postData = postData;
		this._encode = encode;
		this._timeout = timeout;
		this._proxyServer = proxyServer;
	}

	public HttpClientTimeOutInstance(HttpClientInstance httpClientInstance, String postUrl, String postData, String encode, int timeout, String proxyServer, String refererUrl) {
		super();
		this._httpClientInstance = httpClientInstance;
		this._postUrl = postUrl;
		this._postData = postData;
		this._encode = encode;
		this._timeout = timeout;
		this._proxyServer = proxyServer;
		this._refererUrl = refererUrl;
	}

	public HttpClientTimeOutInstance(HttpClientInstance httpClientInstance, String postUrl, String postData, String encode, int timeout, String proxyServer, String refererUrl, final Map<String, String> additionalHeaders) {
		super();
		this._httpClientInstance = httpClientInstance;
		this._postUrl = postUrl;
		this._postData = postData;
		this._encode = encode;
		this._timeout = timeout;
		this._proxyServer = proxyServer;
		this._refererUrl = refererUrl;
		this._additionalHeaders = additionalHeaders;
	}

	public HttpClientTimeOutInstance(HttpClientInstance httpClientInstance, String postUrl, String postData, String encode, int timeout, String proxyServer, String refererUrl, List<Header> responseHeaders) {
		super();
		this._httpClientInstance = httpClientInstance;
		this._postUrl = postUrl;
		this._postData = postData;
		this._encode = encode;
		this._timeout = timeout;
		this._proxyServer = proxyServer;
		this._refererUrl = refererUrl;
		this._responseHeaders = responseHeaders;
	}

	@Override
	protected String execute() {
		if (Strings.isNullOrEmpty(_encode)) {
			_encode = "utf-8";
		}

		return _httpClientInstance.getHTML(_postUrl, _postData, _encode, _timeout, _proxyServer, _refererUrl, _additionalHeaders, _responseHeaders);
	}

	/**
	 * 抓取目标页面源代码
	 *
	 * @return
	 */
	public String getHTML() {
		return super.execute(_timeout);
	}

	/**
	 * 抓取目标页面源代码（按次数）
	 *
	 * @param times
	 * @return
	 */
	public String getHTML_Times(final int times) {
		String result = "";

		try {
			for (int i = 0; i < times; i++) {
				result = getHTML();

				if (!Strings.isNullOrEmpty(result)) {
					break;
				} else {
					TimeUnit.MILLISECONDS.sleep(1000);
				}
			}
		} catch (Exception ex) {
			s_logger.error(String.format(LogInfoUtils.NO_DATA_TMPL, Thread.currentThread().getStackTrace()[1].getMethodName()), ex);
		}

		return result;
	}
}
