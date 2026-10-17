package com.vbforge.functional_interfaces;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Demonstrates lambda expressions: syntax forms, target typing, variable capture,
 * the meaning of {@code this}, and deferred execution.
 */
public class LambdasDemo {

    // ── 1. Syntax forms ────────────────────────────────────────────────────────
    /*
     * WHAT:  (parameters) -> body
     *          ()         -> 42                  no parameters
     *          x          -> x * 2               one parameter, parentheses optional
     *          (int a, int b) -> a + b           explicit types (all or none)
     *          (a, b) -> { int s = a + b; return s; }   block body needs an explicit return
     * WHY:   Types are inferred from the target interface, so most lambdas stay one short line.
     * COMPLEXITY: none — a lambda is just a compact implementation of one method.
     * GOTCHA: Mixing typed and untyped parameters, "(int a, b) -> ...", does not compile, and a
     *         block body without "return" returns void — it will not fit a Function.
     */
    public static int applyBinary(BiFunction<Integer, Integer, Integer> operation, int a, int b) {
        return operation.apply(a, b);
    }

    static void syntaxDemo() {
        Supplier<Integer> noParams = () -> 42;
        Function<Integer, Integer> oneParam = x -> x * 2;
        BiFunction<Integer, Integer, Integer> typed = (Integer a, Integer b) -> a + b;
        BiFunction<Integer, Integer, Integer> block = (a, b) -> {
            int sum = a + b;
            return sum * sum;
        };
        System.out.println("no params:  " + noParams.get());
        System.out.println("one param:  " + oneParam.apply(21));
        System.out.println("typed:      " + typed.apply(2, 3));
        System.out.println("block body: " + block.apply(2, 3));
        System.out.println("passed as argument: " + applyBinary((a, b) -> a * b, 6, 7));
    }


    // ── 2. Target typing ───────────────────────────────────────────────────────
    /*
     * WHAT:  A lambda has no type of its own — the compiler gives it the type expected at that
     *        spot (assignment, argument, return). The same text can become different interfaces.
     * WHY:   That is why one lambda can be a Comparator here and a BinaryOperator there.
     * GOTCHA: Because the type comes from context, "var f = x -> x;" and "Object o = x -> x;"
     *        do not compile, and two overloads that take different functional interfaces with the
     *        same shape make a call ambiguous.
     */
    public static Comparator<String> byLength() {
        return (a, b) -> Integer.compare(a.length(), b.length());     // here it is a Comparator
    }

    public static BiFunction<Integer, Integer, Integer> larger() {
        return (a, b) -> a >= b ? a : b;                              // same shape, here a BiFunction
    }

    static void targetTypingDemo() {
        List<String> words = new ArrayList<>();
        words.add("pear");
        words.add("fig");
        words.add("banana");
        words.sort(byLength());
        System.out.println("sorted by length: " + words);
        System.out.println("larger(3, 9): " + larger().apply(3, 9));
        // Object notAllowed = () -> 42;    // ← compile error: Object is not a functional interface
    }


    // ── 3. Capturing variables ─────────────────────────────────────────────────
    /*
     * WHAT:  A lambda can read local variables of the enclosing method, but only if they are
     *        final or EFFECTIVELY final (never reassigned). Fields have no such restriction.
     * WHY:   The lambda may run later, on another thread, after the method has returned —
     *        it copies the value, so letting the original change would give two truths.
     * WORKAROUND: To count or accumulate, mutate an object the variable points to
     *        (AtomicInteger, array, List) — the reference stays unchanged.
     * GOTCHA: In a loop, capture a fresh effectively-final copy (or use the loop variable of a
     *        for-each); the classic "for (int i...)" variable cannot be captured directly.
     */
    public static List<Supplier<Integer>> tenfoldSuppliers(int count) {
        List<Supplier<Integer>> suppliers = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            final int captured = i;                      // a fresh copy per iteration
            suppliers.add(() -> captured * 10);          // using "i" here would not compile
        }
        return suppliers;
    }

    public static int countWithAtomic(List<String> items) {
        AtomicInteger counter = new AtomicInteger();     // the reference is effectively final
        items.forEach(item -> counter.incrementAndGet()); // the object behind it may change
        return counter.get();
    }

    static void captureDemo() {
        int base = 100;                                  // effectively final: never reassigned
        Function<Integer, Integer> addBase = x -> x + base;
        // base = 200;                                   // ← would break the lambda above (compile error)
        System.out.println("addBase(5): " + addBase.apply(5));
        for (Supplier<Integer> supplier : tenfoldSuppliers(3)) {
            System.out.println("supplier: " + supplier.get());
        }
        System.out.println("counted: " + countWithAtomic(java.util.Arrays.asList("a", "b", "c")));
    }


    // ── 4. "this" inside a lambda ──────────────────────────────────────────────
    /*
     * WHAT:  Inside a lambda, "this" is the ENCLOSING object. Inside an anonymous class,
     *        "this" is the anonymous object itself.
     * WHY:   A lambda is not a new scope or class — it is only a body of code; an anonymous
     *        class is a real object with its own fields and its own "this".
     * GOTCHA: Moving code between a lambda and an anonymous class silently changes the meaning
     *        of "this" (and of any shadowed field).
     */
    public static class Owner {
        private final String name = "owner";

        public String nameViaLambda() {
            Supplier<String> supplier = () -> this.name;               // this = the Owner
            return supplier.get();
        }

        public String nameViaAnonymousClass() {
            Supplier<String> supplier = new Supplier<String>() {
                private final String name = "anonymous";
                @Override
                public String get() {
                    return this.name;                                  // this = the anonymous object
                }
            };
            return supplier.get();
        }
    }

    static void thisDemo() {
        Owner owner = new Owner();
        System.out.println("lambda this:    " + owner.nameViaLambda());
        System.out.println("anonymous this: " + owner.nameViaAnonymousClass());
    }


    // ── 5. Deferred execution and closures ─────────────────────────────────────
    /*
     * WHAT:  Writing a lambda does not run its body. The body runs each time somebody calls the
     *        interface method. A lambda that remembers captured values is called a closure.
     * WHY:   It is the basis of laziness (Supplier), callbacks (Runnable), and factories that
     *        return configured behaviour (adder(5) → "add five").
     * GOTCHA: Side effects inside the body happen at CALL time, possibly many times, possibly never.
     */
    public static Function<Integer, Integer> adder(int amount) {
        return x -> x + amount;                          // closes over "amount"
    }

    /** Returns {runsBeforeCall, runsAfterTwoCalls}: the body does not run until it is invoked. */
    public static int[] deferredRuns() {
        AtomicInteger runs = new AtomicInteger();
        Runnable task = runs::incrementAndGet;           // nothing runs yet
        int before = runs.get();
        task.run();
        task.run();
        return new int[]{before, runs.get()};
    }

    static void deferredDemo() {
        Function<Integer, Integer> addFive = adder(5);
        System.out.println("adder(5)(10): " + addFive.apply(10));
        int[] runs = deferredRuns();
        System.out.println("runs before/after two calls: " + runs[0] + "/" + runs[1]);
    }


    // ── Main — runs every section ──────────────────────────────────────────────
    public static void main(String[] args) {
        syntaxDemo();
        System.out.println("---");
        targetTypingDemo();
        System.out.println("---");
        captureDemo();
        System.out.println("---");
        thisDemo();
        System.out.println("---");
        deferredDemo();
    }
}
