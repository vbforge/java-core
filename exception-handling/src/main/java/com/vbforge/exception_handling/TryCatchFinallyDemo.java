package com.vbforge.exception_handling;

import java.util.ArrayList;
import java.util.List;

/**
 * Demonstrates {@code try / catch / finally}.
 *
 * <p>Why it matters: an exception unwinds the call stack until something catches it.
 * {@code try/catch} lets you decide WHERE a failure is handled; {@code finally}
 * guarantees cleanup code runs no matter how the block is left.
 *
 * <p>Performance note: a {@code try} block costs nothing when nothing is thrown, but
 * creating an exception captures the stack trace, which is comparatively expensive —
 * do not use exceptions as ordinary control flow.
 *
 * <p>Demo methods are {@code static} and RETURN their result so that
 * {@code TryCatchFinallyDemoTest} can verify every claim.
 */
public class TryCatchFinallyDemo {

    // ── 1. try / catch basics ─────────────────────────────────────────────────
    /**
     * Handle a failure close to where it happens and continue with a sensible value.
     * Catch the most SPECIFIC exception you can handle — here only {@link NumberFormatException}.
     */
    public static int parseOrDefaultDemo(String text, int defaultValue) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }


    // ── 2. Multi-catch (Java 7+) ──────────────────────────────────────────────
    /**
     * {@code catch (A | B e)} handles several unrelated exception types the same way
     * without duplicating the catch body. The types must not be subclasses of each other.
     * Here a non-String argument fails with {@link ClassCastException}; text that is
     * not a number (or is {@code null}) fails with {@link NumberFormatException}.
     */
    public static int toIntOrMinusOneDemo(Object value) {
        try {
            return Integer.parseInt((String) value);
        } catch (NumberFormatException | ClassCastException e) {
            return -1;
        }
    }


    // ── 3. Catch order ────────────────────────────────────────────────────────
    /**
     * Catch blocks are checked top-down and the FIRST match wins, so write the most
     * specific type first. Reversing the order does not compile: the compiler rejects
     * a catch for {@code ArithmeticException} placed after {@code RuntimeException}
     * as unreachable.
     */
    public static String classifyDemo(Runnable action) {
        try {
            action.run();
            return "none";
        } catch (ArithmeticException e) {
            return "arithmetic";
        } catch (RuntimeException e) {
            return "runtime";
        }
    }


    // ── 4. finally always runs ────────────────────────────────────────────────
    /**
     * {@code finally} runs whether the try block finishes normally or throws —
     * after the catch block, if one ran. Use it for cleanup that must always happen.
     */
    public static void finallyOrderDemo(boolean fail, List<String> log) {
        try {
            log.add("try");
            if (fail) {
                throw new IllegalStateException("boom");
            }
            log.add("try-end");
        } catch (IllegalStateException e) {
            log.add("catch");
        } finally {
            log.add("finally");
        }
    }

    /** {@code finally} even runs when the try block executes {@code return}. */
    public static String finallyAfterReturnDemo(List<String> log) {
        try {
            log.add("try");
            return "from-try";
        } finally {
            log.add("finally");
        }
    }


    // ── 5. finally gotchas ────────────────────────────────────────────────────
    /**
     * <b>Gotcha:</b> the return value is computed BEFORE {@code finally} runs.
     * Changing the variable in {@code finally} does not change what is returned.
     */
    public static int finallyDoesNotChangeReturnedValueDemo() {
        int x = 1;
        try {
            return x;
        } finally {
            x = 2;
        }
    }

    /**
     * <b>Gotcha:</b> a {@code return} inside {@code finally} silently DISCARDS any
     * exception that was in flight. Never do this.
     */
    @SuppressWarnings("finally")
    public static String returnInFinallyDemo() {
        try {
            throw new IllegalStateException("lost");
        } finally {
            return "finally wins";
        }
    }

    /**
     * <b>Gotcha:</b> an exception thrown from {@code finally} REPLACES the original one.
     * The caller sees "from finally" and never learns about "original".
     * (try-with-resources fixes this — see {@code TryWithResourcesDemo}.)
     */
    @SuppressWarnings("finally")
    public static void finallyThrowsDemo() {
        try {
            throw new IllegalStateException("original");
        } finally {
            throw new IllegalArgumentException("from finally");
        }
    }


    // ── Main — quick smoke run ────────────────────────────────────────────────
    public static void main(String[] args) {
        System.out.println("=== 1. try/catch ===");
        System.out.println(parseOrDefaultDemo("42", 0));
        System.out.println(parseOrDefaultDemo("abc", 0));

        System.out.println("---");
        System.out.println("=== 2. multi-catch ===");
        System.out.println(toIntOrMinusOneDemo("12"));
        System.out.println(toIntOrMinusOneDemo(42));     // ClassCastException -> -1
        System.out.println(toIntOrMinusOneDemo(null));   // NumberFormatException -> -1

        System.out.println("---");
        System.out.println("=== 3. catch order ===");
        System.out.println(classifyDemo(() -> System.out.print("")));
        System.out.println(classifyDemo(() -> {
            int zero = 0;
            System.out.print(1 / zero);
        }));
        System.out.println(classifyDemo(() -> {
            throw new IllegalStateException();
        }));

        System.out.println("---");
        System.out.println("=== 4. finally always runs ===");
        List<String> log = new ArrayList<String>();
        finallyOrderDemo(true, log);
        System.out.println(log);
        log.clear();
        System.out.println(finallyAfterReturnDemo(log) + " " + log);

        System.out.println("---");
        System.out.println("=== 5. finally gotchas ===");
        System.out.println(finallyDoesNotChangeReturnedValueDemo());   // 1
        System.out.println(returnInFinallyDemo());                     // finally wins
        try {
            finallyThrowsDemo();
        } catch (RuntimeException e) {
            System.out.println(e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
}
