package com.youngboss.disruptor.starter;

/**
 * @author ybd
 * @date 18-5-7
 * @contact yangbingdong1994@gmail.com
 */
public class DisruptorPublisherBuilderFactory {
	private DisruptorPublisherBuilderFactory(){}

	public static DisruptorPublisherBuilderFactory getInstance() {
		return SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder{
		private static DisruptorPublisherBuilderFactory INSTANCE = new DisruptorPublisherBuilderFactory();
	}

	public <S, E extends DisruptorEvent<S>> DisruptorPublisherBuilder<S, E> builder(Class<E> eventType) {
		return new DisruptorPublisherBuilder<>(eventType);
	}
}
