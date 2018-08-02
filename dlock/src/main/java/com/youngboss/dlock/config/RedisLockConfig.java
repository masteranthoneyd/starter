package com.youngboss.dlock.config;

import com.youngboss.dlock.core.DLock;
import com.youngboss.dlock.core.impl.redisson.RedissonDLock;
import com.youngboss.dlock.core.impl.spring.SpringDataRedisDLock;
import com.youngboss.dlock.core.impl.zookeeper.ZookeeperDLock;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author ybd
 * @date 18-8-1
 * @contact yangbingdong1994@gmail.com
 */
@Configuration
@EnableConfigurationProperties(DLockConfigProperty.class)
public class RedisLockConfig {

	@Resource
	private DLockConfigProperty property;

	@ConditionalOnMissingBean(DLock.class)
	@Bean
	public DLock springDataRedisDLock() {
		switch (property.getDLockType()) {
			case REDISSON:
				return new RedissonDLock(property);
			case SPRING:
				return new SpringDataRedisDLock(property);
			case ZOOKEEPER:
				return new ZookeeperDLock(property);
			default:
				return null;
		}

	}
}
