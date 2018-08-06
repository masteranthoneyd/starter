package com.youngboss.fms;

import com.youngboss.fms.fmstest.SomeEvent;
import com.youngboss.fms.fmstest.SomeSource;
import com.youngboss.fms.fmstest.StateEnum;
import com.youngboss.fms.fmstest.TestStateMachine;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FmsApplicationTests {

	@Resource
	private TestStateMachine testStateMachine;

	@Test
	public void fmsTest() {
		SomeSource someSource = new SomeSource().setSourceState(StateEnum.STATE0);
		testStateMachine.fire(someSource, SomeEvent.EVENT1);
		testStateMachine.fire(someSource, SomeEvent.EVENT2);
		testStateMachine.fire(someSource, SomeEvent.EVENT3);
		testStateMachine.fire(someSource, SomeEvent.EVENT4);
	}

}
