package com.vbforge.logging_basics;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.filter.ThresholdFilter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.slf4j.LoggerFactory;

/**
 * LogbackConfigurationDemo — annotated reference for how Logback (the backend) is configured.
 *
 * <p>Normally you configure Logback with {@code logback.xml} (see
 * {@code src/main/resources/logback.xml}). Everything in that file can also be done in Java,
 * which is what this demo does — it lets us <em>watch</em> the mechanics at work and test them
 * without touching files.
 *
 * <p>Topics:
 * <ol>
 *   <li>The configuration file — what {@code logback.xml} gave us</li>
 *   <li>Logger hierarchy — names, levels and inheritance</li>
 *   <li>Level parsing — and the silent fallback trap</li>
 *   <li>Appenders and additivity — where events go, and why they sometimes go twice</li>
 *   <li>Pattern layout — how an event becomes a line of text</li>
 *   <li>Threshold filter — per-appender level gate</li>
 * </ol>
 *
 * <p>Vocabulary: a <b>logger</b> decides <em>whether</em> an event is accepted (level),
 * an <b>appender</b> decides <em>where</em> it is written, and a <b>layout/encoder</b>
 * decides <em>how</em> it looks.
 */
public class LogbackConfigurationDemo {

    // The LoggerContext is Logback's registry of all loggers. SLF4J hands it out as an
    // ILoggerFactory — the cast works because Logback is the backend on the classpath.
    private static final LoggerContext CONTEXT = (LoggerContext) LoggerFactory.getILoggerFactory();

    public static void main(String[] args) {
        configFileDemo();
        System.out.println("---");
        hierarchyDemo();
        System.out.println("---");
        levelParsingDemo();
        System.out.println("---");
        additivityDemo();
        System.out.println("---");
        patternLayoutDemo();
        System.out.println("---");
        thresholdFilterDemo();
    }

    // ── 1. The configuration file ─────────────────────────────────────────────

    /**
     * On startup Logback searches the classpath for {@code logback-test.xml}, then
     * {@code logback.xml}; if neither exists it falls back to a built-in default
     * (console output at DEBUG).
     *
     * <p>WHY this matters: configuration lives <em>outside</em> the code, so operations can
     * change levels and destinations per environment without a rebuild. The file shipped
     * with this module puts the root logger at INFO with a CONSOLE appender, and opens this
     * package up to DEBUG.
     *
     * <p>Common gotcha: a stray {@code logback-test.xml} on the test classpath silently
     * wins over {@code logback.xml}. If tests log differently from the app, look there first.
     */
    static void configFileDemo() {
        System.out.println("=== Configuration file ===");

        Logger root = CONTEXT.getLogger(Logger.ROOT_LOGGER_NAME);
        Logger pkg  = CONTEXT.getLogger("com.vbforge.logging_basics");

        System.out.println("root level      : " + root.getLevel());                          // INFO
        System.out.println("root appender   : " + root.getAppender("CONSOLE").getName());    // CONSOLE
        System.out.println("package level   : " + pkg.getLevel());                           // DEBUG
    }

    // ── 2. Logger hierarchy ───────────────────────────────────────────────────

    /**
     * Loggers form a tree based on their dot-separated names: {@code a.b.c} is a child of
     * {@code a.b}, which is a child of {@code a}, which is a child of the ROOT logger.
     *
     * <p>WHY this matters: a logger with no level of its own inherits the <em>effective level</em>
     * of its nearest ancestor that has one. Setting a level on {@code org.hibernate} quietens
     * every class in that package with a single line.
     *
     * <ul>
     *   <li>{@code getLevel()} — the level set on <em>this</em> logger, or {@code null}</li>
     *   <li>{@code getEffectiveLevel()} — the level actually applied (never {@code null})</li>
     * </ul>
     *
     * <p>Common gotcha: reading {@code getLevel()} and being surprised by {@code null}.
     */
    static void hierarchyDemo() {
        System.out.println("=== Logger hierarchy ===");

        Logger parent = CONTEXT.getLogger("demo.cfg.hierarchy");
        Logger child  = CONTEXT.getLogger("demo.cfg.hierarchy.child");

        System.out.println("child level          : " + child.getLevel());            // null — nothing set
        System.out.println("child effective      : " + child.getEffectiveLevel());   // INFO — inherited from ROOT

        parent.setLevel(Level.WARN);
        System.out.println("parent set to WARN   : " + child.getEffectiveLevel());   // WARN — inherited from parent

        child.setLevel(Level.DEBUG);
        System.out.println("child set to DEBUG   : " + child.getEffectiveLevel());   // DEBUG — own level wins

        child.setLevel(null);                                                        // null = "inherit again"
        System.out.println("child reset          : " + child.getEffectiveLevel());   // WARN

        parent.setLevel(null);                                                       // leave no trace behind
    }

    // ── 3. Level parsing ──────────────────────────────────────────────────────

    /**
     * Turning a String such as {@code "warn"} (from a config file or an environment variable)
     * into a {@link Level}.
     *
     * <p>WHY this matters: levels often arrive as text. {@code Level.toLevel(String)} is
     * case-insensitive, but <em>every</em> variant that takes only the String quietly returns
     * DEBUG for text it does not understand — including {@code Level.valueOf}. A typo such as
     * {@code "WARNING"} therefore turns your service into a DEBUG firehose with no error.
     *
     * <p>Fix: use {@code Level.toLevel(text, null)} and treat {@code null} as "invalid".
     */
    static void levelParsingDemo() {
        System.out.println("=== Level parsing ===");

        System.out.println("toLevel(\"warn\")           : " + Level.toLevel("warn"));              // WARN
        System.out.println("valueOf(\"WARNING\")        : " + Level.valueOf("WARNING"));           // DEBUG — silent fallback!
        System.out.println("toLevel(\"WARNING\", null)  : " + Level.toLevel("WARNING", null));     // null — detectable
    }

    // ── 4. Appenders and additivity ───────────────────────────────────────────

    /**
     * An <b>appender</b> is a destination (console, file, socket, memory). A logger can have
     * several, and — unless told otherwise — an event is also handed to every appender of
     * every <em>ancestor</em> logger. That inheritance is called <b>additivity</b>.
     *
     * <p>WHY this matters: additivity is what makes the root appender catch everything in
     * the application. It is also the classic cause of <em>duplicate log lines</em>: attach the
     * same appender to both a child and the root and the event arrives twice.
     *
     * <ul>
     *   <li>{@code logger.addAppender(a)} — add a destination to this logger</li>
     *   <li>{@code logger.setAdditive(false)} — stop events travelling up to ancestors' appenders</li>
     * </ul>
     *
     * <p>Common gotcha: {@code setAdditive(false)} changes <em>only</em> where events go. The
     * level check has already happened, so it never changes whether an event is accepted.
     */
    static void additivityDemo() {
        System.out.println("=== Appenders and additivity ===");

        Logger parent = CONTEXT.getLogger("demo.cfg.additivity");
        Logger child  = CONTEXT.getLogger("demo.cfg.additivity.child");
        ListAppender<ILoggingEvent> parentEvents = attach(parent);
        ListAppender<ILoggingEvent> childEvents  = attach(child);

        // Additive (default): child appender, parent appender and the root CONSOLE all see it
        child.info("first event (additive)");
        System.out.println("child appender  : " + childEvents.list.size());    // 1
        System.out.println("parent appender : " + parentEvents.list.size());   // 1

        // Not additive: the event stops at the child's own appenders
        child.setAdditive(false);
        child.info("second event (not additive)");
        System.out.println("child appender  : " + childEvents.list.size());    // 2
        System.out.println("parent appender : " + parentEvents.list.size());   // still 1

        // Duplicate-lines trap: the SAME appender at two levels of the tree
        child.setAdditive(true);
        ListAppender<ILoggingEvent> shared = attach(parent);
        child.addAppender(shared);
        child.info("third event (shared appender on parent AND child)");
        System.out.println("shared appender : " + shared.list.size());        // 2 — the same event, twice!

        // Clean up so the demo can be run repeatedly
        parent.detachAndStopAllAppenders();
        child.detachAndStopAllAppenders();
    }

    // ── 5. Pattern layout ─────────────────────────────────────────────────────

    /**
     * A <b>layout</b> turns an {@link ILoggingEvent} into text using a pattern of
     * {@code %conversion} words.
     *
     * <p>WHY this matters: the pattern decides what every line of your logs contains, and
     * therefore what you can search for at 3 a.m. Common conversion words:
     * <ul>
     *   <li>{@code %d{HH:mm:ss.SSS}} — timestamp</li>
     *   <li>{@code %thread} — name of the logging thread</li>
     *   <li>{@code %-5level} — level, left-aligned in 5 columns</li>
     *   <li>{@code %logger{36}} — logger name, abbreviated to about 36 characters</li>
     *   <li>{@code %msg} — the formatted message</li>
     *   <li>{@code %X{key}} — a value from the MDC (see StructuredLoggingDemo)</li>
     *   <li>{@code %n} — platform line separator</li>
     * </ul>
     *
     * <p>Common gotcha: a layout must be given the {@link LoggerContext} and be
     * <em>started</em> before use — an unstarted component is silently inert.
     */
    static void patternLayoutDemo() {
        System.out.println("=== Pattern layout ===");

        Logger logger = CONTEXT.getLogger("demo.cfg.layout");
        logger.setAdditive(false);
        ListAppender<ILoggingEvent> appender = attach(logger);
        logger.warn("disk at {}%", 91);

        PatternLayout layout = new PatternLayout();
        layout.setContext(CONTEXT);
        layout.setPattern("%-5level %logger - %msg");
        layout.start();

        System.out.println("formatted       : " + layout.doLayout(appender.list.get(0)));   // WARN  demo.cfg.layout - disk at 91%

        logger.detachAndStopAllAppenders();
        logger.setAdditive(true);
    }

    // ── 6. Threshold filter ───────────────────────────────────────────────────

    /**
     * A {@link ThresholdFilter} attached to an <em>appender</em> drops events below a level
     * for that destination only.
     *
     * <p>WHY this matters: the logger level is one knob for the whole logger; filters give
     * each destination its own. Typical use: log DEBUG to a file for developers, but send only
     * WARN and above to the console (or to an alerting appender).
     *
     * <p>Order of checks: logger level first (cheap, before the event is even built), then
     * the appender's filters.
     *
     * <p>Common gotcha: a filter you forgot to {@code start()} does nothing — it answers
     * "no opinion" to every event and everything passes. No warning, no error.
     */
    static void thresholdFilterDemo() {
        System.out.println("=== Threshold filter ===");

        Logger logger = CONTEXT.getLogger("demo.cfg.threshold");
        logger.setAdditive(false);
        logger.setLevel(Level.DEBUG);
        ListAppender<ILoggingEvent> appender = attach(logger);

        ThresholdFilter filter = new ThresholdFilter();
        filter.setLevel("WARN");
        appender.addFilter(filter);                              // attached, but NOT started

        logger.info("info while the filter is not started");
        System.out.println("filter not started : " + appender.list.size());   // 1 — filter ignored!

        filter.start();
        logger.debug("debug is rejected");
        logger.warn("warn is kept");
        System.out.println("filter started     : " + appender.list.size());   // 2 — only the WARN was added

        logger.detachAndStopAllAppenders();
        logger.setLevel(null);
        logger.setAdditive(true);
    }

    // ── helper ────────────────────────────────────────────────────────────────

    /** Creates a started in-memory appender and attaches it to {@code logger}. */
    private static ListAppender<ILoggingEvent> attach(Logger logger) {
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.setContext(CONTEXT);
        appender.start();
        logger.addAppender(appender);
        return appender;
    }
}
