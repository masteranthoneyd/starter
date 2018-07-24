package com.youngboss.mvc.starter.aop;

import com.youngboss.mvc.starter.BossApi;
import com.youngboss.mvc.starter.BossApiUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author ybd
 * @date 18-7-3
 * @contact yangbingdong1994@gmail.com
 */
@Slf4j
@Component
@Aspect
@Order(0)
public class BossApiAspect {

	@Pointcut("execution(public * com.youngboss..mvc..*.*(..))")
	public void path() {
	}

	@Around(value = "path() && @annotation(bossApi)")
	public Object doAround(ProceedingJoinPoint pjp, BossApi bossApi) throws Throwable {
		Object proceed;
		try {
			proceed = BossApiUtil.wrapper(pjp.proceed(pjp.getArgs()));
		} catch (Exception e) {
			log.error("Boss Api Exception: ", e);
			proceed = BossApiUtil.wrapperError(e.getMessage());
		}
		return proceed;
	}
}
