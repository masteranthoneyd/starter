package com.youngboss.fms.core;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static org.apache.commons.collections4.MapUtils.isEmpty;
import static org.apache.commons.collections4.MapUtils.isNotEmpty;

/**
 * @author ybd
 * @date 18-5-18
 * @contact yangbingdong1994@gmail.com
 */
@Data
@Accessors(chain = true, fluent = true)
public class FakeStateMachineBuilder<S, E, SOURCE> {

	private Map<S, Map<E, StateTransition<S, E, SOURCE>>> groupMap;
	private Function<SOURCE, S> stateFunction;

	public static <S, E, SOURCE> FakeStateMachineBuilder<S, E, SOURCE> init(S[] ss) {
		Map<S, Map<E, StateTransition<S, E, SOURCE>>> m = new HashMap<>(16);
		for (S s : ss) {
			m.put(s, new HashMap<>(4));
		}
		FakeStateMachineBuilder<S, E, SOURCE> builder = new FakeStateMachineBuilder<>();
		builder.groupMap(m);
		return builder;
	}

	public TransitionBuilder<S, E, SOURCE> withTransition() {
		return new TransitionBuilder<S, E, SOURCE>().fakeStateMachineBuilder(this);
	}

	public Map<E, StateTransition<S, E, SOURCE>> getEventMap(S s) {
		return groupMap.get(s);
	}

	public FakeStateMachine<S, E, SOURCE> build() {
		if (isEmpty(groupMap) || stateFunction == null) {
			throw new FsmException();
		}
		FakeStateMachine<S, E, SOURCE> stateMachine = new FakeStateMachine<>();
		return stateMachine.transitionMap(groupMap)
						   .stateFunction(stateFunction);
	}

	public FakeStateMachineBuilder<S, E, SOURCE> defaultAction(Action<S, SOURCE> action) {
		groupMap.forEach((k, v) -> {
			if (isNotEmpty(v)) {
				v.forEach((key, transition) -> {
					if (transition != null) {
						transition.putActionIfAbsent(action);
					}
				});
			}
		});
		return this;
	}

}
