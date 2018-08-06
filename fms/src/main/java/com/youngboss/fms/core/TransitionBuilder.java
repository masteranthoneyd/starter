package com.youngboss.fms.core;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

/**
 * @author ybd
 * @date 18-5-18
 * @contact yangbingdong1994@gmail.com
 */
@Data
@Accessors(chain = true, fluent = true)
public class TransitionBuilder<S extends Enum<S>, E extends Enum<E>, SOURCE extends StateSource<S>> {

	private S sourceState;

	private S targetState;

	private E event;

	private Action<S, E, SOURCE> action;

	private FakeStateMachineBuilder<S, E, SOURCE> fakeStateMachineBuilder;

	public FakeStateMachineBuilder<S, E, SOURCE> and() {
		if (sourceState == null || targetState == null || event == null) {
			throw new FsmException();
		}
		Map<E, StateTransition<S, E, SOURCE>> eventMap = fakeStateMachineBuilder.getEventMap(sourceState);
		if (eventMap == null) {
			throw new FsmException();
		}
		eventMap.put(event, new StateTransition<S, E, SOURCE>().sourceState(sourceState)
															   .targetState(targetState)
															   .event(event)
															   .action(action));
		return fakeStateMachineBuilder;
	}

	public TransitionBuilder<S, E, SOURCE> sameSourceState() {
		return this.and()
				   .withTransition()
				   .sourceState(sourceState);
	}

}
