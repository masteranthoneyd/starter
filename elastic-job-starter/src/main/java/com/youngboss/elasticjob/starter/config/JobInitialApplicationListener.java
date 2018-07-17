package com.youngboss.elasticjob.starter.config;

import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import com.youngboss.elasticjob.starter.core.Job;
import com.youngboss.elasticjob.starter.core.JobConfEnum;
import com.youngboss.elasticjob.starter.core.JobParameterVo;
import com.youngboss.elasticjob.starter.service.ElasticJobService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

/**
 * @author ybd
 * @date 18-5-23
 * @contact yangbingdong1994@gmail.com
 */
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Component
@Slf4j
public class JobInitialApplicationListener implements ApplicationListener<ApplicationStartedEvent> {

	private final ZookeeperRegistryCenter zookeeperRegistryCenter;

	private final ElasticJobService elasticJobService;

	private final ApplicationContext applicationContext;

	@Autowired(required = false)
	private List<AbstractDynamicJobInitializer> abstractDynamicJobInitializerList;

	@Override
	public void onApplicationEvent(ApplicationStartedEvent event) {
		Map<String, Object> beanMap = applicationContext.getBeansWithAnnotation(JobConfEnum.class);
		for (Object confBean : beanMap.values()) {
			Class<?> clz = confBean.getClass();
			JobConfEnum conf = clz.getAnnotation(JobConfEnum.class);
			Job job = buildJobByConfEnum(conf);
			elasticJobService.initSpringJobScheduler(job);
		}
		initMonitorJobRegister();
		if (isNotEmpty(abstractDynamicJobInitializerList)) {
			abstractDynamicJobInitializerList.forEach(e -> e.initDynamicJob(zookeeperRegistryCenter, elasticJobService));
		}
	}

	private Job buildJobByConfEnum(JobConfEnum jobConfEnum) {
		return new Job().setJobName(jobConfEnum.jobName())
						.setJobType(jobConfEnum.jobType())
						.setJobClass(jobConfEnum.jobClass())
						.setCron(jobConfEnum.cron())
						.setShardingTotalCount(jobConfEnum.shardingTotalCount())
						.setShardingItemParameters(jobConfEnum.shardingItemParameters())
						.setFailover(jobConfEnum.failover())
						.setMisfire(jobConfEnum.misfire())
						.setDescription(jobConfEnum.description())
						.setOverwrite(true)
						.setStreamingProcess(jobConfEnum.streamingProcess())
						.setScriptCommandLine(jobConfEnum.scriptCommandLine())
						.setMonitorExecution(jobConfEnum.monitorExecution())
						.setMonitorPort(jobConfEnum.monitorPort())
						.setMaxTimeDiffSeconds(jobConfEnum.maxTimeDiffSeconds())
						.setJobShardingStrategyClass(jobConfEnum.jobShardingStrategyClass())
						.setReconcileIntervalMinutes(jobConfEnum.reconcileIntervalMinutes())
						.setEventTraceRdbDataSource(jobConfEnum.eventTraceRdbDataSource())
						.setListener(jobConfEnum.listener())
						.setDisabled(jobConfEnum.disabled())
						.setDistributedListener(jobConfEnum.distributedListener())
						.setStartedTimeoutMilliseconds(jobConfEnum.startedTimeoutMilliseconds())
						.setCompletedTimeoutMilliseconds(jobConfEnum.completedTimeoutMilliseconds())
						.setJobParameterVo(JobParameterVo.of(new Job().getDistributedListener().getName()).setDynamic(false));
	}

	private void initMonitorJobRegister() {
		//开启任务监听,当有任务添加时，监听zk中的数据增加，自动在其他节点也初始化该任务
		if (elasticJobService != null) {
			elasticJobService.monitorJobRegister();
		}
	}

}
