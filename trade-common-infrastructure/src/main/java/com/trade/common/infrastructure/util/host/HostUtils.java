package com.trade.common.infrastructure.util.host;

import com.trade.common.infrastructure.util.httpclient.HttpClientTimeOutUtils;
import com.trade.common.infrastructure.util.logger.LogInfoUtils;
import com.trade.common.infrastructure.util.string.CustomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class HostUtils {

	// 日志记录
	private static final Logger s_logger = LoggerFactory.getLogger(HostUtils.class);

	// 相关常量
	private static final boolean isLinuxOS = true;

	/**
	 * 取得本地IP linux下 和 window下可用 add by RWW
	 *
	 * @return
	 */
	public static String getLocalIP() {
		String sIP = "";
		InetAddress ip = null;
		try {
			// 如果是Windows操作系统
			if (!isLinuxOS) {
				ip = InetAddress.getLocalHost();
			}
			// 如果是Linux操作系统
			else {
				boolean bFindIP = false;
				Enumeration<NetworkInterface> netInterfaces = (Enumeration<NetworkInterface>) NetworkInterface.getNetworkInterfaces();
				while (netInterfaces.hasMoreElements()) {
					if (bFindIP) {
						break;
					}
					NetworkInterface ni = (NetworkInterface) netInterfaces.nextElement();
					// ----------特定情况，可以考虑用ni.getName判断
					// 遍历所有ip
					Enumeration<InetAddress> ips = ni.getInetAddresses();
					while (ips.hasMoreElements()) {
						ip = (InetAddress) ips.nextElement();
						if (ip.isSiteLocalAddress() && !ip.isLoopbackAddress() // 127.开头的都是lookback地址
								&& ip.getHostAddress().indexOf(":") == -1) {
							bFindIP = true;
							break;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			s_logger.error(String.format(LogInfoUtils.HAS_DATA_TMPL, Thread.currentThread().getStackTrace()[1].getMethodName(), "MultiThreadedTrapReceiver 453: " + e.getMessage()));
		}
		if (null != ip) {
			sIP = ip.getHostAddress();
		}
		return sIP;
	}

	/**
	 * 取得客户端IP
	 *
	 * @param request
	 * @return
	 */
	public static String getClientIP(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	/**
	 * 获取公网IP
	 *
	 * @return
	 */
	public static String getPublicNetworkIP() {
		String result = "";

		try {
			String postUrl = "http://1212.ip138.com/ic.asp";
			String html = HttpClientTimeOutUtils.getHTML(postUrl, "");

			result = CustomStringUtils.substringBetween(html, "[", "]");
		} catch (Exception ex) {
			s_logger.error(String.format(LogInfoUtils.NO_DATA_TMPL, Thread.currentThread().getStackTrace()[1].getMethodName()), ex);
		}

		return result;
	}

	/**
	 * 是否为搜索引擎蜘蛛抓取（此方法只能用在 Web 端）
	 *
	 * @param request
	 * @return
	 */
	public static boolean isSpiderCrawlForWeb(HttpServletRequest request) {

		// 通过 UserAgent 判断蜘蛛
		String userAgent = request.getHeader("user-agent").toLowerCase();
		if (userAgent.contains("bot.htm") || userAgent.contains("spider.htm") || userAgent.contains("360spider") || userAgent.contains("yisouspider")) {
			return true;
		}

		// 通过 IP 判断蜘蛛（先写死，后配置）
		String clientIP = getClientIP(request);
		if (clientIP.startsWith("220.181.108.")) {
			return true;
		}

		return false;
	}
}
