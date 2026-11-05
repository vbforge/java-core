package com.vbforge.jvm_basics_memory_and_gc;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

@DisplayName("GcLogsDemo — API contracts")
class GcLogsDemoTest {

    @Nested
    @DisplayName("1. Turning GC logging on")
    class EnablingLogs {

        @Test
        @DisplayName("Java 9+ uses -Xlog")
        void modern() {
            assertThat(GcLogsDemo.loggingFlags(17)).startsWith("-Xlog:gc*");
            assertThat(GcLogsDemo.loggingFlags(9)).startsWith("-Xlog:gc*");
        }

        @Test
        @DisplayName("Java 8 uses the legacy Print flags")
        void legacy() {
            assertThat(GcLogsDemo.loggingFlags(8))
                    .contains("-XX:+PrintGCDetails")
                    .contains("-Xloggc:gc.log");
        }
    }

    @Nested
    @DisplayName("2. Anatomy of a unified GC line")
    class Parsing {

        @Test
        @DisplayName("a young collection line is parsed into its parts")
        void youngLine() {
            GcLogsDemo.GcEvent event = GcLogsDemo.parseUnified(GcLogsDemo.SAMPLE_LOG.get(1)).get();
            assertThat(event.id()).isZero();
            assertThat(event.description()).isEqualTo("Pause Young (Normal) (G1 Evacuation Pause)");
            assertThat(event.beforeKb()).isEqualTo(24 * 1024);
            assertThat(event.afterKb()).isEqualTo(4 * 1024);
            assertThat(event.committedKb()).isEqualTo(256 * 1024);
            assertThat(event.pauseMs()).isCloseTo(3.456, within(1e-9));
            assertThat(event.reclaimedKb()).isEqualTo(20 * 1024);
            assertThat(event.isFull()).isFalse();
        }

        @Test
        @DisplayName("a full collection is recognised")
        void fullLine() {
            GcLogsDemo.GcEvent event = GcLogsDemo.parseUnified(GcLogsDemo.SAMPLE_LOG.get(4)).get();
            assertThat(event.isFull()).isTrue();
            assertThat(event.pauseMs()).isCloseTo(612.345, within(1e-9));
        }

        @Test
        @DisplayName("lines that are not collection summaries yield empty")
        void notACollection() {
            assertThat(GcLogsDemo.parseUnified(GcLogsDemo.SAMPLE_LOG.get(0))).isEqualTo(Optional.empty());
            assertThat(GcLogsDemo.parseUnified("garbage")).isEmpty();
        }

        @ParameterizedTest(name = "{0}{1} -> {2} KB")
        @CsvSource({"512, K, 512", "3, M, 3072", "2, G, 2097152"})
        @DisplayName("toKb normalises K, M and G")
        void units(long value, char unit, long expected) {
            assertThat(GcLogsDemo.toKb(value, unit)).isEqualTo(expected);
        }

        @Test
        @DisplayName("toKb rejects an unknown unit")
        void badUnit() {
            assertThatThrownBy(() -> GcLogsDemo.toKb(1, 'T')).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("3. The legacy format")
    class Legacy {

        private final String line = "[Full GC (Ergonomics)  98304K->40960K(125952K), 0.2500000 secs]";

        @Test
        @DisplayName("seconds are converted to milliseconds")
        void seconds() {
            assertThat(GcLogsDemo.parseLegacy(line).get().pauseMs()).isCloseTo(250.0, within(1e-6));
        }

        @Test
        @DisplayName("sizes are in KB and Full GC is flagged")
        void sizes() {
            GcLogsDemo.GcEvent event = GcLogsDemo.parseLegacy(line).get();
            assertThat(event.beforeKb()).isEqualTo(98_304);
            assertThat(event.afterKb()).isEqualTo(40_960);
            assertThat(event.isFull()).isTrue();
        }

        @Test
        @DisplayName("a minor collection line is not full; other lines give empty")
        void minorAndInvalid() {
            assertThat(GcLogsDemo.parseLegacy("[GC (Allocation Failure)  33280K->4096K(125952K), 0.0050000 secs]")
                    .get().isFull()).isFalse();
            assertThat(GcLogsDemo.parseLegacy("nonsense")).isEmpty();
        }
    }

    @Nested
    @DisplayName("4. Summarising a log")
    class Summary {

        private final List<GcLogsDemo.GcEvent> events = GcLogsDemo.parseAll(GcLogsDemo.SAMPLE_LOG);

        @Test
        @DisplayName("non-collection lines are skipped")
        void skipsNoise() {
            assertThat(events).hasSize(5);
        }

        @Test
        @DisplayName("full collections are counted")
        void fullCount() {
            assertThat(GcLogsDemo.countFull(events)).isEqualTo(1);
        }

        @Test
        @DisplayName("total and maximum pause are computed")
        void pauses() {
            assertThat(GcLogsDemo.totalPauseMs(events)).isCloseTo(634.173, within(1e-6));
            assertThat(GcLogsDemo.maxPauseMs(events)).isCloseTo(612.345, within(1e-9));
        }
    }

    @Nested
    @DisplayName("5. Warning signs")
    class Warnings {

        @Test
        @DisplayName("the saw-tooth in the sample log is not flagged as growth")
        void sampleNotLeak() {
            assertThat(GcLogsDemo.floorKeepsRising(GcLogsDemo.parseAll(GcLogsDemo.SAMPLE_LOG))).isFalse();
        }

        @Test
        @DisplayName("a strictly rising after-GC floor is flagged")
        void rising() {
            List<GcLogsDemo.GcEvent> events = GcLogsDemo.parseAll(Arrays.asList(
                    "[1.0s][info][gc] GC(0) Pause Young (Normal) (G1 Evacuation Pause) 50M->10M(256M) 5.0ms",
                    "[2.0s][info][gc] GC(1) Pause Young (Normal) (G1 Evacuation Pause) 60M->20M(256M) 5.0ms",
                    "[3.0s][info][gc] GC(2) Pause Young (Normal) (G1 Evacuation Pause) 90M->35M(256M) 5.0ms"));
            assertThat(GcLogsDemo.afterValues(events)).containsExactly(10_240L, 20_480L, 35_840L);
            assertThat(GcLogsDemo.floorKeepsRising(events)).isTrue();
        }

        @Test
        @DisplayName("fewer than three events is never flagged")
        void tooFew() {
            assertThat(GcLogsDemo.floorKeepsRising(GcLogsDemo.parseAll(GcLogsDemo.SAMPLE_LOG.subList(1, 3)))).isFalse();
        }
    }
}
