package com.youngboss.dlock.zookeeper;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author ybd
 * @date 18-8-2
 * @contact yangbingdong1994@gmail.com
 */
public class CuratorTest {
	private static String address = "127.0.0.1:2181";


	public static void main(String[] args) throws Exception {
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
		CuratorFramework client = CuratorFrameworkFactory.newClient(address, retryPolicy);
		client.start();

		//创建分布式锁, 锁空间的根节点路径为/curator/lock
		InterProcessMutex mutex = new InterProcessMutex(client, "/curator/lock");
		ExecutorService fixedThreadPool = Executors.newFixedThreadPool(5);

		for (int i = 0; i < 5; i++) {
			fixedThreadPool.submit(() -> {
				boolean flag = false;
				try {
					//尝试获取锁，最多等待5秒
					flag = mutex.acquire(5, TimeUnit.SECONDS);
					Thread currentThread = Thread.currentThread();
					if (flag) {
						System.out.println("线程" + currentThread.getId() + "获取锁成功");
					} else {
						System.out.println("线程" + currentThread.getId() + "获取锁失败");
					}
					//模拟业务逻辑，延时4秒
					Thread.sleep(4000);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (flag) {
						try {
							mutex.release();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			});
		}
		Thread.sleep(20000L);
		client.close();
		fixedThreadPool.shutdown();
	}
}
