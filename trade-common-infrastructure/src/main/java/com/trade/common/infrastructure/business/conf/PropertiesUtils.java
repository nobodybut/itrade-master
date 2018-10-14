package com.trade.common.infrastructure.business.conf;

import com.trade.common.infrastructure.business.context.ApplicationContextUtils;
import com.trade.common.infrastructure.util.math.CustomNumberUtils;

/**
 * Spring-PropertiesUtils工具类 -获取属性值
 *
 * @author Administrator
 */
public class PropertiesUtils {

	private static final String BEAN_KEY = "propertyConfigurer";

	/**
	 * 获取配置文件中的内容（String类型）
	 *
	 * @param keyName
	 * @return
	 */
	public static String getStringValue(String keyName) {
		CustomizedPropertyPlaceholderConfigurer cp = (CustomizedPropertyPlaceholderConfigurer) ApplicationContextUtils.getBean(BEAN_KEY);
		return cp.getContextProperty(keyName);
	}

	/**
	 * 获取配置文件中的内容（Int类型）
	 *
	 * @param keyName
	 * @return
	 */
	public static int getIntValue(String keyName) {
		CustomizedPropertyPlaceholderConfigurer cp = (CustomizedPropertyPlaceholderConfigurer) ApplicationContextUtils.getBean(BEAN_KEY);
		return CustomNumberUtils.toInt(cp.getContextProperty(keyName));
	}

	/**
	 * 获取配置文件中的内容（Double类型）
	 *
	 * @param keyName
	 * @return
	 */
	public static double getDoubleValue(String keyName) {
		CustomizedPropertyPlaceholderConfigurer cp = (CustomizedPropertyPlaceholderConfigurer) ApplicationContextUtils.getBean(BEAN_KEY);
		return Double.parseDouble(cp.getContextProperty(keyName));
	}

	/**
	 * 获取配置文件中的内容（Boolean类型）
	 *
	 * @param keyName
	 * @return
	 */
	public static boolean getBooleanValue(String keyName) {
		CustomizedPropertyPlaceholderConfigurer cp = (CustomizedPropertyPlaceholderConfigurer) ApplicationContextUtils.getBean(BEAN_KEY);
		return Boolean.parseBoolean(cp.getContextProperty(keyName));
	}
}