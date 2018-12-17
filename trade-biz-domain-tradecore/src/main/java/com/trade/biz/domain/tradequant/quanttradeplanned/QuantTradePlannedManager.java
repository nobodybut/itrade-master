package com.trade.biz.domain.tradequant.quanttradeplanned;

import com.google.common.collect.Lists;
import com.trade.biz.dal.tradecore.DayKLineDao;
import com.trade.biz.dal.tradecore.QuantTradePlannedDao;
import com.trade.biz.dal.tradecore.StockDao;
import com.trade.biz.domain.tradequant.futu.FutunnAccountHelper;
import com.trade.common.infrastructure.util.date.CustomDateFormatUtils;
import com.trade.common.infrastructure.util.json.CustomJSONUtils;
import com.trade.common.infrastructure.util.logger.LogInfoUtils;
import com.trade.common.tradeutil.consts.QuantTradeConsts;
import com.trade.common.tradeutil.klineutil.DayKLineUtils;
import com.trade.common.tradeutil.quanttradeutil.TradeDateUtils;
import com.trade.common.tradeutil.techindicatorsutil.KDJUtils;
import com.trade.model.tradecore.enums.StockPlateEnum;
import com.trade.model.tradecore.kline.DayKLine;
import com.trade.model.tradecore.quanttrade.QuantTradePlanned;
import com.trade.model.tradecore.stock.Stock;
import com.trade.model.tradecore.techindicators.KDJ;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class QuantTradePlannedManager {

	// 依赖注入
	@Resource
	private StockDao stockDao;

	@Resource
	private DayKLineDao dayKLineDao;

	@Resource
	private FutunnAccountHelper futunnAccountHelper;

	@Resource
	private QuantTradePlannedDao quantTradePlannedDao;

	/**
	 * 处理全部股票计划交易逻辑
	 */
	public void execute() {
		try {
			List<QuantTradePlanned> quantTradePlanneds = Lists.newArrayList();

			// 计算当前交易日期，并处理是否在交易日期段内
			LocalDate tradeDate = TradeDateUtils.getUsCurrentDate();
			if (!TradeDateUtils.isUsTradeDate(tradeDate)) {
				return;
			}

			// 获取富途账户剩余资金金额
			int accountTotalAmount = futunnAccountHelper.getAccountTotalAmount();

			// 计算全部股票计划交易数据（包含计划交易评分）
			List<Stock> allStocks = stockDao.queryListByMarketID(2);
			allStocks = allStocks.stream().filter(x -> x.getPlateID() != StockPlateEnum.GLOBAL.getPlateID()).collect(Collectors.toList());
			for (int i = 0; i < allStocks.size(); i++) {
				Stock stock = allStocks.get(i);

				// 循环处理每只股票的不同场景，计算得出最终的股票买入计划
				QuantTradePlanned quantTradePlanned = calcQuantTradePlanned(stock, tradeDate, accountTotalAmount);
				if (quantTradePlanned != null) {
					quantTradePlanneds.add(quantTradePlanned);
				}
			}

			// 按计划交易综合评分排序结果
			quantTradePlanneds = getNonZeroAndSortQuantTradePlanneds(quantTradePlanneds);
			quantTradePlanneds = quantTradePlanneds.stream().limit(QuantTradeConsts.PLANNED_TRADE_STOCK_MAX_COUNT).collect(Collectors.toList());

			// 把计算结果写入数据库
			for (QuantTradePlanned quantTradePlanned : quantTradePlanneds) {
				quantTradePlannedDao.insertOrUpdate(quantTradePlanned);
			}

			// 记录文件日志
			log.info("QuantTradePlanned SUCCESS! plannedCount={}, tradeDate={}", quantTradePlanneds.size(), CustomDateFormatUtils.formatDate(tradeDate));
		} catch (Exception ex) {
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			log.error(String.format(LogInfoUtils.NO_DATA_TMPL, methodName), ex);
		}
	}

	/**
	 * 计算单个股票计划交易数据（包含计划交易评分）
	 *
	 * @param stock
	 * @param tradeDate
	 * @param accountTotalAmount
	 * @return
	 */
	private QuantTradePlanned calcQuantTradePlanned(Stock stock, LocalDate tradeDate, int accountTotalAmount) {
		try {
			// 读取当前股票的K线数据，并计算当前股票在当前交易日是否可放入待交易队列
			List<DayKLine> dayKLines = dayKLineDao.queryListByStockID(stock.getStockID());
			DayKLine predayKLine = DayKLineUtils.calcPrevDayKLine(dayKLines, tradeDate);
			DayKLine prePredayKLine = DayKLineUtils.calcPrevDayKLine(dayKLines, TradeDateUtils.calcPrevTradeDate(tradeDate));
			List<DayKLine> prevNDaysKLines = DayKLineUtils.calcPrevNDaysKLines(dayKLines, tradeDate, QuantTradeConsts.PLANNED_KLINE_PRE_N_DAYS);
			if (predayKLine == null || prevNDaysKLines.size() != QuantTradeConsts.PLANNED_KLINE_PRE_N_DAYS) {
				return null;
			}

			// 根据 成交量、成交金额、换手率 指标，判断是否符合待交易规则
			if (!checkTurnoverRateIsCanPlanned(predayKLine, prevNDaysKLines)) {
				return null;
			}

//			// 根据 完全包含 条件，判断是否符合待交易规则
//			if (!checkIncludeCompletelyIsCanPlanned(predayKLine, prevNDaysKLines)) {
//				return null;
//			}

			// 根据 KDJ 指标，判断是否符合待交易规则
			if (!checkKDJIsCanPlanned(predayKLine, prevNDaysKLines)) {
				return null;
			}

			// 计算计划交易方案综合评分
			float plannedScore = calcPlannedScore(predayKLine, prevNDaysKLines);
			if (plannedScore == 0) {
				return null;
			}

			// 创建股票交易计划对象，并返回数据
			String predayKLineJson = CustomJSONUtils.toJSONString(predayKLine);
			String prePredayKLineJson = CustomJSONUtils.toJSONString(prePredayKLine);
			return QuantTradePlanned.createDataModel(stock.getStockID(), stock.getCode(), tradeDate, predayKLineJson, prePredayKLineJson, plannedScore);
		} catch (Exception ex) {
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			String logData = String.format("stockID=%s, tradeDate=%s, accountTotalAmount=%s", stock.getStockID(), CustomDateFormatUtils.formatDate(tradeDate), accountTotalAmount);
			log.error(String.format(LogInfoUtils.HAS_DATA_TMPL, methodName, logData), ex);
		}

		return null;
	}

	/**
	 * 根据 成交量、成交金额、换手率 指标，判断是否符合待交易规则
	 *
	 * @param predayKLine
	 * @param prevNDaysKLines
	 * @return
	 */
	private boolean checkTurnoverRateIsCanPlanned(DayKLine predayKLine, List<DayKLine> prevNDaysKLines) {
		// 如果换手率小于等于0，则返回不符合交易规则
		if (predayKLine.getTurnoverRate() <= 0) {
			return false;
		}

		// 如果成交量小于设置阈值，则返回不符合交易规则
		if (predayKLine.getVolume() < QuantTradeConsts.PLANNED_TRADE_MIN_VOLUME) {
			return false;
		}

		// 如果成交金额小于设置阈值，则返回不符合交易规则
		if (predayKLine.getTurnover() < QuantTradeConsts.PLANNED_TRADE_MIN_TURNOVER) {
			return false;
		}

		return true;
	}

	/**
	 * 根据 完全包含 条件，判断是否符合待交易规则
	 *
	 * @param predayKLine
	 * @param prevNDaysKLines
	 * @return
	 */
	private boolean checkIncludeCompletelyIsCanPlanned(DayKLine predayKLine, List<DayKLine> prevNDaysKLines) {
		// 获取倒数第2天的K线数据
		DayKLine prePrevDaysKLine = prevNDaysKLines.get(1);
		if (prePrevDaysKLine == null) {
			return false;
		}

		// 判断是否符合 完全包含 条件
		if (predayKLine.getHigh() > prePrevDaysKLine.getHigh()
				&& predayKLine.getLow() < prePrevDaysKLine.getLow()
				&& predayKLine.getClose() < prePrevDaysKLine.getLow()) {
			return true;
		}

		return false;
	}

	/**
	 * 根据 KDJ 指标，判断是否符合待交易规则
	 *
	 * @param predayKLine
	 * @param prevNDaysKLines
	 * @return
	 */
	private boolean checkKDJIsCanPlanned(DayKLine predayKLine, List<DayKLine> prevNDaysKLines) {
		// 解析获得前1天的 KDJ 指标数据
		KDJ prevKdj = KDJUtils.parseKDJ(predayKLine.getKdjJson());
		if (prevKdj == null) {
			return false;
		}

		// 解析获得前N天的 KDJ 指标数据列表
		List<KDJ> prevKdjs = KDJUtils.parseKDJs(prevNDaysKLines);
		if (prevKdjs.size() != prevNDaysKLines.size()) {
			return false;
		}

		// 根据 KDJ 指标，判断是否符合待交易规则
		if (prevKdj.getD() < 20
				&& prevKdj.getJ() < 30
				&& prevKdj.getJ() > prevKdj.getD() + 2
				&& prevKdj.getJ() > prevKdj.getK() + 1
				&& prevKdj.getK() > prevKdj.getD()) {
			return true;
		}

		return false;
	}

	/**
	 * 计算计划交易方案综合评分
	 *
	 * @param predayKLine
	 * @param prevNDaysKLines
	 * @return
	 */
	private float calcPlannedScore(DayKLine predayKLine, List<DayKLine> prevNDaysKLines) {
		return 1F;
//		float result = quantTradeAnalysis.getProfitOrLessRate();
//
//		// 当日亏损，则返回0分
//		if (result < 0) {
//			return 0;
//		}
//
//
//		// 2次交易间隔时间段的 +10%
//		if (CustomDateUtils.getDurationBetween(quantTradeAnalysis.getActualTradeStartTime(), quantTradeAnalysis.getActualTradeEndTime()).toMinutes() <= BEST_TRADE_TIME_INTERVAL_MINUTES) {
//			result = result * 1.1F;
//		}
//
//		// 到达降低利润率和亏损值的倍数时间点的 -10%
//		if (quantTradeAnalysis.getReduceProfitRateMultiple() != 0) {
//			result = result * 0.9F;
//		}
//
//		return result;
	}

	/**
	 * 计算非零分的 quantTradePlanneds 数据列表
	 *
	 * @param quantTradePlanneds
	 * @return
	 */
	private List<QuantTradePlanned> getNonZeroAndSortQuantTradePlanneds(List<QuantTradePlanned> quantTradePlanneds) {
		List<QuantTradePlanned> result = quantTradePlanneds.stream().filter(x -> x.getPlannedScore() > 0).collect(Collectors.toList());
		result.sort(Comparator.comparing(QuantTradePlanned::getPlannedScore, Comparator.reverseOrder()));

		return result;
	}
}
