package com.vbforge.practice.practice_6_meta_annotations_overview;

import com.vbforge.practice.practice_5_reading_annotations_via_reflection.Audit;
import com.vbforge.practice.practice_5_reading_annotations_via_reflection.Retry;

import java.lang.annotation.*;
import java.util.Arrays;

/**
 * Demonstrates Java meta-annotations — annotations that annotate other annotations.
 *
 * Meta-annotations are defined in {@link java.lang.annotation} and control
 * how custom annotations behave.
 *
 * ┌─────────────────┬──────────────────────────────────────────────────────┐
 * │ Meta-annotation │ Meaning                                              │
 * ├─────────────────┼──────────────────────────────────────────────────────┤
 * │ @Retention      │ When the annotation is available                     │
 * │                 │   SOURCE  → discarded by compiler                    │
 * │                 │   CLASS   → in .class but not loaded (default)       │
 * │                 │   RUNTIME → loaded by JVM; readable via reflection   │
 * ├─────────────────┼──────────────────────────────────────────────────────┤
 * │ @Target         │ Which elements the annotation can be placed on       │
 * │                 │   TYPE, FIELD, METHOD, PARAMETER, CONSTRUCTOR, ...   │
 * ├─────────────────┼──────────────────────────────────────────────────────┤
 * │ @Documented     │ Include annotation in Javadoc output                 │
 * ├─────────────────┼──────────────────────────────────────────────────────┤
 * │ @Inherited      │ Subclasses inherit the annotation from superclass    │
 * │                 │   ONLY works for class-level annotations             │
 * ├─────────────────┼──────────────────────────────────────────────────────┤
 * │ @Repeatable     │ Same annotation can appear multiple times on one     │
 * │                 │   element; requires a container annotation           │
 * └─────────────────┴──────────────────────────────────────────────────────┘
 */
public class MetaAnnotationsDemo {

    // -----------------------------------------------------------------------
    // 1. @Retention — reading retention policy via reflection
    // -----------------------------------------------------------------------

    public static void demonstrateRetention() {
        System.out.println("=== 1. @Retention — policy on each annotation ===\n");

        Class<?>[] annotationTypes = {
            Audit.class,
            Retry.class,
            // Note: @Override has SOURCE retention — not loadable at runtime
        };

        for (Class<?> type : annotationTypes) {
            Retention ret = type.getAnnotation(Retention.class);
            System.out.printf("%-30s → retention = %s%n",
                "@" + type.getSimpleName(),
                ret != null ? ret.value() : "(default = CLASS)"
            );
        }

        System.out.println();
    }

    // -----------------------------------------------------------------------
    // 2. @Target — reading which elements an annotation targets
    // -----------------------------------------------------------------------

    public static void demonstrateTarget() {
        System.out.println("=== 2. @Target — allowed element types ===\n");

        Class<?>[] annotationTypes = { Audit.class, Retry.class };

        for (Class<?> type : annotationTypes) {
            Target target = type.getAnnotation(Target.class);
            if (target != null) {
                System.out.printf("%-30s → %s%n",
                    "@" + type.getSimpleName(),
                    Arrays.toString(target.value())
                );
            }
        }

        System.out.println();
    }

    // -----------------------------------------------------------------------
    // 3. @Inherited  — see practice_9_inherited / InheritedAnnotationDemo
    // 4. @Repeatable — see practice_10_repeatable / RepeatableAnnotationDemo
    //    (they live next to the other demos of the same concept)
    // -----------------------------------------------------------------------

    // -----------------------------------------------------------------------
    // main
    // -----------------------------------------------------------------------

    public static void main(String[] args) {
        demonstrateRetention();
        demonstrateTarget();
    }
}
