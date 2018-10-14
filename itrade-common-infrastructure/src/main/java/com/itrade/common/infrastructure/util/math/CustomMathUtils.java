package com.itrade.common.infrastructure.util.math;

import java.math.BigDecimal;
import java.util.Random;

public class CustomMathUtils {

	/**
	 * double 四舍五入不保留小数
	 *
	 * @param val
	 * @return
	 */
	public static int round(double val) {
		return (int) round(val, 0);
	}

	/**
	 * double 四舍五入保留N位小数
	 *
	 * @param val
	 * @param digits
	 * @return
	 */
	public static double round(double val, int digits) {
		return new BigDecimal(val).setScale(digits, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	/**
	 * float 四舍五入不保留小数
	 *
	 * @param val
	 * @return
	 */
	public static int round(float val) {
		return (int) round(val, 0);
	}

	/**
	 * float 四舍五入保留N位小数
	 *
	 * @param val
	 * @param digits
	 * @return
	 */
	public static float round(float val, int digits) {
		return new BigDecimal(val).setScale(digits, BigDecimal.ROUND_HALF_UP).floatValue();
	}

	/**
	 * 两个数字（double）相除，然后四舍五入得到整数
	 *
	 * @param val1
	 * @param val2
	 * @return
	 */
	public static int calIntDivideRound(int val1, int val2) {
		return (int) calDoubleDivideRound((double) val1, (double) val2, 0);
	}

	/**
	 * 两个数字（double）相除，然后四舍五入
	 *
	 * @param val1
	 * @param val2
	 * @param digits
	 * @return
	 */
	public static double calDoubleDivideRound(double val1, double val2, int digits) {
		if (val2 == 0) {
			return 0;
		}

		return round(val1 / val2, digits);
	}

	/**
	 * toRad
	 *
	 * @param d
	 * @return
	 */
	public static double toRad(double d) {
		return d * Math.PI / 180;
	}

	/**
	 * 获取 Math.random() 字符串
	 *
	 * @return
	 */
	public static String getRandomStr() {
		return String.valueOf(Math.random());
	}

	/**
	 * 随机计算一个 (0 至 max-1) 范围内的一个整数
	 *
	 * @param max
	 * @return
	 */
	public static int calRandomInteger(int max) {
		return (int) (Math.random() * max);
	}

	/**
	 * 随机计算一个 (min 至 max-1) 范围内的一个整数
	 *
	 * @param min
	 * @param max
	 * @return
	 */
	public static int calRandomInteger(int min, int max) {
		Random random = new Random();
		return random.nextInt(max) % (max - min) + min;
	}

	/**
	 * 计算一个数字的阶乘
	 *
	 * @param num
	 * @return
	 */
	public static int calFactorial(int num) {
		int result = 1;

		if (num > 0) {
			for (int i = 1; i <= num; i++) {
				result *= i;
			}
		} else {
			result = 0;
		}

		return result;
	}

	/**
	 * 转换米到公里
	 *
	 * @param distance
	 * @return
	 */
	public static double mToKM(double distance) {
		Double result = CustomMathUtils.round(distance / 1000, 2);
		if (result == 0) {
			return 0.01D;
		} else {
			return result;
		}
	}
}
