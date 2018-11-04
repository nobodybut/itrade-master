package com.trade.biz.domain.tradejob.mocktrading;

import com.trade.biz.dal.tradecore.StockDao;
import com.trade.biz.domain.tradejob.futu.FutunnTradingHelper;
import com.trade.model.tradecore.enums.TrdSideEnum;
import com.trade.model.tradecore.stock.Stock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class MockTradingManager {

	// 依赖注入
	@Resource
	private FutunnTradingHelper futunnTradingHelper;

	@Resource
	private StockDao stockDao;

	public void execute() {
		Stock stock = stockDao.queryByStockID(205189);
		long startMills = System.currentTimeMillis();
		boolean isSuccess = futunnTradingHelper.stockTrading(stock, TrdSideEnum.BUY, 190, 90);
		long spendTime = System.currentTimeMillis() - startMills;
		String a = "";
	}
}
