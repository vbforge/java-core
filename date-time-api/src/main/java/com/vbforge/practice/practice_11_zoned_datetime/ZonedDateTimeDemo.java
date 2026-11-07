// IMPLEMENTATION
package com.vbforge.practice.practice_11_zoned_datetime;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Set;

/**
 * Demonstrates {@link ZonedDateTime} and {@link OffsetDateTime}.
 *
 * Key concepts:
 *   - ZonedDateTime  = LocalDateTime + ZoneId  (DST-aware, full timezone rules)
 *   - OffsetDateTime = LocalDateTime + ZoneOffset (fixed offset, no DST)
 *
 * When to use which:
 *   - ZonedDateTime  → scheduling future events that must respect DST transitions
 *                      (e.g., "every day at 09:00 Europe/Kiev")
 *   - OffsetDateTime → storing a historical moment precisely
 *                      (e.g., database timestamp with offset; no DST ambiguity)
 *   - Instant        → machine time / UTC; no human-readable date
 *
 * DST (Daylight Saving Time):
 *   - When clocks spring forward, one wall-clock hour is skipped.
 *     Adding Duration.ofHours(1) near a gap adjusts the offset correctly.
 *   - When clocks fall back, one hour is repeated (overlap).
 *     ZonedDateTime.ofLocal() lets you pick EARLIER or LATER occurrence.
 *
 * ZoneId vs ZoneOffset:
 *   - ZoneId.of("Europe/Kyiv")  → IANA tz database (DST-aware)
 *   - ZoneOffset.of("+02:00")   → fixed, never changes
 *   - ZoneId.systemDefault()    → JVM's configured timezone
 */
public class ZonedDateTimeDemo {

    public static void main(String[] args) {
        System.out.println("=== ZonedDateTime Demo ===\n");

        // --- 1. Creation ---
        ZonedDateTime nowKyiv   = ZonedDateTime.now(ZoneId.of("Europe/Kyiv"));
        ZonedDateTime nowTokyo  = ZonedDateTime.now(ZoneId.of("Asia/Tokyo"));
        ZonedDateTime nowUtc    = ZonedDateTime.now(ZoneId.of("UTC"));

        System.out.println("now (Kyiv)  : " + nowKyiv);
        System.out.println("now (Tokyo) : " + nowTokyo);
        System.out.println("now (UTC)   : " + nowUtc);

        // --- 2. From LocalDateTime + ZoneId ---
        LocalDateTime local  = LocalDateTime.of(2024, 6, 15, 14, 30);
        ZonedDateTime zoned  = ZonedDateTime.of(local, ZoneId.of("Europe/Kyiv"));
        System.out.println("\nfrom LocalDateTime: " + zoned);
        System.out.println("zone               : " + zoned.getZone());
        System.out.println("offset             : " + zoned.getOffset()); // +03:00 (EEST)

        // --- 3. OffsetDateTime (fixed offset) ---
        OffsetDateTime offsetDT = OffsetDateTime.of(local, ZoneOffset.of("+02:00"));
        System.out.println("\nOffsetDateTime    : " + offsetDT);

        // --- 4. Convert to another zone (same instant, different wall clock) ---
        ZonedDateTime inNewYork = zoned.withZoneSameInstant(ZoneId.of("America/New_York"));
        ZonedDateTime inUtc     = zoned.withZoneSameInstant(ZoneId.of("UTC"));
        System.out.println("\n-- Same instant, different zones --");
        System.out.println("Kyiv    : " + zoned);
        System.out.println("New York: " + inNewYork);
        System.out.println("UTC     : " + inUtc);
        System.out.println("Instant equality: " + zoned.toInstant().equals(inNewYork.toInstant())); // true

        // --- 5. Arithmetic respects DST ---
        // Ukraine: spring forward last Sunday of March → +02:00 → +03:00 (EEST)
        ZonedDateTime beforeDst = ZonedDateTime.of(
                LocalDateTime.of(2024, 3, 31, 2, 0), ZoneId.of("Europe/Kyiv"));
        System.out.println("\n-- DST transition (spring forward) --");
        System.out.println("before: " + beforeDst);
        System.out.println("+ 1h  : " + beforeDst.plusHours(1));   // offset may jump

        // --- 6. ZoneId utilities ---
        System.out.println("\n-- ZoneId --");
        System.out.println("system default   : " + ZoneId.systemDefault());
        System.out.println("available zones  : " + ZoneId.getAvailableZoneIds().size() + " total");

        // --- 7. Conversion ---
        Instant instant = zoned.toInstant();
        LocalDateTime backToLocal = zoned.toLocalDateTime();
        System.out.println("\ntoInstant()      : " + instant);
        System.out.println("toLocalDateTime(): " + backToLocal);

        // --- 8. Formatting ---
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm z");
        System.out.println("\nformatted        : " + zoned.format(fmt));

        // --- 9 .. : extra scenes ---
        System.out.println("\n*** zoneIdWithLocalDateAndLocalTime ***");
        zoneIdWithLocalDateAndLocalTime();

        System.out.println("\n*** createZonedDateTimes ***");
        createZonedDateTimes();

        System.out.println("\n*** getUtcLocalDateTimeExample ***");
        getUtcLocalDateTimeExample();

        System.out.println("\n*** listingOfAllZoneIDs ***");
        listingOfAllZoneIDs();

        System.out.println("\n*** listingOfAllZoneIDsDisplayNames ***");
        listingOfAllZoneIDsDisplayNames();
    }

    /**
     * ZonedDateTime from LocalDate + LocalTime + ZoneId, plus the set of all zone ids.
     */
    private static void zoneIdWithLocalDateAndLocalTime() {
        LocalDate localDate = LocalDate.now();
        LocalTime localTime = LocalTime.now();
        ZoneId zoneId = ZoneId.of("Europe/Kiev");
        ZonedDateTime zdt = ZonedDateTime.of(localDate, localTime,zoneId);
        System.out.println(zdt);

        Set<String> allZoneIds = ZoneId.getAvailableZoneIds();
        System.out.println(allZoneIds);
    }

    private static void getUtcLocalDateTimeExample() {
        // Get the current time in UTC as an Instant
        Instant utcNow = Instant.now();
        System.out.println("Current time utc now = \n" + utcNow);
        // Convert Instant to LocalDateTime in UTC
        LocalDateTime utcLocalDateTime = LocalDateTime.ofInstant(utcNow, ZoneOffset.UTC);
        System.out.println("Current LocalDateTime in UTC: \n" + utcLocalDateTime);
    }

    private static void createZonedDateTimes() {
        ZoneId zoneId = ZoneId.of("Europe/Kyiv");
        LocalDateTime ldt = LocalDateTime.now();
        ZonedDateTime zdt1 = ZonedDateTime.of(ldt, zoneId);
        System.out.println(zoneId);
        System.out.println(zdt1);

        //other way to create ZoneDateTime instance:
        ZonedDateTime zdt2 = ldt.atZone(zoneId);
        //or:
        ZonedDateTime zdt3 = ZonedDateTime.now();
        System.out.println("zdt2 = " + zdt2);
        System.out.println("zdt3 = " + zdt3.getZone());
    }

    private static void listingOfAllZoneIDsDisplayNames() {
        for (String zoneIDString : ZoneId.getAvailableZoneIds()) {
            ZoneId zoneId = ZoneId.of(zoneIDString);
            String zoneIdDisplayName = zoneId.getDisplayName(TextStyle.FULL, Locale.UK);
            System.out.println(zoneIDString + " : " + zoneIdDisplayName); //Europe/Kyiv : Kyiv Time
        }
    }

    private static void listingOfAllZoneIDs() {
        for (String zoneID : ZoneId.getAvailableZoneIds()) {
            System.out.println(zoneID); //Europe/Kiev
        }
    }
}