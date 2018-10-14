package com.itrade.common.infrastructure.util.collection;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.itrade.common.infrastructure.util.math.CustomMathUtils;
import org.apache.commons.lang.StringUtils;

import java.util.*;

public class CustomListUtils {

	/**
	 * array 转换成 list
	 *
	 * @param array
	 * @return
	 */
	public static <T> List<T> arrayToList(T[] array) {

		if (array != null) {
			return new ArrayList<T>(Arrays.asList(array));
		}

		return Lists.newArrayList();
	}

	/**
	 * array 转换成 set
	 *
	 * @param array
	 * @return
	 */
	public static <T> Set<T> arrayToSet(T[] array) {
		Set<T> result = Sets.newHashSet();

		if (array != null) {
			for (T t : array) {
				result.add(t);
			}
		}

		return result;
	}

	/**
	 * collection 转换成 list
	 *
	 * @param collection
	 * @return
	 */
	public static <T> List<T> collectionToList(Collection<T> collection) {
		List<T> result = Lists.newArrayList();

		if (collection != null) {
			result.addAll(collection);
		}

		return result;
	}

	/**
	 * set 转换成 list
	 *
	 * @param set
	 * @return
	 */
	public static <T> List<T> setToList(Set<T> set) {
		List<T> result = Lists.newArrayList();

		if (set != null) {
			result.addAll(set);
		}

		return result;
	}

	/**
	 * list 转换成 set
	 *
	 * @param list
	 * @return
	 */
	public static <T> Set<T> listToSet(List<T> list) {
		Set<T> result = Sets.newHashSet();

		if (list != null) {
			result.addAll(list);
		}

		return result;
	}

	/**
	 * collection 转换成 Array（String 类型）
	 *
	 * @param collection
	 * @return
	 */
	public static String[] collectionToArray(Collection<String> collection) {
		return collection.toArray(new String[collection.size()]);
	}

	/**
	 * collection 转换成 Array（Integer 类型）
	 *
	 * @param collection
	 * @return
	 */
	public static Integer[] collectionToArrayInteger(Collection<Integer> collection) {
		return collection.toArray(new Integer[collection.size()]);
	}

	/**
	 * set 转换成 Array（String 类型）
	 *
	 * @param set
	 * @return
	 */
	public static String[] setToArray(Set<String> set) {
		return set.toArray(new String[set.size()]);
	}

	/**
	 * set 转换成 Array（Integer 类型）
	 *
	 * @param set
	 * @return
	 */
	public static Integer[] setToArrayInteger(Set<Integer> set) {
		return set.toArray(new Integer[set.size()]);
	}

	/**
	 * list 转换成 Array（String 类型）
	 *
	 * @param list
	 * @return
	 */
	public static String[] listToArray(List<String> list) {
		return list.toArray(new String[list.size()]);
	}

	/**
	 * list 转换成 Array（Integer 类型）
	 *
	 * @param list
	 * @return
	 */
	public static Integer[] listToArrayInteger(List<Integer> list) {
		return list.toArray(new Integer[list.size()]);
	}

	/**
	 * 从list中随机抽取n个元素, 返回一个新List对象
	 *
	 * @param list
	 * @param n
	 * @param <T>
	 * @return
	 */
	public static <T> List<T> randomSubList(List<T> list, int n) {
		if (n < 0) {
			// 特殊处理, n为负时返回list本身
			return list;
		} else if (n == 0 || n > list.size()) {
			return Lists.newArrayList();
		} else {
			List<T> result = Lists.newArrayList();
			result.addAll(list);

			while (result.size() > n) {
				result.remove(CustomMathUtils.calRandomInteger(result.size()));
			}

			return result;
		}
	}

	/**
	 * list2是否是list1的子集
	 *
	 * @param list1
	 * @param list2
	 * @param <T>
	 * @return
	 */
	public static <T> boolean doesList1CoverList2(List<T> list1, List<T> list2) {
		list2.removeAll(list1);
		if (list2.size() <= 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * list 转换成 String（String 类型）
	 *
	 * @param list
	 * @return
	 */
	public static <T> String listToString(List<T> list) {
		StringBuilder sBuilder = new StringBuilder();

		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				sBuilder.append(list.get(i));
			}
		}

		return sBuilder.toString();
	}

	/**
	 * list 转换成 String（String 类型，带分隔符）
	 *
	 * @param list
	 * @param separator
	 * @return
	 */
	public static <T> String listToString(List<T> list, String separator) {
		StringBuilder sBuilder = new StringBuilder();

		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				sBuilder.append(list.get(i));
				sBuilder.append(separator);
			}
		}

		int separatorLength = StringUtils.isEmpty(separator) ? 0 : separator.length();
		return (sBuilder.length() > 0) ? sBuilder.substring(0, sBuilder.length() - separatorLength) : "";
	}

	/**
	 * string 转换成 string list（带分隔符）
	 *
	 * @param str
	 * @param separator
	 * @return
	 */
	public static List<String> stringToList(String str, String separator) {
		List<String> result = Lists.newArrayList();

		String[] list = StringUtils.split(str, separator);
		for (int i = 0; i < list.length; i++) {
			if (!Strings.isNullOrEmpty(list[i])) {
				result.add(list[i]);
			}
		}

		return result;
	}

	/**
	 * string 转换成 integer list（带分隔符）
	 *
	 * @param str
	 * @param separator
	 * @return
	 */
	public static List<Integer> stringToListInteger(String str, String separator) {
		List<Integer> result = Lists.newArrayList();

		String[] list = StringUtils.split(str, separator);
		for (int i = 0; i < list.length; i++) {
			if (!Strings.isNullOrEmpty(list[i])) {
				result.add(Integer.valueOf(list[i]));
			}
		}

		return result;
	}

	/**
	 * string 转换成 integer list（带分隔符），并处理负数问题
	 *
	 * @param str
	 * @param separator
	 * @return
	 */
	public static List<Integer> stringToListIntegerWithMinus(String str, String separator) {
		List<Integer> result = Lists.newArrayList();

		String[] list = str.split(separator);
		boolean minus = false;
		for (int i = 0; i < list.length; i++) {
			if (!Strings.isNullOrEmpty(list[i])) {
				//处理负数
				if (minus) {
					result.add(0 - Integer.valueOf(list[i]));
				} else {
					result.add(Integer.valueOf(list[i]));
				}
				minus = false;
			} else if (separator.equals("-")) {
				minus = true;
			}
		}

		return result;
	}

	/**
	 * List<Integer> 转换成 List<String>
	 *
	 * @param list
	 * @return
	 */
	public static List<String> listIntegerToListString(List<Integer> list) {
		List<String> result = Lists.newArrayList();

		for (int i = 0; i < list.size(); i++) {
			result.add(String.valueOf(list.get(i)));
		}

		return result;
	}

	/**
	 * List<String> 转换成 List<Integer>
	 *
	 * @param list
	 * @return
	 */
	public static List<Integer> listStringToListInteger(List<String> list) {
		List<Integer> result = Lists.newArrayList();

		for (int i = 0; i < list.size(); i++) {
			result.add(Integer.valueOf(list.get(i)));
		}

		return result;
	}

	public static void main(String[] args) {
		List<Integer> list = Lists.newArrayList(12, 45, 4280);
		System.out.println(listToString(list, " - "));
		System.out.println(listToString(list, ","));

		list = stringToListIntegerWithMinus("-1-0", "-");
		System.out.println(listToString(list, ","));
	}

	public static List<Integer> getListFromKey(List<KeyValuePair<Integer, Integer>> kvs) {
		List<Integer> ids = Lists.newArrayList();
		for (KeyValuePair<Integer, Integer> cs : kvs) {
			ids.add(cs.getKey());
		}
		return ids;
	}

	public static List<Integer> getListFromValue(List<KeyValuePair<Integer, Integer>> kvs) {
		List<Integer> ids = Lists.newArrayList();
		for (KeyValuePair<Integer, Integer> cs : kvs) {
			ids.add(cs.getValue());
		}
		return ids;
	}

	/**
	 * 按列表中的值从列表中删除数据
	 *
	 * @param list
	 * @param value
	 */
	public static void removeFromListByValue(List<Integer> list, int value) {
		for (int i = list.size() - 1; i >= 0; i--) {
			if (list.get(i) == value) {
				list.remove(i);
			}
		}
	}
}
