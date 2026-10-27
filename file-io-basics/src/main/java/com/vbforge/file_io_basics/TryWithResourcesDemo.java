package com.vbforge.file_io_basics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * TryWithResourcesDemo — annotated reference for {@code try-with-resources} (Java 7+):
 * the only reliable way to make sure files, streams and readers are closed.
 *
 * <p>Read top-to-bottom. Each section covers one idea with concrete examples and a note
 * explaining <em>why</em> the behaviour is what it is.
 *
 * <p>The demos use two tiny helper resources defined at the bottom of this class,
 * {@link NamedResource} and {@link FailingResource}, so the order of events is visible
 * without touching the disk. The {@code *Tasks} class reuses them.
 *
 * <p>Topics:
 * <ol>
 *   <li>Manual {@code finally} vs try-with-resources</li>
 *   <li>Closing order — reverse of opening, and <em>before</em> {@code catch}</li>
 *   <li>Suppressed exceptions — what happens when {@code close()} fails too</li>
 *   <li>Wrapping constructors — one resource per declaration</li>
 *   <li>With files, {@code null} resources and {@code AutoCloseable}</li>
 * </ol>
 */
@SuppressWarnings("try")   // the demo resources are deliberately not referenced: only their open/close events matter
public class TryWithResourcesDemo {

    public static void main(String[] args) throws IOException {
        manualVersusAutomaticDemo();
        System.out.println("---");
        closeOrderDemo();
        System.out.println("---");
        suppressedDemo();
        System.out.println("---");
        wrappingDemo();
        System.out.println("---");
        filesAndNullDemo();
    }

    // ── 1. Manual finally vs try-with-resources ───────────────────────────────

    /**
     * Before Java 7 every resource needed a {@code try / finally} with a null check and a nested
     * {@code try / catch} around {@code close()}. Forgetting any piece leaks a file handle.
     *
     * <p>WHY this matters: an unclosed stream keeps the file locked (Windows) or uses up the
     * process's limited number of file descriptors (Linux) — "Too many open files" in a long-running
     * server. Try-with-resources closes every declared resource automatically, even when an
     * exception is thrown, and does it correctly.
     *
     * <p>Anything that implements {@link AutoCloseable} can be used; all java.io streams,
     * readers and writers do.
     */
    static void manualVersusAutomaticDemo() {
        System.out.println("=== Manual finally vs try-with-resources ===");

        // The old way — 8 lines of ceremony around 1 line of work
        List<String> manualLog = new ArrayList<>();
        NamedResource manual = null;
        try {
            manual = new NamedResource("manual", manualLog);
            manualLog.add("work");
        } finally {
            if (manual != null) {
                manual.close();
            }
        }
        System.out.println("manual         : " + manualLog);     // [open manual, work, close manual]

        // The modern way — same events, nothing to forget
        List<String> autoLog = new ArrayList<>();
        try (NamedResource auto = new NamedResource("auto", autoLog)) {
            autoLog.add("work");
        }
        System.out.println("try-with-res   : " + autoLog);       // [open auto, work, close auto]

        // It also closes when the body throws
        List<String> failLog = new ArrayList<>();
        try (NamedResource r = new NamedResource("r", failLog)) {
            throw new IllegalStateException("boom");
        } catch (IllegalStateException e) {
            failLog.add("caught " + e.getMessage());
        }
        System.out.println("on exception   : " + failLog);       // [open r, close r, caught boom]
    }

    // ── 2. Closing order ──────────────────────────────────────────────────────

    /**
     * Resources are closed in the <strong>reverse</strong> order of their declaration, like
     * unwinding a stack. A later resource may depend on an earlier one (a reader wrapped around a
     * stream), so it must be closed first.
     *
     * <p>WHY this matters: the resource is closed <em>before</em> the {@code catch} and
     * {@code finally} blocks run. In a {@code catch} you can no longer use the resource — and you
     * can be sure it is already closed.
     *
     * <p>Gotcha: separate resources with a semicolon inside the same parentheses; a trailing semicolon
     * after the last one is allowed.
     */
    static void closeOrderDemo() {
        System.out.println("=== Closing order ===");

        List<String> log = new ArrayList<>();
        try (NamedResource first = new NamedResource("first", log);
             NamedResource second = new NamedResource("second", log)) {
            log.add("body");
        }
        System.out.println("order          : " + log);
        // [open first, open second, body, close second, close first]

        // close happens before catch and finally
        List<String> flow = new ArrayList<>();
        try (NamedResource r = new NamedResource("r", flow)) {
            flow.add("body");
            throw new IllegalStateException("x");
        } catch (IllegalStateException e) {
            flow.add("catch");
        } finally {
            flow.add("finally");
        }
        System.out.println("close vs catch : " + flow);
        // [open r, body, close r, catch, finally]
    }

    // ── 3. Suppressed exceptions ──────────────────────────────────────────────

    /**
     * What if the body throws <em>and</em> {@code close()} throws as well? Two exceptions, one
     * propagation slot.
     *
     * <ul>
     *   <li><b>try-with-resources:</b> the exception from the body wins. The exception from
     *       {@code close()} is attached to it and available via {@code getSuppressed()}.</li>
     *   <li><b>manual finally:</b> the exception from {@code close()} <em>replaces</em> the body's
     *       exception — the real cause is lost.</li>
     * </ul>
     *
     * <p>WHY this matters: when debugging a failure you want the original problem (disk full while
     * writing), not a follow-up problem (cannot close the half-written file).
     */
    static void suppressedDemo() {
        System.out.println("=== Suppressed exceptions ===");

        try (FailingResource r = new FailingResource("close failed")) {
            throw new IllegalStateException("body failed");
        } catch (IllegalStateException e) {
            System.out.println("primary        : " + e.getMessage());                       // body failed
            System.out.println("suppressed #   : " + e.getSuppressed().length);             // 1
            System.out.println("suppressed[0]  : " + e.getSuppressed()[0].getMessage());    // close failed
        }

        // The pre-Java-7 pattern loses the original exception
        try {
            FailingResource r = new FailingResource("close failed");
            try {
                throw new IllegalStateException("body failed");
            } finally {
                r.close();                                    // throws and replaces "body failed"
            }
        } catch (IllegalStateException e) {
            System.out.println("manual primary : " + e.getMessage());                       // close failed — real cause lost!
            System.out.println("manual suppr.  : " + e.getSuppressed().length);             // 0
        }
    }

    // ── 4. Wrapping constructors ──────────────────────────────────────────────

    /**
     * {@code new BufferedReader(new FileReader(file))} is two objects. If the <em>outer</em>
     * constructor throws, nobody holds a reference to the inner one — it is never closed.
     * Declaring each object as its own resource avoids the problem.
     *
     * <p>WHY this matters: this is exactly how file handles leak in otherwise "correct" code.
     * {@code BufferedReader} throws {@link IllegalArgumentException} for a buffer size of 0 — a
     * realistic stand-in for any constructor failure (bad charset name, out of memory, ...).
     *
     * <p>Rule: one declaration per resource; closing the outermost wrapper closes the ones inside it too,
     * and closing an already closed {@code Closeable} is harmless.
     */
    static void wrappingDemo() throws IOException {
        System.out.println("=== Wrapping constructors ===");

        // Single expression: the inner reader leaks when the BufferedReader constructor throws
        TrackingReader leaked = new TrackingReader();
        try (BufferedReader reader = new BufferedReader(leaked, 0)) {
            System.out.println("not reached");
        } catch (IllegalArgumentException e) {
            System.out.println("single expr    : inner closed = " + leaked.closed);   // false — LEAK
        }

        // Two declarations: the inner reader is a resource of its own, so it is closed
        TrackingReader safe = new TrackingReader();
        try (Reader inner = safe;
             BufferedReader reader = new BufferedReader(inner, 0)) {
            System.out.println("not reached");
        } catch (IllegalArgumentException e) {
            System.out.println("two decls      : inner closed = " + safe.closed);     // true
        }
    }

    // ── 5. Files, null resources, AutoCloseable ───────────────────────────────

    /**
     * Typical file code, plus two details worth knowing.
     *
     * <ul>
     *   <li>A resource expression that evaluates to {@code null} is allowed: the body runs and
     *       {@code close()} is simply skipped — no {@link NullPointerException}.</li>
     *   <li>{@link java.io.Closeable} (readers, streams) declares {@code close() throws IOException}
     *       and promises that closing twice is harmless. {@link AutoCloseable} is the broader parent
     *       with {@code close() throws Exception} and no such promise.</li>
     * </ul>
     *
     * <p>Note: Java 9+ also accepts an existing effectively-final variable in the header
     * ({@code try (reader) { ... }}); on Java 8 each resource must be declared in the parentheses.
     */
    static void filesAndNullDemo() throws IOException {
        System.out.println("=== Files, null, AutoCloseable ===");

        File file = Files.createTempFile("file-io-basics-", ".txt").toFile();
        try {
            Files.write(file.toPath(), Arrays.asList("first", "second"), StandardCharsets.UTF_8);

            try (FileReader fileReader = new FileReader(file);
                 BufferedReader reader = new BufferedReader(fileReader)) {
                System.out.println("first line     : " + reader.readLine());   // first
            }                                                                  // both closed here, reader first

            // null resource: allowed, close() is skipped
            List<String> log = new ArrayList<>();
            try (NamedResource nothing = null) {
                log.add("body ran");
            }
            System.out.println("null resource  : " + log);                     // [body ran]

            // Closeable.close() is idempotent
            Reader reader = new StringReader("x");
            reader.close();
            reader.close();                                                    // second close: no exception
            System.out.println("double close   : ok");
        } finally {
            Files.deleteIfExists(file.toPath());
        }
    }

    // ── Helper resources (also used by TryWithResourcesTasks) ─────────────────

    /** A resource that records {@code "open <name>"} and {@code "close <name>"} in a shared log. */
    static class NamedResource implements AutoCloseable {
        private final String name;
        private final List<String> log;

        NamedResource(String name, List<String> log) {
            this.name = name;
            this.log = log;
            log.add("open " + name);
        }

        @Override
        public void close() {
            log.add("close " + name);
        }
    }

    /** A resource whose {@code close()} always fails with an {@link IllegalStateException}. */
    static class FailingResource implements AutoCloseable {
        private final String closeMessage;

        FailingResource(String closeMessage) {
            this.closeMessage = closeMessage;
        }

        @Override
        public void close() {
            throw new IllegalStateException(closeMessage);
        }
    }

    /** A reader that remembers whether {@code close()} has been called. */
    private static class TrackingReader extends StringReader {
        boolean closed;

        TrackingReader() {
            super("");
        }

        @Override
        public void close() {
            closed = true;
            super.close();
        }
    }
}
