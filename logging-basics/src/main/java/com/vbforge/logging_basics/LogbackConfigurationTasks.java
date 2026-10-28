package com.vbforge.logging_basics;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.read.ListAppender;

/**
 * LogbackConfigurationTasks — implement every method below to make
 * {@code LogbackConfigurationTasksTest} go GREEN.
 *
 * <p><b>Workflow:</b>
 * <ol>
 *   <li>Read {@code LogbackConfigurationDemo.java} to understand the relevant concept.</li>
 *   <li>Implement the method — remove the {@code UnsupportedOperationException}.</li>
 *   <li>Run {@code mvn test -Dtest=LogbackConfigurationTasksTest} and watch it turn GREEN ✅</li>
 * </ol>
 *
 * <p>Each method has a Javadoc describing exactly what to implement. Do not change
 * method signatures or add external libraries — only SLF4J and Logback are needed.
 * Work with the {@link LoggerContext} that is passed in; do not create your own.
 */
public class LogbackConfigurationTasks {

    // ── Task 1 ─────────────────────────────────────────────────────────────────

    /**
     * Sets the level of the logger called {@code loggerName} to the level named by
     * {@code levelName} (case-insensitive, e.g. {@code "warn"} or {@code "WARN"}).
     *
     * <p>An unknown level name must throw {@link IllegalArgumentException} and must
     * <em>not</em> change the logger. (Do not let it silently fall back to DEBUG.)
     *
     * <p>Examples:
     * <pre>
     *   setLevel(ctx, "com.acme",       "debug")  → logger "com.acme" now has level DEBUG
     *   setLevel(ctx, "org.hibernate",  "WARN")   → logger "org.hibernate" now has level WARN
     *   setLevel(ctx, "com.acme",       "LOUD")   → throws IllegalArgumentException
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@link LoggerContext#getLogger(String)} finds or creates a logger by name.</li>
     *   <li>Read the demo's "Level parsing" section — one parsing method lets you detect bad input.</li>
     * </ul>
     *
     * @param context    the Logback logger context
     * @param loggerName name of the logger to configure
     * @param levelName  name of the level, any case
     * @throws IllegalArgumentException if {@code levelName} is not a valid level
     */
    public void setLevel(LoggerContext context, String loggerName, String levelName) {
        throw new UnsupportedOperationException("TODO: implement setLevel");
    }

    // ── Task 2 ─────────────────────────────────────────────────────────────────

    /**
     * Creates an in-memory {@link ListAppender}, makes it ready to receive events, attaches
     * it to the logger called {@code loggerName} and returns it. Events logged to that logger
     * (or to its children) end up in the appender's {@code list}.
     *
     * <p>The returned appender must be <em>started</em> and must belong to {@code context}.
     *
     * <p>Examples:
     * <pre>
     *   app = attachListAppender(ctx, "app.service");
     *   ctx.getLogger("app.service").info("hi");       → app.list contains one event, message "hi"
     *   ctx.getLogger("app.service.db").info("deep");  → app.list now contains two events
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>An appender that has not been started silently ignores every event.</li>
     *   <li>Appenders need to know which context they live in — see the demo's {@code attach} helper.</li>
     * </ul>
     *
     * @param context    the Logback logger context
     * @param loggerName name of the logger to attach to
     * @return the started appender, already attached
     */
    public ListAppender<ILoggingEvent> attachListAppender(LoggerContext context, String loggerName) {
        throw new UnsupportedOperationException("TODO: implement attachListAppender");
    }

    // ── Task 3 ─────────────────────────────────────────────────────────────────

    /**
     * Formats {@code event} as text using the given Logback pattern and returns the result.
     *
     * <p>Examples (event: INFO from logger {@code "app"} with message {@code "hello"}):
     * <pre>
     *   formatWithPattern(ctx, event, "%level|%logger|%msg")  → "INFO|app|hello"
     *   formatWithPattern(ctx, event, "%-5level %msg")        → "INFO  hello"
     *   formatWithPattern(ctx, event, "%msg")                 → "hello"
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>See the demo's "Pattern layout" section — it does exactly this once.</li>
     *   <li>Layouts, like appenders, need a context and must be started before use.</li>
     * </ul>
     *
     * @param context the Logback logger context
     * @param event   the event to format
     * @param pattern a Logback pattern, e.g. {@code "%level %msg"}
     * @return the formatted event
     */
    public String formatWithPattern(LoggerContext context, ILoggingEvent event, String pattern) {
        throw new UnsupportedOperationException("TODO: implement formatWithPattern");
    }

    // ── Task 4 ─────────────────────────────────────────────────────────────────

    /**
     * Makes {@code appender} accept only events at {@code levelName} or above
     * (case-insensitive name, e.g. {@code "warn"}). Events below the level must no longer
     * reach the appender; events at or above it must keep reaching it.
     *
     * <p>Examples (threshold {@code "WARN"}):
     * <pre>
     *   DEBUG event → rejected
     *   INFO  event → rejected
     *   WARN  event → accepted
     *   ERROR event → accepted
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Logback ships a ready-made filter class for exactly this — see the demo's last section.</li>
     *   <li>A filter that was never started has no effect at all.</li>
     * </ul>
     *
     * @param appender  the appender to restrict
     * @param levelName the minimum level, any case
     */
    public void addThreshold(Appender<ILoggingEvent> appender, String levelName) {
        throw new UnsupportedOperationException("TODO: implement addThreshold");
    }

    // ── Task 5 ─────────────────────────────────────────────────────────────────

    /**
     * Attaches {@code appender} to the logger called {@code loggerName} and stops that
     * logger's events from also reaching its ancestors' appenders. After this call, events
     * logged to the logger (and to its children) reach {@code appender}, but not the
     * appenders of the logger's parents or of the root logger.
     *
     * <p>Examples:
     * <pre>
     *   isolateLogger(ctx, "app.audit", auditAppender);
     *   ctx.getLogger("app.audit").info("x");  → auditAppender gets it; the root CONSOLE does NOT
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Two calls on the logger are needed — one for "where", one for "how far it travels".</li>
     * </ul>
     *
     * @param context    the Logback logger context
     * @param loggerName name of the logger to isolate
     * @param appender   the appender that should receive the logger's events
     */
    public void isolateLogger(LoggerContext context, String loggerName, Appender<ILoggingEvent> appender) {
        throw new UnsupportedOperationException("TODO: implement isolateLogger");
    }
}
