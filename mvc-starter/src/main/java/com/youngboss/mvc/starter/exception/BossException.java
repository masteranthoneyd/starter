package com.youngboss.mvc.starter.exception;

/**
 * @author ybd
 * @date 18-7-17
 * @contact yangbingdong1994@gmail.com
 */
public class BossException extends RuntimeException {
	private static final long serialVersionUID = -1940208739294268501L;

	public BossException(String message) {
		super(message);
	}

	public BossException(String message, Throwable cause) {
		super(message, cause);
	}

	public BossException(Throwable cause) {
		super(cause);
	}

	public BossException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
