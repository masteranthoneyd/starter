package com.youngboss.elasticjob.starter.config;

import com.alibaba.fastjson.JSONObject;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import com.youngboss.elasticjob.starter.core.Job;
import com.youngboss.elasticjob.starter.service.ElasticJobService;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.data.Stat;

import java.util.List;

import static com.youngboss.util.function.Trier.tryConsumer;
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

	public abstract void addJobIfNotExist(T t);

	private String getConfigPath(T t) {
		return "/" + getJobName(t) + "/config";
	}

	public void initDynamicJob(ZookeeperRegistryCenter zookeeperRegistryCenter, ElasticJobService elasticJobService) {
		log.info("开始初始化 动态任务");
		CuratorFramework curatorFramework = zookeeperRegistryCenter.getClient();
		try {
			getScheduleList().forEach(tryConsumer(e -> {
				String configPath = getConfigPath(e);
				Stat stat = curatorFramework.checkExists().forPath(configPath);
				if (nonNull(stat)) {
					String config = new String(curatorFramework.getData().forPath(configPath));
					Job job = JSONObject.parseObject(config, Job.class).parseParameter();
					elasticJobService.initSpringJobScheduler(job);
				} else {
					addJobIfNotExist(e);
				}
			}));
		} catch (Exception e) {
			log.error("初始化delay 动态任务失败", e);
		}
		log.info("初始化 动态任务结束");
	}

}
