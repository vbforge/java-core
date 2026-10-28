package com.vbforge.logging_basics;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.JsonEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * StructuredLoggingDemo — annotated reference for logging <em>data</em> instead of prose.
 *
 * <p>A plain line such as {@code "User alice paid 42 EUR for order A-100"} is easy to read but
 * hard to query. <b>Structured logging</b> keeps the facts (user, amount, order) as separate
 * fields so a log platform can filter, group and graph them. SLF4J/Logback give you four tools:
 *
 * <ol>
 *   <li>MDC — context that is attached to <em>every</em> line logged by the current thread</li>
 *   <li>MDC cleanup — why it must be removed again, and how to make that automatic</li>
 *   <li>Key-value pairs — fields attached to <em>one</em> event (SLF4J 2.x fluent API)</li>
 *   <li>JSON encoding — turning events into one JSON object per line</li>
 *   <li>Markers — tags that classify events (audit, security, ...)</li>
 * </ol>
 */
public class StructuredLoggingDemo {

    private static final Logger log = LoggerFactory.getLogger(StructuredLoggingDemo.class);

    public static void main(String[] args) throws Exception {
        mdcDemo();
        System.out.println("---");
        mdcCleanupDemo();
        System.out.println("---");
        keyValueDemo();
        System.out.println("---");
        jsonDemo();
        System.out.println("---");
        markerDemo();
    }

    // ── 1. MDC ────────────────────────────────────────────────────────────────

    /**
     * The <b>Mapped Diagnostic Context</b> is a per-thread {@code Map<String, String>}. Whatever
     * you put there is available to every log event the thread produces, without passing it
     * to each log call.
     *
     * <p>WHY this matters: in a busy service a hundred requests interleave in one log file.
     * Put a {@code requestId} into the MDC at the entrance of the request, add
     * {@code %X{requestId}} to the pattern (or use a JSON encoder), and you can pull out all
     * lines of one request with a single search — no matter which class wrote them.
     *
     * <p>Common gotcha: the MDC belongs to <em>one thread</em>. Work handed to another
     * thread (a pool, an async callback) does not see your values unless you copy them across.
     */
    static void mdcDemo() throws Exception {
        System.out.println("=== MDC ===");

        // A worker thread that exists BEFORE we touch the MDC, like a thread in a pool
        ExecutorService pool = Executors.newSingleThreadExecutor();
        try {
            pool.submit(() -> { }).get();

            MDC.put("requestId", "req-42");
            MDC.put("userId", "alice");
            log.info("handling request");                                    // carries requestId and userId

            System.out.println("requestId        : " + MDC.get("requestId")); // req-42
            MDC.remove("userId");
            System.out.println("userId after rm  : " + MDC.get("userId"));    // null

            String seenByWorker = pool.submit(() -> MDC.get("requestId")).get();
            System.out.println("worker thread    : " + seenByWorker);         // null — the MDC is per thread
        } finally {
            pool.shutdown();
            MDC.clear();                                                      // always clean up
        }
    }

    // ── 2. MDC cleanup ────────────────────────────────────────────────────────

    /**
     * Whatever you put into the MDC stays there until somebody removes it.
     *
     * <p>WHY this matters: servers reuse threads. If request 1 forgets to clean up, request 2
     * running on the same thread logs with request 1's {@code userId} — wrong data in your logs
     * that looks perfectly plausible. Always remove in a {@code finally} block, or let
     * {@link MDC#putCloseable(String, String)} do it for you in try-with-resources.
     *
     * <p>Common gotcha: {@code MDC.put} inside a method that can throw, with the cleanup
     * placed after the risky code instead of in {@code finally}.
     */
    static void mdcCleanupDemo() {
        System.out.println("=== MDC cleanup ===");

        try (MDC.MDCCloseable ignored = MDC.putCloseable("orderId", "A-100")) {
            log.info("processing order");
            System.out.println("inside block     : " + MDC.get("orderId"));   // A-100
        }
        System.out.println("after block      : " + MDC.get("orderId"));       // null — removed automatically

        MDC.put("orderId", "A-101");
        try {
            log.info("processing another order");
        } finally {
            MDC.remove("orderId");                                            // the classic manual form
        }
        System.out.println("after finally    : " + MDC.get("orderId"));       // null
    }

    // ── 3. Key-value pairs ────────────────────────────────────────────────────

    /**
     * Key-value pairs attach named fields to a <em>single</em> event. They need SLF4J 2.x and
     * Logback 1.3+.
     *
     * <p>WHY this matters: MDC is for context that spans many lines; key-values are for the
     * facts of one line. Compared with baking values into the message, the message stays
     * constant ("Order placed") — easy to group and count — while the data remains
     * individually searchable. The {@code %kvp} pattern word (or a JSON encoder) prints them.
     *
     * <p>Common gotcha: the older {@code key=value} text style ({@code log.info("order={}", id)})
     * still works and is fine for human-read logs, but a machine sees only one opaque string.
     */
    static void keyValueDemo() {
        System.out.println("=== Key-value pairs ===");

        // Old style: facts live inside the message text
        log.info("Order placed orderId={} items={}", "A-100", 3);

        // Structured style: constant message + named fields
        log.atInfo()
           .addKeyValue("orderId", "A-100")
           .addKeyValue("items", 3)
           .log("Order placed");
    }

    // ── 4. JSON encoding ──────────────────────────────────────────────────────

    /**
     * Logback ships a {@code JsonEncoder} that renders each event as one JSON object — message,
     * level, logger, thread, MDC and key-value pairs included.
     *
     * <p>WHY this matters: log platforms (ELK, Loki, Datadog, CloudWatch) parse JSON natively, so
     * fields become searchable without fragile regular expressions. In {@code logback.xml} it
     * is a one-liner:
     * <pre>
     *   &lt;appender name="JSON" class="ch.qos.logback.core.ConsoleAppender"&gt;
     *       &lt;encoder class="ch.qos.logback.classic.encoder.JsonEncoder"/&gt;
     *   &lt;/appender&gt;
     * </pre>
     * Here we drive the encoder by hand to see its output.
     *
     * <p>Common gotcha: JSON suits machines, not people reading a terminal. Keep a readable
     * pattern encoder for local development and use JSON in the deployed environments.
     */
    static void jsonDemo() {
        System.out.println("=== JSON encoding ===");

        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        ch.qos.logback.classic.Logger logger =
                (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("demo.structured.json");
        logger.setAdditive(false);

        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.setContext(context);
        appender.start();
        logger.addAppender(appender);

        JsonEncoder encoder = new JsonEncoder();
        encoder.setContext(context);
        encoder.start();

        MDC.put("requestId", "req-42");
        try {
            logger.atInfo().addKeyValue("orderId", "A-100").log("Order placed");

            // Encode while the MDC is still populated: the event reads it lazily
            byte[] bytes = encoder.encode(appender.list.get(0));
            System.out.println(new String(bytes, StandardCharsets.UTF_8).trim());
        } finally {
            MDC.clear();
            logger.detachAndStopAllAppenders();
            logger.setAdditive(true);
        }
    }

    // ── 5. Markers ────────────────────────────────────────────────────────────

    /**
     * A {@link Marker} is a named tag you can attach to an event. Markers are singletons:
     * {@code MarkerFactory.getMarker("AUDIT")} always returns the same instance.
     *
     * <p>WHY this matters: levels say <em>how severe</em> an event is; markers say <em>what kind</em>
     * it is. Logback can route on them — for example send everything tagged AUDIT to a separate,
     * long-retention file — without the calling code knowing anything about that.
     *
     * <p>Common gotcha: inventing a new marker name per call site. Keep markers few,
     * constant and documented, otherwise nobody can write a filter for them.
     */
    static void markerDemo() {
        System.out.println("=== Markers ===");

        Marker audit = MarkerFactory.getMarker("AUDIT");
        System.out.println("same instance    : " + (audit == MarkerFactory.getMarker("AUDIT"))); // true

        log.info(audit, "User {} deleted account {}", "alice", 42);          // classic API
        log.atInfo().addMarker(audit).log("fluent variant of the same idea"); // fluent API
    }
}
