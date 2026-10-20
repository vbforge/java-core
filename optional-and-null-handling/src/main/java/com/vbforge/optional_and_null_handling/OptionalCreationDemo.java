package com.vbforge.optional_and_null_handling;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;

/**
 * Demonstrates how to CREATE and INSPECT an {@link Optional}.
 *
 * <p>Why {@code Optional} exists: before Java 8 a method that "might not have an
 * answer" returned {@code null}, and nothing in the signature warned the caller.
 * The result was the infamous {@code NullPointerException} far away from the
 * real mistake. {@code Optional<T>} makes "might be absent" part of the type.
 *
 * <p>Demo methods are {@code static} and RETURN their result (instead of only
 * printing) so that {@code OptionalCreationDemoTest} can verify every claim.
 */
public class OptionalCreationDemo {

    // ── 1. Optional.empty() ───────────────────────────────────────────────────
    /**
     * An Optional that holds nothing — the type-safe replacement for "return null".
     *
     * <p>Complexity: O(1) time and space.
     */
    public static Optional<String> emptyDemo() {
        return Optional.empty();
    }


    // ── 2. Optional.of(value) ─────────────────────────────────────────────────
    /**
     * Wraps a value that you KNOW is non-null.
     *
     * <p>Why it exists: it documents "this can never be null here". If you are
     * wrong, you find out immediately, at the creation site.
     *
     * <p><b>Gotcha:</b> {@code Optional.of(null)} throws {@link NullPointerException}.
     * Use {@code ofNullable} when the value might be null.
     */
    public static Optional<String> ofDemo(String value) {
        return Optional.of(value);
    }


    // ── 3. Optional.ofNullable(value) ─────────────────────────────────────────
    /**
     * Wraps a value that MIGHT be null: null becomes {@code Optional.empty()}.
     * This is the bridge between the null world and the Optional world.
     */
    public static Optional<String> ofNullableDemo(String value) {
        return Optional.ofNullable(value);
    }


    // ── 4. Wrapping legacy null-returning APIs ────────────────────────────────
    /**
     * {@code Map.get} returns {@code null} for a missing key (and also for a key
     * that is mapped to {@code null} — you cannot tell the two apart).
     * Wrapping at the boundary stops the null from leaking into the rest of your code.
     */
    public static Optional<String> lookupDemo(Map<String, String> map, String key) {
        return Optional.ofNullable(map.get(key));
    }


    // ── 5. isPresent() / get() ────────────────────────────────────────────────
    /**
     * Checking before reading is the SAFE way to call {@code get()}.
     * (Later demos show better alternatives: orElse, map, ifPresent.)
     */
    public static String describeDemo(Optional<String> opt) {
        if (opt.isPresent()) {
            return "present:" + opt.get();
        }
        return "absent";
    }

    /**
     * <b>Gotcha:</b> {@code get()} on an empty Optional throws
     * {@link java.util.NoSuchElementException} — you merely traded a
     * NullPointerException for a different exception. Never call {@code get()}
     * without proving the value is there.
     */
    public static String unsafeGetDemo(Optional<String> opt) {
        return opt.get();
    }


    // ── 6. toString() and equality ────────────────────────────────────────────
    /** {@code Optional[value]} when present, {@code Optional.empty} when absent. */
    public static String toStringDemo(Optional<?> opt) {
        return opt.toString();
    }

    /**
     * Optionals are compared by VALUE (and by value type): two empties are equal,
     * two present Optionals are equal when their contents are {@code equals}.
     * <b>Gotcha:</b> {@code Optional.of(1)} is NOT equal to {@code Optional.of(1L)}
     * because {@code Integer.equals(Long)} is false.
     */
    public static boolean sameValueDemo(Optional<?> a, Optional<?> b) {
        return a.equals(b);
    }


    // ── 7. Primitive specialisations ──────────────────────────────────────────
    /**
     * {@link OptionalInt} (also {@code OptionalLong}, {@code OptionalDouble}) avoids
     * boxing an {@code int} into an {@code Integer} object.
     * It has no {@code map}/{@code flatMap}, so use it for simple "maybe a number" results.
     *
     * <p>Complexity: O(n) time, O(1) space.
     */
    public static OptionalInt firstEvenDemo(int[] numbers) {
        for (int n : numbers) {
            if (n % 2 == 0) {
                return OptionalInt.of(n);
            }
        }
        return OptionalInt.empty();
    }


    // ── 8. When NOT to return an Optional ─────────────────────────────────────
    /**
     * For collections (and arrays) "nothing" is already expressible: an EMPTY
     * collection. Returning {@code Optional<List<T>>} gives callers two kinds of
     * "nothing" to check. Return an empty list instead.
     *
     * <p><b>Gotcha:</b> {@code Collections.emptyList()} is immutable — adding to it throws
     * {@link UnsupportedOperationException}.
     */
    public static List<String> tagsDemo(Map<String, List<String>> store, String key) {
        List<String> tags = store.get(key);
        return tags == null ? Collections.<String>emptyList() : tags;
    }


    // ── Main — quick smoke run ────────────────────────────────────────────────
    public static void main(String[] args) {
        System.out.println("=== 1-3. empty / of / ofNullable ===");
        System.out.println(emptyDemo());
        System.out.println(ofDemo("hello"));
        System.out.println(ofNullableDemo(null));
        try {
            ofDemo(null);
        } catch (NullPointerException e) {
            System.out.println("Optional.of(null) -> NullPointerException");
        }

        System.out.println("---");
        System.out.println("=== 4. wrapping a legacy API ===");
        System.out.println(lookupDemo(Collections.singletonMap("id", "42"), "id"));
        System.out.println(lookupDemo(Collections.singletonMap("id", "42"), "missing"));

        System.out.println("---");
        System.out.println("=== 5. isPresent / get ===");
        System.out.println(describeDemo(Optional.of("x")));
        System.out.println(describeDemo(Optional.<String>empty()));
        try {
            unsafeGetDemo(Optional.<String>empty());
        } catch (java.util.NoSuchElementException e) {
            System.out.println("get() on empty -> NoSuchElementException");
        }

        System.out.println("---");
        System.out.println("=== 6. toString and equality ===");
        System.out.println(toStringDemo(Optional.of("abc")));
        System.out.println(toStringDemo(Optional.empty()));
        System.out.println(sameValueDemo(Optional.of(1), Optional.of(1)));    // true
        System.out.println(sameValueDemo(Optional.of(1), Optional.of(1L)));   // false

        System.out.println("---");
        System.out.println("=== 7. primitive specialisation ===");
        System.out.println(firstEvenDemo(new int[]{3, 5, 8, 10}));
        System.out.println(firstEvenDemo(new int[]{3, 5}));

        System.out.println("---");
        System.out.println("=== 8. collections: return empty, not Optional ===");
        System.out.println(tagsDemo(Collections.<String, List<String>>emptyMap(), "java"));
    }
}
