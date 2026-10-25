package com.vbforge.date_time_api;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.zone.ZoneRules;
import java.time.zone.ZoneRulesException;

/**
 * ZonedDateTimeDemo — annotated reference for time zones: {@link ZoneId}, {@link ZoneOffset},
 * {@link ZonedDateTime}, daylight-saving transitions and {@link OffsetDateTime}.
 *
 * <p>Read top-to-bottom. Each section covers one idea with concrete examples and a note
 * explaining <em>why</em> the behaviour is what it is.
 *
 * <p>All examples use explicit zones — never {@code ZoneId.systemDefault()} — so the output
 * is identical on every machine.
 *
 * <p>Topics:
 * <ol>
 *   <li>ZoneId vs ZoneOffset — a region with rules vs a fixed shift from UTC</li>
 *   <li>Creating and converting ZonedDateTime — same instant vs same local time</li>
 *   <li>DST gap — local times that do not exist, and 23-hour days</li>
 *   <li>DST overlap — local times that exist twice, and 25-hour days</li>
 *   <li>OffsetDateTime — when a fixed offset is enough</li>
 * </ol>
 */
public class ZonedDateTimeDemo {

    private static final ZoneId PARIS = ZoneId.of("Europe/Paris");
    private static final ZoneId NEW_YORK = ZoneId.of("America/New_York");

    public static void main(String[] args) {
        zoneIdAndOffsetDemo();
        System.out.println("---");
        zonedCreationDemo();
        System.out.println("---");
        dstGapDemo();
        System.out.println("---");
        dstOverlapDemo();
        System.out.println("---");
        offsetDateTimeDemo();
    }

    // ── 1. ZoneId vs ZoneOffset ───────────────────────────────────────────────

    /**
     * A {@link ZoneId} identifies a <em>region</em> ({@code Europe/Paris}) together with its
     * rules: when daylight saving starts, what the offset was in 1950, and so on.
     * A {@link ZoneOffset} (a subclass) is just a fixed shift from UTC ({@code +02:00}).
     *
     * <p>WHY this matters: Paris is {@code +01:00} in winter and {@code +02:00} in summer.
     * "+02:00" is therefore NOT a time zone — it is only one of the offsets Paris uses.
     * Store region ids when you must keep doing calendar maths in the future; store offsets
     * when you only need to record what happened.
     *
     * <p>Gotcha: the zone database is data, not code — governments change rules.
     * Never hard-code "Paris = UTC+1".
     */
    static void zoneIdAndOffsetDemo() {
        System.out.println("=== ZoneId vs ZoneOffset ===");

        ZoneOffset plus2 = ZoneOffset.ofHours(2);

        System.out.println("region id       : " + PARIS.getId());            // Europe/Paris
        System.out.println("fixed offset    : " + plus2);                    // +02:00
        System.out.println("UTC offset      : " + ZoneOffset.UTC);           // Z

        // Same region, different offsets at different times of the year
        ZoneRules rules = PARIS.getRules();
        Instant winter = Instant.parse("2024-01-15T12:00:00Z");
        Instant summer = Instant.parse("2024-07-15T12:00:00Z");
        System.out.println("Paris in winter : " + rules.getOffset(winter));  // +01:00
        System.out.println("Paris in summer : " + rules.getOffset(summer));  // +02:00

        // A fixed offset never changes
        System.out.println("Paris fixed?    : " + rules.isFixedOffset());                  // false
        System.out.println("+02:00 fixed?   : " + plus2.getRules().isFixedOffset());       // true

        // Unknown ids fail fast
        try {
            ZoneId.of("Mars/Base");
        } catch (ZoneRulesException e) {
            System.out.println("Mars/Base      : ZoneRulesException");
        }
    }

    // ── 2. Creating and converting ZonedDateTime ──────────────────────────────

    /**
     * {@link ZonedDateTime} = {@link LocalDateTime} + {@link ZoneId} + the resolved
     * {@link ZoneOffset}. It is a complete, unambiguous description of a moment <em>as seen on
     * a wall clock in a particular region</em>.
     *
     * <p>WHY this matters: for user-facing times ("the call is at 09:00 New York time")
     * you need both the moment and the region's rules. Converting between zones is the
     * everyday operation — and it has two very different flavours:
     * <ul>
     *   <li>{@code withZoneSameInstant} — same moment, different wall clock (what you want 99% of the time)</li>
     *   <li>{@code withZoneSameLocal} — same wall clock, different moment (almost always a bug)</li>
     * </ul>
     */
    static void zonedCreationDemo() {
        System.out.println("=== ZonedDateTime creation & conversion ===");

        LocalDateTime noon = LocalDateTime.of(2024, 3, 15, 12, 0);

        ZonedDateTime parisNoon = ZonedDateTime.of(noon, PARIS);
        ZonedDateTime fromInstant = Instant.parse("2024-03-15T11:00:00Z").atZone(PARIS);

        System.out.println("of(ldt, zone)       : " + parisNoon);    // 2024-03-15T12:00+01:00[Europe/Paris]
        System.out.println("instant.atZone      : " + fromInstant);  // 2024-03-15T12:00+01:00[Europe/Paris]
        System.out.println("equal?              : " + parisNoon.equals(fromInstant)); // true

        // Same instant, different wall clock — the correct "convert to another zone"
        ZonedDateTime sameInstant = parisNoon.withZoneSameInstant(NEW_YORK);
        System.out.println("withZoneSameInstant : " + sameInstant);  // 2024-03-15T07:00-04:00[America/New_York]
        System.out.println("same Instant?       : " + parisNoon.toInstant().equals(sameInstant.toInstant())); // true

        // Same wall clock, different instant — the classic bug
        ZonedDateTime sameLocal = parisNoon.withZoneSameLocal(NEW_YORK);
        System.out.println("withZoneSameLocal   : " + sameLocal);    // 2024-03-15T12:00-04:00[America/New_York]
        System.out.println("same Instant?       : " + parisNoon.toInstant().equals(sameLocal.toInstant())); // false

        // Getters
        System.out.println("zone                : " + parisNoon.getZone());    // Europe/Paris
        System.out.println("offset              : " + parisNoon.getOffset());  // +01:00
    }

    // ── 3. DST gap ────────────────────────────────────────────────────────────

    /**
     * When clocks jump forward, a block of local times simply does not exist. In New York on
     * 2024-03-10 the clock goes from 02:00 straight to 03:00.
     *
     * <p>WHY this matters: a daily job "at 02:30" has no 02:30 on that day. java.time does
     * not throw — it moves the time <em>forward by the length of the gap</em> and tells you
     * the result. Likewise, "plus one day" and "plus 24 hours" are different questions across
     * a transition.
     *
     * <p>Rules of thumb:
     * <ul>
     *   <li>{@code plusDays/plusMonths/...} operate on the local date-time, then re-resolve the zone</li>
     *   <li>{@code plusHours/plusMinutes/...} operate on the timeline (exact elapsed time)</li>
     * </ul>
     */
    static void dstGapDemo() {
        System.out.println("=== DST gap (spring forward) ===");

        // 02:30 does not exist on 2024-03-10 in New York
        LocalDateTime missing = LocalDateTime.of(2024, 3, 10, 2, 30);
        ZonedDateTime resolved = ZonedDateTime.of(missing, NEW_YORK);
        System.out.println("requested 02:30     : " + missing);                   // 2024-03-10T02:30
        System.out.println("resolved to         : " + resolved);                  // 2024-03-10T03:30-04:00[America/New_York]
        System.out.println("valid offsets       : " + NEW_YORK.getRules().getValidOffsets(missing)); // []

        // "+1 day" vs "+24 hours" across the transition
        ZonedDateTime saturdayNoon = ZonedDateTime.of(2024, 3, 9, 12, 0, 0, 0, NEW_YORK);
        ZonedDateTime plusDay = saturdayNoon.plusDays(1);
        ZonedDateTime plusHours = saturdayNoon.plusHours(24);
        System.out.println("saturday noon       : " + saturdayNoon);              // 2024-03-09T12:00-05:00[America/New_York]
        System.out.println("plusDays(1)         : " + plusDay);                   // 2024-03-10T12:00-04:00[America/New_York]
        System.out.println("plusHours(24)       : " + plusHours);                 // 2024-03-10T13:00-04:00[America/New_York]
        System.out.println("elapsed for +1 day  : " + Duration.between(saturdayNoon, plusDay)); // PT23H

        // A calendar day is not always 24 hours
        ZonedDateTime start = LocalDate.of(2024, 3, 10).atStartOfDay(NEW_YORK);
        ZonedDateTime next = LocalDate.of(2024, 3, 11).atStartOfDay(NEW_YORK);
        System.out.println("length of Mar 10    : " + Duration.between(start, next)); // PT23H
    }

    // ── 4. DST overlap ────────────────────────────────────────────────────────

    /**
     * When clocks jump back, a block of local times happens <em>twice</em>. In New York on
     * 2024-11-03 the clock goes from 02:00 EDT back to 01:00 EST, so 01:30 occurs once at
     * {@code -04:00} and again at {@code -05:00}.
     *
     * <p>WHY this matters: a {@link LocalDateTime} alone cannot identify a moment in an
     * overlap. {@code ZonedDateTime.of(...)} picks the <em>earlier</em> offset (the first
     * occurrence) by default; {@code withLaterOffsetAtOverlap()} picks the second one.
     */
    static void dstOverlapDemo() {
        System.out.println("=== DST overlap (fall back) ===");

        LocalDateTime ambiguous = LocalDateTime.of(2024, 11, 3, 1, 30);

        System.out.println("valid offsets       : " + NEW_YORK.getRules().getValidOffsets(ambiguous)); // [-04:00, -05:00]

        ZonedDateTime first = ZonedDateTime.of(ambiguous, NEW_YORK);
        ZonedDateTime second = first.withLaterOffsetAtOverlap();
        System.out.println("default (earlier)   : " + first);    // 2024-11-03T01:30-04:00[America/New_York]
        System.out.println("later offset        : " + second);   // 2024-11-03T01:30-05:00[America/New_York]
        System.out.println("hours between them  : " + Duration.between(first, second)); // PT1H

        // Same wall-clock text, two different moments
        System.out.println("same local time?    : " + first.toLocalDateTime().equals(second.toLocalDateTime())); // true
        System.out.println("same instant?       : " + first.toInstant().equals(second.toInstant()));            // false

        // A calendar day is not always 24 hours
        ZonedDateTime start = LocalDate.of(2024, 11, 3).atStartOfDay(NEW_YORK);
        ZonedDateTime next = LocalDate.of(2024, 11, 4).atStartOfDay(NEW_YORK);
        System.out.println("length of Nov 3     : " + Duration.between(start, next)); // PT25H
    }

    // ── 5. OffsetDateTime ─────────────────────────────────────────────────────

    /**
     * {@link OffsetDateTime} = {@link LocalDateTime} + a fixed {@link ZoneOffset}. No region,
     * no rules.
     *
     * <p>WHY this matters: it is the natural type for data that crosses system boundaries —
     * JSON/REST payloads, database {@code timestamptz} columns — because it is unambiguous and
     * needs no zone database. The price: it cannot do DST-aware maths.
     *
     * <p>Rule of thumb: use {@code OffsetDateTime} (or {@code Instant}) to record what
     * <em>happened</em>; use {@code ZonedDateTime} to schedule what <em>will happen</em>
     * on a user's calendar.
     */
    static void offsetDateTimeDemo() {
        System.out.println("=== OffsetDateTime ===");

        OffsetDateTime offset = OffsetDateTime.of(2024, 3, 15, 12, 0, 0, 0, ZoneOffset.ofHours(1));
        System.out.println("offset date-time    : " + offset);                       // 2024-03-15T12:00+01:00

        // Converting from a ZonedDateTime drops the region and keeps the current offset
        ZonedDateTime parisNoon = ZonedDateTime.of(2024, 3, 15, 12, 0, 0, 0, PARIS);
        System.out.println("toOffsetDateTime    : " + parisNoon.toOffsetDateTime()); // 2024-03-15T12:00+01:00

        // Six months later: Paris has switched to summer time, the fixed offset has not
        System.out.println("zoned  +6 months    : " + parisNoon.plusMonths(6));      // 2024-09-15T12:00+02:00[Europe/Paris]
        System.out.println("offset +6 months    : " + offset.plusMonths(6));         // 2024-09-15T12:00+01:00
    }
}
