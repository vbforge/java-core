package com.vbforge.generics_fundamentals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Demonstrates generic methods: declaring {@code <T>}, explicit type arguments,
 * type inference, generic varargs and generic methods inside generic classes.
 */
public class GenericMethodsDemo {

    // ── 1. Declaring a generic method ──────────────────────────────────────────
    /*
     * WHAT:  A method with its own type parameter list, written BEFORE the return type:
     *            public static <T> T identity(T value)
     * WHY:   One method body works for any type while the compiler still tracks the exact
     *        type flowing in and out — no Object return type, no casts at the call site.
     *        The class itself does not have to be generic.
     * RULE:  Type parameters belong to the METHOD, so static methods can be generic
     *        (a static method cannot use its class's T).
     * GOTCHA: Type arguments are always reference types — List<int> does not compile,
     *         use List<Integer> (autoboxing does the conversion).
     */
    public static <T> T identity(T value) {
        return value;
    }

    /** T is inferred from BOTH arguments, so the default must fit the list's element type. */
    public static <T> T firstOrDefault(List<T> list, T defaultValue) {
        return list.isEmpty() ? defaultValue : list.get(0);
    }

    /** Reverses the array in place. Works for String[], Integer[], any reference-type array. */
    public static <T> void reverseInPlace(T[] array) {
        for (int left = 0, right = array.length - 1; left < right; left++, right--) {
            T tmp = array[left];
            array[left] = array[right];
            array[right] = tmp;
        }
    }

    static void declaringDemo() {
        String s = identity("hello");               // T = String, no cast
        Integer n = identity(42);                   // T = Integer
        System.out.println("identity: " + s + ", " + n);

        System.out.println("firstOrDefault: " + firstOrDefault(Arrays.asList("x", "y"), "none"));
        System.out.println("firstOrDefault (empty): " + firstOrDefault(Collections.<String>emptyList(), "none"));

        Integer[] numbers = {1, 2, 3};              // must be Integer[] — int[] is not a T[]
        reverseInPlace(numbers);
        System.out.println("reversed: " + Arrays.toString(numbers));
    }


    // ── 2. Type inference and explicit type arguments ──────────────────────────
    /*
     * WHAT:  The compiler normally infers T from the arguments and, since Java 8, also from
     *        the TARGET type (what the result is assigned to or passed into).
     * WHY:   You almost never have to write type arguments by hand.
     * WHEN TO WRITE THEM: when inference has nothing to work with or picks the wrong type.
     *        Syntax: Class.<Type>method(...), e.g. Collections.<String>emptyList().
     * GOTCHA: With mixed arguments the inferred T is the closest common supertype, which is
     *         often not what you meant (listOf(1, 2.5) is a list of "Number & Comparable<…>").
     *         An explicit witness makes the intent obvious.
     */
    @SafeVarargs
    public static <T> List<T> listOf(T... items) {
        return Arrays.asList(items);
    }

    static void inferenceDemo() {
        List<String> inferred = listOf("a", "b");               // T = String from the arguments
        List<Object> byTarget = listOf("a", 1);                 // T = Object from the TARGET type
        List<Number> explicit = GenericMethodsDemo.<Number>listOf(1, 2.5);   // explicit type witness
        List<String> empty = Collections.emptyList();           // nothing to infer from but the target

        System.out.println("inferred: " + inferred);
        System.out.println("by target type: " + byTarget);
        System.out.println("explicit: " + explicit);
        System.out.println("empty: " + empty);
    }


    // ── 3. Generic varargs and heap pollution ──────────────────────────────────
    /*
     * WHAT:  A varargs parameter T... is really a T[] created by the CALLER.
     * WHY IT IS RISKY: arrays are reified (they know their runtime type) but T is erased, so
     *        inside a generic method the array is created as Object[]. Handing that array
     *        back out as T[] can blow up far from the cause.
     * @SafeVarargs: promise from the author that the method only READS the array
     *        (does not store it or expose it). Allowed on static, final and private methods
     *        (and constructors) — see listOf above.
     * GOTCHA: A method that returns its varargs array as T[] is NOT safe.
     */
    @SafeVarargs
    private static <T> T[] arrayOf(T... items) {
        return items;                                // T[] is really Object[] when called from generic code
    }

    @SuppressWarnings("unchecked")
    private static <T> T[] twoOf(T a, T b) {
        return arrayOf(a, b);                        // creates Object[], NOT String[], even for Strings
    }

    /** Looks harmless, but throws ClassCastException when the Object[] meets the String[] type. */
    public static String[] twoStrings() {
        return twoOf("a", "b");                      // compiler-inserted cast to String[] fails here
    }

    static void varargsDemo() {
        System.out.println("safe varargs read: " + listOf(1, 2, 3));
        try {
            twoStrings();
        } catch (ClassCastException e) {
            System.out.println("returning T[] from varargs: ClassCastException");
        }
    }


    // ── 4. Generic methods inside generic classes ──────────────────────────────
    /*
     * WHAT:  A generic class can declare methods with ADDITIONAL type parameters.
     * WHY:   map() must change the element type (Wrapper<T> → Wrapper<R>); the class's T is
     *        fixed for the lifetime of the object, so the method needs its own R.
     * GOTCHA: Never reuse the class's name for the method's parameter (<T> inside Wrapper<T>):
     *         it SHADOWS the class's T and compiles into something very confusing.
     */
    public static class Wrapper<T> {
        private final T value;

        public Wrapper(T value) {
            this.value = value;
        }

        public T get() {
            return value;
        }

        /** R is chosen per call: Wrapper<String>.map(String::length) gives a Wrapper<Integer>. */
        public <R> Wrapper<R> map(Function<T, R> mapper) {
            return new Wrapper<>(mapper.apply(value));
        }

        /** U is unrelated to T, so wrappers of different types can be compared. */
        public <U> boolean sameValueAs(Wrapper<U> other) {
            return Objects.equals(value, other.value);
        }
    }

    static void genericMethodInGenericClassDemo() {
        Wrapper<String> text = new Wrapper<>("generics");
        Wrapper<Integer> length = text.map(String::length);     // Wrapper<String> → Wrapper<Integer>
        System.out.println("mapped: " + length.get());
        System.out.println("same value as Wrapper<Integer>(8)? " + length.sameValueAs(new Wrapper<>(8)));
    }


    // ── Main — runs every section ──────────────────────────────────────────────
    public static void main(String[] args) {
        declaringDemo();
        System.out.println("---");
        inferenceDemo();
        System.out.println("---");
        varargsDemo();
        System.out.println("---");
        genericMethodInGenericClassDemo();
    }
}
