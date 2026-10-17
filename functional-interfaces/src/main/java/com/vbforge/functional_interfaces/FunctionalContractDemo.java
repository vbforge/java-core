package com.vbforge.functional_interfaces;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.function.Function;

/**
 * Demonstrates what makes an interface "functional": exactly one abstract method,
 * the role of {@code @FunctionalInterface}, default/static methods, methods inherited
 * from {@code Object}, and generic functional interfaces.
 *
 * <p>The supporting interfaces ({@link TextOperation}, {@link Validator}, {@link Labeled},
 * {@link TriFunction}, {@link LegacyCallback}) are public nested types so that
 * {@link FunctionalContractTasks} and the tests can reuse them.
 */
public class FunctionalContractDemo {

    /**
     * Counts the abstract methods of an interface that COUNT for the functional contract:
     * public abstract methods that are not just re-declarations of {@code Object} methods.
     */
    public static long countAbstractMethods(Class<?> type) {
        long count = 0;
        for (Method method : type.getMethods()) {
            if (Modifier.isAbstract(method.getModifiers()) && !isObjectMethod(method)) {
                count++;
            }
        }
        return count;
    }

    private static boolean isObjectMethod(Method method) {
        try {
            Object.class.getMethod(method.getName(), method.getParameterTypes());
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }


    // ── 1. One abstract method = a functional interface ────────────────────────
    /*
     * WHAT:  A functional interface has EXACTLY ONE abstract method (its "function type").
     *        Any lambda or method reference whose shape matches that method can be assigned to it.
     * WHY:   Behaviour becomes a value you can pass, store and return — without writing a
     *        named class or an anonymous class every time (what Java did before 8).
     * ANNOTATION: @FunctionalInterface asks the COMPILER to verify the rule; adding a second
     *        abstract method later then breaks the build at the interface, not at some distant lambda.
     * GOTCHA: A lambda needs a target type. "Object o = () -> {};" does not compile
     *        because Object is not a functional interface.
     */
    @FunctionalInterface
    public interface TextOperation {
        String apply(String text);
    }

    static void singleAbstractMethodDemo() {
        TextOperation shout = text -> text.toUpperCase() + "!";     // lambda implements apply()
        TextOperation trim = String::trim;                          // method reference, same shape
        System.out.println("shout: " + shout.apply("hello"));
        System.out.println("trim:  [" + trim.apply("  padded  ") + "]");
        System.out.println("abstract methods of TextOperation: " + countAbstractMethods(TextOperation.class));
        // TextOperation twoArgs = (a, b) -> a + b;     // ← compile error: wrong number of parameters
    }


    // ── 2. Default and static methods do not count ─────────────────────────────
    /*
     * WHAT:  A functional interface may declare any number of default and static methods.
     * WHY:   They let you ship reusable behaviour right next to the abstract method —
     *        this is how Predicate.and/negate, Function.andThen and Comparator.reversed work.
     * TIP:   default methods return a NEW lambda that wraps the old one (composition),
     *        they do not mutate anything.
     * GOTCHA: A default method may call the abstract method, but a lambda can never override a
     *        default method — it only supplies the single abstract one.
     */
    @FunctionalInterface
    public interface Validator<T> {
        boolean validate(T value);

        default Validator<T> and(Validator<? super T> other) {
            return value -> validate(value) && other.validate(value);
        }

        default Validator<T> negate() {
            return value -> !validate(value);
        }

        static <T> Validator<T> alwaysValid() {
            return value -> true;
        }
    }

    static void defaultAndStaticDemo() {
        Validator<String> notEmpty = text -> !text.isEmpty();
        Validator<String> shortText = text -> text.length() <= 5;
        Validator<String> combined = notEmpty.and(shortText);
        System.out.println("'abc' valid? " + combined.validate("abc"));
        System.out.println("'' valid?    " + combined.validate(""));
        System.out.println("negated 'abc' valid? " + combined.negate().validate("abc"));
        System.out.println("alwaysValid: " + Validator.<String>alwaysValid().validate("anything"));
    }


    // ── 3. Methods from Object do not count ────────────────────────────────────
    /*
     * WHAT:  Re-declaring equals(Object), hashCode() or toString() as abstract in an interface
     *        does NOT add to the abstract-method count — every class already inherits them.
     * WHY:   This is why java.util.Comparator, which declares both compare(...) and equals(Object),
     *        is still a functional interface and works with lambdas.
     * GOTCHA: Only the exact Object signatures are exempt. An abstract equals(String) would count.
     */
    @FunctionalInterface
    public interface Labeled {
        String label();

        @Override
        boolean equals(Object other);   // exempt: same signature as Object.equals
    }

    static void objectMethodsDemo() {
        Labeled item = () -> "inventory-item";
        System.out.println("label: " + item.label());
        System.out.println("Labeled abstract methods:    " + countAbstractMethods(Labeled.class));
        System.out.println("Comparator abstract methods: " + countAbstractMethods(java.util.Comparator.class));
        System.out.println("List abstract methods:       " + countAbstractMethods(java.util.List.class) + " (not functional)");
    }


    // ── 4. Generic functional interfaces ───────────────────────────────────────
    /*
     * WHAT:  A functional interface can have type parameters, including more arguments than
     *        the JDK offers: TriFunction<A, B, C, R> fills the gap after BiFunction.
     * WHY:   The JDK stops at two arguments. When you need three, write your own — it is
     *        three lines — and still get lambdas, method references and default methods.
     * GOTCHA: Prefer the JDK interfaces (Function, Predicate, …) whenever one fits; a custom
     *        interface is only worth it when it adds meaning or an arity the JDK lacks.
     */
    @FunctionalInterface
    public interface TriFunction<A, B, C, R> {
        R apply(A a, B b, C c);

        default <V> TriFunction<A, B, C, V> andThen(Function<? super R, ? extends V> after) {
            return (a, b, c) -> after.apply(apply(a, b, c));
        }
    }

    static void genericInterfaceDemo() {
        TriFunction<Integer, Integer, Integer, Integer> volume = (w, h, d) -> w * h * d;
        TriFunction<Integer, Integer, Integer, String> described = volume.andThen(v -> v + " cm3");
        System.out.println("volume:    " + volume.apply(2, 3, 4));
        System.out.println("described: " + described.apply(2, 3, 4));
    }


    // ── 5. The annotation is optional, the rule is not ─────────────────────────
    /*
     * WHAT:  An interface with one abstract method is functional whether or not it carries
     *        @FunctionalInterface (Runnable existed long before the annotation).
     * WHY:   The annotation is a safety net and documentation, not a switch.
     * GOTCHA: Without it nothing stops a teammate from adding a second abstract method —
     *        and every lambda written against the interface stops compiling.
     */
    public interface LegacyCallback {
        void done(String result);
    }

    static void annotationDemo() {
        StringBuilder log = new StringBuilder();
        LegacyCallback callback = log::append;                       // works without the annotation
        callback.done("finished");
        System.out.println("callback received: " + log);
        System.out.println("TextOperation annotated?   " + TextOperation.class.isAnnotationPresent(FunctionalInterface.class));
        System.out.println("LegacyCallback annotated?  " + LegacyCallback.class.isAnnotationPresent(FunctionalInterface.class));
    }


    // ── Main — runs every section ──────────────────────────────────────────────
    public static void main(String[] args) {
        singleAbstractMethodDemo();
        System.out.println("---");
        defaultAndStaticDemo();
        System.out.println("---");
        objectMethodsDemo();
        System.out.println("---");
        genericInterfaceDemo();
        System.out.println("---");
        annotationDemo();
    }
}
