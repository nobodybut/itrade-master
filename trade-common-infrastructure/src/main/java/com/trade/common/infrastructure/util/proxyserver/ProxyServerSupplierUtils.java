package com.trade.common.infrastructure.util.proxyserver;

import com.trade.common.infrastructure.util.collection.KeyValuePair;
import com.trade.common.infrastructure.util.enums.ProxyServerSupplierEnum;
import com.trade.common.infrastructure.util.math.CustomNumberUtils;
import org.apache.commons.exec.util.StringUtils;

public class ProxyServerSupplierUtils {

	public static KeyValuePair<ProxyServerSupplierEnum, String> parseProxyServerKV(String proxyServerWithSupplier) {
		if (proxyServerWithSupplier.contains("^")) {
			String[] proxyServerArr = StringUtils.split(proxyServerWithSupplier, "^");
			if (proxyServerArr.length == 2) {
				return new KeyValuePair<>(ProxyServerSupplierEnum.values()[CustomNumberUtils.toInt(proxyServerArr[0])], proxyServerArr[1]);
			}
		}

		return new KeyValuePair<>(ProxyServerSupplierEnum.NONE, proxyServerWithSupplier);
	}

	public static String getProxyServerWithSupplier(ProxyServerSupplierEnum proxyServerSupplierEnum, String proxyServer) {
		return String.format("%s^%s", proxyServerSupplierEnum.ordinal(), proxyServer);
	}
}
