package com.youngboss.fms.core;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author ybd
 * @date 18-5-18
 * @contact yangbingdong1994@gmail.com
 */
@Data
@Accessors(chain = true, fluent = true)
public class StateTransition<S extends Enum<S>, E extends Enum<E>, SOURCE extends StateSource<S>> {
	private S sourceState;

	private S targetState;

	private E event;

	private Action<S, E, SOURCE> action;

	void putActionIfAbsent(Action<S, E, SOURCE> action) {
		if (this.action == null) {
			this.action = action;
		}
	}
}
