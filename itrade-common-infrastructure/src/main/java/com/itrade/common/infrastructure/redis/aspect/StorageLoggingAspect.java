package com.itrade.common.infrastructure.redis.aspect;

import com.itrade.common.infrastructure.redis.utils.MethodUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * Created by tomxiaodong on 18/1/19.
 */
@Aspect
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class StorageLoggingAspect {

	@Around("@within(com.itrade.common.infrastructure.redis.aspect.StorageLogging) || @annotation(com.itrade.common.infrastructure.redis.aspect.StorageLogging)")
	public Object executeStorageLogging(ProceedingJoinPoint pjp) throws Exception{
		String className = pjp.getTarget().getClass().getName();
		String methodName = MethodUtils.fetchMethodName(((MethodSignature) pjp.getSignature()).getMethod());
		long now = System.currentTimeMillis();
		Object ret;
		try {
			ret = pjp.proceed();
		} catch (Throwable throwable) {
			ret = null;
			log.error("storage invoke exception!", throwable);
		}
		log.info("store invoke! method:{}, class:{}, time consumed:{}", methodName, className, System.currentTimeMillis()-now);
		return ret;
	}

}
