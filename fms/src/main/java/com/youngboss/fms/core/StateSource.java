package com.youngboss.fms.core;

/**
 * @author ybd
 * @date 18-8-6
 * @contact yangbingdong1994@gmail.com
 */
public interface StateSource<S extends Enum<S>> {
	S getState();

}
