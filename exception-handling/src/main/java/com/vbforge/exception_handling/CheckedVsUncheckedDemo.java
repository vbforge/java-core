package com.vbforge.exception_handling;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Demonstrates CHECKED vs UNCHECKED exceptions.
 *
 * <pre>
 *   Throwable
 *   ├── Error                 unchecked — JVM-level problems (OutOfMemoryError, StackOverflowError)
 *   └── Exception             CHECKED   — compiler forces "handle or declare"
 *       └── RuntimeException  unchecked — programming errors (NullPointerException, IllegalArgumentException)
 * </pre>
 *
 * <p>Why the split exists: checked exceptions model failures a caller can reasonably
 * recover from (missing file, network down); unchecked ones model bugs that should be
 * fixed rather than caught.
 *
 * <p>Demo methods are {@code static} and RETURN their result so that
 * {@code CheckedVsUncheckedDemoTest} can verify every claim.
 */
public class CheckedVsUncheckedDemo {

    // ── 1. The hierarchy ──────────────────────────────────────────────────────
    /**
     * "Checked" means: a {@link Throwable} that is neither a {@link RuntimeException}
     * nor an {@link Error} (this includes {@code Exception} itself).
     */
    public static boolean isCheckedDemo(Throwable throwable) {
        return !(throwable instanceof RuntimeException) && !(throwable instanceof Error);
    }


    // ── 2. Checked: handle or declare ─────────────────────────────────────────
    /**
     * A method that can throw a checked exception MUST declare it with {@code throws}.
     * Callers then have to either catch it or declare it too.
     */
    public static String loadDemo(String key) throws IOException {
        if (key == null || key.isEmpty()) {
            throw new IOException("empty key");
        }
        return "value-of-" + key;
    }

    /** Handling: catch the checked exception and recover with a default. */
    public static String loadOrDefaultDemo(String key) {
        try {
            return loadDemo(key);
        } catch (IOException e) {
            return "default";
        }
    }


    // ── 3. Unchecked: no declaration required ─────────────────────────────────
    /**
     * Unchecked exceptions need no {@code throws} clause and no {@code catch}.
     * Use them for contract violations — the caller passed something illegal.
     */
    public static int requirePositiveDemo(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("n must be positive: " + n);
        }
        return n;
    }


    // ── 4. Catching a parent type ─────────────────────────────────────────────
    /**
     * A {@code catch} for a parent type also catches every subclass.
     * {@link FileNotFoundException} is an {@link IOException}, so this block handles it.
     * <b>Gotcha:</b> catching {@code Exception} or {@code Throwable} that broadly also swallows
     * bugs (and, for {@code Throwable}, JVM {@link Error}s) — catch what you can actually handle.
     */
    public static String catchParentDemo() {
        try {
            throw new FileNotFoundException("missing.txt");
        } catch (IOException e) {
            return "caught as IOException: " + e.getClass().getSimpleName();
        }
    }


    // ── 5. Converting checked → unchecked ─────────────────────────────────────
    /**
     * Wrap a checked exception in an unchecked one when you cannot handle it here and
     * do not want {@code throws} on every caller. ALWAYS pass the original as the cause:
     * {@link UncheckedIOException} exists exactly for wrapping an {@link IOException}.
     */
    public static String wrapDemo(String key) {
        try {
            return loadDemo(key);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }


    // ── 6. Checked exceptions and lambdas ─────────────────────────────────────
    /**
     * <b>Gotcha:</b> {@code Function}, {@code Consumer} and the other standard functional
     * interfaces declare no checked exceptions, so a lambda body cannot let one escape.
     * Inside the lambda you must catch it and rethrow an unchecked wrapper.
     */
    public static List<String> loadAllDemo(List<String> keys) {
        return keys.stream()
                .map(key -> {
                    try {
                        return loadDemo(key);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                })
                .collect(Collectors.toList());
    }


    // ── Main — quick smoke run ────────────────────────────────────────────────
    public static void main(String[] args) {
        System.out.println("=== 1. hierarchy ===");
        System.out.println("IOException checked?        " + isCheckedDemo(new IOException()));
        System.out.println("IllegalArgumentException?   " + isCheckedDemo(new IllegalArgumentException()));
        System.out.println("StackOverflowError?         " + isCheckedDemo(new StackOverflowError()));

        System.out.println("---");
        System.out.println("=== 2. checked: handle or declare ===");
        System.out.println(loadOrDefaultDemo("a"));
        System.out.println(loadOrDefaultDemo(""));

        System.out.println("---");
        System.out.println("=== 3. unchecked ===");
        try {
            requirePositiveDemo(-1);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("---");
        System.out.println("=== 4. catching a parent type ===");
        System.out.println(catchParentDemo());

        System.out.println("---");
        System.out.println("=== 5. wrapping ===");
        try {
            wrapDemo("");
        } catch (UncheckedIOException e) {
            System.out.println("wrapped, cause = " + e.getCause().getMessage());
        }

        System.out.println("---");
        System.out.println("=== 6. lambdas ===");
        System.out.println(loadAllDemo(Arrays.asList("x", "y")));
    }
}
