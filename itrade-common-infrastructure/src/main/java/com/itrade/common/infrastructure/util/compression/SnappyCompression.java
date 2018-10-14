package com.itrade.common.infrastructure.util.compression;

import com.itrade.common.infrastructure.util.json.CustomJSONUtils;
import com.itrade.common.infrastructure.util.logger.LogInfoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xerial.snappy.Snappy;

import java.io.IOException;

public class SnappyCompression {

	// 日志记录
	private static final Logger s_logger = LoggerFactory.getLogger(SnappyCompression.class);

	/**
	 * Object对象压缩为字符串（不一定带属性名）
	 *
	 * @param obj
	 * @return
	 */
	public static String compressToString(final Object obj) {
		try {
			if (obj == null) {
				return "";
			} else {
				return clearDoubleQuotation(CustomJSONUtils.toJSONString(compress(CustomJSONUtils.toJSONString(obj))));
			}
		} catch (Exception ex) {
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			String logData = String.format("obj=[%s]；", CustomJSONUtils.toJSONString(obj));

			s_logger.error(String.format(LogInfoUtils.HAS_DATA_TMPL, methodName, logData), ex);
		}

		return "";
	}

	/**
	 * Object对象压缩为字符串（带属性名）
	 *
	 * @param obj
	 * @return
	 */
	public static String compressToStringWithFieldName(final Object obj) {
		try {
			if (obj == null) {
				return "";
			} else {
				return clearDoubleQuotation(CustomJSONUtils.toJSONString(compress(CustomJSONUtils.toJSONStringWithFieldName(obj))));
			}
		} catch (Exception ex) {
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			String logData = String.format("obj=[%s]；", CustomJSONUtils.toJSONString(obj));

			s_logger.error(String.format(LogInfoUtils.HAS_DATA_TMPL, methodName, logData), ex);
		}

		return "";
	}

	/**
	 * 字符串解压缩为泛型对象
	 *
	 * @param compressedStr
	 * @param clazz
	 * @return
	 */
	public static <T> T decompressToObject(String compressedStr, Class<T> clazz) {
		try {
			return CustomJSONUtils.parseObject(decompressToString(compressedStr), clazz);
		} catch (Exception ex) {
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			String logData = String.format("compressedStr=[%s], clazz=[%s]；", compressedStr, clazz.toString());

			s_logger.error(String.format(LogInfoUtils.HAS_DATA_TMPL, methodName, logData), ex);
		}

		return null;
	}

	/**
	 * 字符串解压缩为字符串
	 *
	 * @param compressedStr
	 * @return
	 */
	public static String decompressToString(String compressedStr) {
		try {
			if (compressedStr == null || compressedStr.equals("null")) {
				return "";
			} else {
				return decompress(CustomJSONUtils.parseObject(String.format("\"%s\"", compressedStr), byte[].class));
			}
		} catch (Exception ex) {
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			String logData = String.format("compressedStr=[%s]；", compressedStr);

			s_logger.error(String.format(LogInfoUtils.HAS_DATA_TMPL, methodName, logData), ex);
		}

		return "";
	}

	/**
	 * 字符串压缩为byte数组
	 *
	 * @param str
	 * @return
	 * @throws IOException
	 */
	public static byte[] compress(final String str) throws IOException {
		if ((str == null) || (str.length() == 0)) {
			return null;
		}

		return Snappy.compress(str);
	}

	/**
	 * byte数组解压缩为字符串
	 *
	 * @param compressedBytes
	 * @return
	 * @throws IOException
	 */
	public static String decompress(final byte[] compressedBytes) throws IOException {
		if ((compressedBytes == null) || (compressedBytes.length == 0)) {
			return "";
		}

		byte[] uncompressed = Snappy.uncompress(compressedBytes);
		String result = new String(uncompressed, "UTF-8");

		if (result.contains("\\\"")) {
			result = result.replace("\\\"", "\"");
		}

		return clearDoubleQuotation(result);
	}

	/**
	 * 清空压缩结果字符串前后双引号
	 *
	 * @param compressStr
	 * @return
	 */
	private static String clearDoubleQuotation(String compressStr) {
		if (compressStr.startsWith("\"") && compressStr.endsWith("\"")) {
			return compressStr.substring(1, compressStr.length() - 1);
		}

		return compressStr;
	}
}
