package com.vbforge.optional_and_null_handling;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * Demonstrates how to GET A VALUE OUT of an {@link Optional}:
 * {@code orElse}, {@code orElseGet}, {@code orElseThrow}, {@code ifPresent}.
 *
 * <p>Why it matters: the whole point of Optional is to force you to decide what
 * happens when the value is missing. These methods are the possible decisions:
 * use a default, compute a default, fail loudly, or do nothing.
 *
 * <p>Several demos take an {@link AtomicInteger} "calls" counter. It records how
 * many times the fallback was evaluated, which makes the eager-vs-lazy
 * difference between {@code orElse} and {@code orElseGet} measurable.
 */
public class OptionalUnwrappingDemo {

    // ── 1. orElse(default) ────────────────────────────────────────────────────
    /**
     * Returns the value, or the given default when empty.
     *
     * <p><b>Gotcha — the argument is EAGER.</b> Java evaluates method arguments
     * before the call, so {@code expensiveDefault(calls)} runs EVERY time —
     * even when the Optional is present and the default is thrown away.
     * Fine for constants and variables; wasteful (or harmful) for anything that
     * does real work or has side effects.
     */
    public static String orElseDemo(Optional<String> opt, AtomicInteger calls) {
        return opt.orElse(expensiveDefault(calls));
    }


    // ── 2. orElseGet(supplier) ────────────────────────────────────────────────
    /**
     * Returns the value, or the result of the {@link Supplier} when empty.
     * The lambda is only invoked when needed — LAZY.
     *
     * <p>Rule of thumb: constant or already-computed default → {@code orElse};
     * database call, object creation, logging, anything with a cost → {@code orElseGet}.
     */
    public static String orElseGetDemo(Optional<String> opt, AtomicInteger calls) {
        return opt.orElseGet(() -> expensiveDefault(calls));
    }

    /** Stand-in for "something costly": counts how often it runs. */
    private static String expensiveDefault(AtomicInteger calls) {
        calls.incrementAndGet();
        return "default";
    }


    // ── 3. orElseThrow(supplier) ──────────────────────────────────────────────
    /**
     * Use when absence is a BUG or a business error, not a normal case.
     * The exception is created lazily (inside the supplier), so no stack trace is
     * built when the value is present.
     *
     * <p>Note: the no-argument {@code orElseThrow()} only exists since Java 10 —
     * on Java 8 you must pass a supplier.
     */
    public static String orElseThrowDemo(Optional<String> opt, String message) {
        return opt.orElseThrow(() -> new IllegalArgumentException(message));
    }

    /** A constructor reference works as the supplier when no message is needed. */
    public static String orElseThrowRefDemo(Optional<String> opt) {
        return opt.orElseThrow(IllegalStateException::new);
    }


    // ── 4. orElse(null) — back to the null world ──────────────────────────────
    /**
     * <b>Gotcha:</b> {@code orElse(null)} compiles and works, but it hands the
     * absent value back as {@code null} — exactly the problem Optional was
     * meant to remove. Acceptable only when calling a legacy API that requires null.
     */
    public static String orElseNullDemo(Optional<String> opt) {
        return opt.orElse(null);
    }


    // ── 5. ifPresent(consumer) ────────────────────────────────────────────────
    /**
     * Runs an action only when a value is present; does nothing otherwise.
     * Replaces {@code if (x != null) { ... }}.
     */
    public static void ifPresentDemo(Optional<String> opt, List<String> sink) {
        opt.ifPresent(sink::add);
    }

    /**
     * "Do this if present, otherwise do that" — {@code ifPresentOrElse} was added in
     * Java 9, so on Java 8 you write an explicit {@code if / else}.
     */
    public static void ifPresentOrElseJava8Demo(Optional<String> opt, List<String> sink) {
        if (opt.isPresent()) {
            sink.add("found:" + opt.get());
        } else {
            sink.add("missing");
        }
    }


    // ── 6. Fallback to another Optional ───────────────────────────────────────
    /**
     * "Try A, and if it is empty try B." Java 9 added {@code Optional.or(supplier)};
     * on Java 8 you write it by hand. The secondary lookup is wrapped in a
     * {@link Supplier} so it only runs when the primary is empty (lazy).
     */
    public static <T> Optional<T> fallbackDemo(Optional<T> primary, Supplier<Optional<T>> secondary) {
        return primary.isPresent() ? primary : secondary.get();
    }


    // ── Main — quick smoke run ────────────────────────────────────────────────
    public static void main(String[] args) {
        AtomicInteger calls = new AtomicInteger();

        System.out.println("=== 1. orElse is eager ===");
        System.out.println(orElseDemo(Optional.of("value"), calls) + " | default evaluated " + calls.get() + "x");

        System.out.println("---");
        calls.set(0);
        System.out.println("=== 2. orElseGet is lazy ===");
        System.out.println(orElseGetDemo(Optional.of("value"), calls) + " | default evaluated " + calls.get() + "x");
        System.out.println(orElseGetDemo(Optional.<String>empty(), calls) + " | default evaluated " + calls.get() + "x");

        System.out.println("---");
        System.out.println("=== 3. orElseThrow ===");
        System.out.println(orElseThrowDemo(Optional.of("ok"), "unused"));
        try {
            orElseThrowDemo(Optional.<String>empty(), "user not found");
        } catch (IllegalArgumentException e) {
            System.out.println("IllegalArgumentException: " + e.getMessage());
        }
        try {
            orElseThrowRefDemo(Optional.<String>empty());
        } catch (IllegalStateException e) {
            System.out.println("IllegalStateException via constructor reference");
        }

        System.out.println("---");
        System.out.println("=== 4. orElse(null) ===");
        System.out.println(orElseNullDemo(Optional.<String>empty()));

        System.out.println("---");
        System.out.println("=== 5. ifPresent ===");
        List<String> sink = new ArrayList<String>();
        ifPresentDemo(Optional.of("a"), sink);
        ifPresentDemo(Optional.<String>empty(), sink);
        ifPresentOrElseJava8Demo(Optional.of("b"), sink);
        ifPresentOrElseJava8Demo(Optional.<String>empty(), sink);
        System.out.println(sink);

        System.out.println("---");
        System.out.println("=== 6. fallback ===");
        System.out.println(fallbackDemo(Optional.<String>empty(), () -> Optional.of("from-secondary")));
    }
}
