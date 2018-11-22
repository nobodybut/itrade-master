package com.trade.biz.domain.tradejob.job;

import com.trade.biz.domain.tradeacq.DayKLineAcq;
import com.trade.common.infrastructure.business.context.ApplicationContextUtils;
import com.trade.common.infrastructure.business.quartz.AbstractQuartzJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DayKLineAcq_Job extends AbstractQuartzJob {

	@Override
	protected Logger getLogger() {
		return LoggerFactory.getLogger(getClass());
	}

	@Override
	protected void execute() {
		DayKLineAcq dayKLineAcq = (DayKLineAcq) ApplicationContextUtils.getBean("dayKLineAcq");
		dayKLineAcq.execute();
	}
}
