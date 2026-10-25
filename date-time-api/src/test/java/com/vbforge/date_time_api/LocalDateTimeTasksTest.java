package com.vbforge.date_time_api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * LocalDateTimeTasksTest — tests for every method in {@link LocalDateTimeTasks}.
 *
 * <p><b>Getting started:</b>
 * <ol>
 *   <li>Open {@code LocalDateTimeTasks.java} and implement a method.</li>
 *   <li>Remove the {@code @Disabled} annotation from the corresponding {@code @Nested} class.</li>
 *   <li>Run {@code mvn test -Dtest=LocalDateTimeTasksTest} — those tests should go GREEN ✅</li>
 *   <li>Repeat until all 5 task groups are GREEN.</li>
 * </ol>
 */
@DisplayName("LocalDateTimeTasks — your implementations")
class LocalDateTimeTasksTest {

    private LocalDateTimeTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new LocalDateTimeTasks();
    }

    // ── Task 1: lastDayOfMonth ────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement lastDayOfMonth()")
    @DisplayName("Task 1 — lastDayOfMonth()")
    class LastDayOfMonthTest {

        @Test
        @DisplayName("2024-02-10 → 2024-02-29 (leap year)")
        void leapYearFebruary() {
            assertThat(tasks.lastDayOfMonth(LocalDate.of(2024, 2, 10))).isEqualTo(LocalDate.of(2024, 2, 29));
        }

        @Test
        @DisplayName("2023-02-10 → 2023-02-28")
        void commonYearFebruary() {
            assertThat(tasks.lastDayOfMonth(LocalDate.of(2023, 2, 10))).isEqualTo(LocalDate.of(2023, 2, 28));
        }

        @Test
        @DisplayName("2024-04-01 → 2024-04-30")
        void thirtyDayMonth() {
            assertThat(tasks.lastDayOfMonth(LocalDate.of(2024, 4, 1))).isEqualTo(LocalDate.of(2024, 4, 30));
        }

        @Test
        @DisplayName("already the last day stays unchanged")
        void alreadyLast() {
            assertThat(tasks.lastDayOfMonth(LocalDate.of(2024, 12, 31))).isEqualTo(LocalDate.of(2024, 12, 31));
        }

        @Test
        @DisplayName("the year and month never change")
        void sameMonth() {
            LocalDate result = tasks.lastDayOfMonth(LocalDate.of(2024, 7, 9));
            assertThat(result.getYear()).isEqualTo(2024);
            assertThat(result.getMonth()).isEqualTo(Month.JULY);
        }
    }

    // ── Task 2: nextWorkingDay ────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement nextWorkingDay()")
    @DisplayName("Task 2 — nextWorkingDay()")
    class NextWorkingDayTest {

        @Test
        @DisplayName("Thursday → Friday")
        void thursday() {
            assertThat(tasks.nextWorkingDay(LocalDate.of(2024, 3, 14))).isEqualTo(LocalDate.of(2024, 3, 15));
        }

        @Test
        @DisplayName("Friday → Monday")
        void friday() {
            assertThat(tasks.nextWorkingDay(LocalDate.of(2024, 3, 15))).isEqualTo(LocalDate.of(2024, 3, 18));
        }

        @Test
        @DisplayName("Saturday → Monday")
        void saturday() {
            assertThat(tasks.nextWorkingDay(LocalDate.of(2024, 3, 16))).isEqualTo(LocalDate.of(2024, 3, 18));
        }

        @Test
        @DisplayName("Sunday → Monday")
        void sunday() {
            assertThat(tasks.nextWorkingDay(LocalDate.of(2024, 3, 17))).isEqualTo(LocalDate.of(2024, 3, 18));
        }

        @Test
        @DisplayName("2024-12-31 → 2025-01-01 (crosses the year boundary)")
        void yearBoundary() {
            assertThat(tasks.nextWorkingDay(LocalDate.of(2024, 12, 31))).isEqualTo(LocalDate.of(2025, 1, 1));
        }

        @Test
        @DisplayName("the result is never on a weekend")
        void neverWeekend() {
            LocalDate day = LocalDate.of(2024, 3, 1);
            for (int i = 0; i < 14; i++) {
                DayOfWeek dow = tasks.nextWorkingDay(day.plusDays(i)).getDayOfWeek();
                assertThat(dow).isNotIn(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
            }
        }
    }

    // ── Task 3: quarterOf ─────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement quarterOf()")
    @DisplayName("Task 3 — quarterOf()")
    class QuarterOfTest {

        @ParameterizedTest(name = "{0} → Q{1}")
        @CsvSource({
                "2024-01-01, 1",
                "2024-03-31, 1",
                "2024-04-01, 2",
                "2024-06-30, 2",
                "2024-07-01, 3",
                "2024-09-30, 3",
                "2024-10-01, 4",
                "2024-12-31, 4"
        })
        @DisplayName("quarter boundaries")
        void boundaries(String date, int expectedQuarter) {
            assertThat(tasks.quarterOf(LocalDate.parse(date))).isEqualTo(expectedQuarter);
        }

        @Test
        @DisplayName("the year does not matter")
        void yearIrrelevant() {
            assertThat(tasks.quarterOf(LocalDate.of(1999, 8, 15))).isEqualTo(3);
        }
    }

    // ── Task 4: floorToQuarterHour ────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement floorToQuarterHour()")
    @DisplayName("Task 4 — floorToQuarterHour()")
    class FloorToQuarterHourTest {

        @Test
        @DisplayName("10:47:30.123 → 10:45")
        void basic() {
            LocalDateTime input = LocalDateTime.of(2024, 3, 15, 10, 47, 30, 123_000_000);
            assertThat(tasks.floorToQuarterHour(input)).isEqualTo(LocalDateTime.of(2024, 3, 15, 10, 45));
        }

        @Test
        @DisplayName("10:14:59 → 10:00")
        void justBeforeBoundary() {
            assertThat(tasks.floorToQuarterHour(LocalDateTime.of(2024, 3, 15, 10, 14, 59)))
                    .isEqualTo(LocalDateTime.of(2024, 3, 15, 10, 0));
        }

        @Test
        @DisplayName("already aligned at 10:15:00 stays the same")
        void alreadyAligned() {
            assertThat(tasks.floorToQuarterHour(LocalDateTime.of(2024, 3, 15, 10, 15)))
                    .isEqualTo(LocalDateTime.of(2024, 3, 15, 10, 15));
        }

        @Test
        @DisplayName("23:59:59 → 23:45 on the same date")
        void endOfDay() {
            assertThat(tasks.floorToQuarterHour(LocalDateTime.of(2024, 3, 15, 23, 59, 59)))
                    .isEqualTo(LocalDateTime.of(2024, 3, 15, 23, 45));
        }

        @Test
        @DisplayName("midnight stays midnight")
        void midnight() {
            LocalDateTime midnight = LocalDateTime.of(2024, 3, 15, 0, 0);
            assertThat(tasks.floorToQuarterHour(midnight)).isEqualTo(midnight);
        }

        @ParameterizedTest(name = "minute {0} → {1}")
        @CsvSource({"0, 0", "14, 0", "15, 15", "29, 15", "30, 30", "44, 30", "45, 45", "59, 45"})
        @DisplayName("every minute maps to its quarter-hour slot")
        void allSlots(int minute, int expectedMinute) {
            LocalDateTime result = tasks.floorToQuarterHour(LocalDateTime.of(2024, 3, 15, 8, minute, 17, 999));
            assertThat(result).isEqualTo(LocalDateTime.of(2024, 3, 15, 8, expectedMinute));
        }
    }

    // ── Task 5: nthWeekdayOfMonth ─────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement nthWeekdayOfMonth()")
    @DisplayName("Task 5 — nthWeekdayOfMonth()")
    class NthWeekdayOfMonthTest {

        @Test
        @DisplayName("4th Thursday of November 2024 → 2024-11-28")
        void thanksgiving() {
            assertThat(tasks.nthWeekdayOfMonth(2024, Month.NOVEMBER, DayOfWeek.THURSDAY, 4))
                    .isEqualTo(LocalDate.of(2024, 11, 28));
        }

        @Test
        @DisplayName("1st Monday of March 2024 → 2024-03-04 (month starts on a Friday)")
        void firstMonday() {
            assertThat(tasks.nthWeekdayOfMonth(2024, Month.MARCH, DayOfWeek.MONDAY, 1))
                    .isEqualTo(LocalDate.of(2024, 3, 4));
        }

        @Test
        @DisplayName("3rd Monday of January 2024 → 2024-01-15 (Jan 1 is itself a Monday)")
        void monthStartsOnTheWeekday() {
            assertThat(tasks.nthWeekdayOfMonth(2024, Month.JANUARY, DayOfWeek.MONDAY, 3))
                    .isEqualTo(LocalDate.of(2024, 1, 15));
        }

        @Test
        @DisplayName("1st Monday of January 2024 is the 1st itself")
        void firstDayCounts() {
            assertThat(tasks.nthWeekdayOfMonth(2024, Month.JANUARY, DayOfWeek.MONDAY, 1))
                    .isEqualTo(LocalDate.of(2024, 1, 1));
        }

        @Test
        @DisplayName("result is in the requested month, on the requested weekday")
        void resultProperties() {
            LocalDate result = tasks.nthWeekdayOfMonth(2025, Month.FEBRUARY, DayOfWeek.SUNDAY, 4);
            assertThat(result.getMonth()).isEqualTo(Month.FEBRUARY);
            assertThat(result.getDayOfWeek()).isEqualTo(DayOfWeek.SUNDAY);
            assertThat(result).isEqualTo(LocalDate.of(2025, 2, 23));
        }
    }
}
