package com.vbforge.date_time_api;

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

/**
 * InstantDemo — annotated reference for {@link Instant}, the "machine" time type,
 * and for UTC timestamps in general.
 *
 * <p>Read top-to-bottom. Each section covers one idea with concrete examples and a note
 * explaining <em>why</em> the behaviour is what it is.
 *
 * <p>Topics:
 * <ol>
 *   <li>Instant basics — creating, reading and shifting a point on the timeline</li>
 *   <li>Epoch numbers &amp; precision — seconds, millis, nanos, negative values</li>
 *   <li>Unsupported units — why an Instant cannot "add one month"</li>
 *   <li>Clock — making "now" injectable and tests deterministic</li>
 * </ol>
 */
public class InstantDemo {

    public static void main(String[] args) {
        instantBasicsDemo();
        System.out.println("---");
        epochAndPrecisionDemo();
        System.out.println("---");
        unsupportedUnitsDemo();
        System.out.println("---");
        clockDemo();
    }

    // ── 1. Instant basics ─────────────────────────────────────────────────────

    /**
     * An {@link Instant} is one point on the UTC timeline, stored as seconds + nanoseconds
     * since the epoch {@code 1970-01-01T00:00:00Z}.
     *
     * <p>WHY this matters: it is the right type for <em>when something happened</em> —
     * log entries, database timestamps, token expiry. It has no zone, no calendar, no
     * "wall clock": two machines in different countries describe the same Instant identically.
     * It replaces {@code java.util.Date} for this purpose.
     *
     * <p>Complexity: O(1) for every operation (two numbers: seconds and nanos).
     *
     * <p>Gotcha: {@code toString()} always prints in UTC and ends with {@code Z} ("Zulu" = UTC).
     * That is NOT the time on the wall in your city.
     */
    static void instantBasicsDemo() {
        System.out.println("=== Instant basics ===");

        Instant epoch = Instant.EPOCH;                                   // 1970-01-01T00:00:00Z
        Instant fromSeconds = Instant.ofEpochSecond(1_700_000_000L);     // seconds since epoch
        Instant fromMillis = Instant.ofEpochMilli(1_700_000_000_123L);   // millis since epoch
        Instant parsed = Instant.parse("2024-03-15T10:15:30Z");          // ISO-8601, must end in Z

        System.out.println("EPOCH          : " + epoch);        // 1970-01-01T00:00:00Z
        System.out.println("ofEpochSecond  : " + fromSeconds);  // 2023-11-14T22:13:20Z
        System.out.println("ofEpochMilli   : " + fromMillis);   // 2023-11-14T22:13:20.123Z
        System.out.println("parse          : " + parsed);       // 2024-03-15T10:15:30Z

        // Reading the numbers back
        System.out.println("getEpochSecond : " + parsed.getEpochSecond());  // 1710497730
        System.out.println("toEpochMilli   : " + parsed.toEpochMilli());    // 1710497730000
        System.out.println("getNano        : " + parsed.getNano());         // 0

        // Shifting — immutable: every call returns a new Instant
        System.out.println("plusSeconds(90): " + parsed.plusSeconds(90));            // 2024-03-15T10:17:00Z
        System.out.println("plusMillis(500): " + parsed.plusMillis(500));            // 2024-03-15T10:15:30.500Z
        System.out.println("minus(2h)      : " + parsed.minus(Duration.ofHours(2))); // 2024-03-15T08:15:30Z

        // Ordering
        Instant later = parsed.plusSeconds(1);
        System.out.println("isBefore       : " + parsed.isBefore(later));   // true
        System.out.println("isAfter        : " + parsed.isAfter(later));    // false
    }

    // ── 2. Epoch numbers & precision ──────────────────────────────────────────

    /**
     * Epoch numbers are how Instants travel through databases, JSON and other languages.
     *
     * <p>WHY this matters: you must know which unit a number is in. Seconds (Unix time, JWT
     * {@code exp}) and milliseconds ({@code System.currentTimeMillis()}, JavaScript
     * {@code Date.now()}) differ by a factor of 1000 — mixing them gives dates in 1970 or in the year 55000.
     *
     * <p>Precision: an Instant holds nanoseconds, a {@code long} of millis does not —
     * {@code toEpochMilli()} truncates. Times before 1970 are simply negative numbers.
     *
     * <p>Gotcha: {@code toString()} prints the fraction only when it is non-zero, in groups
     * of 3 digits (millis, micros or nanos).
     */
    static void epochAndPrecisionDemo() {
        System.out.println("=== Epoch numbers & precision ===");

        // Before the epoch — negative numbers
        System.out.println("ofEpochSecond(-1) : " + Instant.ofEpochSecond(-1));   // 1969-12-31T23:59:59Z
        System.out.println("ofEpochMilli(-1)  : " + Instant.ofEpochMilli(-1));    // 1969-12-31T23:59:59.999Z

        // Seconds + nano adjustment
        Instant halfSecond = Instant.ofEpochSecond(0, 500_000_000L);
        System.out.println("0s + 500ms        : " + halfSecond);                   // 1970-01-01T00:00:00.500Z

        // Nanosecond precision survives inside the Instant ...
        Instant nanos = Instant.ofEpochSecond(0, 123_456_789L);
        System.out.println("nanos             : " + nanos);                        // 1970-01-01T00:00:00.123456789Z
        System.out.println("getNano           : " + nanos.getNano());              // 123456789
        // ... but is lost when squeezed into a long of millis
        System.out.println("toEpochMilli      : " + nanos.toEpochMilli());         // 123

        // Truncation — drop the part smaller than the unit
        Instant precise = Instant.parse("2024-03-15T10:15:30.987654321Z");
        System.out.println("truncated(SECONDS): " + precise.truncatedTo(ChronoUnit.SECONDS)); // 2024-03-15T10:15:30Z
        System.out.println("truncated(MINUTES): " + precise.truncatedTo(ChronoUnit.MINUTES)); // 2024-03-15T10:15:00Z

        // Seconds vs millis mix-up: the same number means very different moments
        long n = 1_700_000_000L;
        System.out.println("as seconds        : " + Instant.ofEpochSecond(n));     // 2023-11-14T22:13:20Z
        System.out.println("as millis (bug!)  : " + Instant.ofEpochMilli(n));      // 1970-01-20T16:13:20Z
    }

    // ── 3. Unsupported units ──────────────────────────────────────────────────

    /**
     * An Instant only understands <em>exact</em> durations: nanos up to {@code DAYS}
     * (where a day is exactly 86 400 seconds). It has no idea what a month, a year or a
     * calendar day-of-month is, so asking for them throws
     * {@link UnsupportedTemporalTypeException}.
     *
     * <p>WHY this matters: "one month later" is not a fixed number of seconds (28–31 days),
     * and "one day later" is not always 24 hours in a real zone (DST). Those questions need
     * a calendar and a zone. The fix is explicit: put the Instant into a
     * {@code ZonedDateTime} (here UTC), do calendar maths there, convert back.
     */
    static void unsupportedUnitsDemo() {
        System.out.println("=== Unsupported units ===");

        Instant instant = Instant.parse("2024-01-31T10:00:00Z");

        // DAYS is supported — and means exactly 24 hours
        System.out.println("plus(1, DAYS)   : " + instant.plus(1, ChronoUnit.DAYS));    // 2024-02-01T10:00:00Z

        // MONTHS is not
        try {
            instant.plus(1, ChronoUnit.MONTHS);
        } catch (UnsupportedTemporalTypeException e) {
            System.out.println("plus(1, MONTHS): UnsupportedTemporalTypeException");
        }

        // Neither are calendar fields such as YEAR
        try {
            instant.get(ChronoField.YEAR);
        } catch (UnsupportedTemporalTypeException e) {
            System.out.println("get(YEAR)      : UnsupportedTemporalTypeException");
        }

        // The correct route for calendar maths: go through a zone (UTC here) and back
        Instant nextMonth = instant.atZone(ZoneOffset.UTC).plusMonths(1).toInstant();
        System.out.println("via UTC +1 month: " + nextMonth);                           // 2024-02-29T10:00:00Z
    }

    // ── 4. Clock ──────────────────────────────────────────────────────────────

    /**
     * {@link Clock} is a source of "now" that you can pass around.
     *
     * <p>WHY this matters: {@code Instant.now()} / {@code LocalDate.now()} are hidden global
     * inputs — code that calls them cannot be tested deterministically ("is this token
     * expired?" depends on when the test runs). Inject a {@code Clock}; production uses
     * {@code Clock.systemUTC()}, tests use {@code Clock.fixed(...)}.
     *
     * <p>Gotcha: a Clock also carries a {@link ZoneId}. The Instant is the same everywhere,
     * but {@code LocalDate.now(clock)} reads the calendar in the clock's zone, so the date can differ.
     */
    static void clockDemo() {
        System.out.println("=== Clock ===");

        Instant moment = Instant.parse("2024-03-15T10:15:30Z");
        Clock fixed = Clock.fixed(moment, ZoneOffset.UTC);

        // Every "now" derived from the fixed clock is the same value, forever
        System.out.println("Instant.now(fixed)       : " + Instant.now(fixed));            // 2024-03-15T10:15:30Z
        System.out.println("LocalDate.now(fixed)     : " + LocalDate.now(fixed));          // 2024-03-15
        System.out.println("LocalDateTime.now(fixed) : " + LocalDateTime.now(fixed));      // 2024-03-15T10:15:30

        // Shifted clock — "two hours from now" without sleeping
        Clock later = Clock.offset(fixed, Duration.ofHours(2));
        System.out.println("offset(+2h)              : " + later.instant());               // 2024-03-15T12:15:30Z

        // Same Instant, different zone => possibly a different calendar date
        Instant lateEvening = Instant.parse("2024-03-15T23:30:00Z");
        Clock tokyo = Clock.fixed(lateEvening, ZoneId.of("Asia/Tokyo"));
        System.out.println("LocalDate.now(Tokyo)     : " + LocalDate.now(tokyo));          // 2024-03-16 (UTC+9)

        // The real clock is always "after" a fixed moment in the past
        System.out.println("systemUTC after 2024     : " + Clock.systemUTC().instant().isAfter(moment)); // true
    }
}
