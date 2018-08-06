package com.youngboss.fms.core;

/**
 * @author ybd
 * @date 18-5-18
 * @contact yangbingdong1994@gmail.com
 */
public interface Action<TARGET_S extends Enum<TARGET_S>, E extends Enum<E>, SOURCE extends StateSource<TARGET_S>> {
	void doStateChangeAction(TARGET_S s, E event, SOURCE source);
}
