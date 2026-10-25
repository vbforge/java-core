package com.vbforge.date_time_api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.UnsupportedTemporalTypeException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * InstantDemoTest — verifies every concept shown in {@link InstantDemo}.
 *
 * <p>These tests are always GREEN. Read them as executable specification of
 * {@code Instant}, epoch numbers and {@code Clock}.
 */
@DisplayName("InstantDemo — API contracts")
class InstantDemoTest {

    // ── 1. Instant basics ─────────────────────────────────────────────────────

    @Nested
    @DisplayName("1. Instant basics")
    class InstantBasicsTest {

        private final Instant parsed = Instant.parse("2024-03-15T10:15:30Z");

        @Test
        @DisplayName("EPOCH is 1970-01-01T00:00:00Z")
        void epoch() {
            assertThat(Instant.EPOCH.toString()).isEqualTo("1970-01-01T00:00:00Z");
        }

        @Test
        @DisplayName("ofEpochSecond and ofEpochMilli build the same moment from different units")
        void factories() {
            assertThat(Instant.ofEpochSecond(1_700_000_000L).toString()).isEqualTo("2023-11-14T22:13:20Z");
            assertThat(Instant.ofEpochMilli(1_700_000_000_123L).toString()).isEqualTo("2023-11-14T22:13:20.123Z");
        }

        @Test
        @DisplayName("epoch numbers can be read back in seconds and millis")
        void readBack() {
            assertThat(parsed.getEpochSecond()).isEqualTo(1_710_497_730L);
            assertThat(parsed.toEpochMilli()).isEqualTo(1_710_497_730_000L);
            assertThat(parsed.getNano()).isZero();
        }

        @Test
        @DisplayName("plus/minus return new instants")
        void shifting() {
            assertThat(parsed.plusSeconds(90)).isEqualTo(Instant.parse("2024-03-15T10:17:00Z"));
            assertThat(parsed.plusMillis(500)).isEqualTo(Instant.parse("2024-03-15T10:15:30.500Z"));
            assertThat(parsed.minus(Duration.ofHours(2))).isEqualTo(Instant.parse("2024-03-15T08:15:30Z"));
        }

        @Test
        @DisplayName("isBefore and isAfter order moments")
        void ordering() {
            Instant later = parsed.plusSeconds(1);
            assertThat(parsed.isBefore(later)).isTrue();
            assertThat(parsed.isAfter(later)).isFalse();
        }
    }

    // ── 2. Epoch numbers & precision ──────────────────────────────────────────

    @Nested
    @DisplayName("2. Epoch numbers & precision")
    class EpochAndPrecisionTest {

        @Test
        @DisplayName("negative numbers are before 1970")
        void negativeNumbers() {
            assertThat(Instant.ofEpochSecond(-1).toString()).isEqualTo("1969-12-31T23:59:59Z");
            assertThat(Instant.ofEpochMilli(-1).toString()).isEqualTo("1969-12-31T23:59:59.999Z");
        }

        @Test
        @DisplayName("fractions print in groups of three digits")
        void fractionPrinting() {
            assertThat(Instant.ofEpochSecond(0, 500_000_000L).toString()).isEqualTo("1970-01-01T00:00:00.500Z");
            assertThat(Instant.ofEpochSecond(0, 123_456_789L).toString()).isEqualTo("1970-01-01T00:00:00.123456789Z");
        }

        @Test
        @DisplayName("nanoseconds survive in the Instant but are lost in toEpochMilli")
        void nanosVersusMillis() {
            Instant nanos = Instant.ofEpochSecond(0, 123_456_789L);
            assertThat(nanos.getNano()).isEqualTo(123_456_789);
            assertThat(nanos.toEpochMilli()).isEqualTo(123L);
        }

        @Test
        @DisplayName("truncatedTo drops the smaller part")
        void truncation() {
            Instant precise = Instant.parse("2024-03-15T10:15:30.987654321Z");
            assertThat(precise.truncatedTo(ChronoUnit.SECONDS)).isEqualTo(Instant.parse("2024-03-15T10:15:30Z"));
            assertThat(precise.truncatedTo(ChronoUnit.MINUTES)).isEqualTo(Instant.parse("2024-03-15T10:15:00Z"));
        }

        @Test
        @DisplayName("mixing seconds and millis gives a wildly different moment")
        void unitMixUp() {
            long n = 1_700_000_000L;
            assertThat(Instant.ofEpochSecond(n).toString()).isEqualTo("2023-11-14T22:13:20Z");
            assertThat(Instant.ofEpochMilli(n).toString()).isEqualTo("1970-01-20T16:13:20Z");
        }
    }

    // ── 3. Unsupported units ──────────────────────────────────────────────────

    @Nested
    @DisplayName("3. Unsupported units")
    class UnsupportedUnitsTest {

        private final Instant instant = Instant.parse("2024-01-31T10:00:00Z");

        @Test
        @DisplayName("DAYS are supported and mean exactly 24 hours")
        void daysSupported() {
            assertThat(instant.plus(1, ChronoUnit.DAYS)).isEqualTo(Instant.parse("2024-02-01T10:00:00Z"));
        }

        @Test
        @DisplayName("MONTHS are not supported")
        void monthsUnsupported() {
            assertThatThrownBy(() -> instant.plus(1, ChronoUnit.MONTHS))
                    .isInstanceOf(UnsupportedTemporalTypeException.class);
        }

        @Test
        @DisplayName("calendar fields such as YEAR are not supported")
        void yearFieldUnsupported() {
            assertThatThrownBy(() -> instant.get(ChronoField.YEAR))
                    .isInstanceOf(UnsupportedTemporalTypeException.class);
        }

        @Test
        @DisplayName("calendar maths go through a zone (UTC) and back")
        void viaZone() {
            Instant nextMonth = instant.atZone(ZoneOffset.UTC).plusMonths(1).toInstant();
            assertThat(nextMonth).isEqualTo(Instant.parse("2024-02-29T10:00:00Z"));
        }
    }

    // ── 4. Clock ──────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("4. Clock")
    class ClockTest {

        private final Instant moment = Instant.parse("2024-03-15T10:15:30Z");
        private final Clock fixed = Clock.fixed(moment, ZoneOffset.UTC);

        @Test
        @DisplayName("a fixed clock always returns the same 'now'")
        void fixedClock() {
            assertThat(Instant.now(fixed)).isEqualTo(moment);
            assertThat(LocalDate.now(fixed)).isEqualTo(LocalDate.of(2024, 3, 15));
            assertThat(LocalDateTime.now(fixed)).isEqualTo(LocalDateTime.of(2024, 3, 15, 10, 15, 30));
        }

        @Test
        @DisplayName("Clock.offset shifts a clock without sleeping")
        void offsetClock() {
            Clock later = Clock.offset(fixed, Duration.ofHours(2));
            assertThat(later.instant()).isEqualTo(Instant.parse("2024-03-15T12:15:30Z"));
        }

        @Test
        @DisplayName("the clock's zone decides the calendar date")
        void zoneDecidesDate() {
            Instant lateEvening = Instant.parse("2024-03-15T23:30:00Z");
            Clock tokyo = Clock.fixed(lateEvening, ZoneId.of("Asia/Tokyo"));
            Clock utc = Clock.fixed(lateEvening, ZoneOffset.UTC);
            assertThat(LocalDate.now(tokyo)).isEqualTo(LocalDate.of(2024, 3, 16));
            assertThat(LocalDate.now(utc)).isEqualTo(LocalDate.of(2024, 3, 15));
        }

        @Test
        @DisplayName("the system clock is after a fixed moment in the past")
        void systemClock() {
            assertThat(Clock.systemUTC().instant().isAfter(moment)).isTrue();
        }
    }
}
