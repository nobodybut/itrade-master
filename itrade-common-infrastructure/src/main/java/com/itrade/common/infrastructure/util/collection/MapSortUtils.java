package com.itrade.common.infrastructure.util.collection;

import com.google.common.collect.Maps;
import com.itrade.common.infrastructure.util.enums.SortEnum;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

public class MapSortUtils {

	/**
	 * 处理 Map<Integer, T> 按 key 进行排序
	 *
	 * @param map
	 * @return
	 */
	public static <T> Map<Integer, T> sortMapByIntegerKey(final Map<Integer, T> map, final SortEnum sortEnum) {
		if (map == null) {
			return Maps.newTreeMap();
		}

		Map<Integer, T> sortMap = new TreeMap<Integer, T>(new Comparator<Integer>() {
			public int compare(Integer o1, Integer o2) {
				if (sortEnum == SortEnum.ASC) {
					return o1.compareTo(o2);
				} else {
					return o2.compareTo(o1);
				}
			}

			@Override
			public Comparator<Integer> reversed() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Comparator<Integer> thenComparing(Comparator<? super Integer> other) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <U> Comparator<Integer> thenComparing(Function<? super Integer, ? extends U> keyExtractor, Comparator<? super U> keyComparator) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <U extends Comparable<? super U>> Comparator<Integer> thenComparing(Function<? super Integer, ? extends U> keyExtractor) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Comparator<Integer> thenComparingInt(ToIntFunction<? super Integer> keyExtractor) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Comparator<Integer> thenComparingLong(ToLongFunction<? super Integer> keyExtractor) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Comparator<Integer> thenComparingDouble(ToDoubleFunction<? super Integer> keyExtractor) {
				// TODO Auto-generated method stub
				return null;
			}
		});

		sortMap.putAll(map);
		return sortMap;
	}

	/**
	 * 处理 Map<Double, T> 按 key 进行排序
	 *
	 * @param map
	 * @return
	 */
	public static <T> Map<Double, T> sortMapByDoubleKey(final Map<Double, T> map, final SortEnum sortEnum) {
		if (map == null) {
			return Maps.newTreeMap();
		}

		Map<Double, T> sortMap = new TreeMap<Double, T>(new Comparator<Double>() {
			public int compare(Double o1, Double o2) {
				if (sortEnum == SortEnum.ASC) {
					return o1.compareTo(o2);
				} else {
					return o2.compareTo(o1);
				}
			}

			@Override
			public Comparator<Double> reversed() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Comparator<Double> thenComparing(Comparator<? super Double> other) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <U> Comparator<Double> thenComparing(Function<? super Double, ? extends U> keyExtractor, Comparator<? super U> keyComparator) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <U extends Comparable<? super U>> Comparator<Double> thenComparing(Function<? super Double, ? extends U> keyExtractor) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Comparator<Double> thenComparingInt(ToIntFunction<? super Double> keyExtractor) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Comparator<Double> thenComparingLong(ToLongFunction<? super Double> keyExtractor) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Comparator<Double> thenComparingDouble(ToDoubleFunction<? super Double> keyExtractor) {
				// TODO Auto-generated method stub
				return null;
			}
		});

		sortMap.putAll(map);
		return sortMap;
	}

	/**
	 * 处理 Map<String, T> 按 key 进行排序
	 *
	 * @param map
	 * @return
	 */
	public static <T> Map<String, T> sortMapByStringKey(final Map<String, T> map, final SortEnum sortEnum) {
		if (map == null) {
			return Maps.newTreeMap();
		}

		Map<String, T> sortMap = new TreeMap<String, T>(new Comparator<String>() {
			public int compare(String o1, String o2) {
				if (sortEnum == SortEnum.ASC) {
					return o1.compareTo(o2);
				} else {
					return o2.compareTo(o1);
				}
			}

			@Override
			public Comparator<String> reversed() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Comparator<String> thenComparing(Comparator<? super String> other) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <U> Comparator<String> thenComparing(Function<? super String, ? extends U> keyExtractor, Comparator<? super U> keyComparator) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <U extends Comparable<? super U>> Comparator<String> thenComparing(Function<? super String, ? extends U> keyExtractor) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Comparator<String> thenComparingInt(ToIntFunction<? super String> keyExtractor) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Comparator<String> thenComparingLong(ToLongFunction<? super String> keyExtractor) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Comparator<String> thenComparingDouble(ToDoubleFunction<? super String> keyExtractor) {
				// TODO Auto-generated method stub
				return null;
			}
		});

		sortMap.putAll(map);
		return sortMap;
	}

	/**
	 * 处理 Map<T, Integer> 按 value 进行排序
	 *
	 * @param map
	 * @param sortEnum
	 * @return
	 */
	public static <T> Map<T, Integer> sortMapByIntegerValue(final Map<T, Integer> map, final SortEnum sortEnum) {
		if (map == null) {
			return Maps.newLinkedHashMap();
		}

		Map<T, Integer> sortedMap = new LinkedHashMap<T, Integer>();
		List<Entry<T, Integer>> entryList = new ArrayList<Entry<T, Integer>>(map.entrySet());
		Collections.sort(entryList, new Comparator<Entry<T, Integer>>() {
			public int compare(Entry<T, Integer> o1, Entry<T, Integer> o2) {
				if (sortEnum == SortEnum.ASC) {
					return o1.getValue().compareTo(o2.getValue());
				} else {
					return o2.getValue().compareTo(o1.getValue());
				}
			}

			@Override
			public Comparator<Entry<T, Integer>> reversed() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Comparator<Entry<T, Integer>> thenComparing(Comparator<? super Entry<T, Integer>> other) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <U> Comparator<Entry<T, Integer>> thenComparing(Function<? super Entry<T, Integer>, ? extends U> keyExtractor, Comparator<? super U> keyComparator) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <U extends Comparable<? super U>> Comparator<Entry<T, Integer>> thenComparing(Function<? super Entry<T, Integer>, ? extends U> keyExtractor) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Comparator<Entry<T, Integer>> thenComparingInt(ToIntFunction<? super Entry<T, Integer>> keyExtractor) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Comparator<Entry<T, Integer>> thenComparingLong(ToLongFunction<? super Entry<T, Integer>> keyExtractor) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Comparator<Entry<T, Integer>> thenComparingDouble(ToDoubleFunction<? super Entry<T, Integer>> keyExtractor) {
				// TODO Auto-generated method stub
				return null;
			}
		});

		Iterator<Entry<T, Integer>> iter = entryList.iterator();
		Entry<T, Integer> tmpEntry = null;
		while (iter.hasNext()) {
			tmpEntry = iter.next();
			sortedMap.put(tmpEntry.getKey(), tmpEntry.getValue());
		}

		return sortedMap;
	}

	/**
	 * 处理 Map<T, Double> 按 value 进行排序
	 *
	 * @param map
	 * @param sortEnum
	 * @return
	 */
	public static <T> Map<T, Double> sortMapByDoubleValue(final Map<T, Double> map, final SortEnum sortEnum) {
		if (map == null) {
			return Maps.newLinkedHashMap();
		}

		Map<T, Double> sortedMap = new LinkedHashMap<T, Double>();
		List<Entry<T, Double>> entryList = new ArrayList<Entry<T, Double>>(map.entrySet());
		Collections.sort(entryList, new Comparator<Entry<T, Double>>() {
			public int compare(Entry<T, Double> o1, Entry<T, Double> o2) {
				if (sortEnum == SortEnum.ASC) {
					return o1.getValue().compareTo(o2.getValue());
				} else {
					return o2.getValue().compareTo(o1.getValue());
				}
			}

			@Override
			public Comparator<Entry<T, Double>> reversed() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Comparator<Entry<T, Double>> thenComparing(Comparator<? super Entry<T, Double>> other) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <U> Comparator<Entry<T, Double>> thenComparing(Function<? super Entry<T, Double>, ? extends U> keyExtractor, Comparator<? super U> keyComparator) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <U extends Comparable<? super U>> Comparator<Entry<T, Double>> thenComparing(Function<? super Entry<T, Double>, ? extends U> keyExtractor) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Comparator<Entry<T, Double>> thenComparingInt(ToIntFunction<? super Entry<T, Double>> keyExtractor) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Comparator<Entry<T, Double>> thenComparingLong(ToLongFunction<? super Entry<T, Double>> keyExtractor) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Comparator<Entry<T, Double>> thenComparingDouble(ToDoubleFunction<? super Entry<T, Double>> keyExtractor) {
				// TODO Auto-generated method stub
				return null;
			}
		});

		Iterator<Entry<T, Double>> iter = entryList.iterator();
		Entry<T, Double> tmpEntry = null;
		while (iter.hasNext()) {
			tmpEntry = iter.next();
			sortedMap.put(tmpEntry.getKey(), tmpEntry.getValue());
		}

		return sortedMap;
	}

	/**
	 * 处理 Map<T, String> 按 value 进行排序
	 *
	 * @param map
	 * @param sortEnum
	 * @return
	 */
	public static <T> Map<T, String> sortMapByStringValue(final Map<T, String> map, final SortEnum sortEnum) {
		if (map == null) {
			return Maps.newLinkedHashMap();
		}

		Map<T, String> sortedMap = new LinkedHashMap<T, String>();
		List<Entry<T, String>> entryList = new ArrayList<Entry<T, String>>(map.entrySet());
		Collections.sort(entryList, new Comparator<Entry<T, String>>() {
			public int compare(Entry<T, String> o1, Entry<T, String> o2) {
				if (sortEnum == SortEnum.ASC) {
					return o1.getValue().compareTo(o2.getValue());
				} else {
					return o2.getValue().compareTo(o1.getValue());
				}
			}

			@Override
			public Comparator<Entry<T, String>> reversed() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Comparator<Entry<T, String>> thenComparing(Comparator<? super Entry<T, String>> other) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <U> Comparator<Entry<T, String>> thenComparing(Function<? super Entry<T, String>, ? extends U> keyExtractor, Comparator<? super U> keyComparator) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <U extends Comparable<? super U>> Comparator<Entry<T, String>> thenComparing(Function<? super Entry<T, String>, ? extends U> keyExtractor) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Comparator<Entry<T, String>> thenComparingInt(ToIntFunction<? super Entry<T, String>> keyExtractor) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Comparator<Entry<T, String>> thenComparingLong(ToLongFunction<? super Entry<T, String>> keyExtractor) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Comparator<Entry<T, String>> thenComparingDouble(ToDoubleFunction<? super Entry<T, String>> keyExtractor) {
				// TODO Auto-generated method stub
				return null;
			}
		});

		Iterator<Entry<T, String>> iter = entryList.iterator();
		Entry<T, String> tmpEntry = null;
		while (iter.hasNext()) {
			tmpEntry = iter.next();
			sortedMap.put(tmpEntry.getKey(), tmpEntry.getValue());
		}

		return sortedMap;
	}
}
