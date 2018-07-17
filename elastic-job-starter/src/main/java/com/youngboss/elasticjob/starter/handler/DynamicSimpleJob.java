package com.youngboss.elasticjob.starter.handler;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.youngboss.elasticjob.starter.help.JobNameParser;
import com.youngboss.elasticjob.starter.core.JobParameterVo;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import static com.alibaba.fastjson.JSON.parseObject;

/**
 * @author ybd
 * @date 18-7-16
 * @contact yangbingdong1994@gmail.com
 */
public interface DynamicSimpleJob<T extends JobParameterVo> extends SimpleJob {
	@Override
	default void execute(ShardingContext shardingContext) {
		T t = parseObject(shardingContext.getJobParameter(), getGenericType());
		String jobName = shardingContext.getJobName();
		t.setJobName(jobName)
		 .setShardingItem(shardingContext.getShardingItem())
		 .setJobId(JobNameParser.getJobId(jobName));
		executeDynamicJob(t);
	}

	default Type getGenericType() {
		return ((ParameterizedType) this.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
	}

	void executeDynamicJob(T t);
}
