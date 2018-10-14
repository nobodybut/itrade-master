package com.itrade.common.infrastructure.util.knn;

import java.util.*;

/*
 * K-近邻算法
 */
public class KNN {

	/**
	 * KNN数据模型
	 */
	public static class KNNModel implements Comparable<KNNModel> {
		public double price;
		public double distince;
		public String series;

		public KNNModel(double price, String series) {
			this.price = price;
			this.series = series;
		}

		/**
		 * 按距离排序
		 *
		 * @param arg
		 * @return
		 */
		@Override
		public int compareTo(KNNModel arg) {
			return Double.valueOf(this.distince).compareTo(Double.valueOf(arg.distince));
		}
	}

	/**
	 * 计算距离
	 *
	 * @param knnModelList
	 * @param i
	 */
	private static void calDistince(List<KNNModel> knnModelList, KNNModel i) {
		for (KNNModel m : knnModelList) {
			m.distince = Math.abs(i.price - m.price);
		}
	}

	/**
	 * 找出前k个数据中分类最多的数据
	 *
	 * @param knnModelList
	 * @return
	 */
	private static String findMostData(List<KNNModel> knnModelList, int k) {
		Map<String, Integer> typeCountMap = new HashMap<String, Integer>();
		String type = "";
		Integer tempVal = 0;
		// 统计分类个数
		int size = k;
		if (k > knnModelList.size()) {
			size = knnModelList.size();
		}
		for (int i = 0; i < size; i++) {
			KNNModel model = knnModelList.get(i);
			if (typeCountMap.containsKey(model.series)) {
				typeCountMap.put(model.series, typeCountMap.get(model.series) + 1);
			} else {
				typeCountMap.put(model.series, 1);
			}
		}
		// 找出最多分类
		for (Map.Entry<String, Integer> entry : typeCountMap.entrySet()) {
			if (entry.getValue() > tempVal) {
				tempVal = entry.getValue();
				type = entry.getKey();
			}
		}
		return type;
	}

	/**
	 * KNN 算法的实现
	 *
	 * @param k
	 * @param knnModelList
	 * @param inputModel
	 * @return
	 */
	public static String calKNN(int k, List<KNNModel> knnModelList, KNNModel inputModel) {
		calDistince(knnModelList, inputModel);
		Collections.sort(knnModelList);
		return findMostData(knnModelList, k);
	}

	/**
	 * 测试KNN算法
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		// 准备数据
		List<KNNModel> knnModelList = new ArrayList<KNNModel>();
		knnModelList.add(new KNNModel(1000, "t"));
		knnModelList.add(new KNNModel(900, "t"));
		knnModelList.add(new KNNModel(800, "t"));
		knnModelList.add(new KNNModel(600, "c"));
		knnModelList.add(new KNNModel(600, "c"));
		knnModelList.add(new KNNModel(500, "c"));
		knnModelList.add(new KNNModel(701, "c"));
		knnModelList.add(new KNNModel(300, "e"));
		knnModelList.add(new KNNModel(317, "e"));
		// 预测数据
		KNNModel predictionData = new KNNModel(540, "N");
		// 计算
		String result = calKNN(3, knnModelList, predictionData);
		System.out.println("预测结果：" + result);
	}
}
