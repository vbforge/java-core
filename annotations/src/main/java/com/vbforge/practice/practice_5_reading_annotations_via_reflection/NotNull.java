package com.vbforge.practice.practice_5_reading_annotations_via_reflection;

import java.lang.annotation.*;

/**
 * Custom annotation: {@code @NotNull}
 *
 * Marks a field as a non-null constraint.
 * Demonstrates FIELD target and how validation frameworks (e.g. Hibernate Validator)
 * use field-level annotations at runtime to enforce constraints.
 *
 * Note: This is a simplified custom version for learning purposes.
 * Production code would use {@code jakarta.validation.constraints.NotNull}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface NotNull {
    String message() default "Field must not be null";
}