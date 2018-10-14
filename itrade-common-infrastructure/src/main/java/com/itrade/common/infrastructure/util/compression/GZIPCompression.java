package com.itrade.common.infrastructure.util.compression;

import com.itrade.common.infrastructure.util.json.CustomJSONUtils;
import com.itrade.common.infrastructure.util.logger.LogInfoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GZIPCompression {

	// 日志记录
	private static final Logger s_logger = LoggerFactory.getLogger(GZIPCompression.class);

	/**
	 * Object对象GZIP压缩为字符串（不一定带属性名）
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
	 * Object对象GZIP压缩为字符串（带属性名）
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
	 * 字符串GZIP解压缩为泛型对象
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
	 * 字符串GZIP解压缩为字符串
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
	 * 字符串GZIP压缩为byte数组
	 *
	 * @param str
	 * @return
	 * @throws IOException
	 */
	public static byte[] compress(final String str) throws IOException {
		if ((str == null) || (str.length() == 0)) {
			return null;
		}
		ByteArrayOutputStream obj = new ByteArrayOutputStream();
		GZIPOutputStream gzip = new GZIPOutputStream(obj);
		gzip.write(str.getBytes("UTF-8"));
		gzip.close();

		return obj.toByteArray();
	}

	/**
	 * byte数组GZIP解压缩为字符串
	 *
	 * @param compressedBytes
	 * @return
	 * @throws IOException
	 */
	public static String decompress(final byte[] compressedBytes) throws IOException {
		String result = "";

		if ((compressedBytes == null) || (compressedBytes.length == 0)) {
			return "";
		}
		if (isCompressed(compressedBytes)) {
			GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(compressedBytes));
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(gis, "UTF-8"));

			String line;
			while ((line = bufferedReader.readLine()) != null) {
				result += line;
			}
		} else {
			result = new String(compressedBytes);
		}

		if (result.contains("\\\"")) {
			result = result.replace("\\\"", "\"");
		}

		if (result.contains("\\")) {
			result = result.replace("\\", "");
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

	/**
	 * 是否为 GZIP 压缩数据
	 *
	 * @param compressed
	 * @return
	 */
	private static boolean isCompressed(final byte[] compressed) {
		return (compressed[0] == (byte) (GZIPInputStream.GZIP_MAGIC)) && (compressed[1] == (byte) (GZIPInputStream.GZIP_MAGIC >> 8));
	}
}
