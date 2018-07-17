package com.youngboss.elasticjob.starter.service;

import com.alibaba.fastjson.JSONObject;
import com.dangdang.ddframe.job.api.ElasticJob;
import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.JobTypeConfiguration;
import com.dangdang.ddframe.job.config.dataflow.DataflowJobConfiguration;
import com.dangdang.ddframe.job.config.script.ScriptJobConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.event.JobEventConfiguration;
import com.dangdang.ddframe.job.lite.api.listener.AbstractDistributeOnceElasticJobListener;
import com.dangdang.ddframe.job.lite.api.listener.ElasticJobListener;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.internal.schedule.JobRegistry;
import com.dangdang.ddframe.job.lite.internal.schedule.JobScheduleController;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import com.youngboss.elasticjob.starter.core.Job;
import com.youngboss.elasticjob.starter.core.JobParameterVo;
import com.youngboss.elasticjob.starter.util.CronUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.utils.ZKPaths;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
import java.util.Date;
import java.util.Objects;

import static com.dangdang.ddframe.job.executor.handler.JobProperties.JobPropertiesEnum.EXECUTOR_SERVICE_HANDLER;
import static com.dangdang.ddframe.job.executor.handler.JobProperties.JobPropertiesEnum.JOB_EXCEPTION_HANDLER;
import static com.youngboss.elasticjob.starter.core.JobType.DATAFLOW;
import static com.youngboss.elasticjob.starter.core.JobType.SCRIPT;
import static com.youngboss.elasticjob.starter.core.JobType.SIMPLE;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * Created by IntelliJ IDEA.
 *
 * @author lzt
 * @date 2018/5/15
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class ElasticJobService {

	@Value("${elasticjob.event:false}")
	private boolean enableEventConfig;

	private final ZookeeperRegistryCenter zookeeperRegistryCenter;
	private final JobEventConfiguration jobEventConfiguration;

	/**
	 * 开启任务监听,当有任务添加时，监听zk中的数据增加，自动在其他节点也初始化该任务
	 */
	public void monitorJobRegister() {
		CuratorFramework client = zookeeperRegistryCenter.getClient();
		PathChildrenCache childrenCache = new PathChildrenCache(client, "/", true);
		PathChildrenCacheListener childrenCacheListener = (curatorFramework, event) -> {
			ChildData data = event.getData();
			switch (event.getType()) {
				case CHILD_ADDED:
					//找不到配置信息时将job在zk上面的节点删除
					addJob4Monitor(curatorFramework, data);
					break;
				case CHILD_REMOVED:
					String jobName = data.getPath().substring(1);
					removeJob4Monitor(jobName);
				default:
					break;
			}
		};
		childrenCache.getListenable().addListener(childrenCacheListener);
		try {
			childrenCache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addJob4Monitor(CuratorFramework curatorFramework, ChildData data) throws Exception {
		if (isNull(curatorFramework.getZookeeperClient()
								   .getZooKeeper()
								   .exists(ZKPaths.makePath("/", curatorFramework.getNamespace()) + data.getPath() + "/config", false))) {
			deleteJob(data.getPath().substring(1));
			return;
		}
		String config = new String(curatorFramework.getData().forPath(data.getPath() + "/config"));
		Job job = JSONObject.parseObject(config, Job.class);
		JobParameterVo parameterVo = job.parseParameter().getJobParameterVo();

		Date cronDate = parameterVo.isDynamic() ? CronUtils.getDateByCron(job.getCron()) : null;
		//定时发送的时间存在时比并且执行时间大于当前时间或者cronDate时间为空；并且服务器实例中不存在该作业
		long time = System.currentTimeMillis();
		if ((isNull(cronDate) || cronDate.getTime() > time) && isNull(JobRegistry.getInstance()
																				 .getJobInstance(job.getJobName()))) {
			addOrUpdateJob(job);
		} else if (nonNull(cronDate) && cronDate.getTime() < time) {
			//定时发送的时间存在时比并且执行时间小于当前时间时删除该任务
			deleteJob(job.getJobName());
		}
	}

	public void addOrUpdateJob(Job job) {
		initSpringJobScheduler(job);
	}

	public void initSpringJobScheduler(Job job) {
		Date cronDate = job.getJobParameterVo().isDynamic() ? CronUtils.getDateByCron(job.getCron()) : null;
		//定时发送的时间存在时并且执行时间小于于当前时间
		if (nonNull(cronDate) && cronDate.getTime() < System.currentTimeMillis()) {
			log.warn("jobName为%s尝试创建定时任务失败，执行时间小于当前时间，执行时间为%s", job.getJobName(), cronDate);
			return;
		}
		ElasticJob elasticJob = null;
		try {
			elasticJob = job.getJobClass().newInstance();
		} catch (Exception e) {
			log.error("初始化elastic job 失败", e);
		}
		JobCoreConfiguration jobCoreConfig = getJobCoreConfiguration(job);
		JobTypeConfiguration typeConfig = getJobTypeConfiguration(job, jobCoreConfig);
		LiteJobConfiguration jobConfig = getLiteJobConfiguration(job, typeConfig);
		ElasticJobListener distributedJobListener = getDistributedJobListener(job);
		SpringJobScheduler springJobScheduler = enableEventConfig ? new SpringJobScheduler(elasticJob, zookeeperRegistryCenter, jobConfig, jobEventConfiguration, distributedJobListener) :
				new SpringJobScheduler(elasticJob, zookeeperRegistryCenter, jobConfig, distributedJobListener);
		springJobScheduler.init();
		log.info("初始化 动态任务成功【" + job.getJobName() + "】\t" + job.getJobClass() + "\tinit success");
	}

	public ElasticJobListener getDistributedJobListener(Job job) {
		Long startedTimeoutMilliseconds = job.getStartedTimeoutMilliseconds();
		Long completedTimeoutMilliseconds = job.getCompletedTimeoutMilliseconds();
		//JobParameterVo 保存了分布式监听器
		String distributedListenerFromZk = job.getJobParameterVo().getDistributedJobListener();
		AbstractDistributeOnceElasticJobListener distributeOnceElasticJobListener = null;
		try {
			Class<? extends AbstractDistributeOnceElasticJobListener> clazz = Class.forName(distributedListenerFromZk)
																				   .asSubclass(AbstractDistributeOnceElasticJobListener.class);
			Constructor<? extends AbstractDistributeOnceElasticJobListener> constructor = clazz.getDeclaredConstructor(Long.class, Long.class);
			constructor.setAccessible(true);
			distributeOnceElasticJobListener = constructor.newInstance(startedTimeoutMilliseconds, completedTimeoutMilliseconds);
		} catch (Exception e) {
			log.error("getDistributedJobListener fail", e);
		}
		return distributeOnceElasticJobListener;
	}

	public LiteJobConfiguration getLiteJobConfiguration(Job job, JobTypeConfiguration typeConfig) {
		LiteJobConfiguration jobConfig;
		jobConfig = LiteJobConfiguration.newBuilder(typeConfig)
										.overwrite(job.isOverwrite())
										.disabled(job.isDisabled())
										.monitorPort(job.getMonitorPort())
										.monitorExecution(job.isMonitorExecution())
										.maxTimeDiffSeconds(job.getMaxTimeDiffSeconds())
										.jobShardingStrategyClass(job.getJobShardingStrategyClass())
										.reconcileIntervalMinutes(job.getReconcileIntervalMinutes())
										.build();
		return jobConfig;
	}

	public JobTypeConfiguration getJobTypeConfiguration(Job job, JobCoreConfiguration coreConfig) {
		JobTypeConfiguration typeConfig = null;
		switch (job.getJobType()) {
			case SIMPLE:
				typeConfig = new SimpleJobConfiguration(coreConfig, job.getJobClass().getName());
				break;
			case DATAFLOW:
				typeConfig = new DataflowJobConfiguration(coreConfig, job.getJobClass()
																		 .getName(), job.isStreamingProcess());
				break;
			case SCRIPT:
				typeConfig = new ScriptJobConfiguration(coreConfig, job.getScriptCommandLine());
				break;
			default:
				break;
		}
		return typeConfig;
	}

	public JobCoreConfiguration getJobCoreConfiguration(Job job) {
		// 核心配置
		return JobCoreConfiguration.newBuilder(job.getJobName(), job.getCron(), job.getShardingTotalCount())
								   .shardingItemParameters(job.getShardingItemParameters())
								   .description(job.getDescription())
								   .failover(job.isFailover())
								   .jobParameter(job.getJobParameter())
								   .misfire(job.isMisfire())
								   .jobProperties(JOB_EXCEPTION_HANDLER.getKey(), job.getJobProperties()
																					 .getJobExceptionHandler())
								   .jobProperties(EXECUTOR_SERVICE_HANDLER.getKey(), job.getJobProperties()
																						.getExecutorServiceHandler())
								   .build();
	}

	public void deleteJob(String jobName) {
		removeJob4Monitor(jobName);
		CuratorFramework client = zookeeperRegistryCenter.getClient();
		try {
			client.delete().deletingChildrenIfNeeded().forPath("/" + jobName);
		} catch (Exception e) {
			log.error("删除zookeeper节点数据失败，节点为 " + jobName, e);
		}
	}


	public void removeJob4Monitor(String jobName) {
		JobScheduleController jobScheduleController = JobRegistry.getInstance().getJobScheduleController(jobName);
		if (Objects.nonNull(jobScheduleController)) {
			jobScheduleController.shutdown();
		}
	}
}
