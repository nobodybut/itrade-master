package com.itrade.common.infrastructure.util.tsp;

import com.google.common.collect.Lists;
import com.itrade.common.infrastructure.util.ditu.GMapUtils;
import com.itrade.common.infrastructure.util.json.CustomJSONUtils;
import com.itrade.common.infrastructure.util.logger.LogInfoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CustomGeneticTsp {

	// 日志记录
	private static final Logger s_logger = LoggerFactory.getLogger(CustomGeneticTsp.class);

	/**
	 * 获取经过路径优化的坐标点最优顺序列表
	 *
	 * @param originPoint
	 * @param destinationPoint
	 * @param wayPoints
	 * @return
	 */
	public List<Integer> getOptimizeWaypointsOrders(String originPoint, String destinationPoint, List<String> wayPoints) {
		List<Integer> result = Lists.newArrayList();

		try {
			GeneticAlgorithm ga = new GeneticAlgorithm();
			ga.setMaxGeneration(1000);
			ga.setAutoNextGeneration(true);

			List<String> allPoints = calAllPoints(originPoint, destinationPoint, wayPoints);
			for (int i = 0; i < 100; i++) {
				int[] bestOrders = ga.tsp(getDist(allPoints));
				int originOrder = bestOrders[0];
				int destinationOrder = bestOrders[bestOrders.length - 1];

				if (originOrder == 0 && destinationOrder == bestOrders.length - 1) {
					for (int j = 1; j < bestOrders.length - 1; j++) {
						result.add(bestOrders[j] - 1);
					}
					break;
				}
			}
		} catch (Exception ex) {
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			String logData = String.format("originPoint=%s, destinationPoint=%s, wayPoints=%s", originPoint, destinationPoint, CustomJSONUtils.toJSONString(wayPoints));
			s_logger.error(String.format(LogInfoUtils.HAS_DATA_TMPL, methodName, logData), ex);
		}

		return result;
	}

	/**
	 * 计算全部坐标点列表
	 *
	 * @param originPoint
	 * @param destinationPoint
	 * @param wayPoints
	 * @return
	 */
	private List<String> calAllPoints(String originPoint, String destinationPoint, List<String> wayPoints) {
		List<String> allPoints = Lists.newArrayList();

		allPoints.add(originPoint);
		allPoints.addAll(wayPoints);
		allPoints.add(destinationPoint);

		return allPoints;
	}

	/**
	 * 获取坐标点距离数组
	 *
	 * @param points
	 * @return
	 */
	private float[][] getDist(List<String> points) {
		float[][] dist = new float[points.size()][points.size()];
		for (int i = 0; i < points.size(); i++) {
			for (int j = 0; j < points.size(); j++) {
				dist[i][j] = GMapUtils.calDistance_M(points.get(i), points.get(j));
			}
		}
		return dist;
	}
}
