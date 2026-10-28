package com.vbforge.logging_basics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * Slf4jBasicsDemo — annotated reference for the SLF4J API (the logging facade).
 *
 * <p>Read top-to-bottom. Each section covers one part of the API with concrete
 * examples and a note explaining <em>why</em> the behaviour is what it is.
 *
 * <p>Topics:
 * <ol>
 *   <li>Logger acquisition — {@code LoggerFactory.getLogger}, one logger per class</li>
 *   <li>Levels — TRACE &lt; DEBUG &lt; INFO &lt; WARN &lt; ERROR, and what "enabled" means</li>
 *   <li>Parameterized messages — {@code {}} placeholders instead of concatenation</li>
 *   <li>Logging exceptions — the throwable goes last, and keeps its stack trace</li>
 *   <li>Lazy evaluation — guards and the SLF4J 2.x fluent API</li>
 * </ol>
 *
 * <p>SLF4J is only a <em>facade</em>: it defines the API, while a backend (here Logback)
 * decides where the output goes. Your code depends on {@code org.slf4j.*} only,
 * so the backend can be swapped without touching a single log statement.
 */
public class Slf4jBasicsDemo {

    // WHY private static final: a Logger is thread-safe and cheap to share. Looking it up
    // once per class (instead of once per call) keeps the hot path free of map lookups,
    // and naming it after the class gives every line a traceable origin.
    private static final Logger log = LoggerFactory.getLogger(Slf4jBasicsDemo.class);

    public static void main(String[] args) {
        loggerDemo();
        System.out.println("---");
        levelsDemo();
        System.out.println("---");
        parameterizedDemo();
        System.out.println("---");
        exceptionDemo();
        System.out.println("---");
        lazyEvaluationDemo();
    }

    // ── 1. Logger acquisition ─────────────────────────────────────────────────

    /**
     * {@code LoggerFactory.getLogger(...)} returns the Logger registered under a name.
     *
     * <p>WHY this matters: the logger <em>name</em> is what the backend uses to decide the
     * level and the appenders. Names are dot-separated, so {@code com.acme.Foo} is a child
     * of {@code com.acme} — configure the package once and every class below inherits it.
     * Passing the class (instead of a hand-typed string) keeps the name correct when the
     * class is renamed or moved.
     *
     * <p>Common gotcha: copy-pasting {@code getLogger(OtherClass.class)} into a new class.
     * Every line then claims to come from the wrong place.
     */
    static void loggerDemo() {
        System.out.println("=== Logger acquisition ===");

        Logger byClass = LoggerFactory.getLogger(Slf4jBasicsDemo.class);
        Logger byName  = LoggerFactory.getLogger("com.vbforge.logging_basics.Slf4jBasicsDemo");
        Logger other   = LoggerFactory.getLogger("payments.audit");

        System.out.println("name            : " + byClass.getName());       // fully-qualified class name
        System.out.println("same instance   : " + (byClass == byName));     // true — one Logger per name
        System.out.println("other instance  : " + (byClass == other));      // false
        System.out.println("other name      : " + other.getName());
    }

    // ── 2. Levels ─────────────────────────────────────────────────────────────

    /**
     * Five levels, ordered by severity: TRACE &lt; DEBUG &lt; INFO &lt; WARN &lt; ERROR.
     *
     * <p>WHY this matters: the level is a <em>volume knob</em>. A logger set to INFO emits
     * INFO, WARN and ERROR and silently drops TRACE and DEBUG. That lets you leave rich
     * diagnostics in the code and switch them on only when investigating.
     *
     * <ul>
     *   <li>TRACE — finest detail (each loop iteration, full payloads); never on in production</li>
     *   <li>DEBUG — developer diagnostics (chosen branch, computed values)</li>
     *   <li>INFO  — normal business milestones (started, order placed, job finished)</li>
     *   <li>WARN  — unexpected but handled (retrying, fallback used, deprecated call)</li>
     *   <li>ERROR — an operation failed and someone should look</li>
     * </ul>
     *
     * <p>Common gotcha: logging a handled, expected condition (user typed a wrong password)
     * as ERROR. Alerts built on ERROR then fire all day and everybody learns to ignore them.
     */
    static void levelsDemo() {
        System.out.println("=== Levels ===");

        log.trace("TRACE - finest detail, e.g. every loop iteration");
        log.debug("DEBUG - diagnostic detail for developers");
        log.info("INFO  - normal business milestone");
        log.warn("WARN  - unexpected, but we recovered");
        log.error("ERROR - the operation failed");

        // The is...Enabled() methods report what the backend currently lets through.
        // With the shipped logback.xml this package is at DEBUG: TRACE is off, the rest are on.
        System.out.println("trace enabled   : " + log.isTraceEnabled());
        System.out.println("debug enabled   : " + log.isDebugEnabled());
        System.out.println("info enabled    : " + log.isInfoEnabled());
    }

    // ── 3. Parameterized messages ─────────────────────────────────────────────

    /**
     * {@code log.info("User {} logged in", user)} — the {@code {}} is a placeholder that
     * SLF4J fills in with the arguments, in order.
     *
     * <p>WHY this matters: with concatenation ({@code "User " + user + ...}) the final
     * String is built <em>before</em> the call, even when the level is disabled and the
     * line is thrown away. With placeholders, SLF4J builds the String only after it knows
     * the line will actually be written. As a bonus the template stays constant, which
     * makes log lines easy to group and search.
     *
     * <p>Rules worth memorising:
     * <ul>
     *   <li>a backslash before the placeholder (written {@code "\\{}"} in Java) prints a literal {@code {}}</li>
     *   <li>fewer arguments than placeholders — the leftover {@code {}} stay as they are</li>
     *   <li>more arguments than placeholders — the extras are ignored</li>
     *   <li>{@code null} arguments print as {@code null}</li>
     * </ul>
     *
     * <p>Common gotcha: the placeholder is {@code {}}, not {@code %s} or {@code {0}}.
     */
    static void parameterizedDemo() {
        System.out.println("=== Parameterized messages ===");

        String user = "alice";
        int attempts = 3;

        log.info("User {} logged in after {} attempt(s)", user, attempts);   // preferred
        log.info("User " + user + " logged in after " + attempts + " attempt(s)"); // works, but builds the String eagerly

        log.info("Literal \\{} stays, value={}", "x");          // escaped placeholder
        log.info("Too few arguments: {} and {}", "only-one");   // second {} is left untouched
        log.info("Too many arguments: {}", "used", "ignored");  // extras are dropped
        log.info("Null argument: {}", (Object) null);           // prints null
        log.info("{} + {} + {} = {}", 1, 2, 3, 6);              // 3+ arguments are fine (varargs)
    }

    // ── 4. Logging exceptions ─────────────────────────────────────────────────

    /**
     * Pass the exception as the <em>last</em> argument and SLF4J logs its stack trace.
     *
     * <p>WHY this matters: the stack trace is usually the single most valuable thing in
     * a failure report. SLF4J recognises a trailing {@link Throwable} argument, attaches it
     * to the event and does <em>not</em> consume a {@code {}} placeholder for it.
     *
     * <p>Common gotcha: {@code log.error("Failed: " + e.getMessage())} (or {@code e.toString()})
     * throws the stack trace away — you are left with a message and no idea where it came
     * from. Also never call {@code e.printStackTrace()}: it writes to stderr and bypasses
     * the logging configuration completely.
     */
    static void exceptionDemo() {
        System.out.println("=== Logging exceptions ===");

        String input = "abc";
        try {
            Integer.parseInt(input);
        } catch (NumberFormatException e) {
            log.error("Cannot parse '{}'", input, e);                  // GOOD: message + stack trace
            log.error("Cannot parse: " + e.getMessage());              // BAD: stack trace is lost
        }
    }

    // ── 5. Lazy evaluation ────────────────────────────────────────────────────

    /**
     * Placeholders defer the <em>formatting</em>, but not the <em>evaluation</em> of arguments.
     *
     * <p>WHY this matters: {@code log.trace("dump={}", buildHugeDump())} still calls
     * {@code buildHugeDump()} even when TRACE is off — Java evaluates arguments before the
     * call. Two ways to pay that cost only when the line will be written:
     * <ul>
     *   <li>a guard: {@code if (log.isTraceEnabled()) { ... }} — works with every SLF4J version</li>
     *   <li>the SLF4J 2.x fluent API: {@code log.atTrace().log(() -> buildHugeDump())} —
     *       the lambda runs only if TRACE is enabled</li>
     * </ul>
     *
     * <p>Common gotcha: guarding cheap arguments such as a local variable. That only adds
     * noise — reserve guards for arguments that are genuinely expensive to compute.
     */
    static void lazyEvaluationDemo() {
        System.out.println("=== Lazy evaluation ===");

        AtomicInteger calls = new AtomicInteger();
        Supplier<String> expensive = () -> {
            calls.incrementAndGet();
            return "huge dump";
        };

        // TRACE is disabled for this package, yet the argument is still evaluated:
        log.trace("unguarded: {}", expensive.get());
        System.out.println("after unguarded : " + calls.get());   // 1 — wasted work

        // Guard: the expensive call is skipped entirely
        if (log.isTraceEnabled()) {
            log.trace("guarded: {}", expensive.get());
        }
        System.out.println("after guard     : " + calls.get());   // still 1

        // Fluent API: the supplier is only invoked when the level is enabled
        log.atTrace().log(() -> expensive.get());
        System.out.println("after fluent    : " + calls.get());   // still 1
    }
}
