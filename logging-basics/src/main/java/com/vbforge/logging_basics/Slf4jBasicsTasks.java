package com.vbforge.logging_basics;

import org.slf4j.Logger;

import java.util.function.Supplier;

/**
 * Slf4jBasicsTasks — implement every method below to make {@code Slf4jBasicsTasksTest} go GREEN.
 *
 * <p><b>Workflow:</b>
 * <ol>
 *   <li>Read {@code Slf4jBasicsDemo.java} to understand the relevant concept.</li>
 *   <li>Implement the method — remove the {@code UnsupportedOperationException}.</li>
 *   <li>Run {@code mvn test -Dtest=Slf4jBasicsTasksTest} and watch it turn GREEN ✅</li>
 * </ol>
 *
 * <p>Each method has a Javadoc describing exactly what to implement. Do not change
 * method signatures or add external libraries — only SLF4J is needed. Use the
 * {@link Logger} that is passed in; do not create your own.
 */
public class Slf4jBasicsTasks {

    // ── Task 1 ─────────────────────────────────────────────────────────────────

    /**
     * Logs a successful login at INFO level with the message
     * {@code "User <user> logged in after <attempts> attempt(s)"}.
     * The user and the attempt count must be passed as <em>placeholder arguments</em>,
     * not glued into the string.
     *
     * <p>Examples:
     * <pre>
     *   logLogin(log, "alice", 3)  → INFO  "User alice logged in after 3 attempt(s)"
     *   logLogin(log, "bob",   1)  → INFO  "User bob logged in after 1 attempt(s)"
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>The template string should stay constant: {@code "User {} logged in after {} attempt(s)"}.</li>
     *   <li>Exactly one log event must be produced.</li>
     * </ul>
     *
     * @param log      the logger to write to
     * @param user     the user name
     * @param attempts how many attempts the login took
     */
    public void logLogin(Logger log, String user, int attempts) {
        throw new UnsupportedOperationException("TODO: implement logLogin");
    }

    // ── Task 2 ─────────────────────────────────────────────────────────────────

    /**
     * Logs a failed operation at ERROR level with the message
     * {@code "Operation <operation> failed"} <strong>and</strong> the exception's
     * stack trace attached to the log event.
     *
     * <p>The exception's own message must <em>not</em> appear in the log message text —
     * it travels with the attached exception instead.
     *
     * <p>Examples:
     * <pre>
     *   logFailure(log, "save", new IllegalStateException("db down"))
     *       → ERROR "Operation save failed"   (+ IllegalStateException attached)
     *
     *   logFailure(log, "load", new RuntimeException())
     *       → ERROR "Operation load failed"   (+ RuntimeException attached)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>SLF4J treats a trailing {@link Throwable} argument specially.</li>
     *   <li>Only one placeholder is needed — the exception does not get one.</li>
     * </ul>
     *
     * @param log       the logger to write to
     * @param operation a short name of the operation that failed
     * @param e         the exception to attach
     */
    public void logFailure(Logger log, String operation, Exception e) {
        throw new UnsupportedOperationException("TODO: implement logFailure");
    }

    // ── Task 3 ─────────────────────────────────────────────────────────────────

    /**
     * Logs the String produced by {@code expensive} at DEBUG level — but calls
     * {@code expensive.get()} <strong>only if DEBUG is enabled</strong>.
     * Returns {@code true} if something was logged, {@code false} otherwise.
     *
     * <p>The logged message is exactly the supplied String.
     *
     * <p>Examples:
     * <pre>
     *   logger at INFO  :  logExpensiveIfDebug(log, () -> "dump")  → false  (supplier NOT called, nothing logged)
     *   logger at DEBUG :  logExpensiveIfDebug(log, () -> "dump")  → true   (DEBUG "dump")
     *   logger at TRACE :  logExpensiveIfDebug(log, () -> "dump")  → true   (TRACE includes DEBUG)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Ask the logger whether the level is enabled before touching the supplier.</li>
     *   <li>The SLF4J 2.x fluent API ({@code atDebug()}) is an alternative, but you still
     *       need to know whether a line was logged to compute the return value.</li>
     * </ul>
     *
     * @param log       the logger to write to
     * @param expensive supplies the (costly) message text
     * @return {@code true} if a DEBUG line was written
     */
    public boolean logExpensiveIfDebug(Logger log, Supplier<String> expensive) {
        throw new UnsupportedOperationException("TODO: implement logExpensiveIfDebug");
    }

    // ── Task 4 ─────────────────────────────────────────────────────────────────

    /**
     * Logs {@code message} at the level named by {@code levelName}
     * (one of {@code TRACE, DEBUG, INFO, WARN, ERROR}, case-insensitive).
     * An unknown level name must throw {@link IllegalArgumentException} and log nothing.
     *
     * <p>Examples:
     * <pre>
     *   logAtLevel(log, "WARN",  "disk almost full")  → WARN  "disk almost full"
     *   logAtLevel(log, "debug", "cache miss")        → DEBUG "cache miss"
     *   logAtLevel(log, "LOUD",  "hello")             → throws IllegalArgumentException
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>SLF4J has no {@code log(level, msg)} method on {@link Logger}, but it does have
     *       an enum for levels: {@link org.slf4j.event.Level}.</li>
     *   <li>Look at the fluent API method that takes such a level (it starts with {@code at}).</li>
     *   <li>Remember to normalise the case before looking the level up.</li>
     * </ul>
     *
     * @param log       the logger to write to
     * @param levelName name of the level, any case
     * @param message   the message to log
     * @throws IllegalArgumentException if {@code levelName} is not a valid level
     */
    public void logAtLevel(Logger log, String levelName, String message) {
        throw new UnsupportedOperationException("TODO: implement logAtLevel");
    }
}
