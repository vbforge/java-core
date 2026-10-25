package com.vbforge.date_time_api;

import java.time.Instant;
import java.time.LocalDate;

/**
 * FormattingParsingTasks — implement every method below to make {@code FormattingParsingTasksTest} go GREEN.
 *
 * <p><b>Workflow:</b>
 * <ol>
 *   <li>Read {@code FormattingParsingDemo.java} to understand the relevant concept.</li>
 *   <li>Implement the method — remove the {@code UnsupportedOperationException}.</li>
 *   <li>Run {@code mvn test -Dtest=FormattingParsingTasksTest} and watch it turn GREEN ✅</li>
 * </ol>
 *
 * <p>Each method has a Javadoc describing exactly what to implement. Do not change
 * method signatures or add external libraries — only the Java standard library is needed.
 */
public class FormattingParsingTasks {

    // ── Task 1 ─────────────────────────────────────────────────────────────────

    /**
     * Formats a date the European way: two-digit day, two-digit month, four-digit year,
     * separated by dots.
     *
     * <p>Examples:
     * <pre>
     *   toEuropeanDate(2024-03-15)  → "15.03.2024"
     *   toEuropeanDate(2024-03-05)  → "05.03.2024"   (zero-padded)
     *   toEuropeanDate(2024-12-31)  → "31.12.2024"
     *   toEuropeanDate(2000-01-01)  → "01.01.2000"
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@link java.time.format.DateTimeFormatter#ofPattern(String)} — mind the letter case:
     *       month is upper-case {@code M}, year is {@code y}.</li>
     * </ul>
     *
     * @param date the date to format
     * @return text in the form {@code dd.MM.yyyy}
     */
    public String toEuropeanDate(LocalDate date) {
        throw new UnsupportedOperationException("TODO: implement toEuropeanDate");
    }

    // ── Task 2 ─────────────────────────────────────────────────────────────────

    /**
     * Parses a US-style date in the exact form {@code MM/dd/yyyy} (both month and day
     * zero-padded). Dates that do not exist on the calendar must be <strong>rejected</strong>,
     * not silently corrected.
     *
     * <p>Examples:
     * <pre>
     *   parseUsDate("03/15/2024")  → 2024-03-15
     *   parseUsDate("02/29/2024")  → 2024-02-29            (2024 is a leap year)
     *   parseUsDate("02/30/2024")  → throws DateTimeParseException
     *   parseUsDate("02/29/2023")  → throws DateTimeParseException
     *   parseUsDate("13/01/2024")  → throws DateTimeParseException   (no month 13)
     *   parseUsDate("3/5/2024")    → throws DateTimeParseException   (not zero-padded)
     *   parseUsDate("garbage")     → throws DateTimeParseException
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>The default resolver style of {@code ofPattern} is forgiving — see
     *       {@link java.time.format.ResolverStyle} and the "Resolver styles" section of the demo.</li>
     *   <li>In the strict style the year letter has to be one that does not need an era.</li>
     * </ul>
     *
     * @param text the text to parse
     * @return the parsed date
     * @throws java.time.format.DateTimeParseException if the text is not a valid {@code MM/dd/yyyy} date
     */
    public LocalDate parseUsDate(String text) {
        throw new UnsupportedOperationException("TODO: implement parseUsDate");
    }

    // ── Task 3 ─────────────────────────────────────────────────────────────────

    /**
     * Checks whether {@code text} is a real calendar date in ISO format {@code yyyy-MM-dd}
     * (zero-padded). Never throws: invalid input, including {@code null}, gives {@code false}.
     *
     * <p>Examples:
     * <pre>
     *   isValidIsoDate("2024-03-15")  → true
     *   isValidIsoDate("2024-02-29")  → true
     *   isValidIsoDate("2023-02-29")  → false   (not a leap year)
     *   isValidIsoDate("2024-02-30")  → false
     *   isValidIsoDate("2024-3-5")    → false   (not zero-padded)
     *   isValidIsoDate("15.03.2024")  → false
     *   isValidIsoDate("")            → false
     *   isValidIsoDate(null)          → false
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code LocalDate.parse(text)} without a formatter is already strict ISO.</li>
     *   <li>Which exception does a failed parse throw? And what does {@code parse(null)} throw?</li>
     * </ul>
     *
     * @param text any string, possibly {@code null}
     * @return {@code true} only for a valid ISO calendar date
     */
    public boolean isValidIsoDate(String text) {
        throw new UnsupportedOperationException("TODO: implement isValidIsoDate");
    }

    // ── Task 4 ─────────────────────────────────────────────────────────────────

    /**
     * Prints {@code instant} as seen on a UTC wall clock using the given
     * {@link java.time.format.DateTimeFormatter} {@code pattern}. Only purely numeric
     * patterns are used, so no locale is involved.
     *
     * <p>Examples:
     * <pre>
     *   formatInstantUtc(2023-11-14T22:13:20Z, "yyyy-MM-dd HH:mm")      → "2023-11-14 22:13"
     *   formatInstantUtc(2024-03-15T10:15:30Z, "dd.MM.yyyy HH:mm:ss")   → "15.03.2024 10:15:30"
     *   formatInstantUtc(2024-03-15T23:59:59Z, "yyyyMMdd")              → "20240315"
     *   formatInstantUtc(1970-01-01T00:00:00Z, "yyyy-MM-dd'T'HH:mm")    → "1970-01-01T00:00"
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Formatting an Instant with a pattern fails unless the formatter knows a zone —
     *       see the "Instant &amp; zone formatting" section of the demo.</li>
     *   <li>Use the UTC offset constant from {@link java.time.ZoneOffset}; do not use the
     *       system default zone.</li>
     * </ul>
     *
     * @param instant the moment to print
     * @param pattern a valid numeric {@code DateTimeFormatter} pattern
     * @return the formatted text in UTC
     */
    public String formatInstantUtc(Instant instant, String pattern) {
        throw new UnsupportedOperationException("TODO: implement formatInstantUtc");
    }

    // ── Task 5 ─────────────────────────────────────────────────────────────────

    /**
     * Formats a date as an English long date: full weekday name, full month name, day of
     * month without zero padding, four-digit year — {@code Weekday, Month d, yyyy}.
     * The result must be the same on every machine, whatever its default language.
     *
     * <p>Examples:
     * <pre>
     *   longDateEnglish(2024-03-15)  → "Friday, March 15, 2024"
     *   longDateEnglish(2024-03-02)  → "Saturday, March 2, 2024"
     *   longDateEnglish(2024-12-25)  → "Wednesday, December 25, 2024"
     *   longDateEnglish(2000-01-01)  → "Saturday, January 1, 2000"
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Four {@code E}s and four {@code M}s print the full names.</li>
     *   <li>Names depend on the locale — the formatter has to be told which one.</li>
     * </ul>
     *
     * @param date the date to format
     * @return the English long date text
     */
    public String longDateEnglish(LocalDate date) {
        throw new UnsupportedOperationException("TODO: implement longDateEnglish");
    }

    // ── Task 6 ─────────────────────────────────────────────────────────────────

    /**
     * Parses ISO-8601 text that carries a UTC offset (for example {@code 2024-03-15T10:15:30+02:00},
     * or {@code ...Z} for UTC) and returns the point on the timeline it describes.
     *
     * <p>Examples:
     * <pre>
     *   parseOffsetDateTime("2024-03-15T10:15:30+02:00")  → 2024-03-15T08:15:30Z
     *   parseOffsetDateTime("2024-03-15T10:15:30Z")       → 2024-03-15T10:15:30Z
     *   parseOffsetDateTime("2024-03-15T10:15:30-05:00")  → 2024-03-15T15:15:30Z
     *   parseOffsetDateTime("2024-03-15T00:30:00+01:00")  → 2024-03-14T23:30:00Z   (previous UTC day)
     *   parseOffsetDateTime("2024-03-15 10:15:30")        → throws DateTimeParseException
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Which java.time type represents "local date-time plus fixed offset"?
     *       It has its own {@code parse} method and a conversion to an {@link Instant}.</li>
     * </ul>
     *
     * @param text ISO-8601 date-time text with an offset
     * @return the corresponding {@link Instant}
     * @throws java.time.format.DateTimeParseException if the text is not valid
     */
    public Instant parseOffsetDateTime(String text) {
        throw new UnsupportedOperationException("TODO: implement parseOffsetDateTime");
    }
}
