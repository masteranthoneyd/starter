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
public class StateTransition<S, E, SOURCE> {
	private S sourceState;

	private S targetState;

	private E event;

	private Action<S, SOURCE> action;
}
