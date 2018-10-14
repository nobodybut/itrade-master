package com.trade.common.infrastructure.util.http;

import java.net.HttpURLConnection;
import java.net.URL;

public class HttpFileUtils {

	/**
	 * 检查 HTTP 远程文件是否存在
	 *
	 * @param fileUrl
	 * @return
	 */
	public static boolean checkHttpFileExists(String fileUrl) {
		try {
			HttpURLConnection.setFollowRedirects(false);
			HttpURLConnection con = (HttpURLConnection) new URL(fileUrl).openConnection();
			con.setRequestMethod("HEAD");
			return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
