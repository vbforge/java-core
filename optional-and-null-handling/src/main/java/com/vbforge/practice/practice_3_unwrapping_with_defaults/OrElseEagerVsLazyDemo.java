package com.vbforge.practice.practice_3_unwrapping_with_defaults;

import java.util.Optional;

/**
 * addition: makes the "orElse is eager, orElseGet is lazy" rule visible with a trace.
 *
 * orElse(expensiveDefault()) evaluates its argument BEFORE the call, even when the
 * value is present. orElseGet(() -> expensiveDefault()) only runs the supplier when empty.
 */
public class OrElseEagerVsLazyDemo {

    static int calls = 0;

    static String expensiveDefault() {
        calls++;
        System.out.println("    expensiveDefault() called (call #" + calls + ")");
        return "default";
    }

    public static void main(String[] args) {
        Optional<String> present = Optional.of("value");
        Optional<String> empty = Optional.empty();

        System.out.println("present.orElse(expensiveDefault()):");
        System.out.println("  -> " + present.orElse(expensiveDefault()));

        System.out.println("present.orElseGet(() -> expensiveDefault()):");
        System.out.println("  -> " + present.orElseGet(() -> expensiveDefault()));

        System.out.println("empty.orElseGet(() -> expensiveDefault()):");
        System.out.println("  -> " + empty.orElseGet(() -> expensiveDefault()));

        System.out.println("total calls: " + calls);
    }
}
