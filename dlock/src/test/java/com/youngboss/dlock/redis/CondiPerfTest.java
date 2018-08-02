package com.youngboss.dlock.redis;

import com.youngboss.dlock.core.DLock;
import org.databene.contiperf.PerfTest;
import org.databene.contiperf.junit.ContiPerfRule;
import org.junit.AfterClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author ybd
 * @date 18-8-1
 * @contact yangbingdong1994@gmail.com
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class CondiPerfTest {
	@Rule
	public ContiPerfRule contiPerfRule = new ContiPerfRule();

	@Resource
	private DLock dLock;

	private static int success = 0;
	private static int fail = 0;
	private static LongAdder longAdder = new LongAdder();

	@PerfTest(duration = 20000, threads = 6, rampUp = 500, warmUp = 500)
	@Test
	public void redissonLockTest() {
		longAdder.increment();
		dLock.tryLockAndAction(() -> "redisson-lockInner", () -> success ++);
	}

	@AfterClass
	public static void destroy() {
		System.out.println("--------------------------  total: " + longAdder.longValue() + "  success: " + success + "  fail: " + fail);
	}

}
