package com.vbforge.logging_basics;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.JsonEncoder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * StructuredLoggingDemoTest — verifies every concept shown in {@link StructuredLoggingDemo}.
 *
 * <p>These tests are always GREEN. Read them as executable specification of
 * MDC, key-value pairs, JSON encoding and markers.
 */
@DisplayName("StructuredLoggingDemo — API contracts")
class StructuredLoggingDemoTest {

    // ── 1. MDC ────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("1. MDC")
    class MdcTest {

        @Test
        @DisplayName("put / get / remove manage single entries")
        void putGetRemove() {
            try {
                MDC.put("requestId", "req-42");
                assertThat(MDC.get("requestId")).isEqualTo("req-42");

                MDC.remove("requestId");
                assertThat(MDC.get("requestId")).isNull();
            } finally {
                MDC.clear();
            }
        }

        @Test
        @DisplayName("clear() removes every entry")
        void clear() {
            MDC.put("a", "1");
            MDC.put("b", "2");

            MDC.clear();

            assertThat(MDC.get("a")).isNull();
            assertThat(MDC.get("b")).isNull();
        }

        @Test
        @DisplayName("events logged while entries are set carry them")
        void eventsCarryMdc() {
            try (LogCapture capture = LogCapture.of("demo.test.structured.mdc")) {
                MDC.put("requestId", "req-42");
                MDC.put("userId", "alice");
                try {
                    capture.logger().info("handling request");
                } finally {
                    MDC.clear();
                }

                assertThat(capture.single().getMDCPropertyMap())
                        .containsEntry("requestId", "req-42")
                        .containsEntry("userId", "alice");
            }
        }

        @Test
        @DisplayName("events logged after remove() no longer carry the entry")
        void removedEntryIsNotCarried() {
            try (LogCapture capture = LogCapture.of("demo.test.structured.mdc.removed")) {
                MDC.put("userId", "alice");
                MDC.remove("userId");
                capture.logger().info("after remove");

                assertThat(capture.single().getMDCPropertyMap()).doesNotContainKey("userId");
            } finally {
                MDC.clear();
            }
        }

        @Test
        @DisplayName("the MDC is per thread: a thread that already exists does not see our entries")
        void mdcIsPerThread() throws Exception {
            ExecutorService pool = Executors.newSingleThreadExecutor();
            try {
                pool.submit(() -> { }).get();               // worker exists BEFORE we use the MDC

                MDC.put("requestId", "req-42");
                String seenByWorker = pool.submit(() -> MDC.get("requestId")).get();

                assertThat(seenByWorker).isNull();
                assertThat(MDC.get("requestId")).isEqualTo("req-42");
            } finally {
                pool.shutdown();
                MDC.clear();
            }
        }
    }

    // ── 2. MDC cleanup ────────────────────────────────────────────────────────

    @Nested
    @DisplayName("2. MDC cleanup")
    class MdcCleanupTest {

        @Test
        @DisplayName("putCloseable() sets the entry inside the block and removes it afterwards")
        void putCloseable() {
            try (MDC.MDCCloseable ignored = MDC.putCloseable("orderId", "A-100")) {
                assertThat(MDC.get("orderId")).isEqualTo("A-100");
            }
            assertThat(MDC.get("orderId")).isNull();
        }

        @Test
        @DisplayName("putCloseable() also cleans up when the block throws")
        void putCloseableOnException() {
            assertThatThrownBy(() -> {
                try (MDC.MDCCloseable ignored = MDC.putCloseable("orderId", "A-100")) {
                    throw new IllegalStateException("boom");
                }
            }).isInstanceOf(IllegalStateException.class);

            assertThat(MDC.get("orderId")).isNull();
        }

        @Test
        @DisplayName("try/finally with MDC.remove() is the manual equivalent")
        void tryFinally() {
            MDC.put("orderId", "A-101");
            try {
                assertThat(MDC.get("orderId")).isEqualTo("A-101");
            } finally {
                MDC.remove("orderId");
            }
            assertThat(MDC.get("orderId")).isNull();
        }
    }

    // ── 3. Key-value pairs ────────────────────────────────────────────────────

    @Nested
    @DisplayName("3. Key-value pairs")
    class KeyValueTest {

        @Test
        @DisplayName("the fluent API attaches named fields to the event, in order")
        void fluentApiAttachesFields() {
            try (LogCapture capture = LogCapture.of("demo.test.structured.kv")) {
                capture.logger().atInfo()
                        .addKeyValue("orderId", "A-100")
                        .addKeyValue("items", 3)
                        .log("Order placed");

                assertThat(capture.single().getKeyValuePairs())
                        .extracting(kv -> kv.key + "=" + kv.value)
                        .containsExactly("orderId=A-100", "items=3");
            }
        }

        @Test
        @DisplayName("the message of a structured event stays constant")
        void messageStaysConstant() {
            try (LogCapture capture = LogCapture.of("demo.test.structured.kv.message")) {
                capture.logger().atInfo().addKeyValue("orderId", "A-100").log("Order placed");

                assertThat(capture.single().getFormattedMessage()).isEqualTo("Order placed");
            }
        }

        @Test
        @DisplayName("the key=value text style bakes the facts into the message")
        void textStyle() {
            try (LogCapture capture = LogCapture.of("demo.test.structured.kv.text")) {
                capture.logger().info("Order placed orderId={} items={}", "A-100", 3);

                assertThat(capture.single().getFormattedMessage())
                        .isEqualTo("Order placed orderId=A-100 items=3");
            }
        }
    }

    // ── 4. JSON encoding ──────────────────────────────────────────────────────

    @Nested
    @DisplayName("4. JSON encoding")
    class JsonEncodingTest {

        @Test
        @DisplayName("JsonEncoder renders one JSON object with level, message and MDC")
        void jsonEncoder() {
            LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
            JsonEncoder encoder = new JsonEncoder();
            encoder.setContext(context);
            encoder.start();

            try (LogCapture capture = LogCapture.of("demo.test.structured.json")) {
                MDC.put("requestId", "req-42");
                try {
                    capture.logger().atInfo().addKeyValue("orderId", "A-100").log("Order placed");
                } finally {
                    MDC.clear();
                }

                String json = new String(encoder.encode(capture.single()), StandardCharsets.UTF_8).trim();

                assertThat(json).startsWith("{").endsWith("}");
                assertThat(json).contains("INFO");
                assertThat(json).contains("Order placed");
                assertThat(json).contains("req-42");
            }
        }
    }

    // ── 5. Markers ────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("5. Markers")
    class MarkersTest {

        @Test
        @DisplayName("a marker name always resolves to the same instance")
        void markersAreSingletons() {
            assertThat(MarkerFactory.getMarker("AUDIT")).isSameAs(MarkerFactory.getMarker("AUDIT"));
            assertThat(MarkerFactory.getMarker("AUDIT")).isNotSameAs(MarkerFactory.getMarker("SECURITY"));
        }

        @Test
        @DisplayName("the classic API tags an event with the marker")
        void classicApi() {
            Marker audit = MarkerFactory.getMarker("AUDIT");
            try (LogCapture capture = LogCapture.of("demo.test.structured.marker.classic")) {
                capture.logger().info(audit, "User {} deleted account {}", "alice", 42);

                assertThat(capture.single().getFormattedMessage()).isEqualTo("User alice deleted account 42");
                assertThat(capture.single().getMarkerList()).extracting(Marker::getName).contains("AUDIT");
            }
        }

        @Test
        @DisplayName("the fluent API tags an event with the marker")
        void fluentApi() {
            Marker audit = MarkerFactory.getMarker("AUDIT");
            try (LogCapture capture = LogCapture.of("demo.test.structured.marker.fluent")) {
                capture.logger().atInfo().addMarker(audit).log("fluent variant");

                assertThat(capture.single().getMarkerList()).extracting(Marker::getName).contains("AUDIT");
            }
        }
    }

    // ── main() ────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("6. The demo itself")
    class DemoMainTest {

        @Test
        @DisplayName("main() runs every section without throwing")
        void mainRuns() {
            assertThatCode(() -> StructuredLoggingDemo.main(new String[0])).doesNotThrowAnyException();
        }
    }
}
