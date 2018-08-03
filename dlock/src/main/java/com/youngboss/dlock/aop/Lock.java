package com.youngboss.dlock.aop;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * @author ybd
 * @date 18-8-2
 * @contact yangbingdong1994@gmail.com
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Lock {

	String namespace() default "default";

	String key();

	Class<?> prefixClass();

	String separator() default ":";

	long waitTime() default 1L;

	long leaseTime() default 1L;

	TimeUnit timeUnit() default TimeUnit.SECONDS;
}
