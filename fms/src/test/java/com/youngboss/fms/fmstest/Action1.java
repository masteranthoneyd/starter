package com.youngboss.fms.fmstest;

import com.youngboss.fms.core.Action;

/**
 * @author ybd
 * @date 18-8-6
 * @contact yangbingdong1994@gmail.com
 */
public class Action1 implements Action<StateEnum, SomeEvent, SomeSource> {
	@Override
	public void doStateChangeAction(StateEnum stateEnum, SomeEvent someEvent, SomeSource someSource) {
		System.out.println("This is action1");
		someSource.setSourceState(stateEnum);
	}
}
