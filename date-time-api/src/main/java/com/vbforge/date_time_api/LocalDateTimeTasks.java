package com.vbforge.date_time_api;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;

/**
 * LocalDateTimeTasks — implement every method below to make {@code LocalDateTimeTasksTest} go GREEN.
 *
 * <p><b>Workflow:</b>
 * <ol>
 *   <li>Read {@code LocalDateTimeDemo.java} to understand the relevant concept.</li>
 *   <li>Implement the method — remove the {@code UnsupportedOperationException}.</li>
 *   <li>Run {@code mvn test -Dtest=LocalDateTimeTasksTest} and watch it turn GREEN ✅</li>
 * </ol>
 *
 * <p>Each method has a Javadoc describing exactly what to implement. Do not change
 * method signatures or add external libraries — only the Java standard library is needed.
 */
public class LocalDateTimeTasks {

    // ── Task 1 ─────────────────────────────────────────────────────────────────

    /**
     * Returns the last calendar day of the month that contains {@code date}.
     * Leap years must be respected.
     *
     * <p>Examples:
     * <pre>
     *   lastDayOfMonth(2024-02-10)  → 2024-02-29   (2024 is a leap year)
     *   lastDayOfMonth(2023-02-10)  → 2023-02-28
     *   lastDayOfMonth(2024-04-01)  → 2024-04-30
     *   lastDayOfMonth(2024-12-31)  → 2024-12-31   (already the last day)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>You do not need any month-length table — look at what
     *       {@link java.time.temporal.TemporalAdjusters} offers.</li>
     * </ul>
     *
     * @param date any date
     * @return the last day of the same month
     */
    public LocalDate lastDayOfMonth(LocalDate date) {
        throw new UnsupportedOperationException("TODO: implement lastDayOfMonth");
    }

    // ── Task 2 ─────────────────────────────────────────────────────────────────

    /**
     * Returns the next working day <em>strictly after</em> {@code date}.
     * Working days are Monday to Friday; public holidays are ignored.
     *
     * <p>Examples:
     * <pre>
     *   nextWorkingDay(2024-03-14 Thu)  → 2024-03-15 Fri
     *   nextWorkingDay(2024-03-15 Fri)  → 2024-03-18 Mon
     *   nextWorkingDay(2024-03-16 Sat)  → 2024-03-18 Mon
     *   nextWorkingDay(2024-03-17 Sun)  → 2024-03-18 Mon
     *   nextWorkingDay(2024-12-31 Tue)  → 2025-01-01 Wed   (crosses the year boundary)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Move one day forward, then keep moving while the day is a Saturday or Sunday.</li>
     *   <li>{@link DayOfWeek} is an enum — you can compare it directly.</li>
     * </ul>
     *
     * @param date the starting date
     * @return the first Monday–Friday date after {@code date}
     */
    public LocalDate nextWorkingDay(LocalDate date) {
        throw new UnsupportedOperationException("TODO: implement nextWorkingDay");
    }

    // ── Task 3 ─────────────────────────────────────────────────────────────────

    /**
     * Returns the calendar quarter (1–4) of {@code date}:
     * Q1 = Jan–Mar, Q2 = Apr–Jun, Q3 = Jul–Sep, Q4 = Oct–Dec.
     *
     * <p>Examples:
     * <pre>
     *   quarterOf(2024-01-01)  → 1
     *   quarterOf(2024-03-31)  → 1
     *   quarterOf(2024-04-01)  → 2
     *   quarterOf(2024-09-30)  → 3
     *   quarterOf(2024-12-31)  → 4
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>A little integer arithmetic on the month number is enough
     *       (remember months are 1-based).</li>
     * </ul>
     *
     * @param date any date
     * @return quarter number from 1 to 4
     */
    public int quarterOf(LocalDate date) {
        throw new UnsupportedOperationException("TODO: implement quarterOf");
    }

    // ── Task 4 ─────────────────────────────────────────────────────────────────

    /**
     * Rounds {@code dateTime} <em>down</em> to the start of its 15-minute slot:
     * minutes become 0, 15, 30 or 45, and seconds and nanoseconds become zero.
     * The date never changes.
     *
     * <p>Examples:
     * <pre>
     *   floorToQuarterHour(2024-03-15T10:47:30.123)  → 2024-03-15T10:45
     *   floorToQuarterHour(2024-03-15T10:14:59)      → 2024-03-15T10:00
     *   floorToQuarterHour(2024-03-15T10:15:00)      → 2024-03-15T10:15   (already aligned)
     *   floorToQuarterHour(2024-03-15T23:59:59)      → 2024-03-15T23:45
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>First drop seconds and nanos in one call — see {@code truncatedTo}.</li>
     *   <li>Then subtract the remainder of {@code minute % 15}.</li>
     * </ul>
     *
     * @param dateTime the value to round down
     * @return a new {@link LocalDateTime} aligned to a quarter hour
     */
    public LocalDateTime floorToQuarterHour(LocalDateTime dateTime) {
        throw new UnsupportedOperationException("TODO: implement floorToQuarterHour");
    }

    // ── Task 5 ─────────────────────────────────────────────────────────────────

    /**
     * Returns the {@code n}-th given weekday of a month, e.g. "the 4th Thursday of
     * November 2024". {@code n} is always between 1 and 4, so the result always exists.
     *
     * <p>Examples:
     * <pre>
     *   nthWeekdayOfMonth(2024, NOVEMBER, THURSDAY, 4)  → 2024-11-28
     *   nthWeekdayOfMonth(2024, MARCH,    MONDAY,   1)  → 2024-03-04   (Mar 1 is a Friday)
     *   nthWeekdayOfMonth(2024, JANUARY,  MONDAY,   3)  → 2024-01-15   (Jan 1 is itself a Monday)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Start from the first day of the month and let an adjuster from
     *       {@link java.time.temporal.TemporalAdjusters} do the weekday search.</li>
     * </ul>
     *
     * @param year      the year, e.g. 2024
     * @param month     the month
     * @param dayOfWeek the weekday to look for
     * @param n         which occurrence, from 1 to 4
     * @return the date of the n-th {@code dayOfWeek} in that month
     */
    public LocalDate nthWeekdayOfMonth(int year, Month month, DayOfWeek dayOfWeek, int n) {
        throw new UnsupportedOperationException("TODO: implement nthWeekdayOfMonth");
    }
}
