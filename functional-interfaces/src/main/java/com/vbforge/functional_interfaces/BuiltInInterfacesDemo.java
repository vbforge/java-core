package com.vbforge.functional_interfaces;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntBinaryOperator;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * Demonstrates the built-in functional interfaces of {@code java.util.function}:
 * {@code Predicate}, {@code Function}, {@code Consumer}, {@code Supplier}, their
 * specialisations, primitive variants and higher-order functions.
 */
public class BuiltInInterfacesDemo {

    // ── 1. Predicate<T>: T → boolean ───────────────────────────────────────────
    /*
     * WHAT:  boolean test(T). Default methods and(), or(), negate() build bigger conditions;
     *        Predicate.isEqual(x) is a ready-made "equals x" predicate.
     * WHY:   Filters, validations and rules become small named values you can combine.
     * COMPLEXITY: and()/or() short-circuit like && and ||: the second predicate may not run.
     * GOTCHA: Predicate.not(...) exists only since Java 11 — on Java 8 use negate().
     */
    public static Predicate<String> longWordStartingWithA() {
        Predicate<String> startsWithA = word -> word.startsWith("a");
        Predicate<String> longWord = word -> word.length() > 3;
        return startsWithA.and(longWord);
    }

    public static Predicate<String> notBlank() {
        Predicate<String> blank = word -> word.trim().isEmpty();
        return blank.negate();
    }

    static void predicateDemo() {
        Predicate<String> rule = longWordStartingWithA();
        System.out.println("apple: " + rule.test("apple") + ", ant: " + rule.test("ant") + ", pear: " + rule.test("pear"));
        System.out.println("notBlank('  '): " + notBlank().test("  "));
        System.out.println("isEqual: " + Predicate.isEqual("x").test("x"));
        System.out.println("or: " + rule.or(word -> word.equals("pear")).test("pear"));
    }


    // ── 2. Function<T, R>, UnaryOperator<T>, BinaryOperator<T> ─────────────────
    /*
     * WHAT:  R apply(T). andThen(g) means "first this, then g"; compose(g) means "first g, then this".
     *        UnaryOperator<T> is Function<T, T>; BinaryOperator<T> is BiFunction<T, T, T>.
     *        Function.identity() returns its argument.
     * WHY:   Transformations chain into pipelines without temporary variables.
     * GOTCHA: andThen and compose differ ONLY in order, and mixing them up gives wrong results
     *        without any error.
     */
    /** Returns {composeResult, andThenResult} for f = x + 1 and g = x * 2. */
    public static int[] composeVsAndThen(int x) {
        Function<Integer, Integer> plusOne = value -> value + 1;
        Function<Integer, Integer> timesTwo = value -> value * 2;
        int composed = plusOne.compose(timesTwo).apply(x);     // plusOne(timesTwo(x))
        int chained = plusOne.andThen(timesTwo).apply(x);      // timesTwo(plusOne(x))
        return new int[]{composed, chained};
    }

    static void functionDemo() {
        int[] results = composeVsAndThen(5);
        System.out.println("compose: " + results[0] + ", andThen: " + results[1]);
        UnaryOperator<String> shout = text -> text.toUpperCase() + "!";
        BinaryOperator<Integer> max = BinaryOperator.maxBy(Integer::compare);
        System.out.println("unary: " + shout.apply("hi") + ", maxBy: " + max.apply(3, 8));
        System.out.println("identity: " + Function.<String>identity().apply("same"));
    }


    // ── 3. Consumer<T> and Supplier<T> ─────────────────────────────────────────
    /*
     * WHAT:  Consumer: void accept(T) — does something with a value (side effect).
     *        Supplier: T get() — produces a value on demand, takes nothing.
     * WHY:   Consumer.andThen chains side effects in order; Supplier delays work until needed
     *        (an expensive default is computed only when actually required).
     * GOTCHA: Passing a ready value where a Supplier is expected is EAGER; passing a lambda is LAZY.
     *        orElse(compute()) always computes; orElseGet(() -> compute()) only when needed.
     */
    public static List<String> consumerChain(String input) {
        List<String> sink = new ArrayList<>();
        Consumer<String> addUpper = text -> sink.add(text.toUpperCase());
        Consumer<String> addLength = text -> sink.add(String.valueOf(text.length()));
        addUpper.andThen(addLength).accept(input);
        return sink;
    }

    public static <T> T valueOrElseGet(T value, Supplier<? extends T> fallback) {
        return value != null ? value : fallback.get();
    }

    /** Counts how many times the fallback supplier ran. */
    public static int fallbackCalls(String value) {
        AtomicInteger calls = new AtomicInteger();
        valueOrElseGet(value, () -> {
            calls.incrementAndGet();
            return "default";
        });
        return calls.get();
    }

    static void consumerSupplierDemo() {
        System.out.println("consumer chain: " + consumerChain("abc"));
        System.out.println("fallback calls for 'x': " + fallbackCalls("x"));
        System.out.println("fallback calls for null: " + fallbackCalls(null));
    }


    // ── 4. Primitive specialisations ───────────────────────────────────────────
    /*
     * WHAT:  IntPredicate, IntFunction, ToIntFunction, IntBinaryOperator, IntUnaryOperator …
     *        — variants that work with int/long/double instead of boxed Integer/Long/Double.
     * WHY:   Function<Integer, Integer> boxes and unboxes on every call; IntUnaryOperator does not.
     *        In hot loops over large arrays this removes object allocation.
     * GOTCHA: The names differ per arity and result: IntPredicate (test), IntBinaryOperator
     *        (applyAsInt), ToIntFunction (applyAsInt) — each has its own method name.
     */
    public static int reduceInts(int[] values, int identity, IntBinaryOperator operator) {
        int result = identity;
        for (int value : values) {
            result = operator.applyAsInt(result, value);
        }
        return result;
    }

    public static int countMatching(int[] values, IntPredicate predicate) {
        int count = 0;
        for (int value : values) {
            if (predicate.test(value)) {
                count++;
            }
        }
        return count;
    }

    static void primitiveDemo() {
        int[] numbers = {3, 8, 1, 6};
        System.out.println("sum: " + reduceInts(numbers, 0, (a, b) -> a + b));
        System.out.println("max: " + reduceInts(numbers, Integer.MIN_VALUE, Math::max));
        System.out.println("even count: " + countMatching(numbers, n -> n % 2 == 0));
    }


    // ── 5. Higher-order functions ──────────────────────────────────────────────
    /*
     * WHAT:  A method that takes a function as a parameter, returns one, or both.
     * WHY:   The "what to do" is passed in, the loop stays in one place — this is the
     *        building block behind Stream.filter/map and the Strategy pattern.
     * GOTCHA: Use ? super T for parameters you only feed (Predicate/Function inputs) and
     *        ? extends R for results you only read, so callers can pass more general functions.
     */
    public static <T> T applyTwice(UnaryOperator<T> operator, T start) {
        return operator.apply(operator.apply(start));
    }

    public static <T> List<T> filter(List<T> items, Predicate<? super T> predicate) {
        List<T> result = new ArrayList<>();
        for (T item : items) {
            if (predicate.test(item)) {
                result.add(item);
            }
        }
        return result;
    }

    static void higherOrderDemo() {
        System.out.println("applyTwice: " + applyTwice(x -> x * 3, 2));
        List<String> words = new ArrayList<>();
        words.add("kiwi");
        words.add("fig");
        words.add("plum");
        System.out.println("filtered: " + filter(words, word -> word.length() == 4));
    }


    // ── Main — runs every section ──────────────────────────────────────────────
    public static void main(String[] args) {
        predicateDemo();
        System.out.println("---");
        functionDemo();
        System.out.println("---");
        consumerSupplierDemo();
        System.out.println("---");
        primitiveDemo();
        System.out.println("---");
        higherOrderDemo();
    }
}
