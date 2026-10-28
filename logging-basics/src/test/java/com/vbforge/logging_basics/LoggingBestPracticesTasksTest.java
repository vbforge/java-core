package com.vbforge.logging_basics;

import ch.qos.logback.classic.Level;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * LoggingBestPracticesTasksTest — tests for every method in {@link LoggingBestPracticesTasks}.
 *
 * <p><b>Getting started:</b>
 * <ol>
 *   <li>Open {@code LoggingBestPracticesTasks.java} and implement a method.</li>
 *   <li>Remove the {@code @Disabled} annotation from the corresponding {@code @Nested} class.</li>
 *   <li>Run {@code mvn test -Dtest=LoggingBestPracticesTasksTest} — those tests should go GREEN ✅</li>
 *   <li>Repeat until all 5 task groups are GREEN.</li>
 * </ol>
 */
@DisplayName("LoggingBestPracticesTasks — your implementations")
class LoggingBestPracticesTasksTest {

    private LoggingBestPracticesTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new LoggingBestPracticesTasks();
    }

    // ── Task 1: maskCardNumber ────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement maskCardNumber()")
    @DisplayName("Task 1 — maskCardNumber()")
    class MaskCardNumberTest {

        @Test
        @DisplayName("4111111111111111 → ************1111")
        void javadocExample() {
            assertThat(tasks.maskCardNumber("4111111111111111")).isEqualTo("************1111");
        }

        @ParameterizedTest(name = "\"{0}\" → \"{1}\"")
        @CsvSource({
                "12345,       *2345",
                "123456,      **3456",
                "1234,        ****",
                "123,         ***",
                "12,          **",
                "1,           *",
        })
        @DisplayName("keeps the last four characters; shorter inputs are fully masked")
        void lengths(String input, String expected) {
            assertThat(tasks.maskCardNumber(input)).isEqualTo(expected);
        }

        @Test
        @DisplayName("empty String → empty String")
        void emptyInput() {
            assertThat(tasks.maskCardNumber("")).isEmpty();
        }

        @Test
        @DisplayName("null → null")
        void nullInput() {
            assertThat(tasks.maskCardNumber(null)).isNull();
        }

        @Test
        @DisplayName("the result has the same length as the input")
        void sameLength() {
            String card = "5500005555555559";
            assertThat(tasks.maskCardNumber(card)).hasSameSizeAs(card);
        }
    }

    // ── Task 2: maskEmail ─────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement maskEmail()")
    @DisplayName("Task 2 — maskEmail()")
    class MaskEmailTest {

        @Test
        @DisplayName("john.doe@example.com → j*******@example.com")
        void javadocExample() {
            assertThat(tasks.maskEmail("john.doe@example.com")).isEqualTo("j*******@example.com");
        }

        @ParameterizedTest(name = "\"{0}\" → \"{1}\"")
        @CsvSource({
                "ab@x.org,    a*@x.org",
                "abc@x.org,   a**@x.org",
                "a@x.org,     *@x.org",
        })
        @DisplayName("short local parts")
        void shortLocalParts(String input, String expected) {
            assertThat(tasks.maskEmail(input)).isEqualTo(expected);
        }

        @Test
        @DisplayName("no '@' → ***")
        void noAtSign() {
            assertThat(tasks.maskEmail("not-an-email")).isEqualTo("***");
        }

        @Test
        @DisplayName("empty String → ***")
        void emptyInput() {
            assertThat(tasks.maskEmail("")).isEqualTo("***");
        }

        @Test
        @DisplayName("null → ***")
        void nullInput() {
            assertThat(tasks.maskEmail(null)).isEqualTo("***");
        }

        @Test
        @DisplayName("the domain is kept unchanged")
        void domainKept() {
            assertThat(tasks.maskEmail("someone@mail.example.co.uk")).endsWith("@mail.example.co.uk");
        }
    }

    // ── Task 3: redactSecrets ─────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement redactSecrets()")
    @DisplayName("Task 3 — redactSecrets()")
    class RedactSecretsTest {

        @Test
        @DisplayName("user=bob password=hunter2 → user=bob password=***")
        void javadocExample() {
            assertThat(tasks.redactSecrets("user=bob password=hunter2"))
                    .isEqualTo("user=bob password=***");
        }

        @Test
        @DisplayName("key matching is case-insensitive and the key keeps its spelling")
        void caseInsensitive() {
            assertThat(tasks.redactSecrets("TOKEN=abc123 other=1")).isEqualTo("TOKEN=*** other=1");
        }

        @Test
        @DisplayName("all four secret keys are redacted: password, pwd, secret, token")
        void allKeys() {
            assertThat(tasks.redactSecrets("password=a pwd=b secret=c token=d"))
                    .isEqualTo("password=*** pwd=*** secret=*** token=***");
        }

        @Test
        @DisplayName("a value ends at the next whitespace")
        void valueEndsAtWhitespace() {
            assertThat(tasks.redactSecrets("password=hunter2 next=keep me"))
                    .isEqualTo("password=*** next=keep me");
        }

        @Test
        @DisplayName("text without secrets is unchanged")
        void nothingToRedact() {
            assertThat(tasks.redactSecrets("login ok")).isEqualTo("login ok");
            assertThat(tasks.redactSecrets("user=bob")).isEqualTo("user=bob");
        }

        @Test
        @DisplayName("null → null")
        void nullInput() {
            assertThat(tasks.redactSecrets(null)).isNull();
        }
    }

    // ── Task 4: logProgress ───────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement logProgress()")
    @DisplayName("Task 4 — logProgress()")
    class LogProgressTest {

        private void runJob(LogCapture capture, int total, int every) {
            for (int processed = 1; processed <= total; processed++) {
                tasks.logProgress(capture.logger(), processed, total, every);
            }
        }

        @Test
        @DisplayName("total=10, every=5 → \"Processed 5/10 items\" and \"Processed 10/10 items\"")
        void javadocExample() {
            try (LogCapture capture = LogCapture.of("tasks.practices.progress.example")) {
                runJob(capture, 10, 5);

                assertThat(capture.messages()).containsExactly("Processed 5/10 items", "Processed 10/10 items");
            }
        }

        @Test
        @DisplayName("total=7, every=5 → the final item is always reported")
        void finalItemAlwaysReported() {
            try (LogCapture capture = LogCapture.of("tasks.practices.progress.final")) {
                runJob(capture, 7, 5);

                assertThat(capture.messages()).containsExactly("Processed 5/7 items", "Processed 7/7 items");
            }
        }

        @Test
        @DisplayName("total=10, every=3 → logs at 3, 6, 9 and 10")
        void everyThree() {
            try (LogCapture capture = LogCapture.of("tasks.practices.progress.three")) {
                runJob(capture, 10, 3);

                assertThat(capture.messages()).containsExactly(
                        "Processed 3/10 items", "Processed 6/10 items",
                        "Processed 9/10 items", "Processed 10/10 items");
            }
        }

        @ParameterizedTest(name = "total={0} every={1} → {2} line(s)")
        @CsvSource({
                "10, 5,  2",
                "7,  5,  2",
                "3,  10, 1",
                "6,  3,  2",
                "3,  1,  3",
                "1,  1,  1",
        })
        @DisplayName("number of lines written")
        void lineCounts(int total, int every, int expectedLines) {
            try (LogCapture capture = LogCapture.of("tasks.practices.progress.count")) {
                runJob(capture, total, every);

                assertThat(capture.events()).hasSize(expectedLines);
            }
        }

        @Test
        @DisplayName("lines are written at INFO level")
        void infoLevel() {
            try (LogCapture capture = LogCapture.of("tasks.practices.progress.level")) {
                tasks.logProgress(capture.logger(), 5, 10, 5);

                assertThat(capture.single().getLevel()).isEqualTo(Level.INFO);
            }
        }

        @Test
        @DisplayName("uses {} placeholders for the numbers")
        void usesPlaceholders() {
            try (LogCapture capture = LogCapture.of("tasks.practices.progress.placeholders")) {
                tasks.logProgress(capture.logger(), 5, 10, 5);

                assertThat(capture.single().getArgumentArray()).containsExactly(5, 10);
            }
        }

        @ParameterizedTest(name = "every={0} → IllegalArgumentException")
        @CsvSource({"0", "-1", "-100"})
        @DisplayName("every < 1 is rejected before anything is logged")
        void invalidEvery(int every) {
            try (LogCapture capture = LogCapture.of("tasks.practices.progress.invalid")) {
                assertThatThrownBy(() -> tasks.logProgress(capture.logger(), 1, 10, every))
                        .isInstanceOf(IllegalArgumentException.class);
                assertThat(capture.events()).isEmpty();
            }
        }
    }

    // ── Task 5: handleAtBoundary ──────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement handleAtBoundary()")
    @DisplayName("Task 5 — handleAtBoundary()")
    class HandleAtBoundaryTest {

        @Test
        @DisplayName("work that succeeds → true, nothing logged")
        void success() {
            try (LogCapture capture = LogCapture.of("tasks.practices.boundary.success")) {
                boolean ok = tasks.handleAtBoundary(capture.logger(), () -> { });

                assertThat(ok).isTrue();
                assertThat(capture.events()).isEmpty();
            }
        }

        @Test
        @DisplayName("work that throws → false, one ERROR \"Unhandled error in request\" with the exception attached")
        void failure() {
            try (LogCapture capture = LogCapture.of("tasks.practices.boundary.failure")) {
                boolean ok = tasks.handleAtBoundary(capture.logger(), () -> {
                    throw new IllegalStateException("db down");
                });

                assertThat(ok).isFalse();
                assertThat(capture.single().getLevel()).isEqualTo(Level.ERROR);
                assertThat(capture.single().getFormattedMessage()).isEqualTo("Unhandled error in request");
                assertThat(capture.single().getThrowableProxy().getClassName())
                        .isEqualTo("java.lang.IllegalStateException");
                assertThat(capture.single().getThrowableProxy().getMessage()).isEqualTo("db down");
            }
        }

        @Test
        @DisplayName("the exception is not rethrown")
        void notRethrown() {
            try (LogCapture capture = LogCapture.of("tasks.practices.boundary.rethrow")) {
                assertThat(tasks.handleAtBoundary(capture.logger(), () -> {
                    throw new RuntimeException("x");
                })).isFalse();
            }
        }

        @Test
        @DisplayName("the work runs exactly once, whether it succeeds or fails")
        void runsOnce() {
            try (LogCapture capture = LogCapture.of("tasks.practices.boundary.once")) {
                AtomicInteger runs = new AtomicInteger();

                tasks.handleAtBoundary(capture.logger(), runs::incrementAndGet);
                tasks.handleAtBoundary(capture.logger(), () -> {
                    runs.incrementAndGet();
                    throw new RuntimeException("x");
                });

                assertThat(runs.get()).isEqualTo(2);
            }
        }

        @Test
        @DisplayName("a failure with a cause keeps the whole cause chain in the log event")
        void keepsCauseChain() {
            try (LogCapture capture = LogCapture.of("tasks.practices.boundary.cause")) {
                tasks.handleAtBoundary(capture.logger(), () -> {
                    throw new IllegalStateException("outer", new java.io.IOException("inner"));
                });

                assertThat(capture.single().getThrowableProxy().getCause().getClassName())
                        .isEqualTo("java.io.IOException");
            }
        }
    }
}
