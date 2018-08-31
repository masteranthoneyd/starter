package com.youngboss.elasticjob.starter.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author ybd
 * @date 18-5-22
 * @contact yangbingdong1994@gmail.com
 */
@Component
public class ApplicationJobUtil implements ApplicationContextAware {
	public static ApplicationContext context;
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		context = applicationContext;
	}

	public static <T> T getBean(Class<T> clazz) {
		return context.getBean(clazz);
	}
}
