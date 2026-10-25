package com.vbforge.date_time_api;

import java.time.DateTimeException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

/**
 * PeriodDurationDemo — annotated reference for the two "amount of time" types,
 * {@link Period} and {@link Duration}, and for calculating differences between date-time values.
 *
 * <p>Read top-to-bottom. Each section covers one idea with concrete examples and a note
 * explaining <em>why</em> the behaviour is what it is.
 *
 * <p>Topics:
 * <ol>
 *   <li>Period — calendar amounts: years, months, days</li>
 *   <li>Duration — exact amounts: seconds and nanoseconds</li>
 *   <li>Period vs Duration — which one to use, and what happens across a DST change</li>
 *   <li>ChronoUnit.between — total whole units between two values</li>
 * </ol>
 */
public class PeriodDurationDemo {

    private static final ZoneId NEW_YORK = ZoneId.of("America/New_York");

    public static void main(String[] args) {
        periodDemo();
        System.out.println("---");
        durationDemo();
        System.out.println("---");
        periodVsDurationDemo();
        System.out.println("---");
        chronoUnitDemo();
    }

    // ── 1. Period ─────────────────────────────────────────────────────────────

    /**
     * {@link Period} is a <em>date-based</em> amount: a number of years, a number of months and
     * a number of days, stored separately. {@code P1Y2M3D} reads "1 year, 2 months, 3 days".
     *
     * <p>WHY this matters: "one month" has no fixed length (28–31 days), so it cannot be turned
     * into seconds. A Period keeps the three parts apart and lets the calendar interpret them
     * when you add it to a date. Use it for ages, subscriptions, "due in 2 weeks".
     *
     * <p>Gotchas:
     * <ul>
     *   <li>{@code getDays()} is only the <em>days component</em> — not the total number of days
     *       (use {@code ChronoUnit.DAYS.between} for that).</li>
     *   <li>{@code P1M} and {@code P30D} are different periods — they are not equal.</li>
     * </ul>
     */
    static void periodDemo() {
        System.out.println("=== Period ===");

        Period p = Period.of(1, 2, 3);
        System.out.println("of(1,2,3)        : " + p);                     // P1Y2M3D
        System.out.println("components       : " + p.getYears() + "y " + p.getMonths() + "m " + p.getDays() + "d"); // 1y 2m 3d
        System.out.println("toTotalMonths    : " + p.toTotalMonths());     // 14  (years*12 + months; days ignored)

        // Factories
        System.out.println("ofWeeks(2)       : " + Period.ofWeeks(2));     // P14D  (weeks are stored as days)
        System.out.println("ofMonths(14)     : " + Period.ofMonths(14));   // P14M  (NOT normalised automatically)
        System.out.println("normalized       : " + Period.ofMonths(14).normalized()); // P1Y2M
        System.out.println("parse            : " + Period.parse("P1Y2M3D").equals(p)); // true
        System.out.println("ZERO             : " + Period.ZERO + " isZero=" + Period.ZERO.isZero()); // P0D isZero=true

        // Arithmetic on the period itself
        System.out.println("negated          : " + p.negated());           // P-1Y-2M-3D
        System.out.println("multipliedBy(2)  : " + p.multipliedBy(2));     // P2Y4M6D

        // Adding a Period to a date — the calendar decides what "a month" means
        LocalDate jan31 = LocalDate.of(2024, 1, 31);
        System.out.println("Jan31 + P1M      : " + jan31.plus(Period.ofMonths(1)));   // 2024-02-29 (clamped)
        System.out.println("Jan31 + P30D     : " + jan31.plus(Period.ofDays(30)));    // 2024-03-01

        // GOTCHA — P1M and P30D are different values even if they sometimes land on the same date
        System.out.println("P1M equals P30D  : " + Period.ofMonths(1).equals(Period.ofDays(30))); // false

        // Period.between — largest units first: years, then months, then days
        LocalDate birth = LocalDate.of(1990, 5, 20);
        LocalDate today = LocalDate.of(2024, 3, 15);
        Period age = Period.between(birth, today);
        System.out.println("age              : " + age);                   // P33Y9M24D
        System.out.println("between Jan31..Mar1: " + Period.between(jan31, LocalDate.of(2024, 3, 1))); // P1M1D

        // GOTCHA — getDays() is NOT the total number of days
        Period p2 = Period.between(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 3, 15));
        System.out.println("P2M14D getDays   : " + p2 + " -> " + p2.getDays());       // P2M14D -> 14
        System.out.println("total days       : " + ChronoUnit.DAYS.between(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 3, 15))); // 74
    }

    // ── 2. Duration ───────────────────────────────────────────────────────────

    /**
     * {@link Duration} is a <em>time-based</em> amount: a signed number of seconds plus
     * nanoseconds. {@code PT1H30M} reads "period of time: 1 hour 30 minutes".
     *
     * <p>WHY this matters: timeouts, elapsed time, TTLs, "how long did this take" — all exact
     * lengths on the timeline. A Duration never depends on a calendar: one day is always
     * exactly 24 hours.
     *
     * <p>Gotchas:
     * <ul>
     *   <li>{@code toMinutes()}, {@code toHours()} truncate: {@code PT119S} is 1 minute.</li>
     *   <li>The "part" methods ({@code toMinutesPart()}, ...) only exist since Java 9 —
     *       on Java 8 compute remainders yourself with {@code %}.</li>
     *   <li>Negative durations print each unit with its sign ({@code PT-2H}); use {@code abs()} if needed.</li>
     * </ul>
     */
    static void durationDemo() {
        System.out.println("=== Duration ===");

        Duration d = Duration.ofMinutes(90);
        System.out.println("ofMinutes(90)    : " + d);                         // PT1H30M
        System.out.println("ofHours(25)      : " + Duration.ofHours(25));      // PT25H  (no days field in toString)
        System.out.println("ofMillis(1500)   : " + Duration.ofMillis(1500));   // PT1.5S
        System.out.println("parse            : " + Duration.parse("PT1H30M").equals(d)); // true

        // Getting numbers out — total amounts, truncated
        Duration d2 = Duration.ofSeconds(3725);
        System.out.println("PT3725S          : " + d2);                        // PT1H2M5S
        System.out.println("getSeconds       : " + d2.getSeconds());           // 3725
        System.out.println("toMinutes        : " + d2.toMinutes());            // 62  (truncated)
        System.out.println("toHours          : " + d2.toHours());              // 1   (truncated)
        System.out.println("toMillis         : " + d2.toMillis());             // 3725000
        System.out.println("remainder secs   : " + (d2.getSeconds() % 60));    // 5   (Java 8 way)

        // Between two moments
        Instant start = Instant.parse("2024-03-15T10:00:00Z");
        Instant end = Instant.parse("2024-03-15T12:30:00Z");
        System.out.println("between instants : " + Duration.between(start, end));        // PT2H30M
        System.out.println("between reversed : " + Duration.between(end, start));        // PT-2H-30M
        System.out.println("abs              : " + Duration.between(end, start).abs());  // PT2H30M

        LocalDateTime a = LocalDateTime.of(2024, 3, 15, 22, 0);
        LocalDateTime b = LocalDateTime.of(2024, 3, 16, 6, 0);
        System.out.println("overnight        : " + Duration.between(a, b));               // PT8H

        // Arithmetic
        System.out.println("plus             : " + d.plus(Duration.ofMinutes(45)));       // PT2H15M
        System.out.println("multipliedBy(3)  : " + d.multipliedBy(3));                    // PT4H30M
        System.out.println("dividedBy(2)     : " + d.dividedBy(2));                       // PT45M
        System.out.println("compareTo        : " + (d.compareTo(Duration.ofHours(2)) < 0)); // true (90 min < 2 h)

        // A "day" is exactly 24 hours
        System.out.println("ofDays(1) == 24h : " + Duration.ofDays(1).equals(Duration.ofHours(24))); // true
    }

    // ── 3. Period vs Duration ─────────────────────────────────────────────────

    /**
     * Rule of thumb: <strong>Period for the calendar, Duration for the stopwatch.</strong>
     *
     * <p>WHY this matters: they answer different questions. "Same time tomorrow" is a Period of
     * one day; "24 hours from now" is a Duration. On most days both give the same result, but
     * across a daylight-saving change they differ by an hour. The types also refuse to be
     * mixed up: a LocalDate has no time of day, so it rejects a Duration.
     */
    static void periodVsDurationDemo() {
        System.out.println("=== Period vs Duration ===");

        // 2024-03-10 is the spring-forward day in New York (23 hours long)
        ZonedDateTime saturdayNoon = ZonedDateTime.of(2024, 3, 9, 12, 0, 0, 0, NEW_YORK);

        System.out.println("start            : " + saturdayNoon);                                // 2024-03-09T12:00-05:00[America/New_York]
        System.out.println("+ Period 1 day   : " + saturdayNoon.plus(Period.ofDays(1)));         // 2024-03-10T12:00-04:00[America/New_York]
        System.out.println("+ Duration 1 day : " + saturdayNoon.plus(Duration.ofDays(1)));       // 2024-03-10T13:00-04:00[America/New_York]

        // Mixing the wrong type fails fast
        try {
            LocalDate.of(2024, 3, 15).plus(Duration.ofHours(1));   // a date has no hours
        } catch (DateTimeException e) {
            System.out.println("date + Duration  : DateTimeException");
        }
        try {
            Duration.between(LocalDate.of(2024, 3, 15), LocalDate.of(2024, 3, 16)); // no time-of-day
        } catch (DateTimeException e) {
            System.out.println("Duration of dates: DateTimeException");
        }

        // Pick by question:
        //   "How old is she?"                -> Period.between(birth, today)
        //   "How long did the request take?" -> Duration.between(startInstant, endInstant)
        //   "Due in 2 weeks"                 -> date.plus(Period.ofWeeks(2))
        //   "Timeout after 30 seconds"       -> Duration.ofSeconds(30)
    }

    // ── 4. ChronoUnit.between ─────────────────────────────────────────────────

    /**
     * {@code ChronoUnit.X.between(a, b)} returns the number of <em>complete</em> units of X
     * from {@code a} (inclusive) to {@code b} (exclusive), as a single {@code long}.
     *
     * <p>WHY this matters: it is the answer to "how many days / hours / months are between
     * these two values?" — one number in one unit — whereas {@code Period.between} splits
     * the answer into years, months and days.
     *
     * <p>Gotchas:
     * <ul>
     *   <li>Whole units only — the remainder is discarded (truncation toward zero).</li>
     *   <li>Argument order matters: swap them and the result is negated.</li>
     *   <li>The unit must make sense for the type: {@code HOURS.between(LocalDate, LocalDate)} throws.</li>
     * </ul>
     */
    static void chronoUnitDemo() {
        System.out.println("=== ChronoUnit.between ===");

        LocalDate jan31 = LocalDate.of(2024, 1, 31);
        LocalDate mar1 = LocalDate.of(2024, 3, 1);

        System.out.println("DAYS             : " + ChronoUnit.DAYS.between(jan31, mar1));     // 30
        System.out.println("WEEKS            : " + ChronoUnit.WEEKS.between(jan31, mar1));    // 4   (30 / 7, truncated)
        System.out.println("MONTHS           : " + ChronoUnit.MONTHS.between(jan31, mar1));   // 1
        System.out.println("reversed DAYS    : " + ChronoUnit.DAYS.between(mar1, jan31));     // -30
        System.out.println("until(DAYS)      : " + jan31.until(mar1, ChronoUnit.DAYS));       // 30  (same thing, other spelling)
        System.out.println("until() -> Period: " + jan31.until(mar1));                        // P1M1D

        // Age in whole years
        System.out.println("YEARS (age)      : " + ChronoUnit.YEARS.between(LocalDate.of(1990, 5, 20), LocalDate.of(2024, 3, 15))); // 33

        // Time-based units on time-based types
        LocalDateTime start = LocalDateTime.of(2024, 3, 15, 10, 0);
        LocalDateTime end = LocalDateTime.of(2024, 3, 15, 15, 59);
        System.out.println("HOURS            : " + ChronoUnit.HOURS.between(start, end));    // 5   (5h59m truncated)
        System.out.println("MINUTES          : " + ChronoUnit.MINUTES.between(start, end));  // 359

        // Wrong unit for the type
        try {
            ChronoUnit.HOURS.between(jan31, mar1);    // LocalDate has no hours
        } catch (DateTimeException e) {
            System.out.println("HOURS on dates   : DateTimeException");
        }
    }
}
