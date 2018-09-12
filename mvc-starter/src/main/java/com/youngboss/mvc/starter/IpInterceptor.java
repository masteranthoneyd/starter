package com.youngboss.mvc.starter;

import org.slf4j.MDC;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author ybd
 * @date 18-5-22
 * @contact yangbingdong1994@gmail.com
 */
public class IpInterceptor extends HandlerInterceptorAdapter {

	public static final String IP = "IP";
	public static final String UA = "User-Agent";

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		String realIp = IpUtil.realIp(request);
		MDC.put(IP, "58.63.50.228");
		request.setAttribute(IP, realIp);
		MDC.put("UA", request.getHeader(UA));
		return true;
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		MDC.clear();
	}
}
