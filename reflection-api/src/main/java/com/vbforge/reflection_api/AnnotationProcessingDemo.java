package com.vbforge.reflection_api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Annotation processing at runtime — reading annotations through reflection and
 * acting on them.
 *
 * <p>The earlier {@code annotations} module showed how to <em>declare</em> annotations.
 * Here we <em>consume</em> them: the same technique JUnit ({@code @Test}), Jackson
 * ({@code @JsonProperty}), Spring ({@code @Autowired}) and JPA ({@code @Entity})
 * rely on. An annotation does nothing by itself — code like the validator and
 * dispatcher below gives it meaning.
 */
public class AnnotationProcessingDemo {

    // ── Annotation declarations ─────────────────────────────────────────

    /** Retention SOURCE: discarded by the compiler — never visible at runtime. */
    @Retention(RetentionPolicy.SOURCE)
    @Target(ElementType.TYPE)
    public @interface SourceOnly { }

    /** Retention CLASS (the default!): kept in the .class file, dropped by the JVM. */
    @Retention(RetentionPolicy.CLASS)
    @Target(ElementType.TYPE)
    public @interface ClassOnly { }

    /** Marks a class as persistent and names its table. */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Entity {
        String table();
    }

    /** Maps a field to a column; an empty value means "use the field name". */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Column {
        String value() default "";
    }

    /** Validation: a String field must contain text. */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface NotBlank {
        String message() default "must not be blank";
    }

    /** Validation: an int field must be within [min, max]. */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Range {
        int min() default 0;

        int max();
    }

    /** {@code @Inherited}: sub-classes of an annotated <em>class</em> see it too. */
    @Inherited
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Audited { }

    /** Marks a method as a named command. */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface Command {
        String value();
    }

    /** A repeatable label; the compiler wraps repeats into {@link Tags}. */
    @Repeatable(Tags.class)
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE, ElementType.METHOD})
    public @interface Tag {
        String value();
    }

    /** Container annotation required by {@code @Repeatable}. */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE, ElementType.METHOD})
    public @interface Tags {
        Tag[] value();
    }

    // ── Fixtures ────────────────────────────────────────────────────────

    /** Fully annotated sample class. */
    @SourceOnly
    @ClassOnly
    @Entity(table = "users")
    @Audited
    @Tag("core")
    @Tag("public")
    public static class User {
        @Column("user_name")
        @NotBlank
        private String name;

        @Range(min = 18, max = 120)
        private int age;

        private String email;

        public User(String name, int age, String email) {
            this.name = name;
            this.age = age;
            this.email = email;
        }

        @Command("greet")
        public String greet() {
            return "Hello, " + name;
        }

        @Command("birthday")
        public int birthday() {
            return ++age;
        }

        @Deprecated
        public String legacy() {
            return email;
        }

        public String plain() {
            return "plain";
        }
    }

    /** Sub-class of {@link User}: shows which annotations are inherited. */
    public static class Admin extends User {
        public Admin() {
            super("root", 40, "root@example.com");
        }
    }

    // ── Demo entry point ────────────────────────────────────────────────

    public static void main(String[] args) throws Exception {
        retentionDemo();
        System.out.println("---");
        readingDemo();
        System.out.println("---");
        inheritedDemo();
        System.out.println("---");
        repeatableDemo();
        System.out.println("---");
        validatorDemo();
        System.out.println("---");
        dispatcherDemo();
    }

    // ── 1. Retention decides visibility ─────────────────────────────────

    /**
     * <b>Only {@code RetentionPolicy.RUNTIME} annotations can be read by reflection.</b>
     *
     * <ul>
     *   <li>{@code SOURCE} — gone after compilation (e.g. {@code @Override})</li>
     *   <li>{@code CLASS} — in the .class file but not loaded by the JVM; <b>this is the
     *       default</b> when you write no {@code @Retention}</li>
     *   <li>{@code RUNTIME} — available through reflection</li>
     * </ul>
     *
     * <p><b>Why it matters:</b> the most common reason "my annotation is not found" is a
     * missing {@code @Retention(RUNTIME)}.
     *
     * <p><b>Gotcha:</b> {@code User} is written with six annotations (two of them {@code @Tag}),
     * but reflection sees only the runtime ones: {@code @Entity}, {@code @Audited}
     * and the {@code @Tags} container.
     */
    static void retentionDemo() {
        System.out.println("@SourceOnly present : " + User.class.isAnnotationPresent(SourceOnly.class));
        System.out.println("@ClassOnly present  : " + User.class.isAnnotationPresent(ClassOnly.class));
        System.out.println("@Entity present     : " + User.class.isAnnotationPresent(Entity.class));
        System.out.println("runtime annotations : " + User.class.getAnnotations().length);
    }

    // ── 2. Reading annotations ──────────────────────────────────────────

    /**
     * <b>Reading annotations from classes, fields and methods.</b>
     *
     * <p>Everything that can carry an annotation implements {@code AnnotatedElement}
     * ({@code Class}, {@code Field}, {@code Method}, {@code Constructor}, {@code Parameter}).
     * Its key methods:
     *
     * <ul>
     *   <li>{@code isAnnotationPresent(type)} — yes/no</li>
     *   <li>{@code getAnnotation(type)} — the instance, or {@code null}</li>
     *   <li>{@code getAnnotations()} — everything visible, <em>including inherited</em></li>
     *   <li>{@code getDeclaredAnnotations()} — only what is written on this element</li>
     * </ul>
     *
     * <p>Element values come from calling the annotation's methods
     * ({@code entity.table()}); omitted elements return their {@code default}.
     *
     * <p><b>Why it matters:</b> an annotation instance is a JDK dynamic proxy
     * (see {@code DynamicProxyDemo}) generated on the fly — that is how
     * an interface can have "implementations" you never wrote.
     *
     * <p><b>Gotcha:</b> {@code getAnnotation} returns {@code null} (not an exception)
     * when the annotation is absent — always null-check or use
     * {@code isAnnotationPresent}.
     */
    static void readingDemo() throws NoSuchFieldException, NoSuchMethodException {
        Entity entity = User.class.getAnnotation(Entity.class);
        System.out.println("@Entity.table        : " + entity.table());
        System.out.println("annotationType()     : " + entity.annotationType().getSimpleName());
        System.out.println("annotation is proxy  : " + Proxy.isProxyClass(entity.getClass()));

        Field name = User.class.getDeclaredField("name");
        System.out.println("@Column on name      : " + name.getAnnotation(Column.class).value());
        System.out.println("@NotBlank default msg: " + name.getAnnotation(NotBlank.class).message());

        Field email = User.class.getDeclaredField("email");
        System.out.println("@Column on email     : " + email.getAnnotation(Column.class));

        Method legacy = User.class.getMethod("legacy");
        System.out.println("legacy() @Deprecated : " + legacy.isAnnotationPresent(Deprecated.class));
    }

    // ── 3. @Inherited ───────────────────────────────────────────────────

    /**
     * <b>{@code @Inherited} works only on class-level annotations, and only down the
     * class hierarchy.</b>
     *
     * <p>{@code Admin extends User}. {@code @Audited} is meta-annotated with
     * {@code @Inherited}, so {@code Admin} sees it; {@code @Entity} is not, so
     * {@code Admin} does not — even though {@code Admin} <em>is</em> a {@code User}.
     *
     * <p><b>Why it matters:</b> a framework that scans for {@code @Entity} will silently
     * skip sub-classes unless the annotation is {@code @Inherited}.
     *
     * <p><b>Gotcha:</b> {@code @Inherited} has <em>no</em> effect on annotations placed on
     * methods, fields, or interfaces. Use {@code getDeclaredAnnotations()} to see only
     * what is written directly on the class.
     */
    static void inheritedDemo() {
        System.out.println("Admin @Audited (inherited)  : " + Admin.class.isAnnotationPresent(Audited.class));
        System.out.println("Admin @Entity (not inherited): " + Admin.class.isAnnotationPresent(Entity.class));
        System.out.println("Admin declared annotations  : " + Admin.class.getDeclaredAnnotations().length);
        System.out.println("Admin all annotations       : " + Admin.class.getAnnotations().length);
    }

    // ── 4. Repeatable annotations ───────────────────────────────────────

    /**
     * <b>Repeatable annotations (Java 8) are stored in a hidden container.</b>
     *
     * <p>Writing {@code @Tag("core") @Tag("public")} compiles to a single
     * {@code @Tags({@Tag("core"), @Tag("public")})}. Therefore:
     *
     * <ul>
     *   <li>{@code getAnnotation(Tag.class)} → {@code null} when repeated!</li>
     *   <li>{@code getAnnotation(Tags.class)} → the container</li>
     *   <li>{@code getAnnotationsByType(Tag.class)} → the right answer for 0, 1 or many</li>
     * </ul>
     *
     * <p><b>Why it matters:</b> code that worked with one {@code @Tag} silently breaks
     * the day someone adds a second.
     *
     * <p><b>Gotcha:</b> with exactly <em>one</em> {@code @Tag}, {@code getAnnotation(Tag.class)}
     * works and the container does not exist. Always prefer {@code getAnnotationsByType}.
     */
    static void repeatableDemo() {
        System.out.println("getAnnotation(Tag)        : " + User.class.getAnnotation(Tag.class));
        System.out.println("getAnnotation(Tags) values: " + User.class.getAnnotation(Tags.class).value().length);
        Tag[] tags = User.class.getAnnotationsByType(Tag.class);
        for (Tag tag : tags) {
            System.out.println("  tag: " + tag.value());
        }
    }

    // ── 5. Mini validator ───────────────────────────────────────────────

    /**
     * <b>Annotation processor #1 — a bean validator.</b>
     *
     * <p>Walks every declared field of the object's class, reads {@code @NotBlank} and
     * {@code @Range}, and collects human-readable violations sorted by message.
     * This is a 20-line version of what Hibernate Validator does.
     *
     * <p><b>Complexity:</b> O(f × a) — fields times annotations per field; the
     * reflective lookups dominate, so real frameworks cache them per class.
     *
     * <p><b>Gotcha:</b> the processor must check the <em>type</em> it is reading
     * ({@code String} vs {@code int}) — annotations are not type-checked against the
     * field's type unless you add that rule yourself.
     *
     * @param bean the object to validate
     * @return violation messages (empty when the bean is valid)
     */
    public static List<String> validate(Object bean) {
        List<String> violations = new ArrayList<String>();
        for (Field field : bean.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object value = field.get(bean);

                NotBlank notBlank = field.getAnnotation(NotBlank.class);
                if (notBlank != null && (value == null || ((String) value).trim().isEmpty())) {
                    violations.add(field.getName() + " " + notBlank.message());
                }

                Range range = field.getAnnotation(Range.class);
                if (range != null) {
                    int number = ((Integer) value).intValue();
                    if (number < range.min() || number > range.max()) {
                        violations.add(field.getName() + " must be between "
                                + range.min() + " and " + range.max());
                    }
                }
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
        }
        Collections.sort(violations);
        return violations;
    }

    static void validatorDemo() {
        System.out.println("valid user      : " + validate(new User("Ann", 30, null)));
        System.out.println("blank + too young: " + validate(new User(" ", 10, null)));
    }

    // ── 6. Mini command dispatcher ──────────────────────────────────────

    /**
     * <b>Annotation processor #2 — a command dispatcher.</b>
     *
     * <p>Finds the public method annotated {@code @Command("<command>")} and invokes it.
     * It uses {@code getMethods()}, so inherited commands work as well — {@code Admin}
     * answers to {@code "greet"} although the method lives in {@code User}.
     * This is the pattern behind JUnit's {@code @Test} discovery, CLI frameworks
     * and web-controller routing.
     *
     * <p><b>Gotcha:</b> the failure of the invoked method arrives wrapped in
     * {@code InvocationTargetException}; this dispatcher re-wraps the real cause.
     *
     * @param target  the object providing commands
     * @param command the command name
     * @return the method's result
     * @throws IllegalArgumentException if no method carries that command name
     */
    public static Object dispatch(Object target, String command) {
        for (Method method : target.getClass().getMethods()) {
            Command annotation = method.getAnnotation(Command.class);
            if (annotation != null && annotation.value().equals(command)) {
                try {
                    return method.invoke(target);
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException(e);
                } catch (InvocationTargetException e) {
                    throw new IllegalStateException(e.getCause());
                }
            }
        }
        throw new IllegalArgumentException("Unknown command: " + command);
    }

    static void dispatcherDemo() {
        User user = new User("Ann", 30, null);
        System.out.println("dispatch(greet)    : " + dispatch(user, "greet"));
        System.out.println("dispatch(birthday) : " + dispatch(user, "birthday"));
        System.out.println("Admin greet        : " + dispatch(new Admin(), "greet"));
        try {
            dispatch(user, "fly");
        } catch (IllegalArgumentException e) {
            System.out.println("dispatch(fly)      : " + e.getMessage());
        }
    }
}
