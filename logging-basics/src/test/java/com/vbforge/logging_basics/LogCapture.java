package com.vbforge.logging_basics;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * LogCapture — a tiny test helper that records everything a logger emits.
 *
 * <p>It attaches an in-memory {@link ListAppender} to the named logger, switches
 * the logger to TRACE (so nothing is filtered unless a test asks for it), and
 * turns additivity off (so test output does not spill to the console).
 * {@link #close()} restores the logger to exactly how it was found.
 *
 * <pre>
 *   try (LogCapture capture = LogCapture.of("my.logger")) {
 *       capture.logger().info("hello {}", "world");
 *       assertThat(capture.messages()).containsExactly("hello world");
 *   }
 * </pre>
 */
final class LogCapture implements AutoCloseable {

    private final Logger logger;
    // Logback reads some event data lazily (notably the MDC map). prepareForDeferredProcessing()
    // snapshots it at append time — exactly what AsyncAppender does — so tests can inspect an
    // event long after the MDC has been cleared.
    private final ListAppender<ILoggingEvent> appender = new ListAppender<ILoggingEvent>() {
        @Override
        protected void append(ILoggingEvent event) {
            event.prepareForDeferredProcessing();
            super.append(event);
        }
    };
    private final Level previousLevel;
    private final boolean previousAdditive;

    private LogCapture(Logger logger) {
        this.logger = logger;
        this.previousLevel = logger.getLevel();
        this.previousAdditive = logger.isAdditive();

        appender.setContext(logger.getLoggerContext());
        appender.start();
        logger.addAppender(appender);
        logger.setLevel(Level.TRACE);
        logger.setAdditive(false);
    }

    static LogCapture of(String loggerName) {
        return new LogCapture((Logger) LoggerFactory.getLogger(loggerName));
    }

    static LogCapture of(Class<?> type) {
        return of(type.getName());
    }

    /** The captured logger — it is an SLF4J {@link org.slf4j.Logger}, so it can be passed to task methods. */
    Logger logger() {
        return logger;
    }

    /** Overrides the logger level for the duration of the capture. */
    LogCapture level(Level level) {
        logger.setLevel(level);
        return this;
    }

    List<ILoggingEvent> events() {
        return appender.list;
    }

    /** Formatted messages ({@code {}} placeholders already substituted), in order. */
    List<String> messages() {
        List<String> result = new ArrayList<>();
        for (ILoggingEvent event : appender.list) {
            result.add(event.getFormattedMessage());
        }
        return result;
    }

    /** The one and only captured event — fails loudly if there are zero or several. */
    ILoggingEvent single() {
        if (appender.list.size() != 1) {
            throw new AssertionError("Expected exactly 1 log event but found " + appender.list.size()
                    + ": " + messages());
        }
        return appender.list.get(0);
    }

    @Override
    public void close() {
        logger.detachAppender(appender);
        appender.stop();
        logger.setLevel(previousLevel);
        logger.setAdditive(previousAdditive);
    }
}
