package com.youngboss.dlock.core.impl.spring;

import com.youngboss.dlock.config.DLockConfigProperty;
import com.youngboss.dlock.core.AfterAcquireAction;
import com.youngboss.dlock.core.AfterAcquireCommand;
import com.youngboss.dlock.core.DLock;
import com.youngboss.dlock.core.FailAcquireAction;
import com.youngboss.dlock.core.LockKeyGenerator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.core.types.Expiration;

import javax.annotation.Resource;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.youngboss.dlock.core.FailAcquireAction.DEFAULT_FAIL_ACQUIRE_ACTION;
import static java.util.Collections.singletonList;
import static org.springframework.data.redis.connection.RedisStringCommands.SetOption.SET_IF_ABSENT;

/**
 * @author ybd
 * @date 18-8-1
 * @contact yangbingdong1994@gmail.com
 */
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
@Data
public class SpringDataRedisDLock implements DLock {

	private final Long waitTime;
	private final Long leaseTime;
	private final TimeUnit timeUnit;

	@Resource
	private StringRedisTemplate stringRedisTemplate;

	@Resource
	private RedisScript<Boolean> script;

	public SpringDataRedisDLock(DLockConfigProperty property) {
		this.waitTime = property.getWaitTime();
		this.leaseTime = property.getLeaseTime();
		this.timeUnit = property.getTimeUnit();
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
		String lockKey = lockKeyGenerator.getLockKey();
		String value = UUID.randomUUID().toString();
		try (LockHolder holder = new LockHolder(lockKey, value, leaseTime, timeUnit)) {
			boolean acquire = false;
			long begin = System.currentTimeMillis();
			long waitTimeMillis = timeUnit.toMillis(waitTime);
			while ((System.currentTimeMillis() - begin) < waitTimeMillis) {
				if (holder.lockInner()) {
					acquire = true;
					acquireAction.doAction();
					break;
				}
				sleep();
			}
			if (!acquire) {
				failAcquireAction.doOnFail();
			}
		}
	}

	@Override
	public <T> T tryLockAndExecuteCommand(LockKeyGenerator lockKeyGenerator, AfterAcquireCommand<T> command, FailAcquireAction failAcquireAction, Long waitTime, Long leaseTime, TimeUnit timeUnit) throws Throwable {
		String lockKey = lockKeyGenerator.getLockKey();
		String value = UUID.randomUUID().toString();
		try (LockHolder holder = new LockHolder(lockKey, value, leaseTime, timeUnit)) {
			long begin = System.currentTimeMillis();
			long waitTimeMillis = timeUnit.toMillis(waitTime);
			while ((System.currentTimeMillis() - begin) < waitTimeMillis) {
				if (holder.lockInner()) {
					return command.executeCommand();
				}
				sleep();
			}
			failAcquireAction.doOnFail();
		}
		return null;
	}

	private void sleep() {
		try {
			TimeUnit.MICROSECONDS.sleep(1);
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}
	}

	@Data
	@Accessors(chain = true)
	@AllArgsConstructor
	private class LockHolder implements AutoCloseable {
		private String lockKey;
		private String value;
		private long leaseTime;
		private TimeUnit timeUnit;

		Boolean lockInner() {
			return stringRedisTemplate.execute((RedisCallback<Boolean>) connection -> {
				StringRedisConnection stringRedisConn = (StringRedisConnection) connection;
				return stringRedisConn.set(lockKey, value, Expiration.from(leaseTime, timeUnit), SET_IF_ABSENT);
			});
		}

		@Override
		public void close() {
			stringRedisTemplate.execute(script, singletonList(lockKey), value);
		}
	}
}
