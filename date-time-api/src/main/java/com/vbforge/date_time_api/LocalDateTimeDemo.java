package com.vbforge.date_time_api;

import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;

/**
 * LocalDateTimeDemo — annotated reference for {@link LocalDate}, {@link LocalTime}
 * and {@link LocalDateTime}: the "human" date-time types that have <em>no time zone</em>.
 *
 * <p>Read top-to-bottom. Each section covers one idea with concrete examples and a note
 * explaining <em>why</em> the behaviour is what it is.
 *
 * <p>Topics:
 * <ol>
 *   <li>LocalDate — a calendar date (year-month-day), creation, getters, arithmetic</li>
 *   <li>LocalTime — a wall-clock time of day, wrap-around, truncation</li>
 *   <li>LocalDateTime — date + time glued together, still without a zone</li>
 *   <li>TemporalAdjusters — "first day of next month", "next Monday", ...</li>
 *   <li>Immutability &amp; validation — why discarding the result of {@code plusDays} is a bug</li>
 * </ol>
 */
public class LocalDateTimeDemo {

    public static void main(String[] args) {
        localDateDemo();
        System.out.println("---");
        localTimeDemo();
        System.out.println("---");
        localDateTimeDemo();
        System.out.println("---");
        adjustersDemo();
        System.out.println("---");
        immutabilityDemo();
    }

    // ── 1. LocalDate ──────────────────────────────────────────────────────────

    /**
     * {@link LocalDate} is a date without time and without zone: {@code 2024-03-15}.
     *
     * <p>WHY this matters: birthdays, invoice dates, holidays — values that mean the same
     * thing everywhere on Earth — should never carry a time or zone. It replaces
     * {@code java.util.Date}/{@code Calendar} (mutable, zero-based months, misleading names).
     *
     * <p>Complexity: every operation is O(1) — a LocalDate is just three numbers.
     *
     * <p>Gotcha: month arithmetic clamps to the last valid day of the target month, and the
     * order of chained operations matters ({@code Jan 31 + 1 month + 1 month != Jan 31 + 2 months}).
     */
    static void localDateDemo() {
        System.out.println("=== LocalDate ===");

        // Creation — months are 1-based (1 = January), unlike java.util.Calendar
        LocalDate date = LocalDate.of(2024, 3, 15);
        LocalDate sameDate = LocalDate.of(2024, Month.MARCH, 15);   // enum overload — more readable
        LocalDate parsed = LocalDate.parse("2024-03-15");           // ISO-8601 yyyy-MM-dd
        LocalDate epoch = LocalDate.ofEpochDay(0);                  // 1970-01-01
        LocalDate dayOfYear = LocalDate.ofYearDay(2024, 75);        // 75th day of a leap year

        System.out.println("of(y,m,d)     : " + date);                          // 2024-03-15
        System.out.println("same via enum : " + date.equals(sameDate));          // true
        System.out.println("parsed equals : " + date.equals(parsed));            // true
        System.out.println("epoch day 0   : " + epoch);                          // 1970-01-01
        System.out.println("ofYearDay(75) : " + dayOfYear);                      // 2024-03-15

        // Getters
        System.out.println("year          : " + date.getYear());                 // 2024
        System.out.println("month         : " + date.getMonth());                // MARCH
        System.out.println("monthValue    : " + date.getMonthValue());           // 3
        System.out.println("dayOfMonth    : " + date.getDayOfMonth());           // 15
        System.out.println("dayOfWeek     : " + date.getDayOfWeek());            // FRIDAY
        System.out.println("dayOfYear     : " + date.getDayOfYear());            // 75
        System.out.println("isLeapYear    : " + date.isLeapYear());              // true
        System.out.println("lengthOfMonth : " + date.lengthOfMonth());            // 31
        System.out.println("lengthOfYear  : " + date.lengthOfYear());            // 366

        // Arithmetic — plusX / minusX; every call returns a NEW LocalDate
        System.out.println("plusDays(20)   : " + date.plusDays(20));              // 2024-04-04
        System.out.println("minusWeeks(2)  : " + date.minusWeeks(2));             // 2024-03-01
        System.out.println("plusYears(1)   : " + date.plusYears(1));              // 2025-03-15
        System.out.println("withDayOfMonth : " + date.withDayOfMonth(1));         // 2024-03-01

        // GOTCHA — month arithmetic clamps to the last valid day of the target month
        LocalDate jan31 = LocalDate.of(2024, 1, 31);
        System.out.println("Jan31 +1 month : " + jan31.plusMonths(1));            // 2024-02-29 (leap year)
        System.out.println("2023 Jan31 +1m : " + LocalDate.of(2023, 1, 31).plusMonths(1)); // 2023-02-28

        // GOTCHA — order matters: the clamped day is "remembered" by the intermediate result
        System.out.println("Jan31 +2 months       : " + jan31.plusMonths(2));                    // 2024-03-31
        System.out.println("Jan31 +1m then +1m    : " + jan31.plusMonths(1).plusMonths(1));      // 2024-03-29
    }

    // ── 2. LocalTime ──────────────────────────────────────────────────────────

    /**
     * {@link LocalTime} is a time of day without date and zone: {@code 09:30:15.250}.
     *
     * <p>WHY this matters: opening hours, alarm clocks, "the shop closes at 17:00" — a time
     * that repeats every day. Precision is nanoseconds; range is {@code 00:00} to {@code 23:59:59.999999999}.
     *
     * <p>Gotcha: arithmetic wraps around midnight silently, because a LocalTime has no
     * date to carry the overflow into.
     */
    static void localTimeDemo() {
        System.out.println("=== LocalTime ===");

        LocalTime morning = LocalTime.of(9, 30);              // hour, minute
        LocalTime withSeconds = LocalTime.of(9, 30, 15);      // + seconds
        LocalTime parsed = LocalTime.parse("14:45:10");

        // toString omits seconds/nanos when they are zero
        System.out.println("of(9,30)       : " + morning);            // 09:30
        System.out.println("of(9,30,15)    : " + withSeconds);        // 09:30:15
        System.out.println("parsed         : " + parsed);             // 14:45:10

        // Constants
        System.out.println("MIDNIGHT       : " + LocalTime.MIDNIGHT); // 00:00
        System.out.println("NOON           : " + LocalTime.NOON);     // 12:00
        System.out.println("MAX            : " + LocalTime.MAX);      // 23:59:59.999999999

        // Wrap-around — no date, so 20 hours after 09:30 is "05:30 the next day", shown as 05:30
        System.out.println("09:30 + 20h    : " + morning.plusHours(20));   // 05:30

        // Truncation — drop everything smaller than the given unit
        LocalTime precise = LocalTime.of(9, 30, 15, 250_000_000);
        System.out.println("precise        : " + precise);                                  // 09:30:15.250
        System.out.println("truncated(MIN) : " + precise.truncatedTo(ChronoUnit.MINUTES)); // 09:30
        System.out.println("truncated(HRS) : " + precise.truncatedTo(ChronoUnit.HOURS));   // 09:00

        // Seconds since midnight — handy for compact storage and range maths
        System.out.println("toSecondOfDay  : " + morning.toSecondOfDay());  // 34200

        // Invalid values are rejected immediately
        try {
            LocalTime.of(24, 0);
        } catch (DateTimeException e) {
            System.out.println("of(24,0)       : DateTimeException");
        }
    }

    // ── 3. LocalDateTime ──────────────────────────────────────────────────────

    /**
     * {@link LocalDateTime} = {@link LocalDate} + {@link LocalTime}, still without a zone.
     *
     * <p>WHY this matters: "the meeting is at 2024-03-15 10:15" is a perfectly good
     * <em>local</em> description — but it is NOT a point on the timeline. The same value means
     * a different moment in Paris and in Tokyo. Use it for local schedules, not for event timestamps.
     * For a real moment use {@code Instant} or {@code ZonedDateTime} (see the next demos).
     *
     * <p>Complexity: O(1) for every operation.
     */
    static void localDateTimeDemo() {
        System.out.println("=== LocalDateTime ===");

        LocalDateTime dt = LocalDateTime.of(2024, 3, 15, 10, 15, 30);
        LocalDate date = LocalDate.of(2024, 3, 15);
        LocalTime time = LocalTime.of(10, 15);

        System.out.println("of(...)        : " + dt);                          // 2024-03-15T10:15:30
        System.out.println("of(date,time)  : " + LocalDateTime.of(date, time));// 2024-03-15T10:15
        System.out.println("date.atTime    : " + date.atTime(10, 15));         // 2024-03-15T10:15
        System.out.println("atStartOfDay   : " + date.atStartOfDay());         // 2024-03-15T00:00
        System.out.println("time.atDate    : " + time.atDate(date));           // 2024-03-15T10:15
        System.out.println("parsed         : " + LocalDateTime.parse("2024-03-15T10:15:30")); // ISO format

        // Splitting back into the parts
        System.out.println("toLocalDate    : " + dt.toLocalDate());            // 2024-03-15
        System.out.println("toLocalTime    : " + dt.toLocalTime());            // 10:15:30

        // Arithmetic carries over into the date part (unlike LocalTime)
        System.out.println("plusHours(14)  : " + dt.plusHours(14));            // 2024-03-16T00:15:30
        System.out.println("minusMinutes   : " + dt.minusMinutes(20));         // 2024-03-15T09:55:30

        // Field replacement
        System.out.println("withHour(23)   : " + dt.withHour(23));             // 2024-03-15T23:15:30

        // Truncation works here too
        System.out.println("truncated(DAYS): " + dt.truncatedTo(ChronoUnit.DAYS)); // 2024-03-15T00:00
    }

    // ── 4. TemporalAdjusters ──────────────────────────────────────────────────

    /**
     * {@link TemporalAdjusters} is a toolbox of ready-made "move to ..." operations used
     * with {@code date.with(adjuster)}.
     *
     * <p>WHY this matters: "last day of the month" or "next Monday" by hand means fiddling with
     * {@code lengthOfMonth()} and day-of-week arithmetic. Adjusters name the intent and handle
     * leap years and month ends for you.
     *
     * <p>Gotcha: {@code next(X)} is strictly <em>after</em> the date; {@code nextOrSame(X)}
     * returns the date itself when it already is an X.
     */
    static void adjustersDemo() {
        System.out.println("=== TemporalAdjusters ===");

        LocalDate friday = LocalDate.of(2024, 3, 15);   // a Friday

        System.out.println("firstDayOfMonth     : " + friday.with(TemporalAdjusters.firstDayOfMonth()));     // 2024-03-01
        System.out.println("lastDayOfMonth      : " + friday.with(TemporalAdjusters.lastDayOfMonth()));      // 2024-03-31
        System.out.println("firstDayOfNextMonth : " + friday.with(TemporalAdjusters.firstDayOfNextMonth())); // 2024-04-01
        System.out.println("lastDayOfYear       : " + friday.with(TemporalAdjusters.lastDayOfYear()));       // 2024-12-31

        System.out.println("next(MONDAY)        : " + friday.with(TemporalAdjusters.next(DayOfWeek.MONDAY)));     // 2024-03-18
        System.out.println("previous(MONDAY)    : " + friday.with(TemporalAdjusters.previous(DayOfWeek.MONDAY))); // 2024-03-11
        System.out.println("next(FRIDAY)        : " + friday.with(TemporalAdjusters.next(DayOfWeek.FRIDAY)));     // 2024-03-22 (strictly after)
        System.out.println("nextOrSame(FRIDAY)  : " + friday.with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY))); // 2024-03-15

        System.out.println("2nd Tuesday         : " + friday.with(TemporalAdjusters.dayOfWeekInMonth(2, DayOfWeek.TUESDAY))); // 2024-03-12
        System.out.println("last Friday         : " + friday.with(TemporalAdjusters.lastInMonth(DayOfWeek.FRIDAY)));          // 2024-03-29

        // LocalDateTime accepts the same adjusters and keeps the time part
        LocalDateTime dt = LocalDateTime.of(2024, 3, 15, 10, 15);
        System.out.println("dt lastDayOfMonth   : " + dt.with(TemporalAdjusters.lastDayOfMonth())); // 2024-03-31T10:15
    }

    // ── 5. Immutability & validation ──────────────────────────────────────────

    /**
     * Every java.time class is <strong>immutable</strong> and therefore thread-safe.
     *
     * <p>WHY this matters: you can share a {@code LocalDate} between threads, use it as a
     * map key or put it in a {@code static final} constant without defensive copies.
     * The flip side — and the #1 beginner bug — is that {@code date.plusDays(1);} on its own
     * line does nothing: the new value is returned, the original is untouched.
     *
     * <p>Invalid input fails fast with {@link DateTimeException} instead of rolling over
     * silently like {@code java.util.Calendar} does.
     */
    static void immutabilityDemo() {
        System.out.println("=== Immutability & validation ===");

        LocalDate original = LocalDate.of(2024, 3, 15);

        original.plusDays(1);                       // BUG: result thrown away
        System.out.println("after discarded plusDays : " + original);   // 2024-03-15 (unchanged)

        LocalDate tomorrow = original.plusDays(1);  // correct: keep the returned value
        System.out.println("tomorrow                 : " + tomorrow);   // 2024-03-16
        System.out.println("original still           : " + original);   // 2024-03-15

        // Value-based equality: usable in sets and as map keys
        System.out.println("equals                   : " + original.equals(LocalDate.parse("2024-03-15"))); // true

        // Fail fast on impossible dates
        try {
            LocalDate.of(2023, 2, 29);              // 2023 is not a leap year
        } catch (DateTimeException e) {
            System.out.println("of(2023,2,29)            : DateTimeException");
        }
        try {
            LocalDate.of(2024, 13, 1);              // there is no 13th month
        } catch (DateTimeException e) {
            System.out.println("of(2024,13,1)            : DateTimeException");
        }
    }
}
