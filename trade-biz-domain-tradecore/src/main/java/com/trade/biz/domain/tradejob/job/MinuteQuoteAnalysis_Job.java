package com.trade.biz.domain.tradejob.job;

import com.google.common.collect.Lists;
import com.trade.biz.domain.tradejob.stocktradeplanned.MinuteQuoteAnalysis;
import com.trade.common.infrastructure.business.context.ApplicationContextUtils;
import com.trade.common.infrastructure.business.quartz.AbstractQuartzJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MinuteQuoteAnalysis_Job extends AbstractQuartzJob {

	@Override
	protected Logger getLogger() {
		return LoggerFactory.getLogger(getClass());
	}

	@Override
	protected void execute() {
		MinuteQuoteAnalysis minuteQuoteAnalysis = (MinuteQuoteAnalysis) ApplicationContextUtils.getBean("minuteQuoteAnalysis");
		minuteQuoteAnalysis.execute(Lists.newArrayList());
	}
}
