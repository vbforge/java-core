// IMPLEMENTATION
package com.vbforge.practice.practice_6_localdate;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;

/**
 * Demonstrates {@link LocalDate} — a date without time or timezone.
 *
 * Key concepts:
 *   - LocalDate stores year, month, day only. No hours, no timezone.
 *   - Immutable and thread-safe.
 *   - Ideal for birthdays, holidays, business dates.
 *
 * Factory methods:
 *   - LocalDate.now()              — current date from system clock
 *   - LocalDate.of(year, month, day)
 *   - LocalDate.parse("2024-06-15") — ISO-8601 (yyyy-MM-dd)
 *
 * Arithmetic (all return new instance):
 *   - plus/minusDays, Weeks, Months, Years
 *   - plus(long, ChronoUnit)
 *
 * Adjusters:
 *   - TemporalAdjusters.firstDayOfMonth(), lastDayOfYear(), next(DayOfWeek), ...
 *
 * Queries:
 *   - isLeapYear(), getDayOfWeek(), getDayOfYear(), lengthOfMonth()
 *   - isBefore(), isAfter(), isEqual()
 */
public class LocalDateDemo {

    public static void main(String[] args) {
        System.out.println("=== LocalDate Demo ===\n");

        // --- 1. Creation ---
        LocalDate today    = LocalDate.now();
        LocalDate specific = LocalDate.of(2024, Month.JUNE, 15);
        LocalDate parsed   = LocalDate.parse("2000-01-01");

        System.out.println("today    : " + today);
        System.out.println("specific : " + specific);
        System.out.println("parsed   : " + parsed);

        // --- 2. Field access ---
        System.out.println("\n-- Fields --");
        System.out.println("year         : " + specific.getYear());
        System.out.println("month        : " + specific.getMonth());        // enum JUNE
        System.out.println("monthValue   : " + specific.getMonthValue());   // 6
        System.out.println("dayOfMonth   : " + specific.getDayOfMonth());
        System.out.println("dayOfWeek    : " + specific.getDayOfWeek());    // SATURDAY
        System.out.println("dayOfYear    : " + specific.getDayOfYear());
        System.out.println("lengthOfMonth: " + specific.lengthOfMonth());
        System.out.println("isLeapYear   : " + specific.isLeapYear());

        // --- 3. Arithmetic ---
        System.out.println("\n-- Arithmetic --");
        System.out.println("+ 10 days   : " + specific.plusDays(10));
        System.out.println("- 2 months  : " + specific.minusMonths(2));
        System.out.println("+ 1 year    : " + specific.plusYears(1));
        System.out.println("+ 45 days   : " + specific.plus(45, ChronoUnit.DAYS));

        // --- 4. TemporalAdjusters ---
        System.out.println("\n-- TemporalAdjusters --");
        System.out.println("firstDayOfMonth  : " + specific.with(TemporalAdjusters.firstDayOfMonth()));
        System.out.println("lastDayOfMonth   : " + specific.with(TemporalAdjusters.lastDayOfMonth()));
        System.out.println("firstDayOfYear   : " + specific.with(TemporalAdjusters.firstDayOfYear()));
        System.out.println("lastDayOfYear    : " + specific.with(TemporalAdjusters.lastDayOfYear()));
        System.out.println("next MONDAY      : " + specific.with(TemporalAdjusters.next(DayOfWeek.MONDAY)));
        System.out.println("prev FRIDAY      : " + specific.with(TemporalAdjusters.previous(DayOfWeek.FRIDAY)));
        System.out.println("firstInMonth MON : " + specific.with(TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY)));

        // --- 5. Difference between dates ---
        System.out.println("\n-- ChronoUnit.between() ---");
        LocalDate start = LocalDate.of(2020, 1, 1);
        LocalDate end   = LocalDate.of(2024, 6, 15);
        System.out.println("days between    : " + ChronoUnit.DAYS.between(start, end));
        System.out.println("months between  : " + ChronoUnit.MONTHS.between(start, end));
        System.out.println("years between   : " + ChronoUnit.YEARS.between(start, end));

        // --- 6. Comparison ---
        System.out.println("\n-- Comparison --");
        LocalDate a = LocalDate.of(2023, 1, 1);
        LocalDate b = LocalDate.of(2024, 1, 1);
        System.out.println("a.isBefore(b) : " + a.isBefore(b));
        System.out.println("b.isAfter(a)  : " + b.isAfter(a));
        System.out.println("a.isEqual(a)  : " + a.isEqual(a));

        // --- 7. Edge cases ---
        System.out.println("\n-- Edge cases --");
        LocalDate leapDay  = LocalDate.of(2024, 2, 29); // 2024 is a leap year
        LocalDate nextYear = leapDay.plusYears(1);       // 2025-02-28, not 29
        System.out.println("2024-02-29 + 1 year : " + nextYear);

        // --- 8. Current date (simplest form) ---
        System.out.println("\n-- Current date --");
        LocalDate myObj = LocalDate.now(); // Create a date object
        System.out.println(myObj); // Display the current date

        // --- 9. More creation examples ---
        System.out.println("\n-- More creation examples --");
        LocalDate ld1 = LocalDate.now();
        LocalDate ld2 = LocalDate.of(2025, 1, 16);
        LocalDate ld3 = LocalDate.of(2025, Month.JUNE, 2);

        System.out.println("date");
        System.out.println(ld1);
        System.out.println(ld2);
        System.out.println(ld3 + "\n");

        //no compile error:
        //LocalDate invalidMonth = LocalDate.of(2022, 13, 12);
        //13 month not existing: DateTimeException: Invalid value for MonthOfYear (valid values 1 - 12): 13

        System.out.println(LocalDate.of(2023, 02, 10).plusDays(1));

        int monthValue = LocalDate.parse("2023-02-10").getMonthValue();
        System.out.println(monthValue);
    }
}