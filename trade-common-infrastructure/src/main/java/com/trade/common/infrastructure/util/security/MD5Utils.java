package com.trade.common.infrastructure.util.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {

	// 日志记录
	private static final Logger _logger = LoggerFactory.getLogger(MD5Utils.class);

	// 相关常量
	private final static char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

	/**
	 * MD5 32位
	 *
	 * @param input
	 * @return
	 */
	public static String md5_32(String input) {
		try {
			return code(input, 32).toLowerCase();
		} catch (Exception e) {
			_logger.error("md5_32", e);
		}

		return input;
	}

	/**
	 * MD5 16位
	 *
	 * @param input
	 * @return
	 */
	public static String md5_16(String input) {
		try {
			return code(input, 16).toLowerCase();
		} catch (Exception e) {
			_logger.error("md5_16", e);
		}

		return input;
	}

	/**
	 * MD5 加密
	 *
	 * @param input
	 * @param bit
	 * @return
	 * @throws Exception
	 */
	private static String code(String input, int bit) throws Exception {
		try {
			MessageDigest md = MessageDigest.getInstance(System.getProperty("MD5.algorithm", "MD5"));
			if (bit == 16)
				return bytesToHex(md.digest(input.getBytes("utf-8"))).substring(8, 24);
			return bytesToHex(md.digest(input.getBytes("utf-8")));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new Exception("Could not found MD5 algorithm.", e);
		}
	}

	private static String bytesToHex(byte[] bytes) {
		StringBuffer sBuffer = new StringBuffer();
		int t;
		for (int i = 0; i < 16; i++) {
			t = bytes[i];
			if (t < 0)
				t += 256;
			sBuffer.append(hexDigits[(t >>> 4)]);
			sBuffer.append(hexDigits[(t % 16)]);
		}
		return sBuffer.toString();
	}
}
