package com.youngboss.dlock.aop;

import com.youngboss.dlock.core.DLock;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;

import static com.youngboss.dlock.core.FailAcquireAction.DEFAULT_FAIL_ACQUIRE_ACTION;
import static com.youngboss.dlock.util.SpelHelper.parseSpel;

/**
 * @author ybd
 * @date 18-8-2
 * @contact yangbingdong1994@gmail.com
 */
@Slf4j
@Component
@Aspect
@Order(1)
public class DLockAspect {
	@Resource
	private DLock dLock;

	@Value("${spring.application.name}")
	private String namespace;

	@Around(value = "@annotation(lock)")
	public Object doAround(ProceedingJoinPoint pjp, Lock lock) throws Throwable {
		Method method = ((MethodSignature) pjp.getSignature()).getMethod();

		Object[] args = pjp.getArgs();
		String keySpEL = lock.key();
		String resourceKey = parseSpel(method, args, keySpEL, String.class);

		String finalKey = buildFinalKey(lock, resourceKey);
		return dLock.tryLockAndExecuteCommand(() -> finalKey, () -> pjp.proceed(pjp.getArgs()), DEFAULT_FAIL_ACQUIRE_ACTION,
				lock.waitTime(), lock.leaseTime(), lock.timeUnit());
	}

	private String buildFinalKey(Lock lock, String key) {
		return namespace == null || namespace.length() == 0 ? lock.namespace() : namespace +
				lock.separator() +
				lock.prefixClass().getSimpleName() +
				lock.separator() +
				key;
	}


}
