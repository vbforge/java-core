package com.vbforge.date_time_api;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * ComparisonTasks — implement every method below to make {@code ComparisonTasksTest} go GREEN.
 *
 * <p><b>Workflow:</b>
 * <ol>
 *   <li>Read {@code ComparisonDemo.java} to understand the relevant concept.</li>
 *   <li>Implement the method — remove the {@code UnsupportedOperationException}.</li>
 *   <li>Run {@code mvn test -Dtest=ComparisonTasksTest} and watch it turn GREEN ✅</li>
 * </ol>
 *
 * <p>Each method has a Javadoc describing exactly what to implement. Do not change
 * method signatures or add external libraries — only the Java standard library is needed.
 */
public class ComparisonTasks {

    // ── Task 1 ─────────────────────────────────────────────────────────────────

    /**
     * Checks whether {@code date} lies in the <strong>closed</strong> range
     * {@code [startInclusive, endInclusive]} — both end dates belong to the range.
     * {@code startInclusive} is never after {@code endInclusive}.
     *
     * <p>Examples:
     * <pre>
     *   isWithinRange(2024-03-15, 2024-03-01, 2024-03-31)  → true
     *   isWithinRange(2024-03-01, 2024-03-01, 2024-03-31)  → true    (start boundary)
     *   isWithinRange(2024-03-31, 2024-03-01, 2024-03-31)  → true    (end boundary)
     *   isWithinRange(2024-02-29, 2024-03-01, 2024-03-31)  → false
     *   isWithinRange(2024-04-01, 2024-03-01, 2024-03-31)  → false
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>There is no {@code isBetween}. Combine {@code isBefore} and {@code isAfter}
     *       — and think about which one must be negated for an inclusive boundary.</li>
     * </ul>
     *
     * @param date           the date to test
     * @param startInclusive first day of the range
     * @param endInclusive   last day of the range
     * @return {@code true} if {@code date} is in the range, boundaries included
     */
    public boolean isWithinRange(LocalDate date, LocalDate startInclusive, LocalDate endInclusive) {
        throw new UnsupportedOperationException("TODO: implement isWithinRange");
    }

    // ── Task 2 ─────────────────────────────────────────────────────────────────

    /**
     * Checks whether two <strong>closed</strong> date ranges share at least one day.
     * Both ends of each range belong to it (think hotel bookings counted by whole days),
     * and each range has {@code start <= end}.
     *
     * <p>Examples:
     * <pre>
     *   [Mar 1 .. Mar 10]  vs [Mar 10 .. Mar 20]  → true    (both include Mar 10)
     *   [Mar 1 .. Mar 9]   vs [Mar 10 .. Mar 20]  → false   (a one-day gap)
     *   [Mar 1 .. Mar 31]  vs [Mar 10 .. Mar 12]  → true    (one contains the other)
     *   [Mar 5 .. Mar 5]   vs [Mar 5 .. Mar 5]    → true    (the same single day)
     *   [Mar 10 .. Mar 20] vs [Mar 1 .. Mar 9]    → false   (argument order does not matter)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Two ranges do <em>not</em> overlap exactly when one ends before the other starts.</li>
     *   <li>Mind the closed boundaries: touching on a shared day counts as an overlap.</li>
     * </ul>
     *
     * @param start1 first day of the first range
     * @param end1   last day of the first range
     * @param start2 first day of the second range
     * @param end2   last day of the second range
     * @return {@code true} if the ranges have at least one day in common
     */
    public boolean dateRangesOverlap(LocalDate start1, LocalDate end1, LocalDate start2, LocalDate end2) {
        throw new UnsupportedOperationException("TODO: implement dateRangesOverlap");
    }

    // ── Task 3 ─────────────────────────────────────────────────────────────────

    /**
     * Tells whether two zoned values describe the <strong>same point on the timeline</strong>,
     * even if their zones and wall-clock fields differ.
     *
     * <p>Examples:
     * <pre>
     *   sameMoment(2024-01-15T12:00 Europe/Paris, 2024-01-15T06:00 America/New_York)  → true
     *   sameMoment(2024-01-15T12:00 Europe/Paris, 2024-01-15T12:00 America/New_York)  → false
     *   sameMoment(2024-01-15T12:00 Europe/Paris, 2024-01-15T12:00 Europe/Paris)      → true
     *   sameMoment(2024-01-15T12:00 Europe/Paris, 2024-01-15T20:00 Asia/Tokyo)        → true    (11:00Z both)
     *   sameMoment(2024-01-15T12:00 Europe/Paris, 2024-01-15T19:00 Asia/Tokyo)        → false
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code equals} compares zone and wall-clock fields too — that is not what is asked.</li>
     *   <li>One of the {@code ZonedDateTime} comparison methods looks only at the instant.</li>
     * </ul>
     *
     * @param a first value
     * @param b second value
     * @return {@code true} if both represent the same instant
     */
    public boolean sameMoment(ZonedDateTime a, ZonedDateTime b) {
        throw new UnsupportedOperationException("TODO: implement sameMoment");
    }

    // ── Task 4 ─────────────────────────────────────────────────────────────────

    /**
     * Returns the value from {@code moments} that happens <strong>first on the timeline</strong>.
     * The values may be in different zones. The list is never empty, no two values are the
     * same moment, and it must not be modified.
     *
     * <p>Examples:
     * <pre>
     *   earliest([ 2024-01-15T12:00 Europe/Paris      (= 11:00Z),
     *              2024-01-15T19:30 Asia/Tokyo        (= 10:30Z),
     *              2024-01-15T06:30 America/New_York  (= 11:30Z) ])
     *       → 2024-01-15T19:30 Asia/Tokyo         (smallest wall clock is New York's 06:30 — a trap!)
     *
     *   earliest([ 2024-01-15T12:00 Europe/Paris ])
     *       → 2024-01-15T12:00 Europe/Paris
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Do not compare {@code toLocalDateTime()} — wall clocks of different zones are not
     *       comparable.</li>
     *   <li>{@code ZonedDateTime} is {@link Comparable}; check what its natural order sorts by first.</li>
     * </ul>
     *
     * @param moments a non-empty list of zoned values
     * @return the earliest element of the list
     */
    public ZonedDateTime earliest(List<ZonedDateTime> moments) {
        throw new UnsupportedOperationException("TODO: implement earliest");
    }

    // ── Task 5 ─────────────────────────────────────────────────────────────────

    /**
     * Returns a <strong>new</strong> list with the same dates sorted newest first.
     * The input list is left exactly as it was (it might even be immutable).
     * Equal dates stay in the result.
     *
     * <p>Examples:
     * <pre>
     *   sortNewestFirst([2024-01-01, 2024-03-15, 2023-12-31])  → [2024-03-15, 2024-01-01, 2023-12-31]
     *   sortNewestFirst([2024-05-05, 2024-05-05])              → [2024-05-05, 2024-05-05]
     *   sortNewestFirst([2024-05-05])                          → [2024-05-05]
     *   sortNewestFirst([])                                    → []
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Copy first, then sort the copy.</li>
     *   <li>{@link java.util.Comparator} has a ready-made reversed natural order.</li>
     * </ul>
     *
     * @param dates the dates to sort (not modified)
     * @return a new list in descending order
     */
    public List<LocalDate> sortNewestFirst(List<LocalDate> dates) {
        throw new UnsupportedOperationException("TODO: implement sortNewestFirst");
    }

    // ── Task 6 ─────────────────────────────────────────────────────────────────

    /**
     * Decides whether {@code dateTime} falls inside business hours: Monday to Friday,
     * from 09:00 (inclusive) up to 17:00 (<strong>exclusive</strong>). Holidays are ignored.
     *
     * <p>Examples:
     * <pre>
     *   isBusinessHours(2024-03-18T09:00)     → true    (Monday, opening time itself)
     *   isBusinessHours(2024-03-18T16:59:59)  → true
     *   isBusinessHours(2024-03-18T17:00)     → false   (closing time itself)
     *   isBusinessHours(2024-03-18T08:59:59)  → false
     *   isBusinessHours(2024-03-16T12:00)     → false   (Saturday)
     *   isBusinessHours(2024-03-17T12:00)     → false   (Sunday)
     *   isBusinessHours(2024-03-22T12:00)     → true    (Friday)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Split the value into its day-of-week and its time-of-day.</li>
     *   <li>The time check is a half-open range: start included, end excluded.</li>
     * </ul>
     *
     * @param dateTime the local date-time to test
     * @return {@code true} if it is a weekday between 09:00 (incl.) and 17:00 (excl.)
     */
    public boolean isBusinessHours(LocalDateTime dateTime) {
        throw new UnsupportedOperationException("TODO: implement isBusinessHours");
    }
}
