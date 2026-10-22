package com.vbforge.exception_handling;

/**
 * Like {@code java.util.function.Function}, but the method may throw a CHECKED exception.
 * The standard functional interfaces cannot — which is exactly the lambda gotcha
 * shown in {@code CheckedVsUncheckedDemo}.
 *
 * @param <T> input type
 * @param <R> result type
 */
@FunctionalInterface
public interface ThrowingFunction<T, R> {

    R apply(T input) throws Exception;
}
