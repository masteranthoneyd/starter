package com.youngboss.disruptor.starter;

import java.util.List;

import static com.lmax.disruptor.dsl.ProducerType.MULTI;

/**
 * @author ybd
 * @date 18-6-29
 * @contact yangbingdong1994@gmail.com
 */
public class PublisherFactory {
	public static <S, E extends DisruptorEvent<S>> DisruptorPublisher<S> getPublisher(Class<E> clazz, List<DisruptorEventHandler<E>> handlerList) {
		return DisruptorPublisherBuilderFactory.getInstance()
											   .builder(clazz)
											   .customDisruptorComponent(c -> c.setProducerType(MULTI)
																			   .setBufferSizePower(10))
											   .disruptorEventHandlers(handlerList)
											   .defaultTranslator()
											   .build();
	}
}
