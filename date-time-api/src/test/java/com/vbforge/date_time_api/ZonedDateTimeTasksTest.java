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

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ZonedDateTimeTasksTest — tests for every method in {@link ZonedDateTimeTasks}.
 *
 * <p><b>Getting started:</b>
 * <ol>
 *   <li>Open {@code ZonedDateTimeTasks.java} and implement a method.</li>
 *   <li>Remove the {@code @Disabled} annotation from the corresponding {@code @Nested} class.</li>
 *   <li>Run {@code mvn test -Dtest=ZonedDateTimeTasksTest} — those tests should go GREEN ✅</li>
 *   <li>Repeat until all 5 task groups are GREEN.</li>
 * </ol>
 */
@DisplayName("ZonedDateTimeTasks — your implementations")
class ZonedDateTimeTasksTest {

    private static final ZoneId PARIS = ZoneId.of("Europe/Paris");
    private static final ZoneId NEW_YORK = ZoneId.of("America/New_York");
    private static final ZoneId TOKYO = ZoneId.of("Asia/Tokyo");

    private ZonedDateTimeTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new ZonedDateTimeTasks();
    }

    // ── Task 1: convertToZone ─────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement convertToZone()")
    @DisplayName("Task 1 — convertToZone()")
    class ConvertToZoneTest {

        @Test
        @DisplayName("Paris 12:00 → New York 07:00")
        void parisToNewYork() {
            ZonedDateTime result = tasks.convertToZone(ZonedDateTime.of(2024, 3, 15, 12, 0, 0, 0, PARIS), NEW_YORK);
            assertThat(result.toLocalDateTime()).isEqualTo(LocalDateTime.of(2024, 3, 15, 7, 0));
            assertThat(result.getZone()).isEqualTo(NEW_YORK);
        }

        @Test
        @DisplayName("Paris 12:00 → Tokyo 20:00")
        void parisToTokyo() {
            ZonedDateTime result = tasks.convertToZone(ZonedDateTime.of(2024, 3, 15, 12, 0, 0, 0, PARIS), TOKYO);
            assertThat(result.toLocalDateTime()).isEqualTo(LocalDateTime.of(2024, 3, 15, 20, 0));
        }

        @Test
        @DisplayName("the date can change: New York 23:30 → Paris 04:30 next day")
        void dateChanges() {
            ZonedDateTime result = tasks.convertToZone(ZonedDateTime.of(2024, 3, 15, 23, 30, 0, 0, NEW_YORK), PARIS);
            assertThat(result.toLocalDateTime()).isEqualTo(LocalDateTime.of(2024, 3, 16, 4, 30));
        }

        @Test
        @DisplayName("the instant never changes")
        void sameInstant() {
            ZonedDateTime source = ZonedDateTime.of(2024, 7, 1, 8, 45, 10, 0, PARIS);
            assertThat(tasks.convertToZone(source, TOKYO).toInstant()).isEqualTo(source.toInstant());
        }

        @Test
        @DisplayName("converting to the same zone changes nothing")
        void sameZone() {
            ZonedDateTime source = ZonedDateTime.of(2024, 3, 15, 12, 0, 0, 0, PARIS);
            assertThat(tasks.convertToZone(source, PARIS)).isEqualTo(source);
        }
    }

    // ── Task 2: meetingTimeFor ────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement meetingTimeFor()")
    @DisplayName("Task 2 — meetingTimeFor()")
    class MeetingTimeForTest {

        @Test
        @DisplayName("09:00 New York → 22:00 Tokyo")
        void newYorkToTokyo() {
            assertThat(tasks.meetingTimeFor(LocalDateTime.of(2024, 3, 15, 9, 0), NEW_YORK, TOKYO))
                    .isEqualTo(LocalDateTime.of(2024, 3, 15, 22, 0));
        }

        @Test
        @DisplayName("09:00 New York → 14:00 Paris")
        void newYorkToParis() {
            assertThat(tasks.meetingTimeFor(LocalDateTime.of(2024, 3, 15, 9, 0), NEW_YORK, PARIS))
                    .isEqualTo(LocalDateTime.of(2024, 3, 15, 14, 0));
        }

        @Test
        @DisplayName("20:00 New York → 09:00 Tokyo the next day")
        void nextDay() {
            assertThat(tasks.meetingTimeFor(LocalDateTime.of(2024, 3, 15, 20, 0), NEW_YORK, TOKYO))
                    .isEqualTo(LocalDateTime.of(2024, 3, 16, 9, 0));
        }

        @Test
        @DisplayName("same zone → unchanged")
        void sameZone() {
            LocalDateTime time = LocalDateTime.of(2024, 3, 15, 9, 0);
            assertThat(tasks.meetingTimeFor(time, PARIS, PARIS)).isEqualTo(time);
        }

        @Test
        @DisplayName("daylight saving is honoured: the gap between New York and Paris shrinks for a week")
        void dstGap() {
            // 2024-03-12: New York is already on EDT (-4) but Paris is still on CET (+1) → 5 hours
            assertThat(tasks.meetingTimeFor(LocalDateTime.of(2024, 3, 12, 9, 0), NEW_YORK, PARIS))
                    .isEqualTo(LocalDateTime.of(2024, 3, 12, 14, 0));
            // 2024-03-05: both on winter time → 6 hours
            assertThat(tasks.meetingTimeFor(LocalDateTime.of(2024, 3, 5, 9, 0), NEW_YORK, PARIS))
                    .isEqualTo(LocalDateTime.of(2024, 3, 5, 15, 0));
        }
    }

    // ── Task 3: offsetAt ──────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement offsetAt()")
    @DisplayName("Task 3 — offsetAt()")
    class OffsetAtTest {

        @ParameterizedTest(name = "{0} at {1} → {2}")
        @CsvSource({
                "Europe/Paris,     2024-01-15T12:00:00Z, +01:00",
                "Europe/Paris,     2024-07-15T12:00:00Z, +02:00",
                "America/New_York, 2024-07-15T12:00:00Z, -04:00",
                "America/New_York, 2024-01-15T12:00:00Z, -05:00",
                "Asia/Kolkata,     2024-07-15T12:00:00Z, +05:30",
                "Asia/Tokyo,       2024-01-15T12:00:00Z, +09:00"
        })
        @DisplayName("offsets in winter, summer and with half-hour zones")
        void offsets(String zone, String instant, String expectedOffset) {
            assertThat(tasks.offsetAt(ZoneId.of(zone), Instant.parse(instant)))
                    .isEqualTo(ZoneOffset.of(expectedOffset));
        }

        @Test
        @DisplayName("UTC is always offset zero")
        void utc() {
            assertThat(tasks.offsetAt(ZoneOffset.UTC, Instant.parse("2024-07-15T12:00:00Z"))).isEqualTo(ZoneOffset.UTC);
        }

        @Test
        @DisplayName("the switch happens at the exact transition instant")
        void transitionInstant() {
            // New York springs forward at 2024-03-10T07:00:00Z
            assertThat(tasks.offsetAt(NEW_YORK, Instant.parse("2024-03-10T06:59:59Z"))).isEqualTo(ZoneOffset.ofHours(-5));
            assertThat(tasks.offsetAt(NEW_YORK, Instant.parse("2024-03-10T07:00:00Z"))).isEqualTo(ZoneOffset.ofHours(-4));
        }
    }

    // ── Task 4: hoursInDay ────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement hoursInDay()")
    @DisplayName("Task 4 — hoursInDay()")
    class HoursInDayTest {

        @Test
        @DisplayName("an ordinary day has 24 hours")
        void ordinaryDay() {
            assertThat(tasks.hoursInDay(LocalDate.of(2024, 6, 1), NEW_YORK)).isEqualTo(24);
        }

        @Test
        @DisplayName("spring-forward day has 23 hours")
        void springForward() {
            assertThat(tasks.hoursInDay(LocalDate.of(2024, 3, 10), NEW_YORK)).isEqualTo(23);
        }

        @Test
        @DisplayName("fall-back day has 25 hours")
        void fallBack() {
            assertThat(tasks.hoursInDay(LocalDate.of(2024, 11, 3), NEW_YORK)).isEqualTo(25);
        }

        @Test
        @DisplayName("southern hemisphere: Sydney's fall-back day is 2024-04-07")
        void sydney() {
            assertThat(tasks.hoursInDay(LocalDate.of(2024, 4, 7), ZoneId.of("Australia/Sydney"))).isEqualTo(25);
        }

        @Test
        @DisplayName("a zone without DST is always 24 hours")
        void noDst() {
            assertThat(tasks.hoursInDay(LocalDate.of(2024, 3, 10), TOKYO)).isEqualTo(24);
        }

        @Test
        @DisplayName("the day before and after a transition are normal")
        void neighbours() {
            assertThat(tasks.hoursInDay(LocalDate.of(2024, 3, 9), NEW_YORK)).isEqualTo(24);
            assertThat(tasks.hoursInDay(LocalDate.of(2024, 3, 11), NEW_YORK)).isEqualTo(24);
        }
    }

    // ── Task 5: isDaylightSavingTime ──────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement isDaylightSavingTime()")
    @DisplayName("Task 5 — isDaylightSavingTime()")
    class IsDaylightSavingTimeTest {

        @ParameterizedTest(name = "{0} at {1} → {2}")
        @CsvSource({
                "America/New_York, 2024-07-15T12:00:00Z, true",
                "America/New_York, 2024-01-15T12:00:00Z, false",
                "Europe/Paris,     2024-07-15T12:00:00Z, true",
                "Europe/Paris,     2024-12-15T12:00:00Z, false",
                "Australia/Sydney, 2024-01-15T00:00:00Z, true",
                "Australia/Sydney, 2024-07-15T00:00:00Z, false"
        })
        @DisplayName("northern and southern hemisphere")
        void hemispheres(String zone, String instant, boolean expected) {
            assertThat(tasks.isDaylightSavingTime(ZoneId.of(zone), Instant.parse(instant))).isEqualTo(expected);
        }

        @Test
        @DisplayName("Tokyo has no DST")
        void tokyo() {
            assertThat(tasks.isDaylightSavingTime(TOKYO, Instant.parse("2024-07-15T12:00:00Z"))).isFalse();
        }

        @Test
        @DisplayName("UTC has no DST")
        void utc() {
            assertThat(tasks.isDaylightSavingTime(ZoneOffset.UTC, Instant.parse("2024-07-15T12:00:00Z"))).isFalse();
        }
    }
}
