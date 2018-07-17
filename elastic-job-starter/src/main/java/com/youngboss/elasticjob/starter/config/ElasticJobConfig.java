package com.youngboss.elasticjob.starter.config;

import com.dangdang.ddframe.job.event.JobEventConfiguration;
import com.dangdang.ddframe.job.event.rdb.JobEventRdbConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 * Created by IntelliJ IDEA.
 *
 * @author lzt
 * @date 2018/5/15
 **/
@Configuration
@Data
@ConfigurationProperties(prefix = "elasticjob")
public class ElasticJobConfig {
    private String serverlists;
    private String namespace;
    /**
     * 等待重试的间隔时间的初始值. 单位毫秒.
     */
    private int baseSleepTimeMilliseconds = 1000;

    /**
     * 等待重试的间隔时间的最大值. 单位毫秒.
     */
    private int maxSleepTimeMilliseconds = 3000;

    /**
     * 最大重试次数.
     */
    private int maxRetries = 3;

    /**
     * 会话超时时间. 单位毫秒.
     */
    private int sessionTimeoutMilliseconds;

    /**
     * 连接超时时间. 单位毫秒.
     */
    private int connectionTimeoutMilliseconds;

    /**
     * 连接Zookeeper的权限令牌. 缺省为不需要权限验证.
     */
    private String digest;
    @Resource
    private DataSource dataSource;

    @Bean
    public ZookeeperConfiguration zkConfig() {
        return new ZookeeperConfiguration(serverlists, namespace);
    }

    @Bean(initMethod = "init")
    public ZookeeperRegistryCenter regCenter(ZookeeperConfiguration config) {
        return new ZookeeperRegistryCenter(config);
    }

    /**
     * 将作业运行的痕迹进行持久化到DB
     *
     * @return
     */
    @Bean
    public JobEventConfiguration jobEventConfiguration() {
        return new JobEventRdbConfiguration(dataSource);
    }
}
