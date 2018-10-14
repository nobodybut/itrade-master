package com.trade.common.infrastructure.util.http;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URLEncoder;
import java.util.Map;

public class HttpFileDownloadUtils {

	// logger
	private static final Logger logger = LoggerFactory.getLogger(HttpFileDownloadUtils.class);

	/**
	 * 下载文件保存到本地
	 *
	 * @param saveFullPath 文件保存位置
	 * @param httpFileUrl  网络文件文件地址
	 * @throws IOException
	 */
	@SuppressWarnings("deprecation")
	public static void downloadFile(String saveFullPath, String httpFileUrl) throws IOException {
		HttpClient client = null;
		try {
			client = HttpClients.createDefault();
			HttpGet httpGet = getHttpGet(httpFileUrl, null, null);
			HttpResponse response = client.execute(httpGet);

			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				byte[] result = EntityUtils.toByteArray(response.getEntity());
				BufferedOutputStream bw = null;

				try {
					// 创建文件对象（如果文件所在文件夹不存在则创建新的文件夹）

					File f = new File(saveFullPath);
					if (!f.getParentFile().exists()) {
						f.getParentFile().mkdirs();
					}

					// 写入文件
					bw = new BufferedOutputStream(new FileOutputStream(saveFullPath));
					bw.write(result);
				} catch (Exception e) {
					logger.error("保存文件错误,saveFullPath=" + saveFullPath + ",httpFileUrl=" + httpFileUrl, e);
				} finally {
					try {
						if (bw != null) {
							bw.close();
						}
					} catch (Exception e) {
						logger.error("finally BufferedOutputStream shutdown close", e);
					}
				}
			} else {
				StringBuffer errorMsg = new StringBuffer();
				errorMsg.append("httpStatus:");
				errorMsg.append(response.getStatusLine().getStatusCode());
				errorMsg.append(response.getStatusLine().getReasonPhrase());
				errorMsg.append(", Header: ");
				Header[] headers = response.getAllHeaders();
				for (Header header : headers) {
					errorMsg.append(header.getName());
					errorMsg.append(":");
					errorMsg.append(header.getValue());
				}
				logger.error("HttpResonse Error:" + errorMsg);
			}
		} catch (ClientProtocolException e) {
			logger.error("下载文件保存到本地,http连接异常,saveFullPath=" + saveFullPath + ",httpFileUrl=" + httpFileUrl, e);
			throw e;
		} catch (IOException e) {
			logger.error("下载文件保存到本地,文件操作异常,saveFullPath=" + saveFullPath + ",httpFileUrl=" + httpFileUrl, e);
			throw e;
		} finally {
			try {
				client.getConnectionManager().shutdown();
			} catch (Exception e) {
				logger.error("finally HttpClient shutdown error", e);
			}
		}
	}

	/**
	 * 获得HttpGet对象
	 *
	 * @param url    请求地址
	 * @param params 请求参数
	 * @param encode 编码方式
	 * @return HttpGet对象
	 */
	private static HttpGet getHttpGet(String url, Map<String, String> params, String encode) {
		StringBuffer buf = new StringBuffer(url);
		if (params != null) {
			// 地址增加?或者&
			String flag = (url.indexOf('?') == -1) ? "?" : "&";

			// 添加参数
			for (String name : params.keySet()) {
				buf.append(flag);
				buf.append(name);
				buf.append("=");
				try {
					String param = params.get(name);
					if (param == null) {
						param = "";
					}
					buf.append(URLEncoder.encode(param, encode));
				} catch (UnsupportedEncodingException e) {
					logger.error("URLEncoder Error,encode=" + encode + ",param=" + params.get(name), e);
				}
				flag = "&";
			}
		}
		HttpGet httpGet = new HttpGet(buf.toString());
		return httpGet;
	}

	/**
	 * 读取 http文件 文件名
	 *
	 * @param httpFileUrl
	 * @return
	 */
	public static String getHttpFileName(String httpFileUrl) {
		String fileName = httpFileUrl.substring(httpFileUrl.lastIndexOf("/") + 1);
		if (fileName.contains("?")) {
			fileName = fileName.split("\\?")[0];
		}
		return fileName;
	}

	public static void main(String[] args) {
		System.out.println(getHttpFileName("http://pix1.agoda.net/hotelImages/433/433329/433329_14121817350024049139.jpg?s=1100x825"));
	}
}
