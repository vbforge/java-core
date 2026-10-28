package com.vbforge.logging_basics;

import ch.qos.logback.classic.Level;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * Slf4jBasicsDemoTest — verifies every concept shown in {@link Slf4jBasicsDemo}.
 *
 * <p>These tests are always GREEN. Read them as executable specification of
 * the SLF4J API.
 */
@DisplayName("Slf4jBasicsDemo — API contracts")
class Slf4jBasicsDemoTest {

    // ── 1. Logger acquisition ─────────────────────────────────────────────────

    @Nested
    @DisplayName("1. Logger acquisition")
    class LoggerAcquisitionTest {

        @Test
        @DisplayName("getLogger(Class) names the logger after the fully-qualified class name")
        void nameIsClassName() {
            Logger logger = LoggerFactory.getLogger(Slf4jBasicsDemo.class);
            assertThat(logger.getName()).isEqualTo("com.vbforge.logging_basics.Slf4jBasicsDemo");
        }

        @Test
        @DisplayName("the same name always returns the same Logger instance")
        void sameNameSameInstance() {
            Logger byClass = LoggerFactory.getLogger(Slf4jBasicsDemo.class);
            Logger byName  = LoggerFactory.getLogger("com.vbforge.logging_basics.Slf4jBasicsDemo");
            assertThat(byClass).isSameAs(byName);
        }

        @Test
        @DisplayName("different names give different Logger instances")
        void differentNamesDifferentInstances() {
            assertThat(LoggerFactory.getLogger("payments.audit"))
                    .isNotSameAs(LoggerFactory.getLogger("payments.billing"));
        }
    }

    // ── 2. Levels ─────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("2. Levels")
    class LevelsTest {

        @Test
        @DisplayName("a logger at INFO enables INFO, WARN, ERROR and disables TRACE, DEBUG")
        void infoThreshold() {
            try (LogCapture capture = LogCapture.of("demo.slf4j.levels.info").level(Level.INFO)) {
                Logger log = capture.logger();
                assertThat(log.isTraceEnabled()).isFalse();
                assertThat(log.isDebugEnabled()).isFalse();
                assertThat(log.isInfoEnabled()).isTrue();
                assertThat(log.isWarnEnabled()).isTrue();
                assertThat(log.isErrorEnabled()).isTrue();
            }
        }

        @Test
        @DisplayName("raising the threshold to WARN also disables INFO")
        void warnThreshold() {
            try (LogCapture capture = LogCapture.of("demo.slf4j.levels.warn").level(Level.WARN)) {
                Logger log = capture.logger();
                log.info("dropped");
                log.warn("kept");
                assertThat(capture.messages()).containsExactly("kept");
            }
        }

        @Test
        @DisplayName("at TRACE every level is emitted, in call order, with the right level")
        void allLevelsAtTrace() {
            try (LogCapture capture = LogCapture.of("demo.slf4j.levels.all")) {   // helper starts at TRACE
                Logger log = capture.logger();
                log.trace("t");
                log.debug("d");
                log.info("i");
                log.warn("w");
                log.error("e");

                List<String> levels = new ArrayList<>();
                capture.events().forEach(event -> levels.add(event.getLevel().toString()));
                assertThat(levels).containsExactly("TRACE", "DEBUG", "INFO", "WARN", "ERROR");
            }
        }

        @Test
        @DisplayName("the shipped logback.xml sets this package to DEBUG: TRACE off, DEBUG on")
        void shippedConfiguration() {
            Logger log = LoggerFactory.getLogger(Slf4jBasicsDemo.class);
            assertThat(log.isTraceEnabled()).isFalse();
            assertThat(log.isDebugEnabled()).isTrue();
        }
    }

    // ── 3. Parameterized messages ─────────────────────────────────────────────

    @Nested
    @DisplayName("3. Parameterized messages")
    class ParameterizedMessagesTest {

        @Test
        @DisplayName("{} placeholders are filled in order")
        void placeholdersFilledInOrder() {
            try (LogCapture capture = LogCapture.of("demo.slf4j.params.order")) {
                capture.logger().info("User {} logged in after {} attempt(s)", "alice", 3);
                assertThat(capture.single().getFormattedMessage())
                        .isEqualTo("User alice logged in after 3 attempt(s)");
            }
        }

        @Test
        @DisplayName("the event keeps the constant template and the raw arguments")
        void templateStaysConstant() {
            try (LogCapture capture = LogCapture.of("demo.slf4j.params.template")) {
                capture.logger().info("User {} logged in after {} attempt(s)", "alice", 3);
                assertThat(capture.single().getMessage()).isEqualTo("User {} logged in after {} attempt(s)");
                assertThat(capture.single().getArgumentArray()).containsExactly("alice", 3);
            }
        }

        @Test
        @DisplayName("a backslash escapes a placeholder so it prints literally")
        void escapedPlaceholder() {
            try (LogCapture capture = LogCapture.of("demo.slf4j.params.escape")) {
                capture.logger().info("Literal \\{} stays, value={}", "x");
                assertThat(capture.messages()).containsExactly("Literal {} stays, value=x");
            }
        }

        @Test
        @DisplayName("fewer arguments than placeholders leave the extra {} untouched")
        void tooFewArguments() {
            try (LogCapture capture = LogCapture.of("demo.slf4j.params.few")) {
                capture.logger().info("{} and {}", "only-one");
                assertThat(capture.messages()).containsExactly("only-one and {}");
            }
        }

        @Test
        @DisplayName("more arguments than placeholders are ignored")
        void tooManyArguments() {
            try (LogCapture capture = LogCapture.of("demo.slf4j.params.many")) {
                capture.logger().info("only {}", "used", "ignored");
                assertThat(capture.messages()).containsExactly("only used");
            }
        }

        @Test
        @DisplayName("a null argument prints as null")
        void nullArgument() {
            try (LogCapture capture = LogCapture.of("demo.slf4j.params.null")) {
                capture.logger().info("value={}", (Object) null);
                assertThat(capture.messages()).containsExactly("value=null");
            }
        }

        @Test
        @DisplayName("three or more arguments work through varargs")
        void varargs() {
            try (LogCapture capture = LogCapture.of("demo.slf4j.params.varargs")) {
                capture.logger().info("{} + {} + {} = {}", 1, 2, 3, 6);
                assertThat(capture.messages()).containsExactly("1 + 2 + 3 = 6");
            }
        }
    }

    // ── 4. Logging exceptions ─────────────────────────────────────────────────

    @Nested
    @DisplayName("4. Logging exceptions")
    class ExceptionsTest {

        @Test
        @DisplayName("a trailing Throwable is attached to the event and does not use a placeholder")
        void trailingThrowableIsAttached() {
            try (LogCapture capture = LogCapture.of("demo.slf4j.ex.attached")) {
                capture.logger().error("Cannot parse '{}'", "abc", new NumberFormatException("bad"));

                assertThat(capture.single().getFormattedMessage()).isEqualTo("Cannot parse 'abc'");
                assertThat(capture.single().getThrowableProxy()).isNotNull();
                assertThat(capture.single().getThrowableProxy().getClassName())
                        .isEqualTo("java.lang.NumberFormatException");
            }
        }

        @Test
        @DisplayName("logging only e.getMessage() loses the stack trace")
        void messageOnlyLosesStackTrace() {
            try (LogCapture capture = LogCapture.of("demo.slf4j.ex.lost")) {
                Exception e = new IllegalStateException("db down");
                capture.logger().error("Failed: " + e.getMessage());

                assertThat(capture.single().getThrowableProxy()).isNull();
            }
        }
    }

    // ── 5. Lazy evaluation ────────────────────────────────────────────────────

    @Nested
    @DisplayName("5. Lazy evaluation")
    class LazyEvaluationTest {

        @Test
        @DisplayName("arguments are evaluated even when the level is disabled")
        void argumentsAreEagerlyEvaluated() {
            try (LogCapture capture = LogCapture.of("demo.slf4j.lazy.eager").level(Level.INFO)) {
                AtomicInteger calls = new AtomicInteger();
                capture.logger().trace("dump={}", expensive(calls));

                assertThat(calls.get()).isEqualTo(1);       // evaluated...
                assertThat(capture.events()).isEmpty();     // ...but never logged
            }
        }

        @Test
        @DisplayName("an isTraceEnabled() guard skips the expensive call")
        void guardSkipsEvaluation() {
            try (LogCapture capture = LogCapture.of("demo.slf4j.lazy.guard").level(Level.INFO)) {
                AtomicInteger calls = new AtomicInteger();
                Logger log = capture.logger();
                if (log.isTraceEnabled()) {
                    log.trace("dump={}", expensive(calls));
                }
                assertThat(calls.get()).isZero();
            }
        }

        @Test
        @DisplayName("the fluent API runs the supplier only when the level is enabled")
        void fluentApiIsLazy() {
            try (LogCapture capture = LogCapture.of("demo.slf4j.lazy.fluent").level(Level.INFO)) {
                AtomicInteger calls = new AtomicInteger();
                Logger log = capture.logger();

                log.atTrace().log(() -> expensive(calls));
                assertThat(calls.get()).isZero();
                assertThat(capture.events()).isEmpty();

                capture.level(Level.TRACE);
                log.atTrace().log(() -> expensive(calls));
                assertThat(calls.get()).isEqualTo(1);
                assertThat(capture.messages()).containsExactly("huge dump");
            }
        }

        private String expensive(AtomicInteger calls) {
            calls.incrementAndGet();
            return "huge dump";
        }
    }

    // ── main() ────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("6. The demo itself")
    class DemoMainTest {

        @Test
        @DisplayName("main() runs every section without throwing")
        void mainRuns() {
            assertThatCode(() -> Slf4jBasicsDemo.main(new String[0])).doesNotThrowAnyException();
        }
    }
}
