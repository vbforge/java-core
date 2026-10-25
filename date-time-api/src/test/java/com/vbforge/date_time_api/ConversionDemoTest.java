package com.vbforge.date_time_api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ConversionDemoTest — verifies every concept shown in {@link ConversionDemo}.
 *
 * <p>These tests are always GREEN. Every conversion names its zone explicitly, so the
 * results do not depend on the machine's default time zone.
 */
@DisplayName("ConversionDemo — API contracts")
class ConversionDemoTest {

    private static final ZoneId PARIS = ZoneId.of("Europe/Paris");
    private static final ZoneId TOKYO = ZoneId.of("Asia/Tokyo");
    private static final ZoneId NEW_YORK = ZoneId.of("America/New_York");

    // ── 1. Instant <-> LocalDateTime ──────────────────────────────────────────

    @Nested
    @DisplayName("1. Instant <-> LocalDateTime")
    class InstantAndLocalDateTimeTest {

        private final Instant instant = Instant.parse("2024-03-15T11:00:00Z");

        @Test
        @DisplayName("one Instant shows different wall clocks in different zones")
        void differentWallClocks() {
            assertThat(LocalDateTime.ofInstant(instant, ZoneOffset.UTC)).isEqualTo(LocalDateTime.of(2024, 3, 15, 11, 0));
            assertThat(LocalDateTime.ofInstant(instant, PARIS)).isEqualTo(LocalDateTime.of(2024, 3, 15, 12, 0));
            assertThat(LocalDateTime.ofInstant(instant, TOKYO)).isEqualTo(LocalDateTime.of(2024, 3, 15, 20, 0));
        }

        @Test
        @DisplayName("going back with the same zone restores the Instant")
        void roundTripWithSameZone() {
            LocalDateTime tokyo = LocalDateTime.ofInstant(instant, TOKYO);
            assertThat(tokyo.atZone(TOKYO).toInstant()).isEqualTo(instant);
        }

        @Test
        @DisplayName("toInstant(offset) interprets the wall clock with a fixed offset")
        void fixedOffset() {
            assertThat(LocalDateTime.of(2024, 3, 15, 11, 0).toInstant(ZoneOffset.UTC)).isEqualTo(instant);
        }

        @Test
        @DisplayName("using the wrong zone gives a moment that is off by the offset")
        void wrongZone() {
            LocalDateTime tokyo = LocalDateTime.ofInstant(instant, TOKYO);
            assertThat(tokyo.toInstant(ZoneOffset.UTC)).isEqualTo(Instant.parse("2024-03-15T20:00:00Z"));
        }
    }

    // ── 2. ZonedDateTime conversions ──────────────────────────────────────────

    @Nested
    @DisplayName("2. ZonedDateTime conversions")
    class ZonedConversionsTest {

        private final Instant instant = Instant.parse("2024-03-15T11:00:00Z");
        private final ZonedDateTime paris = instant.atZone(PARIS);

        @Test
        @DisplayName("each arrow of the conversion table")
        void conversionTable() {
            assertThat(paris.toString()).isEqualTo("2024-03-15T12:00+01:00[Europe/Paris]");
            assertThat(paris.toInstant()).isEqualTo(instant);
            assertThat(paris.toLocalDateTime()).isEqualTo(LocalDateTime.of(2024, 3, 15, 12, 0));
            assertThat(paris.toLocalDate()).isEqualTo(LocalDate.of(2024, 3, 15));
            assertThat(paris.toOffsetDateTime().toString()).isEqualTo("2024-03-15T12:00+01:00");
        }

        @Test
        @DisplayName("LocalDateTime gains a zone with atZone and an offset with atOffset")
        void localToZoned() {
            LocalDateTime local = LocalDateTime.of(2024, 3, 15, 12, 0);
            assertThat(local.atZone(PARIS)).isEqualTo(paris);
            OffsetDateTime offset = local.atOffset(ZoneOffset.ofHours(1));
            assertThat(offset.toInstant()).isEqualTo(instant);
        }

        @Test
        @DisplayName("withZoneSameInstant moves to another zone")
        void otherZone() {
            ZonedDateTime tokyo = paris.withZoneSameInstant(TOKYO);
            assertThat(tokyo.toLocalDateTime()).isEqualTo(LocalDateTime.of(2024, 3, 15, 20, 0));
            assertThat(tokyo.toInstant()).isEqualTo(instant);
        }

        @Test
        @DisplayName("UTC wall clock of any zoned value")
        void utcWallClock() {
            assertThat(paris.withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime())
                    .isEqualTo(LocalDateTime.of(2024, 3, 15, 11, 0));
        }

        @Test
        @DisplayName("Instant -> ZonedDateTime -> Instant is lossless")
        void lossless() {
            assertThat(instant.atZone(NEW_YORK).toInstant()).isEqualTo(instant);
        }
    }

    // ── 3. Round-trip gotcha ──────────────────────────────────────────────────

    @Nested
    @DisplayName("3. Round-trip gotcha (DST overlap)")
    class RoundTripGotchaTest {

        private final Instant secondOccurrence = Instant.parse("2024-11-03T06:30:00Z");   // 01:30 EST

        @Test
        @DisplayName("both occurrences of 01:30 give the same LocalDateTime")
        void sameWallClock() {
            Instant firstOccurrence = Instant.parse("2024-11-03T05:30:00Z");               // 01:30 EDT
            assertThat(LocalDateTime.ofInstant(firstOccurrence, NEW_YORK))
                    .isEqualTo(LocalDateTime.ofInstant(secondOccurrence, NEW_YORK));
        }

        @Test
        @DisplayName("going back through LocalDateTime lands on the earlier occurrence")
        void lossyRoundTrip() {
            LocalDateTime wallClock = LocalDateTime.ofInstant(secondOccurrence, NEW_YORK);
            Instant viaLocal = wallClock.atZone(NEW_YORK).toInstant();
            assertThat(viaLocal).isEqualTo(Instant.parse("2024-11-03T05:30:00Z"));
            assertThat(viaLocal).isNotEqualTo(secondOccurrence);
        }

        @Test
        @DisplayName("a ZonedDateTime keeps the offset, so nothing is lost")
        void zonedKeepsOffset() {
            ZonedDateTime zoned = secondOccurrence.atZone(NEW_YORK);
            assertThat(zoned.getOffset()).isEqualTo(ZoneOffset.ofHours(-5));
            assertThat(zoned.toInstant()).isEqualTo(secondOccurrence);
        }
    }

    // ── 4. Legacy interop ─────────────────────────────────────────────────────

    @Nested
    @DisplayName("4. Legacy interop")
    class LegacyInteropTest {

        private final Instant instant = Instant.parse("2024-03-15T10:15:30Z");

        @Test
        @DisplayName("Date is an instant with millisecond precision")
        void dateIsAnInstant() {
            Date date = Date.from(instant);
            assertThat(date.getTime()).isEqualTo(1_710_497_730_000L);
            assertThat(date.toInstant()).isEqualTo(instant);
        }

        @Test
        @DisplayName("Date.from truncates nanoseconds to milliseconds")
        void dateLosesNanos() {
            Instant nanos = Instant.ofEpochSecond(0, 123_456_789L);
            assertThat(Date.from(nanos).toInstant()).isEqualTo(Instant.ofEpochMilli(123));
        }

        @Test
        @DisplayName("Date to LocalDate needs a zone")
        void dateToLocalDate() {
            Instant lateEvening = Instant.parse("2024-03-15T23:30:00Z");
            Date date = Date.from(lateEvening);
            assertThat(date.toInstant().atZone(TOKYO).toLocalDate()).isEqualTo(LocalDate.of(2024, 3, 16));
            assertThat(date.toInstant().atZone(ZoneOffset.UTC).toLocalDate()).isEqualTo(LocalDate.of(2024, 3, 15));
        }

        @Test
        @DisplayName("GregorianCalendar and TimeZone bridges")
        void calendarAndTimeZone() {
            ZonedDateTime paris = instant.atZone(PARIS);
            assertThat(GregorianCalendar.from(paris).toZonedDateTime()).isEqualTo(paris);
            assertThat(TimeZone.getTimeZone(PARIS).toZoneId()).isEqualTo(PARIS);
        }
    }

    // ── 5. Epoch numbers ──────────────────────────────────────────────────────

    @Nested
    @DisplayName("5. Epoch numbers")
    class EpochNumbersTest {

        @Test
        @DisplayName("epoch day round trip")
        void epochDay() {
            assertThat(LocalDate.of(2024, 3, 15).toEpochDay()).isEqualTo(19_797L);
            assertThat(LocalDate.ofEpochDay(19_797L)).isEqualTo(LocalDate.of(2024, 3, 15));
        }

        @Test
        @DisplayName("LocalDateTime.toEpochSecond needs an offset")
        void epochSecondNeedsOffset() {
            LocalDateTime ldt = LocalDateTime.of(2024, 3, 15, 10, 15, 30);
            assertThat(ldt.toEpochSecond(ZoneOffset.UTC)).isEqualTo(1_710_497_730L);
            assertThat(ldt.toEpochSecond(ZoneOffset.ofHours(1))).isEqualTo(1_710_494_130L);
        }

        @Test
        @DisplayName("ZonedDateTime.toEpochSecond equals the Instant's epoch second")
        void zonedEpochSecond() {
            ZonedDateTime zoned = LocalDateTime.of(2024, 3, 15, 10, 15, 30).atZone(PARIS);
            assertThat(zoned.toEpochSecond()).isEqualTo(1_710_494_130L);
            assertThat(zoned.toInstant().getEpochSecond()).isEqualTo(1_710_494_130L);
        }
    }
}
