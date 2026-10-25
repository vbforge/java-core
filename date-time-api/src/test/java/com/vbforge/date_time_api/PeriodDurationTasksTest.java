package com.vbforge.date_time_api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Period;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * PeriodDurationTasksTest — tests for every method in {@link PeriodDurationTasks}.
 *
 * <p><b>Getting started:</b>
 * <ol>
 *   <li>Open {@code PeriodDurationTasks.java} and implement a method.</li>
 *   <li>Remove the {@code @Disabled} annotation from the corresponding {@code @Nested} class.</li>
 *   <li>Run {@code mvn test -Dtest=PeriodDurationTasksTest} — those tests should go GREEN ✅</li>
 *   <li>Repeat until all 5 task groups are GREEN.</li>
 * </ol>
 */
@DisplayName("PeriodDurationTasks — your implementations")
class PeriodDurationTasksTest {

    private PeriodDurationTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new PeriodDurationTasks();
    }

    // ── Task 1: ageInYears ────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement ageInYears()")
    @DisplayName("Task 1 — ageInYears()")
    class AgeInYearsTest {

        @Test
        @DisplayName("birthday not reached yet this year → 33")
        void beforeBirthday() {
            assertThat(tasks.ageInYears(LocalDate.of(1990, 5, 20), LocalDate.of(2024, 3, 15))).isEqualTo(33);
        }

        @Test
        @DisplayName("the day before the birthday → 33")
        void dayBefore() {
            assertThat(tasks.ageInYears(LocalDate.of(1990, 5, 20), LocalDate.of(2024, 5, 19))).isEqualTo(33);
        }

        @Test
        @DisplayName("on the birthday → 34")
        void onBirthday() {
            assertThat(tasks.ageInYears(LocalDate.of(1990, 5, 20), LocalDate.of(2024, 5, 20))).isEqualTo(34);
        }

        @Test
        @DisplayName("born today → 0")
        void bornToday() {
            assertThat(tasks.ageInYears(LocalDate.of(2024, 3, 15), LocalDate.of(2024, 3, 15))).isZero();
        }

        @Test
        @DisplayName("less than one year old → 0")
        void infant() {
            assertThat(tasks.ageInYears(LocalDate.of(2023, 12, 31), LocalDate.of(2024, 12, 30))).isZero();
        }

        @Test
        @DisplayName("born on 29 Feb: still 22 on 28 Feb 2023, 23 on 1 Mar 2023")
        void leapDayBirth() {
            LocalDate birth = LocalDate.of(2000, 2, 29);
            assertThat(tasks.ageInYears(birth, LocalDate.of(2023, 2, 28))).isEqualTo(22);
            assertThat(tasks.ageInYears(birth, LocalDate.of(2023, 3, 1))).isEqualTo(23);
        }
    }

    // ── Task 2: daysBetween ───────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement daysBetween()")
    @DisplayName("Task 2 — daysBetween()")
    class DaysBetweenTest {

        @Test
        @DisplayName("2024-03-15 → 2024-03-20 = 5")
        void basic() {
            assertThat(tasks.daysBetween(LocalDate.of(2024, 3, 15), LocalDate.of(2024, 3, 20))).isEqualTo(5L);
        }

        @Test
        @DisplayName("2024-01-01 → 2024-03-15 = 74 (not the days component 14)")
        void totalNotComponent() {
            assertThat(tasks.daysBetween(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 3, 15))).isEqualTo(74L);
        }

        @Test
        @DisplayName("end before start → negative")
        void negative() {
            assertThat(tasks.daysBetween(LocalDate.of(2024, 3, 20), LocalDate.of(2024, 3, 15))).isEqualTo(-5L);
        }

        @Test
        @DisplayName("same day → 0")
        void sameDay() {
            assertThat(tasks.daysBetween(LocalDate.of(2024, 3, 15), LocalDate.of(2024, 3, 15))).isZero();
        }

        @Test
        @DisplayName("across a year boundary")
        void yearBoundary() {
            assertThat(tasks.daysBetween(LocalDate.of(2023, 12, 31), LocalDate.of(2024, 1, 1))).isEqualTo(1L);
        }

        @Test
        @DisplayName("more than a year apart: 2023-01-01 → 2025-01-01 = 731")
        void multiYear() {
            assertThat(tasks.daysBetween(LocalDate.of(2023, 1, 1), LocalDate.of(2025, 1, 1))).isEqualTo(731L);
        }
    }

    // ── Task 3: formatDuration ────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement formatDuration()")
    @DisplayName("Task 3 — formatDuration()")
    class FormatDurationTest {

        @Test
        @DisplayName("zero → 00:00:00")
        void zero() {
            assertThat(tasks.formatDuration(Duration.ZERO)).isEqualTo("00:00:00");
        }

        @Test
        @DisplayName("PT1H2M5S → 01:02:05")
        void typical() {
            assertThat(tasks.formatDuration(Duration.ofSeconds(3725))).isEqualTo("01:02:05");
        }

        @Test
        @DisplayName("hours are not wrapped at 24: PT25H → 25:00:00")
        void moreThanADay() {
            assertThat(tasks.formatDuration(Duration.ofHours(25))).isEqualTo("25:00:00");
        }

        @Test
        @DisplayName("three-digit hours: PT100H5M → 100:05:00")
        void threeDigitHours() {
            assertThat(tasks.formatDuration(Duration.ofHours(100).plusMinutes(5))).isEqualTo("100:05:00");
        }

        @Test
        @DisplayName("fractions are cut off, not rounded: PT59.999S → 00:00:59")
        void fractionCutOff() {
            assertThat(tasks.formatDuration(Duration.ofMillis(59_999))).isEqualTo("00:00:59");
        }

        @ParameterizedTest(name = "{0} seconds → {1}")
        @CsvSource({"1, 00:00:01", "59, 00:00:59", "60, 00:01:00", "3599, 00:59:59", "3600, 01:00:00", "86399, 23:59:59", "86400, 24:00:00"})
        @DisplayName("unit boundaries")
        void boundaries(long seconds, String expected) {
            assertThat(tasks.formatDuration(Duration.ofSeconds(seconds))).isEqualTo(expected);
        }
    }

    // ── Task 4: describePeriod ────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement describePeriod()")
    @DisplayName("Task 4 — describePeriod()")
    class DescribePeriodTest {

        @Test
        @DisplayName("P1Y2M3D → \"1 year, 2 months, 3 days\"")
        void allComponents() {
            assertThat(tasks.describePeriod(Period.of(1, 2, 3))).isEqualTo("1 year, 2 months, 3 days");
        }

        @Test
        @DisplayName("zero components are left out: P2Y5D → \"2 years, 5 days\"")
        void skipsZeroMonths() {
            assertThat(tasks.describePeriod(Period.of(2, 0, 5))).isEqualTo("2 years, 5 days");
        }

        @Test
        @DisplayName("singular for exactly 1: P1M → \"1 month\"")
        void singularMonth() {
            assertThat(tasks.describePeriod(Period.ofMonths(1))).isEqualTo("1 month");
        }

        @Test
        @DisplayName("singular for exactly 1: P1D → \"1 day\"")
        void singularDay() {
            assertThat(tasks.describePeriod(Period.ofDays(1))).isEqualTo("1 day");
        }

        @Test
        @DisplayName("not normalised: P14M → \"14 months\"")
        void notNormalised() {
            assertThat(tasks.describePeriod(Period.ofMonths(14))).isEqualTo("14 months");
        }

        @Test
        @DisplayName("zero period → \"0 days\"")
        void zeroPeriod() {
            assertThat(tasks.describePeriod(Period.ZERO)).isEqualTo("0 days");
        }

        @Test
        @DisplayName("weeks are days: P2W → \"14 days\"")
        void weeks() {
            assertThat(tasks.describePeriod(Period.ofWeeks(2))).isEqualTo("14 days");
        }
    }

    // ── Task 5: daysUntilNextBirthday ─────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement daysUntilNextBirthday()")
    @DisplayName("Task 5 — daysUntilNextBirthday()")
    class DaysUntilNextBirthdayTest {

        @Test
        @DisplayName("1990-05-20 on 2024-03-15 → 66")
        void thisYear() {
            assertThat(tasks.daysUntilNextBirthday(LocalDate.of(1990, 5, 20), LocalDate.of(2024, 3, 15)))
                    .isEqualTo(66L);
        }

        @Test
        @DisplayName("on the birthday → 0")
        void today() {
            assertThat(tasks.daysUntilNextBirthday(LocalDate.of(1990, 5, 20), LocalDate.of(2024, 5, 20))).isZero();
        }

        @Test
        @DisplayName("the day after the birthday → next year's date (364)")
        void dayAfter() {
            assertThat(tasks.daysUntilNextBirthday(LocalDate.of(1990, 5, 20), LocalDate.of(2024, 5, 21)))
                    .isEqualTo(364L);
        }

        @Test
        @DisplayName("across the year boundary: Dec 31 → Jan 1 = 1")
        void yearBoundary() {
            assertThat(tasks.daysUntilNextBirthday(LocalDate.of(1990, 1, 1), LocalDate.of(2024, 12, 31)))
                    .isEqualTo(1L);
        }

        @Test
        @DisplayName("the answer is never negative and never more than 365")
        void bounds() {
            LocalDate birth = LocalDate.of(1985, 8, 14);
            LocalDate day = LocalDate.of(2024, 1, 1);
            for (int i = 0; i < 366; i++) {
                long result = tasks.daysUntilNextBirthday(birth, day.plusDays(i));
                assertThat(result).isBetween(0L, 365L);
            }
        }
    }
}
