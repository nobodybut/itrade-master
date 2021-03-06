package com.trade.biz.domain.tradeacq;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.trade.biz.dal.tradecore.StockDao;
import com.trade.common.infrastructure.util.httpclient.HttpClientUtils;
import com.trade.common.infrastructure.util.logger.LogInfoUtils;
import com.trade.common.infrastructure.util.math.CustomNumberUtils;
import com.trade.common.infrastructure.util.string.CustomStringUtils;
import com.trade.common.tradeutil.consts.FutunnConsts;
import com.trade.model.tradecore.enums.MarketEnum;
import com.trade.model.tradecore.enums.StockPlateEnum;
import com.trade.model.tradecore.stock.Stock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class UsStockAcq {

	// 相关常量
	private List<Integer> PLATE_IDS = Lists.newArrayList(StockPlateEnum.GLOBAL.getPlateID(), StockPlateEnum.NYSE.getPlateID(), StockPlateEnum.NASDAQ.getPlateID(),
			StockPlateEnum.ASE.getPlateID(), StockPlateEnum.CHINA.getPlateID(), StockPlateEnum.STAR.getPlateID());
	private List<Integer> PLATE_PAGES = Lists.newArrayList(1, 195, 195, 20, 15, 15);

	// 依赖注入
	@Resource
	private StockDao stockDao;

	public void execute() {
		try {
			for (int i = 0; i < PLATE_IDS.size(); i++) {
				int plateID = PLATE_IDS.get(i);
				int platePages = PLATE_PAGES.get(i);

				for (int page = 0; page < platePages; page++) {
					String plateUrl = String.format(FutunnConsts.FUTUNN_TOP_LIST_URL_TMPL, plateID, page, System.currentTimeMillis());
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
							fillStockBasicInfo(stock);
							stock.setDelete(stock.getMarketValue() <= 0);

							stockDao.insertOrUpdate(stock);
						}
					}
				}
			}
		} catch (Throwable ex) {
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			log.error(String.format(LogInfoUtils.NO_DATA_TMPL, methodName), ex);
		}
	}

	/**
	 * 填充股票 公司英文名称、流通市值、市盈率 数据
	 *
	 * @param stock
	 */
	private void fillStockBasicInfo(Stock stock) {
		String json = HttpClientUtils.getHTML(String.format(FutunnConsts.FUTUNN_QUOTE_BASIC_URL_TMPL, stock.getStockID(), String.valueOf(System.currentTimeMillis())));
		String enName = CustomStringUtils.substringBetween(json, "\"en_name\":", ",", "\"", "\"");
		long marketValue = (long) CustomNumberUtils.toDouble(CustomStringUtils.substringBetween(json, "\"mv\":", ",").replace(" ", ""));
		float earnings = CustomNumberUtils.toFloat(CustomStringUtils.substringBetween(json, "\"eps\":", ",").replace(" ", ""));

		stock.setEnName(enName);
		stock.setMarketValue(marketValue);
		stock.setEarnings(earnings);
	}

	/**
	 * 根据 plateID 计算交易所ID
	 *
	 * @param plateID
	 * @return
	 */
	private int calExchangeID(int plateID) {
		if (plateID == StockPlateEnum.NYSE.getPlateID()) {
			return 1; // 纽交所
		} else if (plateID == StockPlateEnum.ASE.getPlateID()) {
			return 2; // 美交所
		} else if (plateID == StockPlateEnum.NASDAQ.getPlateID()) {
			return 3; // 纳斯达克
		} else {
			return 0;
		}
	}
}
