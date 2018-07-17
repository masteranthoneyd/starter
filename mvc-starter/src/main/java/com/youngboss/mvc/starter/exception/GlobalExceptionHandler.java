package com.youngboss.mvc.starter.exception;

import com.alibaba.fastjson.JSONObject;
import com.youngboss.mvc.starter.BossApiUtil;
import com.youngboss.mvc.starter.Response;
import com.youngboss.mvc.starter.Rest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author ybd
 * @date 17-12-12
 *
 * 全局异常处理
 */
@Slf4j
@RestControllerAdvice(annotations = Rest.class)
public class GlobalExceptionHandler {

	@ExceptionHandler(value = Exception.class)
	public Response defaultErrorHandler(Exception e) {
		log.error("异常信息: ", e);
		return Response.error(e);
	}

	@ExceptionHandler(value = BossException.class)
	public JSONObject bossErrorErrorHandler(Exception e) {
		log.error("异常信息: ", e);
		return BossApiUtil.wrapperError(e.getMessage());
	}
}
