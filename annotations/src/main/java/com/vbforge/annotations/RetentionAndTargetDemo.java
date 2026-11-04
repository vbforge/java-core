package com.vbforge.annotations;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * The two meta-annotations that decide <i>who can see</i> an annotation ({@code @Retention})
 * and <i>where it may be written</i> ({@code @Target}), plus the other meta-annotations and
 * the Java 8 type annotations.
 *
 * <p>Rule of thumb: <b>if reflection returns nothing, check {@code @Retention} first.</b>
 */
public class RetentionAndTargetDemo {

    public static void main(String[] args) {
        retentionPoliciesDemo();
        System.out.println("---");
        targetDemo();
        System.out.println("---");
        metaAnnotationsDemo();
        System.out.println("---");
        typeAnnotationsDemo();
    }

    // =========================================================================
    // Annotation declarations used by the demo
    // =========================================================================

    @Retention(RetentionPolicy.SOURCE)
    public @interface SourceOnly {
    }

    @Retention(RetentionPolicy.CLASS)
    public @interface ClassOnly {
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface RuntimeVisible {
    }

    /** No @Retention at all: the default policy applies, which is CLASS. */
    public @interface NoRetention {
    }

    @SourceOnly
    @ClassOnly
    @RuntimeVisible
    @NoRetention
    public static class AllPolicies {
    }

    /** Only legal on methods. */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface MethodOnly {
    }

    /** Legal on several declaration kinds. */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD,
            ElementType.PARAMETER, ElementType.CONSTRUCTOR})
    public @interface Tag {
        String value();
    }

    /** No @Target: legal on every declaration (but not as a type annotation). */
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Anywhere {
    }

    /** Legal on local variables — and invisible to reflection no matter what the retention says. */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.LOCAL_VARIABLE)
    public @interface LocalOnly {
    }

    /** A class with a {@link Tag} at every kind of declaration the annotation allows. */
    @Tag("type")
    public static class Located {
        @Tag("field")
        public String field = "";

        @Tag("ctor")
        public Located() {
        }

        @Tag("method")
        @MethodOnly
        public void method(@Tag("param") String parameter, int untagged) {
        }
    }

    /** A meta-annotation: it can only be placed on other annotation types. */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.ANNOTATION_TYPE)
    public @interface Stereotype {
        String value();
    }

    /** A "composed" annotation: carries the meta-annotation {@link Stereotype}. */
    @Stereotype("service")
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Service {
    }

    @Service
    public static class OrderService {
    }

    /** Appears in generated Javadoc of every element it annotates. */
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Published {
    }

    /** A type annotation (Java 8): attaches to a TYPE that is used, not to a declaration. */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE_USE)
    public @interface NonNull {
    }

    /** A type-parameter annotation (Java 8): written next to {@code <T>}. */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE_PARAMETER)
    public @interface Unit {
    }

    public static class Registry {
        public @NonNull String lookup(@NonNull String key) {
            return key;
        }

        public List<@NonNull String> names() {
            return Collections.emptyList();
        }

        public void plain() {
        }
    }

    public static class Box<@Unit T> {
    }

    // =========================================================================
    // 1. @Retention — how long does the annotation live?
    // =========================================================================

    /*
     * What:   @Retention picks one of three lifetimes:
     *           SOURCE  - exists only in the .java file; javac throws it away.
     *                     Used by the compiler and annotation processors (@Override,
     *                     @SuppressWarnings, Lombok-style generators).
     *           CLASS   - written into the .class file but NOT loaded by the JVM.
     *                     Used by bytecode tools. This is the DEFAULT.
     *           RUNTIME - written into the .class file AND available to reflection.
     *                     Required for frameworks that read annotations while running
     *                     (JUnit, Jackson, Spring, your own validators).
     * Why:    Annotations that nobody needs at runtime cost nothing at runtime.
     *         Choose the shortest lifetime that still serves your reader.
     * Cost:   RUNTIME annotations live in the class file and are parsed lazily on the
     *         first reflective access, then cached — cheap, but not free.
     * Gotcha: Forgetting @Retention(RUNTIME) is the #1 custom-annotation bug. The code
     *         compiles, the annotation is "there" in the source, and getAnnotation(...)
     *         returns null. Without @Retention the policy is CLASS, not RUNTIME.
     */
    static void retentionPoliciesDemo() {
        System.out.println("annotations on AllPolicies visible to reflection: "
                + AllPolicies.class.getAnnotations().length + " (written: 4)");
        System.out.println("@SourceOnly visible?     " + AllPolicies.class.isAnnotationPresent(SourceOnly.class));
        System.out.println("@ClassOnly visible?      " + AllPolicies.class.isAnnotationPresent(ClassOnly.class));
        System.out.println("@NoRetention visible?    " + AllPolicies.class.isAnnotationPresent(NoRetention.class));
        System.out.println("@RuntimeVisible visible? " + AllPolicies.class.isAnnotationPresent(RuntimeVisible.class));

        System.out.println("@NoRetention declares @Retention itself? "
                + NoRetention.class.isAnnotationPresent(Retention.class) + "  (so the default, CLASS, applies)");
        System.out.println("@RuntimeVisible policy: " + RuntimeVisible.class.getAnnotation(Retention.class).value());
    }

    // =========================================================================
    // 2. @Target — where may the annotation be written?
    // =========================================================================

    /*
     * What:   @Target lists the ElementType locations where the compiler allows the
     *         annotation: TYPE, FIELD, METHOD, PARAMETER, CONSTRUCTOR, LOCAL_VARIABLE,
     *         ANNOTATION_TYPE, PACKAGE — and, since Java 8, TYPE_PARAMETER and TYPE_USE.
     * Why:    It turns misuse into a compile error: @MethodOnly on a field is rejected
     *         instead of being silently ignored by your framework.
     * Default: With no @Target, the annotation is allowed on every declaration (but not
     *         as a type annotation).
     * Reading: each location has its own reflective hook — Class, Field, Method,
     *         Constructor, and Method.getParameterAnnotations() for parameters.
     * Gotcha: - LOCAL_VARIABLE annotations are never stored in the class file in a form
     *           reflection can read: they work only for source-level tools.
     *         - getParameterAnnotations() returns one array PER PARAMETER, including
     *           empty arrays for parameters without annotations.
     */
    static void targetDemo() {
        System.out.println("@Tag targets:        " + Arrays.toString(Tag.class.getAnnotation(Target.class).value()));
        System.out.println("@MethodOnly targets: " + Arrays.toString(MethodOnly.class.getAnnotation(Target.class).value()));
        System.out.println("@Anywhere has @Target? " + Anywhere.class.isAnnotationPresent(Target.class));

        try {
            System.out.println("type:        " + Located.class.getAnnotation(Tag.class).value());
            System.out.println("field:       " + Located.class.getField("field").getAnnotation(Tag.class).value());
            System.out.println("constructor: " + Located.class.getConstructor().getAnnotation(Tag.class).value());

            Method method = Located.class.getMethod("method", String.class, int.class);
            System.out.println("method:      " + method.getAnnotation(Tag.class).value());
            Object[][] perParameter = method.getParameterAnnotations();
            System.out.println("parameters:  " + perParameter.length + " arrays; sizes = "
                    + perParameter[0].length + ", " + perParameter[1].length);
        } catch (NoSuchMethodException | NoSuchFieldException e) {
            throw new IllegalStateException(e);
        }

        @LocalOnly int scratch = 1; // legal at compile time, impossible to read at runtime
        System.out.println("local variable annotated with @LocalOnly: " + scratch);
    }

    // =========================================================================
    // 3. Meta-annotations — annotations on annotations
    // =========================================================================

    /*
     * What:   Annotations placed on annotation TYPES. The standard ones are @Retention,
     *         @Target, @Documented, @Inherited and @Repeatable. Because annotation types
     *         are classes too, you can write your own (@Stereotype here).
     * Why:    Composition. A framework can say "any annotation that is itself marked
     *         @Stereotype counts as a stereotype", and users then define their own
     *         shorthand annotations (@Service) without the framework knowing them.
     *         Spring's @Component / @Service / @Repository work exactly like this.
     * @Documented: only affects the Javadoc tool — annotated elements show the
     *         annotation in the generated documentation.
     * Gotcha: Meta-annotations are read with the same getAnnotation(...) API, just on
     *         the annotation's Class object — and they are only visible if THEY have
     *         RUNTIME retention (which is why Retention, Target and Documented do).
     */

    /** Finds the stereotype of a class by looking at the meta-annotations of its annotations. */
    public static Optional<String> stereotypeOf(Class<?> type) {
        for (Annotation annotation : type.getAnnotations()) {
            Stereotype stereotype = annotation.annotationType().getAnnotation(Stereotype.class);
            if (stereotype != null) {
                return Optional.of(stereotype.value());
            }
        }
        return Optional.empty();
    }

    static void metaAnnotationsDemo() {
        System.out.println("@Service meta-annotations: " + Service.class.getAnnotations().length
                + " (Stereotype, Retention, Target)");
        System.out.println("stereotypeOf(OrderService) -> " + stereotypeOf(OrderService.class));
        System.out.println("stereotypeOf(Registry)     -> " + stereotypeOf(Registry.class));
        System.out.println("@Published is @Documented? " + Published.class.isAnnotationPresent(Documented.class));
    }

    // =========================================================================
    // 4. Type annotations (Java 8): TYPE_USE and TYPE_PARAMETER
    // =========================================================================

    /*
     * What:   Since Java 8 an annotation can attach to a TYPE where it is used:
     *         List<@NonNull String>, (@NonNull String) cast, new @NonNull Object(),
     *         or to a type parameter declaration: class Box<@Unit T>.
     * Why:    Declaration annotations say "this FIELD is non-null"; type annotations
     *         can say "this list contains non-null Strings". Static analyzers
     *         (Checker Framework, IDE null-checkers) rely on them.
     * Reading: via AnnotatedType — Method.getAnnotatedReturnType(),
     *         getAnnotatedParameterTypes(), Field.getAnnotatedType() — or directly on a
     *         TypeVariable for type parameters.
     * Gotcha: An annotation whose @Target is ONLY TYPE_USE does NOT show up through the
     *         ordinary method.getAnnotation(...) or getParameterAnnotations() — those
     *         return declaration annotations. You must go through AnnotatedType.
     */
    static void typeAnnotationsDemo() {
        try {
            Method lookup = Registry.class.getMethod("lookup", String.class);
            System.out.println("method.getAnnotation(@NonNull)           -> " + lookup.getAnnotation(NonNull.class));
            System.out.println("annotated return type has @NonNull?      -> "
                    + lookup.getAnnotatedReturnType().isAnnotationPresent(NonNull.class));
            System.out.println("annotated 1st parameter has @NonNull?    -> "
                    + lookup.getAnnotatedParameterTypes()[0].isAnnotationPresent(NonNull.class));
            System.out.println("declaration annotations on 1st parameter -> "
                    + lookup.getParameterAnnotations()[0].length);

            AnnotatedParameterizedType names =
                    (AnnotatedParameterizedType) Registry.class.getMethod("names").getAnnotatedReturnType();
            AnnotatedType element = names.getAnnotatedActualTypeArguments()[0];
            System.out.println("List<@NonNull String>: type argument has @NonNull? -> "
                    + element.isAnnotationPresent(NonNull.class));
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }

        System.out.println("Box<@Unit T>: type parameter has @Unit? -> "
                + Box.class.getTypeParameters()[0].isAnnotationPresent(Unit.class));
    }
}
