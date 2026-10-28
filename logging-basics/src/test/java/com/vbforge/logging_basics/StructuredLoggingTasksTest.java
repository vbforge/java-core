package com.vbforge.logging_basics;

import ch.qos.logback.classic.Level;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.slf4j.MDC;
import org.slf4j.Marker;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * StructuredLoggingTasksTest — tests for every method in {@link StructuredLoggingTasks}.
 *
 * <p><b>Getting started:</b>
 * <ol>
 *   <li>Open {@code StructuredLoggingTasks.java} and implement a method.</li>
 *   <li>Remove the {@code @Disabled} annotation from the corresponding {@code @Nested} class.</li>
 *   <li>Run {@code mvn test -Dtest=StructuredLoggingTasksTest} — those tests should go GREEN ✅</li>
 *   <li>Repeat until all 5 task groups are GREEN.</li>
 * </ol>
 */
@DisplayName("StructuredLoggingTasks — your implementations")
class StructuredLoggingTasksTest {

    private StructuredLoggingTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new StructuredLoggingTasks();
        MDC.clear();   // start every test with an empty MDC
    }

    /** Builds an insertion-ordered map from alternating key, value arguments. */
    private static Map<String, Object> fields(Object... keysAndValues) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (int i = 0; i < keysAndValues.length; i += 2) {
            map.put((String) keysAndValues[i], keysAndValues[i + 1]);
        }
        return map;
    }

    // ── Task 1: runWithContext ────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement runWithContext()")
    @DisplayName("Task 1 — runWithContext()")
    class RunWithContextTest {

        @Test
        @DisplayName("while the action runs, requestId and userId are in the MDC; afterwards they are gone")
        void javadocExample() {
            AtomicReference<String> requestIdInside = new AtomicReference<>();
            AtomicReference<String> userIdInside = new AtomicReference<>();

            tasks.runWithContext("req-1", "u-7", () -> {
                requestIdInside.set(MDC.get("requestId"));
                userIdInside.set(MDC.get("userId"));
            });

            assertThat(requestIdInside.get()).isEqualTo("req-1");
            assertThat(userIdInside.get()).isEqualTo("u-7");
            assertThat(MDC.get("requestId")).isNull();
            assertThat(MDC.get("userId")).isNull();
        }

        @Test
        @DisplayName("if the action throws, the exception propagates and the MDC is still clean")
        void cleansUpOnException() {
            assertThatThrownBy(() -> tasks.runWithContext("req-1", "u-7", () -> {
                throw new IllegalStateException("boom");
            })).isInstanceOf(IllegalStateException.class).hasMessage("boom");

            assertThat(MDC.get("requestId")).isNull();
            assertThat(MDC.get("userId")).isNull();
        }

        @Test
        @DisplayName("log events written by the action carry both entries")
        void logEventsCarryContext() {
            try (LogCapture capture = LogCapture.of("tasks.structured.context.events")) {
                tasks.runWithContext("req-1", "u-7", () -> capture.logger().info("working"));

                assertThat(capture.single().getMDCPropertyMap())
                        .containsEntry("requestId", "req-1")
                        .containsEntry("userId", "u-7");
            }
        }

        @Test
        @DisplayName("the action runs exactly once")
        void runsOnce() {
            AtomicInteger runs = new AtomicInteger();

            tasks.runWithContext("req-1", "u-7", runs::incrementAndGet);

            assertThat(runs.get()).isEqualTo(1);
        }
    }

    // ── Task 2: toLogfmt ──────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement toLogfmt()")
    @DisplayName("Task 2 — toLogfmt()")
    class ToLogfmtTest {

        @Test
        @DisplayName("{user=alice, count=3} → user=alice count=3")
        void javadocExample() {
            assertThat(tasks.toLogfmt(fields("user", "alice", "count", 3)))
                    .isEqualTo("user=alice count=3");
        }

        @Test
        @DisplayName("a value with a space is quoted")
        void spaceIsQuoted() {
            assertThat(tasks.toLogfmt(fields("msg", "hello world")))
                    .isEqualTo("msg=\"hello world\"");
        }

        @Test
        @DisplayName("double quotes inside a value are escaped with a backslash")
        void quotesAreEscaped() {
            assertThat(tasks.toLogfmt(fields("quote", "say \"hi\"")))
                    .isEqualTo("quote=\"say \\\"hi\\\"\"");
        }

        @Test
        @DisplayName("a value containing '=' is quoted")
        void equalsSignIsQuoted() {
            assertThat(tasks.toLogfmt(fields("expr", "a=b"))).isEqualTo("expr=\"a=b\"");
        }

        @Test
        @DisplayName("an empty value becomes \"\"")
        void emptyValue() {
            assertThat(tasks.toLogfmt(fields("name", ""))).isEqualTo("name=\"\"");
        }

        @Test
        @DisplayName("a null value is written as null")
        void nullValue() {
            assertThat(tasks.toLogfmt(fields("missing", null))).isEqualTo("missing=null");
        }

        @Test
        @DisplayName("an empty map gives an empty String")
        void emptyMap() {
            assertThat(tasks.toLogfmt(fields())).isEmpty();
        }

        @Test
        @DisplayName("fields keep the iteration order of the map, separated by single spaces")
        void keepsOrder() {
            assertThat(tasks.toLogfmt(fields("z", 1, "a", 2, "m", "x y")))
                    .isEqualTo("z=1 a=2 m=\"x y\"");
        }
    }

    // ── Task 3: logOrderPlaced ────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement logOrderPlaced()")
    @DisplayName("Task 3 — logOrderPlaced()")
    class LogOrderPlacedTest {

        @Test
        @DisplayName("A-100, 3 → INFO \"Order placed\" with orderId=A-100, itemCount=3")
        void javadocExample() {
            try (LogCapture capture = LogCapture.of("tasks.structured.order.example")) {
                tasks.logOrderPlaced(capture.logger(), "A-100", 3);

                assertThat(capture.single().getLevel()).isEqualTo(Level.INFO);
                assertThat(capture.single().getFormattedMessage()).isEqualTo("Order placed");
                assertThat(capture.single().getKeyValuePairs())
                        .extracting(kv -> kv.key + "=" + kv.value)
                        .containsExactly("orderId=A-100", "itemCount=3");
            }
        }

        @Test
        @DisplayName("the message text does not contain the order id or the item count")
        void messageStaysConstant() {
            try (LogCapture capture = LogCapture.of("tasks.structured.order.constant")) {
                tasks.logOrderPlaced(capture.logger(), "B-777", 42);

                assertThat(capture.single().getFormattedMessage())
                        .doesNotContain("B-777")
                        .doesNotContain("42");
            }
        }

        @Test
        @DisplayName("works for other values")
        void otherValues() {
            try (LogCapture capture = LogCapture.of("tasks.structured.order.other")) {
                tasks.logOrderPlaced(capture.logger(), "C-1", 0);

                assertThat(capture.single().getKeyValuePairs())
                        .extracting(kv -> kv.key + "=" + kv.value)
                        .containsExactly("orderId=C-1", "itemCount=0");
            }
        }
    }

    // ── Task 4: escapeJson ────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement escapeJson()")
    @DisplayName("Task 4 — escapeJson()")
    class EscapeJsonTest {

        @Test
        @DisplayName("plain text is unchanged")
        void plainText() {
            assertThat(tasks.escapeJson("plain")).isEqualTo("plain");
        }

        @Test
        @DisplayName("double quotes are escaped")
        void quotes() {
            assertThat(tasks.escapeJson("say \"hi\"")).isEqualTo("say \\\"hi\\\"");
        }

        @Test
        @DisplayName("each backslash is doubled")
        void backslashes() {
            assertThat(tasks.escapeJson("C:\\temp")).isEqualTo("C:\\\\temp");
        }

        @Test
        @DisplayName("newline, carriage return and tab become \\n, \\r and \\t")
        void whitespaceControls() {
            assertThat(tasks.escapeJson("line1\nline2")).isEqualTo("line1\\nline2");
            assertThat(tasks.escapeJson("a\rb")).isEqualTo("a\\rb");
            assertThat(tasks.escapeJson("tab\there")).isEqualTo("tab\\there");
        }

        @ParameterizedTest(name = "char code {0} → {1}")
        @CsvSource({
                "1,  \\u0001",
                "7,  \\u0007",
                "27, \\u001b",
                "31, \\u001f",
        })
        @DisplayName("other control characters become backslash-u plus four lower-case hex digits")
        void otherControlCharacters(int code, String expected) {
            assertThat(tasks.escapeJson("x" + (char) code)).isEqualTo("x" + expected);
        }

        @Test
        @DisplayName("characters from U+0020 upwards are copied unchanged")
        void printableCharactersUnchanged() {
            assertThat(tasks.escapeJson("a b/c é €")).isEqualTo("a b/c é €");
        }

        @Test
        @DisplayName("empty String → empty String; null → empty String")
        void emptyAndNull() {
            assertThat(tasks.escapeJson("")).isEmpty();
            assertThat(tasks.escapeJson(null)).isEmpty();
        }
    }

    // ── Task 5: logAudit ──────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement logAudit()")
    @DisplayName("Task 5 — logAudit()")
    class LogAuditTest {

        @Test
        @DisplayName("delete by alice → INFO [AUDIT] \"User alice performed delete\"")
        void javadocExample() {
            try (LogCapture capture = LogCapture.of("tasks.structured.audit.example")) {
                tasks.logAudit(capture.logger(), "delete", "alice");

                assertThat(capture.single().getLevel()).isEqualTo(Level.INFO);
                assertThat(capture.single().getFormattedMessage()).isEqualTo("User alice performed delete");
                assertThat(capture.single().getMarkerList()).extracting(Marker::getName).contains("AUDIT");
            }
        }

        @ParameterizedTest(name = "action={0} user={1}")
        @CsvSource({
                "login,  bob",
                "export, carol",
                "delete, dave",
        })
        @DisplayName("works for other actions and users")
        void otherValues(String action, String user) {
            try (LogCapture capture = LogCapture.of("tasks.structured.audit.values")) {
                tasks.logAudit(capture.logger(), action, user);

                assertThat(capture.single().getFormattedMessage())
                        .isEqualTo("User " + user + " performed " + action);
                assertThat(capture.single().getMarkerList()).extracting(Marker::getName).contains("AUDIT");
            }
        }
    }
}
