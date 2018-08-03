package com.youngboss.fms.core;

/**
 * @author ybd
 * @date 18-5-21
 * @contact yangbingdong1994@gmail.com
 */
public class FsmException extends RuntimeException {
	private static final long serialVersionUID = 2632987938495199529L;

	public FsmException(String s) {
		super(s);
	}

	public FsmException() {
	}
}
