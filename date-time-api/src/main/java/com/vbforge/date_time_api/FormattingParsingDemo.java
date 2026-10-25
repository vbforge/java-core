package com.vbforge.date_time_api;

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

/**
 * FormattingParsingDemo — annotated reference for turning date-time values into text
 * ({@code format}) and text back into values ({@code parse}) with {@link DateTimeFormatter}.
 *
 * <p>Read top-to-bottom. Each section covers one idea with concrete examples and a note
 * explaining <em>why</em> the behaviour is what it is.
 *
 * <p>Every pattern that prints words (month names, AM/PM) is given an explicit {@link Locale},
 * so the output does not depend on the machine running the demo.
 *
 * <p>Topics:
 * <ol>
 *   <li>ISO-8601 formatters — the built-in standard formats</li>
 *   <li>Pattern formatting — {@code ofPattern}, the letters that matter, and the traps</li>
 *   <li>Parsing — text to value, and what {@link DateTimeParseException} tells you</li>
 *   <li>Resolver styles — why {@code 2024-02-31} can silently become {@code 2024-02-29}</li>
 *   <li>Formatting an Instant and zones — why an Instant needs a zone to be printed</li>
 *   <li>Immutable formatters and locale — safe to share, {@code withLocale} returns a copy</li>
 * </ol>
 */
public class FormattingParsingDemo {

    public static void main(String[] args) {
        isoFormattersDemo();
        System.out.println("---");
        patternFormattingDemo();
        System.out.println("---");
        parsingDemo();
        System.out.println("---");
        resolverStyleDemo();
        System.out.println("---");
        instantAndZoneFormattingDemo();
        System.out.println("---");
        formatterReuseDemo();
    }

    // ── 1. ISO formatters ─────────────────────────────────────────────────────

    /**
     * ISO-8601 is the international standard for writing dates and times
     * ({@code 2024-03-15T10:15:30}). java.time ships a constant formatter for each shape.
     *
     * <p>WHY this matters: ISO text sorts alphabetically in chronological order, is
     * unambiguous (is {@code 03/04/2024} March 4th or April 3rd?) and is what JSON APIs, databases
     * and logs should exchange. Reserve custom patterns for what humans read.
     *
     * <p>Gotcha: {@code toString()} drops zero seconds ({@code 10:15}), while the ISO formatter
     * always prints them ({@code 10:15:00}). Both are valid ISO, but they are not the same string.
     */
    static void isoFormattersDemo() {
        System.out.println("=== ISO formatters ===");

        LocalDate date = LocalDate.of(2024, 3, 15);
        LocalDateTime dateTime = LocalDateTime.of(2024, 3, 15, 10, 15, 30);
        ZonedDateTime paris = ZonedDateTime.of(dateTime, ZoneId.of("Europe/Paris"));
        Instant instant = Instant.parse("2024-03-15T10:15:30Z");

        System.out.println("ISO_LOCAL_DATE       : " + DateTimeFormatter.ISO_LOCAL_DATE.format(date));          // 2024-03-15
        System.out.println("ISO_LOCAL_DATE_TIME  : " + DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(dateTime));  // 2024-03-15T10:15:30
        System.out.println("ISO_OFFSET_DATE_TIME : " + DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(paris));    // 2024-03-15T10:15:30+01:00
        System.out.println("ISO_ZONED_DATE_TIME  : " + DateTimeFormatter.ISO_ZONED_DATE_TIME.format(paris));     // 2024-03-15T10:15:30+01:00[Europe/Paris]
        System.out.println("ISO_INSTANT          : " + DateTimeFormatter.ISO_INSTANT.format(instant));           // 2024-03-15T10:15:30Z
        System.out.println("BASIC_ISO_DATE       : " + DateTimeFormatter.BASIC_ISO_DATE.format(date));           // 20240315

        // toString() of the java.time types already IS the ISO format ...
        System.out.println("date.toString()      : " + date);                                                    // 2024-03-15

        // ... except that zero seconds are omitted
        LocalDateTime noSeconds = LocalDateTime.of(2024, 3, 15, 10, 15);
        System.out.println("toString, 0 seconds  : " + noSeconds);                                              // 2024-03-15T10:15
        System.out.println("ISO formatter        : " + DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(noSeconds)); // 2024-03-15T10:15:00
    }

    // ── 2. Pattern formatting ─────────────────────────────────────────────────

    /**
     * {@code DateTimeFormatter.ofPattern(...)} builds a formatter from a pattern string.
     *
     * <p>Letters you need:
     * <ul>
     *   <li>{@code yyyy} year-of-era, {@code uuuu} year, {@code MM} month number, {@code MMM}/{@code MMMM} month name</li>
     *   <li>{@code dd} day of month, {@code EEE}/{@code EEEE} day-of-week name</li>
     *   <li>{@code HH} hour 00–23, {@code hh} hour 01–12 (pair with {@code a} for AM/PM)</li>
     *   <li>{@code mm} minute, {@code ss} second, {@code SSS} millisecond</li>
     *   <li>{@code 'T'} — text in single quotes is printed literally</li>
     * </ul>
     *
     * <p>WHY this matters: patterns are case-sensitive and a wrong letter case compiles fine
     * but prints wrong data. Classic traps: {@code mm} (minutes) vs {@code MM} (month),
     * {@code hh} (12-hour) vs {@code HH} (24-hour), and {@code YYYY} (week-based year) vs
     * {@code yyyy} (calendar year).
     */
    static void patternFormattingDemo() {
        System.out.println("=== Pattern formatting ===");

        LocalDateTime dt = LocalDateTime.of(2024, 3, 15, 14, 5, 9);

        DateTimeFormatter european = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        DateTimeFormatter american = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        DateTimeFormatter clock24 = DateTimeFormatter.ofPattern("HH:mm:ss");
        DateTimeFormatter clock12 = DateTimeFormatter.ofPattern("hh:mm a", Locale.US);
        DateTimeFormatter longDate = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy", Locale.US);
        DateTimeFormatter withLiteral = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

        System.out.println("dd.MM.yyyy           : " + european.format(dt));      // 15.03.2024
        System.out.println("MM/dd/yyyy           : " + american.format(dt));      // 03/15/2024
        System.out.println("HH:mm:ss             : " + clock24.format(dt));       // 14:05:09
        System.out.println("hh:mm a              : " + clock12.format(dt));       // 02:05 PM
        System.out.println("EEEE, MMMM d, yyyy   : " + longDate.format(dt));      // Friday, March 15, 2024
        System.out.println("quoted literal 'T'   : " + withLiteral.format(dt));   // 2024-03-15T14:05

        // Shortcut: both the value and the formatter can start the call
        System.out.println("dt.format(f)         : " + dt.format(european));      // 15.03.2024

        // GOTCHA — mm is minutes, MM is month
        System.out.println("mm vs MM             : " + DateTimeFormatter.ofPattern("mm").format(dt)
                + " vs " + DateTimeFormatter.ofPattern("MM").format(dt));        // 05 vs 03

        // GOTCHA — YYYY is the WEEK-based year: around New Year it can be off by one
        LocalDate dec30 = LocalDate.of(2024, 12, 30);
        System.out.println("yyyy                 : " + DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.US).format(dec30)); // 2024-12-30
        System.out.println("YYYY (bug!)          : " + DateTimeFormatter.ofPattern("YYYY-MM-dd", Locale.US).format(dec30)); // 2025-12-30
    }

    // ── 3. Parsing ────────────────────────────────────────────────────────────

    /**
     * Parsing is the reverse of formatting: {@code Type.parse(text)} for ISO text or
     * {@code Type.parse(text, formatter)} for custom text.
     *
     * <p>WHY this matters: input from users, files and APIs is text. Bad text must be
     * rejected with a clear error, and java.time does that with an unchecked
     * {@link DateTimeParseException} that knows the offending text and the position of the problem.
     *
     * <p>Gotchas:
     * <ul>
     *   <li>Parsing must use a pattern that matches the whole text — leftover characters are an error.</li>
     *   <li>{@code dd} insists on two digits; a single {@code d} accepts one or two.</li>
     *   <li>You parse into the type that carries <em>exactly</em> the information in the text:
     *       a date-only string cannot become a LocalDateTime.</li>
     * </ul>
     */
    static void parsingDemo() {
        System.out.println("=== Parsing ===");

        System.out.println("LocalDate.parse      : " + LocalDate.parse("2024-03-15"));                // 2024-03-15
        System.out.println("LocalTime.parse      : " + LocalTime.parse("10:15"));                     // 10:15
        System.out.println("LocalDateTime.parse  : " + LocalDateTime.parse("2024-03-15T10:15:30"));   // 2024-03-15T10:15:30

        DateTimeFormatter european = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        System.out.println("custom pattern       : " + LocalDate.parse("15.03.2024", european));      // 2024-03-15

        // d / M accept one OR two digits
        DateTimeFormatter flexible = DateTimeFormatter.ofPattern("d.M.yyyy");
        System.out.println("flexible '5.3.2024'  : " + LocalDate.parse("5.3.2024", flexible));        // 2024-03-05

        // A failure carries diagnostics
        try {
            LocalDate.parse("2024-3-5");                          // ISO needs zero-padded month and day
        } catch (DateTimeParseException e) {
            System.out.println("bad text            : '" + e.getParsedString() + "' failed at index " + e.getErrorIndex()); // '2024-3-5' failed at index 5
        }

        // Valid shape, impossible value — also rejected by the ISO parser
        try {
            LocalDate.parse("2023-02-29");
        } catch (DateTimeParseException e) {
            System.out.println("2023-02-29           : DateTimeParseException");
        }

        // Text with a time cannot be parsed as a date-only type
        try {
            LocalDate.parse("2024-03-15T10:15:30");
        } catch (DateTimeParseException e) {
            System.out.println("date from date-time  : DateTimeParseException");
        }
    }

    // ── 4. Resolver styles ────────────────────────────────────────────────────

    /**
     * After the text is split into fields, the formatter <em>resolves</em> them into a date.
     * {@link ResolverStyle} controls how forgiving that step is:
     * <ul>
     *   <li>{@code SMART} (default for {@code ofPattern}) — day 29–31 beyond the month's end is clamped to the last valid day</li>
     *   <li>{@code STRICT} — every field must be exactly valid</li>
     *   <li>{@code LENIENT} — out-of-range values roll over ({@code 2024-02-31} → {@code 2024-03-02})</li>
     * </ul>
     *
     * <p>WHY this matters: with the default style a typo like {@code 2024-02-31} is accepted
     * and silently becomes {@code 2024-02-29} — corrupt data without any exception.
     * {@code LocalDate.parse(text)} without a formatter is strict (it uses {@code ISO_LOCAL_DATE}).
     *
     * <p>Gotcha: in {@code STRICT} mode use {@code uuuu} (year) instead of {@code yyyy}
     * (year-of-era) — year-of-era alone cannot be resolved without an era (AD/BC).
     */
    static void resolverStyleDemo() {
        System.out.println("=== Resolver styles ===");

        DateTimeFormatter smart = DateTimeFormatter.ofPattern("uuuu-MM-dd");   // SMART is the default
        DateTimeFormatter strict = DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT);
        DateTimeFormatter lenient = DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.LENIENT);

        System.out.println("SMART   2024-02-31   : " + LocalDate.parse("2024-02-31", smart));    // 2024-02-29  (silent clamp!)
        System.out.println("SMART   2023-02-30   : " + LocalDate.parse("2023-02-30", smart));    // 2023-02-28
        System.out.println("LENIENT 2024-02-31   : " + LocalDate.parse("2024-02-31", lenient));  // 2024-03-02  (rolls over)

        try {
            LocalDate.parse("2024-02-31", strict);
        } catch (DateTimeParseException e) {
            System.out.println("STRICT  2024-02-31   : DateTimeParseException");
        }

        // Day 32 is out of range in every mode except LENIENT
        try {
            LocalDate.parse("2024-01-32", smart);
        } catch (DateTimeParseException e) {
            System.out.println("SMART   2024-01-32   : DateTimeParseException");
        }

        // ISO_LOCAL_DATE is STRICT
        try {
            LocalDate.parse("2024-02-31");
        } catch (DateTimeParseException e) {
            System.out.println("ISO     2024-02-31   : DateTimeParseException");
        }

        // STRICT + yyyy (year-of-era) cannot be resolved
        DateTimeFormatter strictWithY = DateTimeFormatter.ofPattern("yyyy-MM-dd").withResolverStyle(ResolverStyle.STRICT);
        try {
            LocalDate.parse("2024-03-15", strictWithY);
        } catch (DateTimeParseException e) {
            System.out.println("STRICT  yyyy         : DateTimeParseException (use uuuu)");
        }
    }

    // ── 5. Instant and zone formatting ────────────────────────────────────────

    /**
     * An {@link Instant} knows only seconds and nanos, so a pattern with year, month or hour
     * has nothing to read from it and throws {@link UnsupportedTemporalTypeException}.
     * Give the formatter a zone with {@code withZone(...)} and it can convert first.
     *
     * <p>WHY this matters: "print this timestamp for the user" always means "in <em>which</em>
     * zone?" — the API makes you say it explicitly instead of silently using the server's zone.
     *
     * <p>For a ZonedDateTime the zone can be printed too: {@code z} (short name, {@code EDT}),
     * {@code VV} (id, {@code America/New_York}), {@code XXX} (offset, {@code -04:00}).
     */
    static void instantAndZoneFormattingDemo() {
        System.out.println("=== Instant & zone formatting ===");

        Instant instant = Instant.parse("2024-03-15T10:15:30Z");
        DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        try {
            pattern.format(instant);                              // no zone => no year/hour fields
        } catch (UnsupportedTemporalTypeException e) {
            System.out.println("pattern.format(Instant) : UnsupportedTemporalTypeException");
        }

        System.out.println("withZone(UTC)          : " + pattern.withZone(ZoneOffset.UTC).format(instant));          // 2024-03-15 10:15
        System.out.println("withZone(Tokyo)        : " + pattern.withZone(ZoneId.of("Asia/Tokyo")).format(instant));  // 2024-03-15 19:15
        System.out.println("ISO_INSTANT            : " + DateTimeFormatter.ISO_INSTANT.format(instant));              // 2024-03-15T10:15:30Z (needs no zone)

        // Zone details on a ZonedDateTime
        ZonedDateTime newYork = instant.atZone(ZoneId.of("America/New_York"));
        System.out.println("z (short name)         : " + DateTimeFormatter.ofPattern("HH:mm z", Locale.US).format(newYork));   // 06:15 EDT
        System.out.println("VV (zone id)           : " + DateTimeFormatter.ofPattern("HH:mm VV").format(newYork));            // 06:15 America/New_York
        System.out.println("XXX (offset)           : " + DateTimeFormatter.ofPattern("HH:mm XXX").format(newYork));           // 06:15 -04:00

        // Parsing text that carries an offset — go through OffsetDateTime to an Instant
        OffsetDateTime parsed = OffsetDateTime.parse("2024-03-15T10:15:30+02:00");
        System.out.println("offset text -> Instant : " + parsed.toInstant());                                                 // 2024-03-15T08:15:30Z
        System.out.println("zoned text             : " + ZonedDateTime.parse("2024-03-15T10:15:30+01:00[Europe/Paris]"));     // 2024-03-15T10:15:30+01:00[Europe/Paris]
    }

    // ── 6. Formatter reuse and locale ─────────────────────────────────────────

    /**
     * {@link DateTimeFormatter} is <strong>immutable and thread-safe</strong>: declare it once as a
     * {@code private static final} constant and share it. This is the big improvement over the old
     * {@code SimpleDateFormat}, which was mutable and corrupted its output when used from two threads.
     *
     * <p>WHY this matters: building a formatter parses the pattern string, which is wasted work if
     * done on every call. Methods such as {@code withLocale}, {@code withZone} and
     * {@code withResolverStyle} never change the original — they return a new formatter.
     *
     * <p>Gotcha: patterns that print names ({@code MMMM}, {@code EEEE}, {@code a}) use the JVM's
     * <em>default locale</em> unless you set one. The same code prints "March" on one server and
     * "März" on another. Always pass a {@code Locale} for text output that must be stable.
     */
    static void formatterReuseDemo() {
        System.out.println("=== Formatter reuse & locale ===");

        LocalDate date = LocalDate.of(2024, 3, 15);
        DateTimeFormatter base = DateTimeFormatter.ofPattern("EEEE d MMMM yyyy");

        DateTimeFormatter english = base.withLocale(Locale.ENGLISH);
        DateTimeFormatter french = base.withLocale(Locale.FRENCH);

        System.out.println("ENGLISH              : " + english.format(date));    // Friday 15 March 2024
        System.out.println("FRENCH               : " + french.format(date));     // vendredi 15 mars 2024
        System.out.println("withLocale new obj   : " + (english != base));       // true  (original untouched)
        System.out.println("locale of copy       : " + french.getLocale());      // fr
    }
}
