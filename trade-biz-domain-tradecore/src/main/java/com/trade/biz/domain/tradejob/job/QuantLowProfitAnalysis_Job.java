package com.trade.biz.domain.tradejob.job;

import com.trade.biz.domain.tradequant.quanttradeanalysis.QuantLowProfitAnalysisManager;
import com.trade.common.infrastructure.business.context.ApplicationContextUtils;
import com.trade.common.infrastructure.business.quartz.AbstractQuartzJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class QuantLowProfitAnalysis_Job extends AbstractQuartzJob {

	@Override
	protected Logger getLogger() {
		return LoggerFactory.getLogger(getClass());
	}

	@Override
	protected void execute() {
		QuantLowProfitAnalysisManager quantLowProfitAnalysisManager = (QuantLowProfitAnalysisManager) ApplicationContextUtils.getBean("quantLowProfitAnalysisManager");
		quantLowProfitAnalysisManager.execute();
	}
}
