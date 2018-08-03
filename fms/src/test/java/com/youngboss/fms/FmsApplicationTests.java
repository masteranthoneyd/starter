package com.youngboss.fms;

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

	/*public void fmsTest() {
		public FakeStateMachine<MsgStatus, MsgEvent, Message> msgFiniteStateMachine(ChangeMessageStateAction changeMessageStateAction) {
			return FakeStateMachineBuilder.<MsgStatus, MsgEvent, Message>init(MsgStatus.values())
					.withTransition().sourceState(MsgStatus.PENDING).event(MsgEvent.SEND).targetState(MsgStatus.SENT)
					.and()
					.withTransition().sourceState(MsgStatus.PENDING).event(MsgEvent.CANCEL).targetState(MsgStatus.CANCELED)
					.and()
					.withTransition().sourceState(MsgStatus.PENDING).event(MsgEvent.SEND_FAIL).targetState(MsgStatus.FAILURE)
					.and()
					.withTransition().sourceState(MsgStatus.CANCELED).event(MsgEvent.DELETE).targetState(MsgStatus.DELETED)
					.and()
					.withTransition().sourceState(MsgStatus.FAILURE).event(MsgEvent.DELETE).targetState(MsgStatus.DELETED)
					.and()
					.withTransition().sourceState(MsgStatus.SENT).event(MsgEvent.DELETE).targetState(MsgStatus.DELETED)
					.and()
					.withTransition().sourceState(MsgStatus.SENT).event(MsgEvent.INVALID).targetState(MsgStatus.INVALID)
					.and()
					.withTransition().sourceState(MsgStatus.INVALID).event(MsgEvent.DELETE).targetState(MsgStatus.DELETED)
					.and()
					.globalAction(changeMessageStateAction)
					.stateFunction(Message::getStatus)
					.build();
		}
	}*/

}
