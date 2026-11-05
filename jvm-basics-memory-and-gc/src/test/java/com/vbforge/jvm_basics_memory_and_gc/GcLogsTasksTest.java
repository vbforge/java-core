package com.vbforge.jvm_basics_memory_and_gc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

@DisplayName("GcLogsTasks — your implementations")
class GcLogsTasksTest {

    private GcLogsTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new GcLogsTasks();
    }

    private static String young(int id, String before, String after, String pause) {
        return "[1.0s][info][gc] GC(" + id + ") Pause Young (Normal) (G1 Evacuation Pause) "
                + before + "->" + after + "(256M) " + pause + "ms";
    }

    private static String full(int id, String before, String after, String pause) {
        return "[1.0s][info][gc] GC(" + id + ") Pause Full (G1 Compaction Pause) "
                + before + "->" + after + "(256M) " + pause + "ms";
    }

    private static final String NOISE = "[0.5s][info][gc] Using G1";

    @Nested
    @Disabled("Remove @Disabled when you implement reclaimedKb()")
    @DisplayName("reclaimedKb")
    class ReclaimedKbTest {

        @Test
        @DisplayName("24M->4M → 20480")
        void megabytes() {
            assertThat(tasks.reclaimedKb(young(0, "24M", "4M", "3.456"))).isEqualTo(20_480L);
        }

        @Test
        @DisplayName("2G->512M → 1572864")
        void gigabytesToMegabytes() {
            assertThat(tasks.reclaimedKb(young(5, "2G", "512M", "10.0"))).isEqualTo(1_572_864L);
        }

        @Test
        @DisplayName("900K->100K → 800")
        void kilobytes() {
            assertThat(tasks.reclaimedKb(young(1, "900K", "100K", "1.0"))).isEqualTo(800L);
        }

        @Test
        @DisplayName("nothing freed → 0")
        void nothingFreed() {
            assertThat(tasks.reclaimedKb(full(2, "250M", "250M", "800.0"))).isZero();
        }

        @Test
        @DisplayName("a non-collection line → IllegalArgumentException")
        void notACollection() {
            assertThatThrownBy(() -> tasks.reclaimedKb(NOISE)).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement pauseMs()")
    @DisplayName("pauseMs")
    class PauseMsTest {

        @Test
        @DisplayName("young line → 3.456")
        void young() {
            assertThat(tasks.pauseMs(GcLogsTasksTest.young(0, "24M", "4M", "3.456"))).isCloseTo(3.456, within(1e-9));
        }

        @Test
        @DisplayName("full line → 612.345")
        void full() {
            assertThat(tasks.pauseMs(GcLogsTasksTest.full(3, "250M", "48M", "612.345"))).isCloseTo(612.345, within(1e-9));
        }

        @Test
        @DisplayName("an integral duration such as 7ms is parsed")
        void integral() {
            assertThat(tasks.pauseMs(GcLogsTasksTest.young(1, "10M", "5M", "7"))).isCloseTo(7.0, within(1e-9));
        }

        @Test
        @DisplayName("a non-collection line → IllegalArgumentException")
        void notACollection() {
            assertThatThrownBy(() -> tasks.pauseMs(NOISE)).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement totalPauseMs()")
    @DisplayName("totalPauseMs")
    class TotalPauseMsTest {

        @Test
        @DisplayName("noise + 3.0ms + 7.0ms → 10.0")
        void example() {
            assertThat(tasks.totalPauseMs(Arrays.asList(
                    NOISE,
                    GcLogsTasksTest.young(0, "10M", "5M", "3.0"),
                    GcLogsTasksTest.young(1, "10M", "5M", "7.0")))).isCloseTo(10.0, within(1e-9));
        }

        @Test
        @DisplayName("only noise → 0.0")
        void onlyNoise() {
            assertThat(tasks.totalPauseMs(Collections.singletonList(NOISE))).isZero();
        }

        @Test
        @DisplayName("empty log → 0.0")
        void empty() {
            assertThat(tasks.totalPauseMs(Collections.<String>emptyList())).isZero();
        }

        @Test
        @DisplayName("the shared sample log sums to 634.173")
        void sampleLog() {
            assertThat(tasks.totalPauseMs(GcLogsDemo.SAMPLE_LOG)).isCloseTo(634.173, within(1e-6));
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement countFullGcs()")
    @DisplayName("countFullGcs")
    class CountFullGcsTest {

        @Test
        @DisplayName("[Young, Young, Full, Young, Full] → 2")
        void example() {
            assertThat(tasks.countFullGcs(Arrays.asList(
                    GcLogsTasksTest.young(0, "10M", "5M", "1.0"),
                    GcLogsTasksTest.young(1, "10M", "5M", "1.0"),
                    GcLogsTasksTest.full(2, "200M", "50M", "500.0"),
                    GcLogsTasksTest.young(3, "10M", "5M", "1.0"),
                    GcLogsTasksTest.full(4, "200M", "60M", "500.0")))).isEqualTo(2);
        }

        @Test
        @DisplayName("only young collections → 0")
        void none() {
            assertThat(tasks.countFullGcs(Arrays.asList(
                    GcLogsTasksTest.young(0, "10M", "5M", "1.0"),
                    GcLogsTasksTest.young(1, "10M", "5M", "1.0")))).isZero();
        }

        @Test
        @DisplayName("empty log → 0")
        void empty() {
            assertThat(tasks.countFullGcs(Collections.<String>emptyList())).isZero();
        }

        @Test
        @DisplayName("a non-summary line that merely mentions 'Pause Full' is not counted")
        void mentionDoesNotCount() {
            assertThat(tasks.countFullGcs(Collections.singletonList(
                    "[0.5s][info][gc] note: Pause Full is rare"))).isZero();
        }

        @Test
        @DisplayName("the shared sample log has one full collection")
        void sampleLog() {
            assertThat(tasks.countFullGcs(GcLogsDemo.SAMPLE_LOG)).isEqualTo(1);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement heapAfterGcGrowing()")
    @DisplayName("heapAfterGcGrowing")
    class HeapAfterGcGrowingTest {

        @Test
        @DisplayName("after-values 10M, 20M, 35M, 50M → true")
        void rising() {
            assertThat(tasks.heapAfterGcGrowing(Arrays.asList(
                    GcLogsTasksTest.young(0, "60M", "10M", "1.0"),
                    GcLogsTasksTest.young(1, "60M", "20M", "1.0"),
                    GcLogsTasksTest.young(2, "60M", "35M", "1.0"),
                    GcLogsTasksTest.young(3, "90M", "50M", "1.0")))).isTrue();
        }

        @Test
        @DisplayName("after-values 10M, 20M, 20M → false (not strictly rising)")
        void plateau() {
            assertThat(tasks.heapAfterGcGrowing(Arrays.asList(
                    GcLogsTasksTest.young(0, "60M", "10M", "1.0"),
                    GcLogsTasksTest.young(1, "60M", "20M", "1.0"),
                    GcLogsTasksTest.young(2, "60M", "20M", "1.0")))).isFalse();
        }

        @Test
        @DisplayName("after-values 10M, 30M, 12M, 40M → false (saw-tooth)")
        void sawTooth() {
            assertThat(tasks.heapAfterGcGrowing(Arrays.asList(
                    GcLogsTasksTest.young(0, "60M", "10M", "1.0"),
                    GcLogsTasksTest.young(1, "60M", "30M", "1.0"),
                    GcLogsTasksTest.young(2, "60M", "12M", "1.0"),
                    GcLogsTasksTest.young(3, "90M", "40M", "1.0")))).isFalse();
        }

        @Test
        @DisplayName("fewer than three events → false")
        void tooFew() {
            assertThat(tasks.heapAfterGcGrowing(Arrays.asList(
                    GcLogsTasksTest.young(0, "60M", "10M", "1.0"),
                    GcLogsTasksTest.young(1, "60M", "20M", "1.0")))).isFalse();
        }

        @Test
        @DisplayName("after-values 900K, 1M, 2M → true (units are normalised)")
        void mixedUnits() {
            assertThat(tasks.heapAfterGcGrowing(Arrays.asList(
                    GcLogsTasksTest.young(0, "5M", "900K", "1.0"),
                    GcLogsTasksTest.young(1, "5M", "1M", "1.0"),
                    GcLogsTasksTest.young(2, "5M", "2M", "1.0")))).isTrue();
        }

        @Test
        @DisplayName("non-collection lines are ignored")
        void noiseIgnored() {
            assertThat(tasks.heapAfterGcGrowing(Arrays.asList(
                    NOISE,
                    GcLogsTasksTest.young(0, "60M", "10M", "1.0"),
                    NOISE,
                    GcLogsTasksTest.young(1, "60M", "20M", "1.0"),
                    GcLogsTasksTest.young(2, "60M", "30M", "1.0")))).isTrue();
        }

        @Test
        @DisplayName("a smaller unit value that is numerically larger is not a rise (2M then 900K)")
        void unitsNotCompared() {
            assertThat(tasks.heapAfterGcGrowing(Arrays.asList(
                    GcLogsTasksTest.young(0, "5M", "1M", "1.0"),
                    GcLogsTasksTest.young(1, "5M", "2M", "1.0"),
                    GcLogsTasksTest.young(2, "5M", "900K", "1.0")))).isFalse();
        }
    }
}
