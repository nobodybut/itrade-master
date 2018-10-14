package com.itrade.common.infrastructure.util.xml;

import com.thoughtworks.xstream.XStream;

public class CustomXMLUtils {

	/**
	 * 对象转换为 XML
	 *
	 * @param obj
	 * @return
	 */
	public static String toXML(Object obj) {
		return new XStream().toXML(obj);
	}
}
