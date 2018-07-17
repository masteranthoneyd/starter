package com.youngboss.elasticjob.starter.core;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class JobProperties {
	
	/**
	 * 自定义异常处理类
	 * @return
	 */
	@JSONField(name = "job_exception_handler")
	private String jobExceptionHandler = "com.dangdang.ddframe.job.executor.handler.impl.DefaultJobExceptionHandler";
	
	/**
	 * 自定义业务处理线程池
	 * @return
	 */
	@JSONField(name = "executor_service_handler")
	private String executorServiceHandler = "com.dangdang.ddframe.job.executor.handler.impl.DefaultExecutorServiceHandler";



}
