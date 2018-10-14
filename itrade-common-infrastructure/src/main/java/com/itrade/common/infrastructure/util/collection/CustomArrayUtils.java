package com.itrade.common.infrastructure.util.collection;

import java.util.Arrays;

public class CustomArrayUtils {

	/**
	 * 按索引和长度读取数组数组
	 *
	 * @param source
	 * @param index
	 * @param length
	 * @return
	 */
	public static <T> T[] subarray(T[] source, int index, int length) {
		if (index < 0 || length < 0 || source.length - index < length)
			return source;

		return Arrays.copyOfRange(source, index, index + length);
	}
}
