package com.itrade.common.infrastructure.util.pppoe;

import com.google.common.collect.Lists;
import com.itrade.common.infrastructure.util.collection.KeyValuePair;
import com.itrade.common.infrastructure.util.httpclient.HttpClientTimeOutUtils;
import com.itrade.common.infrastructure.util.math.CustomMathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import static java.lang.Thread.sleep;

/**
 * Created by cc on 10/24/16.
 */
public class PPPOEManagementUtils {

	// 日志记录
	private static final Logger logger = LoggerFactory.getLogger(PPPOEManagementUtils.class);

	private static PPPOEStatus _pppoeStatus = new PPPOEStatus();

	public static PPPOEStatus getPPPOEStatus() {
		return _pppoeStatus;
	}

	private static List<String> usedIPPool = Lists.newArrayList();
	private static int POOL_RESIZE_LIMIT = 100;

	private static List<KeyValuePair<String, String>> s_targetWebSites = Lists.newArrayList(
			new KeyValuePair<String, String>("https://www.baidu.com/", "baidu.com"),
			new KeyValuePair<String, String>("https://www.taobao.com", "taobao.com"),
			new KeyValuePair<String, String>("https://www.sogou.com/", "sogou.com")
	);

	public static String executeCommandLine(String command) {
		String result = null;

		try {
			Process ps = Runtime.getRuntime().exec(command);
			ps.waitFor();

			BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));
			StringBuffer sb = new StringBuffer();
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line).append("\n");
			}
			result = sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	private static void PPPOEStart() {
		executeCommandLine("pppoe-start");
//		logger.info("pppoe-start returned: " + executeCommandLine("pppoe-start"));
	}

	private static void PPPOEStop() {
		executeCommandLine("pppoe-stop");
//		logger.info("pppoe-stop returned: " + executeCommandLine("pppoe-stop"));
	}

	private static void PPPOEInquire() {
		String commandResult = executeCommandLine("pppoe-status");
		_pppoeStatus.setStatusMessage(commandResult);
//		logger.info(_pppoeStatus.getStatusMessage());

		if (commandResult.contains("pppoe-status: Link is up")) {
			_pppoeStatus.setLinkStatus(1);
			String parsedIP = "";
			try {
				parsedIP = commandResult.substring(commandResult.indexOf("inet ") + 5, commandResult.indexOf(" peer "));
			} catch (Exception e) {
				parsedIP = "UNKNOWN";
				logger.info(String.format("Unknown output format, IP cannot be parsed."));
			}
			_pppoeStatus.setIP(parsedIP);
		} else if (commandResult.contains("pppoe-status: Link is down")) {
			_pppoeStatus.setLinkStatus(0);
			_pppoeStatus.setIP(null);
		} else {
			_pppoeStatus.setLinkStatus(-1);
			_pppoeStatus.setIP(null);
			logger.info(String.format("Unknown message format of ./pppoe-status output."));
		}
	}

	public static boolean isTargetReachable() {
		KeyValuePair<String, String> targetWebSiteKV = s_targetWebSites.get(CustomMathUtils.calRandomInteger(s_targetWebSites.size()));
		String testUrl = targetWebSiteKV.getKey();
		String assertionStr = targetWebSiteKV.getValue();

		String pageCode = HttpClientTimeOutUtils.getHTML(testUrl, "", "utf-8", 30000);
		if (pageCode.contains(assertionStr)) {
			return true;
		} else {
			// 本次拨号实际并未成功，免谈重播
			long sleepInterval = 10000;
			logger.info(String.format("Dialing failed，getting %s unsuccessful，Redial after %sms......", testUrl, sleepInterval));
			try {
				sleep(sleepInterval);
			} catch (Throwable ex) {
				ex.printStackTrace();
			}

			return false;
		}
	}

	private static void usedIPPoolAdd(String IP) {
		usedIPPool.add(IP);
		if (usedIPPool.size() >= POOL_RESIZE_LIMIT) {
			List<String> resizedPool = Lists.newArrayList();
			for (int i = usedIPPool.size() - POOL_RESIZE_LIMIT / 5; i < usedIPPool.size(); i++) {
				resizedPool.add(usedIPPool.get(i));
			}
			usedIPPool = resizedPool;
		}
	}

	public static String smartDial(int windowSize, int thresholdSegment) {
		boolean isTargetUnReachable;
		int dialFailedCount = 0;
		do {
			do {
				PPPOEStop();
				PPPOEStart();
				isTargetUnReachable = !isTargetReachable();

				//记录拨号失败的次数,大于11次(2分钟)则重新启动服务器
				if (isTargetUnReachable) {
					dialFailedCount++;
				}
				if (dialFailedCount > 11) {
					logger.info("dialFailed too many times,reboot start......");
					executeCommandLine("reboot");
				}
			} while (isTargetUnReachable);
			PPPOEInquire();
		} while (areIPsSimilarEnough(_pppoeStatus, windowSize, thresholdSegment));

		usedIPPool.add(_pppoeStatus.getIP());

		return _pppoeStatus.getIP();
	}

	/**
	 * windowSize＝3，查最近三个使用过的IP
	 * thresholdSameSegement=4，不查IP段，新IP只要和老IP有任何不同，就认为是不同IP
	 * thresholdSameSegement=3，查IP段，新IP如果和老IP只有最后一小段有区别，就认为是相同IP
	 *
	 * @param pppoeStatus
	 * @param windowSize
	 * @param thresholdSameSegement
	 * @return
	 */
	private static boolean areIPsSimilarEnough(PPPOEStatus pppoeStatus, int windowSize, int thresholdSameSegement) {
		for (int j = 1; j <= windowSize && usedIPPool.size() - j >= 0; j++) {
			String usedIP = usedIPPool.get(usedIPPool.size() - j);
			String newIP = pppoeStatus.getIP();
			String[] usedIPSegments = usedIP.split("\\.");
			String[] newIPSegments = newIP.split("\\.");

			if (newIPSegments.length != 4) {
				logger.info(String.format("cannot parse this IP，running shell command ./pppoe-status gives this：％s", pppoeStatus.getStatusMessage()));
			}

			if (usedIPSegments.length != 4 || newIPSegments.length != 4) {
				return false;
			}

			int i;
			for (i = 0; i < 4; i++) {
				if (!usedIPSegments[i].equals(newIPSegments[i])) {
					break;
				}
			}
			if (i >= thresholdSameSegement) {
				return true;
			}
		}

		return false;
	}
}
