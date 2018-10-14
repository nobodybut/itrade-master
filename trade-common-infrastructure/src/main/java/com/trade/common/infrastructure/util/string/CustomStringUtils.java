package com.trade.common.infrastructure.util.string;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.trade.common.infrastructure.util.collection.CustomListUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CustomStringUtils {

	/**
	 * 读取一对标签间的字符串
	 *
	 * @param str
	 * @param open
	 * @param close
	 * @return
	 */
	public static String substringBetween(String str, String open, String close) {
		String result = StringUtils.substringBetween(str, open, close);

		if (result == null) {
			result = "";
		}

		return result.trim();
	}

	/**
	 * 读取两对标签间的字符串
	 *
	 * @param str
	 * @param open1
	 * @param close1
	 * @param open2
	 * @param close2
	 * @return
	 */
	public static String substringBetween(String str, String open1, String close1, String open2, String close2) {
		String result = StringUtils.substringBetween(StringUtils.substringBetween(str, open1, close1), open2, close2);

		if (result == null) {
			result = "";
		}

		return result.trim();
	}

	/**
	 * 读取三对标签间的字符串
	 *
	 * @param str
	 * @param open1
	 * @param close1
	 * @param open2
	 * @param close2
	 * @param open3
	 * @param close3
	 * @return
	 */
	public static String substringBetween(String str, String open1, String close1, String open2, String close2, String open3, String close3) {
		String result = StringUtils.substringBetween(StringUtils.substringBetween(StringUtils.substringBetween(str, open1, close1), open2, close2), open3, close3);

		if (result == null) {
			result = "";
		}

		return result.trim();
	}

	/**
	 * 读取一对标签间的字符串数组
	 *
	 * @param str
	 * @param open
	 * @param close
	 * @return
	 */
	public static String[] substringsBetween(String str, String open, String close) {
		String[] result = StringUtils.substringsBetween(str, open, close);

		if (result == null) {
			result = new String[0];
		}

		return result;
	}

	/**
	 * 读取一对标签间的字符串数组，并转换为List
	 *
	 * @param str
	 * @param open
	 * @param close
	 * @return
	 */
	public static List<String> substringsBetweenToList(String str, String open, String close) {
		return CustomListUtils.arrayToList(substringsBetween(str, open, close));
	}

	/**
	 * 模拟C#的 substring
	 *
	 * @param str
	 * @param startIndex
	 * @param length
	 * @return
	 */
	public static String substringCS(String str, int startIndex, int length) {
		int strLength = str.length();

		if (startIndex < 0) {
			throw new StringIndexOutOfBoundsException("ArgumentOutOfRange_StartIndex");
		}
		if (startIndex > strLength) {
			throw new StringIndexOutOfBoundsException("ArgumentOutOfRange_StartIndexLargerThanLength");
		}
		if (length < 0) {
			throw new StringIndexOutOfBoundsException("ArgumentOutOfRange_NegativeLength");
		}
		if (startIndex > (strLength - length)) {
			throw new StringIndexOutOfBoundsException("ArgumentOutOfRange_IndexLength");
		}
		if (length == 0) {
			return "";
		}

		return StringUtils.substring(str, startIndex, startIndex + length);
	}

	/**
	 * 对字符串做 encodeURIComponent
	 *
	 * @param str
	 * @return
	 */
	public static String encodeURIComponent(String str) {
		if (str == null) {
			return "";
		}

		try {
			return URLEncoder.encode(str, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return "";
	}

	/**
	 * 对字符串做 decodeURIComponent
	 *
	 * @param str
	 * @return
	 */
	public static String decodeURIComponent(String str) {
		if (str == null) {
			return "";
		}

		try {
			return URLDecoder.decode(str, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return "";
	}

	/**
	 * 替换掉空格（字符串格式）
	 *
	 * @param str
	 * @return
	 */
	public static String replaceEnterRN(String str) {
		if (str == null) {
			return "";
		}

		return str.replace("\r\n", "").replace("\r", "").replace("\n", "");
	}

	/**
	 * 替换掉空格（字符串格式）
	 *
	 * @param str
	 * @return
	 */
	public static String replaceEnter(String str) {
		if (str == null) {
			return "";
		}

		return str.replace("\r\n", "").replace("\r", "").replace("\n", "").replace("<br />", "").replace("<br/>", "").replace("<br>", "");
	}

	/**
	 * 替换掉空格（编码格式）
	 *
	 * @param str
	 * @return
	 */
	public static String replaceEnterCode(String str) {
		if (str == null) {
			return "";
		}

		return str.replace("\\u000d", "").replace("\\u000a", "");
	}

	/**
	 * 替换英文格式单引号为中文格式的单引号
	 *
	 * @param str
	 * @return
	 */
	public static String convertToCNSingleQuotes(String str) {
		if (str == null) {
			return "";
		}

		if (str.contains("'")) {
			return str.replace("'", "‘");
		}

		return str;
	}

	/**
	 * 替换英文格式双引号为中文格式的双引号
	 *
	 * @param str
	 * @return
	 */
	public static String convertToCNDoubleQuotes(String str) {
		if (str == null) {
			return "";
		}

		if (str.contains("\"")) {
			return str.replace("\"", "“");
		}

		return str;
	}

	/**
	 * 清除字符串中的双引号
	 *
	 * @param str
	 * @return
	 */
	public static String clearDoubleQuotes(String str) {
		if (str == null) {
			return "";
		}

		if (str.contains("\"")) {
			return str.replace("\"", "");
		}

		return str;
	}

	/**
	 * 清除字符串中的双引号
	 *
	 * @param list
	 */
	public static void clearListDoubleQuotes(List<String> list) {
		for (int i = 0; i < list.size(); i++) {
			list.set(i, CustomStringUtils.clearDoubleQuotes(list.get(i)));
		}
	}

	/**
	 * 截取固定长度字符串函数(如果超过加...)
	 *
	 * @param str
	 * @param maxLength
	 * @return
	 */
	public static String shortString(final String str, final int maxLength) {
		if (str == null) {
			return "";
		}

		final StringBuffer sbuffer = new StringBuffer();
		final char[] chr = str.trim().toCharArray();
		int len = 0;
		for (int i = 0; i < chr.length; i++) {

			if (chr[i] >= 0xa1) {
				len += 2;
			} else {
				len++;
			}
		}

		if (len <= maxLength) {
			return str;
		}

		len = 0;
		for (int i = 0; i < chr.length; i++) {

			if (chr[i] >= 0xa1) {
				len += 2;
				if (len > maxLength) {
					break;
				} else {
					sbuffer.append(chr[i]);
				}
			} else {
				len++;
				if (len > maxLength) {
					break;
				} else {
					sbuffer.append(chr[i]);
				}
			}
		}
		sbuffer.append("..");

		return sbuffer.toString();
	}

	/**
	 * 所有单词更改为首字母大写
	 *
	 * @param str
	 * @return
	 */
	public static String toEnUpper(String str) {
		if (str == null) {
			return "";
		}

		StringBuffer stringbf = new StringBuffer();
		Matcher m = Pattern.compile("([a-z])([a-z]*)", Pattern.CASE_INSENSITIVE).matcher(str);
		while (m.find()) {
			m.appendReplacement(stringbf, m.group(1).toUpperCase() + m.group(2).toLowerCase());
		}

		return m.appendTail(stringbf).toString();
	}

	/**
	 * 处理Url特殊字符
	 *
	 * @param str
	 * @return
	 */
	public static String convertUrlSpecialCharacters(String str) {
		if (str == null) {
			return "";
		}

		return CustomStringUtils.toEnUpper(str.replace("&amp;", "").replace("&#39;", "").replace(" ", "_").replace("/", "_").replace("-", "_").replace(".", "_").replace("&", "_").replace("，", "_")
				.replace(",", "_").replace("@", "_").replace("#", "").replace("！", "").replace("!", "").replace("$", "").replace("^", "").replace("*", "").replace("(", "_").replace(")", "")
				.replace("（", "_").replace("）", "").replace("~", ""));
	}

	/**
	 * 删除括号内部字符串
	 *
	 * @param str
	 * @return
	 */
	public static String removeStringInBrackets(String str) {
		if (str == null) {
			return "";
		}

		while (true) {
			if (str.contains("(")) {
				str = str.replace("(" + CustomStringUtils.substringBetween(str, "(", ")") + ")", "");
			} else {
				break;
			}
		}

		return str;
	}

	/**
	 * 删除中括号内部字符串
	 *
	 * @param str
	 * @return
	 */
	public static String removeStringInSquareBrackets(String str) {
		if (str == null) {
			return "";
		}

		while (true) {
			if (str.contains("["))
				str = str.replace("[" + CustomStringUtils.substringBetween(str, "[", "]") + "]", "");
			else
				break;
		}

		return str;
	}

	/**
	 * 清除逗号
	 *
	 * @param str
	 * @return
	 */
	public static String clearComma(String str) {
		if (str == null) {
			return "";
		}

		if (str.contains(",")) {
			str = str.replace(",", "");
		}

		if (str.contains(" ")) {
			str = str.replace(" ", "");
		}

		return str;
	}

	/**
	 * 清除空格
	 *
	 * @param str
	 * @return
	 */
	public static String clearBlank(String str) {
		if (str == null) {
			return "";
		}

		if (str.contains(" ")) {
			return str.replace(" ", "");
		}

		return str;
	}

	/**
	 * 转换 "|" 间隔字符串为List
	 *
	 * @param str
	 * @return
	 */
	public static List<String> convertStringToStrList(String str) {
		if (!Strings.isNullOrEmpty(str)) {
			return CustomListUtils.arrayToList(StringUtils.split(str, "|"));
		}

		return Lists.newArrayList();
	}

	/**
	 * 转换 List 为 "|" 间隔字符串
	 *
	 * @param str
	 * @return
	 */
	public static List<Integer> convertStringToIntList(String str) {
		List<Integer> result = Lists.newArrayList();

		if (!Strings.isNullOrEmpty(str)) {
			String[] codes = StringUtils.split(str, "|");
			for (String code : codes) {
				int intCode = NumberUtils.toInt(code);
				if (intCode > 0) {
					result.add(intCode);
				}
			}
		}

		return result;
	}

	/**
	 * 判断字符串长度（区别中英文）
	 *
	 * @param str
	 * @return
	 */
	public static int strLength(String str) {
		if (Strings.isNullOrEmpty(str)) {
			return 0;
		}

		try {
			return new String(str.getBytes("gb2312"), "iso-8859-1").length();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return str.length();
	}

	/**
	 * 转换特殊字符
	 *
	 * @param str
	 * @return
	 */
	public static String convertSpecialWords(String str) {
		if (str == null) {
			return "";
		}

		return str.replace("（", "(").replace("）", ")").replace("#", "").replace("$", "").replace("|", "").replace(":", "").replace("~", "").replace("^", "").replace("+", "");
	}

	/**
	 * 转换括号
	 *
	 * @param str
	 * @return
	 */
	public static String convertBrackets(String str) {
		if (str == null) {
			return "";
		}

		return str.replace("（", "(").replace("）", ")");
	}

	/**
	 * 清除括号
	 *
	 * @param str
	 * @return
	 */
	public static String removeBrackets(String str) {
		if (str == null) {
			return "";
		}

		return str.replace("(", "").replace(")", "");
	}

	/**
	 * 处理结尾没有标点符号问题
	 *
	 * @param str
	 * @return
	 */
	public static String performEndWith(String str) {
		if (str == null) {
			return "";
		}

		if (!str.endsWith("!") && !str.endsWith("！") && !str.endsWith("。") && !str.endsWith("~")) {
			str += "。";
		}

		return str;
	}

	/**
	 * 清除 <span ...></span>
	 *
	 * @param str
	 * @return
	 */
	public static String clearSpan(String str) {
		if (str == null) {
			return "";
		}

		List<String> noRepeatSpanCodes = Lists.newArrayList();
		String[] spanCodes = CustomStringUtils.substringsBetween(str, "<span ", ">");
		for (String spanCode : spanCodes) {
			if (!noRepeatSpanCodes.contains(spanCode)) {
				noRepeatSpanCodes.add(spanCode);
			}
		}

		for (String noRepeatSpanCode : noRepeatSpanCodes) {
			str = str.replace(MessageFormat.format("<span {0}>", noRepeatSpanCode), "").replace("</span>", "");
		}

		return str;
	}

	/**
	 * 首字母转大写
	 *
	 * @param str
	 * @return
	 */
	public static String toUpperCaseFirstOne(String str) {
		if (str == null) {
			return "";
		}

		return (new StringBuilder()).append(Character.toUpperCase(str.charAt(0))).append(str.substring(1)).toString();
	}

	/**
	 * 首字母转小写
	 *
	 * @param str
	 * @return
	 */
	public static String toLowerCaseFirstOne(String str) {
		if (str == null) {
			return "";
		}

		return (new StringBuilder()).append(Character.toLowerCase(str.charAt(0))).append(str.substring(1)).toString();
	}

	/**
	 * 用某个字符串分割,取字符串后面的元素
	 *
	 * @param str
	 * @param splitStr
	 * @return
	 */
	public static String[] splitAfter(String str, String splitStr) {
		String[] temp = str.split(escapeSpecialChar(splitStr));
		if (temp.length > 1) {
			String[] result = new String[temp.length - 1];
			System.arraycopy(temp, 1, result, 0, temp.length - 1);
			return result;
		}
		return temp;
	}

	/**
	 * split本质是正则
	 * 正则需要转义字符'$', '(', ')', '*', '+', '.', '[', ']', '?', '\\', '^', '{', '}', '|'
	 *
	 * @param str
	 * @return
	 */
	public static String escapeSpecialChar(String str) {
		if (!Strings.isNullOrEmpty(str)) {
			return str.replace("$", "\\$")
					.replace("(", "\\(")
					.replace(")", "\\)")
					.replace("*", "\\*")
					.replace("+", "\\+")
					.replace(".", "\\.")
					.replace("[", "\\[")
					.replace("]", "\\]")
					.replace("?", "\\?")
					.replace("\\", "\\\\")
					.replace("^", "\\^")
					.replace("{", "\\{")
					.replace("}", "\\}")
					.replace("|", "\\|");
		}
		return "";
	}

	/**
	 * 用某个字符串分割,取字符串前面的元素
	 *
	 * @param str
	 * @param splitStr
	 * @return
	 */
	public static String[] splitBefour(String str, String splitStr) {
		String[] temp = str.split(escapeSpecialChar(splitStr));
		if (temp.length > 1) {
			String[] result = new String[temp.length - 1];
			System.arraycopy(temp, 0, result, 0, temp.length - 1);
			return result;
		}

		return temp;
	}

	public static String[] camelSplit(String str) {
		if (StringUtils.isEmpty(str)) {
			return new String[0];
		}
		return str.split("(?<!(^|[A-Z0-9]))(?=[A-Z0-9])|(?<!(^|[^A-Z]))(?=[0-9])|(?<!(^|[^0-9]))(?=[A-Za-z])|(?<!^)(?=[A-Z][a-z])");
	}

	public static void main(String[] args) {
		String[] arr = CustomStringUtils.camelSplit("5P");
		String str = Arrays.stream(arr).collect(Collectors.joining(" "));
		System.out.println(str);
	}
}