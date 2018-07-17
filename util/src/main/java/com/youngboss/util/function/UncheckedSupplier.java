package com.youngboss.util.function;

/**
 * @author ybd
 * @date 17-11-28.
 */
@FunctionalInterface
public interface UncheckedSupplier<T> {
	T get() throws Exception;
}
