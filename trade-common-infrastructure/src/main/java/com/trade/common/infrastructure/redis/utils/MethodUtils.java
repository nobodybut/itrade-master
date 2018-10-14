package com.trade.common.infrastructure.redis.utils;

import java.lang.reflect.Method;

public class MethodUtils {

	public static String fetchMethodName(Method method) {
		StringBuilder info = new StringBuilder()
				.append(method.getName())
				.append("(")
				;
		String methodName = method.getName();
		Class<?>[] paramTypes = method.getParameterTypes();
		for (Class<?> paramType : paramTypes) {
			info.append(paramType.getSimpleName()).append(",");
		}
		if (info.length() > 0) {
			info.deleteCharAt(info.length() - 1);
		}
		info.append(")");
		return methodName;
	}
}
