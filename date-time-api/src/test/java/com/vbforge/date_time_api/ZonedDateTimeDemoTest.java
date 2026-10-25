package com.vbforge.date_time_api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.zone.ZoneRulesException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * ZonedDateTimeDemoTest — verifies every concept shown in {@link ZonedDateTimeDemo}.
 *
 * <p>These tests are always GREEN. Every zone is explicit, so they do not depend on the
 * machine's default time zone.
 */
@DisplayName("ZonedDateTimeDemo — API contracts")
class ZonedDateTimeDemoTest {

    private static final ZoneId PARIS = ZoneId.of("Europe/Paris");
    private static final ZoneId NEW_YORK = ZoneId.of("America/New_York");

    // ── 1. ZoneId vs ZoneOffset ───────────────────────────────────────────────

    @Nested
    @DisplayName("1. ZoneId vs ZoneOffset")
    class ZoneIdAndOffsetTest {

        @Test
        @DisplayName("a region uses different offsets in winter and summer")
        void regionOffsetsChange() {
            Instant winter = Instant.parse("2024-01-15T12:00:00Z");
            Instant summer = Instant.parse("2024-07-15T12:00:00Z");
            assertThat(PARIS.getRules().getOffset(winter)).isEqualTo(ZoneOffset.ofHours(1));
            assertThat(PARIS.getRules().getOffset(summer)).isEqualTo(ZoneOffset.ofHours(2));
        }

        @Test
        @DisplayName("a region is not a fixed offset; a ZoneOffset is")
        void fixedOffset() {
            assertThat(PARIS.getRules().isFixedOffset()).isFalse();
            assertThat(ZoneOffset.ofHours(2).getRules().isFixedOffset()).isTrue();
        }

        @Test
        @DisplayName("UTC prints as Z")
        void utcPrintsAsZ() {
            assertThat(ZoneOffset.UTC.toString()).isEqualTo("Z");
        }

        @Test
        @DisplayName("an unknown region id fails fast")
        void unknownRegion() {
            assertThatThrownBy(() -> ZoneId.of("Mars/Base")).isInstanceOf(ZoneRulesException.class);
        }
    }

    // ── 2. Creating and converting ────────────────────────────────────────────

    @Nested
    @DisplayName("2. ZonedDateTime creation & conversion")
    class CreationTest {

        private final ZonedDateTime parisNoon = ZonedDateTime.of(LocalDateTime.of(2024, 3, 15, 12, 0), PARIS);

        @Test
        @DisplayName("of(local, zone) and instant.atZone describe the same value")
        void twoWaysToCreate() {
            assertThat(Instant.parse("2024-03-15T11:00:00Z").atZone(PARIS)).isEqualTo(parisNoon);
        }

        @Test
        @DisplayName("toString shows offset and region id")
        void toStringFormat() {
            assertThat(parisNoon.toString()).isEqualTo("2024-03-15T12:00+01:00[Europe/Paris]");
        }

        @Test
        @DisplayName("withZoneSameInstant keeps the moment and changes the wall clock")
        void sameInstant() {
            ZonedDateTime ny = parisNoon.withZoneSameInstant(NEW_YORK);
            assertThat(ny.toLocalDateTime()).isEqualTo(LocalDateTime.of(2024, 3, 15, 7, 0));
            assertThat(ny.toInstant()).isEqualTo(parisNoon.toInstant());
        }

        @Test
        @DisplayName("withZoneSameLocal keeps the wall clock and changes the moment")
        void sameLocal() {
            ZonedDateTime ny = parisNoon.withZoneSameLocal(NEW_YORK);
            assertThat(ny.toLocalDateTime()).isEqualTo(parisNoon.toLocalDateTime());
            assertThat(ny.toInstant()).isNotEqualTo(parisNoon.toInstant());
        }

        @Test
        @DisplayName("getZone and getOffset")
        void getters() {
            assertThat(parisNoon.getZone()).isEqualTo(PARIS);
            assertThat(parisNoon.getOffset()).isEqualTo(ZoneOffset.ofHours(1));
        }
    }

    // ── 3. DST gap ────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("3. DST gap (spring forward)")
    class DstGapTest {

        @Test
        @DisplayName("a local time inside the gap has no valid offset")
        void noValidOffset() {
            LocalDateTime missing = LocalDateTime.of(2024, 3, 10, 2, 30);
            assertThat(NEW_YORK.getRules().getValidOffsets(missing)).isEmpty();
        }

        @Test
        @DisplayName("a time in the gap is moved forward by the length of the gap")
        void movedForward() {
            ZonedDateTime resolved = ZonedDateTime.of(LocalDateTime.of(2024, 3, 10, 2, 30), NEW_YORK);
            assertThat(resolved.toLocalDateTime()).isEqualTo(LocalDateTime.of(2024, 3, 10, 3, 30));
            assertThat(resolved.getOffset()).isEqualTo(ZoneOffset.ofHours(-4));
        }

        @Test
        @DisplayName("plusDays keeps the wall clock, plusHours(24) keeps the elapsed time")
        void plusDaysVersusPlusHours() {
            ZonedDateTime saturdayNoon = ZonedDateTime.of(2024, 3, 9, 12, 0, 0, 0, NEW_YORK);
            assertThat(saturdayNoon.plusDays(1).toLocalDateTime()).isEqualTo(LocalDateTime.of(2024, 3, 10, 12, 0));
            assertThat(saturdayNoon.plusHours(24).toLocalDateTime()).isEqualTo(LocalDateTime.of(2024, 3, 10, 13, 0));
            assertThat(Duration.between(saturdayNoon, saturdayNoon.plusDays(1))).isEqualTo(Duration.ofHours(23));
        }

        @Test
        @DisplayName("the spring-forward day is 23 hours long")
        void shortDay() {
            ZonedDateTime start = LocalDate.of(2024, 3, 10).atStartOfDay(NEW_YORK);
            ZonedDateTime next = LocalDate.of(2024, 3, 11).atStartOfDay(NEW_YORK);
            assertThat(Duration.between(start, next)).isEqualTo(Duration.ofHours(23));
        }
    }

    // ── 4. DST overlap ────────────────────────────────────────────────────────

    @Nested
    @DisplayName("4. DST overlap (fall back)")
    class DstOverlapTest {

        private final LocalDateTime ambiguous = LocalDateTime.of(2024, 11, 3, 1, 30);

        @Test
        @DisplayName("an ambiguous local time has two valid offsets")
        void twoOffsets() {
            assertThat(NEW_YORK.getRules().getValidOffsets(ambiguous))
                    .containsExactly(ZoneOffset.ofHours(-4), ZoneOffset.ofHours(-5));
        }

        @Test
        @DisplayName("the default picks the earlier offset")
        void defaultIsEarlier() {
            assertThat(ZonedDateTime.of(ambiguous, NEW_YORK).getOffset()).isEqualTo(ZoneOffset.ofHours(-4));
        }

        @Test
        @DisplayName("withLaterOffsetAtOverlap picks the second occurrence, one hour later")
        void laterOffset() {
            ZonedDateTime first = ZonedDateTime.of(ambiguous, NEW_YORK);
            ZonedDateTime second = first.withLaterOffsetAtOverlap();
            assertThat(second.getOffset()).isEqualTo(ZoneOffset.ofHours(-5));
            assertThat(Duration.between(first, second)).isEqualTo(Duration.ofHours(1));
            assertThat(second.toLocalDateTime()).isEqualTo(first.toLocalDateTime());
        }

        @Test
        @DisplayName("the fall-back day is 25 hours long")
        void longDay() {
            ZonedDateTime start = LocalDate.of(2024, 11, 3).atStartOfDay(NEW_YORK);
            ZonedDateTime next = LocalDate.of(2024, 11, 4).atStartOfDay(NEW_YORK);
            assertThat(Duration.between(start, next)).isEqualTo(Duration.ofHours(25));
        }
    }

    // ── 5. OffsetDateTime ─────────────────────────────────────────────────────

    @Nested
    @DisplayName("5. OffsetDateTime")
    class OffsetDateTimeTest {

        private final OffsetDateTime offset =
                OffsetDateTime.of(2024, 3, 15, 12, 0, 0, 0, ZoneOffset.ofHours(1));

        @Test
        @DisplayName("toOffsetDateTime drops the region and keeps the current offset")
        void dropsRegion() {
            ZonedDateTime parisNoon = ZonedDateTime.of(2024, 3, 15, 12, 0, 0, 0, PARIS);
            assertThat(parisNoon.toOffsetDateTime()).isEqualTo(offset);
        }

        @Test
        @DisplayName("a fixed offset does not follow DST, a region does")
        void noDstMaths() {
            ZonedDateTime parisNoon = ZonedDateTime.of(2024, 3, 15, 12, 0, 0, 0, PARIS);
            assertThat(parisNoon.plusMonths(6).getOffset()).isEqualTo(ZoneOffset.ofHours(2));
            assertThat(offset.plusMonths(6).getOffset()).isEqualTo(ZoneOffset.ofHours(1));
        }
    }
}
