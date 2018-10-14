package com.itrade.common.infrastructure.util.ditu;

import com.google.common.base.Strings;
import com.google.common.primitives.Doubles;
import com.itrade.common.infrastructure.util.collection.KeyValuePair;
import com.itrade.common.infrastructure.util.consts.GlobalConsts;
import com.itrade.common.infrastructure.util.httpclient.HttpClientUtils;
import com.itrade.common.infrastructure.util.logger.LogInfoUtils;
import com.itrade.common.infrastructure.util.math.CustomMathUtils;
import com.itrade.common.infrastructure.util.string.CustomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class GMapUtils {

	// Logger
	private static final Logger s_logger = LoggerFactory.getLogger(GMapUtils.class);

	/**
	 * 从Google接口获取Google坐标，返回 String
	 *
	 * @param cityName
	 * @param cityPoint
	 * @param keyWord
	 * @param isCheckPoint
	 * @return
	 */
	public static String getGooglePoint(String cityName, String cityPoint, String keyWord, boolean isCheckPoint) {
		String result = "";

		if (!Strings.isNullOrEmpty(keyWord)) {
			String postUrl = String
					.format("http://ditu.google.cn/maps/geo?q=%s+%s&output=xml&sensor=false&key=ABQIAAAAL5z4AzZ_lS7gfRZ7vuau5hThVaPVXvXrklHSGbm91ffZkZdiCxQx1vhgi_cCvmX80MJlNbaFMM2qcA",
							CustomStringUtils.encodeURIComponent(cityName), CustomStringUtils.encodeURIComponent(keyWord));

			String html = HttpClientUtils.getHTML(postUrl, GlobalConsts.UTF_8_STR);
			if (!Strings.isNullOrEmpty(html)) {
				String coordinates = CustomStringUtils.substringBetween(html, "<coordinates>", "</coordinates>");
				if (!Strings.isNullOrEmpty(coordinates)) {
					String[] arr = StringUtils.split(coordinates, ",");
					if (arr.length > 2) {
						String lat = arr[1];
						String lng = arr[0];
						if (!lat.equals("0") && !lng.equals("0")) {
							result = String.format("%s,%s", lat, lng);
							if (result.equals(cityPoint)) {
								result = "";
							}
						}
					}
				}
			}
		}

		return result;
	}

	/**
	 * 计算两点间距离（单位：公里）
	 *
	 * @param srcPoint
	 * @param tarPoint
	 * @return
	 */
	public static int calDistance_KM(String srcPoint, String tarPoint) {
		int metreValue = calDistance_M(srcPoint, tarPoint);
		return (int) (metreValue / 1000);
	}

	/**
	 * 计算两点间距离（单位：米）
	 *
	 * @param srcPoint
	 * @param tarPoint
	 * @return
	 */
	public static int calDistance_M(String srcPoint, String tarPoint) {
		double[] fromPoint = convertStringToPoint(srcPoint);
		double[] toPoint = convertStringToPoint(tarPoint);

		return calDistance_M(fromPoint, toPoint);
	}

	/**
	 * 计算两点间距离（单位：公里）
	 *
	 * @param srcPoint
	 * @param tarPoint
	 * @return
	 */
	public static int calDistance_KM(double[] srcPoint, double[] tarPoint) {
		int metreValue = calDistance_M(srcPoint, tarPoint);
		return (int) (metreValue / 1000);
	}

	/**
	 * 计算两点间距离（单位：米）
	 *
	 * @param srcPoint
	 * @param tarPoint
	 * @return
	 */
	public static int calDistance_M(double[] srcPoint, double[] tarPoint) {
		if (srcPoint.length == 2 && tarPoint.length == 2) {
			double srcLat = srcPoint[0];
			double srcLng = srcPoint[1];
			double tarLat = tarPoint[0];
			double tarLng = tarPoint[1];
			double N = 6378137;
			double M = 6356752.3142;
			double G = (1 / 298.257223563);
			double m = CustomMathUtils.toRad(tarLng - srcLng);
			double K = Math.atan((1 - G) * Math.tan(CustomMathUtils.toRad(srcLat)));
			double I = Math.atan((1 - G) * Math.tan(CustomMathUtils.toRad(tarLat)));
			double k = Math.sin(K);
			double e = Math.cos(K);
			double j = Math.sin(I);
			double d = Math.cos(I);
			double q = m, l, o = 100;

			double z;
			double c;
			double Q;
			double E;
			double y;
			double x;
			double F;
			double n;
			double u;

			do {
				z = Math.sin(q);
				c = Math.cos(q);
				Q = Math.sqrt((d * z) * (d * z) + (e * j - k * d * c) * (e * j - k * d * c));
				if (Q == 0) {
					return 0;
				}
				E = k * j + e * d * c;
				y = Math.atan2(Q, E);
				x = e * d * z / Q;
				F = 1 - x * x;
				n = E - 2 * k * j / F;
				if (Double.isNaN(n)) {
					n = 0;
				}
				u = G / 16 * F * (4 + G * (4 - 3 * F));
				l = q;
				q = m + (1 - u) * G * x * (y + u * Q * (n + u * E * (-1 + 2 * n * n)));
			} while (Math.abs(q - l) > 1e-12 && --o > 0);

			if (o == 0) {
				return -1;
			}
			double t = F * (N * N - M * M) / (M * M);
			double w = 1 + t / 16384 * (4096 + t * (-768 + t * (320 - 175 * t)));
			double v = t / 1024 * (256 + t * (-128 + t * (74 - 47 * t)));
			double D = v * Q * (n + v / 4 * (E * (-1 + 2 * n * n) - v / 6 * n * (-3 + 4 * Q * Q) * (-3 + 4 * n * n)));

			return (int) (M * w * (y - D));
		}

		return -1;
	}

	public static String convertPointToString(double[] arrPoint) {
		return (arrPoint.length == 2) ? String.format("%s,%s", arrPoint[0], arrPoint[1]) : "";
	}

	public static double[] convertStringToPoint(String pointStr) {
		if (!Strings.isNullOrEmpty(pointStr)) {
			try {
				pointStr = pointStr.replace(" ", "");
				String[] arr = StringUtils.split(pointStr, ",");
				if (arr.length == 2) {
					double lat = Doubles.tryParse(arr[0]);
					double lng = Doubles.tryParse(arr[1]);

					if (lat != 0 && lng != 0) {
						return new double[]{lat, lng};
					}
				}
			} catch (Exception ex) {
				String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
				String logData = String.format("pointStr=%s", pointStr);

				s_logger.error(String.format(LogInfoUtils.HAS_DATA_TMPL, methodName, logData), ex);
			}
		}

		return new double[0];
	}

	public static Double[] convertStringToPointDouble(String str) {
		double[] point = convertStringToPoint(str);
		if (point.length == 2) {
			return new Double[]{point[0], point[1]};
		}

		return new Double[0];
	}

	/**
	 * 通过 originPoint、destinationPoint 计算 pointPair 数据
	 *
	 * @param originPoint
	 * @param destinationPoint
	 * @return
	 */
	public static String createPointPair(String originPoint, String destinationPoint) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(originPoint);
		stringBuilder.append("^");
		stringBuilder.append(destinationPoint);

		return stringBuilder.toString();
	}

	/**
	 * 通过 pointPair 解析 point KV 数据
	 *
	 * @param pointPair
	 * @return
	 */
	public static KeyValuePair<String, String> parsePointKV(String pointPair) {
		String[] pointArr = StringUtils.split(pointPair, "^");
		if (pointArr.length == 2) {
			return new KeyValuePair<>(pointArr[0], pointArr[1]);
		}

		return null;
	}

	/**
	 * 根据输入的坐标点列表计算中心点坐标（适用于400km以下的场合）
	 *
	 * @param points
	 * @return
	 */
	public static String calPointsCenter(List<String> points) {
		int total = points.size();
		double lat = 0, lon = 0;
		for (String point : points) {
			Double[] pointArr = convertStringToPointDouble(point);
			if (pointArr.length == 2) {
				lat += pointArr[0] * Math.PI / 180;
				lon += pointArr[1] * Math.PI / 180;
			}
		}
		lat /= total;
		lon /= total;

		return String.format("%s,%s", lat * 180 / Math.PI, lon * 180 / Math.PI);
	}
}