package com.trade.common.infrastructure.business.quartz;

import com.trade.common.infrastructure.util.logger.LogInfoUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;

public abstract class AbstractQuartzJob implements Job {

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {

		Logger logger = getLogger();
		long startTimeMillis = System.currentTimeMillis();

		try {
			logger.info(LogInfoUtils.createPerformStartInfo());
			execute();
			logger.info(LogInfoUtils.createPerformSuccessInfo(startTimeMillis));
		} catch (Throwable ex) {
			logger.error(LogInfoUtils.createPerformErrorInfo(startTimeMillis), ex);
		}
	}

	/**
	 * 设置任务 Logger
	 *
	 * @return
	 */
	protected abstract Logger getLogger();

	/**
	 * 设置具体执行任务过程
	 */
	protected abstract void execute();
}
