package com.vbforge.date_time_api;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

/**
 * ZonedDateTimeTasks — implement every method below to make {@code ZonedDateTimeTasksTest} go GREEN.
 *
 * <p><b>Workflow:</b>
 * <ol>
 *   <li>Read {@code ZonedDateTimeDemo.java} to understand the relevant concept.</li>
 *   <li>Implement the method — remove the {@code UnsupportedOperationException}.</li>
 *   <li>Run {@code mvn test -Dtest=ZonedDateTimeTasksTest} and watch it turn GREEN ✅</li>
 * </ol>
 *
 * <p>Each method has a Javadoc describing exactly what to implement. Do not change
 * method signatures or add external libraries — only the Java standard library is needed.
 */
public class ZonedDateTimeTasks {

    // ── Task 1 ─────────────────────────────────────────────────────────────────

    /**
     * Converts {@code moment} to {@code targetZone} so that it describes the <em>same
     * point on the timeline</em> but shows the wall-clock time of the target zone.
     *
     * <p>Examples:
     * <pre>
     *   convertToZone(2024-03-15T12:00 Europe/Paris,  America/New_York)  → 2024-03-15T07:00 America/New_York
     *   convertToZone(2024-03-15T12:00 Europe/Paris,  Asia/Tokyo)        → 2024-03-15T20:00 Asia/Tokyo
     *   convertToZone(2024-03-15T23:30 America/New_York, Europe/Paris)   → 2024-03-16T04:30 Europe/Paris
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>There are two {@code withZone...} methods — one keeps the instant, the other keeps
     *       the local time. You need the one that keeps the instant.</li>
     * </ul>
     *
     * @param moment     the moment to convert
     * @param targetZone the zone to display it in
     * @return a {@link ZonedDateTime} in {@code targetZone} representing the same instant
     */
    public ZonedDateTime convertToZone(ZonedDateTime moment, ZoneId targetZone) {
        throw new UnsupportedOperationException("TODO: implement convertToZone");
    }

    // ── Task 2 ─────────────────────────────────────────────────────────────────

    /**
     * A meeting is scheduled at {@code meetingLocalTime} in {@code meetingZone}. Returns the
     * local date-time that a participant living in {@code participantZone} sees on their own
     * wall clock. The date may differ from the meeting's date.
     *
     * <p>Examples:
     * <pre>
     *   meetingTimeFor(2024-03-15T09:00, America/New_York, Asia/Tokyo)    → 2024-03-15T22:00
     *   meetingTimeFor(2024-03-15T09:00, America/New_York, Europe/Paris)  → 2024-03-15T14:00
     *   meetingTimeFor(2024-03-15T20:00, America/New_York, Asia/Tokyo)    → 2024-03-16T09:00
     *   meetingTimeFor(2024-03-15T09:00, Europe/Paris,     Europe/Paris)  → 2024-03-15T09:00
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Attach the meeting zone to the local time, shift to the other zone while
     *       keeping the instant, then drop the zone again.</li>
     * </ul>
     *
     * @param meetingLocalTime the meeting start as written on the organiser's wall clock
     * @param meetingZone      the organiser's zone
     * @param participantZone  the participant's zone
     * @return the meeting start on the participant's wall clock
     */
    public LocalDateTime meetingTimeFor(LocalDateTime meetingLocalTime,
                                        ZoneId meetingZone,
                                        ZoneId participantZone) {
        throw new UnsupportedOperationException("TODO: implement meetingTimeFor");
    }

    // ── Task 3 ─────────────────────────────────────────────────────────────────

    /**
     * Returns the UTC offset that {@code zone} uses at the given {@code instant}.
     * The same zone can answer differently in winter and summer.
     *
     * <p>Examples:
     * <pre>
     *   offsetAt(Europe/Paris,      2024-01-15T12:00:00Z)  → +01:00
     *   offsetAt(Europe/Paris,      2024-07-15T12:00:00Z)  → +02:00
     *   offsetAt(America/New_York,  2024-07-15T12:00:00Z)  → -04:00
     *   offsetAt(Asia/Kolkata,      2024-07-15T12:00:00Z)  → +05:30   (no DST)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>A {@link ZoneId} knows its own rules — ask it for them, then ask the rules about
     *       the instant.</li>
     * </ul>
     *
     * @param zone    the region
     * @param instant the moment
     * @return the offset in force at that moment
     */
    public ZoneOffset offsetAt(ZoneId zone, Instant instant) {
        throw new UnsupportedOperationException("TODO: implement offsetAt");
    }

    // ── Task 4 ─────────────────────────────────────────────────────────────────

    /**
     * Returns how many hours the calendar day {@code date} lasts in {@code zone}.
     * Normally 24, but 23 on a spring-forward day and 25 on a fall-back day.
     *
     * <p>Examples:
     * <pre>
     *   hoursInDay(2024-06-01, America/New_York)  → 24
     *   hoursInDay(2024-03-10, America/New_York)  → 23   (clocks jump 02:00 → 03:00)
     *   hoursInDay(2024-11-03, America/New_York)  → 25   (clocks fall back 02:00 → 01:00)
     *   hoursInDay(2024-04-07, Australia/Sydney)  → 25
     *   hoursInDay(2024-03-10, Asia/Tokyo)        → 24   (no DST)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Find the exact moment the day starts in the zone, and the moment the next day starts.</li>
     *   <li>{@code LocalDate.atStartOfDay(ZoneId)} — not {@code atTime(0, 0)} — is the safe way
     *       to get a day's start.</li>
     *   <li>{@link java.time.Duration#between} measures elapsed time on the timeline.</li>
     * </ul>
     *
     * @param date the calendar day
     * @param zone the region whose clock decides when the day starts and ends
     * @return length of the day in whole hours
     */
    public long hoursInDay(LocalDate date, ZoneId zone) {
        throw new UnsupportedOperationException("TODO: implement hoursInDay");
    }

    // ── Task 5 ─────────────────────────────────────────────────────────────────

    /**
     * Tells whether {@code zone} is on daylight-saving ("summer") time at the given
     * {@code instant}. Zones without DST always answer {@code false}.
     *
     * <p>Examples:
     * <pre>
     *   isDaylightSavingTime(America/New_York, 2024-07-15T12:00:00Z)  → true
     *   isDaylightSavingTime(America/New_York, 2024-01-15T12:00:00Z)  → false
     *   isDaylightSavingTime(Australia/Sydney, 2024-01-15T00:00:00Z)  → true    (southern summer)
     *   isDaylightSavingTime(Asia/Tokyo,       2024-07-15T12:00:00Z)  → false
     *   isDaylightSavingTime(UTC,              2024-07-15T12:00:00Z)  → false
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>The zone's rules object already has a method for this question.</li>
     *   <li>Do not guess from the month — the southern hemisphere is flipped.</li>
     * </ul>
     *
     * @param zone    the region
     * @param instant the moment
     * @return {@code true} if daylight-saving time is in force
     */
    public boolean isDaylightSavingTime(ZoneId zone, Instant instant) {
        throw new UnsupportedOperationException("TODO: implement isDaylightSavingTime");
    }
}
