package com.trade.common.infrastructure.util.date;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.trade.common.infrastructure.util.collection.KeyValuePair;
import com.trade.common.infrastructure.util.collection.MapSortUtils;
import com.trade.common.infrastructure.util.enums.SortEnum;
import org.apache.commons.lang.StringUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;

public class CustomDateMathUtils {

	/**
	 * 计算某日期+周边几天等到的日期列表（如果提前向前的日期早于当前日期，不要补全）
	 *
	 * @param localDate
	 * @param surroundingDays
	 * @return
	 */
	public static List<LocalDate> calDateAddSurroundingDays(LocalDate localDate, int surroundingDays) {
		List<LocalDate> result = Lists.newArrayList();

		LocalDate today = LocalDate.now();

		for (int i = -surroundingDays; i <= surroundingDays; i++) {
			LocalDate curDate = localDate.plusDays(i);
			if (curDate.equals(today) || curDate.isAfter(today)) {
				result.add(curDate);
			}
		}

		return result;
	}

	/**
	 * 计算某日期+周边几天等到的日期列表（如果提前向前的日期早于当前日期，需要补全）
	 *
	 * @param localDate
	 * @param surroundingDays
	 * @return
	 */
	public static List<LocalDate> calDateAddSurroundingDaysMustCompletion(LocalDate localDate, int surroundingDays) {
		List<LocalDate> result = Lists.newArrayList();

		result = calDateAddSurroundingDays(localDate, surroundingDays);
		if (result.size() > 0) {
			int remainderDays = (surroundingDays * 2 + 1) - result.size();
			if (remainderDays > 0) {
				LocalDate maxDate = result.get(result.size() - 1);
				for (int i = 1; i <= remainderDays; i++) {
					result.add(maxDate.plusDays(i));
				}
			}
		}

		return result;
	}

	/**
	 * 计算指定日期开始未来指定几个月的日期对数据
	 *
	 * @param localDate
	 * @param monthCount
	 * @return
	 */
	public static List<KeyValuePair<LocalDate, LocalDate>> calDatePairListByMonthCount(LocalDate localDate, int monthCount) {
		List<KeyValuePair<LocalDate, LocalDate>> result = Lists.newArrayList();

		// 定义变量
		LocalDate beforeDate = localDate;
		LocalDate afterDate;

		// 先处理本周的日期对
		int dayOfWeek = localDate.getDayOfWeek().getValue();
		if (dayOfWeek < 6) {
			// 周中
			afterDate = beforeDate.plusDays(6 - dayOfWeek);
			result.add(new KeyValuePair<LocalDate, LocalDate>(beforeDate, afterDate));

			beforeDate = afterDate;
			afterDate = beforeDate.plusDays(2);
			result.add(new KeyValuePair<LocalDate, LocalDate>(beforeDate, afterDate));
		} else {
			// 周末
			afterDate = beforeDate.plusDays(8 - dayOfWeek);
			result.add(new KeyValuePair<LocalDate, LocalDate>(beforeDate, afterDate));
		}

		// 再处理下周开始未来几个月的日期对
		int weekCount = monthCount * 4;
		for (int i = 0; i < weekCount; i++) {
			// 周中
			beforeDate = afterDate;
			afterDate = beforeDate.plusDays(5);
			result.add(new KeyValuePair<LocalDate, LocalDate>(beforeDate, afterDate));

			// 周末
			beforeDate = afterDate;
			afterDate = beforeDate.plusDays(2);
			result.add(new KeyValuePair<LocalDate, LocalDate>(beforeDate, afterDate));
		}

		return result;
	}

	/**
	 * 计算两个日期之间的所有日期对可能的组合（按距离天数倒序排列）
	 *
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static List<KeyValuePair<LocalDate, LocalDate>> calDatePairOtherDatePairList(LocalDate startDate, LocalDate endDate) {
		List<KeyValuePair<LocalDate, LocalDate>> result = Lists.newArrayList();

		// 计算日期之间的所有日期对可能的组合
		Map<String, Integer> datePairMap = Maps.newHashMap();
		int days = CustomDateUtils.getTotalTravelDays(startDate, endDate);
		for (int i = 0; i < days; i++) {
			for (int j = 1; j < days; j++) {
				LocalDate inDate = startDate.plusDays(i);
				LocalDate outDate = inDate.plusDays(j);

				if (outDate.isBefore(endDate) || outDate.equals(endDate)) {
					String mapKey = String.format("%s|%s", CustomDateFormatUtils.formatDate(inDate), CustomDateFormatUtils.formatDate(outDate));
					datePairMap.put(mapKey, j);
				}
			}
		}

		// 按距离天数倒序排列
		datePairMap = MapSortUtils.sortMapByIntegerValue(datePairMap, SortEnum.DESC);

		// 转换结果
		for (Map.Entry<String, Integer> entry : datePairMap.entrySet()) {
			String[] dateArr = StringUtils.split(entry.getKey(), "|");
			if (dateArr.length == 2) {
				result.add(new KeyValuePair<LocalDate, LocalDate>(CustomDateParseUtils.parseDate(dateArr[0]), CustomDateParseUtils.parseDate(dateArr[1])));
			}
		}

		return result;
	}

	/**
	 * 根据月份计算每个月第一个完整周的日期列表（当年年份）
	 *
	 * @return
	 */
	public static List<LocalDate> calMonthFirstWeekDates(int month) {
		return calMonthFirstWeekDates(LocalDate.now().getYear(), month);
	}

	/**
	 * 根据月份计算每个月第一个完整周的日期列表（可指定年份）
	 *
	 * @return
	 */
	public static List<LocalDate> calMonthFirstWeekDates(int year, int month) {
		List<LocalDate> result = Lists.newArrayList();

		LocalDate date = LocalDate.of(year, month, 1).with(TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY));
		for (int i = 0; i < 7; i++) {
			result.add(date.plusDays(i));
		}

		return result;
	}

	/**
	 * 通过分钟数计算 "X小时Y分钟" 格式的字符串
	 *
	 * @param minutes
	 * @return
	 */
	public static String calCNDateTimeHumanByMinutes(int minutes) {
		StringBuilder stringBuilder = new StringBuilder();

		if (minutes < 60) {
			stringBuilder.append(minutes);
			stringBuilder.append("分钟");
		} else {
			int modMinutes = minutes % 60;
			stringBuilder.append((minutes - modMinutes) / 60);
			stringBuilder.append("小时");

			if (modMinutes > 0) {
				stringBuilder.append(modMinutes);
				stringBuilder.append("分钟");
			}
		}

		return stringBuilder.toString();
	}

	/**
	 * 计算距离当前日期N周的一周日期列表（周一开始）
	 *
	 * @param nextNWeek
	 * @return
	 */
	public static List<LocalDate> calNextNWeekDateList(int nextNWeek) {
		List<LocalDate> result = Lists.newArrayList();

		DayOfWeek todayOfWeek = LocalDate.now().getDayOfWeek();
		int daysToWeekMonday = 8 - todayOfWeek.getValue();
		LocalDate nextNWeekMonday = LocalDate.now().plusDays(daysToWeekMonday).plusWeeks(nextNWeek - 1);

		for (int i = 0; i < 7; i++) {
			result.add(nextNWeekMonday.plusDays(i));
		}

		return result;
	}

	/**
	 * 计算起始天数、终止天数包含的周几数据列表
	 *
	 * @param startDate
	 * @param endDate
	 * @param isAddLastDay
	 * @return
	 */
	public static List<DayOfWeek> calDayOfWeekList(LocalDate startDate, LocalDate endDate, boolean isAddLastDay) {
		List<DayOfWeek> result = Lists.newArrayList();

		long days = CustomDateUtils.getDurationBetween(startDate, endDate).toDays();
		if (isAddLastDay) {
			days++;
		}
		for (int i = 0; i < days; i++) {
			DayOfWeek dayOfWeek = startDate.plusDays(i).getDayOfWeek();
			if (!result.contains(dayOfWeek)) {
				result.add(dayOfWeek);
			}
		}

		return result;
	}
}
