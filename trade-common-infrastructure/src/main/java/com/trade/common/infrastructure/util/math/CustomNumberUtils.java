package com.trade.common.infrastructure.util.math;

import com.trade.common.infrastructure.util.string.CustomStringUtils;
import org.apache.commons.lang.math.NumberUtils;

public class CustomNumberUtils {

	/**
	 * 转换为Integer型
	 *
	 * @param str
	 * @return
	 */
	public static int toInt(String str) {
		return NumberUtils.toInt(CustomStringUtils.clearComma(str));
	}

	/**
	 * 转换为Float型
	 *
	 * @param str
	 * @return
	 */
	public static float toFloat(String str) {
		return NumberUtils.toFloat(CustomStringUtils.clearComma(str));
	}

	/**
	 * 转换为Double型
	 *
	 * @param str
	 * @return
	 */
	public static double toDouble(String str) {
		return NumberUtils.toDouble(CustomStringUtils.clearComma(str));
	}

	/**
	 * 由数字转换为英文字母
	 *
	 * @param number
	 * @return
	 */
	public static String convertToEnglishLetter(int number) {
		switch (number) {
			case 1:
				return "A";
			case 2:
				return "B";
			case 3:
				return "C";
			case 4:
				return "D";
			case 5:
				return "E";
			case 6:
				return "F";
			case 7:
				return "G";
			case 8:
				return "H";
			case 9:
				return "I";
			case 10:
				return "J";
			case 11:
				return "K";
			case 12:
				return "L";
			case 13:
				return "M";
			case 14:
				return "N";
			case 15:
				return "O";
			case 16:
				return "P";
			case 17:
				return "Q";
			case 18:
				return "R";
			case 19:
				return "S";
			case 20:
				return "T";
			case 21:
				return "U";
			case 22:
				return "V";
			case 23:
				return "W";
			case 24:
				return "X";
			case 25:
				return "Y";
			case 26:
				return "Z";
			default:
				return "";
		}
	}
}
