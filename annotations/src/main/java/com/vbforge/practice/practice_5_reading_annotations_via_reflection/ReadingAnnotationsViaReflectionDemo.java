package com.vbforge.practice.practice_5_reading_annotations_via_reflection;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.Arrays;

/**
 * Demonstrates Java Annotations — metadata attached to program elements
 * that can be read at compile-time (by tools/compilers) or at runtime
 * (via reflection).
 *
 * What is an annotation?
 *   An annotation is a special interface whose instances (markers) are
 *   attached to classes, methods, fields, parameters, constructors, etc.
 *   They carry no executable logic by themselves — they are pure metadata.
 *
 * Key concepts:
 *
 *   @Retention — when the annotation is visible:
 *     SOURCE   → discarded by compiler; only for tools (e.g. @Override)
 *     CLASS    → stored in .class file but NOT loaded by JVM (default)
 *     RUNTIME  → loaded by JVM; accessible via reflection ← most useful
 *
 *   @Target — which program elements can be annotated:
 *     TYPE, FIELD, METHOD, PARAMETER, CONSTRUCTOR,
 *     LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE, MODULE, RECORD_COMPONENT
 *
 *   @Documented — include in Javadoc output
 *   @Inherited  — subclasses inherit the annotation from the superclass
 *   @Repeatable — the same annotation can appear multiple times on one element
 *
 * How to read annotations at runtime:
 *   element.getAnnotation(MyAnnotation.class)     — returns null if absent
 *   element.isAnnotationPresent(MyAnnotation.class)
 *   element.getAnnotations()                      — all annotations
 *   element.getDeclaredAnnotations()              — excl. inherited
 */
public class ReadingAnnotationsViaReflectionDemo {

    // -----------------------------------------------------------------------
    // 1. Inspecting built-in / standard library annotations
    // -----------------------------------------------------------------------

    public static void demonstrateBuiltInAnnotations() {
        System.out.println("=== 1. Built-in Annotations ===\n");

        Class<AnnotatedService> clazz = AnnotatedService.class;

        // @Deprecated on the class itself
        Deprecated deprecated = clazz.getAnnotation(Deprecated.class);
        System.out.println("@Deprecated on class  : " + (deprecated != null));
        if (deprecated != null) {
            System.out.println("  since   = " + deprecated.since());
            System.out.println("  forRemoval = " + deprecated.forRemoval());
        }

        // @SuppressWarnings is SOURCE retention — not visible at runtime
        SuppressWarnings sw = clazz.getAnnotation(SuppressWarnings.class);
        System.out.println("@SuppressWarnings (SOURCE retention) : " + sw); // null expected

        System.out.println();
    }

    // -----------------------------------------------------------------------
    // 2. Inspecting custom annotations on methods
    // -----------------------------------------------------------------------

    public static void demonstrateMethodAnnotations() throws NoSuchMethodException {
        System.out.println("=== 2. Custom Annotations on Methods ===\n");

        Class<AnnotatedService> clazz = AnnotatedService.class;

        // getDeclaredMethods() returns methods in no guaranteed order -> sort by name for stable output
        Method[] methods = clazz.getDeclaredMethods();
        Arrays.sort(methods, java.util.Comparator.comparing(Method::getName));

        for (Method m : methods) {
            System.out.println("Method: " + m.getName());

            // Check for @Audit annotation
            if (m.isAnnotationPresent(Audit.class)) {
                Audit audit = m.getAnnotation(Audit.class);
                System.out.println("  @Audit found:");
                System.out.println("    action  = " + audit.action());
                System.out.println("    level   = " + audit.level());
                System.out.println("    enabled = " + audit.enabled());
            }

            // Check for @Retry annotation
            if (m.isAnnotationPresent(Retry.class)) {
                Retry retry = m.getAnnotation(Retry.class);
                System.out.println("  @Retry found:");
                System.out.println("    maxAttempts = " + retry.maxAttempts());
                System.out.println("    delayMs     = " + retry.delayMs());
            }

            System.out.println();
        }
    }

    // -----------------------------------------------------------------------
    // 3. Inspecting annotations on fields
    // -----------------------------------------------------------------------

    public static void demonstrateFieldAnnotations() {
        System.out.println("=== 3. Annotations on Fields ===\n");

        for (Field f : AnnotatedService.class.getDeclaredFields()) {
            System.out.println("Field: " + f.getName());
            Annotation[] annotations = f.getDeclaredAnnotations();
            if (annotations.length == 0) {
                System.out.println("  (no annotations)");
            } else {
                Arrays.stream(annotations)
                      .forEach(a -> System.out.println("  " + a));
            }
            System.out.println();
        }
    }

    // -----------------------------------------------------------------------
    // 4. Inspecting annotations on parameters
    // -----------------------------------------------------------------------

    public static void demonstrateParameterAnnotations() throws NoSuchMethodException {
        System.out.println("=== 4. Annotations on Parameters ===\n");

        Method method = AnnotatedService.class
            .getDeclaredMethod("processRequest", String.class, int.class);

        Parameter[] params = method.getParameters();
        for (Parameter p : params) {
            // without the -parameters compiler flag the real names are not kept: arg0, arg1
            System.out.println("Parameter: " + p.getName() + " (" + p.getType().getSimpleName() + ")");
            for (Annotation a : p.getDeclaredAnnotations()) {
                System.out.println("  annotation: " + a);
            }
            System.out.println();
        }
    }

    // -----------------------------------------------------------------------
    // 5. Listing ALL annotations on a class
    // -----------------------------------------------------------------------

    public static void demonstrateAllAnnotations() {
        System.out.println("=== 5. All Annotations on AnnotatedService ===\n");

        for (Annotation a : AnnotatedService.class.getDeclaredAnnotations()) {
            System.out.println("  " + a.annotationType().getSimpleName() + " → " + a);
        }

        System.out.println();
    }

    // -----------------------------------------------------------------------
    // main
    // -----------------------------------------------------------------------

    public static void main(String[] args) throws Exception {
        demonstrateBuiltInAnnotations();
        demonstrateMethodAnnotations();
        demonstrateFieldAnnotations();
        demonstrateParameterAnnotations();
        demonstrateAllAnnotations();
    }
}