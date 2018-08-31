package com.youngboss.elasticjob.starter.util;

/**
 * @author lzt
 */
public class StringUtil {

	public static Boolean isEmptyString(final String value) {
		return ( null == value || "".equals(value.trim()) );
	}

	public static Boolean isNotEmptyString(final String value) {
		return !isEmptyString(value);
	}

}
