package com.vbforge.annotations;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Writing your own annotations.
 *
 * <p>An annotation by itself does <b>nothing</b>. It is a typed, structured label that
 * some other code (the compiler, a build tool, or a framework using reflection) reads
 * and acts on. This demo covers the declaration syntax and the Java 8 reading API
 * ({@code getAnnotation}, {@code getAnnotationsByType}); section 6 builds a tiny
 * validator to show the "label + reader" pairing end to end.
 *
 * <p>Every custom annotation here uses {@code @Retention(RUNTIME)} — otherwise reflection
 * could not see it. The full story of retention and target is in {@link RetentionAndTargetDemo}.
 */
public class CustomAnnotationsDemo {

    public static void main(String[] args) {
        declaringAnnotationsDemo();
        System.out.println("---");
        singleValueShorthandDemo();
        System.out.println("---");
        readingAnnotationsDemo();
        System.out.println("---");
        repeatableDemo();
        System.out.println("---");
        inheritedDemo();
        System.out.println("---");
        annotationProcessingDemo();
        System.out.println("---");
        annotationsAreInterfacesDemo();
    }

    // =========================================================================
    // Annotation declarations used by the demo
    // =========================================================================

    public enum Priority { LOW, MEDIUM, HIGH }

    /** Marker annotation: no elements, the mere presence is the information. */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Marker {
    }

    /** Single-element annotation: the element is called {@code value}, so the name may be omitted. */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
    public @interface Info {
        String value();
    }

    /** Multi-element annotation with required and defaulted elements of several allowed types. */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE, ElementType.METHOD})
    public @interface Ticket {
        String id();                                  // required: no default

        Priority priority() default Priority.MEDIUM;  // enum

        String[] labels() default {};                 // array

        int estimateHours() default 1;                // primitive

        Class<?> owner() default Void.class;          // Class (Void = "nobody")
    }

    /** Repeatable annotation: may be written several times on the same element. */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @Repeatable(Roles.class)
    public @interface Role {
        String value();
    }

    /** Container for {@link Role}: javac wraps repeated roles in this automatically. */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Roles {
        Role[] value();
    }

    /** Inherited annotation: a subclass "has" it when its superclass does. */
    @Inherited
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Audited {
    }

    /** Validation annotation: the String field must contain a non-whitespace character. */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface NotBlank {
    }

    /** Validation annotation: the int field must lie in [min, max]. */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Range {
        int min() default 0;

        int max() default Integer.MAX_VALUE;
    }

    // =========================================================================
    // Annotated sample classes
    // =========================================================================

    @Marker
    @Info("Demo class")
    @Ticket(id = "BUG-42", priority = Priority.HIGH, labels = {"ui", "urgent"}, estimateHours = 5)
    public static class Annotated {

        @Info("a field")
        public String name = "demo";

        @Info("a method")
        @Ticket(id = "T-1") // every other element takes its default
        public void work() {
        }

        @Ticket(id = "T-2", labels = "solo") // single-element array: braces may be dropped
        public void solo() {
        }

        public void plain() {
        }
    }

    @Role("admin")
    @Role("auditor")
    public static class MultiRole {
    }

    @Role("viewer")
    public static class SingleRole {
    }

    public static class NoRole {
    }

    @Audited
    public static class BaseService {
    }

    public static class ChildService extends BaseService {
    }

    @Audited
    public interface AuditedContract {
    }

    public static class ContractImpl implements AuditedContract {
    }

    public static class Person {
        @NotBlank
        private final String name;

        @Range(min = 18, max = 65)
        private final int age;

        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }
    }

    // =========================================================================
    // 1. Declaring annotations
    // =========================================================================

    /*
     * What:   {@code @interface} declares an annotation type. Its "methods" are elements.
     * Rules:  Element types are limited to primitives, String, Class, enums, other
     *         annotations, and one-dimensional arrays of those. null is not allowed,
     *         so "absent" is modelled with a default such as {} or Void.class.
     * Why:    Structured metadata beats naming conventions ("methods starting with
     *         handle...") and beats XML kept in a separate file: the compiler checks
     *         the element names, types and required values for you.
     * Gotcha: An element with no default is REQUIRED at every use site. Adding a new
     *         element without a default later breaks every existing usage — give new
     *         elements a default to stay source-compatible.
     */
    static void declaringAnnotationsDemo() {
        Marker marker = Annotated.class.getAnnotation(Marker.class);
        System.out.println("@Marker present: " + (marker != null));

        Ticket ticket = Annotated.class.getAnnotation(Ticket.class);
        System.out.println("class ticket -> id=" + ticket.id()
                + " priority=" + ticket.priority()
                + " labels=" + Arrays.toString(ticket.labels())
                + " hours=" + ticket.estimateHours());

        try {
            Ticket defaults = Annotated.class.getMethod("work").getAnnotation(Ticket.class);
            System.out.println("method ticket (defaults) -> id=" + defaults.id()
                    + " priority=" + defaults.priority()
                    + " labels=" + Arrays.toString(defaults.labels())
                    + " owner=" + defaults.owner().getSimpleName());
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }

    // =========================================================================
    // 2. Single-value shorthand
    // =========================================================================

    /*
     * What:   If an annotation has an element named {@code value} (and no other
     *         element without a default), you may write @Info("text") instead of
     *         @Info(value = "text").
     * Why:    It is why @SuppressWarnings("unchecked") and @Ticket-like one-liners
     *         read so naturally. Name your main element "value" whenever there is one.
     * Gotcha: The shorthand works only for an element literally named "value".
     *         As soon as you need to set a second element you must name both:
     *         @Info(value = "x", other = 1).
     */
    static void singleValueShorthandDemo() {
        System.out.println("class @Info -> " + Annotated.class.getAnnotation(Info.class).value());
        try {
            System.out.println("field @Info -> "
                    + Annotated.class.getField("name").getAnnotation(Info.class).value());
            System.out.println("method @Info -> "
                    + Annotated.class.getMethod("work").getAnnotation(Info.class).value());
        } catch (NoSuchMethodException | NoSuchFieldException e) {
            throw new IllegalStateException(e);
        }
    }

    // =========================================================================
    // 3. Reading annotations (reflection API basics)
    // =========================================================================

    /*
     * What:   Every reflective element (Class, Method, Field, Constructor, Parameter)
     *         implements AnnotatedElement:
     *           - isAnnotationPresent(X.class)   -> boolean
     *           - getAnnotation(X.class)         -> X or null (never throws if absent)
     *           - getAnnotations()               -> everything, including inherited ones
     *           - getDeclaredAnnotations()       -> only those written on THIS element
     *           - getAnnotationsByType(X.class)  -> array, works for repeatable annotations
     * Cost:   O(number of annotations on the element); the JVM parses and caches them.
     * Gotcha: getAnnotation returns null when the annotation is missing — always check
     *         or use isAnnotationPresent first. And it can only return annotations with
     *         RUNTIME retention.
     */
    static void readingAnnotationsDemo() {
        System.out.println("class has @Ticket? " + Annotated.class.isAnnotationPresent(Ticket.class));
        System.out.println("class annotation count: " + Annotated.class.getAnnotations().length);

        Method[] methods = Annotated.class.getDeclaredMethods();
        Arrays.sort(methods, Comparator.comparing(Method::getName));
        for (Method m : methods) {
            System.out.println("method " + m.getName() + "() has @Ticket? " + m.isAnnotationPresent(Ticket.class));
        }
    }

    // =========================================================================
    // 4. @Repeatable
    // =========================================================================

    /*
     * What:   @Repeatable(Container.class) lets the same annotation appear several times
     *         on one element. Behind the scenes javac wraps the repeats in ONE container
     *         annotation (Roles) holding an array.
     * Why:    Before Java 8 you had to write @Roles({@Role("a"), @Role("b")}) by hand.
     * Gotcha: When an annotation is repeated, the element does not carry @Role directly —
     *         it carries @Roles. So getAnnotation(Role.class) returns null for a
     *         repeated annotation but returns the annotation for a single use.
     *         getAnnotationsByType(Role.class) handles both cases — prefer it.
     */
    static void repeatableDemo() {
        System.out.println("MultiRole  getAnnotation(Role)        -> " + MultiRole.class.getAnnotation(Role.class));
        System.out.println("MultiRole  getAnnotationsByType(Role)  -> " + roleValues(MultiRole.class));
        System.out.println("SingleRole getAnnotationsByType(Role)  -> " + roleValues(SingleRole.class));
        System.out.println("NoRole     getAnnotationsByType(Role)  -> " + roleValues(NoRole.class));
        System.out.println("MultiRole has container @Roles? " + MultiRole.class.isAnnotationPresent(Roles.class));
    }

    private static List<String> roleValues(Class<?> type) {
        List<String> values = new ArrayList<>();
        for (Role role : type.getAnnotationsByType(Role.class)) {
            values.add(role.value());
        }
        return values;
    }

    // =========================================================================
    // 5. @Inherited
    // =========================================================================

    /*
     * What:   A meta-annotation. If annotation X is meta-annotated with @Inherited and
     *         is placed on a class, then subclasses automatically "have" X when queried
     *         with getAnnotation / isAnnotationPresent.
     * Why:    Framework markers ("this hierarchy is audited / transactional") should
     *         not have to be repeated on every subclass.
     * Gotcha: - Only works for CLASS inheritance. It is NOT inherited from interfaces,
     *           and not from overridden methods, fields or constructors.
     *         - getDeclaredAnnotation(s) ignores inheritance: it shows only what is
     *           written directly on that class.
     */
    static void inheritedDemo() {
        System.out.println("ChildService  isAnnotationPresent(@Audited)     -> "
                + ChildService.class.isAnnotationPresent(Audited.class));
        System.out.println("ChildService  getDeclaredAnnotation(@Audited)   -> "
                + ChildService.class.getDeclaredAnnotation(Audited.class));
        System.out.println("ContractImpl  isAnnotationPresent(@Audited)     -> "
                + ContractImpl.class.isAnnotationPresent(Audited.class) + "  (interfaces are not inherited)");
    }

    // =========================================================================
    // 6. Annotation processing at runtime — a mini validator
    // =========================================================================

    /*
     * What:   The standard framework pattern: annotations declare the rules, a generic
     *         piece of code reads them reflectively and enforces them. Bean Validation
     *         (@NotNull, @Size), JUnit (@Test), Jackson (@JsonProperty) and Spring
     *         (@Component) all work like this.
     * Why:    The rules live next to the data they describe, and the enforcement code
     *         is written once and works for every class.
     * Cost:   Reflection is slower than direct calls (field lookup + access checks per
     *         call). Real frameworks cache the discovered metadata per class.
     * Gotcha: Private fields need setAccessible(true) before they can be read.
     *         Sort the fields: getDeclaredFields() order is not guaranteed by the spec.
     */
    public static List<String> validate(Object target) {
        List<String> errors = new ArrayList<>();
        Field[] fields = target.getClass().getDeclaredFields();
        Arrays.sort(fields, Comparator.comparing(Field::getName));

        for (Field field : fields) {
            field.setAccessible(true);
            try {
                if (field.isAnnotationPresent(NotBlank.class)) {
                    Object value = field.get(target);
                    if (value == null || value.toString().trim().isEmpty()) {
                        errors.add(field.getName() + ": must not be blank");
                    }
                }
                Range range = field.getAnnotation(Range.class);
                if (range != null) {
                    int value = field.getInt(target);
                    if (value < range.min() || value > range.max()) {
                        errors.add(field.getName() + ": must be between " + range.min()
                                + " and " + range.max() + " but was " + value);
                    }
                }
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Cannot read field " + field.getName(), e);
            }
        }
        return errors;
    }

    static void annotationProcessingDemo() {
        System.out.println("valid person   -> " + validate(new Person("Ann", 30)));
        System.out.println("invalid person -> " + validate(new Person("  ", 70)));
    }

    // =========================================================================
    // 7. Annotations are interfaces
    // =========================================================================

    /*
     * What:   {@code @interface} compiles to an interface that extends
     *         java.lang.annotation.Annotation. Each element is an abstract method,
     *         and the JVM supplies the implementation (a generated proxy) when you
     *         call getAnnotation(...).
     * Why:    It explains the API you see: ticket.id() is a normal method call,
     *         ticket.annotationType() gives the interface back, and two annotation
     *         instances with equal element values are equals().
     * Defaults: are stored on the element method and available through
     *         Method.getDefaultValue() — handy for tools that print documentation.
     * Gotcha: You cannot extend an annotation interface or write your own
     *         implementation of one in a useful way — annotations cannot have
     *         superclasses and cannot be generic.
     */
    static void annotationsAreInterfacesDemo() {
        Ticket ticket = Annotated.class.getAnnotation(Ticket.class);
        Annotation asAnnotation = ticket;
        System.out.println("Ticket.class.isAnnotation()        -> " + Ticket.class.isAnnotation());
        System.out.println("Ticket.class.isInterface()         -> " + Ticket.class.isInterface());
        System.out.println("ticket.annotationType()            -> " + asAnnotation.annotationType().getSimpleName());
        try {
            System.out.println("default of priority()              -> "
                    + Ticket.class.getMethod("priority").getDefaultValue());
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }
}
