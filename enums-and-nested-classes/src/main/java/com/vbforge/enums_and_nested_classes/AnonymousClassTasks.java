package com.vbforge.enums_and_nested_classes;

import java.util.List;
import java.util.function.Supplier;

/**
 * 🎯 TASKS — Anonymous classes
 *
 * <p>Implement every method marked {@code TODO} so that the tests in
 * {@code AnonymousClassTasksTest} pass.
 *
 * <p>Rules:
 * <ul>
 *   <li>Do NOT change method signatures or the shape of the supporting class</li>
 *   <li>Replace the {@code throw new UnsupportedOperationException(...)} line with your code</li>
 *   <li>You may add private helpers if you need them</li>
 * </ul>
 */
public class AnonymousClassTasks {

    // ── Task 1 ─────────────────────────────────────────────────────────────────
    /**
     * Return a NEW list with the words sorted by their LAST character (ascending).
     * Words with the same last character are ordered by their natural
     * (alphabetical) order. The input list must not be modified.
     *
     * <p>Assume every word has at least one character.
     *
     * <p>Examples:
     * <pre>
     *   sortByLastChar(["apple", "kiwi", "banana", "fig"]) → ["banana", "apple", "fig", "kiwi"]
     *   sortByLastChar(["ab", "cb", "aa"])                 → ["aa", "ab", "cb"]
     *   sortByLastChar([])                                 → []
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code Collections.sort} / {@code List.sort} accept a {@code Comparator}</li>
     *   <li>Practise the anonymous-class form: {@code new Comparator<String>() { ... }}</li>
     *   <li>{@code Character.compare} compares two chars</li>
     * </ul>
     *
     * @param words the words to sort (never {@code null}, no empty strings)
     * @return a new, sorted list
     */
    public List<String> sortByLastChar(List<String> words) {
        throw new UnsupportedOperationException("TODO: implement sortByLastChar");
    }


    // ── Task 2 ─────────────────────────────────────────────────────────────────
    /**
     * Return a supplier that produces the Fibonacci sequence, one number per
     * {@code get()} call: 0, 1, 1, 2, 3, 5, 8, ...
     *
     * <p>Every call to {@code fibonacciSupplier()} must return an independent
     * supplier that starts again at 0. Only the first 40 numbers are tested
     * (they fit in an {@code int}).
     *
     * <p>Examples:
     * <pre>
     *   s = fibonacciSupplier();   s.get() → 0, 1, 1, 2, 3, 5
     *   t = fibonacciSupplier();   t.get() → 0   (independent of s)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>The supplier has to remember something between calls — where can an
     *       anonymous class keep that, when a captured local variable cannot change?</li>
     * </ul>
     *
     * @return a stateful supplier of Fibonacci numbers
     */
    public Supplier<Integer> fibonacciSupplier() {
        throw new UnsupportedOperationException("TODO: implement fibonacciSupplier");
    }


    // ── Task 3 ─────────────────────────────────────────────────────────────────
    /**
     * Return {@code count} suppliers; supplier number {@code k} (0-based)
     * always returns {@code factor * (k + 1)}, no matter when it is called.
     *
     * <p>Examples:
     * <pre>
     *   multiplesOf(3, 4) → suppliers returning 3, 6, 9, 12
     *   multiplesOf(5, 1) → one supplier returning 5
     *   multiplesOf(2, 0) → []
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Build the suppliers in a loop and call them AFTER the loop finished</li>
     *   <li>A loop counter that changes is not effectively final — the compiler will tell you</li>
     * </ul>
     *
     * @param factor the base number
     * @param count  how many suppliers to create (0 or more)
     * @return the suppliers, in order
     */
    public List<Supplier<Integer>> multiplesOf(int factor, int count) {
        throw new UnsupportedOperationException("TODO: implement multiplesOf");
    }


    // ── Task 4 ─────────────────────────────────────────────────────────────────
    /**
     * Sends messages over a named channel. {@code Notifier} is an abstract
     * CLASS (given) — a lambda cannot implement it.
     */
    public abstract static class Notifier {
        private final String channel;

        protected Notifier(String channel) {
            this.channel = channel;
        }

        public String channel() {
            return channel;
        }

        public abstract String format(String message);
    }

    /**
     * Create a {@link Notifier} for e-mail using an ANONYMOUS subclass
     * (the test checks that the returned object's class is anonymous).
     *
     * <p>Its channel is {@code "email"}; {@code format(message)} returns
     * {@code "[email:" + address + "] " + message}.
     *
     * <p>Examples:
     * <pre>
     *   n = emailNotifier("ann@example.com")
     *   n.channel()        → "email"
     *   n.format("hello")  → "[email:ann@example.com] hello"
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Arguments inside the parentheses of {@code new Notifier(...) { }} go to the superclass constructor</li>
     *   <li>The {@code address} parameter can be used inside the body because it is effectively final</li>
     * </ul>
     *
     * @param address the e-mail address to show in every formatted message
     * @return an anonymous {@code Notifier} subclass instance
     */
    public Notifier emailNotifier(String address) {
        throw new UnsupportedOperationException("TODO: implement emailNotifier");
    }
}
