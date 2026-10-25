package com.vbforge.date_time_api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ConversionTasksTest — tests for every method in {@link ConversionTasks}.
 *
 * <p><b>Getting started:</b>
 * <ol>
 *   <li>Open {@code ConversionTasks.java} and implement a method.</li>
 *   <li>Remove the {@code @Disabled} annotation from the corresponding {@code @Nested} class.</li>
 *   <li>Run {@code mvn test -Dtest=ConversionTasksTest} — those tests should go GREEN ✅</li>
 *   <li>Repeat until all 6 task groups are GREEN.</li>
 * </ol>
 */
@DisplayName("ConversionTasks — your implementations")
class ConversionTasksTest {

    private static final ZoneId PARIS = ZoneId.of("Europe/Paris");
    private static final ZoneId NEW_YORK = ZoneId.of("America/New_York");
    private static final ZoneId TOKYO = ZoneId.of("Asia/Tokyo");

    private ConversionTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new ConversionTasks();
    }

    // ── Task 1: epochSecondsUtc ───────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement epochSecondsUtc()")
    @DisplayName("Task 1 — epochSecondsUtc()")
    class EpochSecondsUtcTest {

        @Test
        @DisplayName("1970-01-01T00:00:00 → 0")
        void epoch() {
            assertThat(tasks.epochSecondsUtc(LocalDateTime.of(1970, 1, 1, 0, 0))).isZero();
        }

        @Test
        @DisplayName("2023-11-14T22:13:20 → 1700000000")
        void roundNumber() {
            assertThat(tasks.epochSecondsUtc(LocalDateTime.of(2023, 11, 14, 22, 13, 20))).isEqualTo(1_700_000_000L);
        }

        @Test
        @DisplayName("2024-03-15T10:15:30 → 1710497730")
        void typical() {
            assertThat(tasks.epochSecondsUtc(LocalDateTime.of(2024, 3, 15, 10, 15, 30))).isEqualTo(1_710_497_730L);
        }

        @Test
        @DisplayName("1969-12-31T23:59:59 → -1")
        void beforeEpoch() {
            assertThat(tasks.epochSecondsUtc(LocalDateTime.of(1969, 12, 31, 23, 59, 59))).isEqualTo(-1L);
        }

        @Test
        @DisplayName("nanoseconds do not change the result")
        void nanosIgnored() {
            assertThat(tasks.epochSecondsUtc(LocalDateTime.of(2024, 3, 15, 10, 15, 30, 999_999_999)))
                    .isEqualTo(1_710_497_730L);
        }
    }

    // ── Task 2: toInstant ─────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement toInstant()")
    @DisplayName("Task 2 — toInstant()")
    class ToInstantTest {

        @ParameterizedTest(name = "{0} in {1} → {2}")
        @CsvSource({
                "2024-03-15T12:00:00, Europe/Paris,     2024-03-15T11:00:00Z",
                "2024-07-15T12:00:00, Europe/Paris,     2024-07-15T10:00:00Z",
                "2024-03-15T12:00:00, UTC,              2024-03-15T12:00:00Z",
                "2024-03-15T20:00:00, Asia/Tokyo,       2024-03-15T11:00:00Z",
                "2024-11-03T01:30:00, America/New_York, 2024-11-03T05:30:00Z",
                "2024-03-10T02:30:00, America/New_York, 2024-03-10T07:30:00Z"
        })
        @DisplayName("winter, summer, UTC, DST overlap and DST gap")
        void conversions(String local, String zone, String expected) {
            assertThat(tasks.toInstant(LocalDateTime.parse(local), ZoneId.of(zone)))
                    .isEqualTo(Instant.parse(expected));
        }

        @Test
        @DisplayName("same wall clock, different zone → different instants")
        void zoneMatters() {
            LocalDateTime noon = LocalDateTime.of(2024, 3, 15, 12, 0);
            assertThat(tasks.toInstant(noon, TOKYO)).isNotEqualTo(tasks.toInstant(noon, PARIS));
        }
    }

    // ── Task 3: toLocalDateTime ───────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement toLocalDateTime()")
    @DisplayName("Task 3 — toLocalDateTime()")
    class ToLocalDateTimeTest {

        @ParameterizedTest(name = "{0} in {1} → {2}")
        @CsvSource({
                "2024-03-15T11:00:00Z, UTC,              2024-03-15T11:00:00",
                "2024-03-15T11:00:00Z, Asia/Tokyo,       2024-03-15T20:00:00",
                "2024-03-15T23:30:00Z, Asia/Tokyo,       2024-03-16T08:30:00",
                "2024-03-15T02:00:00Z, America/New_York, 2024-03-14T22:00:00",
                "2024-11-03T06:30:00Z, America/New_York, 2024-11-03T01:30:00"
        })
        @DisplayName("same instant, different wall clocks")
        void wallClocks(String instant, String zone, String expected) {
            assertThat(tasks.toLocalDateTime(Instant.parse(instant), ZoneId.of(zone)))
                    .isEqualTo(LocalDateTime.parse(expected));
        }

        @Test
        @DisplayName("round trip with toInstant for an unambiguous time")
        void roundTrip() {
            LocalDateTime local = LocalDateTime.of(2024, 7, 15, 9, 45);
            Instant instant = tasks.toInstant(local, PARIS);
            assertThat(tasks.toLocalDateTime(instant, PARIS)).isEqualTo(local);
        }
    }

    // ── Task 4: startOfDayInstant ─────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement startOfDayInstant()")
    @DisplayName("Task 4 — startOfDayInstant()")
    class StartOfDayInstantTest {

        @ParameterizedTest(name = "{0} in {1} → {2}")
        @CsvSource({
                "2024-03-15, UTC,              2024-03-15T00:00:00Z",
                "2024-03-15, Asia/Tokyo,       2024-03-14T15:00:00Z",
                "2024-03-10, America/New_York, 2024-03-10T05:00:00Z",
                "2024-03-11, America/New_York, 2024-03-11T04:00:00Z"
        })
        @DisplayName("the first moment of the day in each zone")
        void starts(String date, String zone, String expected) {
            assertThat(tasks.startOfDayInstant(LocalDate.parse(date), ZoneId.of(zone)))
                    .isEqualTo(Instant.parse(expected));
        }

        @Test
        @DisplayName("a zone east of UTC starts its day earlier than UTC does")
        void eastStartsEarlier() {
            LocalDate date = LocalDate.of(2024, 3, 15);
            assertThat(tasks.startOfDayInstant(date, TOKYO).isBefore(tasks.startOfDayInstant(date, ZoneOffset.UTC)))
                    .isTrue();
        }
    }

    // ── Task 5: toLegacyDate ──────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement toLegacyDate()")
    @DisplayName("Task 5 — toLegacyDate()")
    class ToLegacyDateTest {

        @Test
        @DisplayName("Paris 12:00 → 1710500400000")
        void paris() {
            assertThat(tasks.toLegacyDate(ZonedDateTime.of(2024, 3, 15, 12, 0, 0, 0, PARIS)).getTime())
                    .isEqualTo(1_710_500_400_000L);
        }

        @Test
        @DisplayName("Tokyo 20:00 is the same moment as Paris 12:00")
        void tokyo() {
            assertThat(tasks.toLegacyDate(ZonedDateTime.of(2024, 3, 15, 20, 0, 0, 0, TOKYO)).getTime())
                    .isEqualTo(1_710_500_400_000L);
        }

        @Test
        @DisplayName("epoch → 0")
        void epoch() {
            assertThat(tasks.toLegacyDate(ZonedDateTime.of(1970, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC)).getTime()).isZero();
        }

        @Test
        @DisplayName("nanoseconds are cut to milliseconds")
        void nanosCut() {
            ZonedDateTime precise = Instant.parse("2024-03-15T11:00:00.123456789Z").atZone(ZoneOffset.UTC);
            assertThat(tasks.toLegacyDate(precise).getTime()).isEqualTo(1_710_500_400_123L);
        }

        @Test
        @DisplayName("before 1970 gives a negative time")
        void beforeEpoch() {
            assertThat(tasks.toLegacyDate(ZonedDateTime.of(1969, 12, 31, 23, 59, 59, 0, ZoneOffset.UTC)).getTime())
                    .isEqualTo(-1000L);
        }
    }

    // ── Task 6: fromLegacyDate ────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement fromLegacyDate()")
    @DisplayName("Task 6 — fromLegacyDate()")
    class FromLegacyDateTest {

        @Test
        @DisplayName("Date(0) in UTC → 1970-01-01T00:00")
        void epoch() {
            ZonedDateTime result = tasks.fromLegacyDate(new Date(0), ZoneOffset.UTC);
            assertThat(result.toLocalDateTime()).isEqualTo(LocalDateTime.of(1970, 1, 1, 0, 0));
        }

        @Test
        @DisplayName("1710500400000 in Tokyo → 20:00")
        void tokyo() {
            ZonedDateTime result = tasks.fromLegacyDate(new Date(1_710_500_400_000L), TOKYO);
            assertThat(result.toLocalDateTime()).isEqualTo(LocalDateTime.of(2024, 3, 15, 20, 0));
            assertThat(result.getZone()).isEqualTo(TOKYO);
        }

        @Test
        @DisplayName("1710500400000 in Paris → 12:00")
        void paris() {
            ZonedDateTime result = tasks.fromLegacyDate(new Date(1_710_500_400_000L), PARIS);
            assertThat(result.toLocalDateTime()).isEqualTo(LocalDateTime.of(2024, 3, 15, 12, 0));
        }

        @Test
        @DisplayName("negative time → before 1970")
        void beforeEpoch() {
            ZonedDateTime result = tasks.fromLegacyDate(new Date(-1000), ZoneOffset.UTC);
            assertThat(result.toLocalDateTime()).isEqualTo(LocalDateTime.of(1969, 12, 31, 23, 59, 59));
        }

        @Test
        @DisplayName("the instant is preserved, so it round-trips through toLegacyDate")
        void roundTrip() {
            Date original = new Date(1_710_500_400_123L);
            ZonedDateTime zoned = tasks.fromLegacyDate(original, NEW_YORK);
            assertThat(zoned.toInstant().toEpochMilli()).isEqualTo(original.getTime());
        }
    }
}
