package com.vbforge.annotations;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;
import java.util.List;

public class RetentionAndTargetTasks {

    /**
     * Reports the retention policy of an annotation type, including the <i>default</i> that applies
     * when the annotation type does not declare {@code @Retention} at all.
     *
     * <p>Examples:
     * <pre>
     * retentionOf(RuntimeVisible.class)  -> RetentionPolicy.RUNTIME
     * retentionOf(SourceOnly.class)      -> RetentionPolicy.SOURCE
     * retentionOf(ClassOnly.class)       -> RetentionPolicy.CLASS
     * retentionOf(NoRetention.class)     -> RetentionPolicy.CLASS    (no @Retention -> default policy)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code @Retention} is itself a RUNTIME annotation on the annotation's {@code Class} object</li>
     *   <li>{@code getAnnotation} returns {@code null} when the meta-annotation is missing</li>
     * </ul>
     *
     * @param annotationType the annotation type to inspect
     * @return the effective retention policy, never {@code null}
     */
    public RetentionPolicy retentionOf(Class<? extends Annotation> annotationType) {
        throw new UnsupportedOperationException("TODO: implement retentionOf");
    }

    /**
     * Lists the {@code ElementType} names that an annotation type declares in its {@code @Target},
     * sorted alphabetically. An annotation type without {@code @Target} yields an empty list.
     *
     * <p>Examples:
     * <pre>
     * targetsOf(Tag.class)         -> ["CONSTRUCTOR", "FIELD", "METHOD", "PARAMETER", "TYPE"]
     * targetsOf(MethodOnly.class)  -> ["METHOD"]
     * targetsOf(Anywhere.class)    -> []      (no @Target declared)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code @Target.value()} is an array of {@code ElementType}</li>
     *   <li>{@code Enum.name()} gives the constant's name as a String</li>
     * </ul>
     *
     * @param annotationType the annotation type to inspect
     * @return sorted {@code ElementType} names; empty list when no {@code @Target} is declared
     */
    public List<String> targetsOf(Class<? extends Annotation> annotationType) {
        throw new UnsupportedOperationException("TODO: implement targetsOf");
    }

    /**
     * Decides whether an annotation type may be written at the given kind of location.
     * <ul>
     *   <li>If the annotation type declares {@code @Target}: {@code true} only when the location
     *       is listed there (do not model implicit rules such as TYPE covering ANNOTATION_TYPE).</li>
     *   <li>If it declares no {@code @Target}: {@code true} for every location except
     *       {@code ElementType.TYPE_USE}.</li>
     * </ul>
     *
     * <p>Examples:
     * <pre>
     * canApplyTo(MethodOnly.class, ElementType.METHOD)   -> true
     * canApplyTo(MethodOnly.class, ElementType.FIELD)    -> false
     * canApplyTo(Anywhere.class,   ElementType.FIELD)    -> true    (no @Target: any declaration)
     * canApplyTo(Anywhere.class,   ElementType.TYPE_USE) -> false   (never a type annotation)
     * canApplyTo(NonNull.class,    ElementType.TYPE_USE) -> true
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Handle the "no {@code @Target}" case first — {@code getAnnotation} returns {@code null} then</li>
     *   <li>{@code @Target.value()} is an array; {@code java.util.Arrays} can search it</li>
     * </ul>
     *
     * @param annotationType the annotation type to inspect
     * @param location       the kind of location to test
     * @return {@code true} if the annotation may appear at that location
     */
    public boolean canApplyTo(Class<? extends Annotation> annotationType, ElementType location) {
        throw new UnsupportedOperationException("TODO: implement canApplyTo");
    }

    /**
     * Lists the simple names of the annotations on a class that are visible to reflection at runtime,
     * sorted alphabetically. Annotations with SOURCE or CLASS retention must not appear.
     *
     * <p>Examples:
     * <pre>
     * runtimeVisibleAnnotationNames(AllPolicies.class)   -> ["RuntimeVisible"]
     *                                  (written: @SourceOnly @ClassOnly @RuntimeVisible @NoRetention)
     * runtimeVisibleAnnotationNames(OrderService.class)  -> ["Service"]
     * runtimeVisibleAnnotationNames(Registry.class)      -> []
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Reflection can only ever return what the JVM loaded — retention already did the filtering</li>
     *   <li>{@code annotationType().getSimpleName()} gives the name without package</li>
     * </ul>
     *
     * @param type the class to inspect
     * @return sorted simple names; empty list when no runtime annotation is present
     */
    public List<String> runtimeVisibleAnnotationNames(Class<?> type) {
        throw new UnsupportedOperationException("TODO: implement runtimeVisibleAnnotationNames");
    }

    /**
     * Counts the (declaration) annotations on every parameter of a method. The returned array has one
     * entry per parameter, in parameter order.
     *
     * <p>Examples:
     * <pre>
     * parameterAnnotationCounts(Located.method(String, int))    -> [1, 0]   (@Tag on the first only)
     * parameterAnnotationCounts(Registry.plain())               -> []       (no parameters)
     * parameterAnnotationCounts(Registry.lookup(String))        -> [0]      (@NonNull is a type annotation)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>One reflection call returns an array of annotation arrays — one per parameter</li>
     *   <li>Annotations whose {@code @Target} is only TYPE_USE are not "declaration annotations"</li>
     * </ul>
     *
     * @param method the method to inspect
     * @return annotation count per parameter; empty array for a method without parameters
     */
    public int[] parameterAnnotationCounts(Method method) {
        throw new UnsupportedOperationException("TODO: implement parameterAnnotationCounts");
    }

    /**
     * Lists the simple names of the <b>type annotations</b> placed on a method's return type, sorted
     * alphabetically. Declaration annotations on the method itself are not included.
     *
     * <p>Examples:
     * <pre>
     * returnTypeAnnotationNames(Registry.lookup(String))  -> ["NonNull"]   (public @NonNull String lookup(...))
     * returnTypeAnnotationNames(Registry.plain())         -> []
     * returnTypeAnnotationNames(Located.method(...))      -> []            (@Tag annotates the method, not its type)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Type annotations are read through {@code AnnotatedType}, not through the {@code Method} itself</li>
     *   <li>{@code AnnotatedType} is an {@code AnnotatedElement}: it has {@code getAnnotations()}</li>
     * </ul>
     *
     * @param method the method to inspect
     * @return sorted simple names; empty list when the return type carries no type annotation
     */
    public List<String> returnTypeAnnotationNames(Method method) {
        throw new UnsupportedOperationException("TODO: implement returnTypeAnnotationNames");
    }
}
