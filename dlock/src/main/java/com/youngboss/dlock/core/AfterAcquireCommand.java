package com.youngboss.dlock.core;

/**
 * @author ybd
 * @date 18-8-2
 * @contact yangbingdong1994@gmail.com
 */
public interface AfterAcquireCommand<T> {
	T executeCommand() throws Throwable;
}
