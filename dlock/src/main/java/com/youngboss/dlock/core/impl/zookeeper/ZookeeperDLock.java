package com.youngboss.dlock.core.impl.zookeeper;

import com.youngboss.dlock.core.AfterAcquireAction;
import com.youngboss.dlock.core.AfterAcquireCommand;
import com.youngboss.dlock.core.DLock;
import com.youngboss.dlock.core.FailAcquireAction;
import com.youngboss.dlock.core.LockKeyGenerator;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.DisposableBean;

import java.util.concurrent.TimeUnit;

import static com.youngboss.dlock.core.FailAcquireAction.DEFAULT_FAIL_ACQUIRE_ACTION;

/**
 * @author ybd
 * @date 18-8-2
 * @contact yangbingdong1994@gmail.com
 */
@Data
@Slf4j
public class ZookeeperDLock implements DLock, DisposableBean {

	private final Long waitTime;
	private final TimeUnit timeUnit;
	private final String zookeeperUrl;
	private final String lockPath;
	private final CuratorFramework client;
	private final InterProcessMutex mutex;

	public ZookeeperDLock(Long waitTime, TimeUnit timeUnit, String zookeeperUrl, String lockPath) {
		this.waitTime = waitTime;
		this.timeUnit = timeUnit;
		this.zookeeperUrl = zookeeperUrl;
		this.lockPath = lockPath;

		RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
		client = CuratorFrameworkFactory.newClient(this.zookeeperUrl, retryPolicy);
		client.start();
		mutex = new InterProcessMutex(client, this.lockPath);
	}

	@Override
	public void tryLockAndAction(LockKeyGenerator lockKeyGenerator, AfterAcquireAction acquireAction) {
		tryLockAndAction(lockKeyGenerator, acquireAction, waitTime, 0L, timeUnit);
	}

	@Override
	public void tryLockAndAction(LockKeyGenerator lockKeyGenerator, AfterAcquireAction acquireAction, Long waitTime, Long leaseTime, TimeUnit timeUnit) {
		tryLockAndAction(lockKeyGenerator, acquireAction, DEFAULT_FAIL_ACQUIRE_ACTION, waitTime, leaseTime, timeUnit);
	}

	@Override
	public void tryLockAndAction(LockKeyGenerator lockKeyGenerator, AfterAcquireAction acquireAction, FailAcquireAction failAcquireAction, Long waitTime, Long leaseTime, TimeUnit timeUnit) {
		boolean acquire = false;
		try {
			acquire = mutex.acquire(waitTime, timeUnit);
			if (acquire) {
				acquireAction.doAction();
			} else {
				failAcquireAction.doOnFail();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (acquire) {
				try {
					mutex.release();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public <T> T tryLockAndExecuteCommand(LockKeyGenerator lockKeyGenerator, AfterAcquireCommand<T> command, FailAcquireAction failAcquireAction, Long waitTime, Long leaseTime, TimeUnit timeUnit) throws Throwable {
		boolean acquire = false;
		try {
			acquire = mutex.acquire(waitTime, timeUnit);
			if (acquire) {
				return command.executeCommand();
			}
			failAcquireAction.doOnFail();
		} finally {
			if (acquire) {
				mutex.release();
			}
		}
		return null;
	}

	@Override
	public void destroy() throws Exception {
		log.info("Close zookeeper connect, session id: {}", client.getZookeeperClient().getZooKeeper().getSessionId());
		client.close();
	}
}
