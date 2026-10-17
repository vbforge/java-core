package com.vbforge.functional_interfaces;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;

/**
 * Demonstrates the four kinds of method references: static, bound instance, unbound
 * instance and constructor references — and how each maps to an equivalent lambda.
 */
public class MethodReferencesDemo {

    /** Small class used by the constructor-reference section and by the tasks. */
    public static class Person {
        private final String name;

        public Person(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Person)) return false;
            return Objects.equals(name, ((Person) o).name);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(name);
        }

        @Override
        public String toString() {
            return "Person(" + name + ")";
        }
    }

    /** Applies {@code function} to every element and returns the results as a new list. */
    public static <T, R> List<R> mapAll(List<T> input, Function<? super T, ? extends R> function) {
        List<R> result = new ArrayList<>();
        for (T item : input) {
            result.add(function.apply(item));
        }
        return result;
    }


    // ── 1. Static method reference: ClassName::staticMethod ────────────────────
    /*
     * WHAT:  A reference to a static method. Same as a lambda that passes its arguments straight on:
     *            Integer::parseInt   ==   s -> Integer.parseInt(s)
     * WHY:   It names the existing method instead of repeating its parameters — shorter and
     *        often clearer when the lambda only delegates.
     * GOTCHA: If the lambda does anything else (extra argument, conversion, condition),
     *        keep the lambda — a method reference cannot add logic.
     */
    public static boolean isEven(int number) {
        return number % 2 == 0;
    }

    public static List<Integer> parseAll(List<String> numbers) {
        return mapAll(numbers, Integer::parseInt);
    }

    static void staticReferenceDemo() {
        System.out.println("parsed: " + parseAll(Arrays.asList("1", "22", "-3")));
        Function<Integer, Boolean> check = MethodReferencesDemo::isEven;       // int is autoboxed
        System.out.println("isEven(4): " + check.apply(4));
    }


    // ── 2. Bound instance reference: object::method ────────────────────────────
    /*
     * WHAT:  A reference to a method of ONE SPECIFIC object that already exists.
     *            prefix::concat   ==   s -> prefix.concat(s)
     * WHY:   The object is captured once, at the moment the reference is created.
     *        System.out::println is the famous example.
     * GOTCHA: The receiver is evaluated immediately. If "prefix" is null, creating the
     *        reference throws NullPointerException right away, not when it is called.
     */
    public static List<String> greetAll(String prefix, List<String> names) {
        return mapAll(names, prefix::concat);
    }

    static void boundReferenceDemo() {
        System.out.println("greetings: " + greetAll("Hello, ", Arrays.asList("Ann", "Bob")));
        List<String> sink = new ArrayList<>();
        Function<String, Boolean> adder = sink::add;                           // bound to this exact list
        adder.apply("first");
        System.out.println("sink: " + sink);
    }


    // ── 3. Unbound instance reference: ClassName::instanceMethod ───────────────
    /*
     * WHAT:  A reference to an instance method where the FIRST lambda argument becomes the receiver.
     *            String::toUpperCase        ==   s -> s.toUpperCase()
     *            String::compareToIgnoreCase ==  (a, b) -> a.compareToIgnoreCase(b)
     * WHY:   Works with any object of that class, supplied later — ideal for transformations
     *        and comparators over collections.
     * GOTCHA: It looks identical to the static form. "Integer::toString" is ambiguous for
     *        Function<Integer, String> because both the static toString(int) and the instance
     *        toString() fit — the compiler rejects it; write a lambda instead.
     */
    public static List<String> upperAll(List<String> words) {
        return mapAll(words, String::toUpperCase);
    }

    public static List<String> sortIgnoringCase(List<String> words) {
        List<String> copy = new ArrayList<>(words);
        Collections.sort(copy, String::compareToIgnoreCase);                   // (a, b) -> a.compareToIgnoreCase(b)
        return copy;
    }

    static void unboundReferenceDemo() {
        System.out.println("upper: " + upperAll(Arrays.asList("a", "bc")));
        System.out.println("sorted ignoring case: " + sortIgnoringCase(Arrays.asList("b", "A", "c")));
        // Function<Integer, String> ambiguous = Integer::toString;   // ← compile error: ambiguous reference
    }


    // ── 4. Constructor reference: ClassName::new ───────────────────────────────
    /*
     * WHAT:  A reference to a constructor. The target interface decides WHICH constructor:
     *            Person::new      as Function<String, Person>      → Person(String)
     *            ArrayList::new   as Supplier<List<String>>        → ArrayList()
     *            int[]::new       as IntFunction<int[]>            → new int[n]
     * WHY:   Factories become values: pass "how to create one" to a method.
     * GOTCHA: If no constructor matches the target's parameter list, it does not compile.
     */
    public static List<Person> toPersons(List<String> names) {
        return mapAll(names, Person::new);
    }

    public static int[] newIntArray(int length) {
        IntFunction<int[]> factory = int[]::new;
        return factory.apply(length);
    }

    static void constructorReferenceDemo() {
        System.out.println("persons: " + toPersons(Arrays.asList("Ann", "Bob")));
        Supplier<List<String>> listFactory = ArrayList::new;
        List<String> fresh = listFactory.get();
        System.out.println("fresh list: " + fresh);
        System.out.println("array length: " + newIntArray(4).length);
    }


    // ── 5. Lambda or method reference? ─────────────────────────────────────────
    /*
     * WHAT:  Every method reference can be rewritten as a lambda; the reverse is not true.
     * WHY:   Prefer the reference when the lambda just forwards its parameters
     *        ("s -> s.trim()" → "String::trim"); prefer the lambda when it adds logic
     *        or when the reference would be ambiguous or unclear.
     * GOTCHA: Readability decides. A long qualified name such as
     *        com.example.Helper::process is not shorter than "x -> Helper.process(x)".
     */
    static void equivalenceDemo() {
        Function<String, Integer> asReference = Integer::parseInt;
        Function<String, Integer> asLambda = s -> Integer.parseInt(s);
        System.out.println("same result: " + asReference.apply("7").equals(asLambda.apply("7")));
    }


    // ── Main — runs every section ──────────────────────────────────────────────
    public static void main(String[] args) {
        staticReferenceDemo();
        System.out.println("---");
        boundReferenceDemo();
        System.out.println("---");
        unboundReferenceDemo();
        System.out.println("---");
        constructorReferenceDemo();
        System.out.println("---");
        equivalenceDemo();
    }
}
