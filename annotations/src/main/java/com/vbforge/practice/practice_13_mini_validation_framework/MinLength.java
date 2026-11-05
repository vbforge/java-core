package com.vbforge.practice.practice_13_mini_validation_framework;

import java.lang.annotation.*;

/**
 * {@code @MinLength} — the annotated String field must not be blank
 * and must have at least {@code value} characters.
 * <p>
 * Analogous to {@code jakarta.validation.constraints.Size(min = N)}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
@interface MinLength {
    int value();  // required — no default
}
