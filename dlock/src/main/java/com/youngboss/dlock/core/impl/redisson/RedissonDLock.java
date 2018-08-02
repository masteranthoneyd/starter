package com.youngboss.dlock.core.impl.redisson;

import com.youngboss.dlock.core.AfterAcquireAction;
import com.youngboss.dlock.core.AfterAcquireCommand;
import com.youngboss.dlock.core.DLock;
import com.youngboss.dlock.core.FailAcquireAction;
import com.youngboss.dlock.core.LockKeyGenerator;
import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.util.concurrent.TimeUnit;

import static com.youngboss.dlock.core.FailAcquireAction.DEFAULT_FAIL_ACQUIRE_ACTION;

/**
 * @author ybd
 * @date 18-8-1
 * @contact yangbingdong1994@gmail.com
 */
@Data
public class RedissonDLock implements DLock {

	private final Long waitTime;
	private final Long leaseTime;
	private final TimeUnit timeUnit;
	private final String redisUrl;
	private final RedissonClient redisson;

	public RedissonDLock(Long waitTime, Long leaseTime, TimeUnit timeUnit, String redisUrl) {
		this.waitTime = waitTime;
		this.leaseTime = leaseTime;
		this.timeUnit = timeUnit;
		this.redisUrl = redisUrl;
		Config config = new Config();
		config.useSingleServer().setAddress("redis://" + redisUrl);
//		config.setTransportMode(TransportMode.EPOLL);
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
		RLock lock = redisson.getLock(lockKeyGenerator.getLockKey());
		try {
			boolean acquire = lock.tryLock(waitTime, leaseTime, timeUnit);
			if (acquire) {
				acquireAction.doAction();
			}else {
				failAcquireAction.doOnFail();
			}
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} finally {
			lock.unlockAsync();
		}
	}

	@Override
	public <T> T tryLockAndExecuteCommand(LockKeyGenerator lockKeyGenerator, AfterAcquireCommand<T> command, FailAcquireAction failAcquireAction, Long waitTime, Long leaseTime, TimeUnit timeUnit) throws Throwable {
		RLock lock = redisson.getLock(lockKeyGenerator.getLockKey());
		try {
			boolean acquire = lock.tryLock(waitTime, leaseTime, timeUnit);
			if (acquire) {
				return command.executeCommand();
			}
			failAcquireAction.doOnFail();
		} finally {
			lock.unlockAsync();
		}
		return null;
	}
}
