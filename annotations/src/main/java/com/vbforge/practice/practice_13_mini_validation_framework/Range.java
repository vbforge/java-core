package com.vbforge.practice.practice_13_mini_validation_framework;

import java.lang.annotation.*;

/**
 * {@code @Range} — the annotated numeric field must be between {@code min}
 * and {@code max} inclusive.
 * <p>
 * Analogous to {@code org.hibernate.validator.constraints.Range}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
@interface Range {
    long min() default Long.MIN_VALUE;

    long max() default Long.MAX_VALUE;
}
