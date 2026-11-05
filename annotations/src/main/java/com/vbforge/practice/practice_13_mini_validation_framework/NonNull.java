package com.vbforge.practice.practice_13_mini_validation_framework;

import java.lang.annotation.*;

// -----------------------------------------------------------------------
// Constraint annotation set for the mini-validation framework
// -----------------------------------------------------------------------

/**
 * {@code @NonNull} — the annotated field must not be null.
 *
 * Analogous to {@code jakarta.validation.constraints.NotNull}.
 * The {@code message} element provides a human-readable violation description.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
@interface NonNull {
    String message() default "must not be null";
}

