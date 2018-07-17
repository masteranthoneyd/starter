package com.youngboss.disruptor.starter;

/**
 * @author ybd
 * @date 18-5-17
 * @contact yangbingdong1994@gmail.com
 */
@FunctionalInterface
public interface DisruptorComponentCustom {
	void apply(DefaultDisruptorCommonComponents defaultDisruptorCommonComponents);
}
