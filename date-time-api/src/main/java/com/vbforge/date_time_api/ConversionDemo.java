package com.vbforge.date_time_api;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * ConversionDemo — annotated reference for converting between {@link Instant},
 * {@link LocalDateTime} and {@link ZonedDateTime} (plus epoch numbers and the legacy
 * {@link Date}).
 *
 * <p>Read top-to-bottom. Each section covers one idea with concrete examples and a note
 * explaining <em>why</em> the behaviour is what it is.
 *
 * <p>The one idea behind every conversion:
 * <pre>
 *   Instant        — a moment (no zone, no calendar)
 *   LocalDateTime  — calendar fields (no zone)          ← needs a zone to become a moment
 *   ZonedDateTime  — moment + zone + calendar fields    ← can become either of the others
 * </pre>
 * Going from a moment to calendar fields, or the other way, <em>always</em> needs a zone.
 *
 * <p>Topics:
 * <ol>
 *   <li>Instant ↔ LocalDateTime — the zone is the missing piece</li>
 *   <li>ZonedDateTime conversions — the full conversion table</li>
 *   <li>Round-trip gotcha — why LocalDateTime can lose information during a DST overlap</li>
 *   <li>Legacy interop — java.util.Date, GregorianCalendar, TimeZone</li>
 *   <li>Epoch numbers — epoch day, epoch seconds</li>
 * </ol>
 */
public class ConversionDemo {

    private static final ZoneId PARIS = ZoneId.of("Europe/Paris");
    private static final ZoneId TOKYO = ZoneId.of("Asia/Tokyo");
    private static final ZoneId NEW_YORK = ZoneId.of("America/New_York");

    public static void main(String[] args) {
        instantAndLocalDateTimeDemo();
        System.out.println("---");
        zonedConversionsDemo();
        System.out.println("---");
        roundTripGotchaDemo();
        System.out.println("---");
        legacyInteropDemo();
        System.out.println("---");
        epochNumbersDemo();
    }

    // ── 1. Instant ↔ LocalDateTime ────────────────────────────────────────────

    /**
     * One {@link Instant} shows up as <em>different</em> {@link LocalDateTime} values in
     * different zones. So there is no zone-free conversion: you must always say which zone's
     * wall clock you mean.
     *
     * <p>WHY this matters: the most common date-time bug is converting with the wrong zone
     * (or the JVM default zone by accident) — the result is off by the UTC offset, and it
     * only shows up on a server in another country.
     *
     * <ul>
     *   <li>Instant → LocalDateTime: {@code LocalDateTime.ofInstant(instant, zone)}</li>
     *   <li>LocalDateTime → Instant: {@code ldt.atZone(zone).toInstant()} or
     *       {@code ldt.toInstant(offset)}</li>
     * </ul>
     */
    static void instantAndLocalDateTimeDemo() {
        System.out.println("=== Instant <-> LocalDateTime ===");

        Instant instant = Instant.parse("2024-03-15T11:00:00Z");

        LocalDateTime utc = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
        LocalDateTime paris = LocalDateTime.ofInstant(instant, PARIS);
        LocalDateTime tokyo = LocalDateTime.ofInstant(instant, TOKYO);

        System.out.println("same instant in UTC   : " + utc);     // 2024-03-15T11:00
        System.out.println("same instant in Paris : " + paris);   // 2024-03-15T12:00
        System.out.println("same instant in Tokyo : " + tokyo);   // 2024-03-15T20:00

        // Back to an Instant — use the SAME zone you used going in
        System.out.println("Tokyo -> Instant      : " + tokyo.atZone(TOKYO).toInstant());           // 2024-03-15T11:00:00Z
        System.out.println("Paris -> Instant      : " + paris.atZone(PARIS).toInstant());           // 2024-03-15T11:00:00Z
        System.out.println("fixed offset -> Instant: " + utc.toInstant(ZoneOffset.UTC));           // 2024-03-15T11:00:00Z

        // GOTCHA — the wrong zone gives a plausible but wrong moment
        System.out.println("Tokyo read as UTC     : " + tokyo.toInstant(ZoneOffset.UTC));          // 2024-03-15T20:00:00Z (9 hours off)
    }

    // ── 2. ZonedDateTime conversions ──────────────────────────────────────────

    /**
     * {@link ZonedDateTime} holds everything, so it converts to the other types without
     * guessing — and it is the bridge in the other direction too.
     *
     * <pre>
     *   Instant       → ZonedDateTime : instant.atZone(zone)
     *   ZonedDateTime → Instant       : zdt.toInstant()
     *   LocalDateTime → ZonedDateTime : ldt.atZone(zone)
     *   ZonedDateTime → LocalDateTime : zdt.toLocalDateTime()      (zone is dropped)
     *   ZonedDateTime → OffsetDateTime: zdt.toOffsetDateTime()     (region is dropped)
     *   ZonedDateTime → other zone    : zdt.withZoneSameInstant(zone)
     * </pre>
     *
     * <p>WHY this matters: each arrow that goes to a type with fewer fields throws information
     * away. Convert to the "smaller" type as late as possible, and only when you are sure
     * you will not need the dropped part.
     */
    static void zonedConversionsDemo() {
        System.out.println("=== ZonedDateTime conversions ===");

        Instant instant = Instant.parse("2024-03-15T11:00:00Z");
        ZonedDateTime paris = instant.atZone(PARIS);

        System.out.println("instant.atZone        : " + paris);                       // 2024-03-15T12:00+01:00[Europe/Paris]
        System.out.println("toInstant             : " + paris.toInstant());           // 2024-03-15T11:00:00Z
        System.out.println("toLocalDateTime       : " + paris.toLocalDateTime());     // 2024-03-15T12:00
        System.out.println("toLocalDate           : " + paris.toLocalDate());         // 2024-03-15
        System.out.println("toOffsetDateTime      : " + paris.toOffsetDateTime());    // 2024-03-15T12:00+01:00

        LocalDateTime local = LocalDateTime.of(2024, 3, 15, 12, 0);
        System.out.println("ldt.atZone(Paris)     : " + local.atZone(PARIS));         // 2024-03-15T12:00+01:00[Europe/Paris]
        OffsetDateTime offset = local.atOffset(ZoneOffset.ofHours(1));
        System.out.println("ldt.atOffset(+01:00)  : " + offset);                      // 2024-03-15T12:00+01:00

        // Other zone, same instant
        ZonedDateTime tokyo = paris.withZoneSameInstant(TOKYO);
        System.out.println("withZoneSameInstant   : " + tokyo);                       // 2024-03-15T20:00+09:00[Asia/Tokyo]

        // "UTC wall clock" of any zoned value
        System.out.println("as UTC LocalDateTime  : " + paris.withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime()); // 2024-03-15T11:00

        // Instant -> ZonedDateTime -> Instant never loses anything
        System.out.println("lossless round trip   : " + instant.equals(instant.atZone(NEW_YORK).toInstant())); // true
    }

    // ── 3. Round-trip gotcha ──────────────────────────────────────────────────

    /**
     * Converting through a {@link LocalDateTime} is only lossless when each wall-clock reading
     * identifies exactly one moment. During a daylight-saving <em>overlap</em> (clocks go back)
     * a reading such as {@code 01:30} happens twice, and {@code atZone} must pick one — the earlier.
     *
     * <p>WHY this matters: a log pipeline that stores {@code LocalDateTime} in a zone with
     * DST cannot reconstruct the second occurrence — events look like they happened an hour
     * earlier. Store {@code Instant} (or at least an offset) and convert to local fields only for display.
     */
    static void roundTripGotchaDemo() {
        System.out.println("=== Round-trip gotcha (DST overlap) ===");

        // 2024-11-03 in New York: 01:30 happens twice (EDT first, then EST)
        Instant secondOccurrence = Instant.parse("2024-11-03T06:30:00Z");   // 01:30 EST (-05:00)

        LocalDateTime wallClock = LocalDateTime.ofInstant(secondOccurrence, NEW_YORK);
        System.out.println("as LocalDateTime      : " + wallClock);                                    // 2024-11-03T01:30

        Instant viaLocal = wallClock.atZone(NEW_YORK).toInstant();
        System.out.println("back via LocalDateTime: " + viaLocal);                                     // 2024-11-03T05:30:00Z (one hour earlier!)
        System.out.println("same as original?     : " + viaLocal.equals(secondOccurrence));            // false

        // Keeping the ZonedDateTime keeps the offset, so nothing is lost
        ZonedDateTime zoned = secondOccurrence.atZone(NEW_YORK);
        System.out.println("zoned keeps offset    : " + zoned);                                        // 2024-11-03T01:30-05:00[America/New_York]
        System.out.println("zoned round trip      : " + zoned.toInstant().equals(secondOccurrence));   // true
    }

    // ── 4. Legacy interop ─────────────────────────────────────────────────────

    /**
     * Older APIs still use {@link Date}, {@link GregorianCalendar} and {@link TimeZone}.
     * The bridges are small and explicit.
     *
     * <p>WHY this matters: you will meet JDBC drivers, libraries and old code that hand you
     * a {@code Date}. Convert to java.time at the boundary and stay there.
     *
     * <p>Gotchas:
     * <ul>
     *   <li>{@code java.util.Date} is a misleading name: it is an <em>instant</em> with millisecond
     *       precision, not a calendar date. Its {@code toString()} uses the JVM default zone.</li>
     *   <li>{@code Date.from(instant)} truncates nanoseconds to milliseconds.</li>
     *   <li>{@code java.sql.Date.toInstant()} throws {@code UnsupportedOperationException} —
     *       use {@code java.sql.Date.toLocalDate()} instead.</li>
     * </ul>
     */
    static void legacyInteropDemo() {
        System.out.println("=== Legacy interop ===");

        Instant instant = Instant.parse("2024-03-15T10:15:30Z");

        Date date = Date.from(instant);
        System.out.println("Date.getTime          : " + date.getTime());                       // 1710497730000
        System.out.println("date.toInstant        : " + date.toInstant());                     // 2024-03-15T10:15:30Z
        System.out.println("round trip equal      : " + date.toInstant().equals(instant));     // true

        // Precision: Date only has milliseconds
        Instant nanos = Instant.ofEpochSecond(0, 123_456_789L);
        System.out.println("nanos -> Date -> back : " + Date.from(nanos).toInstant());         // 1970-01-01T00:00:00.123Z

        // Date -> LocalDate needs a zone, like any other moment -> calendar conversion
        LocalDate inTokyo = date.toInstant().atZone(TOKYO).toLocalDate();
        System.out.println("Date as Tokyo date    : " + inTokyo);                              // 2024-03-15

        // Calendar and TimeZone
        ZonedDateTime paris = instant.atZone(PARIS);
        GregorianCalendar calendar = GregorianCalendar.from(paris);
        System.out.println("calendar -> zoned     : " + calendar.toZonedDateTime());           // 2024-03-15T11:15:30+01:00[Europe/Paris]
        System.out.println("TimeZone -> ZoneId    : " + TimeZone.getTimeZone(PARIS).toZoneId()); // Europe/Paris
    }

    // ── 5. Epoch numbers ──────────────────────────────────────────────────────

    /**
     * Epoch numbers are plain {@code long}s counting from {@code 1970-01-01}.
     * <ul>
     *   <li>epoch <em>day</em> — {@code LocalDate.toEpochDay()} (days, no zone needed)</li>
     *   <li>epoch <em>second</em> — {@code Instant.getEpochSecond()} (zone-free)</li>
     *   <li>epoch second of a {@code LocalDateTime} — needs an offset to say whose wall clock it is</li>
     * </ul>
     *
     * <p>WHY this matters: they are the compact, language-neutral format for storage and APIs,
     * and the easiest way to compare or bucket moments with plain arithmetic.
     */
    static void epochNumbersDemo() {
        System.out.println("=== Epoch numbers ===");

        LocalDate date = LocalDate.of(2024, 3, 15);
        System.out.println("toEpochDay            : " + date.toEpochDay());                    // 19797
        System.out.println("ofEpochDay(19797)     : " + LocalDate.ofEpochDay(19797));          // 2024-03-15

        LocalDateTime ldt = LocalDateTime.of(2024, 3, 15, 10, 15, 30);
        System.out.println("epochSecond @UTC      : " + ldt.toEpochSecond(ZoneOffset.UTC));                    // 1710497730
        System.out.println("epochSecond @+01:00   : " + ldt.toEpochSecond(ZoneOffset.ofHours(1)));              // 1710494130 (one hour earlier)

        ZonedDateTime zoned = ldt.atZone(PARIS);
        System.out.println("zoned.toEpochSecond   : " + zoned.toEpochSecond());                // 1710494130
        System.out.println("instant epochSecond   : " + zoned.toInstant().getEpochSecond());   // 1710494130
    }
}
