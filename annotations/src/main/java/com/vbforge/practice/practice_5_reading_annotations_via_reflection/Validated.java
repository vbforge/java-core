package com.vbforge.practice.practice_5_reading_annotations_via_reflection;

import java.lang.annotation.*;

/**
 * Custom annotation: {@code @Validated}
 *
 * Marks a method parameter as requiring validation.
 * Demonstrates PARAMETER target — annotations can be placed on
 * individual parameters of methods and constructors.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@Documented
public @interface Validated {
    String value() default "";
}