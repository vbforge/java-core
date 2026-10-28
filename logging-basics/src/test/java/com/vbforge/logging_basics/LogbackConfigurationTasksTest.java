package com.vbforge.logging_basics;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * LogbackConfigurationTasksTest — tests for every method in {@link LogbackConfigurationTasks}.
 *
 * <p><b>Getting started:</b>
 * <ol>
 *   <li>Open {@code LogbackConfigurationTasks.java} and implement a method.</li>
 *   <li>Remove the {@code @Disabled} annotation from the corresponding {@code @Nested} class.</li>
 *   <li>Run {@code mvn test -Dtest=LogbackConfigurationTasksTest} — those tests should go GREEN ✅</li>
 *   <li>Repeat until all 5 task groups are GREEN.</li>
 * </ol>
 */
@DisplayName("LogbackConfigurationTasks — your implementations")
class LogbackConfigurationTasksTest {

    private LogbackConfigurationTasks tasks;
    private LoggerContext ctx;

    @BeforeEach
    void setUp() {
        tasks = new LogbackConfigurationTasks();
        ctx = (LoggerContext) LoggerFactory.getILoggerFactory();
    }

    /** A started in-memory appender that is NOT attached to any logger yet. */
    private ListAppender<ILoggingEvent> newAppender() {
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.setContext(ctx);
        appender.start();
        return appender;
    }

    // ── Task 1: setLevel ──────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement setLevel()")
    @DisplayName("Task 1 — setLevel()")
    class SetLevelTest {

        @Test
        @DisplayName("\"debug\" on com.acme → that logger has level DEBUG")
        void javadocExample() {
            tasks.setLevel(ctx, "tasks.cfg.level.acme", "debug");

            assertThat(ctx.getLogger("tasks.cfg.level.acme").getLevel()).isEqualTo(Level.DEBUG);
        }

        @ParameterizedTest(name = "\"{0}\" → {1}")
        @CsvSource({
                "TRACE, TRACE",
                "DEBUG, DEBUG",
                "INFO,  INFO",
                "WARN,  WARN",
                "ERROR, ERROR",
                "warn,  WARN",
                "Error, ERROR",
        })
        @DisplayName("level names are case-insensitive")
        void levelNames(String name, String expected) {
            tasks.setLevel(ctx, "tasks.cfg.level.names", name);

            assertThat(ctx.getLogger("tasks.cfg.level.names").getLevel()).isEqualTo(Level.valueOf(expected));
        }

        @Test
        @DisplayName("children inherit the configured level")
        void childrenInherit() {
            tasks.setLevel(ctx, "tasks.cfg.level.parent", "WARN");

            assertThat(ctx.getLogger("tasks.cfg.level.parent.child").getEffectiveLevel()).isEqualTo(Level.WARN);
        }

        @Test
        @DisplayName("unknown level → IllegalArgumentException (no silent fallback to DEBUG)")
        void unknownLevelThrows() {
            assertThatThrownBy(() -> tasks.setLevel(ctx, "tasks.cfg.level.bad", "LOUD"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("unknown level leaves the logger unchanged")
        void unknownLevelDoesNotChangeLogger() {
            Logger logger = ctx.getLogger("tasks.cfg.level.unchanged");
            logger.setLevel(Level.ERROR);

            assertThatThrownBy(() -> tasks.setLevel(ctx, "tasks.cfg.level.unchanged", "WARNING"))
                    .isInstanceOf(IllegalArgumentException.class);

            assertThat(logger.getLevel()).isEqualTo(Level.ERROR);
        }
    }

    // ── Task 2: attachListAppender ────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement attachListAppender()")
    @DisplayName("Task 2 — attachListAppender()")
    class AttachListAppenderTest {

        @Test
        @DisplayName("events logged to the logger end up in the appender's list")
        void javadocExample() {
            ListAppender<ILoggingEvent> appender = tasks.attachListAppender(ctx, "tasks.cfg.attach.service");

            ctx.getLogger("tasks.cfg.attach.service").info("hi");

            assertThat(appender.list).extracting(ILoggingEvent::getFormattedMessage).containsExactly("hi");
        }

        @Test
        @DisplayName("events logged to a child logger reach the parent's appender too")
        void childEventsAreCaptured() {
            ListAppender<ILoggingEvent> appender = tasks.attachListAppender(ctx, "tasks.cfg.attach.parent");

            ctx.getLogger("tasks.cfg.attach.parent").info("one");
            ctx.getLogger("tasks.cfg.attach.parent.db").info("two");

            assertThat(appender.list).extracting(ILoggingEvent::getFormattedMessage).containsExactly("one", "two");
        }

        @Test
        @DisplayName("the returned appender is started, belongs to the context and is attached")
        void appenderState() {
            ListAppender<ILoggingEvent> appender = tasks.attachListAppender(ctx, "tasks.cfg.attach.state");

            assertThat(appender.isStarted()).isTrue();
            assertThat(appender.getContext()).isSameAs(ctx);
            assertThat(ctx.getLogger("tasks.cfg.attach.state").isAttached(appender)).isTrue();
        }

        @Test
        @DisplayName("each call creates a new, independent appender")
        void independentAppenders() {
            ListAppender<ILoggingEvent> first  = tasks.attachListAppender(ctx, "tasks.cfg.attach.first");
            ListAppender<ILoggingEvent> second = tasks.attachListAppender(ctx, "tasks.cfg.attach.second");

            assertThat(first).isNotSameAs(second);
            ctx.getLogger("tasks.cfg.attach.first").info("only first");
            assertThat(second.list).isEmpty();
        }
    }

    // ── Task 3: formatWithPattern ─────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement formatWithPattern()")
    @DisplayName("Task 3 — formatWithPattern()")
    class FormatWithPatternTest {

        /** INFO event from logger "app" with message "hello". */
        private ILoggingEvent helloEvent() {
            try (LogCapture capture = LogCapture.of("app")) {
                capture.logger().info("hello");
                return capture.single();
            }
        }

        @Test
        @DisplayName("\"%level|%logger|%msg\" → \"INFO|app|hello\"")
        void javadocExample() {
            assertThat(tasks.formatWithPattern(ctx, helloEvent(), "%level|%logger|%msg"))
                    .isEqualTo("INFO|app|hello");
        }

        @Test
        @DisplayName("\"%-5level %msg\" pads the level to 5 columns")
        void paddedLevel() {
            assertThat(tasks.formatWithPattern(ctx, helloEvent(), "%-5level %msg"))
                    .isEqualTo("INFO  hello");
        }

        @Test
        @DisplayName("\"%msg\" alone → just the message")
        void messageOnly() {
            assertThat(tasks.formatWithPattern(ctx, helloEvent(), "%msg")).isEqualTo("hello");
        }

        @Test
        @DisplayName("%msg shows the placeholder-substituted message")
        void formattedMessage() {
            ILoggingEvent event;
            try (LogCapture capture = LogCapture.of("tasks.cfg.format.args")) {
                capture.logger().warn("disk at {}%", 91);
                event = capture.single();
            }

            assertThat(tasks.formatWithPattern(ctx, event, "%level %msg")).isEqualTo("WARN disk at 91%");
        }
    }

    // ── Task 4: addThreshold ──────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement addThreshold()")
    @DisplayName("Task 4 — addThreshold()")
    class AddThresholdTest {

        /** Logger at DEBUG, not additive, with an appender that is restricted by the task under test. */
        private ListAppender<ILoggingEvent> restrictedAppender(String loggerName, String threshold) {
            Logger logger = ctx.getLogger(loggerName);
            logger.setLevel(Level.DEBUG);
            logger.setAdditive(false);
            ListAppender<ILoggingEvent> appender = newAppender();
            logger.addAppender(appender);

            tasks.addThreshold(appender, threshold);
            return appender;
        }

        @Test
        @DisplayName("threshold WARN → DEBUG and INFO rejected, WARN and ERROR accepted")
        void javadocExample() {
            ListAppender<ILoggingEvent> appender = restrictedAppender("tasks.cfg.threshold.warn", "WARN");
            Logger logger = ctx.getLogger("tasks.cfg.threshold.warn");

            logger.debug("d");
            logger.info("i");
            logger.warn("w");
            logger.error("e");

            assertThat(appender.list).extracting(ILoggingEvent::getFormattedMessage).containsExactly("w", "e");
        }

        @Test
        @DisplayName("level name is case-insensitive: \"error\" keeps only ERROR")
        void caseInsensitive() {
            ListAppender<ILoggingEvent> appender = restrictedAppender("tasks.cfg.threshold.error", "error");
            Logger logger = ctx.getLogger("tasks.cfg.threshold.error");

            logger.warn("w");
            logger.error("e");

            assertThat(appender.list).extracting(ILoggingEvent::getFormattedMessage).containsExactly("e");
        }

        @Test
        @DisplayName("events exactly at the threshold are kept")
        void thresholdIsInclusive() {
            ListAppender<ILoggingEvent> appender = restrictedAppender("tasks.cfg.threshold.info", "INFO");
            Logger logger = ctx.getLogger("tasks.cfg.threshold.info");

            logger.info("i");

            assertThat(appender.list).hasSize(1);
        }

        @Test
        @DisplayName("only the restricted appender is affected, not the logger")
        void otherAppendersUnaffected() {
            Logger logger = ctx.getLogger("tasks.cfg.threshold.other");
            logger.setLevel(Level.DEBUG);
            logger.setAdditive(false);
            ListAppender<ILoggingEvent> restricted = newAppender();
            ListAppender<ILoggingEvent> everything = newAppender();
            logger.addAppender(restricted);
            logger.addAppender(everything);

            tasks.addThreshold(restricted, "ERROR");
            logger.info("i");

            assertThat(restricted.list).isEmpty();
            assertThat(everything.list).hasSize(1);
        }
    }

    // ── Task 5: isolateLogger ─────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement isolateLogger()")
    @DisplayName("Task 5 — isolateLogger()")
    class IsolateLoggerTest {

        @Test
        @DisplayName("the isolated logger's appender gets the event; the parent's appender does not")
        void javadocExample() {
            Logger parent = ctx.getLogger("tasks.cfg.isolate.example");
            ListAppender<ILoggingEvent> parentAppender = newAppender();
            parent.addAppender(parentAppender);
            ListAppender<ILoggingEvent> audit = newAppender();

            tasks.isolateLogger(ctx, "tasks.cfg.isolate.example.audit", audit);
            ctx.getLogger("tasks.cfg.isolate.example.audit").info("x");

            assertThat(audit.list).hasSize(1);
            assertThat(parentAppender.list).isEmpty();
        }

        @Test
        @DisplayName("the appender is attached and additivity is switched off")
        void loggerState() {
            ListAppender<ILoggingEvent> appender = newAppender();

            tasks.isolateLogger(ctx, "tasks.cfg.isolate.state", appender);

            Logger logger = ctx.getLogger("tasks.cfg.isolate.state");
            assertThat(logger.isAttached(appender)).isTrue();
            assertThat(logger.isAdditive()).isFalse();
        }

        @Test
        @DisplayName("child loggers of the isolated logger are covered too")
        void childrenAreCovered() {
            Logger parent = ctx.getLogger("tasks.cfg.isolate.tree");
            ListAppender<ILoggingEvent> parentAppender = newAppender();
            parent.addAppender(parentAppender);
            ListAppender<ILoggingEvent> audit = newAppender();

            tasks.isolateLogger(ctx, "tasks.cfg.isolate.tree.audit", audit);
            ctx.getLogger("tasks.cfg.isolate.tree.audit.payments").info("x");

            assertThat(audit.list).hasSize(1);
            assertThat(parentAppender.list).isEmpty();
        }
    }
}
