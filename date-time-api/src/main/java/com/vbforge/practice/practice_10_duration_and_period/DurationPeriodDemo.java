// IMPLEMENTATION
package com.vbforge.practice.practice_10_duration_and_period;

import java.time.*;
import java.time.temporal.ChronoUnit;

/**
 * Demonstrates {@link Duration} and {@link Period} — amounts of time.
 *
 * Key difference:
 * ┌─────────┬───────────────────────────────┬──────────────────────────────────┐
 * │         │ Duration                      │ Period                           │
 * ├─────────┼───────────────────────────────┼──────────────────────────────────┤
 * │ Models  │ machine time (seconds+nanos)  │ calendar time (years/months/days)│
 * │ Use for │ Instant, LocalTime, elapsed   │ LocalDate, birthday, anniversary │
 * │ DST     │ unaffected (absolute seconds) │ respects calendar rules          │
 * │ Factory │ of*, between(Instant/LT/LDT) │ of*, between(LocalDate)          │
 * └─────────┴───────────────────────────────┴──────────────────────────────────┘
 *
 * Duration arithmetic:
 *   - plus/minus Duration, toSeconds, toMinutes, toHours, toDays
 *   - negated(), abs(), multipliedBy(long), dividedBy(long)
 *
 * Period arithmetic:
 *   - plus/minus Period
 *   - getYears(), getMonths(), getDays() — NOT total; these are individual units
 *   - toTotalMonths() — years*12 + months
 *
 * Pitfall:
 *   - Period.getDays() returns only the days component, NOT total days.
 *     Use ChronoUnit.DAYS.between() for total days.
 */
public class DurationPeriodDemo {

    public static void main(String[] args) {
        System.out.println("=== Duration & Period Demo ===\n");

        // ─── DURATION ───────────────────────────────────────────────────────

        // --- 1. Creation ---
        Duration d1 = Duration.ofHours(2);
        Duration d2 = Duration.ofMinutes(90);
        Duration d3 = Duration.of(45, ChronoUnit.SECONDS);
        Duration d4 = Duration.parse("PT1H30M");        // ISO-8601 duration

        System.out.println("ofHours(2)      : " + d1);
        System.out.println("ofMinutes(90)   : " + d2);
        System.out.println("of(45, SECONDS) : " + d3);
        System.out.println("parse PT1H30M   : " + d4);

        // --- 2. Between Instants ---
        Instant start = Instant.parse("2024-06-15T10:00:00Z");
        Instant end   = Instant.parse("2024-06-15T13:45:30Z");
        Duration between = Duration.between(start, end);
        System.out.println("\nDuration.between instants : " + between);
        System.out.println("toSeconds  : " + between.toSeconds());
        System.out.println("toMinutes  : " + between.toMinutes());
        System.out.println("toHours    : " + between.toHours());

        // --- 3. Between LocalDateTimes ---
        LocalDateTime ldt1 = LocalDateTime.of(2024, 6, 15, 8, 0);
        LocalDateTime ldt2 = LocalDateTime.of(2024, 6, 15, 17, 30);
        Duration workDay = Duration.between(ldt1, ldt2);
        System.out.println("\nwork day duration : " + workDay + " = " + workDay.toHours() + "h " + workDay.toMinutesPart() + "m");

        // --- 4. Arithmetic ---
        Duration doubled   = d1.multipliedBy(2);
        Duration half      = d1.dividedBy(2);
        Duration negated   = d1.negated();
        Duration absolute  = negated.abs();
        System.out.println("\ndoubled  : " + doubled);
        System.out.println("half     : " + half);
        System.out.println("negated  : " + negated);
        System.out.println("abs      : " + absolute);

        // --- 5. Apply to a temporal ---
        Instant later = start.plus(d1);
        System.out.println("\nstart + 2h : " + later);

        // ─── PERIOD ─────────────────────────────────────────────────────────

        System.out.println("\n=== Period ===\n");

        // --- 6. Creation ---
        Period p1 = Period.ofYears(1);
        Period p2 = Period.ofMonths(6);
        Period p3 = Period.ofDays(10);
        Period p4 = Period.of(1, 6, 10);           // 1y 6m 10d
        Period p5 = Period.parse("P1Y6M10D");

        System.out.println("ofYears(1)     : " + p1);
        System.out.println("ofMonths(6)    : " + p2);
        System.out.println("ofDays(10)     : " + p3);
        System.out.println("of(1,6,10)     : " + p4);
        System.out.println("parse P1Y6M10D : " + p5);

        // --- 7. Between LocalDates ---
        LocalDate birthday = LocalDate.of(1990, 5, 20);
        LocalDate today    = LocalDate.of(2024, 6, 15);
        Period age = Period.between(birthday, today);
        System.out.println("\nage : " + age.getYears() + " years, "
                + age.getMonths() + " months, " + age.getDays() + " days");
        System.out.println("toTotalMonths: " + age.toTotalMonths());

        // --- 8. Arithmetic ---
        Period p6 = p4.plus(Period.ofMonths(3));   // 1y 9m 10d
        Period p7 = p4.minus(Period.ofDays(5));    // 1y 6m 5d
        System.out.println("\np4 + 3m  : " + p6);
        System.out.println("p4 - 5d  : " + p7);

        // --- 9. Apply to a LocalDate ---
        LocalDate future = today.plus(p4);
        System.out.println("\ntoday + P1Y6M10D : " + future);

        // --- 10. Pitfall: getDays() is NOT total days ---
        System.out.println("\n-- Pitfall: Period.getDays() vs ChronoUnit.DAYS.between --");
        Period p8 = Period.between(
                LocalDate.of(2024, 1, 1), LocalDate.of(2024, 2, 15));
        System.out.println("p8.getDays()                    : " + p8.getDays());           // 14
        System.out.println("p8.getMonths()                  : " + p8.getMonths());         // 1
        System.out.println("ChronoUnit.DAYS.between(...)    : " +
                ChronoUnit.DAYS.between(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 2, 15))); // 45

        // --- 11 .. 12: extra scenes ---
        durationFactoriesAndBetweenLocalTime();
        periodBasicsAndBetweenNow();
    }

    /**
     * Duration created by ChronoUnit, by specific factory methods, by between(LocalTime, LocalTime),
     * and the "how is a Duration created in Java 8?" question.
     */
    private static void durationFactoriesAndBetweenLocalTime() {
        System.out.println("\n-- Duration: factories and between(LocalTime) --");

        Duration d1 = Duration.of(1, ChronoUnit.DAYS);
        Duration d2 = Duration.of(2, ChronoUnit.DAYS);
        System.out.println("d1 = " + d1);//d1 = PT24H
        System.out.println("d2 = " + d2);//d2 = PT48H

        //create Duration by a specific method:
        Duration d3 = Duration.ofHours(5);
        System.out.println("d3 = " + d3);//d3 = PT5H

        //duration and between two times:
        LocalTime ltNow = LocalTime.now();
        LocalTime lt15 = LocalTime.of(15, 0);
        Duration durationBetween = Duration.between(ltNow, lt15);
        System.out.println("durationBetween = " + durationBetween);


        //How is a Duration object created in Java 8?
        Duration oneHour1 = Duration.ofHours(1);
        Duration oneHour2 = Duration. ofMinutes(60);
//        Duration oneHour3 = new Duration("PT1H");
        Duration oneHour4 = Duration.parse("PT1H");

        System.out.println("oneHour1 = " + oneHour1);
        System.out.println("oneHour2 = " + oneHour2);
//        System.out.println("oneHour3 = " + oneHour3);
        System.out.println("oneHour4 = " + oneHour4);

        /*Correct:
        In Java 8, an object of the Duration class can be created using the static methods ofHours(), ofMinutes(), parse(), etc.
        You cannot create an object of the Duration class using a constructor.*/
    }

    /**
     * Period.of(y, m, d), ofDays, and between(startOfYear, now).
     * The getDays() pitfall is shown in scene 10 above.
     */
    private static void periodBasicsAndBetweenNow() {
        System.out.println("\n-- Period: basics and between(…, now) --");

        Period per1 = Period.of(1, 1, 1);
        System.out.println("per1 = " + per1); //per1 = P1Y1M1D

        //period persisting only by days:
        Period per2 = Period.ofDays(2);
        System.out.println("per2 = " + per2); //per2 = P2D

        //2 local dates and between()
        LocalDate ld2 = LocalDate.of(2024, 1, 1);
        LocalDate ld1 = LocalDate.now();
        Period per3 = Period.between(ld2, ld1);
        System.out.println("per3 = " + per3); //calculate amount of days from now to start of the year2024 example: per3 = P1Y16D

        //per3.getDays() does not work correct for total days (skips months and even years);
        //use ChronoUnit.DAYS.between(ld2, ld1) instead — see scene 10
    }
}