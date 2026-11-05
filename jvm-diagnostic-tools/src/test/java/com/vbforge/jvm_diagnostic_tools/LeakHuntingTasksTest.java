package com.vbforge.jvm_diagnostic_tools;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("LeakHuntingTasks — your implementations")
class LeakHuntingTasksTest {

    private LeakHuntingTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new LeakHuntingTasks();
    }

    private static Map<String, Long> histogram(Object... pairs) {
        Map<String, Long> map = new HashMap<>();
        for (int i = 0; i < pairs.length; i += 2) {
            map.put((String) pairs[i], ((Number) pairs[i + 1]).longValue());
        }
        return map;
    }

    @Nested
    @Disabled("Remove @Disabled when you implement isLeakSuspect()")
    @DisplayName("isLeakSuspect")
    class IsLeakSuspect {

        @Test
        @DisplayName("a staircase is a leak (Javadoc example)")
        void staircase() {
            assertThat(tasks.isLeakSuspect(new long[]{100, 150, 150, 200})).isTrue();
        }

        @Test
        @DisplayName("a flat line is healthy (Javadoc example)")
        void flat() {
            assertThat(tasks.isLeakSuspect(new long[]{100, 100, 100, 100})).isFalse();
        }

        @Test
        @DisplayName("a dip means the GC freed memory (Javadoc example)")
        void dip() {
            assertThat(tasks.isLeakSuspect(new long[]{100, 200, 150, 300})).isFalse();
        }

        @Test
        @DisplayName("three samples are too few (Javadoc example)")
        void tooFew() {
            assertThat(tasks.isLeakSuspect(new long[]{100, 200, 300})).isFalse();
        }

        @Test
        @DisplayName("null and empty input are not a leak")
        void nullAndEmpty() {
            assertThat(tasks.isLeakSuspect(null)).isFalse();
            assertThat(tasks.isLeakSuspect(new long[0])).isFalse();
        }

        @Test
        @DisplayName("a long strictly rising series is a leak")
        void longSeries() {
            assertThat(tasks.isLeakSuspect(new long[]{1, 2, 3, 4, 5, 6, 7, 8})).isTrue();
        }

        @Test
        @DisplayName("a single drop at the end breaks the pattern")
        void dropAtEnd() {
            assertThat(tasks.isLeakSuspect(new long[]{100, 200, 300, 400, 399})).isFalse();
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement growthByClass()")
    @DisplayName("growthByClass")
    class GrowthByClass {

        @Test
        @DisplayName("reports growth and new classes (Javadoc example)")
        void javadocExample() {
            Map<String, Long> growth = tasks.growthByClass(
                    histogram("A", 100, "B", 200),
                    histogram("A", 150, "B", 200, "C", 40));
            assertThat(growth).hasSize(2).containsEntry("A", 50L).containsEntry("C", 40L);
        }

        @Test
        @DisplayName("shrinking classes are left out (Javadoc example)")
        void shrinking() {
            assertThat(tasks.growthByClass(histogram("A", 100), histogram("A", 60))).isEmpty();
        }

        @Test
        @DisplayName("two empty histograms give an empty result (Javadoc example)")
        void bothEmpty() {
            assertThat(tasks.growthByClass(Collections.<String, Long>emptyMap(), Collections.<String, Long>emptyMap()))
                    .isEmpty();
        }

        @Test
        @DisplayName("classes that disappeared are left out")
        void disappeared() {
            assertThat(tasks.growthByClass(histogram("A", 100), Collections.<String, Long>emptyMap())).isEmpty();
        }

        @Test
        @DisplayName("the arguments are not modified")
        void argumentsUntouched() {
            Map<String, Long> before = histogram("A", 100);
            Map<String, Long> after = histogram("A", 150);

            Map<String, Long> growth = tasks.growthByClass(before, after);

            assertThat(growth).isNotSameAs(after);
            assertThat(before).containsOnlyKeys("A").containsEntry("A", 100L);
            assertThat(after).containsOnlyKeys("A").containsEntry("A", 150L);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement biggestGrower()")
    @DisplayName("biggestGrower")
    class BiggestGrower {

        @Test
        @DisplayName("names the class with the largest growth (Javadoc example)")
        void javadocExample() {
            assertThat(tasks.biggestGrower(histogram("A", 100, "B", 200), histogram("A", 150, "B", 900)))
                    .isEqualTo("B");
        }

        @Test
        @DisplayName("ties go to the alphabetically first class (Javadoc example)")
        void tie() {
            assertThat(tasks.biggestGrower(histogram("A", 100, "B", 100), histogram("A", 150, "B", 150)))
                    .isEqualTo("A");
        }

        @Test
        @DisplayName("nothing grew gives null (Javadoc example)")
        void nothingGrew() {
            assertThat(tasks.biggestGrower(histogram("A", 100), histogram("A", 100))).isNull();
        }

        @Test
        @DisplayName("a brand-new class can be the biggest grower")
        void newClass() {
            assertThat(tasks.biggestGrower(histogram("A", 100), histogram("A", 110, "Leaky", 5_000)))
                    .isEqualTo("Leaky");
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement bytesPerHour()")
    @DisplayName("bytesPerHour")
    class BytesPerHour {

        @Test
        @DisplayName("extrapolates to one hour (Javadoc example)")
        void javadocExample() {
            assertThat(tasks.bytesPerHour(1_000_000, 4_000_000, 1_800_000)).isEqualTo(6_000_000.0);
        }

        @Test
        @DisplayName("no change gives zero (Javadoc example)")
        void noChange() {
            assertThat(tasks.bytesPerHour(5_000, 5_000, 3_600_000)).isEqualTo(0.0);
        }

        @Test
        @DisplayName("shrinking gives a negative rate (Javadoc example)")
        void shrinking() {
            assertThat(tasks.bytesPerHour(1_000, 500, 3_600_000)).isEqualTo(-500.0);
        }

        @Test
        @DisplayName("zero or negative elapsed time is rejected (Javadoc example)")
        void invalidElapsed() {
            assertThatThrownBy(() -> tasks.bytesPerHour(1_000, 2_000, 0)).isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> tasks.bytesPerHour(1_000, 2_000, -5)).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement hoursUntilFull()")
    @DisplayName("hoursUntilFull")
    class HoursUntilFull {

        @Test
        @DisplayName("divides the free space by the rate (Javadoc example)")
        void javadocExample() {
            assertThat(tasks.hoursUntilFull(400_000_000L, 1_000_000_000L, 100_000_000.0)).isEqualTo(6.0);
        }

        @Test
        @DisplayName("an already full heap gives zero (Javadoc example)")
        void alreadyFull() {
            assertThat(tasks.hoursUntilFull(1_000_000_000L, 1_000_000_000L, 5.0)).isEqualTo(0.0);
        }

        @Test
        @DisplayName("a zero rate never fills the heap (Javadoc example)")
        void zeroRate() {
            assertThat(tasks.hoursUntilFull(400_000_000L, 1_000_000_000L, 0.0)).isEqualTo(Double.POSITIVE_INFINITY);
        }

        @Test
        @DisplayName("a negative rate never fills the heap (Javadoc example)")
        void negativeRate() {
            assertThat(tasks.hoursUntilFull(400_000_000L, 1_000_000_000L, -3.0)).isEqualTo(Double.POSITIVE_INFINITY);
        }
    }
}
