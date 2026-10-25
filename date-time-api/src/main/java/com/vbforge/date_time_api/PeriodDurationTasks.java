package com.vbforge.date_time_api;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Period;

/**
 * PeriodDurationTasks — implement every method below to make {@code PeriodDurationTasksTest} go GREEN.
 *
 * <p><b>Workflow:</b>
 * <ol>
 *   <li>Read {@code PeriodDurationDemo.java} to understand the relevant concept.</li>
 *   <li>Implement the method — remove the {@code UnsupportedOperationException}.</li>
 *   <li>Run {@code mvn test -Dtest=PeriodDurationTasksTest} and watch it turn GREEN ✅</li>
 * </ol>
 *
 * <p>Each method has a Javadoc describing exactly what to implement. Do not change
 * method signatures or add external libraries — only the Java standard library is needed.
 */
public class PeriodDurationTasks {

    // ── Task 1 ─────────────────────────────────────────────────────────────────

    /**
     * Returns a person's age in <em>completed</em> years on the day {@code today}.
     * The age increases on the birthday itself. Someone born on 29 February has their
     * birthday on 1 March in non-leap years.
     *
     * <p>Examples:
     * <pre>
     *   ageInYears(1990-05-20, 2024-03-15)  → 33   (birthday not reached yet this year)
     *   ageInYears(1990-05-20, 2024-05-19)  → 33   (the day before the birthday)
     *   ageInYears(1990-05-20, 2024-05-20)  → 34   (on the birthday)
     *   ageInYears(2024-03-15, 2024-03-15)  → 0    (born today)
     *   ageInYears(2000-02-29, 2023-02-28)  → 22
     *   ageInYears(2000-02-29, 2023-03-01)  → 23
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>The gap between the two dates can be split into years, months and days —
     *       look at {@link Period#between}.</li>
     * </ul>
     *
     * @param birthDate the date of birth
     * @param today     the reference date, never before {@code birthDate}
     * @return age in whole years
     */
    public int ageInYears(LocalDate birthDate, LocalDate today) {
        throw new UnsupportedOperationException("TODO: implement ageInYears");
    }

    // ── Task 2 ─────────────────────────────────────────────────────────────────

    /**
     * Returns the total number of days from {@code start} (inclusive) to {@code end}
     * (exclusive). The result is negative when {@code end} is before {@code start}.
     *
     * <p>Examples:
     * <pre>
     *   daysBetween(2024-03-15, 2024-03-20)  → 5
     *   daysBetween(2024-01-01, 2024-03-15)  → 74    (31 + 29 + 14; 2024 is a leap year)
     *   daysBetween(2024-03-20, 2024-03-15)  → -5
     *   daysBetween(2024-03-15, 2024-03-15)  → 0
     *   daysBetween(2023-12-31, 2024-01-01)  → 1     (crosses a year boundary)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code Period.getDays()} returns only the days <em>component</em> — it is the wrong
     *       tool here.</li>
     *   <li>{@link java.time.temporal.ChronoUnit} has a constant for days with a {@code between} method.</li>
     * </ul>
     *
     * @param start first date (inclusive)
     * @param end   second date (exclusive)
     * @return total whole days, negative if {@code end} precedes {@code start}
     */
    public long daysBetween(LocalDate start, LocalDate end) {
        throw new UnsupportedOperationException("TODO: implement daysBetween");
    }

    // ── Task 3 ─────────────────────────────────────────────────────────────────

    /**
     * Formats a non-negative {@link Duration} as {@code HH:mm:ss}. Hours are NOT wrapped at 24
     * and use at least two digits; minutes and seconds are always two digits.
     * Fractions of a second are cut off, not rounded.
     *
     * <p>Examples:
     * <pre>
     *   formatDuration(PT0S)         → "00:00:00"
     *   formatDuration(PT1H2M5S)     → "01:02:05"
     *   formatDuration(PT25H)        → "25:00:00"
     *   formatDuration(PT100H5M)     → "100:05:00"
     *   formatDuration(PT59.999S)    → "00:00:59"
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Start from the total number of whole seconds, then use {@code /} and {@code %}.</li>
     *   <li>{@code String.format("%02d")} pads with zeros. (The convenient {@code toMinutesPart()}
     *       methods only exist from Java 9 on.)</li>
     * </ul>
     *
     * @param duration a duration of zero or more
     * @return text in the form {@code HH:mm:ss}
     */
    public String formatDuration(Duration duration) {
        throw new UnsupportedOperationException("TODO: implement formatDuration");
    }

    // ── Task 4 ─────────────────────────────────────────────────────────────────

    /**
     * Describes a non-negative {@link Period} in plain English: components that are zero are
     * left out, the unit is singular for exactly 1, components are separated by {@code ", "}.
     * The period is described as given — do <em>not</em> normalise it. A completely zero period
     * is described as {@code "0 days"}.
     *
     * <p>Examples:
     * <pre>
     *   describePeriod(P1Y2M3D)  → "1 year, 2 months, 3 days"
     *   describePeriod(P2Y5D)    → "2 years, 5 days"
     *   describePeriod(P1M)      → "1 month"
     *   describePeriod(P1D)      → "1 day"
     *   describePeriod(P14M)     → "14 months"
     *   describePeriod(P0D)      → "0 days"
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Use {@code getYears()}, {@code getMonths()} and {@code getDays()}.</li>
     *   <li>Collect the non-zero parts first, then join them.</li>
     * </ul>
     *
     * @param period a period whose components are all zero or positive
     * @return human-readable description
     */
    public String describePeriod(Period period) {
        throw new UnsupportedOperationException("TODO: implement describePeriod");
    }

    // ── Task 5 ─────────────────────────────────────────────────────────────────

    /**
     * Returns the number of days from {@code today} until the person's next birthday.
     * If today <em>is</em> the birthday the answer is {@code 0}. The birth date is never
     * 29 February, so every year has the birthday.
     *
     * <p>Examples:
     * <pre>
     *   daysUntilNextBirthday(1990-05-20, 2024-03-15)  → 66
     *   daysUntilNextBirthday(1990-05-20, 2024-05-20)  → 0
     *   daysUntilNextBirthday(1990-05-20, 2024-05-21)  → 364   (next one is in 2025)
     *   daysUntilNextBirthday(1990-01-01, 2024-12-31)  → 1     (next one is across the year boundary)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Build this year's birthday by moving the birth date into this year —
     *       there is a {@code with...} method for the year.</li>
     *   <li>If that date is already behind {@code today}, use next year's instead.</li>
     * </ul>
     *
     * @param birthDate the date of birth (not 29 February)
     * @param today     the reference date
     * @return days until the next birthday, {@code 0} if it is today
     */
    public long daysUntilNextBirthday(LocalDate birthDate, LocalDate today) {
        throw new UnsupportedOperationException("TODO: implement daysUntilNextBirthday");
    }
}
