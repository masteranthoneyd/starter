package com.youngboss.fms.fmstest;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author ybd
 * @date 18-8-6
 * @contact yangbingdong1994@gmail.com
 */
@Data
@Accessors(chain = true)
public class SomeSource {

	private StateEnum sourceState;
}
