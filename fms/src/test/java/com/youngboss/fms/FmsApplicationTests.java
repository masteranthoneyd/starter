package com.youngboss.fms;

import com.youngboss.fms.core.FakeStateMachine;
import com.youngboss.fms.core.FakeStateMachineBuilder;
import com.youngboss.fms.fmstest.Action1;
import com.youngboss.fms.fmstest.DefaultAction;
import com.youngboss.fms.fmstest.SomeEvent;
import com.youngboss.fms.fmstest.SomeSource;
import com.youngboss.fms.fmstest.StateEnum;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FmsApplicationTests {

	@Test
	public void contextLoads() {
	}

	public static void fmsTest() {
		FakeStateMachine<StateEnum, SomeEvent, SomeSource> stateMachine = FakeStateMachineBuilder.<StateEnum, SomeEvent, SomeSource>init(StateEnum.values())
				.withTransition().sourceState(StateEnum.STATE0).event(SomeEvent.EVENT1).targetState(StateEnum.STATE1).action(new Action1())
				                 .sameSourceState().event(SomeEvent.EVENT1_1).targetState(StateEnum.STATE1_1)
				.and()
				.withTransition().sourceState(StateEnum.STATE1).event(SomeEvent.EVENT2).targetState(StateEnum.STATE2)
				.and()
				.withTransition().sourceState(StateEnum.STATE2).event(SomeEvent.EVENT3).targetState(StateEnum.STATE3)
				.and()
				.withTransition().sourceState(StateEnum.STATE3).event(SomeEvent.EVENT4).targetState(StateEnum.STATE4)
				.and()
				.defaultAction(new DefaultAction())
				.stateFunction(SomeSource::getSourceState)
				.build();

		SomeSource someSource = new SomeSource().setSourceState(StateEnum.STATE0);
		stateMachine.fire(someSource, SomeEvent.EVENT1);
		stateMachine.fire(someSource, SomeEvent.EVENT2);
		stateMachine.fire(someSource, SomeEvent.EVENT3);
		stateMachine.fire(someSource, SomeEvent.EVENT4);

	}

	public static void main(String[] args) {
		fmsTest();
	}

}
