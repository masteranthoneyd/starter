package com.youngboss.elasticjob.starter.config;

import com.alibaba.fastjson.JSONObject;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import com.youngboss.elasticjob.starter.core.Job;
import com.youngboss.elasticjob.starter.service.ElasticJobService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.data.Stat;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Objects.nonNull;

/**
 * @author ybd
 * @date 18-7-16
 * @contact yangbingdong1994@gmail.com
 */
@Slf4j
public abstract class AbstractDynamicJobInitializer<T> {

	public abstract String getJobName(T t);

	public abstract List<T> getScheduleList();

	public abstract void addJobIfNotExist(T t) throws Exception;

	private String getConfigPath(T t) {
		return "/" + getJobName(t) + "/config";
	}

	public void initDynamicJob(ZookeeperRegistryCenter zookeeperRegistryCenter, ElasticJobService elasticJobService) {
		log.info("-------------------- Initialing dynamic schedule job --------------------");
		CuratorFramework curatorFramework = zookeeperRegistryCenter.getClient();
		List<T> scheduleList = getScheduleList();
		if (CollectionUtils.isEmpty(scheduleList)) {
			log.info("-------------------- Initialing done, empty job list --------------------");
			return;
		}
		int size = scheduleList.size();
		AtomicInteger successCounter = new AtomicInteger(0);
		AtomicInteger failCounter = new AtomicInteger(0);
		scheduleList.forEach(e -> {
			try {
				String configPath = getConfigPath(e);
				Stat stat = curatorFramework.checkExists().forPath(configPath);
				if (nonNull(stat)) {
					String config = new String(curatorFramework.getData().forPath(configPath));
					Job job = JSONObject.parseObject(config, Job.class).parseParameter();
					elasticJobService.initSpringJobScheduler(job);
				} else {
					addJobIfNotExist(e);
				}
				successCounter.incrementAndGet();
			} catch (Exception ex) {
				log.error("Initialize fail, job: " + e, ex);
				failCounter.incrementAndGet();
			}
		});
		log.info("-------------------- Initialing done, schedule job size: {}, success count: {}, fail count: {} --------------------", size, successCounter.get(), failCounter.get());
	}

}
