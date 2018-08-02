package com.youngboss.dlock.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.concurrent.TimeUnit;

/**
 * @author ybd
 * @date 18-8-1
 * @contact yangbingdong1994@gmail.com
 */
@Data
@ConfigurationProperties(DLockConfigProperty.PREFIX)
public class DLockConfigProperty {
	public static final String PREFIX = "youngboss.dlock";

	private String host = "127.0.0.1";

	private String port = "6379";

	/**
	 * 获取锁时最大等待时间
	 */
	private Long waitTime = 3L;

	/**
	 * 锁释放时间，防止程序挂掉没有释放锁
	 */
	private Long leaseTime = 3L;

	/**
	 * 时间单位
	 */
	private TimeUnit timeUnit = TimeUnit.SECONDS;

	/**
	 * 分布式锁实现类型
	 */
	private DLockType dLockType = DLockType.REDISSON;

	private Spring spring = new Spring();

	@Data
	public static class Spring {
		private String scriptLocation = "scripts/release_lock.lua";
	}
}
