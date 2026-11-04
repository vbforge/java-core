package com.vbforge.annotations;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CustomAnnotationsTasks {

    /**
     * Reads the {@code id} element of the {@code @Ticket} placed directly on a class.
     *
     * <p>Examples:
     * <pre>
     * ticketId(Annotated.class)  -> Optional.of("BUG-42")
     * ticketId(NoRole.class)     -> Optional.empty()      (class has no @Ticket)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code getAnnotation} returns {@code null} when the annotation is absent</li>
     *   <li>{@code Optional.ofNullable} / {@code Optional.empty} model "maybe a value"</li>
     * </ul>
     *
     * @param type the class to inspect
     * @return the ticket id, or an empty Optional when the class carries no {@code @Ticket}
     */
    public Optional<String> ticketId(Class<?> type) {
        throw new UnsupportedOperationException("TODO: implement ticketId");
    }

    /**
     * Reads the {@code labels} element of the {@code @Ticket} placed directly on a class.
     *
     * <p>Examples:
     * <pre>
     * ticketLabels(Annotated.class)  -> ["ui", "urgent"]
     * ticketLabels(NoLabels.class)   -> []     (@Ticket present, labels left at its default)
     * ticketLabels(NoRole.class)     -> []     (no @Ticket at all)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Array-valued elements come back as Java arrays</li>
     *   <li>{@code java.util.Arrays} bridges arrays and lists</li>
     * </ul>
     *
     * @param type the class to inspect
     * @return the labels in declaration order; empty list when there are none or no {@code @Ticket}
     */
    public List<String> ticketLabels(Class<?> type) {
        throw new UnsupportedOperationException("TODO: implement ticketLabels");
    }

    /**
     * Collects the values of all {@code @Role} annotations on a class, in the order they are written.
     * Must work for zero, one, and several {@code @Role} annotations.
     *
     * <p>Examples:
     * <pre>
     * roleNames(MultiRole.class)   -> ["admin", "auditor"]
     * roleNames(SingleRole.class)  -> ["viewer"]
     * roleNames(NoRole.class)      -> []
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code getAnnotation(Role.class)} gives {@code null} as soon as {@code @Role} is repeated</li>
     *   <li>There is a reflection method that "looks through" the container annotation</li>
     * </ul>
     *
     * @param type the class to inspect
     * @return role values in declaration order; empty list when there are none
     */
    public List<String> roleNames(Class<?> type) {
        throw new UnsupportedOperationException("TODO: implement roleNames");
    }

    /**
     * Lists the names of all fields <b>declared by</b> the class (any visibility) that carry the
     * given annotation, sorted alphabetically.
     *
     * <p>Examples:
     * <pre>
     * fieldsAnnotatedWith(Person.class, NotBlank.class)  -> ["name"]
     * fieldsAnnotatedWith(Person.class, Range.class)     -> ["age"]
     * fieldsAnnotatedWith(Person.class, Info.class)      -> []
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code getDeclaredFields()} includes private fields; {@code getFields()} does not</li>
     *   <li>The order of declared fields is not guaranteed — sort the result</li>
     * </ul>
     *
     * @param type       the class to inspect
     * @param annotation the annotation type to look for (must be RUNTIME-retained to be found)
     * @return alphabetically sorted field names; empty list when none match
     */
    public List<String> fieldsAnnotatedWith(Class<?> type, Class<? extends Annotation> annotation) {
        throw new UnsupportedOperationException("TODO: implement fieldsAnnotatedWith");
    }

    /**
     * Tells whether the annotation is written <b>directly</b> on the class, as opposed to merely
     * being inherited from a superclass through {@code @Inherited}.
     *
     * <p>Examples:
     * <pre>
     * isDeclaredDirectly(BaseService.class,  Audited.class)  -> true
     * isDeclaredDirectly(ChildService.class, Audited.class)  -> false  (inherited only)
     * isDeclaredDirectly(NoRole.class,       Audited.class)  -> false
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code isAnnotationPresent} follows {@code @Inherited}; its "declared" sibling does not</li>
     * </ul>
     *
     * @param type       the class to inspect
     * @param annotation the annotation type to look for
     * @return {@code true} only if the annotation is declared on {@code type} itself
     */
    public boolean isDeclaredDirectly(Class<?> type, Class<? extends Annotation> annotation) {
        throw new UnsupportedOperationException("TODO: implement isDeclaredDirectly");
    }

    /**
     * Describes every {@code @Range} constraint declared on the fields of a class (no instance needed).
     * Keys are field names, values have the form {@code "min..max"}. Iteration order must be
     * alphabetical by field name.
     *
     * <p>Examples:
     * <pre>
     * describeRanges(Person.class)    -> {age=18..65}
     * describeRanges(Scores.class)    -> {level=1..10, points=0..2147483647}   (points uses the defaults)
     * describeRanges(NoRole.class)    -> {}
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Annotation elements are read with ordinary method calls on the annotation object</li>
     *   <li>A map type that keeps its keys sorted saves you an explicit sort step</li>
     * </ul>
     *
     * @param type the class to inspect
     * @return field name to {@code "min..max"}, sorted by field name; empty map when no field has {@code @Range}
     */
    public Map<String, String> describeRanges(Class<?> type) {
        throw new UnsupportedOperationException("TODO: implement describeRanges");
    }
}
