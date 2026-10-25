package com.vbforge.date_time_api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * FormattingParsingTasksTest — tests for every method in {@link FormattingParsingTasks}.
 *
 * <p><b>Getting started:</b>
 * <ol>
 *   <li>Open {@code FormattingParsingTasks.java} and implement a method.</li>
 *   <li>Remove the {@code @Disabled} annotation from the corresponding {@code @Nested} class.</li>
 *   <li>Run {@code mvn test -Dtest=FormattingParsingTasksTest} — those tests should go GREEN ✅</li>
 *   <li>Repeat until all 6 task groups are GREEN.</li>
 * </ol>
 */
@DisplayName("FormattingParsingTasks — your implementations")
class FormattingParsingTasksTest {

    private FormattingParsingTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new FormattingParsingTasks();
    }

    // ── Task 1: toEuropeanDate ────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement toEuropeanDate()")
    @DisplayName("Task 1 — toEuropeanDate()")
    class ToEuropeanDateTest {

        @Test
        @DisplayName("2024-03-15 → 15.03.2024")
        void basic() {
            assertThat(tasks.toEuropeanDate(LocalDate.of(2024, 3, 15))).isEqualTo("15.03.2024");
        }

        @Test
        @DisplayName("single-digit day and month are zero-padded")
        void zeroPadded() {
            assertThat(tasks.toEuropeanDate(LocalDate.of(2024, 3, 5))).isEqualTo("05.03.2024");
        }

        @Test
        @DisplayName("2024-12-31 → 31.12.2024")
        void endOfYear() {
            assertThat(tasks.toEuropeanDate(LocalDate.of(2024, 12, 31))).isEqualTo("31.12.2024");
        }

        @Test
        @DisplayName("2000-01-01 → 01.01.2000")
        void newMillennium() {
            assertThat(tasks.toEuropeanDate(LocalDate.of(2000, 1, 1))).isEqualTo("01.01.2000");
        }

        @Test
        @DisplayName("years before 1000 are padded to four digits")
        void shortYear() {
            assertThat(tasks.toEuropeanDate(LocalDate.of(999, 7, 4))).isEqualTo("04.07.0999");
        }
    }

    // ── Task 2: parseUsDate ───────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement parseUsDate()")
    @DisplayName("Task 2 — parseUsDate()")
    class ParseUsDateTest {

        @Test
        @DisplayName("03/15/2024 → 2024-03-15")
        void basic() {
            assertThat(tasks.parseUsDate("03/15/2024")).isEqualTo(LocalDate.of(2024, 3, 15));
        }

        @Test
        @DisplayName("02/29/2024 is valid (leap year)")
        void leapDay() {
            assertThat(tasks.parseUsDate("02/29/2024")).isEqualTo(LocalDate.of(2024, 2, 29));
        }

        @ParameterizedTest(name = "\"{0}\" is rejected")
        @ValueSource(strings = {"02/30/2024", "02/29/2023", "04/31/2024", "13/01/2024", "00/10/2024", "03/32/2024"})
        @DisplayName("dates that do not exist are rejected, not corrected")
        void impossibleDates(String text) {
            assertThatThrownBy(() -> tasks.parseUsDate(text)).isInstanceOf(DateTimeParseException.class);
        }

        @ParameterizedTest(name = "\"{0}\" is rejected")
        @ValueSource(strings = {"3/5/2024", "2024-03-15", "garbage", "", "03/15/24"})
        @DisplayName("wrong shapes are rejected")
        void wrongShapes(String text) {
            assertThatThrownBy(() -> tasks.parseUsDate(text)).isInstanceOf(DateTimeParseException.class);
        }
    }

    // ── Task 3: isValidIsoDate ────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement isValidIsoDate()")
    @DisplayName("Task 3 — isValidIsoDate()")
    class IsValidIsoDateTest {

        @ParameterizedTest(name = "\"{0}\" → true")
        @ValueSource(strings = {"2024-03-15", "2024-02-29", "2000-01-01", "1999-12-31"})
        @DisplayName("valid dates")
        void valid(String text) {
            assertThat(tasks.isValidIsoDate(text)).isTrue();
        }

        @ParameterizedTest(name = "\"{0}\" → false")
        @ValueSource(strings = {"2023-02-29", "2024-02-30", "2024-13-01", "2024-3-5", "15.03.2024", "2024-03-15T10:00", "abc", ""})
        @DisplayName("invalid text")
        void invalid(String text) {
            assertThat(tasks.isValidIsoDate(text)).isFalse();
        }

        @Test
        @DisplayName("null → false, never throws")
        void nullInput() {
            assertThat(tasks.isValidIsoDate(null)).isFalse();
        }
    }

    // ── Task 4: formatInstantUtc ──────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement formatInstantUtc()")
    @DisplayName("Task 4 — formatInstantUtc()")
    class FormatInstantUtcTest {

        @Test
        @DisplayName("yyyy-MM-dd HH:mm")
        void basic() {
            assertThat(tasks.formatInstantUtc(Instant.parse("2023-11-14T22:13:20Z"), "yyyy-MM-dd HH:mm"))
                    .isEqualTo("2023-11-14 22:13");
        }

        @Test
        @DisplayName("dd.MM.yyyy HH:mm:ss")
        void european() {
            assertThat(tasks.formatInstantUtc(Instant.parse("2024-03-15T10:15:30Z"), "dd.MM.yyyy HH:mm:ss"))
                    .isEqualTo("15.03.2024 10:15:30");
        }

        @Test
        @DisplayName("compact yyyyMMdd")
        void compact() {
            assertThat(tasks.formatInstantUtc(Instant.parse("2024-03-15T23:59:59Z"), "yyyyMMdd"))
                    .isEqualTo("20240315");
        }

        @Test
        @DisplayName("epoch with quoted literal T")
        void epoch() {
            assertThat(tasks.formatInstantUtc(Instant.EPOCH, "yyyy-MM-dd'T'HH:mm")).isEqualTo("1970-01-01T00:00");
        }

        @Test
        @DisplayName("late-evening UTC stays on the UTC date")
        void utcDateNotLocal() {
            assertThat(tasks.formatInstantUtc(Instant.parse("2024-03-15T23:30:00Z"), "yyyy-MM-dd"))
                    .isEqualTo("2024-03-15");
        }
    }

    // ── Task 5: longDateEnglish ───────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement longDateEnglish()")
    @DisplayName("Task 5 — longDateEnglish()")
    class LongDateEnglishTest {

        @ParameterizedTest(name = "{0} → {1}")
        @CsvSource(delimiter = '|', value = {
                "2024-03-15 | Friday, March 15, 2024",
                "2024-03-02 | Saturday, March 2, 2024",
                "2024-12-25 | Wednesday, December 25, 2024",
                "2000-01-01 | Saturday, January 1, 2000"
        })
        @DisplayName("English long dates")
        void longDates(String iso, String expected) {
            assertThat(tasks.longDateEnglish(LocalDate.parse(iso))).isEqualTo(expected);
        }

        @Test
        @DisplayName("the day of month has no zero padding")
        void noPadding() {
            assertThat(tasks.longDateEnglish(LocalDate.of(2024, 7, 4))).isEqualTo("Thursday, July 4, 2024");
        }
    }

    // ── Task 6: parseOffsetDateTime ───────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement parseOffsetDateTime()")
    @DisplayName("Task 6 — parseOffsetDateTime()")
    class ParseOffsetDateTimeTest {

        @ParameterizedTest(name = "{0} → {1}")
        @CsvSource({
                "2024-03-15T10:15:30+02:00, 2024-03-15T08:15:30Z",
                "2024-03-15T10:15:30Z,      2024-03-15T10:15:30Z",
                "2024-03-15T10:15:30-05:00, 2024-03-15T15:15:30Z",
                "2024-03-15T00:30:00+01:00, 2024-03-14T23:30:00Z"
        })
        @DisplayName("offsets are applied to get the UTC instant")
        void offsets(String text, String expected) {
            assertThat(tasks.parseOffsetDateTime(text)).isEqualTo(Instant.parse(expected));
        }

        @Test
        @DisplayName("fractional seconds are kept")
        void fraction() {
            assertThat(tasks.parseOffsetDateTime("2024-03-15T10:15:30.250+01:00"))
                    .isEqualTo(Instant.parse("2024-03-15T09:15:30.250Z"));
        }

        @ParameterizedTest(name = "\"{0}\" is rejected")
        @ValueSource(strings = {"2024-03-15 10:15:30", "2024-03-15T10:15:30", "2024-03-15", "garbage"})
        @DisplayName("text without a valid ISO offset date-time is rejected")
        void rejected(String text) {
            assertThatThrownBy(() -> tasks.parseOffsetDateTime(text)).isInstanceOf(DateTimeParseException.class);
        }
    }
}
