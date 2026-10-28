package com.vbforge.logging_basics;

import org.slf4j.Logger;

/**
 * LoggingBestPracticesTasks — implement every method below to make
 * {@code LoggingBestPracticesTasksTest} go GREEN.
 *
 * <p><b>Workflow:</b>
 * <ol>
 *   <li>Read {@code LoggingBestPracticesDemo.java} to understand the relevant concept.</li>
 *   <li>Implement the method — remove the {@code UnsupportedOperationException}.</li>
 *   <li>Run {@code mvn test -Dtest=LoggingBestPracticesTasksTest} and watch it turn GREEN ✅</li>
 * </ol>
 *
 * <p>Each method has a Javadoc describing exactly what to implement. Do not change
 * method signatures or add external libraries — only SLF4J and the Java standard library
 * are needed. Use the {@link Logger} that is passed in; do not create your own.
 */
public class LoggingBestPracticesTasks {

    // ── Task 1 ─────────────────────────────────────────────────────────────────

    /**
     * Masks a card number for safe logging: every character except the <em>last four</em> is
     * replaced by {@code '*'}. The result has the same length as the input. A card number of
     * four characters or fewer is masked completely. {@code null} gives {@code null}.
     *
     * <p>Examples:
     * <pre>
     *   maskCardNumber("4111111111111111")  → "************1111"
     *   maskCardNumber("12345")             → "*2345"
     *   maskCardNumber("1234")              → "****"
     *   maskCardNumber("12")                → "**"
     *   maskCardNumber("")                  → ""
     *   maskCardNumber(null)                → null
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Work out how many characters to hide first; {@link Math#max(int, int)} helps with short inputs.</li>
     *   <li>{@link String#repeat(int)} needs Java 11 — a {@link StringBuilder} loop works everywhere.</li>
     * </ul>
     *
     * @param cardNumber the card number (may be {@code null})
     * @return the masked card number
     */
    public String maskCardNumber(String cardNumber) {
        throw new UnsupportedOperationException("TODO: implement maskCardNumber");
    }

    // ── Task 2 ─────────────────────────────────────────────────────────────────

    /**
     * Masks an e-mail address for safe logging: keep the first character of the part before the
     * {@code '@'}, replace the rest of that part with {@code '*'} (one per character), and keep
     * the {@code '@'} and the domain unchanged. A local part of a single character becomes a
     * single {@code '*'}.
     *
     * <p>If the input is {@code null}, empty, or contains no {@code '@'}, return {@code "***"}.
     *
     * <p>Examples:
     * <pre>
     *   maskEmail("john.doe@example.com")  → "j*******@example.com"
     *   maskEmail("ab@x.org")              → "a*@x.org"
     *   maskEmail("a@x.org")               → "*@x.org"
     *   maskEmail("not-an-email")          → "***"
     *   maskEmail("")                      → "***"
     *   maskEmail(null)                    → "***"
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@link String#indexOf(int)} finds the {@code '@'}; {@link String#substring(int)} splits around it.</li>
     *   <li>Handle the "no usable input" cases first, then the single-character local part.</li>
     * </ul>
     *
     * @param email the e-mail address (may be {@code null})
     * @return the masked address
     */
    public String maskEmail(String email) {
        throw new UnsupportedOperationException("TODO: implement maskEmail");
    }

    // ── Task 3 ─────────────────────────────────────────────────────────────────

    /**
     * Replaces the value of every secret-looking {@code key=value} pair in {@code text} with
     * {@code ***}. The secret keys are {@code password}, {@code pwd}, {@code secret} and
     * {@code token}. A <em>value</em> is the run of non-whitespace characters after the {@code '='}.
     * Key matching is case-insensitive, and the key keeps its original spelling in the output.
     * Everything else in the text is left as it is. {@code null} gives {@code null}.
     *
     * <p>Examples:
     * <pre>
     *   redactSecrets("user=bob password=hunter2")           → "user=bob password=***"
     *   redactSecrets("TOKEN=abc123 other=1")                → "TOKEN=*** other=1"
     *   redactSecrets("pwd=1 secret=2")                      → "pwd=*** secret=***"
     *   redactSecrets("login ok")                            → "login ok"
     *   redactSecrets(null)                                  → null
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>A regular expression with a capturing group for the key lets the replacement reuse it.</li>
     *   <li>{@link String#replaceAll(String, String)} understands {@code $1} in the replacement.</li>
     *   <li>Case-insensitivity is available as an inline flag at the start of the regex.</li>
     * </ul>
     *
     * @param text the text to clean (may be {@code null})
     * @return the text with secret values replaced by {@code ***}
     */
    public String redactSecrets(String text) {
        throw new UnsupportedOperationException("TODO: implement redactSecrets");
    }

    // ── Task 4 ─────────────────────────────────────────────────────────────────

    /**
     * Logs progress of a long-running job <em>sparingly</em>: writes one INFO line
     * {@code "Processed <processed>/<total> items"} when {@code processed} is a multiple of
     * {@code every}, or when {@code processed == total}; otherwise logs nothing. A call
     * satisfying both conditions still logs only one line.
     *
     * <p>The numbers must be passed as placeholder arguments. If {@code every} is zero or negative,
     * throw {@link IllegalArgumentException} and log nothing.
     *
     * <p>Examples (called for processed = 1, 2, 3, ... total):
     * <pre>
     *   total=10, every=5   → logs at 5 and 10:        "Processed 5/10 items", "Processed 10/10 items"
     *   total=7,  every=5   → logs at 5 and 7:         "Processed 5/7 items",   "Processed 7/7 items"
     *   total=10, every=3   → logs at 3, 6, 9 and 10
     *   total=3,  every=1   → logs at 1, 2 and 3
     *   every=0             → throws IllegalArgumentException
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>The remainder operator {@code %} tells you whether a number is a multiple of another.</li>
     *   <li>Validate the argument before anything is logged.</li>
     * </ul>
     *
     * @param log       the logger to write to
     * @param processed how many items are done so far (1-based)
     * @param total     how many items there are in total
     * @param every     log once per this many items
     * @throws IllegalArgumentException if {@code every} is less than 1
     */
    public void logProgress(Logger log, int processed, int total, int every) {
        throw new UnsupportedOperationException("TODO: implement logProgress");
    }

    // ── Task 5 ─────────────────────────────────────────────────────────────────

    /**
     * The one place that logs a failure: runs {@code work} and reports how it went.
     * <ul>
     *   <li>If {@code work} completes normally: returns {@code true} and logs <strong>nothing</strong>.</li>
     *   <li>If {@code work} throws a {@link RuntimeException}: logs exactly <strong>one</strong> ERROR
     *       event with the message {@code "Unhandled error in request"} and the exception attached,
     *       then returns {@code false}. The exception is <em>not</em> rethrown.</li>
     * </ul>
     * {@code work} runs exactly once.
     *
     * <p>Examples:
     * <pre>
     *   handleAtBoundary(log, () -> { })                              → true,  nothing logged
     *   handleAtBoundary(log, () -> { throw new IllegalStateException("db down"); })
     *       → false, ERROR "Unhandled error in request" (+ IllegalStateException attached)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Remember what the demo's "Log or throw" section says about the exception itself —
     *       pass it so that the stack trace is kept.</li>
     * </ul>
     *
     * @param log  the logger to write to
     * @param work the work to run
     * @return {@code true} if the work completed normally, {@code false} if it failed
     */
    public boolean handleAtBoundary(Logger log, Runnable work) {
        throw new UnsupportedOperationException("TODO: implement handleAtBoundary");
    }
}
