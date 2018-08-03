package com.youngboss.fms.core;

/**
 * @author ybd
 * @date 18-5-18
 * @contact yangbingdong1994@gmail.com
 */
public interface Action<TARGET_S, SOURCE> {
	void doStateChangeAction(TARGET_S s, SOURCE source);
}
