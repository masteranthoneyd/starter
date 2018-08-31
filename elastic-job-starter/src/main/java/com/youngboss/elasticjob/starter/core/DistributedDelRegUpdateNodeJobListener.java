package com.youngboss.elasticjob.starter.core;

import com.dangdang.ddframe.job.executor.ShardingContexts;
import com.dangdang.ddframe.job.lite.api.listener.AbstractDistributeOnceElasticJobListener;
import com.youngboss.elasticjob.starter.service.ElasticJobService;
import lombok.extern.slf4j.Slf4j;

import static com.youngboss.elasticjob.starter.util.ApplicationJobUtil.getBean;


/**
 * Created by IntelliJ IDEA.
 *
 * @author lzt
 * @date 2018/8/30
 * 实现分布式任务监听器
 *
 * 如果任务有分片，分布式监听器会在总的任务开始前执行一次，结束时执行一次
 **/
@Slf4j
public class DistributedDelRegUpdateNodeJobListener extends AbstractDistributeOnceElasticJobListener {

    public DistributedDelRegUpdateNodeJobListener(Long startedTimeoutMilliseconds, Long completedTimeoutMilliseconds) {
        super(startedTimeoutMilliseconds, completedTimeoutMilliseconds);
    }

    @Override
    public void doBeforeJobExecutedAtLastStarted(ShardingContexts shardingContexts) {
        log.info("Distribute DelRegUpdateNodeJob {} started", shardingContexts.getJobName());
    }

    @Override
    public void doAfterJobExecutedAtLastCompleted(ShardingContexts shardingContexts) {
		log.info("Distribute DelRegUpdateNodeJob {} completed, node data will be deleted", shardingContexts.getJobName());
        getBean(ElasticJobService.class).deleteJob(shardingContexts.getJobName());
    }
}
