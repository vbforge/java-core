package com.vbforge.reflection_api;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.List;
import java.util.Map;

/**
 * Tasks for runtime annotation processing.
 *
 * <p>Read {@link AnnotationProcessingDemo} first — the annotations
 * ({@code @Entity}, {@code @Column}, {@code @Tag}, …) and the fixtures
 * ({@code User}, {@code Admin}) used in the examples are declared there.
 */
public class AnnotationProcessingTasks {

    /**
     * Return the simple names of all annotations <b>visible at runtime</b> on
     * {@code element} (inherited ones included), sorted alphabetically.
     *
     * <p>Examples:
     * <pre>
     *   annotationNames(User.class)                → ["Audited", "Entity", "Tags"]
     *   annotationNames(Admin.class)               → ["Audited"]        (only @Inherited ones)
     *   annotationNames(User.class.getMethod("plain")) → []
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code AnnotatedElement} is the common super-type of {@code Class}, {@code Field}, {@code Method}</li>
     *   <li>{@code annotationType()} gives you the annotation's interface</li>
     *   <li>Do not expect to see {@code @Tag} twice — look at the repeatable demo</li>
     * </ul>
     *
     * @param element a class, field, method, …
     * @return alphabetically sorted simple names of the runtime annotations
     */
    public List<String> annotationNames(AnnotatedElement element) {
        throw new UnsupportedOperationException("TODO: implement annotationNames");
    }

    /**
     * Return the names of the fields <b>declared directly on {@code type}</b>
     * (any visibility) that carry the given annotation, sorted alphabetically.
     *
     * <p>Examples:
     * <pre>
     *   fieldsWithAnnotation(User.class, NotBlank.class)  → ["name"]
     *   fieldsWithAnnotation(User.class, Range.class)     → ["age"]
     *   fieldsWithAnnotation(User.class, Entity.class)    → []     (Entity targets types, not fields)
     *   fieldsWithAnnotation(Admin.class, NotBlank.class) → []     (the field is declared on User)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code Field} is an {@code AnnotatedElement}: ask it whether the annotation is present</li>
     *   <li>The annotation type parameter is {@code Class<? extends Annotation>}</li>
     * </ul>
     *
     * @param type       the class whose declared fields are inspected
     * @param annotation the annotation type to look for
     * @return sorted names of matching fields, never {@code null}
     */
    public List<String> fieldsWithAnnotation(Class<?> type, Class<? extends Annotation> annotation) {
        throw new UnsupportedOperationException("TODO: implement fieldsWithAnnotation");
    }

    /**
     * Build a {@code fieldName → columnName} mapping for a persistent class.
     * Consider every <b>non-static</b> field declared on {@code type}. The column name is the
     * value of {@code @Column} when it is present and non-empty, otherwise the field name itself.
     * The result keeps the entries sorted by field name.
     *
     * <p>Examples:
     * <pre>
     *   columnMapping(User.class)  → {age=age, email=email, name=user_name}
     *       (name has @Column("user_name"); age and email have no @Column)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>A {@code TreeMap} keeps keys sorted</li>
     *   <li>{@code getAnnotation} returns {@code null} when the annotation is absent</li>
     *   <li>{@code Modifier.isStatic} filters out static fields</li>
     * </ul>
     *
     * @param type the class to map
     * @return sorted map from field name to column name
     */
    public Map<String, String> columnMapping(Class<?> type) {
        throw new UnsupportedOperationException("TODO: implement columnMapping");
    }

    /**
     * Return the values of every {@code @Tag} on {@code element}, in declaration
     * order — whether there are none, one, or many.
     *
     * <p>Examples:
     * <pre>
     *   tagValues(User.class)                       → ["core", "public"]   (repeated → hidden container)
     *   tagValues(classWithSingleTag)               → ["solo"]
     *   tagValues(Admin.class)                      → []                   (@Tag is not @Inherited)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>One {@code AnnotatedElement} method understands both the single and the
     *       container form of a repeatable annotation</li>
     *   <li>{@code getAnnotation(Tag.class)} is the trap — see the repeatable demo</li>
     * </ul>
     *
     * @param element the class or method to inspect
     * @return the tag values, never {@code null}
     */
    public List<String> tagValues(AnnotatedElement element) {
        throw new UnsupportedOperationException("TODO: implement tagValues");
    }
}
