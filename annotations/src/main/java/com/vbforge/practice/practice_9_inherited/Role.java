package com.vbforge.practice.practice_9_inherited;

import java.lang.annotation.*;

/**
 * {@code @Role} — class-level annotation marked with {@code @Inherited}.
 *
 * When a class is annotated with @Role, all its subclasses automatically
 * inherit the annotation — no need to repeat it on the subclass.
 *
 * Caveat: @Inherited ONLY works for class-level annotations.
 * It does NOT propagate to implemented interfaces.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
@interface Role {
    String value() default "USER";
}
