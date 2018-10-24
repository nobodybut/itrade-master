package com.trade.biz.domain.tradejob.stock;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.trade.biz.dal.tradecore.StockDao;
import com.trade.common.infrastructure.util.httpclient.HttpClientUtils;
import com.trade.common.infrastructure.util.logger.LogInfoUtils;
import com.trade.common.infrastructure.util.math.CustomNumberUtils;
import com.trade.common.infrastructure.util.string.CustomStringUtils;
import com.trade.model.tradecore.enums.MarketEnum;
import com.trade.model.tradecore.stock.Stock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class UsStockAcq {

	// 相关常量
	private List<Integer> PLATE_IDS = Lists.newArrayList(200201, 200301, 200303, 200302, 200304, 200305);
	private List<Integer> PLATE_PAGES = Lists.newArrayList(1, 195, 20, 195, 15, 15);

	// 依赖注入
	@Resource
	private StockDao stockDao;

	public void execute() {
		try {
			for (int i = 0; i < PLATE_IDS.size(); i++) {
				int plateID = PLATE_IDS.get(i);
				int platePages = PLATE_PAGES.get(i);

				for (int page = 0; page < platePages; page++) {
					String plateUrl = String.format("https://www.futunn.com/stock/top-list?plate_id=%s&page=%s&_=%s", plateID, page, System.currentTimeMillis());
					String jsonResult = HttpClientUtils.getHTML(plateUrl);
					if (!Strings.isNullOrEmpty(jsonResult)) {
						String listJson = CustomStringUtils.substringBetween(jsonResult, "\"list\":[", "]");
						String[] stockJsons = CustomStringUtils.substringsBetween(listJson, "{", "}");
						for (String stockJson : stockJsons) {
							Stock stock = new Stock();
							stock.setStockID(CustomNumberUtils.toLong(CustomStringUtils.substringBetween(stockJson, "\"security_id\":", ",")));
							stock.setMarketID(MarketEnum.US.ordinal());
							stock.setExchangeID(calExchangeID(plateID));
							stock.setPlateID(plateID);
							stock.setCode(CustomStringUtils.substringBetween(stockJson, "\"security_code\":\"", "\""));
							stock.setName(CustomStringUtils.substringBetween(stockJson, "\"security_name\":\"", "\""));
							stockDao.insertOrUpdate(stock);
						}
					}
				}
			}
		} catch (Throwable ex) {
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			log.error(String.format(LogInfoUtils.NO_DATA_TMPL, methodName), ex);
		}

		int a = 0;
	}

	/**
	 * 根据 plateID 计算交易所ID
	 *
	 * @param plateID
	 * @return
	 */
	private int calExchangeID(int plateID) {
		switch (plateID) {
			case 200301: // 纽交所
				return 1;
			case 200303: // 美交所
				return 2;
			case 200302: // 纳斯达克
				return 3;
			default:
				return 0;
		}
	}
}
