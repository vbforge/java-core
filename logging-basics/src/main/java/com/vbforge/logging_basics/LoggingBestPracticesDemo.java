package com.vbforge.logging_basics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * LoggingBestPracticesDemo — annotated reference for habits that keep logs useful and safe.
 *
 * <p>Knowing the API is the easy half. Logs are read under pressure, by someone who did not
 * write the code, often during an incident. These habits decide whether the log helps them.
 *
 * <p>Topics:
 * <ol>
 *   <li>Choosing levels — and what production will actually show</li>
 *   <li>Sensitive data — never let secrets reach the log</li>
 *   <li>Log <em>or</em> throw — one failure, one log entry</li>
 *   <li>Summaries instead of floods — keep the signal visible</li>
 * </ol>
 */
public class LoggingBestPracticesDemo {

    private static final Logger log = LoggerFactory.getLogger(LoggingBestPracticesDemo.class);

    public static void main(String[] args) {
        levelGuidelinesDemo();
        System.out.println("---");
        sensitiveDataDemo();
        System.out.println("---");
        logOrThrowDemo();
        System.out.println("---");
        summaryLoggingDemo();
    }

    // ── 1. Choosing levels ────────────────────────────────────────────────────

    /**
     * Pick the level by <em>who must react</em>, not by how important the code feels.
     *
     * <ul>
     *   <li>ERROR — a human (or alert) must look: the operation failed and will not fix itself</li>
     *   <li>WARN  — something odd happened but we coped; worth a look if it keeps happening</li>
     *   <li>INFO  — the story of the system: one line per business milestone</li>
     *   <li>DEBUG — developer diagnostics; off in production, on when investigating</li>
     * </ul>
     *
     * <p>WHY this matters: production usually runs at INFO. Everything you want to see there must
     * be INFO or higher, and nothing at WARN/ERROR may be routine — otherwise alerts are ignored.
     *
     * <p>Common gotcha: logging expected conditions (a user mistyped a password, a search found
     * nothing) as ERROR. That is normal business, not a failure of the system.
     */
    static void levelGuidelinesDemo() {
        System.out.println("=== Choosing levels ===");

        log.debug("Cache lookup for key={}", "user:42");                       // developer detail
        log.info("Order {} placed", "A-100");                                  // business milestone
        log.warn("Payment provider slow ({} ms), retrying", 2300);             // coped, but odd
        log.error("Order {} could not be saved", "A-100",
                new IllegalStateException("db down"));                         // needs a human
    }

    // ── 2. Sensitive data ─────────────────────────────────────────────────────

    /**
     * Logs outlive the systems that wrote them: they are copied to log platforms, attached to
     * tickets and read by people who must never see a password or a card number.
     *
     * <p>WHY this matters: the usual leak is not a deliberate {@code log.info(password)} — it is
     * {@code log.info("Login attempt: {}", request)} where {@code request.toString()} happens to
     * print every field. Make safe output the <em>default</em>: give classes that hold secrets a
     * {@code toString()} that leaves them out, and mask what you must show.
     *
     * <p>Never log: passwords, tokens and API keys, full card numbers, national ids, health data.
     * Log an identifier instead (a user id, the last four digits).
     *
     * <p>Common gotcha: lowering the log level does not protect you — secrets in a DEBUG line
     * appear the day somebody switches DEBUG on to chase a bug.
     */
    static void sensitiveDataDemo() {
        System.out.println("=== Sensitive data ===");

        Credentials credentials = new Credentials("alice", "hunter2");
        log.info("Login attempt: {}", credentials);   // prints the user, never the password
    }

    /** A value object whose {@code toString()} is safe to log. */
    static final class Credentials {
        private final String user;
        private final String password;

        Credentials(String user, String password) {
            this.user = user;
            this.password = password;
        }

        @Override
        public String toString() {
            // The field `password` is deliberately NOT printed: the default toString() of many
            // classes would include it, and that is how secrets end up in logs.
            return "Credentials{user=" + user + ", password=***}";
        }
    }

    // ── 3. Log or throw ───────────────────────────────────────────────────────

    /**
     * Handle an exception in exactly one way: <b>either</b> log it <b>or</b> rethrow it —
     * not both. Add context when you rethrow, and let the layer that finally <em>handles</em> the
     * failure (the request boundary, the job runner) write the single log entry.
     *
     * <p>WHY this matters: with log-and-throw every layer on the way up logs the same failure
     * again. One incident becomes five stack traces, the error count is inflated five-fold, and
     * the reader cannot tell whether five things broke or one. Wrapping keeps the cause chain, so
     * the single log entry at the boundary still shows the whole story.
     *
     * <p>Common gotcha: wrapping without the cause — {@code throw new RuntimeException(e.getMessage())}
     * — destroys the original stack trace.
     */
    static void logOrThrowDemo() {
        System.out.println("=== Log or throw ===");

        requestWithLogAndThrow();
        requestWithBoundaryLogging();
    }

    /** ANTI-PATTERN: logs the failure, then throws it on. */
    static String loadSettingLogAndThrow(String path) {
        try {
            throw new IOException("disk unreadable");        // stands in for real I/O
        } catch (IOException e) {
            log.error("Cannot load {}", path, e);            // entry #1 ...
            throw new UncheckedIOException(e);               // ... and the caller gets it again
        }
    }

    /** The caller of the anti-pattern logs the same failure a second time. */
    static void requestWithLogAndThrow() {
        try {
            loadSettingLogAndThrow("app.yml");
        } catch (RuntimeException e) {
            log.error("Request failed", e);                  // entry #2 — the same failure
        }
    }

    /** GOOD: adds context, keeps the cause, does not log. */
    static String loadSettingWrapped(String path) {
        try {
            throw new IOException("disk unreadable");
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot load " + path, e);
        }
    }

    /** GOOD: the boundary logs the failure once; the cause chain tells the whole story. */
    static void requestWithBoundaryLogging() {
        try {
            loadSettingWrapped("app.yml");
        } catch (RuntimeException e) {
            log.error("Request failed", e);                  // the ONE entry
        }
    }

    // ── 4. Summaries instead of floods ────────────────────────────────────────

    /**
     * Do not write one INFO line per item of a big loop. Log what the <em>operation</em> did.
     *
     * <p>WHY this matters: a thousand "Imported row N" lines bury the one line that matters, cost
     * disk and money, and slow the loop. Keep per-item detail at TRACE (off by default) and emit
     * a single INFO summary with the totals.
     *
     * <p>Common gotcha: logging inside the innermost loop "just for now". It ships, and one day
     * production produces gigabytes per hour.
     */
    static void summaryLoggingDemo() {
        System.out.println("=== Summaries instead of floods ===");

        int imported = 0;
        int skipped = 0;
        for (int row = 1; row <= 1000; row++) {
            if (row % 333 == 0) {                            // rows 333, 666 and 999 are invalid
                skipped++;
                continue;
            }
            imported++;
            log.trace("Imported row {}", row);               // per-item detail: TRACE, off by default
        }
        log.info("Import finished: imported={} skipped={}", imported, skipped);
    }
}
