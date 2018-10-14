package com.trade.common.infrastructure.util.paging;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.apache.commons.lang.math.NumberUtils;

import java.util.List;
import java.util.stream.Collectors;

public class PagingUtils {

	/**
	 * 读取分页数据
	 *
	 * @param list
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public static <T> List<T> getPagingList(List<T> list, int page, int pageSize) {
		int totalSize = list.size();
		int skipCount = (page - 1) * pageSize;
		if (skipCount > totalSize) {
			skipCount = totalSize;
		}

		return list.stream().skip(skipCount).limit(pageSize).collect(Collectors.toList());
	}

	/**
	 * 计算分页数
	 *
	 * @param allCount
	 * @param pageSize
	 * @return
	 */
	public static int calPageCount(int allCount, int pageSize) {
		int result = allCount / pageSize;
		int mod = allCount % pageSize;
		if (mod > 0)
			result++;

		return result;
	}

	/**
	 * 处理分页Url参数
	 *
	 * @param pageParam
	 * @return
	 */
	public static int calPageParam(String pageParam) {
		int result = 1;

		if (!Strings.isNullOrEmpty(pageParam)) {
			result = NumberUtils.toInt(pageParam);
			if (result == 0)
				result = 1;
		}

		return result;
	}

	/**
	 * 按照 pageSize 大小拆分列表数据为多个小列表数据
	 *
	 * @param list
	 * @param pageSize
	 * @return
	 */
	public static <T> List<List<T>> splitToPagingLists(List<T> list, int pageSize) {
		List<List<T>> result = Lists.newArrayList();

		int pageCount = PagingUtils.calPageCount(list.size(), pageSize);
		for (int page = 1; page <= pageCount; page++) {
			result.add(getPagingList(list, page, pageSize));
		}

		return result;
	}
}
