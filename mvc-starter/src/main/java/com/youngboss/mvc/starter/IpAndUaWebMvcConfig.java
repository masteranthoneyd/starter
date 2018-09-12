package com.youngboss.mvc.starter;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author ybd
 * @date 18-9-11
 * @contact yangbingdong1994@gmail.com
 */
@Configuration
public class IpAndUaWebMvcConfig implements WebMvcConfigurer {

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new IpInterceptor());
//				.addPathPatterns("/**");
	}
}
