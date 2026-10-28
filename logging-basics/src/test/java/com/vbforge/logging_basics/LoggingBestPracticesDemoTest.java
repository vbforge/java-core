package com.vbforge.logging_basics;

import ch.qos.logback.classic.Level;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UncheckedIOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * LoggingBestPracticesDemoTest — verifies every concept shown in {@link LoggingBestPracticesDemo}.
 *
 * <p>These tests are always GREEN. Read them as executable specification of
 * the logging habits the demo recommends.
 */
@DisplayName("LoggingBestPracticesDemo — API contracts")
class LoggingBestPracticesDemoTest {

    // ── 1. Choosing levels ────────────────────────────────────────────────────

    @Nested
    @DisplayName("1. Choosing levels")
    class LevelGuidelinesTest {

        @Test
        @DisplayName("at DEBUG the demo emits debug, info, warn and error in order")
        void atDebug() {
            try (LogCapture capture = LogCapture.of(LoggingBestPracticesDemo.class).level(Level.DEBUG)) {
                LoggingBestPracticesDemo.levelGuidelinesDemo();

                assertThat(capture.events()).extracting(e -> e.getLevel().toString())
                        .containsExactly("DEBUG", "INFO", "WARN", "ERROR");
            }
        }

        @Test
        @DisplayName("at INFO (typical production) the DEBUG line disappears")
        void atInfo() {
            try (LogCapture capture = LogCapture.of(LoggingBestPracticesDemo.class).level(Level.INFO)) {
                LoggingBestPracticesDemo.levelGuidelinesDemo();

                assertThat(capture.events()).extracting(e -> e.getLevel().toString())
                        .containsExactly("INFO", "WARN", "ERROR");
            }
        }

        @Test
        @DisplayName("at WARN only warnings and errors remain")
        void atWarn() {
            try (LogCapture capture = LogCapture.of(LoggingBestPracticesDemo.class).level(Level.WARN)) {
                LoggingBestPracticesDemo.levelGuidelinesDemo();

                assertThat(capture.events()).extracting(e -> e.getLevel().toString())
                        .containsExactly("WARN", "ERROR");
            }
        }

        @Test
        @DisplayName("the ERROR entry carries the exception's stack trace")
        void errorHasStackTrace() {
            try (LogCapture capture = LogCapture.of(LoggingBestPracticesDemo.class).level(Level.ERROR)) {
                LoggingBestPracticesDemo.levelGuidelinesDemo();

                assertThat(capture.single().getThrowableProxy()).isNotNull();
            }
        }
    }

    // ── 2. Sensitive data ─────────────────────────────────────────────────────

    @Nested
    @DisplayName("2. Sensitive data")
    class SensitiveDataTest {

        @Test
        @DisplayName("a safe toString() keeps the password out of the log line")
        void passwordIsNotLogged() {
            try (LogCapture capture = LogCapture.of(LoggingBestPracticesDemo.class)) {
                LoggingBestPracticesDemo.sensitiveDataDemo();

                assertThat(capture.single().getFormattedMessage())
                        .contains("alice")
                        .doesNotContain("hunter2");
            }
        }

        @Test
        @DisplayName("the value object itself still holds the secret — only its toString() hides it")
        void toStringHidesSecret() {
            LoggingBestPracticesDemo.Credentials credentials =
                    new LoggingBestPracticesDemo.Credentials("alice", "hunter2");

            assertThat(credentials.toString()).isEqualTo("Credentials{user=alice, password=***}");
        }
    }

    // ── 3. Log or throw ───────────────────────────────────────────────────────

    @Nested
    @DisplayName("3. Log or throw")
    class LogOrThrowTest {

        @Test
        @DisplayName("anti-pattern: log-and-throw makes one failure appear twice")
        void logAndThrowDuplicates() {
            try (LogCapture capture = LogCapture.of(LoggingBestPracticesDemo.class)) {
                LoggingBestPracticesDemo.requestWithLogAndThrow();

                assertThat(capture.messages()).containsExactly("Cannot load app.yml", "Request failed");
            }
        }

        @Test
        @DisplayName("good: wrapping and logging once at the boundary gives a single entry")
        void boundaryLogsOnce() {
            try (LogCapture capture = LogCapture.of(LoggingBestPracticesDemo.class)) {
                LoggingBestPracticesDemo.requestWithBoundaryLogging();

                assertThat(capture.messages()).containsExactly("Request failed");
            }
        }

        @Test
        @DisplayName("wrapping adds context to the message and keeps the original cause")
        void wrappingKeepsCause() {
            assertThatThrownBy(() -> LoggingBestPracticesDemo.loadSettingWrapped("app.yml"))
                    .isInstanceOf(UncheckedIOException.class)
                    .hasMessage("Cannot load app.yml")
                    .hasCauseInstanceOf(IOException.class)
                    .hasRootCauseMessage("disk unreadable");
        }

        @Test
        @DisplayName("the single boundary entry still shows the root cause in its stack trace")
        void boundaryEntryShowsCauseChain() {
            try (LogCapture capture = LogCapture.of(LoggingBestPracticesDemo.class)) {
                LoggingBestPracticesDemo.requestWithBoundaryLogging();

                assertThat(capture.single().getThrowableProxy().getCause()).isNotNull();
                assertThat(capture.single().getThrowableProxy().getCause().getClassName())
                        .isEqualTo("java.io.IOException");
            }
        }
    }

    // ── 4. Summaries instead of floods ────────────────────────────────────────

    @Nested
    @DisplayName("4. Summaries instead of floods")
    class SummaryLoggingTest {

        @Test
        @DisplayName("at INFO the whole import produces one summary line")
        void oneSummaryAtInfo() {
            try (LogCapture capture = LogCapture.of(LoggingBestPracticesDemo.class).level(Level.INFO)) {
                LoggingBestPracticesDemo.summaryLoggingDemo();

                assertThat(capture.messages()).containsExactly("Import finished: imported=997 skipped=3");
            }
        }

        @Test
        @DisplayName("at TRACE the same loop produces one line per imported row plus the summary")
        void floodAtTrace() {
            try (LogCapture capture = LogCapture.of(LoggingBestPracticesDemo.class).level(Level.TRACE)) {
                LoggingBestPracticesDemo.summaryLoggingDemo();

                assertThat(capture.events()).hasSize(997 + 1);
            }
        }
    }

    // ── main() ────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("5. The demo itself")
    class DemoMainTest {

        @Test
        @DisplayName("main() runs every section without throwing")
        void mainRuns() {
            assertThatCode(() -> LoggingBestPracticesDemo.main(new String[0])).doesNotThrowAnyException();
        }
    }
}
