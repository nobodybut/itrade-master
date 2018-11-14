package com.trade.common.infrastructure.util.collection;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.trade.common.infrastructure.util.math.CustomMathUtils;
import com.trade.common.infrastructure.util.obj.DeepCopyUtils;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

public class CustomListMathUtils {

	/**
	 * 计算 List<Integer> 平均值
	 *
	 * @param vals
	 * @return
	 */
	public static int calListIntAvg(List<Integer> vals) {
		if (vals.size() > 0) {
			double total = 0;
			for (double val : vals) {
				total += val;
			}

			return CustomMathUtils.round(total / vals.size());
		}

		return 0;
	}

	/**
	 * 计算 List<Float> 平均值（保留几位小数）
	 *
	 * @param vals
	 * @param digits
	 * @return
	 */
	public static float calFloatAvg(List<Float> vals, int digits) {
		if (vals.size() > 0) {
			double total = 0;
			for (float val : vals) {
				total += val;
			}

			return (float) CustomMathUtils.round(total / vals.size(), digits);
		}

		return 0;
	}

	/**
	 * 计算 List<Double> 平均值（保留几位小数）
	 *
	 * @param vals
	 * @param digits
	 * @return
	 */
	public static double calDoubleAvg(List<Double> vals, int digits) {
		if (vals.size() > 0) {
			double total = 0;
			for (double val : vals) {
				total += val;
			}

			return CustomMathUtils.round(total / vals.size(), digits);
		}

		return 0;
	}

	/**
	 * 计算 List<Integer> 总和
	 *
	 * @param vals
	 * @return
	 */
	public static int calListIntTotal(List<Integer> vals) {
		if (vals.size() > 0) {
			int total = 0;
			for (int val : vals) {
				total += val;
			}

			return total;
		}

		return 0;
	}

	/**
	 * 计算 List<Long> 总和
	 *
	 * @param vals
	 * @return
	 */
	public static long calListLongTotal(List<Long> vals) {
		if (vals.size() > 0) {
			long total = 0;
			for (long val : vals) {
				total += val;
			}

			return total;
		}

		return 0;
	}

	/**
	 * 计算 List<Double> 总和
	 *
	 * @param vals
	 * @return
	 */
	public static double calListDoubleTotal(List<Double> vals) {
		if (vals.size() > 0) {
			double total = 0;
			for (double val : vals) {
				total += val;
			}

			return total;
		}

		return 0;
	}

	/**
	 * 计算 List<Float> 总和
	 *
	 * @param vals
	 * @return
	 */
	public static float calListFloatTotal(List<Float> vals) {
		if (vals.size() > 0) {
			float total = 0;
			for (float val : vals) {
				total += val;
			}

			return total;
		}

		return 0;
	}

	/**
	 * 计算 List<Integer> 最小元素的值
	 *
	 * @param vals
	 * @return
	 */
	public static int calListIntMinValue(List<Integer> vals) {
		if (vals.size() > 0) {
			Collections.sort(vals);
			return vals.get(0);
		}

		return 0;
	}

	/**
	 * 计算 List<Integer> 最小元素的值
	 *
	 * @param vals
	 * @return
	 */
	public static int calListIntMaxValue(List<Integer> vals) {
		if (vals.size() > 0) {
			Collections.sort(vals);
			return vals.get(vals.size() - 1);
		}

		return 0;
	}

	/**
	 * 计算 List<Integer> 最小元素的值
	 *
	 * @param vals
	 * @return
	 */
	public static int calListIntMinIndex(List<Integer> vals) {
		int idx = -1;
		int minVal = Integer.MAX_VALUE;
		if (CollectionUtils.isNotEmpty(vals)) {
			for (int i = 0; i < vals.size(); i++) {
				int val = vals.get(i);
				if (val < minVal) {
					minVal = val;
					idx = i;
				}
			}
		}
		return idx;
	}

	/**
	 * 计算 List<Integer> 最小元素的值
	 *
	 * @param vals
	 * @return
	 */
	public static int calListIntMaxIndex(List<Integer> vals) {
		int idx = -1;
		int maxVal = Integer.MIN_VALUE;
		if (CollectionUtils.isNotEmpty(vals)) {
			for (int i = 0; i < vals.size(); i++) {
				int val = vals.get(i);
				if (val > maxVal) {
					maxVal = val;
					idx = i;
				}
			}
		}
		return idx;
	}

	/**
	 * 随机计算一个 (0 至 count-1) 范围内的一个不重复的整数列表，且列表数量为 count
	 *
	 * @param count
	 * @return
	 */
	public static Set<Integer> calRandomIntegerSet(int count) {
		Set<Integer> result = Sets.newHashSet();

		while (true) {
			int randomValue = (int) (Math.random() * count);
			if (!result.contains(randomValue)) {
				result.add(randomValue);
			}

			if (result.size() == count) {
				break;
			}
		}

		return result;
	}

	/**
	 * list1 是否等于 list2，用equals比较具体值
	 *
	 * @param list1
	 * @param list2
	 * @return
	 */
	public static <T> boolean listEqualsList(List<T> list1, List<T> list2) {
		boolean result = true;

		if (list1.size() == list2.size()) {
			for (T item : list1) {
				if (!listContainsItem(list2, item)) {
					result = false;
				}
			}
		} else {
			result = false;
		}

		return result;
	}

	/**
	 * list1 是否包含 list2 的全部值，用equals比较具体值
	 *
	 * @param list1
	 * @param list2
	 * @return
	 */
	public static <T> boolean listContainsList(List<T> list1, List<T> list2) {
		if (list1.size() >= list2.size()) {
			for (T item : list2) {
				if (!listContainsItem(list1, item)) {
					return false;
				}
			}
		} else {
			return false;
		}

		return true;
	}

	/**
	 * list 是否包含 item
	 *
	 * @param list
	 * @param item
	 * @return
	 */
	public static <T> boolean listContainsItem(List<T> list, T item) {
		for (T listItem : list) {
			if (listItem.equals(item)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * list 内的元素是否包含 item（有一个元素包含 item 就返回 true）
	 *
	 * @param list
	 * @param item
	 * @return
	 */
	public static boolean listContainsStr(List<String> list, String item) {
		for (String str : list) {
			if (str.contains(item)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * list 内的元素是否全部包含 item（全部元素都包含 item 才返回 true）
	 *
	 * @param list
	 * @param item
	 * @return
	 */
	public static boolean listAllContainsStr(List<String> list, String item) {
		if (list.size() > 0) {
			for (String str : list) {
				if (!str.contains(item)) {
					return false;
				}
			}

			return true;
		} else {
			return false;
		}
	}

	/**
	 * 计算 List<Integer> 元素相加总数
	 *
	 * @param list
	 * @return
	 */
	public static int calListIntegerSum(List<Integer> list) {
		int result = 0;

		for (int val : list) {
			result += val;
		}

		return result;
	}

	/**
	 * 计算反向数据列表
	 *
	 * @param list
	 * @return
	 */
	public static <T> List<T> calReverseList(List<T> list) {
		List<T> result = Lists.newArrayList();

		for (int i = list.size() - 1; i >= 0; i--) {
			result.add(list.get(i));
		}

		return result;
	}

	/**
	 * 修正列表数据（1.当列表数据<size，用空值补全；2.当>=size，截取size大小的列表）
	 *
	 * @param list
	 * @param size
	 * @return
	 */
	public static List<String> correctionList(List<String> list, int size) {
		if (list.size() >= size) {
			return list.stream().limit(size).collect(Collectors.toList());
		} else {
			List<String> result = Lists.newArrayList();
			result.addAll(list);

			for (int i = 0; i < size - list.size(); i++) {
				result.add("");
			}

			return result;
		}
	}

	/**
	 * 把数据列表拆分数据列表为 listCount 个小表
	 *
	 * @param list
	 * @param listCount
	 * @return
	 */
	public static <T> List<List<T>> splitToListsByListCount(List<T> list, int listCount) {
		if (listCount > 0) {
			int listItemCount = list.size() / listCount;
			if (list.size() % listCount > 0) {
				listItemCount += 1;
			}
			return splitToListsByListItemCount(list, listItemCount);
		}

		return Lists.newArrayList();
	}

	/**
	 * 把数据列表拆分数据列表为 N 个小表，每个小表的数据量等于 listItemCount（最后一个除外）
	 *
	 * @param list
	 * @param listItemCount
	 * @return
	 */
	public static <T> List<List<T>> splitToListsByListItemCount(List<T> list, int listItemCount) {
		Map<Integer, List<T>> result = Maps.newHashMap();

		int mapIndex = -1;
		int count = list.size();
		for (int i = 0; i < count; i++) {
			if (i % listItemCount == 0) {
				mapIndex++;
				result.put(mapIndex, new ArrayList<T>());
			}

			result.get(mapIndex).add(list.get(i));
		}

		return CustomListUtils.collectionToList(result.values());
	}

	/**
	 * List 非全排列算法处理
	 *
	 * @param list
	 * @return
	 */
	public static <T> List<KeyValuePair<T, T>> calListPermutations(List<T> list) {
		List<KeyValuePair<T, T>> result = Lists.newArrayList();

		Set<Integer> finishedIndexSet = Sets.newHashSet();

		for (int i = 0; i < list.size(); i++) {
			for (int j = 0; j < list.size(); j++) {
				if (!finishedIndexSet.contains(j) && i != j) {
					result.add(new KeyValuePair<T, T>(list.get(i), list.get(j)));
				}
			}

			if (!finishedIndexSet.contains(i)) {
				finishedIndexSet.add(i);
			}
		}

		return result;
	}

	/**
	 * List 全排列算法处理
	 *
	 * @param dataList
	 * @return
	 */
	public static <T> List<List<T>> calListFullPermutations(List<T> dataList) {
		List<List<T>> result = Lists.newArrayList();

		int dataLength = dataList.size();
		List<T> targetList = Lists.newArrayList();
		calListFullPermutations(dataList, dataLength, targetList, result);

		return result;
	}

	/**
	 * List 全排列算法处理（递归算法：将数据分为两部分，递归将数据从左侧移右侧实现全排列）
	 *
	 * @param dataList
	 * @param dataLength
	 * @param targetList
	 * @param result
	 */
	private static <T> void calListFullPermutations(List<T> dataList, int dataLength, List<T> targetList, List<List<T>> result) {
		if (targetList.size() == dataLength) {
			List<T> list = Lists.newArrayList();
			for (T item : targetList) {
				list.add(item);
			}

			if (list.size() > 0) {
				result.add(list);
			}

			return;
		}

		for (int i = 0; i < dataList.size(); i++) {
			List<T> newDataList = Lists.newArrayList();
			List<T> newTargetList = Lists.newArrayList();
			newDataList.addAll(dataList);
			newTargetList.addAll(targetList);

			newTargetList.add(newDataList.get(i));
			newDataList.remove(i);

			calListFullPermutations(newDataList, dataLength, newTargetList, result);
		}
	}

	/**
	 * next permutation算法算出全排列
	 *
	 * @param c
	 * @return
	 */
	public static List<List<Integer>> nextPermutations(List<Integer> c) {
		List<List<Integer>> list = Lists.newArrayList();
		list.add(Lists.newArrayList(c));
		while ((c = nextPermutation(c)) != null) {
			list.add(Lists.newArrayList(c));
		}
		return list;
	}

	// modifies c to next permutation or returns null if such permutation does not exist
	private static List<Integer> nextPermutation(final List<Integer> c) {
		// 1. finds the largest k, that c[k] < c[k+1]
		int first = getFirst(c);
		if (first == -1) return null; // no greater permutation
		// 2. find last index toSwap, that c[k] < c[toSwap]
		int toSwap = c.size() - 1;
		while (c.get(first) >= c.get(toSwap))
			--toSwap;
		// 3. swap elements with indexes first and last
		swap(c, first++, toSwap);
		// 4. reverse sequence from k+1 to n (inclusive) 
		toSwap = c.size() - 1;
		while (first < toSwap)
			swap(c, first++, toSwap--);
		return c;
	}

	// finds the largest k, that c[k] < c[k+1]
	// if no such k exists (there is not greater permutation), return -1
	private static int getFirst(final List<Integer> c) {
		for (int i = c.size() - 2; i >= 0; --i)
			if (c.get(i) < c.get(i + 1))
				return i;
		return -1;
	}

	// swaps two elements (with indexes i and j) in array 
	private static void swap(final List<Integer> c, final int i, final int j) {
		final int tmp = c.get(i);
		c.set(i, c.get(j));
		c.set(j, tmp);
	}
	
	/*
	public static void main(String[] argv) {
		List<Integer> c = Lists.newArrayList(1, 2, 4, 5, 7);
		List<List<Integer>> list = nextPermutations(c);
		for (List<Integer> l : list) {
			System.out.println(Arrays.toString(l.toArray()));
		}
	}*/

	public static List<List<Integer>> trimPermutations(List<Integer> c) {
		List<List<Integer>> list = Lists.newArrayList();
		int size = c.size();
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (j == i)
					continue;

				List<Integer> perm = trimPermutation(c, i, j);
				list.add(perm);
			}
		}
		return list;
	}

	private static List<Integer> trimPermutation(List<Integer> c, int i, int j) {
		List<Integer> list = Lists.newArrayList();
		int size = c.size();
		list.add(c.get(i));
		for (int k = 0; k < size; k++) {
			if (k == i || k == j)
				continue;
			list.add(c.get(k));
		}
		list.add(c.get(j));
		return list;
	}
	
	/*
	public static void main(String[] argv) {
		List<Integer> c = Lists.newArrayList(1, 2, 4, 5, 7, 9, 11);
		List<List<Integer>> list = trimPermutations(c);
		for (List<Integer> l : list) {
			System.out.println(Arrays.toString(l.toArray()));
		}
	}*/

	/**
	 * List<T> 笛卡尔乘积算法处理
	 *
	 * @param dimValues
	 * @return
	 */
	public static <T> List<List<T>> calListDescartes(List<List<T>> dimValues) {
		List<List<T>> result = Lists.newArrayList();
		List<T> curValues = Lists.newArrayList();

		calListDescartes(dimValues, result, 0, curValues);

		return result;
	}

	/**
	 * List<T> 笛卡尔乘积算法处理
	 *
	 * @param dimValues
	 * @param result
	 * @param layer
	 * @param curValues
	 */
	private static <T> void calListDescartes(List<List<T>> dimValues, List<List<T>> result, int layer, List<T> curValues) {
		if (layer < dimValues.size() - 1) {
			// 大于一个集合时，第一个集合为空
			if (dimValues.get(layer).size() == 0)
				calListDescartes(dimValues, result, layer + 1, curValues);
			else {
				for (int i = 0; i < dimValues.get(layer).size(); i++) {
					List<T> values = Lists.newArrayList();
					values.addAll(curValues);
					values.add(dimValues.get(layer).get(i));

					calListDescartes(dimValues, result, layer + 1, values);
				}
			}
		} else if (layer == dimValues.size() - 1) {
			// 只有一个集合，且集合中没有元素
			if (dimValues.get(layer).size() == 0) {
				result.add(curValues);
			}
			// 只有一个集合，且集合中有元素时：其笛卡尔积就是这个集合元素本身
			else {
				for (int i = 0; i < dimValues.get(layer).size(); i++) {
					List<T> copyCurValues = DeepCopyUtils.copy(curValues);
					copyCurValues.add(dimValues.get(layer).get(i));

					result.add(copyCurValues);
				}
			}
		}
	}

	/**
	 * 多组List<T>的所有可能连接
	 * 例如, 输入1/2的全排列, 3/4的全排列; 结果, 返回1234/1243/2134/2143
	 *
	 * @param segments
	 * @param <T>
	 * @return
	 */
	public static <T> List<List<T>> calListsConcatenations(List<List<List<T>>> segments) {
		List<List<T>> result = Lists.newArrayList();

		for (List<List<T>> segment : segments) {
			List<List<T>> resultNew = Lists.newArrayList();

			if (result.size() == 0) {
				resultNew.addAll(segment);
			} else {
				for (List<T> headList : result) {
					for (List<T> tailList : segment) {
						List<T> concatList = Lists.newArrayList();
						concatList.addAll(headList);
						concatList.addAll(tailList);
						resultNew.add(concatList);
					}
				}
			}

			result = resultNew;
		}

		return result;
	}

	/*
	public static void main(String[] args){
		List<Integer> vals = Lists.newArrayList(700, 100, 400, 580, 999);
		System.out.println(calListIntMinIndex(vals));
		System.out.println(calListIntMaxIndex(vals));
	}*/
}
