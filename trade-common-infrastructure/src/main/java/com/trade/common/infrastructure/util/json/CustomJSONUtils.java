package com.trade.common.infrastructure.util.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import com.trade.common.infrastructure.util.string.CustomStringUtils;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Slf4j
public class CustomJSONUtils {

	/**
	 * 定义变量
	 **/
	private final static Set<Class<?>> numberClasses = Sets.newHashSet();
	private final static Set<Class<?>> booleanClasses = Sets.newHashSet();

	/** 静态构造块 **/
	static {
		numberClasses.add(byte.class);
		numberClasses.add(short.class);
		numberClasses.add(int.class);
		numberClasses.add(long.class);
		numberClasses.add(float.class);
		numberClasses.add(double.class);
		numberClasses.add(Byte.class);
		numberClasses.add(Short.class);
		numberClasses.add(Integer.class);
		numberClasses.add(Long.class);
		numberClasses.add(Float.class);
		numberClasses.add(Double.class);
		numberClasses.add(BigInteger.class);
		numberClasses.add(BigDecimal.class);

		booleanClasses.add(boolean.class);
		booleanClasses.add(Boolean.class);
	}

	/**
	 * 对象转换为 JSON 字符串（jackson序列化，带属性名）
	 *
	 * @param value
	 * @return
	 */
	public static final String toJSONStringWithFieldName(Object value) {
		try {
			ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule()).configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
			String result = mapper.writeValueAsString(value);
			if (result.contains("'")) {
				result = result.replace("'", "‘");
			}

			return result;
		} catch (JsonProcessingException e) {
			log.error("toJSONStringWithFieldName exception!", e);
		}

		return "";
	}

	/**
	 * 对象转换为 JSON 字符串（jackson序列化，带属性名）
	 *
	 * @param value
	 * @return
	 */
	public static final JSONObject toJSONObjectWithFieldName(Object value) {
		try {
			ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule()).configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
			String result = mapper.writeValueAsString(value);
			if (result.contains("'")) {
				result = result.replace("'", "‘");
			}

			return JSON.parseObject(result);
		} catch (JsonProcessingException e) {
			log.error("toJSONStringWithFieldName exception!", e);
		}

		return new JSONObject();
	}

	/**
	 * 解析JSON字符串到对象（jackson反序列化）
	 *
	 * @param json
	 * @param clazz
	 * @return
	 */
	public static final <T> T parseObjectWithFieldName(String json, Class<T> clazz) {
		try {
			ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule())
					.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
					.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			return mapper.readValue(json, clazz);
		} catch (IOException e) {
			log.error("toJSONStringWithFieldName exception!", e);
		}

		return null;
	}

	/**
	 * 对象转换为 JSON 字符串（fastJson序列化，不是必须带属性名）
	 *
	 * @param value
	 * @return
	 */
	public static final String toJSONString(Object value) {
		return JSON.toJSONString(value);
	}

	/**
	 * 解析JSON字符串到对象
	 *
	 * @param text
	 * @param clazz
	 * @return
	 */
	public static <T> T parseObject(String text, Class<T> clazz) {
		if (Strings.isNullOrEmpty(text)) {
			if (numberClasses.contains(clazz)) {
				return JSON.parseObject("0", clazz);
			} else if (booleanClasses.contains(clazz)) {
				return JSON.parseObject("false", clazz);
			} else if (text == null) {
				return null;
			}
		}

		return JSON.parseObject(text, clazz);
	}

	/**
	 * 解析字符串型的JSON值
	 *
	 * @param code
	 * @param rootName
	 * @return
	 */
	public static String parseJsonStringValue(String code, String rootName) {
		return CustomStringUtils.substringBetween(CustomStringUtils.clearBlank(code), "\"" + rootName + "\":\"", "\",");
	}

	/**
	 * 解析数值型的JSON值
	 *
	 * @param code
	 * @param rootName
	 * @return
	 */
	public static String parseJsonIntValue(String code, String rootName) {
		return CustomStringUtils.substringBetween(CustomStringUtils.clearBlank(code), "\"" + rootName + "\":", ",");
	}

	/**
	 * 转换结果为 Jsonp 可用的值（供Jquery跨域调用），单引号包围
	 *
	 * @param request
	 * @param result
	 * @return
	 */
	public static String getJsonpValue(HttpServletRequest request, String result) {
		if (result.contains("'")) {
			result = result.replace("'", "‘");
		}

		result = convertJsonResultSpecialCharacter(result);
		result = convertJsonResultDoubleQuotes(result);

		return String.format("%s('%s')", request.getParameter("callback"), result);
	}

	/**
	 * 转换结果为 Jsonp 可用的值（供Jquery跨域调用），双引号包围
	 *
	 * @param request
	 * @param result
	 * @return
	 */
	public static String getJsonpValueQuot(HttpServletRequest request, String result) {
		if (result.contains("\"")) {
			result = result.replace("\"", "“");
		}

		result = convertJsonResultSpecialCharacter(result);

		return String.format("%s(\"%s\")", request.getParameter("callback"), result);
	}

	/**
	 * 转换JSON结果中的 \r\n
	 *
	 * @param result
	 * @return
	 */
	private static String convertJsonResultSpecialCharacter(String result) {
		if (result.contains("\\r\\n")) {
			result = result.replace("\\r\\n", "").replace("\r\n", "");
		} else {
			if (result.contains("\\r")) {
				result = result.replace("\\r", "").replace("\r", "");
			}

			if (result.contains("\\n")) {
				result = result.replace("\\n", "").replace("\n", "");
			}
		}

		if (result.contains("\\")) {
			result = result.replace("\\", "");
		}

		return result;
	}

	/**
	 * 转换JSON结果中的 英文双引号
	 *
	 * @param str
	 * @return
	 */
	private static String convertJsonResultDoubleQuotes(String str) {
		char[] temp = str.toCharArray();
		int n = temp.length;
		for (int i = 0; i < n; i++) {
			if (temp[i] == ':' && temp[i + 1] == '"') {
				for (int j = i + 2; j < n; j++) {
					if (temp[j] == '"') {
						if (temp[j + 1] != ',' && temp[j + 1] != '}') {
							temp[j] = '”';
						} else if (temp[j + 1] == ',' || temp[j + 1] == '}') {
							break;
						}
					}
				}
			}
		}

		return new String(temp);
	}

	/**
	 * 清除 json value 中的特殊字符串
	 *
	 * @param str
	 * @return
	 */
	public static String clearJsonValueExpStr(String str) {
		if (str == null) {
			return "";
		}

		if (str.contains("\"")) {
			return str.replace("\"", "");
		}

		if (str.contains(":")) {
			return str.replace(":", "");
		}

		if (str.contains("\\")) {
			return str.replace("\\", "");
		}

		return str;
	}

	/**
	 * 清除 json value list 中的特殊字符串
	 *
	 * @param list
	 */
	public static void clearJsonValueListExpStr(List<String> list) {
		for (int i = 0; i < list.size(); i++) {
			list.set(i, clearJsonValueExpStr(list.get(i)));
		}
	}

	/**
	 * 从holding对象中取JSONArray,如果目标对象不是JSONArray类型,则创建JSONArray add目标对象再返回
	 *
	 * @param holding
	 * @param name
	 * @return
	 */
	public static JSONArray getJSONArray(JSONObject holding, String name) {
		Object obj = holding.get(name);
		if (obj instanceof JSONArray) {
			return (JSONArray) obj;
		}
		JSONArray arr = new JSONArray();
		arr.add(obj);
		return arr;
	}
}
