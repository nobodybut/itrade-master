package com.trade.biz.domain.tradejob.initservlet;

import com.trade.biz.domain.tradejob.job.*;
import com.trade.common.infrastructure.business.quartz.QuartzJobManagerUtils;
import com.trade.common.infrastructure.util.logger.LogInfoUtils;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobManager {

	// 相关变量
	private boolean _isRunning = false;

	// 日志记录
	private static final Logger s_logger = LoggerFactory.getLogger(JobManager.class);

	/**
	 * 私有构造函数
	 */
	private JobManager() {
	}

	/**
	 * 开始运行
	 */
	public void run() {
		if (!_isRunning) {
			try {
				Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
				String groupName = "tradeJobGroup";

				// 添加任务
				QuartzJobManagerUtils.performScheduleJob(scheduler, groupName, s_logger, MockTrading_Job.class);
				QuartzJobManagerUtils.performScheduleJob(scheduler, groupName, s_logger, MinuteQuoteAnalysis_Job.class);
				QuartzJobManagerUtils.performScheduleJob(scheduler, groupName, s_logger, UsStockAcq_Job.class);
				QuartzJobManagerUtils.performScheduleJob(scheduler, groupName, s_logger, MinuteQuoteAcq_Job.class);
				QuartzJobManagerUtils.performScheduleJob(scheduler, groupName, s_logger, StockTradePlanned_Job.class);

				// 开始执行任务
				scheduler.start();
			} catch (SchedulerException ex) {
				String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
				s_logger.error(String.format(LogInfoUtils.NO_DATA_TMPL, methodName), ex);
			}

			// 设置 _isRunning
			_isRunning = true;
		}
	}

	/********************************** 单例模式实现 **********************************/
	/**
	 * 类级的内部类，也就是静态的成员式内部类，该内部类的实例与外部类的实例 没有绑定关系，而且只有被调用到时才会装载，从而实现了延迟加载。
	 */
	private static class JobManagerHolder {
		/**
		 * 静态初始化器，由JVM来保证线程安全
		 */
		private static JobManager instance = new JobManager();
	}

	/**
	 * 当getInstance方法第一次被调用的时候，它第一次读取
	 */
	public static JobManager getInstance() {
		return JobManagerHolder.instance;
	}
}
