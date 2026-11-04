package com.vbforge.annotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Built-in annotations: {@code @Override}, {@code @Deprecated}, {@code @SuppressWarnings},
 * {@code @FunctionalInterface} and {@code @SafeVarargs}.
 *
 * <p>All five are <b>instructions to the compiler</b>. They do not change what your code
 * does at runtime — they make the compiler check (or silence) something on your behalf.
 * Two of them ({@code @Override}, {@code @SuppressWarnings}) are even erased before the
 * class file is written, which is why you can never see them through reflection.
 */
public class BuiltInAnnotationsDemo {

    public static void main(String[] args) {
        overrideDemo();
        System.out.println("---");
        deprecatedDemo();
        System.out.println("---");
        suppressWarningsDemo();
        System.out.println("---");
        functionalInterfaceDemo();
        System.out.println("---");
        safeVarargsDemo();
    }

    // =========================================================================
    // 1. @Override
    // =========================================================================

    /*
     * What:   Declares "this method is meant to override or implement a supertype method".
     * Why:    Without it, a typo or a wrong parameter type silently creates a NEW method
     *         (an overload) and the supertype version keeps running. With it, the
     *         compiler refuses to compile unless a matching supertype method exists.
     * Cost:   Zero — retention is SOURCE, the annotation does not exist in the .class file.
     * Gotcha: equals(MyType) is an OVERLOAD of equals(Object). Collections call
     *         equals(Object), so they never see your overload. Adding @Override to
     *         the overload would have turned this silent bug into a compile error.
     */

    /** A plain supertype. */
    public static class Animal {
        public String sound() {
            return "...";
        }
    }

    /** Overrides {@link Animal#sound()} — the compiler verifies that a supertype method exists. */
    public static class Dog extends Animal {
        @Override
        public String sound() {
            return "Woof";
        }
    }

    /** Looks like an equals() implementation but is only an overload. Do NOT write this. */
    public static class BrokenPoint {
        private final int x;
        private final int y;

        public BrokenPoint(int x, int y) {
            this.x = x;
            this.y = y;
        }

        // Parameter type is BrokenPoint, not Object -> overload, not override.
        // Putting @Override here would fail to compile: "method does not override ...".
        public boolean equals(BrokenPoint other) {
            return other != null && x == other.x && y == other.y;
        }

        @Override
        public int hashCode() {
            return 31 * x + y;
        }
    }

    /** The correct version — @Override guarantees the signature really is equals(Object). */
    public static class FixedPoint {
        private final int x;
        private final int y;

        public FixedPoint(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof FixedPoint)) {
                return false;
            }
            FixedPoint other = (FixedPoint) o;
            return x == other.x && y == other.y;
        }

        @Override
        public int hashCode() {
            return 31 * x + y;
        }
    }

    /** {@code List.contains} calls {@code equals(Object)} — the overload is never used. */
    public static boolean brokenPointFoundInList() {
        List<BrokenPoint> points = new ArrayList<>(Collections.singletonList(new BrokenPoint(1, 2)));
        return points.contains(new BrokenPoint(1, 2));
    }

    /** Same lookup with a real override. */
    public static boolean fixedPointFoundInList() {
        List<FixedPoint> points = new ArrayList<>(Collections.singletonList(new FixedPoint(1, 2)));
        return points.contains(new FixedPoint(1, 2));
    }

    static void overrideDemo() {
        Animal animal = new Dog();
        System.out.println("Animal a = new Dog(); a.sound() -> " + animal.sound());

        BrokenPoint b1 = new BrokenPoint(1, 2);
        BrokenPoint b2 = new BrokenPoint(1, 2);
        System.out.println("BrokenPoint: b1.equals(b2) [static type BrokenPoint] -> " + b1.equals(b2));
        System.out.println("BrokenPoint: list.contains(equal point)             -> " + brokenPointFoundInList());
        System.out.println("FixedPoint:  list.contains(equal point)             -> " + fixedPointFoundInList());
    }

    // =========================================================================
    // 2. @Deprecated
    // =========================================================================

    /*
     * What:   Marks an element as "still works, but do not use it in new code".
     * Why:    Lets a library evolve without breaking callers: the old method keeps
     *         working (usually by delegating to the new one) while every caller gets
     *         a compiler warning pointing at the replacement.
     * Pair it with the Javadoc tag {@code @deprecated} to explain WHAT to use instead —
     *         the annotation alone only says "don't".
     * Retention: RUNTIME, so tools and frameworks can find deprecated members by reflection.
     * Gotcha: (Java 9+ adds since= and forRemoval= elements; on Java 8 the annotation
     *         has no elements — the "since" belongs in the Javadoc tag.)
     */
    public static class Calculator {

        /**
         * Adds two numbers.
         *
         * @param a first operand
         * @param b second operand
         * @return the sum
         */
        public int add(int a, int b) {
            return a + b;
        }

        /**
         * @deprecated use {@link #add(int, int)} instead
         */
        @Deprecated
        public int sum(int a, int b) {
            return add(a, b);
        }

        /**
         * Multiplies two numbers.
         *
         * @param a first operand
         * @param b second operand
         * @return the product
         */
        public int multiply(int a, int b) {
            return a * b;
        }

        /**
         * @deprecated use {@link #multiply(int, int)} instead
         */
        @Deprecated
        public int legacyMultiply(int a, int b) {
            return multiply(a, b);
        }
    }

    static void deprecatedDemo() {
        Calculator calc = new Calculator();
        System.out.println("calc.add(2, 3) -> " + calc.add(2, 3));
        System.out.println("calc.sum(2, 3) -> " + calc.sum(2, 3) + "   (works, but flagged as deprecated)");
        try {
            boolean flagged = Calculator.class.getMethod("sum", int.class, int.class)
                    .isAnnotationPresent(Deprecated.class);
            System.out.println("sum() carries @Deprecated at runtime -> " + flagged);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }

    // =========================================================================
    // 3. @SuppressWarnings
    // =========================================================================

    /*
     * What:   Silences named compiler warnings ("unchecked", "rawtypes", "deprecation", ...)
     *         inside the annotated element.
     * Why:    Sometimes you KNOW a cast is safe but the type system cannot prove it.
     *         Suppressing keeps the build output clean so that real warnings stay visible.
     * Rule:   Apply it to the SMALLEST scope possible (a local variable or one method,
     *         never a whole class) and add a comment explaining why it is safe.
     * Retention: SOURCE — gone after compilation, invisible to reflection.
     * Gotcha: Suppressing does not make the problem go away. If you were wrong, the
     *         failure moves from compile time to a ClassCastException somewhere far
     *         from the actual mistake (see pollutedRead()).
     */

    /** The cast is safe here: we only ever put Strings into the raw list. */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static List<String> uncheckedCast() {
        List raw = new ArrayList();
        raw.add("a");
        raw.add("b");
        return (List<String>) raw;
    }

    /** The cast is NOT safe: an Integer sneaks into a "List<String>" (heap pollution). */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static String pollutedRead() {
        List raw = new ArrayList();
        raw.add(42);
        List<String> strings = (List<String>) raw;
        return strings.get(0); // compiler-inserted cast to String fails here
    }

    static void suppressWarningsDemo() {
        System.out.println("uncheckedCast() -> " + uncheckedCast());
        try {
            pollutedRead();
        } catch (ClassCastException e) {
            System.out.println("pollutedRead() -> ClassCastException (suppressed warning hid a real bug)");
        }
    }

    // =========================================================================
    // 4. @FunctionalInterface
    // =========================================================================

    /*
     * What:   Asserts "this interface has exactly ONE abstract method", which is what
     *         makes it usable as a lambda / method-reference target.
     * Why:    Any interface with a single abstract method already works with lambdas —
     *         the annotation is a guard rail. If a teammate later adds a second abstract
     *         method, compilation of the interface fails instead of breaking every
     *         lambda that uses it.
     * What does NOT count as abstract: default methods, static methods, and methods
     *         that re-declare public methods of Object (Comparator re-declares equals,
     *         yet lambdas still work — see sortByLength()).
     * Retention: RUNTIME.
     * Gotcha: The annotation is optional. Leaving it off does not stop lambdas from
     *         working — it only removes the safety net.
     */
    @FunctionalInterface
    public interface Transformer {
        String apply(String input);

        // default and static methods do not count against the "one abstract method" rule
        default Transformer andThen(Transformer next) {
            return s -> next.apply(apply(s));
        }

        static Transformer identity() {
            return s -> s;
        }
    }

    /** Composes a method reference and a lambda through the default method. */
    public static String shout(String input) {
        Transformer upper = String::toUpperCase;
        Transformer exclaim = s -> s + "!";
        return upper.andThen(exclaim).apply(input);
    }

    /**
     * {@code java.util.Comparator} re-declares {@code boolean equals(Object)} AND has many
     * default/static methods, yet it is a functional interface: only {@code compare} counts.
     */
    public static List<String> sortByLength(List<String> words) {
        Comparator<String> byLength = (a, b) -> Integer.compare(a.length(), b.length());
        List<String> copy = new ArrayList<>(words);
        copy.sort(byLength);
        return copy;
    }

    static void functionalInterfaceDemo() {
        System.out.println("shout(\"hi\") -> " + shout("hi"));
        System.out.println("sortByLength([ccc, a, bb]) -> " + sortByLength(Arrays.asList("ccc", "a", "bb")));
        System.out.println("identity().apply(\"same\") -> " + Transformer.identity().apply("same"));
        System.out.println("Transformer has @FunctionalInterface at runtime -> "
                + Transformer.class.isAnnotationPresent(FunctionalInterface.class));
    }

    // =========================================================================
    // 5. @SafeVarargs
    // =========================================================================

    /*
     * What:   Tells the compiler "my generic varargs method does not misuse the array it
     *         receives", which removes the "unchecked generic array creation" warning
     *         at EVERY call site.
     * Why:    A generic varargs parameter (T...) is really a T[] — but arrays of
     *         non-reifiable types are checked at runtime and generics are erased, so
     *         the compiler warns. If you only read from the array, it is safe.
     * Allowed on: static methods, final methods and constructors (Java 9 adds private).
     * Retention: RUNTIME.
     * Gotcha: NEVER return the varargs array or expose it to other code. The array's
     *         runtime type is decided by the caller's static types; inside a generic
     *         method it is just Object[], so handing it back as T[] blows up later.
     */

    /** Safe: only reads from {@code items} and copies into a fresh list. */
    @SafeVarargs
    public static <T> List<T> listOf(T... items) {
        return Collections.unmodifiableList(new ArrayList<>(Arrays.asList(items)));
    }

    /** Leaks the varargs array — this is the pattern @SafeVarargs must never be put on. */
    @SuppressWarnings("unchecked")
    public static <T> T[] toArray(T... args) {
        return args;
    }

    /** Inside a generic method T is erased, so {@code toArray(a, b)} builds an Object[]. */
    @SuppressWarnings("unchecked")
    public static <T> T[] pair(T a, T b) {
        return toArray(a, b);
    }

    /** The caller expects a String[] but receives an Object[] -> ClassCastException. */
    public static String[] unsafeStringPair() {
        return pair("a", "b");
    }

    static void safeVarargsDemo() {
        System.out.println("listOf(\"x\", \"y\", \"z\") -> " + listOf("x", "y", "z"));
        try {
            unsafeStringPair();
        } catch (ClassCastException e) {
            System.out.println("unsafeStringPair() -> ClassCastException (leaked varargs array is an Object[])");
        }
    }
}
