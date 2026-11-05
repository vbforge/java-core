package com.vbforge.jvm_basics_memory_and_gc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

@DisplayName("GcCollectorsTasks — your implementations")
class GcCollectorsTasksTest {

    private GcCollectorsTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new GcCollectorsTasks();
    }

    @Nested
    @Disabled("Remove @Disabled when you implement gcOverheadPercent()")
    @DisplayName("gcOverheadPercent")
    class GcOverheadPercentTest {

        @ParameterizedTest(name = "pause={0} runtime={1} → {2} %")
        @CsvSource({"300, 10000, 3.0", "0, 5000, 0.0", "50, 100, 50.0", "1, 3, 33.333333333333336"})
        @DisplayName("100 * pause / runtime")
        void table(long pause, long runtime, double expected) {
            assertThat(tasks.gcOverheadPercent(pause, runtime)).isCloseTo(expected, within(1e-9));
        }

        @Test
        @DisplayName("zero runtime → IllegalArgumentException")
        void zeroRuntime() {
            assertThatThrownBy(() -> tasks.gcOverheadPercent(10, 0)).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("negative runtime → IllegalArgumentException")
        void negativeRuntime() {
            assertThatThrownBy(() -> tasks.gcOverheadPercent(10, -5)).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement pausePercentile()")
    @DisplayName("pausePercentile")
    class PausePercentileTest {

        private final List<Long> pauses = Arrays.asList(5L, 6L, 5L, 7L, 6L, 5L, 6L, 5L, 7L, 480L);

        @ParameterizedTest(name = "p{0} → {1}")
        @CsvSource({"50, 6", "90, 7", "99, 480", "100, 480", "10, 5"})
        @DisplayName("nearest-rank percentiles of the documented sample")
        void table(double percentile, long expected) {
            assertThat(tasks.pausePercentile(pauses, percentile)).isEqualTo(expected);
        }

        @Test
        @DisplayName("a single sample is every percentile")
        void single() {
            assertThat(tasks.pausePercentile(Collections.singletonList(42L), 99)).isEqualTo(42L);
        }

        @Test
        @DisplayName("empty list → 0")
        void empty() {
            assertThat(tasks.pausePercentile(Collections.<Long>emptyList(), 99)).isZero();
        }

        @Test
        @DisplayName("the input list is not reordered")
        void inputUntouched() {
            List<Long> unsorted = new ArrayList<>(Arrays.asList(9L, 1L, 5L));
            tasks.pausePercentile(unsorted, 50);
            assertThat(unsorted).containsExactly(9L, 1L, 5L);
        }

        @Test
        @DisplayName("a tiny percentile still returns the smallest sample")
        void tinyPercentile() {
            assertThat(tasks.pausePercentile(Arrays.asList(30L, 10L, 20L), 0.1)).isEqualTo(10L);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement recommendCollector()")
    @DisplayName("recommendCollector")
    class RecommendCollectorTest {

        @ParameterizedTest(name = "cpus={0} memory={1} goal={2} → {3}")
        @CsvSource({
                "1, 4096, LATENCY, SERIAL",
                "4, 1024, THROUGHPUT, SERIAL",
                "8, 32768, LATENCY, ZGC",
                "8, 4096, LATENCY, G1",
                "8, 16384, THROUGHPUT, PARALLEL",
                "8, 16384, BALANCED, G1",
                "2, 2048, BALANCED, G1",
                "2, 2047, BALANCED, SERIAL",
                "8, 8192, LATENCY, ZGC",
                "8, 8191, LATENCY, G1"
        })
        @DisplayName("rules are applied in order")
        void table(int cpus, long memory, String goal, String expected) {
            assertThat(tasks.recommendCollector(cpus, memory, goal)).isEqualTo(expected);
        }

        @Test
        @DisplayName("a null goal is treated as balanced")
        void nullGoal() {
            assertThat(tasks.recommendCollector(8, 16384, null)).isEqualTo("G1");
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement jvmFlags()")
    @DisplayName("jvmFlags")
    class JvmFlagsTest {

        @ParameterizedTest(name = "{0} → {1}")
        @CsvSource({
                "SERIAL, '-Xms64m -Xmx256m -XX:+UseSerialGC'",
                "PARALLEL, '-Xms64m -Xmx256m -XX:+UseParallelGC'",
                "G1, '-Xms64m -Xmx256m -XX:+UseG1GC'",
                "ZGC, '-Xms64m -Xmx256m -XX:+UseZGC'"
        })
        @DisplayName("every collector has its flag")
        void table(String collector, String expected) {
            assertThat(tasks.jvmFlags("64m", "256m", collector)).isEqualTo(expected);
        }

        @Test
        @DisplayName("jvmFlags(\"512m\", \"4g\", \"G1\") → \"-Xms512m -Xmx4g -XX:+UseG1GC\"")
        void example() {
            assertThat(tasks.jvmFlags("512m", "4g", "G1")).isEqualTo("-Xms512m -Xmx4g -XX:+UseG1GC");
        }

        @Test
        @DisplayName("an unknown collector → IllegalArgumentException")
        void unknown() {
            assertThatThrownBy(() -> tasks.jvmFlags("64m", "256m", "CMS")).isInstanceOf(IllegalArgumentException.class);
        }
    }
}
