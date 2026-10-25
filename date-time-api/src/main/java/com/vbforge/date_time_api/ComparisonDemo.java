package com.vbforge.date_time_api;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * ComparisonDemo — annotated reference for comparing date-time values: ordering, equality,
 * sorting and range checks.
 *
 * <p>Read top-to-bottom. Each section covers one idea with concrete examples and a note
 * explaining <em>why</em> the behaviour is what it is.
 *
 * <p>Topics:
 * <ol>
 *   <li>isBefore / isAfter / isEqual / equals / compareTo on local types</li>
 *   <li>Zoned values — same moment vs same fields, and the wall-clock trap</li>
 *   <li>Sorting and extremes — natural order, reversed order, min/max</li>
 *   <li>Ranges — inclusive vs half-open, and the overlap test</li>
 * </ol>
 */
public class ComparisonDemo {

    private static final ZoneId PARIS = ZoneId.of("Europe/Paris");
    private static final ZoneId NEW_YORK = ZoneId.of("America/New_York");
    private static final ZoneId TOKYO = ZoneId.of("Asia/Tokyo");

    public static void main(String[] args) {
        beforeAfterEqualsDemo();
        System.out.println("---");
        zonedEqualityDemo();
        System.out.println("---");
        sortingAndExtremesDemo();
        System.out.println("---");
        rangesDemo();
    }

    // ── 1. isBefore / isAfter / equals / compareTo ────────────────────────────

    /**
     * Every java.time type that has an order offers {@code isBefore}, {@code isAfter},
     * {@code equals} and {@code compareTo}.
     *
     * <p>WHY this matters: prefer the readable {@code a.isBefore(b)} over
     * {@code a.compareTo(b) < 0}. All of them compare <em>values</em> (never use {@code ==}),
     * and all of them are O(1).
     *
     * <p>Gotchas:
     * <ul>
     *   <li>{@code compareTo} returns a negative number, zero or a positive number — the exact
     *       value (it can be 5, not just 1) is not part of the contract. Test its sign only.</li>
     *   <li>You can only compare values of the <em>same</em> type: a {@code LocalDate} and a
     *       {@code LocalDateTime} do not compile together. Convert first.</li>
     *   <li>For "is this the same calendar day?" compare {@code toLocalDate()} values, not the date-times.</li>
     * </ul>
     */
    static void beforeAfterEqualsDemo() {
        System.out.println("=== isBefore / isAfter / equals / compareTo ===");

        LocalDate earlier = LocalDate.of(2024, 3, 10);
        LocalDate later = LocalDate.of(2024, 3, 15);

        System.out.println("isBefore             : " + earlier.isBefore(later));           // true
        System.out.println("isAfter              : " + earlier.isAfter(later));            // false
        System.out.println("isEqual              : " + earlier.isEqual(later));            // false
        System.out.println("equals (same value)  : " + later.equals(LocalDate.parse("2024-03-15"))); // true
        System.out.println("compareTo sign       : " + Integer.signum(earlier.compareTo(later)));    // -1
        System.out.println("compareTo equal      : " + later.compareTo(LocalDate.of(2024, 3, 15)));  // 0

        // Same API on time and date-time
        LocalTime nine = LocalTime.of(9, 0);
        LocalTime five = LocalTime.of(17, 0);
        System.out.println("09:00 before 17:00   : " + nine.isBefore(five));                // true

        LocalDateTime a = LocalDateTime.of(2024, 3, 15, 23, 59);
        LocalDateTime b = LocalDateTime.of(2024, 3, 16, 0, 1);
        System.out.println("23:59 before 00:01+1d: " + a.isBefore(b));                      // true
        System.out.println("same calendar day?   : " + a.toLocalDate().equals(b.toLocalDate())); // false

        // Equality is exact: 10:00 and 10:00:00.000000001 are different values
        LocalDateTime tenSharp = LocalDateTime.of(2024, 3, 15, 10, 0);
        System.out.println("equal to the nano?   : " + tenSharp.equals(tenSharp.plusNanos(1)));  // false
    }

    // ── 2. Zoned equality ─────────────────────────────────────────────────────

    /**
     * Two zoned values can describe the <em>same moment</em> while having different fields:
     * 12:00 in Paris and 06:00 in New York (in January) are one instant.
     *
     * <p>WHY this matters: the three comparison questions have three different answers:
     * <ul>
     *   <li>{@code isEqual / isBefore / isAfter} — compare the point on the timeline</li>
     *   <li>{@code equals} — compares <em>everything</em> (local date-time, offset and zone)</li>
     *   <li>{@code compareTo} — instant first, then local date-time, then zone id; it is
     *       consistent with {@code equals}, so it is non-zero here even though {@code isEqual} is true</li>
     * </ul>
     *
     * <p>Gotcha — the wall-clock trap: comparing {@code toLocalDateTime()} of two zoned values
     * ignores the zones and gives a wrong "chronological" answer.
     */
    static void zonedEqualityDemo() {
        System.out.println("=== Zoned equality ===");

        ZonedDateTime paris = ZonedDateTime.of(2024, 1, 15, 12, 0, 0, 0, PARIS);       // 11:00Z
        ZonedDateTime newYork = ZonedDateTime.of(2024, 1, 15, 6, 0, 0, 0, NEW_YORK);   // 11:00Z

        System.out.println("paris                : " + paris);     // 2024-01-15T12:00+01:00[Europe/Paris]
        System.out.println("newYork              : " + newYork);   // 2024-01-15T06:00-05:00[America/New_York]

        System.out.println("isEqual (instant)    : " + paris.isEqual(newYork));                                 // true
        System.out.println("equals (all fields)  : " + paris.equals(newYork));                                 // false
        System.out.println("instants equal       : " + paris.toInstant().equals(newYork.toInstant()));         // true
        System.out.println("compareTo sign       : " + Integer.signum(paris.compareTo(newYork)));              // 1  (12:00 > 06:00 as tie-break)

        // isBefore / isAfter look at the instant only
        System.out.println("paris before NY+1s   : " + paris.isBefore(newYork.plusSeconds(1)));                // true

        // THE TRAP — wall-clock comparison disagrees with reality
        System.out.println("wall clock isAfter   : " + paris.toLocalDateTime().isAfter(newYork.toLocalDateTime())); // true  (WRONG: same moment!)

        // OffsetDateTime behaves the same way
        OffsetDateTime a = OffsetDateTime.of(2024, 1, 15, 12, 0, 0, 0, ZoneOffset.ofHours(1));
        OffsetDateTime b = OffsetDateTime.of(2024, 1, 15, 11, 0, 0, 0, ZoneOffset.UTC);
        System.out.println("offset isEqual       : " + a.isEqual(b));    // true
        System.out.println("offset equals        : " + a.equals(b));     // false
    }

    // ── 3. Sorting and extremes ───────────────────────────────────────────────

    /**
     * All date-time types are {@link Comparable}, so {@code Collections.sort},
     * {@code List.sort}, {@code Collections.min/max} and stream {@code sorted()/max()} work
     * out of the box.
     *
     * <p>WHY this matters: sorting events is everyday work. Natural order of a
     * {@code ZonedDateTime} is chronological (instant first), so a mixed-zone list sorts correctly —
     * while sorting by wall-clock fields gives a wrong order.
     *
     * <p>Complexity: sorting is O(n log n); {@code min}/{@code max} are O(n).
     */
    static void sortingAndExtremesDemo() {
        System.out.println("=== Sorting and extremes ===");

        List<LocalDate> dates = new ArrayList<>(Arrays.asList(
                LocalDate.of(2024, 3, 15),
                LocalDate.of(2023, 12, 31),
                LocalDate.of(2024, 1, 1)));

        Collections.sort(dates);                                      // natural order = oldest first
        System.out.println("ascending            : " + dates);        // [2023-12-31, 2024-01-01, 2024-03-15]

        dates.sort(Comparator.reverseOrder());                        // newest first
        System.out.println("descending           : " + dates);        // [2024-03-15, 2024-01-01, 2023-12-31]

        System.out.println("min                  : " + Collections.min(dates));  // 2023-12-31
        System.out.println("max                  : " + Collections.max(dates));  // 2024-03-15

        // Mixed zones: instants are 09:30Z (New York), 10:30Z (Tokyo), 11:00Z (Paris)
        List<ZonedDateTime> moments = new ArrayList<>(Arrays.asList(
                ZonedDateTime.of(2024, 1, 15, 12, 0, 0, 0, PARIS),
                ZonedDateTime.of(2024, 1, 15, 4, 30, 0, 0, NEW_YORK),
                ZonedDateTime.of(2024, 1, 15, 19, 30, 0, 0, TOKYO)));

        List<ZonedDateTime> chronological = new ArrayList<>(moments);
        Collections.sort(chronological);                              // by instant
        System.out.println("chronological        : " + toZones(chronological));  // [America/New_York, Asia/Tokyo, Europe/Paris]

        List<ZonedDateTime> byWallClock = new ArrayList<>(moments);
        byWallClock.sort(Comparator.comparing(ZonedDateTime::toLocalDateTime));  // WRONG for mixed zones
        System.out.println("by wall clock (bug)  : " + toZones(byWallClock));     // [America/New_York, Europe/Paris, Asia/Tokyo]
    }

    private static List<String> toZones(List<ZonedDateTime> list) {
        List<String> zones = new ArrayList<>();
        for (ZonedDateTime z : list) {
            zones.add(z.getZone().getId());
        }
        return zones;
    }

    // ── 4. Ranges ─────────────────────────────────────────────────────────────

    /**
     * There is no built-in "is between" method — you build it from {@code isBefore}/{@code isAfter}.
     * Decide first whether the end of the range belongs to it:
     * <ul>
     *   <li><b>Closed</b> {@code [start, end]} — both ends included (e.g. "campaign runs Mar 1 to Mar 31")</li>
     *   <li><b>Half-open</b> {@code [start, end)} — end excluded (e.g. time slots, hotel nights)</li>
     * </ul>
     *
     * <p>WHY this matters: half-open ranges tile perfectly. {@code [9:00,10:00)} followed by
     * {@code [10:00,11:00)} share no instant and leave no gap, so back-to-back bookings do not
     * "overlap". With closed ranges 10:00 would belong to both.
     *
     * <p>Overlap test for half-open ranges: {@code startA < endB && startB < endA}.
     */
    static void rangesDemo() {
        System.out.println("=== Ranges ===");

        // Closed range on dates
        LocalDate start = LocalDate.of(2024, 3, 1);
        LocalDate end = LocalDate.of(2024, 3, 31);
        LocalDate probe = LocalDate.of(2024, 3, 31);
        boolean inClosed = !probe.isBefore(start) && !probe.isAfter(end);
        System.out.println("Mar 31 in [Mar1,Mar31]  : " + inClosed);         // true

        // Half-open range on times
        LocalTime open = LocalTime.of(9, 0);
        LocalTime close = LocalTime.of(17, 0);
        LocalTime t = LocalTime.of(17, 0);
        boolean inHalfOpen = !t.isBefore(open) && t.isBefore(close);
        System.out.println("17:00 in [09:00,17:00)  : " + inHalfOpen);       // false

        // Overlap of half-open slots
        LocalDateTime nine = LocalDateTime.of(2024, 3, 15, 9, 0);
        LocalDateTime ten = LocalDateTime.of(2024, 3, 15, 10, 0);
        LocalDateTime eleven = LocalDateTime.of(2024, 3, 15, 11, 0);
        LocalDateTime twelve = LocalDateTime.of(2024, 3, 15, 12, 0);

        System.out.println("9-10 vs 10-11 overlap   : " + overlaps(nine, ten, ten, eleven));       // false (touching)
        System.out.println("9-11 vs 10-12 overlap   : " + overlaps(nine, eleven, ten, twelve));    // true
        System.out.println("9-12 vs 10-11 overlap   : " + overlaps(nine, twelve, ten, eleven));    // true  (contained)
    }

    private static boolean overlaps(LocalDateTime startA, LocalDateTime endA,
                                    LocalDateTime startB, LocalDateTime endB) {
        return startA.isBefore(endB) && startB.isBefore(endA);
    }
}
