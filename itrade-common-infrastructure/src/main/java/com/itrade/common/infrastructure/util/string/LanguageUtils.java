package com.itrade.common.infrastructure.util.string;

import org.apache.commons.lang.StringUtils;

public class LanguageUtils {

	/**
	 * 判断是否为英文
	 *
	 * @param str
	 * @return
	 */
	public static boolean isEnglish(String str) {
		return str.matches("^[A-Za-z]+$");
	}

	/**
	 * 判断是否为中文
	 *
	 * @param str
	 * @return
	 */
	public static boolean isChinese(String str) {
		return str.matches("[\u4e00-\u9fa5]+$");
	}

	/**
	 * 判断是否包含中文
	 *
	 * @param str
	 * @return
	 */
	public static boolean containsChinese(String str) {
		int length = str.length();
		for (int i = 0; i < length; i++) {
			if (isChinese(CustomStringUtils.substringCS(str, i, 1))) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 判断是否只包含中文 TODO 此方法需改进
	 *
	 * @param str
	 * @return
	 */
	public static boolean containsOnlyChinese(String str) {
		str = str.replace("，", "|").replace(",", "|").replace("。", "|").replace(".", "|").replace("、", "|").replace("！", "|").replace("!", "|").replace("<br", "|").replace("~", "|").replace("～", "|").replace(" ", "|");
		String[] arr = StringUtils.split(str, "|");
		for (String s : arr) {
			if (isChinese(s)) {
				return true;
			}
		}

		return false;
	}
}
