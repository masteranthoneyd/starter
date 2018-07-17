package com.youngboss.disruptor.starter;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventTranslatorOneArg;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

import static org.apache.commons.collections4.CollectionUtils.isEmpty;


/**
 * @author ybd
 * @date 18-5-7
 * @contact yangbingdong1994@gmail.com
 */
@Data
@Accessors(chain = true, fluent = true)
public class DisruptorPublisherBuilder<S, E extends DisruptorEvent<S>> {
	private Class<E> eventType;

	private DefaultDisruptorCommonComponents disruptorCommonComponents;

	private EventTranslatorOneArg<E, S> translatorOneArg;

	private List<DisruptorEventHandler<E>> disruptorEventHandlers;

	public DisruptorPublisherBuilder(Class<E> eventType) {
		this.eventType = eventType;
	}


	private E newEventInstance() throws Exception {
		return eventType.newInstance();
	}

	public DisruptorPublisherBuilder<S, E> defaultTranslator() {
		if (translatorOneArg == null) {
			translatorOneArg = (event, sequence, arg0) -> event.setSource(arg0);
		}
		return this;
	}

	public DisruptorPublisherBuilder<S, E> customDisruptorComponent(DisruptorComponentCustom disruptorComponentCustom) {
		if (disruptorCommonComponents == null) {
			disruptorCommonComponents = new DefaultDisruptorCommonComponents();
		}
		disruptorComponentCustom.apply(disruptorCommonComponents);
		return this;
	}

	public AbstractDisruptorPublisher<S, E> build() {
		if (isEmpty(disruptorEventHandlers)) {
			throw new IllegalArgumentException();
		}

		return new AbstractDisruptorPublisher<S, E>() {
			@Override
			protected DefaultDisruptorCommonComponents provideDisruptorInitialComponents() {
				return disruptorCommonComponents;
			}

			@Override
			protected EventFactory<E> provideEventFactory() {
				return () -> {
					try {
						return newEventInstance();
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				};
			}

			@Override
			protected List<DisruptorEventHandler<E>> provideDisruptorEventHandlers() {
				return disruptorEventHandlers;
			}

			@Override
			protected EventTranslatorOneArg<E, S> provideTranslatorOneArg() {
				return translatorOneArg;
			}

			@Override
			protected Class<E> provideEventType() {
				return eventType;
			}
		};
	}
}
