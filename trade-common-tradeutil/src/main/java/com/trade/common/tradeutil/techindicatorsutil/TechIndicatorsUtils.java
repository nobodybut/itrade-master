package com.trade.common.tradeutil.techindicatorsutil;

import java.math.BigDecimal;

public class TechIndicatorsUtils {

	/**
	 * 四舍五入。
	 * <p>
	 * 解决Java本身无法完全处理四舍五入的问题
	 *
	 * @param value
	 * @param digit 保留小数位数
	 * @return
	 * @see
	 */
	public final static <N extends Number> double round(N value, int digit) {
		return round(value.toString(), digit);
	}

	/**
	 * 四舍五入。
	 * <p>
	 * 解决Java本身无法完全处理四舍五入的问题
	 *
	 * @param value
	 * @param digit 保留小数位数
	 * @return
	 * @see
	 */
	public final static double round(String value, int digit) {
		BigDecimal v_Value = new BigDecimal(value.trim());

		if (v_Value.compareTo(BigDecimal.ZERO) == 0) {
			return 0D;
		}

		BigDecimal v_Pow = new BigDecimal(Math.pow(10d, digit));
		BigDecimal v_Big = v_Value.multiply(v_Pow);
		BigDecimal v_Small = new BigDecimal(Math.floor(v_Big.doubleValue()));
		double v_Subtract = v_Big.subtract(v_Small).doubleValue();

		if (v_Subtract >= 0.5d) {
			v_Small = v_Small.add(new BigDecimal(1d));
		}

		v_Small = v_Small.divide(v_Pow);

		return v_Small.doubleValue();
	}

	/**
	 * 高精度的除法
	 * <p>
	 * 防止被除数为零的情况
	 *
	 * @param value01
	 * @param valueX  可变参数使用N时，第三方调用者为有一个Java警告，所以改为Number
	 * @return
	 */
	public final static <N extends Number> double division(N value01, Number... valueX) {
		return division(value01.toString(), numbersToStrings(valueX));
	}

	/**
	 * 高精度的除法
	 * <p>
	 * 防止被除数为零的情况
	 *
	 * @param value01
	 * @param value02
	 * @return
	 */
	public final static <N extends Number> double division(String value01, N value02) {
		return division(value01, value02.toString());
	}

	/**
	 * 高精度的除法
	 * <p>
	 * 防止被除数为零的情况
	 *
	 * @param value01
	 * @param value02
	 * @return
	 */
	public final static <N extends Number> double division(N value01, String value02) {
		return division(value01.toString(), value02);
	}

	/**
	 * 高精度的除法
	 * <p>
	 * 防止被除数为零的情况
	 *
	 * @param value01
	 * @param valueX
	 * @return
	 */
	public final static double division(String value01, String... valueX) {
		return division(9, value01, valueX);
	}

	/**
	 * 高精度的除法
	 * <p>
	 * 防止被除数为零的情况
	 *
	 * @param scale   精度
	 * @param value01
	 * @param valueX
	 * @return
	 */
	public final static double division(int scale, String value01, String... valueX) {
		BigDecimal v_Ret = new BigDecimal(value01.trim());

		for (String v_ValueStr : valueX) {
			BigDecimal v_Value = new BigDecimal(v_ValueStr.trim());
			if (v_Value.compareTo(BigDecimal.ZERO) == 0) {
				return 0;
			}
			v_Ret = v_Ret.divide(v_Value, scale, BigDecimal.ROUND_HALF_UP);
		}

		return v_Ret.doubleValue();
	}

	/**
	 * 转为字符串数组
	 *
	 * @param valueX
	 * @return
	 */
	private final static <N extends Number> String[] numbersToStrings(N... valueX) {
		String[] v_ValueX = new String[valueX.length];

		for (int i = 0; i < valueX.length; i++) {
			v_ValueX[i] = valueX[i].toString();
		}

		return v_ValueX;
	}
}
