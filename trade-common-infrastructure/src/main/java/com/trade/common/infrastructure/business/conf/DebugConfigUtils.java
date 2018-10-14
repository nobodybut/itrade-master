package com.trade.common.infrastructure.business.conf;

public class DebugConfigUtils {

	// 数据存储
	private static boolean s_isInit = false;
	private static boolean s_isDebug = false;
	private static boolean s_isDebugData = false;

	// 锁对象
	private static final Object s_lockObj = new Object();

	/**
	 * 返回系统是否处于 debug 模式
	 *
	 * @return
	 */
	public static boolean isDebug() {
		init();

		return s_isDebug;
	}

	public static boolean isDebugData() {
		init();

		return s_isDebugData;
	}

	private static void init() {
		if (!s_isInit) {
			synchronized (s_lockObj) {
				if (!s_isInit) {
					s_isDebug = PropertiesUtils.getBooleanValue("isDebug");
					s_isDebugData = PropertiesUtils.getBooleanValue("isDebugData");
					s_isInit = true;
				}
			}
		}
	}
}
