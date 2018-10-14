package com.itrade.common.infrastructure.util.collection;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.lang.StringUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

public class ListSortUtils {

	/**
	 * 锁对象
	 */
	private static final Object s_lockObj = new Object();

	/**
	 * 存放不同线程的处理对象
	 */
	private static ThreadLocal<ListSort> processObj = null;

	/**
	 * 返回一个ThreadLocal的processObj,每个线程只会new一次processObj
	 *
	 * @return
	 */
	private static ListSort getProcessObj() {

		if (processObj == null) {
			synchronized (s_lockObj) {
				if (processObj == null) {
					processObj = new ThreadLocal<ListSort>() {

						@Override
						protected ListSort initialValue() {
							return new ListSortUtils().new ListSort();
						}
					};
				}
			}
		}

		return processObj.get();
	}

	/**
	 * 排序List（多属性）
	 *
	 * @param list
	 * @param properties
	 */
	public static <V> void sort(List<V> list, final String... properties) {
		getProcessObj().sort(list, properties);
	}


	private class ListSort {

		/**
		 * List 元素的多个属性进行排序。例如 ListSorter.sort(list, "name", "age")，则先按 name 属性排序，name 相同的元素按 age 属性排序。
		 *
		 * @param list       包含要排序元素的 List
		 * @param properties 要排序的属性。前面的值优先级高。
		 */
		private <V> void sort(List<V> list, final String... properties) {
			Collections.sort(list, new Comparator<V>() {
				public int compare(V o1, V o2) {
					if (o1 == null && o2 == null) {
						return 0;
					}
					if (o1 == null) {
						return -1;
					}
					if (o2 == null) {
						return 1;
					}

					for (String property : properties) {
						String[] arrProperty = StringUtils.split(property, " ");
						Comparator<V> c = new BeanComparator<V>(arrProperty[0]);
						int result = (arrProperty.length == 1 || arrProperty[1].equals("ASC")) ? c.compare(o1, o2) : c.compare(o2, o1);
						if (result != 0) {
							return result;
						}
					}
					return 0;
				}

				@Override
				public Comparator<V> reversed() {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public Comparator<V> thenComparing(Comparator<? super V> other) {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public <U> Comparator<V> thenComparing(Function<? super V, ? extends U> keyExtractor, Comparator<? super U> keyComparator) {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public <U extends Comparable<? super U>> Comparator<V> thenComparing(Function<? super V, ? extends U> keyExtractor) {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public Comparator<V> thenComparingInt(ToIntFunction<? super V> keyExtractor) {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public Comparator<V> thenComparingLong(ToLongFunction<? super V> keyExtractor) {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public Comparator<V> thenComparingDouble(ToDoubleFunction<? super V> keyExtractor) {
					// TODO Auto-generated method stub
					return null;
				}
			});
		}
	}
}