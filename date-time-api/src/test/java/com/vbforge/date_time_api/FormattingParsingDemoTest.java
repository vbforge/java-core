package com.vbforge.date_time_api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * FormattingParsingDemoTest — verifies every concept shown in {@link FormattingParsingDemo}.
 *
 * <p>These tests are always GREEN. Patterns that print words always carry an explicit
 * {@link Locale}, so the results do not depend on the machine's language.
 */
@DisplayName("FormattingParsingDemo — API contracts")
class FormattingParsingDemoTest {

    // ── 1. ISO formatters ─────────────────────────────────────────────────────

    @Nested
    @DisplayName("1. ISO formatters")
    class IsoFormattersTest {

        private final LocalDate date = LocalDate.of(2024, 3, 15);
        private final LocalDateTime dateTime = LocalDateTime.of(2024, 3, 15, 10, 15, 30);
        private final ZonedDateTime paris = ZonedDateTime.of(dateTime, ZoneId.of("Europe/Paris"));

        @Test
        @DisplayName("local date and date-time formatters")
        void localFormatters() {
            assertThat(DateTimeFormatter.ISO_LOCAL_DATE.format(date)).isEqualTo("2024-03-15");
            assertThat(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(dateTime)).isEqualTo("2024-03-15T10:15:30");
        }

        @Test
        @DisplayName("offset and zoned formatters")
        void zonedFormatters() {
            assertThat(DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(paris)).isEqualTo("2024-03-15T10:15:30+01:00");
            assertThat(DateTimeFormatter.ISO_ZONED_DATE_TIME.format(paris))
                    .isEqualTo("2024-03-15T10:15:30+01:00[Europe/Paris]");
        }

        @Test
        @DisplayName("ISO_INSTANT and BASIC_ISO_DATE")
        void instantAndBasic() {
            assertThat(DateTimeFormatter.ISO_INSTANT.format(Instant.parse("2024-03-15T10:15:30Z")))
                    .isEqualTo("2024-03-15T10:15:30Z");
            assertThat(DateTimeFormatter.BASIC_ISO_DATE.format(date)).isEqualTo("20240315");
        }

        @Test
        @DisplayName("toString is ISO, but omits zero seconds while the formatter keeps them")
        void toStringVersusFormatter() {
            LocalDateTime noSeconds = LocalDateTime.of(2024, 3, 15, 10, 15);
            assertThat(date.toString()).isEqualTo("2024-03-15");
            assertThat(noSeconds.toString()).isEqualTo("2024-03-15T10:15");
            assertThat(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(noSeconds)).isEqualTo("2024-03-15T10:15:00");
        }
    }

    // ── 2. Pattern formatting ─────────────────────────────────────────────────

    @Nested
    @DisplayName("2. Pattern formatting")
    class PatternFormattingTest {

        private final LocalDateTime dt = LocalDateTime.of(2024, 3, 15, 14, 5, 9);

        @Test
        @DisplayName("numeric patterns")
        void numericPatterns() {
            assertThat(DateTimeFormatter.ofPattern("dd.MM.yyyy").format(dt)).isEqualTo("15.03.2024");
            assertThat(DateTimeFormatter.ofPattern("MM/dd/yyyy").format(dt)).isEqualTo("03/15/2024");
            assertThat(DateTimeFormatter.ofPattern("HH:mm:ss").format(dt)).isEqualTo("14:05:09");
        }

        @Test
        @DisplayName("hh with a is the 12-hour clock")
        void twelveHourClock() {
            assertThat(DateTimeFormatter.ofPattern("hh:mm a", Locale.US).format(dt)).isEqualTo("02:05 PM");
        }

        @Test
        @DisplayName("EEEE and MMMM print full names for the given locale")
        void names() {
            assertThat(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy", Locale.US).format(dt))
                    .isEqualTo("Friday, March 15, 2024");
        }

        @Test
        @DisplayName("single quotes print literal text")
        void quotedLiteral() {
            assertThat(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm").format(dt)).isEqualTo("2024-03-15T14:05");
        }

        @Test
        @DisplayName("value.format(formatter) equals formatter.format(value)")
        void bothDirections() {
            DateTimeFormatter f = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            assertThat(dt.format(f)).isEqualTo(f.format(dt));
        }

        @Test
        @DisplayName("mm is minutes, MM is month")
        void minutesVersusMonth() {
            assertThat(DateTimeFormatter.ofPattern("mm").format(dt)).isEqualTo("05");
            assertThat(DateTimeFormatter.ofPattern("MM").format(dt)).isEqualTo("03");
        }

        @Test
        @DisplayName("YYYY is the week-based year and can be wrong around New Year")
        void weekBasedYear() {
            LocalDate dec30 = LocalDate.of(2024, 12, 30);
            assertThat(DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.US).format(dec30)).isEqualTo("2024-12-30");
            assertThat(DateTimeFormatter.ofPattern("YYYY-MM-dd", Locale.US).format(dec30)).isEqualTo("2025-12-30");
        }
    }

    // ── 3. Parsing ────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("3. Parsing")
    class ParsingTest {

        @Test
        @DisplayName("ISO text parses without a formatter")
        void isoParsing() {
            assertThat(LocalDate.parse("2024-03-15")).isEqualTo(LocalDate.of(2024, 3, 15));
            assertThat(LocalTime.parse("10:15")).isEqualTo(LocalTime.of(10, 15));
            assertThat(LocalDateTime.parse("2024-03-15T10:15:30")).isEqualTo(LocalDateTime.of(2024, 3, 15, 10, 15, 30));
        }

        @Test
        @DisplayName("custom patterns parse custom text")
        void customPattern() {
            assertThat(LocalDate.parse("15.03.2024", DateTimeFormatter.ofPattern("dd.MM.yyyy")))
                    .isEqualTo(LocalDate.of(2024, 3, 15));
        }

        @Test
        @DisplayName("single d and M accept one or two digits")
        void flexibleDigits() {
            assertThat(LocalDate.parse("5.3.2024", DateTimeFormatter.ofPattern("d.M.yyyy")))
                    .isEqualTo(LocalDate.of(2024, 3, 5));
        }

        @Test
        @DisplayName("a failed parse reports the text and the error index")
        void diagnostics() {
            assertThatThrownBy(() -> LocalDate.parse("2024-3-5"))
                    .isInstanceOfSatisfying(DateTimeParseException.class, ex -> {
                        assertThat(ex.getParsedString()).isEqualTo("2024-3-5");
                        assertThat(ex.getErrorIndex()).isEqualTo(5);
                    });
        }

        @Test
        @DisplayName("the ISO parser rejects Feb 29 in a non-leap year")
        void impossibleDate() {
            assertThatThrownBy(() -> LocalDate.parse("2023-02-29")).isInstanceOf(DateTimeParseException.class);
        }

        @Test
        @DisplayName("text with a time part cannot be parsed as a date-only type")
        void timeLeftOver() {
            assertThatThrownBy(() -> LocalDate.parse("2024-03-15T10:15:30"))
                    .isInstanceOf(DateTimeParseException.class);
        }
    }

    // ── 4. Resolver styles ────────────────────────────────────────────────────

    @Nested
    @DisplayName("4. Resolver styles")
    class ResolverStyleTest {

        private final DateTimeFormatter smart = DateTimeFormatter.ofPattern("uuuu-MM-dd");
        private final DateTimeFormatter strict = smart.withResolverStyle(ResolverStyle.STRICT);
        private final DateTimeFormatter lenient = smart.withResolverStyle(ResolverStyle.LENIENT);

        @Test
        @DisplayName("SMART silently clamps day 31 in February")
        void smartClamps() {
            assertThat(LocalDate.parse("2024-02-31", smart)).isEqualTo(LocalDate.of(2024, 2, 29));
            assertThat(LocalDate.parse("2023-02-30", smart)).isEqualTo(LocalDate.of(2023, 2, 28));
        }

        @Test
        @DisplayName("LENIENT rolls over into the next month")
        void lenientRollsOver() {
            assertThat(LocalDate.parse("2024-02-31", lenient)).isEqualTo(LocalDate.of(2024, 3, 2));
        }

        @Test
        @DisplayName("STRICT rejects Feb 31")
        void strictRejects() {
            assertThatThrownBy(() -> LocalDate.parse("2024-02-31", strict))
                    .isInstanceOf(DateTimeParseException.class);
        }

        @Test
        @DisplayName("day 32 is rejected even in SMART mode")
        void day32() {
            assertThatThrownBy(() -> LocalDate.parse("2024-01-32", smart))
                    .isInstanceOf(DateTimeParseException.class);
        }

        @Test
        @DisplayName("the plain ISO parse is strict")
        void isoIsStrict() {
            assertThatThrownBy(() -> LocalDate.parse("2024-02-31")).isInstanceOf(DateTimeParseException.class);
        }

        @Test
        @DisplayName("STRICT with yyyy (year-of-era) cannot resolve; uuuu can")
        void strictNeedsUuuu() {
            DateTimeFormatter strictWithY = DateTimeFormatter.ofPattern("yyyy-MM-dd").withResolverStyle(ResolverStyle.STRICT);
            assertThatThrownBy(() -> LocalDate.parse("2024-03-15", strictWithY))
                    .isInstanceOf(DateTimeParseException.class);
            assertThat(LocalDate.parse("2024-03-15", strict)).isEqualTo(LocalDate.of(2024, 3, 15));
        }
    }

    // ── 5. Instant and zone formatting ────────────────────────────────────────

    @Nested
    @DisplayName("5. Instant & zone formatting")
    class InstantAndZoneFormattingTest {

        private final Instant instant = Instant.parse("2024-03-15T10:15:30Z");
        private final DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        @Test
        @DisplayName("a pattern formatter without a zone cannot print an Instant")
        void needsZone() {
            assertThatThrownBy(() -> pattern.format(instant)).isInstanceOf(UnsupportedTemporalTypeException.class);
        }

        @Test
        @DisplayName("withZone makes the same Instant print differently per zone")
        void withZone() {
            assertThat(pattern.withZone(ZoneOffset.UTC).format(instant)).isEqualTo("2024-03-15 10:15");
            assertThat(pattern.withZone(ZoneId.of("Asia/Tokyo")).format(instant)).isEqualTo("2024-03-15 19:15");
        }

        @Test
        @DisplayName("z, VV and XXX print the zone name, id and offset")
        void zoneLetters() {
            ZonedDateTime newYork = instant.atZone(ZoneId.of("America/New_York"));
            assertThat(DateTimeFormatter.ofPattern("HH:mm z", Locale.US).format(newYork)).isEqualTo("06:15 EDT");
            assertThat(DateTimeFormatter.ofPattern("HH:mm VV").format(newYork)).isEqualTo("06:15 America/New_York");
            assertThat(DateTimeFormatter.ofPattern("HH:mm XXX").format(newYork)).isEqualTo("06:15 -04:00");
        }

        @Test
        @DisplayName("text with an offset parses via OffsetDateTime to an Instant")
        void offsetText() {
            assertThat(OffsetDateTime.parse("2024-03-15T10:15:30+02:00").toInstant())
                    .isEqualTo(Instant.parse("2024-03-15T08:15:30Z"));
        }

        @Test
        @DisplayName("ZonedDateTime.parse reads offset and region")
        void zonedText() {
            ZonedDateTime parsed = ZonedDateTime.parse("2024-03-15T10:15:30+01:00[Europe/Paris]");
            assertThat(parsed.getZone()).isEqualTo(ZoneId.of("Europe/Paris"));
            assertThat(parsed.toInstant()).isEqualTo(Instant.parse("2024-03-15T09:15:30Z"));
        }
    }

    // ── 6. Formatter reuse and locale ─────────────────────────────────────────

    @Nested
    @DisplayName("6. Formatter reuse & locale")
    class FormatterReuseTest {

        private final LocalDate date = LocalDate.of(2024, 3, 15);
        private final DateTimeFormatter base = DateTimeFormatter.ofPattern("EEEE d MMMM yyyy");

        @Test
        @DisplayName("withLocale changes the language of names")
        void localeChangesNames() {
            assertThat(base.withLocale(Locale.ENGLISH).format(date)).isEqualTo("Friday 15 March 2024");
            assertThat(base.withLocale(Locale.FRENCH).format(date)).isEqualTo("vendredi 15 mars 2024");
        }

        @Test
        @DisplayName("withLocale returns a new formatter and leaves the original alone")
        void immutable() {
            DateTimeFormatter french = base.withLocale(Locale.FRENCH);
            assertThat(french).isNotSameAs(base);
            assertThat(french.getLocale()).isEqualTo(Locale.FRENCH);
        }
    }
}
