package com.vbforge.logging_basics;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.filter.ThresholdFilter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * LogbackConfigurationDemoTest — verifies every concept shown in {@link LogbackConfigurationDemo}.
 *
 * <p>These tests are always GREEN. Read them as executable specification of
 * how Logback is configured.
 */
@DisplayName("LogbackConfigurationDemo — API contracts")
class LogbackConfigurationDemoTest {

    private static final LoggerContext CTX = (LoggerContext) LoggerFactory.getILoggerFactory();

    /** Creates a started in-memory appender and attaches it to the logger. */
    private static ListAppender<ILoggingEvent> attach(Logger logger) {
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.setContext(CTX);
        appender.start();
        logger.addAppender(appender);
        return appender;
    }

    // ── 1. Configuration file ─────────────────────────────────────────────────

    @Nested
    @DisplayName("1. Configuration file")
    class ConfigurationFileTest {

        @Test
        @DisplayName("logback.xml sets the root logger to INFO")
        void rootLevel() {
            assertThat(CTX.getLogger(Logger.ROOT_LOGGER_NAME).getLevel()).isEqualTo(Level.INFO);
        }

        @Test
        @DisplayName("logback.xml attaches a CONSOLE appender to the root logger")
        void rootConsoleAppender() {
            assertThat(CTX.getLogger(Logger.ROOT_LOGGER_NAME).getAppender("CONSOLE")).isNotNull();
        }

        @Test
        @DisplayName("logback.xml opens this module's package to DEBUG")
        void packageLevel() {
            assertThat(CTX.getLogger("com.vbforge.logging_basics").getLevel()).isEqualTo(Level.DEBUG);
        }

        @Test
        @DisplayName("classes inside that package inherit DEBUG")
        void classesInheritPackageLevel() {
            assertThat(CTX.getLogger("com.vbforge.logging_basics.AnyClass").getEffectiveLevel())
                    .isEqualTo(Level.DEBUG);
        }
    }

    // ── 2. Logger hierarchy ───────────────────────────────────────────────────

    @Nested
    @DisplayName("2. Logger hierarchy")
    class HierarchyTest {

        @Test
        @DisplayName("an unconfigured logger has a null level but inherits the effective level of ROOT")
        void unsetLevelInheritsFromRoot() {
            Logger child = CTX.getLogger("demo.test.hier.unset.child");
            assertThat(child.getLevel()).isNull();
            assertThat(child.getEffectiveLevel()).isEqualTo(Level.INFO);
        }

        @Test
        @DisplayName("a child inherits the level set on its parent")
        void childInheritsFromParent() {
            Logger parent = CTX.getLogger("demo.test.hier.inherit");
            Logger child  = CTX.getLogger("demo.test.hier.inherit.child");
            parent.setLevel(Level.WARN);

            assertThat(child.getLevel()).isNull();
            assertThat(child.getEffectiveLevel()).isEqualTo(Level.WARN);
        }

        @Test
        @DisplayName("a level set on the child wins over the parent's")
        void childLevelWins() {
            Logger parent = CTX.getLogger("demo.test.hier.override");
            Logger child  = CTX.getLogger("demo.test.hier.override.child");
            parent.setLevel(Level.WARN);
            child.setLevel(Level.DEBUG);

            assertThat(child.getEffectiveLevel()).isEqualTo(Level.DEBUG);
        }

        @Test
        @DisplayName("setLevel(null) makes a logger inherit again")
        void nullLevelRestoresInheritance() {
            Logger parent = CTX.getLogger("demo.test.hier.reset");
            Logger child  = CTX.getLogger("demo.test.hier.reset.child");
            parent.setLevel(Level.WARN);
            child.setLevel(Level.DEBUG);

            child.setLevel(null);

            assertThat(child.getEffectiveLevel()).isEqualTo(Level.WARN);
        }
    }

    // ── 3. Level parsing ──────────────────────────────────────────────────────

    @Nested
    @DisplayName("3. Level parsing")
    class LevelParsingTest {

        @Test
        @DisplayName("toLevel(String) is case-insensitive")
        void caseInsensitive() {
            assertThat(Level.toLevel("warn")).isEqualTo(Level.WARN);
            assertThat(Level.toLevel("Error")).isEqualTo(Level.ERROR);
        }

        @Test
        @DisplayName("valueOf(String) silently falls back to DEBUG for unknown text")
        void valueOfFallsBackToDebug() {
            assertThat(Level.valueOf("WARNING")).isEqualTo(Level.DEBUG);
        }

        @Test
        @DisplayName("toLevel(text, null) returns null for unknown text — a detectable failure")
        void toLevelWithNullDefault() {
            assertThat(Level.toLevel("WARNING", null)).isNull();
            assertThat(Level.toLevel("WARN", null)).isEqualTo(Level.WARN);
        }
    }

    // ── 4. Appenders and additivity ───────────────────────────────────────────

    @Nested
    @DisplayName("4. Appenders and additivity")
    class AdditivityTest {

        @Test
        @DisplayName("by default an event reaches the logger's appender AND its parent's")
        void additiveByDefault() {
            Logger parent = CTX.getLogger("demo.test.add.default");
            Logger child  = CTX.getLogger("demo.test.add.default.child");
            ListAppender<ILoggingEvent> parentEvents = attach(parent);
            ListAppender<ILoggingEvent> childEvents  = attach(child);

            child.info("hello");

            assertThat(childEvents.list).hasSize(1);
            assertThat(parentEvents.list).hasSize(1);
            parent.detachAndStopAllAppenders();
            child.detachAndStopAllAppenders();
        }

        @Test
        @DisplayName("setAdditive(false) stops events at the logger's own appenders")
        void nonAdditive() {
            Logger parent = CTX.getLogger("demo.test.add.off");
            Logger child  = CTX.getLogger("demo.test.add.off.child");
            ListAppender<ILoggingEvent> parentEvents = attach(parent);
            ListAppender<ILoggingEvent> childEvents  = attach(child);
            child.setAdditive(false);

            child.info("hello");

            assertThat(childEvents.list).hasSize(1);
            assertThat(parentEvents.list).isEmpty();
            parent.detachAndStopAllAppenders();
            child.detachAndStopAllAppenders();
        }

        @Test
        @DisplayName("the same appender on a logger and its parent receives the event twice")
        void duplicateLines() {
            Logger parent = CTX.getLogger("demo.test.add.dup");
            Logger child  = CTX.getLogger("demo.test.add.dup.child");
            ListAppender<ILoggingEvent> shared = attach(parent);
            child.addAppender(shared);

            child.info("hello");

            assertThat(shared.list).hasSize(2);
            parent.detachAndStopAllAppenders();
            child.detachAndStopAllAppenders();
        }

        @Test
        @DisplayName("additivity never changes the level check: a disabled event is dropped either way")
        void levelCheckComesFirst() {
            Logger logger = CTX.getLogger("demo.test.add.level");
            ListAppender<ILoggingEvent> events = attach(logger);
            logger.setLevel(Level.WARN);
            logger.setAdditive(false);

            logger.info("below the level");

            assertThat(events.list).isEmpty();
            logger.detachAndStopAllAppenders();
        }
    }

    // ── 5. Pattern layout ─────────────────────────────────────────────────────

    @Nested
    @DisplayName("5. Pattern layout")
    class PatternLayoutTest {

        private String format(String pattern, ILoggingEvent event) {
            PatternLayout layout = new PatternLayout();
            layout.setContext(CTX);
            layout.setPattern(pattern);
            layout.start();
            return layout.doLayout(event);
        }

        private ILoggingEvent warnEvent() {
            try (LogCapture capture = LogCapture.of("demo.test.layout")) {
                capture.logger().warn("disk at {}%", 91);
                return capture.single();
            }
        }

        @Test
        @DisplayName("%-5level pads the level; %logger and %msg print name and formatted message")
        void levelLoggerMessage() {
            assertThat(format("%-5level %logger - %msg", warnEvent()))
                    .isEqualTo("WARN  demo.test.layout - disk at 91%");
        }

        @Test
        @DisplayName("literal text in the pattern is copied as-is")
        void literalText() {
            assertThat(format("[%level] %msg", warnEvent())).isEqualTo("[WARN] disk at 91%");
        }
    }

    // ── 6. Threshold filter ───────────────────────────────────────────────────

    @Nested
    @DisplayName("6. Threshold filter")
    class ThresholdFilterTest {

        private ThresholdFilter filter(String level) {
            ThresholdFilter filter = new ThresholdFilter();
            filter.setLevel(level);
            return filter;
        }

        @Test
        @DisplayName("a started filter drops events below the level and keeps the rest")
        void startedFilter() {
            Logger logger = CTX.getLogger("demo.test.threshold.started");
            logger.setAdditive(false);
            logger.setLevel(Level.DEBUG);
            ListAppender<ILoggingEvent> appender = attach(logger);
            ThresholdFilter filter = filter("WARN");
            filter.start();
            appender.addFilter(filter);

            logger.debug("d");
            logger.info("i");
            logger.warn("w");
            logger.error("e");

            assertThat(appender.list).extracting(ILoggingEvent::getFormattedMessage).containsExactly("w", "e");
            logger.detachAndStopAllAppenders();
        }

        @Test
        @DisplayName("a filter that was never started is silently ignored")
        void unstartedFilterIsIgnored() {
            Logger logger = CTX.getLogger("demo.test.threshold.unstarted");
            logger.setAdditive(false);
            logger.setLevel(Level.DEBUG);
            ListAppender<ILoggingEvent> appender = attach(logger);
            appender.addFilter(filter("WARN"));   // no start()!

            logger.debug("d");

            assertThat(appender.list).hasSize(1);
            logger.detachAndStopAllAppenders();
        }

        @Test
        @DisplayName("the logger level is checked before the appender's filter")
        void loggerLevelFirst() {
            Logger logger = CTX.getLogger("demo.test.threshold.order");
            logger.setAdditive(false);
            logger.setLevel(Level.WARN);
            ListAppender<ILoggingEvent> appender = attach(logger);
            ThresholdFilter filter = filter("DEBUG");
            filter.start();
            appender.addFilter(filter);

            logger.debug("accepted by the filter, but the logger already said no");

            assertThat(appender.list).isEmpty();
            logger.detachAndStopAllAppenders();
        }
    }

    // ── main() ────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("7. The demo itself")
    class DemoMainTest {

        @Test
        @DisplayName("main() runs every section without throwing")
        void mainRuns() {
            assertThatCode(() -> LogbackConfigurationDemo.main(new String[0])).doesNotThrowAnyException();
        }
    }
}
