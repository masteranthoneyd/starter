package com.youngboss.fms.core;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.collections4.MapUtils.isEmpty;
import static org.apache.commons.collections4.MapUtils.isNotEmpty;

/**
 * @author ybd
 * @date 18-5-18
 * @contact yangbingdong1994@gmail.com
 */
@Data
@Accessors(chain = true, fluent = true)
public class FakeStateMachineBuilder<S extends Enum<S>, E extends Enum<E>, SOURCE extends StateSource<S>> {

	private Map<S, Map<E, StateTransition<S, E, SOURCE>>> groupMap;
	private AbstractFakeStateMachine<S, E, SOURCE> fakeStateMachine;

	public static <S extends Enum<S>, E extends Enum<E>, SOURCE extends StateSource<S>> FakeStateMachineBuilder<S, E, SOURCE> init(EnumSet<S> enumSet) {
		Map<S, Map<E, StateTransition<S, E, SOURCE>>> m = new HashMap<>(16);
		for (S s : enumSet) {
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

	public void build() {
		if (isEmpty(groupMap)) {
			throw new FsmException();
		}
		fakeStateMachine.transitionMap(groupMap);
	}

	public FakeStateMachineBuilder<S, E, SOURCE> defaultAction(Action<S, E, SOURCE> action) {
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
