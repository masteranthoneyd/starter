package com.youngboss.fms.core;

import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.EnumSet;
import java.util.Map;
import java.util.Optional;

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
public abstract class AbstractFakeStateMachine<S extends Enum<S>, E extends Enum<E>, SOURCE extends StateSource<S>> implements InitializingBean {

	private Map<S, Map<E, StateTransition<S, E, SOURCE>>> transitionMap;

	private StateTransition<S, E, SOURCE> findTransition(S s, E e) {
		return Optional.ofNullable(transitionMap.get(s))
					   .filter(m -> !m.isEmpty())
					   .map(m -> m.get(e))
					   .orElseGet(() -> {
						   throw new FsmException("Could not found transition, source state = " + s + ", event = " + e);
					   });
	}

	public void fire(SOURCE s, E e) {
		S sourceState = s.getState();
		StateTransition<S, E, SOURCE> transition = findTransition(sourceState, e);
		Action<S, E, SOURCE> action = transition.action();
		if (action != null) {
			action.doStateChangeAction(transition.targetState(), e, s);
		} else {
			throw new FsmException("Illegal action, sourceState = " + sourceState + ", event = " + e);
		}
	}

	private FakeStateMachineBuilder<S, E, SOURCE> init(EnumSet<S> enumSet) {
		return FakeStateMachineBuilder.<S, E, SOURCE>init(enumSet).fakeStateMachine(this);
	}

	@Override
	public void afterPropertiesSet() {
		Type actualTypeArgument = ((ParameterizedType) this.getClass()
														   .getGenericSuperclass()).getActualTypeArguments()[0];
		@SuppressWarnings("unchecked")
		Class<S> clazz = (Class<S>) actualTypeArgument;
		EnumSet<S> enumSet = EnumSet.allOf(clazz);
		FakeStateMachineBuilder<S, E, SOURCE> builder = init(enumSet);
		transitionConfig(builder);
	}

	public abstract void transitionConfig(FakeStateMachineBuilder<S, E, SOURCE> machineBuilder);

}
