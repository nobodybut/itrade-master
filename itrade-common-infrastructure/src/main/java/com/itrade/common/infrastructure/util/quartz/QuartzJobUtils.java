package com.itrade.common.infrastructure.util.quartz;

import com.itrade.common.infrastructure.util.string.CustomStringUtils;
import org.quartz.Job;

public class QuartzJobUtils {

	/**
	 * 计算任务名称
	 *
	 * @param jobClass
	 * @return
	 */
	public static String calJobName(Class<? extends Job> jobClass) {
		return CustomStringUtils.toLowerCaseFirstOne(jobClass.getName().substring(jobClass.getName().lastIndexOf(".") + 1));
	}
}
