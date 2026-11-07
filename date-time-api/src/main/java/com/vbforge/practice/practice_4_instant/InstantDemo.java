// IMPLEMENTATION
package com.vbforge.practice.practice_4_instant;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Demonstrates {@link Instant} — a point on the UTC timeline.
 *
 * Key concepts:
 *   - Instant represents machine time: seconds + nanoseconds since the Unix epoch
 *     (1970-01-01T00:00:00Z). It has NO timezone, NO date, NO time-of-day.
 *   - Use Instant for logging, auditing, storing timestamps in databases.
 *   - Instant is immutable and thread-safe.
 *
 * Epoch:
 *   - Instant.EPOCH  → 1970-01-01T00:00:00Z  (epoch second = 0)
 *   - Instant.MIN    → far past  (~year -1_000_000_000)
 *   - Instant.MAX    → far future (~year +1_000_000_000)
 *
 * Arithmetic:
 *   - plus/minus with ChronoUnit (SECONDS, MILLIS, NANOS, DAYS, etc.)
 *   - Duration arithmetic is handled in DurationPeriodDemo
 *
 * Comparison:
 *   - isBefore(Instant), isAfter(Instant), equals(Object)
 *   - compareTo(Instant)
 */
public class InstantDemo {

    public static void main(String[] args) {
        System.out.println("=== Instant Demo ===\n");

        // --- 1. Current instant (UTC wall-clock) ---
        Instant now = Instant.now();
        System.out.println("now()           : " + now);
        System.out.println("epochSecond     : " + now.getEpochSecond());
        System.out.println("nano adjustment : " + now.getNano());

        // --- 2. From epoch seconds / millis ---
        Instant fromEpoch = Instant.ofEpochSecond(0);          // Unix epoch
        Instant fromMillis = Instant.ofEpochMilli(1_000_000_000_000L); // ~2001-09-09
        System.out.println("\nfromEpochSecond(0)         : " + fromEpoch);
        System.out.println("fromEpochMilli(1e12)       : " + fromMillis);

        // ofEpochSecond(seconds, nanoAdjust) — nanoAdjust can be > 999_999_999
        Instant withNano = Instant.ofEpochSecond(1L, 500_000_000L); // +0.5 s
        System.out.println("ofEpochSecond(1, 500M ns)  : " + withNano);

        // --- 3. Parsing from ISO-8601 string ---
        Instant parsed = Instant.parse("2024-06-15T12:00:00Z");
        System.out.println("\nparsed                     : " + parsed);

        // --- 4. Arithmetic — adding / subtracting ---
        Instant plus1Hour  = now.plus(1, ChronoUnit.HOURS);
        Instant minus3Days = now.minus(3, ChronoUnit.DAYS);
        Instant plusMillis = now.plusMillis(500);
        System.out.println("\nnow + 1 hour               : " + plus1Hour);
        System.out.println("now - 3 days               : " + minus3Days);
        System.out.println("now + 500 ms               : " + plusMillis);

        // --- 5. Truncation ---
        // Useful when you want second-precision or millisecond-precision timestamps
        Instant truncatedToSec  = now.truncatedTo(ChronoUnit.SECONDS);
        Instant truncatedToDay  = now.truncatedTo(ChronoUnit.DAYS);
        System.out.println("\ntruncated to SECONDS       : " + truncatedToSec);
        System.out.println("truncated to DAYS          : " + truncatedToDay);

        // --- 6. Comparison ---
        Instant a = Instant.parse("2024-01-01T00:00:00Z");
        Instant b = Instant.parse("2024-06-01T00:00:00Z");
        System.out.println("\na.isBefore(b)              : " + a.isBefore(b));
        System.out.println("a.isAfter(b)               : " + a.isAfter(b));
        System.out.println("a.equals(a)                : " + a.equals(a));
        System.out.println("a.compareTo(b) < 0         : " + (a.compareTo(b) < 0));

        // --- 7. Constants ---
        System.out.println("\nInstant.EPOCH              : " + Instant.EPOCH);
        System.out.println("Instant.MIN                : " + Instant.MIN);
        System.out.println("Instant.MAX                : " + Instant.MAX);
    }
}