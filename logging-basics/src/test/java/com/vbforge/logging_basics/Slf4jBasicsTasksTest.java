package com.vbforge.logging_basics;

import ch.qos.logback.classic.Level;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Slf4jBasicsTasksTest — tests for every method in {@link Slf4jBasicsTasks}.
 *
 * <p><b>Getting started:</b>
 * <ol>
 *   <li>Open {@code Slf4jBasicsTasks.java} and implement a method.</li>
 *   <li>Remove the {@code @Disabled} annotation from the corresponding {@code @Nested} class.</li>
 *   <li>Run {@code mvn test -Dtest=Slf4jBasicsTasksTest} — those tests should go GREEN ✅</li>
 *   <li>Repeat until all 4 task groups are GREEN.</li>
 * </ol>
 */
@DisplayName("Slf4jBasicsTasks — your implementations")
class Slf4jBasicsTasksTest {

    private Slf4jBasicsTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new Slf4jBasicsTasks();
    }

    // ── Task 1: logLogin ──────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement logLogin()")
    @DisplayName("Task 1 — logLogin()")
    class LogLoginTest {

        @Test
        @DisplayName("alice, 3 attempts → INFO \"User alice logged in after 3 attempt(s)\"")
        void javadocExample() {
            try (LogCapture capture = LogCapture.of("tasks.slf4j.login.example")) {
                tasks.logLogin(capture.logger(), "alice", 3);

                assertThat(capture.single().getLevel()).isEqualTo(Level.INFO);
                assertThat(capture.single().getFormattedMessage())
                        .isEqualTo("User alice logged in after 3 attempt(s)");
            }
        }

        @Test
        @DisplayName("uses {} placeholders — the template stays constant on the event")
        void usesPlaceholders() {
            try (LogCapture capture = LogCapture.of("tasks.slf4j.login.placeholders")) {
                tasks.logLogin(capture.logger(), "alice", 3);

                assertThat(capture.single().getMessage()).isEqualTo("User {} logged in after {} attempt(s)");
                assertThat(capture.single().getArgumentArray()).containsExactly("alice", 3);
            }
        }

        @ParameterizedTest(name = "user={0} attempts={1}")
        @CsvSource({
                "bob,   1",
                "carol, 10",
                "dave,  0",
        })
        @DisplayName("works for other values")
        void otherValues(String user, int attempts) {
            try (LogCapture capture = LogCapture.of("tasks.slf4j.login.values")) {
                tasks.logLogin(capture.logger(), user, attempts);

                assertThat(capture.messages())
                        .containsExactly("User " + user + " logged in after " + attempts + " attempt(s)");
            }
        }
    }

    // ── Task 2: logFailure ────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement logFailure()")
    @DisplayName("Task 2 — logFailure()")
    class LogFailureTest {

        @Test
        @DisplayName("save + IllegalStateException → ERROR \"Operation save failed\" with the exception attached")
        void javadocExample() {
            try (LogCapture capture = LogCapture.of("tasks.slf4j.failure.example")) {
                tasks.logFailure(capture.logger(), "save", new IllegalStateException("db down"));

                assertThat(capture.single().getLevel()).isEqualTo(Level.ERROR);
                assertThat(capture.single().getFormattedMessage()).isEqualTo("Operation save failed");
                assertThat(capture.single().getThrowableProxy()).isNotNull();
                assertThat(capture.single().getThrowableProxy().getClassName())
                        .isEqualTo("java.lang.IllegalStateException");
            }
        }

        @Test
        @DisplayName("the exception message is NOT part of the log message text")
        void exceptionMessageNotInText() {
            try (LogCapture capture = LogCapture.of("tasks.slf4j.failure.text")) {
                tasks.logFailure(capture.logger(), "save", new IllegalStateException("db down"));

                assertThat(capture.single().getFormattedMessage()).doesNotContain("db down");
                assertThat(capture.single().getThrowableProxy().getMessage()).isEqualTo("db down");
            }
        }

        @Test
        @DisplayName("works with an exception that has no message")
        void exceptionWithoutMessage() {
            try (LogCapture capture = LogCapture.of("tasks.slf4j.failure.nomessage")) {
                tasks.logFailure(capture.logger(), "load", new RuntimeException());

                assertThat(capture.single().getFormattedMessage()).isEqualTo("Operation load failed");
                assertThat(capture.single().getThrowableProxy().getClassName())
                        .isEqualTo("java.lang.RuntimeException");
            }
        }
    }

    // ── Task 3: logExpensiveIfDebug ───────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement logExpensiveIfDebug()")
    @DisplayName("Task 3 — logExpensiveIfDebug()")
    class LogExpensiveIfDebugTest {

        @Test
        @DisplayName("INFO logger → false, supplier NOT called, nothing logged")
        void disabled() {
            try (LogCapture capture = LogCapture.of("tasks.slf4j.expensive.info").level(Level.INFO)) {
                AtomicInteger calls = new AtomicInteger();

                boolean logged = tasks.logExpensiveIfDebug(capture.logger(), () -> {
                    calls.incrementAndGet();
                    return "dump";
                });

                assertThat(logged).isFalse();
                assertThat(calls.get()).isZero();
                assertThat(capture.events()).isEmpty();
            }
        }

        @Test
        @DisplayName("DEBUG logger → true, supplier called once, message logged at DEBUG")
        void enabled() {
            try (LogCapture capture = LogCapture.of("tasks.slf4j.expensive.debug").level(Level.DEBUG)) {
                AtomicInteger calls = new AtomicInteger();

                boolean logged = tasks.logExpensiveIfDebug(capture.logger(), () -> {
                    calls.incrementAndGet();
                    return "dump";
                });

                assertThat(logged).isTrue();
                assertThat(calls.get()).isEqualTo(1);
                assertThat(capture.single().getLevel()).isEqualTo(Level.DEBUG);
                assertThat(capture.single().getFormattedMessage()).isEqualTo("dump");
            }
        }

        @ParameterizedTest(name = "logger level {0} → returns {1}")
        @CsvSource({
                "TRACE, true",
                "DEBUG, true",
                "INFO,  false",
                "WARN,  false",
                "ERROR, false",
        })
        @DisplayName("return value follows whether DEBUG is enabled")
        void returnValueByLevel(String levelName, boolean expected) {
            try (LogCapture capture = LogCapture.of("tasks.slf4j.expensive." + levelName)
                    .level(Level.valueOf(levelName))) {

                assertThat(tasks.logExpensiveIfDebug(capture.logger(), () -> "dump")).isEqualTo(expected);
                assertThat(capture.events()).hasSize(expected ? 1 : 0);
            }
        }
    }

    // ── Task 4: logAtLevel ────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement logAtLevel()")
    @DisplayName("Task 4 — logAtLevel()")
    class LogAtLevelTest {

        @Test
        @DisplayName("\"WARN\" → WARN \"disk almost full\"")
        void javadocExample() {
            try (LogCapture capture = LogCapture.of("tasks.slf4j.level.example")) {
                tasks.logAtLevel(capture.logger(), "WARN", "disk almost full");

                assertThat(capture.single().getLevel()).isEqualTo(Level.WARN);
                assertThat(capture.single().getFormattedMessage()).isEqualTo("disk almost full");
            }
        }

        @ParameterizedTest(name = "level name {0}")
        @ValueSource(strings = {"TRACE", "DEBUG", "INFO", "WARN", "ERROR"})
        @DisplayName("each level name logs at that level")
        void everyLevel(String levelName) {
            try (LogCapture capture = LogCapture.of("tasks.slf4j.level." + levelName)) {
                tasks.logAtLevel(capture.logger(), levelName, "hello");

                assertThat(capture.single().getLevel()).isEqualTo(Level.valueOf(levelName));
                assertThat(capture.single().getFormattedMessage()).isEqualTo("hello");
            }
        }

        @Test
        @DisplayName("level name is case-insensitive: \"debug\" → DEBUG")
        void caseInsensitive() {
            try (LogCapture capture = LogCapture.of("tasks.slf4j.level.case")) {
                tasks.logAtLevel(capture.logger(), "debug", "cache miss");

                assertThat(capture.single().getLevel()).isEqualTo(Level.DEBUG);
            }
        }

        @Test
        @DisplayName("unknown level name → IllegalArgumentException and nothing is logged")
        void unknownLevel() {
            try (LogCapture capture = LogCapture.of("tasks.slf4j.level.unknown")) {
                assertThatThrownBy(() -> tasks.logAtLevel(capture.logger(), "LOUD", "hello"))
                        .isInstanceOf(IllegalArgumentException.class);
                assertThat(capture.events()).isEmpty();
            }
        }

        @Test
        @DisplayName("a disabled level logs nothing (the level threshold still applies)")
        void respectsThreshold() {
            try (LogCapture capture = LogCapture.of("tasks.slf4j.level.threshold").level(Level.WARN)) {
                tasks.logAtLevel(capture.logger(), "INFO", "dropped");

                assertThat(capture.events()).isEmpty();
            }
        }
    }
}
