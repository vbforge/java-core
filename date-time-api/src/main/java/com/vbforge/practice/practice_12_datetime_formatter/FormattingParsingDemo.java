// IMPLEMENTATION
package com.vbforge.practice.practice_12_datetime_formatter;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.util.Locale;

/**
 * Demonstrates {@link DateTimeFormatter} — formatting and parsing date/time types.
 *
 * Key concepts:
 *   - DateTimeFormatter is immutable and thread-safe (unlike legacy SimpleDateFormat).
 *   - Three ways to obtain a formatter:
 *       1. Predefined constants  : DateTimeFormatter.ISO_LOCAL_DATE, ISO_INSTANT, etc.
 *       2. Pattern-based         : DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
 *       3. Locale-aware style    : DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)
 *
 * Pattern letters (selection):
 *   y  year    M  month-of-year   d  day-of-month
 *   H  hour (0-23)  h  hour (1-12)  m  minute  s  second  S  fraction-of-second
 *   E  day-of-week  a  AM/PM        z  zone name  Z  zone offset  x  offset (+HHmm)
 *   '  text delimiter   ''  literal single quote
 *
 * Formatting:  formatter.format(temporal)  OR  temporal.format(formatter)
 * Parsing:     formatter.parse(text)        — returns a TemporalAccessor
 *              LocalDate.parse(text, fmt)   — returns the specific type
 *
 * Optional sections:
 *   Use [ ] in pattern for optional parts, e.g. "yyyy-MM-dd['T'HH:mm[:ss]]"
 */
public class FormattingParsingDemo {

    public static void main(String[] args) {
        System.out.println("=== DateTimeFormatter Demo ===\n");

        LocalDate      date    = LocalDate.of(2024, 6, 15);
        LocalTime      time    = LocalTime.of(14, 30, 5);
        LocalDateTime  ldt     = LocalDateTime.of(date, time);
        ZonedDateTime  zdt     = ldt.atZone(ZoneId.of("Europe/Kyiv"));

        // --- 1. ISO predefined constants ---
        System.out.println("-- ISO predefined --");
        System.out.println("ISO_LOCAL_DATE     : " + DateTimeFormatter.ISO_LOCAL_DATE.format(date));
        System.out.println("ISO_LOCAL_TIME     : " + DateTimeFormatter.ISO_LOCAL_TIME.format(time));
        System.out.println("ISO_LOCAL_DATE_TIME: " + DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(ldt));
        System.out.println("ISO_ZONED_DATE_TIME: " + DateTimeFormatter.ISO_ZONED_DATE_TIME.format(zdt));
        System.out.println("ISO_INSTANT        : " + DateTimeFormatter.ISO_INSTANT.format(zdt));

        // --- 2. Custom patterns ---
        System.out.println("\n-- Custom patterns --");

        DateTimeFormatter dmySlash  = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter dmyDot    = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        DateTimeFormatter full      = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy, HH:mm:ss");
        DateTimeFormatter withZone  = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm z");
        DateTimeFormatter twelveH   = DateTimeFormatter.ofPattern("hh:mm a");

        System.out.println("dd/MM/yyyy         : " + date.format(dmySlash));
        System.out.println("dd.MM.yyyy         : " + date.format(dmyDot));
        System.out.println("full (default loc) : " + ldt.format(full));
        System.out.println("with zone          : " + zdt.format(withZone));
        System.out.println("12-hour clock      : " + time.format(twelveH));

        // --- 3. Locale-aware formatting ---
        System.out.println("\n-- Locale-aware --");
        DateTimeFormatter ukLocale = DateTimeFormatter
                .ofLocalizedDateTime(FormatStyle.LONG)
                .withLocale(Locale.UK);
        DateTimeFormatter usLocale = DateTimeFormatter
                .ofLocalizedDateTime(FormatStyle.MEDIUM)
                .withLocale(Locale.US);
        DateTimeFormatter plLocale = DateTimeFormatter
                .ofLocalizedDate(FormatStyle.FULL)
                .withLocale(new Locale("pl", "PL"));

        System.out.println("UK LONG   : " + zdt.format(ukLocale));
        System.out.println("US MEDIUM : " + ldt.format(usLocale));
        System.out.println("PL FULL   : " + date.format(plLocale));

        // --- 4. Parsing ---
        System.out.println("\n-- Parsing --");

        LocalDate  parsed1 = LocalDate.parse("15/06/2024", dmySlash);
        LocalDate  parsed2 = LocalDate.parse("15.06.2024", dmyDot);
        LocalDateTime parsedLDT = LocalDateTime.parse("2024-06-15T14:30:05");  // ISO default
        ZonedDateTime parsedZDT = ZonedDateTime.parse("2024-06-15T14:30:05+03:00[Europe/Kyiv]");

        System.out.println("parsed dd/MM/yyyy  : " + parsed1);
        System.out.println("parsed dd.MM.yyyy  : " + parsed2);
        System.out.println("parsed LDT (ISO)   : " + parsedLDT);
        System.out.println("parsed ZDT         : " + parsedZDT);

        // --- 5. Optional pattern sections ---
        DateTimeFormatter flexible = DateTimeFormatter.ofPattern("yyyy-MM-dd['T'HH:mm[:ss]]");
        LocalDateTime withTime    = LocalDateTime.parse("2024-06-15T14:30:05", flexible);
        LocalDateTime withoutSec  = LocalDateTime.parse("2024-06-15T14:30",    flexible);
        // date-only section requires a default LocalTime when time part is absent:
        LocalDate dateOnly = LocalDate.parse("2024-06-15", flexible);
        System.out.println("\n-- Optional sections --");
        System.out.println("with seconds  : " + withTime);
        System.out.println("without secs  : " + withoutSec);
        System.out.println("date only     : " + dateOnly);

        // --- 6. ParseException handling ---
        System.out.println("\n-- ParseException --");
        try {
            LocalDate.parse("not-a-date", dmySlash);
        } catch (DateTimeParseException ex) {
            System.out.println("Caught: " + ex.getClass().getSimpleName()
                    + " — " + ex.getMessage().split("\n")[0]);
        }

        // --- 7 .. 10: extra scenes ---
        dayOfYearAndBuilderParsing();
        patternEMMMddyyyy();
        isoBasicAndLocalizedStyles();
    }

    /**
     * Day-of-year pattern ("DDD"), localized MEDIUM date, and DateTimeFormatterBuilder parsing.
     */
    private static void dayOfYearAndBuilderParsing() {
        System.out.println("\n-- DDD, MEDIUM style, DateTimeFormatterBuilder --");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("DDD", Locale.US);
        System.out.println(formatter.format(LocalDateTime.now()));
//        +++ 030
//        30
//        Jan
//        Monday
//        January
//        Mon

        LocalDateTime ldt1 = LocalDateTime.now();
        String formattedMedium_ldt1 = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).format(ldt1);
        System.out.println("formattedMedium_ldt1 = " + formattedMedium_ldt1);

        System.out.println("----------");
        DateTimeFormatter parser = new DateTimeFormatterBuilder()
                .appendPattern("yyyyMMdd")
                .toFormatter();
        System.out.println(parser.parse("20230115", LocalDate::from)); //2023-01-15
    }

    /**
     * Simple "E, MMM dd yyyy" pattern with the list of other accepted values.
     */
    private static void patternEMMMddyyyy() {
        System.out.println("\n-- Pattern: E, MMM dd yyyy --");

        LocalDateTime myDateObj = LocalDateTime.now();
        System.out.println("Before formatting: " + myDateObj);
        //DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("E, MMM dd yyyy");

        String formattedDate = myDateObj.format(myFormatObj);
        System.out.println("After formatting: " + formattedDate);

        //The ofPattern() method accepts all sorts of values:
        /*
        yyyy-MM-dd
        dd/MM/yyyy
        dd-MMM-yyyy
        E, MMM dd yyyy*/
    }

    /**
     * BASIC_ISO_DATE, dd-MMMM-yyyy, FormatStyle enum variants and the "14:30:00" pattern question.
     */
    private static void isoBasicAndLocalizedStyles() {
        System.out.println("\n-- BASIC_ISO_DATE, FormatStyle variants --");

        LocalDateTime ldt = LocalDateTime.now();

        //formatting:
        String dateFormatted1 = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(ldt);
        System.out.println("dateFormatted1 = " + dateFormatted1);

        String dateFormatted2 = DateTimeFormatter.BASIC_ISO_DATE.format(ldt);
        System.out.println("dateFormatted2 = " + dateFormatted2);

        //custom formatter:
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MMMM-yyyy");
        System.out.println(dtf.format(ldt)); //17-January-2025
        System.out.println(dtf.format(ldt.plusMonths(1))); //17-February-2025

        //other way format style using Enum
        String formatStyle_short = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).format(ldt);
        String formatStyle_medium = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).format(ldt);
        String formatStyle_full = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).format(ldt);
        String formatStyle_long = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).format(ldt);

        System.out.println("formatStyle_short = " + formatStyle_short);
        System.out.println("formatStyle_medium = " + formatStyle_medium);
        System.out.println("formatStyle_full = " + formatStyle_full);
        System.out.println("formatStyle_long = " + formatStyle_long);

        //What value of the timeFormat variable in the first line of the code below would result in the string "14:30:00" in the console?
        String timeFormat = "14:30:00";
        LocalTime time = LocalTime.of(14, 30);
        System.out.println(time.format(DateTimeFormatter.ofPattern(timeFormat)));
        //1)HH:MM:ss
        //2)hh:mm:ss
        //3)HH:MM:SS
        //4) + HH:mm:ss
    }
}