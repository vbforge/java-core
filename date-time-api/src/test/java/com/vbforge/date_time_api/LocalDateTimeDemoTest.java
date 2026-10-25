package com.vbforge.date_time_api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * LocalDateTimeDemoTest — verifies every concept shown in {@link LocalDateTimeDemo}.
 *
 * <p>These tests are always GREEN. Read them as executable specification of
 * {@code LocalDate}, {@code LocalTime} and {@code LocalDateTime}.
 */
@DisplayName("LocalDateTimeDemo — API contracts")
class LocalDateTimeDemoTest {

    // ── 1. LocalDate ──────────────────────────────────────────────────────────

    @Nested
    @DisplayName("1. LocalDate")
    class LocalDateTest {

        @Test
        @DisplayName("months are 1-based; of(int) and of(Month) agree")
        void oneBasedMonths() {
            assertThat(LocalDate.of(2024, 3, 15)).isEqualTo(LocalDate.of(2024, Month.MARCH, 15));
            assertThat(LocalDate.of(2024, 3, 15).getMonthValue()).isEqualTo(3);
        }

        @Test
        @DisplayName("parse reads ISO yyyy-MM-dd")
        void parseIso() {
            assertThat(LocalDate.parse("2024-03-15")).isEqualTo(LocalDate.of(2024, 3, 15));
        }

        @Test
        @DisplayName("epoch day 0 is 1970-01-01 and ofYearDay(75) in 2024 is March 15")
        void epochAndYearDay() {
            assertThat(LocalDate.ofEpochDay(0)).isEqualTo(LocalDate.of(1970, 1, 1));
            assertThat(LocalDate.ofYearDay(2024, 75)).isEqualTo(LocalDate.of(2024, 3, 15));
        }

        @Test
        @DisplayName("getters: day of week, day of year, leap year, lengths")
        void getters() {
            LocalDate date = LocalDate.of(2024, 3, 15);
            assertThat(date.getDayOfWeek()).isEqualTo(DayOfWeek.FRIDAY);
            assertThat(date.getDayOfYear()).isEqualTo(75);
            assertThat(date.isLeapYear()).isTrue();
            assertThat(date.lengthOfMonth()).isEqualTo(31);
            assertThat(date.lengthOfYear()).isEqualTo(366);
        }

        @Test
        @DisplayName("plusDays / minusWeeks / plusYears return new dates")
        void arithmetic() {
            LocalDate date = LocalDate.of(2024, 3, 15);
            assertThat(date.plusDays(20)).isEqualTo(LocalDate.of(2024, 4, 4));
            assertThat(date.minusWeeks(2)).isEqualTo(LocalDate.of(2024, 3, 1));
            assertThat(date.plusYears(1)).isEqualTo(LocalDate.of(2025, 3, 15));
        }

        @Test
        @DisplayName("plusMonths clamps to the last day of the target month")
        void monthClamping() {
            assertThat(LocalDate.of(2024, 1, 31).plusMonths(1)).isEqualTo(LocalDate.of(2024, 2, 29));
            assertThat(LocalDate.of(2023, 1, 31).plusMonths(1)).isEqualTo(LocalDate.of(2023, 2, 28));
        }

        @Test
        @DisplayName("order of chained month operations matters")
        void chainedMonthOrder() {
            LocalDate jan31 = LocalDate.of(2024, 1, 31);
            assertThat(jan31.plusMonths(2)).isEqualTo(LocalDate.of(2024, 3, 31));
            assertThat(jan31.plusMonths(1).plusMonths(1)).isEqualTo(LocalDate.of(2024, 3, 29));
        }
    }

    // ── 2. LocalTime ──────────────────────────────────────────────────────────

    @Nested
    @DisplayName("2. LocalTime")
    class LocalTimeTest {

        @Test
        @DisplayName("toString omits zero seconds")
        void toStringOmitsZeroSeconds() {
            assertThat(LocalTime.of(9, 30).toString()).isEqualTo("09:30");
            assertThat(LocalTime.of(9, 30, 15).toString()).isEqualTo("09:30:15");
        }

        @Test
        @DisplayName("constants MIDNIGHT, NOON and MAX")
        void constants() {
            assertThat(LocalTime.MIDNIGHT).isEqualTo(LocalTime.of(0, 0));
            assertThat(LocalTime.NOON).isEqualTo(LocalTime.of(12, 0));
            assertThat(LocalTime.MAX).isEqualTo(LocalTime.of(23, 59, 59, 999_999_999));
        }

        @Test
        @DisplayName("arithmetic wraps around midnight silently")
        void wrapAround() {
            assertThat(LocalTime.of(9, 30).plusHours(20)).isEqualTo(LocalTime.of(5, 30));
        }

        @Test
        @DisplayName("truncatedTo drops everything smaller than the unit")
        void truncation() {
            LocalTime precise = LocalTime.of(9, 30, 15, 250_000_000);
            assertThat(precise.truncatedTo(ChronoUnit.MINUTES)).isEqualTo(LocalTime.of(9, 30));
            assertThat(precise.truncatedTo(ChronoUnit.HOURS)).isEqualTo(LocalTime.of(9, 0));
        }

        @Test
        @DisplayName("toSecondOfDay counts seconds since midnight")
        void secondOfDay() {
            assertThat(LocalTime.of(9, 30).toSecondOfDay()).isEqualTo(34_200);
        }

        @Test
        @DisplayName("hour 24 is rejected")
        void invalidHour() {
            assertThatThrownBy(() -> LocalTime.of(24, 0)).isInstanceOf(DateTimeException.class);
        }
    }

    // ── 3. LocalDateTime ──────────────────────────────────────────────────────

    @Nested
    @DisplayName("3. LocalDateTime")
    class LocalDateTimeTest {

        private final LocalDateTime dt = LocalDateTime.of(2024, 3, 15, 10, 15, 30);

        @Test
        @DisplayName("composes from a date and a time in several ways")
        void composition() {
            LocalDate date = LocalDate.of(2024, 3, 15);
            LocalTime time = LocalTime.of(10, 15);
            LocalDateTime expected = LocalDateTime.of(2024, 3, 15, 10, 15);
            assertThat(LocalDateTime.of(date, time)).isEqualTo(expected);
            assertThat(date.atTime(10, 15)).isEqualTo(expected);
            assertThat(time.atDate(date)).isEqualTo(expected);
            assertThat(date.atStartOfDay()).isEqualTo(LocalDateTime.of(2024, 3, 15, 0, 0));
        }

        @Test
        @DisplayName("parse reads ISO yyyy-MM-ddTHH:mm:ss")
        void parseIso() {
            assertThat(LocalDateTime.parse("2024-03-15T10:15:30")).isEqualTo(dt);
        }

        @Test
        @DisplayName("splits back into date and time parts")
        void splitting() {
            assertThat(dt.toLocalDate()).isEqualTo(LocalDate.of(2024, 3, 15));
            assertThat(dt.toLocalTime()).isEqualTo(LocalTime.of(10, 15, 30));
        }

        @Test
        @DisplayName("time arithmetic carries over into the date")
        void carryOver() {
            assertThat(dt.plusHours(14)).isEqualTo(LocalDateTime.of(2024, 3, 16, 0, 15, 30));
            assertThat(dt.minusMinutes(20)).isEqualTo(LocalDateTime.of(2024, 3, 15, 9, 55, 30));
        }

        @Test
        @DisplayName("withHour replaces one field; truncatedTo(DAYS) gives midnight")
        void replaceAndTruncate() {
            assertThat(dt.withHour(23)).isEqualTo(LocalDateTime.of(2024, 3, 15, 23, 15, 30));
            assertThat(dt.truncatedTo(ChronoUnit.DAYS)).isEqualTo(LocalDateTime.of(2024, 3, 15, 0, 0));
        }
    }

    // ── 4. TemporalAdjusters ──────────────────────────────────────────────────

    @Nested
    @DisplayName("4. TemporalAdjusters")
    class AdjustersTest {

        private final LocalDate friday = LocalDate.of(2024, 3, 15);

        @Test
        @DisplayName("first/last day of month, next month and year")
        void monthAndYearAdjusters() {
            assertThat(friday.with(TemporalAdjusters.firstDayOfMonth())).isEqualTo(LocalDate.of(2024, 3, 1));
            assertThat(friday.with(TemporalAdjusters.lastDayOfMonth())).isEqualTo(LocalDate.of(2024, 3, 31));
            assertThat(friday.with(TemporalAdjusters.firstDayOfNextMonth())).isEqualTo(LocalDate.of(2024, 4, 1));
            assertThat(friday.with(TemporalAdjusters.lastDayOfYear())).isEqualTo(LocalDate.of(2024, 12, 31));
        }

        @Test
        @DisplayName("next / previous move to another day of week")
        void nextAndPrevious() {
            assertThat(friday.with(TemporalAdjusters.next(DayOfWeek.MONDAY))).isEqualTo(LocalDate.of(2024, 3, 18));
            assertThat(friday.with(TemporalAdjusters.previous(DayOfWeek.MONDAY))).isEqualTo(LocalDate.of(2024, 3, 11));
        }

        @Test
        @DisplayName("next is strictly after; nextOrSame keeps a matching date")
        void strictVersusOrSame() {
            assertThat(friday.with(TemporalAdjusters.next(DayOfWeek.FRIDAY))).isEqualTo(LocalDate.of(2024, 3, 22));
            assertThat(friday.with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY))).isEqualTo(friday);
        }

        @Test
        @DisplayName("dayOfWeekInMonth and lastInMonth find ordinal weekdays")
        void ordinalWeekdays() {
            assertThat(friday.with(TemporalAdjusters.dayOfWeekInMonth(2, DayOfWeek.TUESDAY)))
                    .isEqualTo(LocalDate.of(2024, 3, 12));
            assertThat(friday.with(TemporalAdjusters.lastInMonth(DayOfWeek.FRIDAY)))
                    .isEqualTo(LocalDate.of(2024, 3, 29));
        }

        @Test
        @DisplayName("LocalDateTime keeps its time part when adjusted")
        void keepsTime() {
            LocalDateTime dt = LocalDateTime.of(2024, 3, 15, 10, 15);
            assertThat(dt.with(TemporalAdjusters.lastDayOfMonth())).isEqualTo(LocalDateTime.of(2024, 3, 31, 10, 15));
        }
    }

    // ── 5. Immutability & validation ──────────────────────────────────────────

    @Nested
    @DisplayName("5. Immutability & validation")
    class ImmutabilityTest {

        @Test
        @DisplayName("discarding the result of plusDays leaves the original unchanged")
        void discardedResult() {
            LocalDate original = LocalDate.of(2024, 3, 15);
            original.plusDays(1);
            assertThat(original).isEqualTo(LocalDate.of(2024, 3, 15));
        }

        @Test
        @DisplayName("plusDays returns a different object")
        void newObject() {
            LocalDate original = LocalDate.of(2024, 3, 15);
            assertThat(original.plusDays(1)).isNotSameAs(original);
        }

        @Test
        @DisplayName("equality is by value")
        void valueEquality() {
            assertThat(LocalDate.of(2024, 3, 15)).isEqualTo(LocalDate.parse("2024-03-15"));
            assertThat(LocalDate.of(2024, 3, 15)).hasSameHashCodeAs(LocalDate.parse("2024-03-15"));
        }

        @Test
        @DisplayName("Feb 29 in a non-leap year and month 13 fail fast")
        void invalidDates() {
            assertThatThrownBy(() -> LocalDate.of(2023, 2, 29)).isInstanceOf(DateTimeException.class);
            assertThatThrownBy(() -> LocalDate.of(2024, 13, 1)).isInstanceOf(DateTimeException.class);
        }
    }
}
