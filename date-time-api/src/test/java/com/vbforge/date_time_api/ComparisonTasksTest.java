package com.vbforge.date_time_api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ComparisonTasksTest — tests for every method in {@link ComparisonTasks}.
 *
 * <p><b>Getting started:</b>
 * <ol>
 *   <li>Open {@code ComparisonTasks.java} and implement a method.</li>
 *   <li>Remove the {@code @Disabled} annotation from the corresponding {@code @Nested} class.</li>
 *   <li>Run {@code mvn test -Dtest=ComparisonTasksTest} — those tests should go GREEN ✅</li>
 *   <li>Repeat until all 6 task groups are GREEN.</li>
 * </ol>
 */
@DisplayName("ComparisonTasks — your implementations")
class ComparisonTasksTest {

    private static final ZoneId PARIS = ZoneId.of("Europe/Paris");
    private static final ZoneId NEW_YORK = ZoneId.of("America/New_York");
    private static final ZoneId TOKYO = ZoneId.of("Asia/Tokyo");

    private ComparisonTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new ComparisonTasks();
    }

    // ── Task 1: isWithinRange ─────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement isWithinRange()")
    @DisplayName("Task 1 — isWithinRange()")
    class IsWithinRangeTest {

        private final LocalDate start = LocalDate.of(2024, 3, 1);
        private final LocalDate end = LocalDate.of(2024, 3, 31);

        @Test
        @DisplayName("a date in the middle → true")
        void middle() {
            assertThat(tasks.isWithinRange(LocalDate.of(2024, 3, 15), start, end)).isTrue();
        }

        @Test
        @DisplayName("the start boundary is included")
        void startBoundary() {
            assertThat(tasks.isWithinRange(start, start, end)).isTrue();
        }

        @Test
        @DisplayName("the end boundary is included")
        void endBoundary() {
            assertThat(tasks.isWithinRange(end, start, end)).isTrue();
        }

        @Test
        @DisplayName("one day before the start → false")
        void beforeStart() {
            assertThat(tasks.isWithinRange(LocalDate.of(2024, 2, 29), start, end)).isFalse();
        }

        @Test
        @DisplayName("one day after the end → false")
        void afterEnd() {
            assertThat(tasks.isWithinRange(LocalDate.of(2024, 4, 1), start, end)).isFalse();
        }

        @Test
        @DisplayName("a one-day range contains exactly that day")
        void singleDayRange() {
            LocalDate day = LocalDate.of(2024, 3, 15);
            assertThat(tasks.isWithinRange(day, day, day)).isTrue();
            assertThat(tasks.isWithinRange(day.plusDays(1), day, day)).isFalse();
            assertThat(tasks.isWithinRange(day.minusDays(1), day, day)).isFalse();
        }
    }

    // ── Task 2: dateRangesOverlap ─────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement dateRangesOverlap()")
    @DisplayName("Task 2 — dateRangesOverlap()")
    class DateRangesOverlapTest {

        private LocalDate march(int day) {
            return LocalDate.of(2024, 3, day);
        }

        @Test
        @DisplayName("sharing the boundary day Mar 10 → true")
        void sharedBoundaryDay() {
            assertThat(tasks.dateRangesOverlap(march(1), march(10), march(10), march(20))).isTrue();
        }

        @Test
        @DisplayName("a one-day gap → false")
        void oneDayGap() {
            assertThat(tasks.dateRangesOverlap(march(1), march(9), march(10), march(20))).isFalse();
        }

        @Test
        @DisplayName("one range contains the other → true")
        void contained() {
            assertThat(tasks.dateRangesOverlap(march(1), march(31), march(10), march(12))).isTrue();
        }

        @Test
        @DisplayName("the same single day → true")
        void sameSingleDay() {
            assertThat(tasks.dateRangesOverlap(march(5), march(5), march(5), march(5))).isTrue();
        }

        @Test
        @DisplayName("argument order does not matter")
        void symmetric() {
            assertThat(tasks.dateRangesOverlap(march(10), march(20), march(1), march(9))).isFalse();
            assertThat(tasks.dateRangesOverlap(march(10), march(20), march(1), march(10))).isTrue();
        }

        @Test
        @DisplayName("clearly separate ranges → false")
        void farApart() {
            assertThat(tasks.dateRangesOverlap(march(1), march(5), march(20), march(25))).isFalse();
        }
    }

    // ── Task 3: sameMoment ────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement sameMoment()")
    @DisplayName("Task 3 — sameMoment()")
    class SameMomentTest {

        private final ZonedDateTime parisNoon = ZonedDateTime.of(2024, 1, 15, 12, 0, 0, 0, PARIS);

        @Test
        @DisplayName("Paris 12:00 and New York 06:00 are the same moment")
        void sameInstantDifferentZones() {
            assertThat(tasks.sameMoment(parisNoon, ZonedDateTime.of(2024, 1, 15, 6, 0, 0, 0, NEW_YORK))).isTrue();
        }

        @Test
        @DisplayName("same wall clock in different zones is NOT the same moment")
        void sameWallClockDifferentMoment() {
            assertThat(tasks.sameMoment(parisNoon, ZonedDateTime.of(2024, 1, 15, 12, 0, 0, 0, NEW_YORK))).isFalse();
        }

        @Test
        @DisplayName("identical values → true")
        void identical() {
            assertThat(tasks.sameMoment(parisNoon, ZonedDateTime.of(2024, 1, 15, 12, 0, 0, 0, PARIS))).isTrue();
        }

        @Test
        @DisplayName("Paris 12:00 and Tokyo 20:00 are the same moment")
        void tokyo() {
            assertThat(tasks.sameMoment(parisNoon, ZonedDateTime.of(2024, 1, 15, 20, 0, 0, 0, TOKYO))).isTrue();
        }

        @Test
        @DisplayName("Paris 12:00 and Tokyo 19:00 are different moments")
        void differentMoment() {
            assertThat(tasks.sameMoment(parisNoon, ZonedDateTime.of(2024, 1, 15, 19, 0, 0, 0, TOKYO))).isFalse();
        }

        @Test
        @DisplayName("one nanosecond apart → false")
        void nanoDifference() {
            assertThat(tasks.sameMoment(parisNoon, parisNoon.plusNanos(1))).isFalse();
        }

        @Test
        @DisplayName("different offsets of the same region at an overlap are different moments")
        void dstOverlap() {
            ZonedDateTime first = ZonedDateTime.of(LocalDateTime.of(2024, 11, 3, 1, 30), NEW_YORK);
            assertThat(tasks.sameMoment(first, first.withLaterOffsetAtOverlap())).isFalse();
        }
    }

    // ── Task 4: earliest ──────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement earliest()")
    @DisplayName("Task 4 — earliest()")
    class EarliestTest {

        private final ZonedDateTime paris = ZonedDateTime.of(2024, 1, 15, 12, 0, 0, 0, PARIS);         // 11:00Z
        private final ZonedDateTime tokyo = ZonedDateTime.of(2024, 1, 15, 19, 30, 0, 0, TOKYO);        // 10:30Z
        private final ZonedDateTime newYork = ZonedDateTime.of(2024, 1, 15, 6, 30, 0, 0, NEW_YORK);    // 11:30Z

        @Test
        @DisplayName("picks the earliest moment, not the smallest wall clock")
        void mixedZones() {
            assertThat(tasks.earliest(Arrays.asList(paris, tokyo, newYork))).isEqualTo(tokyo);
        }

        @Test
        @DisplayName("single element → that element")
        void single() {
            assertThat(tasks.earliest(Collections.singletonList(paris))).isEqualTo(paris);
        }

        @Test
        @DisplayName("order of the list does not matter")
        void orderIndependent() {
            assertThat(tasks.earliest(Arrays.asList(tokyo, newYork, paris))).isEqualTo(tokyo);
            assertThat(tasks.earliest(Arrays.asList(newYork, paris, tokyo))).isEqualTo(tokyo);
        }

        @Test
        @DisplayName("returns the original element, with its own zone")
        void keepsOriginalZone() {
            assertThat(tasks.earliest(Arrays.asList(paris, tokyo, newYork)).getZone()).isEqualTo(TOKYO);
        }

        @Test
        @DisplayName("the input list is not modified")
        void inputUntouched() {
            List<ZonedDateTime> input = new ArrayList<>(Arrays.asList(paris, tokyo, newYork));
            tasks.earliest(input);
            assertThat(input).containsExactly(paris, tokyo, newYork);
        }
    }

    // ── Task 5: sortNewestFirst ───────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement sortNewestFirst()")
    @DisplayName("Task 5 — sortNewestFirst()")
    class SortNewestFirstTest {

        @Test
        @DisplayName("sorts newest first")
        void basic() {
            List<LocalDate> result = tasks.sortNewestFirst(Arrays.asList(
                    LocalDate.of(2024, 1, 1), LocalDate.of(2024, 3, 15), LocalDate.of(2023, 12, 31)));
            assertThat(result).containsExactly(
                    LocalDate.of(2024, 3, 15), LocalDate.of(2024, 1, 1), LocalDate.of(2023, 12, 31));
        }

        @Test
        @DisplayName("equal dates are kept")
        void duplicates() {
            LocalDate d = LocalDate.of(2024, 5, 5);
            assertThat(tasks.sortNewestFirst(Arrays.asList(d, d))).containsExactly(d, d);
        }

        @Test
        @DisplayName("single element")
        void single() {
            LocalDate d = LocalDate.of(2024, 5, 5);
            assertThat(tasks.sortNewestFirst(Collections.singletonList(d))).containsExactly(d);
        }

        @Test
        @DisplayName("empty list → empty list")
        void empty() {
            assertThat(tasks.sortNewestFirst(new ArrayList<LocalDate>())).isEmpty();
        }

        @Test
        @DisplayName("the input list keeps its original order")
        void inputUntouched() {
            List<LocalDate> input = new ArrayList<>(Arrays.asList(
                    LocalDate.of(2024, 1, 1), LocalDate.of(2024, 3, 15)));
            List<LocalDate> result = tasks.sortNewestFirst(input);
            assertThat(input).containsExactly(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 3, 15));
            assertThat(result).isNotSameAs(input);
        }

        @Test
        @DisplayName("works on an immutable input list")
        void immutableInput() {
            List<LocalDate> input = Collections.unmodifiableList(Arrays.asList(
                    LocalDate.of(2024, 1, 1), LocalDate.of(2024, 3, 15)));
            assertThat(tasks.sortNewestFirst(input)).containsExactly(
                    LocalDate.of(2024, 3, 15), LocalDate.of(2024, 1, 1));
        }
    }

    // ── Task 6: isBusinessHours ───────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement isBusinessHours()")
    @DisplayName("Task 6 — isBusinessHours()")
    class IsBusinessHoursTest {

        @ParameterizedTest(name = "{0} → {1}")
        @CsvSource({
                "2024-03-18T09:00:00, true",
                "2024-03-18T12:30:00, true",
                "2024-03-18T16:59:59, true",
                "2024-03-18T17:00:00, false",
                "2024-03-18T08:59:59, false",
                "2024-03-18T00:00:00, false",
                "2024-03-22T12:00:00, true"
        })
        @DisplayName("Monday and Friday: opening and closing boundaries")
        void weekdayHours(String dateTime, boolean expected) {
            assertThat(tasks.isBusinessHours(LocalDateTime.parse(dateTime))).isEqualTo(expected);
        }

        @Test
        @DisplayName("Saturday noon → false")
        void saturday() {
            assertThat(tasks.isBusinessHours(LocalDateTime.of(2024, 3, 16, 12, 0))).isFalse();
        }

        @Test
        @DisplayName("Sunday noon → false")
        void sunday() {
            assertThat(tasks.isBusinessHours(LocalDateTime.of(2024, 3, 17, 12, 0))).isFalse();
        }

        @Test
        @DisplayName("every weekday of a week is open at 10:00, weekend days are not")
        void wholeWeek() {
            LocalDate monday = LocalDate.of(2024, 3, 18);
            for (int i = 0; i < 5; i++) {
                assertThat(tasks.isBusinessHours(monday.plusDays(i).atTime(10, 0))).isTrue();
            }
            assertThat(tasks.isBusinessHours(monday.plusDays(5).atTime(10, 0))).isFalse();
            assertThat(tasks.isBusinessHours(monday.plusDays(6).atTime(10, 0))).isFalse();
        }
    }
}
