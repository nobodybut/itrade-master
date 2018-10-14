package com.itrade.common.infrastructure.util.date;

import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

public class TimerUtils {

	/**
	 * 检查当前时间点是否符合运行条件
	 *
	 * @param runTime
	 * @return
	 */
	public static boolean isRunTime(String runTime) {
		return isRunTime(new String[]{runTime});
	}

	/**
	 * 检查当前时间点是否符合运行条件
	 *
	 * @param runTimes
	 * @return
	 */
	public static boolean isRunTime(String[] runTimes) {
		if (runTimes == null || runTimes.length < 1) {
			return false;
		}

		LocalTime now = LocalTime.now();
		for (int i = 0; i < runTimes.length; i++) {
			LocalTime runTime = LocalTime.parse(runTimes[i]);
			if (runTime.getHour() == now.getHour() && runTime.getMinute() == now.getMinute()) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 当前线程挂起一段时间
	 *
	 * @param milliSeconds
	 * @return
	 */
	public static void sleep(int milliSeconds) {
		try {
			TimeUnit.MILLISECONDS.sleep(milliSeconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
