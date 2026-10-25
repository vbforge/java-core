package com.vbforge.date_time_api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.DateTimeException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * PeriodDurationDemoTest — verifies every concept shown in {@link PeriodDurationDemo}.
 *
 * <p>These tests are always GREEN. Read them as executable specification of
 * {@code Period}, {@code Duration} and {@code ChronoUnit.between}.
 */
@DisplayName("PeriodDurationDemo — API contracts")
class PeriodDurationDemoTest {

    // ── 1. Period ─────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("1. Period")
    class PeriodTest {

        @Test
        @DisplayName("of(1,2,3) prints as P1Y2M3D and keeps three separate components")
        void components() {
            Period p = Period.of(1, 2, 3);
            assertThat(p.toString()).isEqualTo("P1Y2M3D");
            assertThat(p.getYears()).isEqualTo(1);
            assertThat(p.getMonths()).isEqualTo(2);
            assertThat(p.getDays()).isEqualTo(3);
        }

        @Test
        @DisplayName("toTotalMonths = years*12 + months, days ignored")
        void totalMonths() {
            assertThat(Period.of(1, 2, 3).toTotalMonths()).isEqualTo(14);
        }

        @Test
        @DisplayName("weeks become days; months are not normalised automatically")
        void factories() {
            assertThat(Period.ofWeeks(2)).isEqualTo(Period.ofDays(14));
            assertThat(Period.ofMonths(14).toString()).isEqualTo("P14M");
            assertThat(Period.ofMonths(14).normalized()).isEqualTo(Period.of(1, 2, 0));
        }

        @Test
        @DisplayName("parse is the inverse of toString; ZERO is zero")
        void parseAndZero() {
            assertThat(Period.parse("P1Y2M3D")).isEqualTo(Period.of(1, 2, 3));
            assertThat(Period.ZERO.isZero()).isTrue();
        }

        @Test
        @DisplayName("negated and multipliedBy work component-wise")
        void arithmetic() {
            Period p = Period.of(1, 2, 3);
            assertThat(p.negated()).isEqualTo(Period.of(-1, -2, -3));
            assertThat(p.multipliedBy(2)).isEqualTo(Period.of(2, 4, 6));
        }

        @Test
        @DisplayName("adding P1M to Jan 31 clamps; P30D does not")
        void addingToDate() {
            LocalDate jan31 = LocalDate.of(2024, 1, 31);
            assertThat(jan31.plus(Period.ofMonths(1))).isEqualTo(LocalDate.of(2024, 2, 29));
            assertThat(jan31.plus(Period.ofDays(30))).isEqualTo(LocalDate.of(2024, 3, 1));
        }

        @Test
        @DisplayName("P1M and P30D are not equal")
        void monthIsNotThirtyDays() {
            assertThat(Period.ofMonths(1)).isNotEqualTo(Period.ofDays(30));
        }

        @Test
        @DisplayName("Period.between splits into years, months and days")
        void between() {
            assertThat(Period.between(LocalDate.of(1990, 5, 20), LocalDate.of(2024, 3, 15)))
                    .isEqualTo(Period.of(33, 9, 24));
            assertThat(Period.between(LocalDate.of(2024, 1, 31), LocalDate.of(2024, 3, 1)))
                    .isEqualTo(Period.of(0, 1, 1));
        }

        @Test
        @DisplayName("getDays is only the days component, not the total")
        void getDaysIsComponent() {
            LocalDate from = LocalDate.of(2024, 1, 1);
            LocalDate to = LocalDate.of(2024, 3, 15);
            assertThat(Period.between(from, to).getDays()).isEqualTo(14);
            assertThat(ChronoUnit.DAYS.between(from, to)).isEqualTo(74);
        }
    }

    // ── 2. Duration ───────────────────────────────────────────────────────────

    @Nested
    @DisplayName("2. Duration")
    class DurationTest {

        @Test
        @DisplayName("factories and toString")
        void factories() {
            assertThat(Duration.ofMinutes(90).toString()).isEqualTo("PT1H30M");
            assertThat(Duration.ofHours(25).toString()).isEqualTo("PT25H");
            assertThat(Duration.ofMillis(1500).toString()).isEqualTo("PT1.5S");
            assertThat(Duration.parse("PT1H30M")).isEqualTo(Duration.ofMinutes(90));
        }

        @Test
        @DisplayName("toMinutes and toHours truncate")
        void truncatingGetters() {
            Duration d = Duration.ofSeconds(3725);
            assertThat(d.getSeconds()).isEqualTo(3725L);
            assertThat(d.toMinutes()).isEqualTo(62L);
            assertThat(d.toHours()).isEqualTo(1L);
            assertThat(d.toMillis()).isEqualTo(3_725_000L);
            assertThat(d.getSeconds() % 60).isEqualTo(5L);
        }

        @Test
        @DisplayName("between instants; reversed order is negative; abs fixes the sign")
        void betweenInstants() {
            Instant start = Instant.parse("2024-03-15T10:00:00Z");
            Instant end = Instant.parse("2024-03-15T12:30:00Z");
            assertThat(Duration.between(start, end)).isEqualTo(Duration.ofMinutes(150));
            assertThat(Duration.between(end, start)).isEqualTo(Duration.ofMinutes(-150));
            assertThat(Duration.between(end, start).abs()).isEqualTo(Duration.ofMinutes(150));
        }

        @Test
        @DisplayName("between local date-times can cross midnight")
        void betweenLocalDateTimes() {
            LocalDateTime a = LocalDateTime.of(2024, 3, 15, 22, 0);
            LocalDateTime b = LocalDateTime.of(2024, 3, 16, 6, 0);
            assertThat(Duration.between(a, b)).isEqualTo(Duration.ofHours(8));
        }

        @Test
        @DisplayName("plus, multipliedBy, dividedBy and compareTo")
        void arithmetic() {
            Duration d = Duration.ofMinutes(90);
            assertThat(d.plus(Duration.ofMinutes(45))).isEqualTo(Duration.ofMinutes(135));
            assertThat(d.multipliedBy(3)).isEqualTo(Duration.ofMinutes(270));
            assertThat(d.dividedBy(2)).isEqualTo(Duration.ofMinutes(45));
            assertThat(d.compareTo(Duration.ofHours(2))).isNegative();
        }

        @Test
        @DisplayName("a day is exactly 24 hours")
        void dayIs24Hours() {
            assertThat(Duration.ofDays(1)).isEqualTo(Duration.ofHours(24));
        }
    }

    // ── 3. Period vs Duration ─────────────────────────────────────────────────

    @Nested
    @DisplayName("3. Period vs Duration")
    class PeriodVsDurationTest {

        private final ZonedDateTime saturdayNoon =
                ZonedDateTime.of(2024, 3, 9, 12, 0, 0, 0, ZoneId.of("America/New_York"));

        @Test
        @DisplayName("Period of one day keeps the wall clock across a DST change")
        void periodKeepsWallClock() {
            assertThat(saturdayNoon.plus(Period.ofDays(1)).toLocalDateTime())
                    .isEqualTo(LocalDateTime.of(2024, 3, 10, 12, 0));
        }

        @Test
        @DisplayName("Duration of one day is 24 elapsed hours — the wall clock moves")
        void durationKeepsElapsedTime() {
            assertThat(saturdayNoon.plus(Duration.ofDays(1)).toLocalDateTime())
                    .isEqualTo(LocalDateTime.of(2024, 3, 10, 13, 0));
        }

        @Test
        @DisplayName("a LocalDate rejects a Duration")
        void dateRejectsDuration() {
            assertThatThrownBy(() -> LocalDate.of(2024, 3, 15).plus(Duration.ofHours(1)))
                    .isInstanceOf(DateTimeException.class);
        }

        @Test
        @DisplayName("Duration.between rejects two LocalDates")
        void durationBetweenDates() {
            assertThatThrownBy(() -> Duration.between(LocalDate.of(2024, 3, 15), LocalDate.of(2024, 3, 16)))
                    .isInstanceOf(DateTimeException.class);
        }
    }

    // ── 4. ChronoUnit.between ─────────────────────────────────────────────────

    @Nested
    @DisplayName("4. ChronoUnit.between")
    class ChronoUnitTest {

        private final LocalDate jan31 = LocalDate.of(2024, 1, 31);
        private final LocalDate mar1 = LocalDate.of(2024, 3, 1);

        @Test
        @DisplayName("returns whole units, truncated")
        void wholeUnits() {
            assertThat(ChronoUnit.DAYS.between(jan31, mar1)).isEqualTo(30L);
            assertThat(ChronoUnit.WEEKS.between(jan31, mar1)).isEqualTo(4L);
            assertThat(ChronoUnit.MONTHS.between(jan31, mar1)).isEqualTo(1L);
        }

        @Test
        @DisplayName("swapping the arguments negates the result")
        void reversed() {
            assertThat(ChronoUnit.DAYS.between(mar1, jan31)).isEqualTo(-30L);
        }

        @Test
        @DisplayName("until(date, unit) and until(date) say the same thing differently")
        void untilSpellings() {
            assertThat(jan31.until(mar1, ChronoUnit.DAYS)).isEqualTo(30L);
            assertThat(jan31.until(mar1)).isEqualTo(Period.of(0, 1, 1));
        }

        @Test
        @DisplayName("YEARS.between gives age in whole years")
        void ageInYears() {
            assertThat(ChronoUnit.YEARS.between(LocalDate.of(1990, 5, 20), LocalDate.of(2024, 3, 15)))
                    .isEqualTo(33L);
        }

        @Test
        @DisplayName("time units truncate on date-times")
        void timeUnits() {
            LocalDateTime start = LocalDateTime.of(2024, 3, 15, 10, 0);
            LocalDateTime end = LocalDateTime.of(2024, 3, 15, 15, 59);
            assertThat(ChronoUnit.HOURS.between(start, end)).isEqualTo(5L);
            assertThat(ChronoUnit.MINUTES.between(start, end)).isEqualTo(359L);
        }

        @Test
        @DisplayName("HOURS on two LocalDates is rejected")
        void wrongUnit() {
            assertThatThrownBy(() -> ChronoUnit.HOURS.between(jan31, mar1))
                    .isInstanceOf(DateTimeException.class);
        }
    }
}
