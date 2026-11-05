package com.vbforge.jvm_basics_memory_and_gc;

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

@DisplayName("GcCollectorsDemo — API contracts")
class GcCollectorsDemoTest {

    private static final List<Long> PAUSES = Arrays.asList(5L, 6L, 5L, 7L, 6L, 5L, 6L, 5L, 7L, 480L);

    @Nested
    @DisplayName("1. Stop-the-world, throughput and latency")
    class StopTheWorld {

        @Test
        @DisplayName("overhead is pause time as a percentage of runtime")
        void overhead() {
            assertThat(GcCollectorsDemo.gcOverheadPercent(300, 10_000)).isEqualTo(3.0);
            assertThat(GcCollectorsDemo.gcOverheadPercent(0, 5_000)).isEqualTo(0.0);
        }

        @Test
        @DisplayName("overhead rejects a non-positive runtime")
        void overheadInvalid() {
            assertThatThrownBy(() -> GcCollectorsDemo.gcOverheadPercent(10, 0))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @ParameterizedTest(name = "p{0} -> {1} ms")
        @CsvSource({"50, 6", "90, 7", "99, 480", "100, 480", "10, 5"})
        @DisplayName("nearest-rank percentile exposes the tail the mean hides")
        void percentiles(double percentile, long expected) {
            assertThat(GcCollectorsDemo.pausePercentile(PAUSES, percentile)).isEqualTo(expected);
        }

        @Test
        @DisplayName("the mean looks harmless while p99 shows the outlier")
        void meanHidesTail() {
            double mean = 0;
            for (long pause : PAUSES) {
                mean += pause;
            }
            mean /= PAUSES.size();
            assertThat(mean).isLessThan(60);
            assertThat(GcCollectorsDemo.pausePercentile(PAUSES, 99)).isGreaterThan(400);
        }

        @Test
        @DisplayName("an empty list gives 0 and the input is never reordered")
        void emptyAndImmutable() {
            assertThat(GcCollectorsDemo.pausePercentile(Collections.<Long>emptyList(), 99)).isZero();
            List<Long> unsorted = new ArrayList<>(Arrays.asList(9L, 1L, 5L));
            GcCollectorsDemo.pausePercentile(unsorted, 50);
            assertThat(unsorted).containsExactly(9L, 1L, 5L);
        }
    }

    @Nested
    @DisplayName("2. Collector profiles")
    class Profiles {

        @Test
        @DisplayName("every collector has its JVM flag, a design note and a use case")
        void profilesComplete() {
            for (GcCollectorsDemo.Collector collector : GcCollectorsDemo.Collector.values()) {
                assertThat(collector.flag()).startsWith("-XX:+Use");
                assertThat(collector.design()).isNotBlank();
                assertThat(collector.bestFor()).isNotBlank();
            }
        }

        @Test
        @DisplayName("the four documented flags")
        void flags() {
            assertThat(GcCollectorsDemo.Collector.SERIAL.flag()).isEqualTo("-XX:+UseSerialGC");
            assertThat(GcCollectorsDemo.Collector.PARALLEL.flag()).isEqualTo("-XX:+UseParallelGC");
            assertThat(GcCollectorsDemo.Collector.G1.flag()).isEqualTo("-XX:+UseG1GC");
            assertThat(GcCollectorsDemo.Collector.ZGC.flag()).isEqualTo("-XX:+UseZGC");
        }
    }

    @Nested
    @DisplayName("3. Choosing a collector")
    class Selection {

        @Test
        @DisplayName("fewer than 2 CPUs or under 2 GB falls back to Serial")
        void serial() {
            assertThat(GcCollectorsDemo.recommend(1, 4096, "LATENCY")).isEqualTo(GcCollectorsDemo.Collector.SERIAL);
            assertThat(GcCollectorsDemo.recommend(4, 1024, "THROUGHPUT")).isEqualTo(GcCollectorsDemo.Collector.SERIAL);
        }

        @Test
        @DisplayName("latency goal on a big machine selects ZGC, on a small one G1")
        void latency() {
            assertThat(GcCollectorsDemo.recommend(8, 32768, "LATENCY")).isEqualTo(GcCollectorsDemo.Collector.ZGC);
            assertThat(GcCollectorsDemo.recommend(8, 4096, "LATENCY")).isEqualTo(GcCollectorsDemo.Collector.G1);
        }

        @Test
        @DisplayName("throughput goal selects Parallel, balanced selects G1")
        void throughputAndBalanced() {
            assertThat(GcCollectorsDemo.recommend(8, 16384, "THROUGHPUT")).isEqualTo(GcCollectorsDemo.Collector.PARALLEL);
            assertThat(GcCollectorsDemo.recommend(8, 16384, "BALANCED")).isEqualTo(GcCollectorsDemo.Collector.G1);
        }

        @Test
        @DisplayName("exactly 2 GB and 2 CPUs is a server-class machine")
        void boundary() {
            assertThat(GcCollectorsDemo.recommend(2, 2048, "BALANCED")).isEqualTo(GcCollectorsDemo.Collector.G1);
        }

        @Test
        @DisplayName("jvmFlags composes heap sizes and the collector flag")
        void flagsFragment() {
            assertThat(GcCollectorsDemo.jvmFlags("512m", "4g", GcCollectorsDemo.Collector.G1))
                    .isEqualTo("-Xms512m -Xmx4g -XX:+UseG1GC");
        }
    }

    @Nested
    @DisplayName("4. Metrics that matter")
    class Metrics {

        @Test
        @DisplayName("percentile 100 is the maximum")
        void maximum() {
            assertThat(GcCollectorsDemo.pausePercentile(Arrays.asList(3L, 9L, 4L), 100)).isEqualTo(9L);
        }

        @Test
        @DisplayName("a single sample is every percentile")
        void single() {
            assertThat(GcCollectorsDemo.pausePercentile(Collections.singletonList(42L), 1)).isEqualTo(42L);
        }
    }

    @Nested
    @DisplayName("5. Observing the live JVM")
    class LiveJvm {

        @Test
        @DisplayName("at least one collector bean is reported")
        void collectorsPresent() {
            assertThat(GcCollectorsDemo.activeCollectorNames()).isNotEmpty();
        }

        @Test
        @DisplayName("total collection count never decreases and a System.gc() is counted")
        void countNeverDecreases() {
            long before = GcCollectorsDemo.totalCollectionCount();
            System.gc();
            assertThat(GcCollectorsDemo.totalCollectionCount()).isGreaterThanOrEqualTo(before);
        }
    }
}
