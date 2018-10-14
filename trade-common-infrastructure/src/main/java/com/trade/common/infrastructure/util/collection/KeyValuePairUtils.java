package com.trade.common.infrastructure.util.collection;

import com.google.common.collect.Lists;

import java.util.List;

public class KeyValuePairUtils {

	/**
	 * 根据 KeyValuePair<T> value值生成数据列表
	 *
	 * @param listKVP
	 * @return
	 */
	public static <T1, T2> List<T2> calListByKeyValuePairValues(List<KeyValuePair<T1, T2>> listKVP) {
		List<T2> result = Lists.newArrayList();

		for (KeyValuePair<T1, T2> kvp : listKVP) {
			result.add(kvp.getValue());
		}

		return result;
	}

	/**
	 * 根据 KeyValuePair<T> key值生成数据列表
	 *
	 * @param listKVP
	 * @return
	 */
	public static <T1, T2> List<T1> calListByKeyValuePairKeys(List<KeyValuePair<T1, T2>> listKVP) {
		List<T1> result = Lists.newArrayList();

		for (KeyValuePair<T1, T2> kvp : listKVP) {
			result.add(kvp.getKey());
		}

		return result;
	}
}
