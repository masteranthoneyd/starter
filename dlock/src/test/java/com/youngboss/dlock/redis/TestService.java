package com.youngboss.dlock.redis;

import com.youngboss.dlock.aop.Lock;
import org.springframework.stereotype.Component;

/**
 * @author ybd
 * @date 18-8-2
 * @contact yangbingdong1994@gmail.com
 */
@Component
public class TestService {

	public static int i = 0;

	@Lock(prefixClass = TestService.class, key = "#args[0]")
	public void lockTest(Long id) {
		i++;
	}
}
