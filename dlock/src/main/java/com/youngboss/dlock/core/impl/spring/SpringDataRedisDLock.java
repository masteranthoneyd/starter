package com.youngboss.dlock.core.impl.spring;

import com.youngboss.dlock.core.AfterAcquireAction;
import com.youngboss.dlock.core.AfterAcquireCommand;
import com.youngboss.dlock.core.DLock;
import com.youngboss.dlock.core.FailAcquireAction;
import com.youngboss.dlock.core.LockKeyGenerator;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
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
		try {
			boolean acquire = false;
			long begin = System.currentTimeMillis();
			long waitTimeMillis = timeUnit.toMillis(waitTime);
			while ((System.currentTimeMillis() - begin) < waitTimeMillis) {
				if (lockInner(lockKey, value, leaseTime, timeUnit)) {
					acquire = true;
					acquireAction.doAction();
					break;
				}
				sleep();
			}
			if (!acquire) {
				failAcquireAction.doOnFail();
			}
		} finally {
			unlockInner(lockKey, value);
		}
	}

	@Override
	public <T> T tryLockAndExecuteCommand(LockKeyGenerator lockKeyGenerator, AfterAcquireCommand<T> command, FailAcquireAction failAcquireAction, Long waitTime, Long leaseTime, TimeUnit timeUnit) throws Throwable {
		String lockKey = lockKeyGenerator.getLockKey();
		String value = UUID.randomUUID().toString();
		try {
			long begin = System.currentTimeMillis();
			long waitTimeMillis = timeUnit.toMillis(waitTime);
			while ((System.currentTimeMillis() - begin) < waitTimeMillis) {
				if (lockInner(lockKey, value, leaseTime, timeUnit)) {
					return command.executeCommand();
				}
				sleep();
			}
			failAcquireAction.doOnFail();
		} finally {
			unlockInner(lockKey, value);
		}
		return null;
	}

	private Boolean lockInner(String k, String v, Long exTime, TimeUnit timeUnit) {
		return stringRedisTemplate.execute((RedisCallback<Boolean>) connection -> {
			StringRedisConnection stringRedisConn = (StringRedisConnection) connection;
			return stringRedisConn.set(k, v, Expiration.from(exTime, timeUnit), SET_IF_ABSENT);
		});
	}

	private void unlockInner(String k, String v) {
		stringRedisTemplate.execute(script, singletonList(k), v);
	}

	private void sleep() {
		try {
			TimeUnit.MICROSECONDS.sleep(1);
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}
	}
}
