package com.trade.biz.domain.tradequant.quanttradeplanned;

import com.google.common.collect.Lists;
import com.trade.biz.dal.tradecore.DayKLineDao;
import com.trade.biz.dal.tradecore.QuantTradePlannedDao;
import com.trade.biz.dal.tradecore.StockDao;
import com.trade.biz.domain.tradequant.futu.FutunnAccountHelper;
import com.trade.common.infrastructure.util.date.CustomDateFormatUtils;
import com.trade.common.infrastructure.util.logger.LogInfoUtils;
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

	// 相关常量
	private static final int PLANNED_TRADE_MAX_COUNT = 100; // 最多选择多少只待购买股票
	private static final float PLANNED_DEVIATION_RATE = 0.4F; // 计划价格偏离比例（默认：0.4F）
	private static final float PLANNED_SELL_OUT_PROFIT_RATE = 0.05F; // 计划卖出/赎回占开盘价的比例
	private static final float PLANNED_STOP_LOSS_PROFIT_RATE = 0.2F; // 计划止损占开盘价的比例
	public static int PLANNED_TRADE_MIN_VOLUME = 500000; // 股票每日最小成交量（小于此成交量配置的股票不进行操作）
	public static long PLANNED_TRADE_MIN_TURNOVER = 5000000000L; // 股票每日最小成交金额（小于此成交金额配置的股票不进行操作）

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
			LocalDate currentTradeDate = TradeDateUtils.getUsCurrentDate();
			if (!TradeDateUtils.isUsTradeDay(currentTradeDate)) {
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
				QuantTradePlanned quantTradePlanned = calcQuantTradePlanned(stock, currentTradeDate, accountTotalAmount);
				if (quantTradePlanned != null) {
					quantTradePlanneds.add(quantTradePlanned);
				}
			}

			// 按计划交易综合评分排序结果
			quantTradePlanneds = getNonZeroAndSortQuantTradePlanneds(quantTradePlanneds);
			quantTradePlanneds = quantTradePlanneds.stream().limit(PLANNED_TRADE_MAX_COUNT).collect(Collectors.toList());

			// 把计算结果写入数据库
			for (QuantTradePlanned quantTradePlanned : quantTradePlanneds) {
				quantTradePlannedDao.insertOrUpdate(quantTradePlanned);
			}

			// 记录文件日志
			log.info("QuantTradePlanned SUCCESS! plannedCount={}, currentTradeDate={}", quantTradePlanneds.size(), CustomDateFormatUtils.formatDate(currentTradeDate));
		} catch (Exception ex) {
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			log.error(String.format(LogInfoUtils.NO_DATA_TMPL, methodName), ex);
		}
	}

	/**
	 * 计算单个股票计划交易数据（包含计划交易评分）
	 *
	 * @param stock
	 * @param currentTradeDate
	 * @param accountTotalAmount
	 * @return
	 */
	private QuantTradePlanned calcQuantTradePlanned(Stock stock, LocalDate currentTradeDate, int accountTotalAmount) {
		try {
			// 读取当前股票的K线数据，并计算当前股票在当前交易日是否可放入待交易队列
			List<DayKLine> dayKLines = dayKLineDao.queryListByStockID(stock.getStockID());
			DayKLine predayKLine = DayKLineUtils.calcPrevDayKLine(dayKLines, currentTradeDate);
			List<DayKLine> prevNDaysKLines = DayKLineUtils.calcPrevNDaysKLines(dayKLines, currentTradeDate, 30);
			if (predayKLine == null || prevNDaysKLines.size() == 0) {
				return null;
			}

			// 根据 成交量、成交金额、换手率 指标，判断是否符合待交易规则
			if (!checkTurnoverRateIsCanPlanned(predayKLine, prevNDaysKLines)) {
				return null;
			}

			// 根据 KDJ 指标，判断是否符合待交易规则
			if (!checkKDJIsCanPlanned(predayKLine, prevNDaysKLines)) {
				return null;
			}

			// 计算计划交易方案综合评分
			float plannedScore = calcPlannedScore(predayKLine, prevNDaysKLines);
			if (plannedScore == 0) {
				return null;
			}

			// 计划当天买入点和卖出点距离开盘价的差价
			int deviationAmount = DayKLineUtils.calDeviationAmount(predayKLine, PLANNED_DEVIATION_RATE);

			// 创建股票交易计划对象，并返回数据
			QuantTradePlanned quantTradePlanned = QuantTradePlanned.createDataModel(stock.getStockID(), stock.getCode(), currentTradeDate, deviationAmount,
					PLANNED_DEVIATION_RATE, PLANNED_SELL_OUT_PROFIT_RATE, PLANNED_STOP_LOSS_PROFIT_RATE, plannedScore, predayKLine.getVolume(),
					predayKLine.getTurnover(), predayKLine.getTurnoverRate(), predayKLine.getChangeRate(), predayKLine.getKdjJson());
			return quantTradePlanned;
		} catch (Exception ex) {
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			String logData = String.format("stockID=%s, currentTradeDate=%s, accountTotalAmount=%s", stock.getStockID(), CustomDateFormatUtils.formatDate(currentTradeDate), accountTotalAmount);
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
		if (predayKLine.getVolume() < PLANNED_TRADE_MIN_VOLUME) {
			return false;
		}

		// 如果成交金额小于设置阈值，则返回不符合交易规则
		if (predayKLine.getTurnover() < PLANNED_TRADE_MIN_TURNOVER) {
			return false;
		}

		return true;
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
		result.sort(Comparator.comparing(QuantTradePlanned::getPlannedScore, Comparator.reverseOrder()).thenComparing(QuantTradePlanned::getPlannedSellOutProfitRate, Comparator.reverseOrder()));

		return result;
	}
}
