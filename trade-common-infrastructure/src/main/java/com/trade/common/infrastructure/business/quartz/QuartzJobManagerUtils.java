package com.trade.common.infrastructure.business.quartz;

import com.trade.common.infrastructure.business.conf.PropertiesUtils;
import com.trade.common.infrastructure.util.logger.LogInfoUtils;
import com.trade.common.infrastructure.util.quartz.QuartzJobUtils;
import com.trade.common.infrastructure.util.string.CustomStringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.quartz.*;
import org.slf4j.Logger;

import java.util.Date;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

public class QuartzJobManagerUtils {

	/**
	 * 设置任务 scheduler
	 *
	 * @param scheduler
	 * @param groupName
	 * @param logger
	 * @param jobClass
	 */
	public static void performScheduleJob(Scheduler scheduler, String groupName, Logger logger, Class<? extends Job> jobClass) {
		String jobName = QuartzJobUtils.calJobName(jobClass);

		if (PropertiesUtils.getBooleanValue(jobName + "_Start")) {
			try {
				// 设置延迟开始时间（分钟）
				int delayMinutes = PropertiesUtils.getIntValue(jobName + "_Delay");
				Date triggerStartTime = DateUtils.addMinutes(new Date(), delayMinutes);

				// 设置任务间隔时间（分钟）
				SimpleScheduleBuilder schedule = SimpleScheduleBuilder.simpleSchedule();
				int intervalMinutes = PropertiesUtils.getIntValue(jobName + "_Interval");
				if (intervalMinutes > 0) {
					schedule.withIntervalInMinutes(intervalMinutes).repeatForever();
				}

				// 加入任务
				JobDetail job = newJob(jobClass).withIdentity(jobName, groupName).build();
				Trigger trigger = newTrigger().withIdentity(jobName + "_Trigger", groupName).startAt(triggerStartTime).withSchedule(schedule).build();
				scheduler.scheduleJob(job, trigger);
			} catch (SchedulerException ex) {
				logger.error(String.format(LogInfoUtils.NO_DATA_TMPL, Thread.currentThread().getStackTrace()[1].getMethodName()), ex);
			}
		}
	}

	/**
	 * 设置任务 scheduler
	 *
	 * @param scheduler
	 * @param groupName
	 * @param logger
	 * @param jobClass
	 * @param delayMinutes
	 * @param intervalMinutes
	 */
	public static void performScheduleJob(Scheduler scheduler, String groupName, Logger logger, Class<? extends Job> jobClass, int delayMinutes, int intervalMinutes) {
		String jobName = CustomStringUtils.toLowerCaseFirstOne(jobClass.getName().substring(jobClass.getName().lastIndexOf(".") + 1));

		try {
			// 设置延迟开始时间（分钟）
			Date triggerStartTime = DateUtils.addMinutes(new Date(), delayMinutes);

			// 设置任务间隔时间（分钟）
			SimpleScheduleBuilder schedule = SimpleScheduleBuilder.simpleSchedule();
			if (intervalMinutes > 0) {
				schedule.withIntervalInMinutes(intervalMinutes).repeatForever();
			}

			// 加入任务
			JobDetail job = newJob(jobClass).withIdentity(jobName, groupName).build();
			Trigger trigger = newTrigger().withIdentity(jobName + "_Trigger", groupName).startAt(triggerStartTime).withSchedule(schedule).build();
			scheduler.scheduleJob(job, trigger);
		} catch (SchedulerException ex) {
			logger.error(String.format(LogInfoUtils.NO_DATA_TMPL, Thread.currentThread().getStackTrace()[1].getMethodName()), ex);
		}
	}
}
