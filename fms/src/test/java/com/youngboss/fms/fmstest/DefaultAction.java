package com.youngboss.fms.fmstest;

import com.youngboss.fms.core.Action;

/**
 * @author ybd
 * @date 18-8-6
 * @contact yangbingdong1994@gmail.com
 */
public class DefaultAction implements Action<StateEnum, SomeSource> {
	@Override
	public void doStateChangeAction(StateEnum stateEnum, SomeSource someSource) {
		System.out.println("Source state: " + someSource.getSourceState() + ", targetState: " + stateEnum);
		someSource.setSourceState(stateEnum);
	}
}
