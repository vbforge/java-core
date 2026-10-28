package com.vbforge.logging_basics;

import org.slf4j.Logger;

import java.util.Map;

/**
 * StructuredLoggingTasks — implement every method below to make
 * {@code StructuredLoggingTasksTest} go GREEN.
 *
 * <p><b>Workflow:</b>
 * <ol>
 *   <li>Read {@code StructuredLoggingDemo.java} to understand the relevant concept.</li>
 *   <li>Implement the method — remove the {@code UnsupportedOperationException}.</li>
 *   <li>Run {@code mvn test -Dtest=StructuredLoggingTasksTest} and watch it turn GREEN ✅</li>
 * </ol>
 *
 * <p>Each method has a Javadoc describing exactly what to implement. Do not change
 * method signatures or add external libraries — only SLF4J and the Java standard library
 * are needed. Use the {@link Logger} that is passed in; do not create your own.
 */
public class StructuredLoggingTasks {

    // ── Task 1 ─────────────────────────────────────────────────────────────────

    /**
     * Runs {@code action} with {@code requestId} and {@code userId} available in the MDC under
     * the keys {@code "requestId"} and {@code "userId"}. When the action is finished the two
     * keys must be gone from the MDC again — <strong>also if the action throws</strong>
     * (the exception must still propagate to the caller).
     *
     * <p>Examples:
     * <pre>
     *   runWithContext("req-1", "u-7", () -> log.info("x"))
     *       → while the action runs: MDC requestId=req-1, userId=u-7
     *       → afterwards:            MDC.get("requestId") == null, MDC.get("userId") == null
     *
     *   runWithContext("req-1", "u-7", () -> { throw new IllegalStateException("boom"); })
     *       → throws IllegalStateException("boom"), and the MDC is clean afterwards
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Cleanup that must always happen belongs in a {@code finally} block —
     *       or use the closeable MDC variant from the demo.</li>
     *   <li>The action must run exactly once.</li>
     * </ul>
     *
     * @param requestId the request identifier
     * @param userId    the user identifier
     * @param action    the work to run with the context in place
     */
    public void runWithContext(String requestId, String userId, Runnable action) {
        throw new UnsupportedOperationException("TODO: implement runWithContext");
    }

    // ── Task 2 ─────────────────────────────────────────────────────────────────

    /**
     * Renders {@code fields} in <em>logfmt</em> style: {@code key=value} pairs separated by one
     * space, in the iteration order of the map.
     *
     * <p>Value rules:
     * <ul>
     *   <li>A value is converted with {@code String.valueOf} — so {@code null} becomes {@code null}.</li>
     *   <li>A value that is empty, or contains a space, an {@code =} sign or a double quote,
     *       is wrapped in double quotes, and every double quote inside it is escaped with a backslash.</li>
     *   <li>Any other value is written as is.</li>
     * </ul>
     * Keys are written as is. An empty map gives an empty String.
     *
     * <p>Examples:
     * <pre>
     *   {user=alice, count=3}        → user=alice count=3
     *   {msg=hello world}            → msg="hello world"
     *   {quote=say "hi"}             → quote="say \"hi\""
     *   {expr=a=b}                   → expr="a=b"
     *   {name=}                      → name=""
     *   {missing=null}               → missing=null     (the value is the Java null)
     *   {}                           → (empty String)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@link StringBuilder} plus a loop over {@code Map.entrySet()}.</li>
     *   <li>Decide "needs quoting?" in one place, so the rules stay readable.</li>
     * </ul>
     *
     * @param fields the fields to render (iteration order is preserved)
     * @return the logfmt line
     */
    public String toLogfmt(Map<String, ?> fields) {
        throw new UnsupportedOperationException("TODO: implement toLogfmt");
    }

    // ── Task 3 ─────────────────────────────────────────────────────────────────

    /**
     * Logs a placed order at INFO level as a <em>structured</em> event: the message is the constant
     * text {@code "Order placed"}, and the two facts travel as key-value pairs named
     * {@code "orderId"} and {@code "itemCount"} (in that order).
     *
     * <p>The message text itself must not contain the order id or the item count.
     *
     * <p>Examples:
     * <pre>
     *   logOrderPlaced(log, "A-100", 3)
     *       → INFO  "Order placed"   with orderId=A-100, itemCount=3
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>This is the SLF4J 2.x fluent API: start with the method that begins an INFO event
     *       and add one key-value pair per fact before the final {@code log(...)} call.</li>
     *   <li>Exactly one log event must be produced.</li>
     * </ul>
     *
     * @param log       the logger to write to
     * @param orderId   the order id
     * @param itemCount the number of items in the order
     */
    public void logOrderPlaced(Logger log, String orderId, int itemCount) {
        throw new UnsupportedOperationException("TODO: implement logOrderPlaced");
    }

    // ── Task 4 ─────────────────────────────────────────────────────────────────

    /**
     * Escapes {@code raw} so it can be placed between double quotes in a JSON document.
     *
     * <p>Rules:
     * <ul>
     *   <li>{@code "} becomes backslash + {@code "}</li>
     *   <li>a backslash becomes two backslashes</li>
     *   <li>newline, carriage return and tab become {@code \n}, {@code \r} and {@code \t}</li>
     *   <li>any other character below U+0020 becomes backslash, {@code u}, then its code as
     *       four lower-case hexadecimal digits (U+0001 becomes backslash-u-0001)</li>
     *   <li>everything else is copied unchanged</li>
     * </ul>
     * {@code null} gives an empty String. The surrounding quotes are <em>not</em> added.
     *
     * <p>Examples:
     * <pre>
     *   escapeJson("plain")             → plain
     *   escapeJson("say \"hi\"")        → say \"hi\"
     *   escapeJson("C:\\temp")          → C:\\temp           (each backslash doubled)
     *   escapeJson("line1\nline2")      → line1\nline2       (a backslash and an n, not a real newline)
     *   escapeJson("tab\there")         → tab\there
     *   escapeJson("bell: " + (char) 7) → bell: followed by backslash-u-0007
     *   escapeJson("")                  → (empty String)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Walk the characters once and append to a {@link StringBuilder}.</li>
     *   <li>{@code String.format("%04x", ...)} gives zero-padded lower-case hex.</li>
     * </ul>
     *
     * @param raw the text to escape (may be {@code null})
     * @return the escaped text, without surrounding quotes
     */
    public String escapeJson(String raw) {
        throw new UnsupportedOperationException("TODO: implement escapeJson");
    }

    // ── Task 5 ─────────────────────────────────────────────────────────────────

    /**
     * Writes an audit entry at INFO level, tagged with the marker named {@code "AUDIT"}, with the
     * message {@code "User <user> performed <action>"}. The user and the action must be passed as
     * placeholder arguments.
     *
     * <p>Examples:
     * <pre>
     *   logAudit(log, "delete", "alice")  → INFO [AUDIT] "User alice performed delete"
     *   logAudit(log, "login",  "bob")    → INFO [AUDIT] "User bob performed login"
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Markers come from {@code org.slf4j.MarkerFactory}.</li>
     *   <li>Most {@code Logger} methods have an overload whose first parameter is a marker.</li>
     *   <li>Mind the parameter order of this method: action first, user second.</li>
     * </ul>
     *
     * @param log    the logger to write to
     * @param action what was done
     * @param user   who did it
     */
    public void logAudit(Logger log, String action, String user) {
        throw new UnsupportedOperationException("TODO: implement logAudit");
    }
}
