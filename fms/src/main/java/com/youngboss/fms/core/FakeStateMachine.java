package com.youngboss.fms.core;

import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author ybd
 * @date 18-5-18
 * @contact yangbingdong1994@gmail.com
 * <p>
 * 有点不要太假的有限状态机...
 */
@Data
@Accessors(fluent = true, chain = true)
@Slf4j
public class FakeStateMachine<S, E, SOURCE> {

	private Map<S, Map<E, StateTransition<S, E, SOURCE>>> transitionMap;

	private Function<SOURCE, S> stateFunction;

	private StateTransition<S, E, SOURCE> findTransition(S s, E e) {
		return Optional.ofNullable(transitionMap.get(s))
					   .filter(m -> !m.isEmpty())
					   .map(m -> m.get(e))
					   .orElseGet(() -> {
						   throw new FsmException("Could not found transition, source state = " + s + ", event = " + e);
					   });
	}

	public void fire(SOURCE s, E e) {
		S sourceState = stateFunction.apply(s);
		StateTransition<S, E, SOURCE> transition = findTransition(sourceState, e);
		Action<S, SOURCE> action = transition.action();
		if (action != null) {
			action.doStateChangeAction(transition.targetState(), s);
		}else {
			log.warn("The action is null, sourceState = {}, event = {}", sourceState, e);
		}
	}
}
