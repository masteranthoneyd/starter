package com.youngboss.dlock.core.impl.redisson;

import com.youngboss.dlock.config.DLockConfigProperty;
import com.youngboss.dlock.core.AfterAcquireAction;
import com.youngboss.dlock.core.AfterAcquireCommand;
import com.youngboss.dlock.core.DLock;
import com.youngboss.dlock.core.FailAcquireAction;
import com.youngboss.dlock.core.LockKeyGenerator;
import io.netty.channel.epoll.Epoll;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.redisson.config.TransportMode;

import java.util.concurrent.TimeUnit;

import static com.youngboss.dlock.core.FailAcquireAction.DEFAULT_FAIL_ACQUIRE_ACTION;

/**
 * @author ybd
 * @date 18-8-1
 * @contact yangbingdong1994@gmail.com
 */
@Data
@Slf4j
public class RedissonDLock implements DLock {

	private final Long waitTime;
	private final Long leaseTime;
	private final TimeUnit timeUnit;
	private final RedissonClient redisson;

	public RedissonDLock(DLockConfigProperty property) {
		this.waitTime = property.getWaitTime();
		this.leaseTime = property.getLeaseTime();
		this.timeUnit = property.getTimeUnit();

		Config config = new Config();
		SingleServerConfig singleServerConfig = config.useSingleServer();
		singleServerConfig.setAddress("redis://" + property.getHost() + ":" + property.getPort());
		if (property.getPassword() != null && property.getPassword().trim().length() > 0) {
			singleServerConfig.setPassword(property.getPassword());
		}
		try {
			Class.forName("io.netty.channel.epoll.Epoll");
			if (Epoll.isAvailable()) {
				config.setTransportMode(TransportMode.EPOLL);
				log.info("Starting with optional epoll library");
			} else {
				log.info("Starting without optional epoll library");
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		redisson = Redisson.create(config);
	}

	@Override
	public void tryLockAndAction(LockKeyGenerator lockKeyGenerator, AfterAcquireAction acquireAction) {
		tryLockAndAction(lockKeyGenerator, acquireAction, waitTime, leaseTime, timeUnit);
	}

	@Override
	public void tryLockAndAction(LockKeyGenerator lockKeyGenerator, AfterAcquireAction acquireAction, Long waitTime, Long leaseTime, TimeUnit timeUnit) {
		tryLockAndAction(lockKeyGenerator, acquireAction, DEFAULT_FAIL_ACQUIRE_ACTION, waitTime, leaseTime, timeUnit);
	}

	@Override
	public void tryLockAndAction(LockKeyGenerator lockKeyGenerator, AfterAcquireAction acquireAction, FailAcquireAction failAcquireAction, Long waitTime, Long leaseTime, TimeUnit timeUnit) {
		try (LockHolder holder = new LockHolder(redisson.getLock(lockKeyGenerator.getLockKey()))) {
			boolean acquire = holder.getLock().tryLock(waitTime, leaseTime, timeUnit);
			if (acquire) {
				acquireAction.doAction();
			} else {
				failAcquireAction.doOnFail();
			}
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public <T> T tryLockAndExecuteCommand(LockKeyGenerator lockKeyGenerator, AfterAcquireCommand<T> command, FailAcquireAction failAcquireAction, Long waitTime, Long leaseTime, TimeUnit timeUnit) throws Throwable {
		try (LockHolder holder = new LockHolder(redisson.getLock(lockKeyGenerator.getLockKey()))) {
			boolean acquire = holder.getLock().tryLock(waitTime, leaseTime, timeUnit);
			if (acquire) {
				return command.executeCommand();
			}
			failAcquireAction.doOnFail();
		}
		return null;
	}

	@Data
	@Accessors(chain = true)
	@AllArgsConstructor
	private static class LockHolder implements AutoCloseable {
		private RLock lock;

		@Override
		public void close() {
			lock.unlockAsync();
		}
	}
}
