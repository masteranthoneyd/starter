package com.youngboss.dlock.core;

import com.youngboss.dlock.exception.AcquireTimeOutException;

/**
 * @author ybd
 * @date 18-8-1
 * @contact yangbingdong1994@gmail.com
 */
public interface FailAcquireAction {
	FailAcquireAction DEFAULT_FAIL_ACQUIRE_ACTION = () -> { throw new AcquireTimeOutException(); };

	void doOnFail();
}
