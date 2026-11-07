// IMPLEMENTATION
package com.vbforge.practice.practice_8_localdatetime;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;

/**
 * Demonstrates {@link LocalDateTime} — date + time, no timezone.
 *
 * Key concepts:
 *   - Combines LocalDate + LocalTime in one object.
 *   - NO timezone or offset awareness. Two LocalDateTimes in different timezones
 *     may represent different instants — LocalDateTime does NOT know about that.
 *   - Use for: scheduling, UI display, log timestamps where timezone is implicit.
 *   - Convert to ZonedDateTime by attaching a ZoneId: atZone(ZoneId)
 *   - Convert to Instant: atZone(ZoneId).toInstant()
 *
 * Notable differences from LocalDate:
 *   - also has hour, minute, second, nanosecond fields
 *   - plusHours, plusMinutes, plusSeconds, plusNanos
 *   - truncatedTo(ChronoUnit) — zeroes everything below the given unit
 */
public class LocalDateTimeDemo {

    public static void main(String[] args) {
        System.out.println("=== LocalDateTime Demo ===\n");

        // --- 1. Creation ---
        LocalDateTime now      = LocalDateTime.now();
        LocalDateTime specific = LocalDateTime.of(2024, Month.JUNE, 15, 14, 30, 0);
        LocalDateTime fromParts = LocalDateTime.of(
                LocalDate.of(2024, 6, 15),
                LocalTime.of(9, 0));
        LocalDateTime parsed   = LocalDateTime.parse("2024-12-31T23:59:59");

        System.out.println("now      : " + now);
        System.out.println("specific : " + specific);
        System.out.println("fromParts: " + fromParts);
        System.out.println("parsed   : " + parsed);

        // --- 2. Field access ---
        System.out.println("\n-- Fields --");
        System.out.println("year   : " + specific.getYear());
        System.out.println("month  : " + specific.getMonth());
        System.out.println("day    : " + specific.getDayOfMonth());
        System.out.println("hour   : " + specific.getHour());
        System.out.println("minute : " + specific.getMinute());
        System.out.println("second : " + specific.getSecond());
        System.out.println("nano   : " + specific.getNano());

        // --- 3. Decomposition ---
        LocalDate datePart = specific.toLocalDate();
        LocalTime timePart = specific.toLocalTime();
        System.out.println("\ndatePart : " + datePart);
        System.out.println("timePart : " + timePart);

        // --- 4. Arithmetic ---
        System.out.println("\n-- Arithmetic --");
        System.out.println("+ 90 minutes : " + specific.plusMinutes(90));
        System.out.println("- 3 hours    : " + specific.minusHours(3));
        System.out.println("+ 10 days    : " + specific.plusDays(10));
        System.out.println("+ 1 year     : " + specific.plusYears(1));

        // Crossing midnight
        LocalDateTime nearMidnight = LocalDateTime.of(2024, 6, 15, 23, 0, 0);
        System.out.println("23:00 + 2h   : " + nearMidnight.plusHours(2)); // → 2024-06-16T01:00

        // --- 5. Truncation ---
        LocalDateTime withNanos = LocalDateTime.of(2024, 6, 15, 14, 35, 47, 123_456_789);
        System.out.println("\n-- Truncation --");
        System.out.println("original       : " + withNanos);
        System.out.println("truncToMinutes : " + withNanos.truncatedTo(ChronoUnit.MINUTES));
        System.out.println("truncToHours   : " + withNanos.truncatedTo(ChronoUnit.HOURS));
        System.out.println("truncToDays    : " + withNanos.truncatedTo(ChronoUnit.DAYS));

        // --- 6. TemporalAdjusters still work ---
        System.out.println("\n-- Adjusters (date part only) --");
        System.out.println("firstDayOfMonth: " + specific.with(TemporalAdjusters.firstDayOfMonth()));
        System.out.println("lastDayOfYear  : " + specific.with(TemporalAdjusters.lastDayOfYear()));

        // --- 7. Conversion to ZonedDateTime / Instant ---
        System.out.println("\n-- Conversion --");
        ZonedDateTime zoned   = specific.atZone(ZoneId.of("Europe/Warsaw"));
        OffsetDateTime offset = specific.atOffset(ZoneOffset.of("+02:00"));
        Instant instant       = zoned.toInstant();
        System.out.println("zoned   : " + zoned);
        System.out.println("offset  : " + offset);
        System.out.println("instant : " + instant);

        // --- 8. Comparison ---
        LocalDateTime a = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime b = LocalDateTime.of(2024, 6, 1, 0, 0);
        System.out.println("\na.isBefore(b) : " + a.isBefore(b));
        System.out.println("b.isAfter(a)  : " + b.isAfter(a));
        System.out.println("a.isEqual(a)  : " + a.isEqual(a));

        // --- 9 .. 12: extra scenes ---
        currentDateAndTime();
        moreCreationExamples();
        compareToExamples();
        changingDateAndTimes();
    }

    /**
     * Current date and time (simplest form).
     */
    private static void currentDateAndTime() {
        System.out.println("\n-- Current date and time --");
        LocalDateTime myObj = LocalDateTime.now();
        System.out.println(myObj);
    }

    /**
     * More creation examples: minute-precision overload and combining LocalDate + LocalTime.
     */
    private static void moreCreationExamples() {
        System.out.println("\n-- More creation examples --");
        LocalDate ld1 = LocalDate.now();
        LocalTime lt1 = LocalTime.now();

        //datetime
        LocalDateTime ldt1 = LocalDateTime.now();
        LocalDateTime ldt2 = LocalDateTime.of(2025, 1, 16, 10, 10);
        LocalDateTime ldt3 = LocalDateTime.of(ld1, lt1);

        System.out.println("datetime");
        System.out.println(ldt1);
        System.out.println(ldt2);
        System.out.println(ldt3 + "\n");
    }

    /**
     * compareTo() alongside isAfter / isBefore / isEqual.
     */
    private static void compareToExamples() {
        System.out.println("-- compareTo --");
        LocalDateTime ldt1 = LocalDateTime.now();
        LocalDateTime ldt2 = LocalDateTime.of(2024, 12, 31, 10, 10);

        //compareTo (only the sign matters; the exact value can be a year/month/day difference)
        System.out.println(ldt1.compareTo(ldt2)); // 1 --> first is greater than second date
        System.out.println(ldt2.compareTo(ldt1)); // -1 --> second is greater than first date

        //isAfter
        System.out.println(ldt1.isAfter(ldt2)); //true

        //isBefore
        System.out.println(ldt1.isBefore(ldt2)); //false

        //isAfter
        System.out.println(ldt1.isEqual(ldt2)); //false
    }

    /**
     * plus / minus / with, including plus(Period) and minus(Duration).
     */
    private static void changingDateAndTimes() {
        System.out.println("\n-- Changing date and times --");

        //create some DateTime object (for example now())
        LocalDateTime ldt1 = LocalDateTime.now();
        System.out.println("ldt1 = " + ldt1);//ldt1 = 2025-01-17T20:05:20.336434700

        //plus and minus methods
        //days (altering year if days more added for the current year)
        LocalDateTime ldt2 = ldt1.plusDays(10);
        System.out.println("ldt2 = " + ldt2);//ldt2 = 2025-01-27T20:06:14.713945900 (10 days were added to ldt1)

        //minutes (altering day in assumption by adding minutes)
        LocalDateTime ldt3 = ldt1.plusMinutes(10);
        System.out.println("ldt3 = " + ldt3);//ldt3 = 2025-01-17T20:18:56.332454500 (were added 10 minutes to ldt1)
        //minus method works the same way in opposite

        //with methods
        //to specify a month for ldt1 (let say we need June)
        LocalDateTime ldt4 = ldt1.withMonth(6);
        System.out.println("ldt4 = " + ldt4);

        //plus and minus methods who working with period and duration:
        //Period: plus week for example
        LocalDateTime plusWeekTo_ldt1 = ldt1.plus(Period.ofWeeks(1));
        System.out.println("plusWeekTo_ldt1 = " + plusWeekTo_ldt1);//plusWeekTo_ldt1 = 2025-01-24T20:25:28.781910800 (week were added)

        //subtract for 10 hours
        LocalDateTime minus10HoursFrom_ldt1 = ldt1.minus(Duration.ofHours(10));
        System.out.println("minus10HoursFrom_ldt1 = " + minus10HoursFrom_ldt1);//minus10HoursFrom_ldt1 = 2025-01-17T10:29:53.707296 (-10 hours were performed)
    }
}