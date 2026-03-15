package com.vbforge.datetime;

import java.time.*;
import java.time.format.*;
import java.time.temporal.*;

/**
 * ═══════════════════════════════════════════════════════════════
 *  DATE-TIME INTERVIEW REFERENCE
 *  Topics: LocalDate/Time · ZonedDateTime · Period vs Duration ·
 *          formatting · common gotchas · legacy Date migration
 * ═══════════════════════════════════════════════════════════════
 *
 *  TODO: expand with your own snippets as you study this topic.
 */
@SuppressWarnings("all")
public class DateTimeReference {

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 1. TYPE OVERVIEW
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    /*
     *  Type               │ What it holds                  │ TZ-aware?
     *  ───────────────────┼────────────────────────────────┼──────────
     *  LocalDate          │ date only (2024-03-15)         │ ❌ No
     *  LocalTime          │ time only (14:30:00)           │ ❌ No
     *  LocalDateTime      │ date + time (no TZ)            │ ❌ No
     *  ZonedDateTime      │ date + time + TZ               │ ✅ Yes
     *  OffsetDateTime     │ date + time + UTC offset       │ Partial
     *  Instant            │ epoch millis (machine time)    │ UTC only
     *  Period             │ date-based duration (years/months/days) │ N/A
     *  Duration           │ time-based duration (hours/minutes/nanos)│ N/A
     *
     *  ALL date-time objects are IMMUTABLE — operations return new instances.
     */

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 2. COMMON OPERATIONS
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    static void commonOperations() {
        // Creation
        LocalDate     today    = LocalDate.now();
        LocalDate     specific = LocalDate.of(2024, Month.MARCH, 15);
        LocalTime     noon     = LocalTime.of(12, 0);
        LocalDateTime dt       = LocalDateTime.of(specific, noon);
        ZonedDateTime zdt      = ZonedDateTime.now(ZoneId.of("Europe/Kiev"));
        Instant       now      = Instant.now();

        // Arithmetic — all return new instances (immutable)
        LocalDate nextWeek  = today.plusWeeks(1);
        LocalDate lastMonth = today.minusMonths(1);
        LocalDate nextYear  = today.with(TemporalAdjusters.firstDayOfNextYear());

        // Period vs Duration
        Period   period   = Period.between(specific, today);       // date-based
        Duration duration = Duration.between(noon, LocalTime.now()); // time-based
        System.out.println("Days between: " + period.getDays());
        System.out.println("Hours since: " + duration.toHours());

        // Formatting
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formatted  = today.format(fmt);
        LocalDate parsed  = LocalDate.parse("15/03/2024", fmt);

        // ⚠️ GOTCHA: LocalDate.parse() uses ISO format by default (yyyy-MM-dd)
        LocalDate isoDate = LocalDate.parse("2024-03-15");  // ✅ works
        // LocalDate.parse("15/03/2024");  // DateTimeParseException — needs formatter
    }


    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 3. LEGACY DATE MIGRATION ⚠️
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    /*
     *  java.util.Date → Instant:     date.toInstant()
     *  Instant → java.util.Date:     Date.from(instant)
     *  LocalDateTime → Instant:      dt.toInstant(ZoneOffset.UTC)
     *  Instant → LocalDateTime:      LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
     *
     *  NEVER use java.util.Date or java.util.Calendar in new code.
     *  They are mutable, thread-unsafe, and have confusing APIs.
     */

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // ANTI-PATTERNS ❌
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    /*
     *  ❌ Using java.util.Date or Calendar         → legacy, mutable, confusing
     *  ❌ Storing LocalDateTime in DB without TZ   → use ZonedDateTime or Instant
     *  ❌ Assuming LocalDate arithmetic handles DST → use ZonedDateTime for that
     *  ❌ Comparing dates with == instead of equals() or isBefore/isAfter
     *  ❌ Using DateTimeFormatter without Locale   → output varies by locale
     */

    public static void main(String[] args) {
        commonOperations();
    }
}
