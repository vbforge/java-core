package com.vbforge.date_time_api;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * InstantTasks — implement every method below to make {@code InstantTasksTest} go GREEN.
 *
 * <p><b>Workflow:</b>
 * <ol>
 *   <li>Read {@code InstantDemo.java} to understand the relevant concept.</li>
 *   <li>Implement the method — remove the {@code UnsupportedOperationException}.</li>
 *   <li>Run {@code mvn test -Dtest=InstantTasksTest} and watch it turn GREEN ✅</li>
 * </ol>
 *
 * <p>Each method has a Javadoc describing exactly what to implement. Do not change
 * method signatures or add external libraries — only the Java standard library is needed.
 */
public class InstantTasks {

    // ── Task 1 ─────────────────────────────────────────────────────────────────

    /**
     * Converts a number of <em>milliseconds</em> since the epoch into the standard
     * ISO-8601 UTC text of that moment (the {@code toString()} form of an {@link Instant}).
     *
     * <p>Examples:
     * <pre>
     *   epochMillisToIso(0)                  → "1970-01-01T00:00:00Z"
     *   epochMillisToIso(1_700_000_000_000)  → "2023-11-14T22:13:20Z"
     *   epochMillisToIso(1_700_000_000_123)  → "2023-11-14T22:13:20.123Z"
     *   epochMillisToIso(-1000)              → "1969-12-31T23:59:59Z"
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>There is a factory method on {@link Instant} that takes milliseconds.</li>
     *   <li>No formatter is needed — the default text form is already what you want.</li>
     * </ul>
     *
     * @param epochMillis milliseconds since 1970-01-01T00:00:00Z (may be negative)
     * @return ISO-8601 UTC text, ending in {@code Z}
     */
    public String epochMillisToIso(long epochMillis) {
        throw new UnsupportedOperationException("TODO: implement epochMillisToIso");
    }

    // ── Task 2 ─────────────────────────────────────────────────────────────────

    /**
     * Decides whether a token issued at {@code issuedAt} with the given {@code timeToLive}
     * has expired at moment {@code now}. A token expires <strong>strictly after</strong>
     * {@code issuedAt + timeToLive}: at exactly that moment it is still valid.
     *
     * <p>Examples:
     * <pre>
     *   issuedAt=10:00:00Z  ttl=PT1H  now=10:59:59Z  → false
     *   issuedAt=10:00:00Z  ttl=PT1H  now=11:00:00Z  → false   (exactly on the boundary)
     *   issuedAt=10:00:00Z  ttl=PT1H  now=11:00:01Z  → true
     *   issuedAt=10:00:00Z  ttl=PT0S  now=10:00:00Z  → false
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Compute the expiry Instant first, then compare it with {@code now}.</li>
     *   <li>Note the method receives {@code now} as a parameter — it must not call
     *       {@code Instant.now()} itself. That is what makes it testable.</li>
     * </ul>
     *
     * @param issuedAt   the moment the token was issued
     * @param timeToLive how long the token stays valid
     * @param now        the current moment
     * @return {@code true} if {@code now} is strictly after the expiry moment
     */
    public boolean isExpired(Instant issuedAt, Duration timeToLive, Instant now) {
        throw new UnsupportedOperationException("TODO: implement isExpired");
    }

    // ── Task 3 ─────────────────────────────────────────────────────────────────

    /**
     * Returns the latest (most recent) moment in {@code instants}, or an empty
     * {@link Optional} when the list is empty. The input list must not be modified
     * and may be in any order.
     *
     * <p>Examples:
     * <pre>
     *   latest([10:00Z, 12:00Z, 11:00Z])  → Optional[12:00Z]
     *   latest([10:00Z])                  → Optional[10:00Z]
     *   latest([10:00Z, 10:00Z])          → Optional[10:00Z]
     *   latest([])                        → Optional.empty
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@link Instant} implements {@link Comparable}, so natural ordering already means
     *       "earlier is smaller".</li>
     *   <li>Do not sort the caller's list — it may be immutable.</li>
     * </ul>
     *
     * @param instants the moments to examine (may be empty, never {@code null})
     * @return the latest moment, or {@link Optional#empty()}
     */
    public Optional<Instant> latest(List<Instant> instants) {
        throw new UnsupportedOperationException("TODO: implement latest");
    }

    // ── Task 4 ─────────────────────────────────────────────────────────────────

    /**
     * Returns the moment that is exactly {@code days} <strong>24-hour days</strong> after
     * (or before, if negative) {@code instant}. No calendar or time zone is involved.
     *
     * <p>Examples:
     * <pre>
     *   addDays(2024-03-15T10:15:30Z,  1)  → 2024-03-16T10:15:30Z
     *   addDays(2024-02-28T00:00:00Z,  2)  → 2024-03-01T00:00:00Z   (2024 is a leap year)
     *   addDays(2024-03-15T10:15:30Z, -15) → 2024-02-29T10:15:30Z
     *   addDays(2024-03-15T10:15:30Z,  0)  → 2024-03-15T10:15:30Z
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>An {@link Instant} supports units up to {@code DAYS} but not {@code MONTHS}
     *       or {@code YEARS}. Check which {@code plus} overload accepts a unit.</li>
     * </ul>
     *
     * @param instant the starting moment
     * @param days    number of 24-hour days to add (may be negative)
     * @return a new {@link Instant}
     */
    public Instant addDays(Instant instant, long days) {
        throw new UnsupportedOperationException("TODO: implement addDays");
    }

    // ── Task 5 ─────────────────────────────────────────────────────────────────

    /**
     * Cuts off everything smaller than a minute: seconds and nanoseconds become zero.
     * Used, for example, to bucket events into per-minute counters.
     *
     * <p>Examples:
     * <pre>
     *   truncateToMinute(2024-03-15T10:15:30.987Z)  → 2024-03-15T10:15:00Z
     *   truncateToMinute(2024-03-15T10:15:59Z)      → 2024-03-15T10:15:00Z
     *   truncateToMinute(2024-03-15T10:15:00Z)      → 2024-03-15T10:15:00Z   (already aligned)
     *   truncateToMinute(1969-12-31T23:59:59Z)      → 1969-12-31T23:59:00Z   (before the epoch)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>One method on {@link Instant} does this when given a {@code ChronoUnit}.</li>
     * </ul>
     *
     * @param instant any moment
     * @return the start of the minute that contains {@code instant}
     */
    public Instant truncateToMinute(Instant instant) {
        throw new UnsupportedOperationException("TODO: implement truncateToMinute");
    }
}
