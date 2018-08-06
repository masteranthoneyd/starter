package com.youngboss.fms.fmstest;

import com.youngboss.fms.core.StateSource;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author ybd
 * @date 18-8-6
 * @contact yangbingdong1994@gmail.com
 */
@Data
@Accessors(chain = true)
public class SomeSource implements StateSource<StateEnum> {

	private StateEnum sourceState;

	@Override
	public StateEnum getState() {
		return sourceState;
	}
}
