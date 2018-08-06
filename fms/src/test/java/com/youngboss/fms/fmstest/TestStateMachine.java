package com.youngboss.fms.fmstest;

import com.youngboss.fms.core.AbstractFakeStateMachine;
import com.youngboss.fms.core.FakeStateMachineBuilder;
import org.springframework.stereotype.Component;

/**
 * @author ybd
 * @date 18-8-6
 * @contact yangbingdong1994@gmail.com
 */
@Component
public class TestStateMachine extends AbstractFakeStateMachine<StateEnum, SomeEvent, SomeSource> {
	@Override
	public void transitionConfig(FakeStateMachineBuilder<StateEnum, SomeEvent, SomeSource> machineBuilder) {
		machineBuilder.withTransition().sourceState(StateEnum.STATE0).event(SomeEvent.EVENT1).targetState(StateEnum.STATE1).action(new Action1())
					  .sameSourceState().event(SomeEvent.EVENT1_1).targetState(StateEnum.STATE1_1)
					  .and()
					  .withTransition().sourceState(StateEnum.STATE1).event(SomeEvent.EVENT2).targetState(StateEnum.STATE2)
					  .and()
					  .withTransition().sourceState(StateEnum.STATE2).event(SomeEvent.EVENT3).targetState(StateEnum.STATE3)
					  .and()
					  .withTransition().sourceState(StateEnum.STATE3).event(SomeEvent.EVENT4).targetState(StateEnum.STATE4)
					  .and()
					  .defaultAction(new DefaultAction())
					  .build();
	}

}
