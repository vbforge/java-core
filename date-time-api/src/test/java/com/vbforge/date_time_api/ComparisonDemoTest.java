package com.vbforge.date_time_api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ComparisonDemoTest — verifies every concept shown in {@link ComparisonDemo}.
 *
 * <p>These tests are always GREEN. Read them as executable specification of how
 * date-time values are ordered, compared and sorted.
 */
@DisplayName("ComparisonDemo — API contracts")
class ComparisonDemoTest {

    private static final ZoneId PARIS = ZoneId.of("Europe/Paris");
    private static final ZoneId NEW_YORK = ZoneId.of("America/New_York");
    private static final ZoneId TOKYO = ZoneId.of("Asia/Tokyo");

    // ── 1. isBefore / isAfter / equals / compareTo ────────────────────────────

    @Nested
    @DisplayName("1. isBefore / isAfter / equals / compareTo")
    class BeforeAfterEqualsTest {

        private final LocalDate earlier = LocalDate.of(2024, 3, 10);
        private final LocalDate later = LocalDate.of(2024, 3, 15);

        @Test
        @DisplayName("isBefore, isAfter and isEqual on dates")
        void dateComparison() {
            assertThat(earlier.isBefore(later)).isTrue();
            assertThat(earlier.isAfter(later)).isFalse();
            assertThat(earlier.isEqual(later)).isFalse();
        }

        @Test
        @DisplayName("equals compares values, not references")
        void valueEquality() {
            assertThat(later).isEqualTo(LocalDate.parse("2024-03-15"));
            assertThat(later).isNotSameAs(LocalDate.parse("2024-03-15"));
        }

        @Test
        @DisplayName("compareTo: test the sign only; zero means equal")
        void compareToSign() {
            assertThat(Integer.signum(earlier.compareTo(later))).isEqualTo(-1);
            assertThat(Integer.signum(later.compareTo(earlier))).isEqualTo(1);
            assertThat(later.compareTo(LocalDate.of(2024, 3, 15))).isZero();
        }

        @Test
        @DisplayName("the same API works for time and date-time")
        void timeAndDateTime() {
            assertThat(LocalTime.of(9, 0).isBefore(LocalTime.of(17, 0))).isTrue();
            assertThat(LocalDateTime.of(2024, 3, 15, 23, 59).isBefore(LocalDateTime.of(2024, 3, 16, 0, 1))).isTrue();
        }

        @Test
        @DisplayName("two date-times on different days have different dates")
        void sameCalendarDay() {
            LocalDateTime a = LocalDateTime.of(2024, 3, 15, 23, 59);
            LocalDateTime b = LocalDateTime.of(2024, 3, 16, 0, 1);
            assertThat(a.toLocalDate()).isNotEqualTo(b.toLocalDate());
        }

        @Test
        @DisplayName("equality is exact down to the nanosecond")
        void nanoPrecision() {
            LocalDateTime tenSharp = LocalDateTime.of(2024, 3, 15, 10, 0);
            assertThat(tenSharp).isNotEqualTo(tenSharp.plusNanos(1));
        }
    }

    // ── 2. Zoned equality ─────────────────────────────────────────────────────

    @Nested
    @DisplayName("2. Zoned equality")
    class ZonedEqualityTest {

        private final ZonedDateTime paris = ZonedDateTime.of(2024, 1, 15, 12, 0, 0, 0, PARIS);
        private final ZonedDateTime newYork = ZonedDateTime.of(2024, 1, 15, 6, 0, 0, 0, NEW_YORK);

        @Test
        @DisplayName("isEqual compares the instant")
        void isEqualUsesInstant() {
            assertThat(paris.isEqual(newYork)).isTrue();
            assertThat(paris.toInstant()).isEqualTo(newYork.toInstant());
        }

        @Test
        @DisplayName("equals compares local date-time, offset and zone as well")
        void equalsUsesEverything() {
            assertThat(paris.equals(newYork)).isFalse();
        }

        @Test
        @DisplayName("compareTo is consistent with equals, so it is not zero for the same instant")
        void compareToConsistentWithEquals() {
            assertThat(paris.compareTo(newYork)).isNotZero();
            assertThat(Integer.signum(paris.compareTo(newYork))).isEqualTo(1);
        }

        @Test
        @DisplayName("isBefore and isAfter look at the instant only")
        void beforeAfter() {
            assertThat(paris.isBefore(newYork.plusSeconds(1))).isTrue();
            assertThat(paris.isAfter(newYork.minusSeconds(1))).isTrue();
        }

        @Test
        @DisplayName("comparing wall clocks of different zones gives a wrong answer")
        void wallClockTrap() {
            assertThat(paris.toLocalDateTime().isAfter(newYork.toLocalDateTime())).isTrue();
            assertThat(paris.isAfter(newYork)).isFalse();
        }

        @Test
        @DisplayName("OffsetDateTime has the same isEqual / equals split")
        void offsetDateTime() {
            OffsetDateTime a = OffsetDateTime.of(2024, 1, 15, 12, 0, 0, 0, ZoneOffset.ofHours(1));
            OffsetDateTime b = OffsetDateTime.of(2024, 1, 15, 11, 0, 0, 0, ZoneOffset.UTC);
            assertThat(a.isEqual(b)).isTrue();
            assertThat(a.equals(b)).isFalse();
        }
    }

    // ── 3. Sorting and extremes ───────────────────────────────────────────────

    @Nested
    @DisplayName("3. Sorting and extremes")
    class SortingTest {

        private List<LocalDate> sampleDates() {
            return new ArrayList<>(Arrays.asList(
                    LocalDate.of(2024, 3, 15), LocalDate.of(2023, 12, 31), LocalDate.of(2024, 1, 1)));
        }

        @Test
        @DisplayName("natural order is oldest first")
        void ascending() {
            List<LocalDate> dates = sampleDates();
            Collections.sort(dates);
            assertThat(dates).containsExactly(
                    LocalDate.of(2023, 12, 31), LocalDate.of(2024, 1, 1), LocalDate.of(2024, 3, 15));
        }

        @Test
        @DisplayName("Comparator.reverseOrder gives newest first")
        void descending() {
            List<LocalDate> dates = sampleDates();
            dates.sort(Comparator.reverseOrder());
            assertThat(dates).containsExactly(
                    LocalDate.of(2024, 3, 15), LocalDate.of(2024, 1, 1), LocalDate.of(2023, 12, 31));
        }

        @Test
        @DisplayName("Collections.min and max")
        void minMax() {
            List<LocalDate> dates = sampleDates();
            assertThat(Collections.min(dates)).isEqualTo(LocalDate.of(2023, 12, 31));
            assertThat(Collections.max(dates)).isEqualTo(LocalDate.of(2024, 3, 15));
        }

        @Test
        @DisplayName("natural order of ZonedDateTime is chronological across zones")
        void mixedZonesChronological() {
            List<ZonedDateTime> moments = mixedZones();
            Collections.sort(moments);
            assertThat(zoneIds(moments)).containsExactly("America/New_York", "Asia/Tokyo", "Europe/Paris");
        }

        @Test
        @DisplayName("sorting by wall-clock fields gives a wrong order for mixed zones")
        void mixedZonesWallClock() {
            List<ZonedDateTime> moments = mixedZones();
            moments.sort(Comparator.comparing(ZonedDateTime::toLocalDateTime));
            assertThat(zoneIds(moments)).containsExactly("America/New_York", "Europe/Paris", "Asia/Tokyo");
        }

        private List<ZonedDateTime> mixedZones() {
            // instants: Paris 11:00Z, New York 09:30Z, Tokyo 10:30Z
            return new ArrayList<>(Arrays.asList(
                    ZonedDateTime.of(2024, 1, 15, 12, 0, 0, 0, PARIS),
                    ZonedDateTime.of(2024, 1, 15, 4, 30, 0, 0, NEW_YORK),
                    ZonedDateTime.of(2024, 1, 15, 19, 30, 0, 0, TOKYO)));
        }

        private List<String> zoneIds(List<ZonedDateTime> list) {
            return list.stream().map(z -> z.getZone().getId()).collect(Collectors.toList());
        }
    }

    // ── 4. Ranges ─────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("4. Ranges")
    class RangesTest {

        @Test
        @DisplayName("closed range [start, end] includes both ends")
        void closedRange() {
            LocalDate start = LocalDate.of(2024, 3, 1);
            LocalDate end = LocalDate.of(2024, 3, 31);
            assertThat(inClosed(start, start, end)).isTrue();
            assertThat(inClosed(end, start, end)).isTrue();
            assertThat(inClosed(LocalDate.of(2024, 4, 1), start, end)).isFalse();
        }

        @Test
        @DisplayName("half-open range [start, end) excludes the end")
        void halfOpenRange() {
            LocalTime open = LocalTime.of(9, 0);
            LocalTime close = LocalTime.of(17, 0);
            assertThat(inHalfOpen(open, open, close)).isTrue();
            assertThat(inHalfOpen(close, open, close)).isFalse();
        }

        @Test
        @DisplayName("back-to-back half-open slots do not overlap")
        void touchingSlots() {
            assertThat(overlaps(at(9), at(10), at(10), at(11))).isFalse();
        }

        @Test
        @DisplayName("partially overlapping and nested slots overlap")
        void overlappingSlots() {
            assertThat(overlaps(at(9), at(11), at(10), at(12))).isTrue();
            assertThat(overlaps(at(9), at(12), at(10), at(11))).isTrue();
        }

        private boolean inClosed(LocalDate d, LocalDate start, LocalDate end) {
            return !d.isBefore(start) && !d.isAfter(end);
        }

        private boolean inHalfOpen(LocalTime t, LocalTime start, LocalTime end) {
            return !t.isBefore(start) && t.isBefore(end);
        }

        private boolean overlaps(LocalDateTime startA, LocalDateTime endA, LocalDateTime startB, LocalDateTime endB) {
            return startA.isBefore(endB) && startB.isBefore(endA);
        }

        private LocalDateTime at(int hour) {
            return LocalDateTime.of(2024, 3, 15, hour, 0);
        }
    }
}
