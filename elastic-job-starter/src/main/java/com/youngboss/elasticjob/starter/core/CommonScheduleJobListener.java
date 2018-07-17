package com.youngboss.elasticjob.starter.core;

import com.dangdang.ddframe.job.executor.ShardingContexts;
import com.dangdang.ddframe.job.lite.api.listener.AbstractDistributeOnceElasticJobListener;
import lombok.extern.slf4j.Slf4j;


/**
 * @author ybd
 * @date 18-7-13
 * @contact yangbingdong1994@gmail.com
 */
@Slf4j
public class CommonScheduleJobListener extends AbstractDistributeOnceElasticJobListener {

	public CommonScheduleJobListener(Long startedTimeoutMilliseconds, Long completedTimeoutMilliseconds) {
		super(startedTimeoutMilliseconds, completedTimeoutMilliseconds);
	}

	@Override
	public void doBeforeJobExecutedAtLastStarted(ShardingContexts shardingContexts) {
		log.info(shardingContexts.getJobName() + " 『Started』, item: " + shardingContexts.getCurrentJobEventSamplingCount());
	}

	@Override
	public void doAfterJobExecutedAtLastCompleted(ShardingContexts shardingContexts) {
		log.info(shardingContexts.getJobName() + " 『End』, item: " + shardingContexts.getCurrentJobEventSamplingCount());
	}
}
