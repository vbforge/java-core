package com.vbforge.practice.practice_5_reading_annotations_via_reflection;

import java.lang.annotation.*;

/**
 * Custom annotation: {@code @Retry}
 *
 * Marks a method as eligible for automatic retry on failure.
 * A framework (or AOP interceptor) would read this annotation at runtime
 * and wrap the method invocation in a retry loop.
 *
 * Elements:
 *   - {@code maxAttempts} — how many times to retry (default 3)
 *   - {@code delayMs}     — wait between retries in milliseconds (default 1000)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface Retry {
    int maxAttempts() default 3;
    long delayMs()    default 1000L;
}