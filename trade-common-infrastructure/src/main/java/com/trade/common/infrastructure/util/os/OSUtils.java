package com.trade.common.infrastructure.util.os;

public class OSUtils {

	/**
	 * 判断系统是否为windows
	 *
	 * @return
	 */
	public static boolean isWindows() {
		return System.getProperty("os.name").toLowerCase().startsWith("win");
	}
}
