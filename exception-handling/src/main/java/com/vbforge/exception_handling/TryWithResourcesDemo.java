package com.vbforge.exception_handling;

import java.util.ArrayList;
import java.util.List;

/**
 * Demonstrates {@code try-with-resources} (Java 7+).
 *
 * <p>Any object implementing {@link AutoCloseable} declared in the parentheses is closed
 * automatically when the block ends — normally or by exception. It replaces the error-prone
 * {@code try / finally { r.close(); }} pattern.
 *
 * <p>All demos use {@link TrackedResource}, which records {@code open:x}, {@code use:x} and
 * {@code close:x} in a log so the exact ORDER of events can be asserted.
 *
 * <p>Demo methods are {@code static} and fill/return their results so that
 * {@code TryWithResourcesDemoTest} can verify every claim.
 */
public class TryWithResourcesDemo {

    // ── 1. Manual close vs try-with-resources ─────────────────────────────────
    /** The pre-Java-7 way: you must remember the {@code finally} and the {@code close()}. */
    public static void manualCloseDemo(List<String> log) {
        TrackedResource r = new TrackedResource("r", log);
        try {
            r.use();
        } finally {
            r.close();
        }
    }

    /** Same behaviour, no boilerplate, nothing to forget. */
    public static void tryWithResourcesDemo(List<String> log) {
        try (TrackedResource r = new TrackedResource("r", log)) {
            r.use();
        }
    }


    // ── 2. Close order ────────────────────────────────────────────────────────
    /**
     * Several resources may be declared, separated by {@code ;}. They are opened top to
     * bottom and CLOSED IN REVERSE ORDER — later resources may depend on earlier ones.
     */
    public static void closeOrderDemo(List<String> log) {
        try (TrackedResource a = new TrackedResource("a", log);
             TrackedResource b = new TrackedResource("b", log)) {
            a.use();
            b.use();
        }
    }


    // ── 3. Closing happens BEFORE catch and finally ───────────────────────────
    /**
     * The resource is closed as soon as the try block is left — before the {@code catch}
     * block runs. With a plain {@code try/finally} the cleanup would come AFTER the catch.
     */
    public static void closeBeforeCatchDemo(List<String> log) {
        try (TrackedResource r = new TrackedResource("r", log)) {
            r.use();
            throw new IllegalStateException("body failed");
        } catch (IllegalStateException e) {
            log.add("catch:" + e.getMessage());
        } finally {
            log.add("finally");
        }
    }


    // ── 4. Suppressed exceptions ──────────────────────────────────────────────
    /**
     * If the body throws AND {@code close()} throws, the BODY exception is the one you get;
     * the close failure is attached to it and available via {@link Throwable#getSuppressed()}.
     * Nothing is lost.
     */
    public static Exception suppressedDemo(List<String> log) {
        try (TrackedResource r = new TrackedResource("r", log, false, true)) {
            r.use();
            throw new IllegalStateException("body failed");
        } catch (IllegalStateException e) {
            return e;
        }
    }

    /**
     * Contrast — the same situation written with {@code try/finally}: the exception from
     * {@code close()} REPLACES the body exception, which is gone for good.
     */
    public static Exception manualFinallyMasksDemo(List<String> log) {
        TrackedResource r = new TrackedResource("r", log, false, true);
        try {
            try {
                r.use();
                throw new IllegalStateException("body failed");
            } finally {
                r.close();
            }
        } catch (IllegalStateException e) {
            return e;
        }
    }

    /** If only {@code close()} fails, that exception propagates normally. */
    public static String closeFailureDemo(List<String> log) {
        try (TrackedResource r = new TrackedResource("r", log, false, true)) {
            r.use();
            return "closed cleanly";
        } catch (IllegalStateException e) {
            return e.getMessage();
        }
    }


    // ── 5. Partial initialisation ─────────────────────────────────────────────
    /**
     * Only resources that were SUCCESSFULLY created get closed. If the second constructor
     * throws, the first resource is still closed, and the failed one never existed.
     */
    public static void partialInitDemo(List<String> log) {
        try (TrackedResource a = new TrackedResource("a", log);
             TrackedResource b = new TrackedResource("b", log, true, false)) {
            a.use();
            b.use();
        } catch (IllegalStateException e) {
            log.add("catch:" + e.getMessage());
        }
    }


    // ── 6. Null resources ─────────────────────────────────────────────────────
    /**
     * A resource that is {@code null} is simply skipped on close — no
     * {@code NullPointerException}. Handy when a factory may return nothing.
     */
    @SuppressWarnings("try")
    public static void nullResourceDemo(List<String> log) {
        try (TrackedResource r = null) {
            log.add("body-ran");
        }
    }


    // ── Main — quick smoke run ────────────────────────────────────────────────
    public static void main(String[] args) {
        List<String> log = new ArrayList<String>();

        System.out.println("=== 1. manual vs try-with-resources ===");
        manualCloseDemo(log);
        System.out.println(log);
        log.clear();
        tryWithResourcesDemo(log);
        System.out.println(log);

        System.out.println("---");
        log.clear();
        System.out.println("=== 2. close order ===");
        closeOrderDemo(log);
        System.out.println(log);

        System.out.println("---");
        log.clear();
        System.out.println("=== 3. close before catch ===");
        closeBeforeCatchDemo(log);
        System.out.println(log);

        System.out.println("---");
        log.clear();
        System.out.println("=== 4. suppressed ===");
        Exception e = suppressedDemo(log);
        System.out.println(e.getMessage() + " | suppressed: " + e.getSuppressed()[0].getMessage());
        System.out.println("manual finally: " + manualFinallyMasksDemo(new ArrayList<String>()).getMessage());
        System.out.println(closeFailureDemo(new ArrayList<String>()));

        System.out.println("---");
        log.clear();
        System.out.println("=== 5. partial init ===");
        partialInitDemo(log);
        System.out.println(log);

        System.out.println("---");
        log.clear();
        System.out.println("=== 6. null resource ===");
        nullResourceDemo(log);
        System.out.println(log);
    }
}
