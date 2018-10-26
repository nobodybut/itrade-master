package com.trade.biz.domain.tradejob.job;

import com.trade.biz.domain.tradejob.quote.MinuteQuoteAcq;
import com.trade.biz.domain.tradejob.stock.UsStockAcq;
import com.trade.common.infrastructure.business.context.ApplicationContextUtils;
import com.trade.common.infrastructure.business.quartz.AbstractQuartzJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MinuteQuoteAcq_Job extends AbstractQuartzJob {

	@Override
	protected Logger getLogger() {
		return LoggerFactory.getLogger(getClass());
	}

	@Override
	protected void execute() {
		MinuteQuoteAcq minuteQuoteAcq = (MinuteQuoteAcq) ApplicationContextUtils.getBean("minuteQuoteAcq");
		minuteQuoteAcq.execute();
	}
}
