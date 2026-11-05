package com.vbforge.practice.practice_5_reading_annotations_via_reflection;

import java.lang.annotation.*;

/**
 * Custom annotation: {@code @Audit}
 *
 * Marks a method for audit logging.
 * Demonstrates annotation elements (attributes), default values,
 * and RUNTIME retention so the annotation is readable via reflection.
 *
 * Design notes:
 *   - {@code action}  — required element (no default), callers must supply it
 *   - {@code level}   — optional, defaults to "INFO"
 *   - {@code enabled} — optional flag, defaults to true
 *
 * Usage:
 *   {@code @Audit(action = "PAYMENT_PROCESS", level = "WARN")}
 *   public void processPayment(...) { ... }
 */
@Retention(RetentionPolicy.RUNTIME)   // must be RUNTIME to be readable via reflection
@Target(ElementType.METHOD)           // can only be applied to methods
@Documented                           // include in generated Javadoc
public @interface Audit {
    String action();                  // required — no default
    String level()   default "INFO";  // optional
    boolean enabled() default true;   // optional flag
}