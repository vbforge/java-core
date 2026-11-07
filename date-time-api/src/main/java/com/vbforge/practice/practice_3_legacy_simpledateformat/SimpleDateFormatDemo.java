package com.vbforge.practice.practice_3_legacy_simpledateformat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Demo class to showcase {@link SimpleDateFormat} patterns in Java.
 * <p>
 * Key concepts demonstrated:
 * <ul>
 *   <li>Common date/time pattern letters</li>
 *   <li>Day of week: 'E' (name) vs 'u' (number)</li>
 *   <li>Year vs Week Year: 'y' vs 'Y'</li>
 *   <li>Multi-line and custom formatting</li>
 *   <li>Formatting a Date with several common patterns</li>
 *   <li>Parsing a String back into a Date</li>
 * </ul>
 * <p>
 * Note: SimpleDateFormat is NOT thread-safe; prefer java.time.format.DateTimeFormatter
 * (see practice_12_datetime_formatter).
 * </p>
 */
public class SimpleDateFormatDemo {

    private final SimpleDateFormat dateFormat;

    /**
     * Creates a formatter with the given pattern using the default locale.
     */
    public SimpleDateFormatDemo(String pattern) {
        this(pattern, Locale.getDefault());
    }

    /**
     * Creates a formatter with the given pattern and locale.
     */
    public SimpleDateFormatDemo(String pattern, Locale locale) {
        this.dateFormat = new SimpleDateFormat(pattern, locale);
        System.out.printf("[Pattern] \"%s\"%n", pattern);
    }

    /**
     * Formats the date and prints it with a descriptive label.
     */
    public void printFormatted(Date date, String label) {
        String formatted = dateFormat.format(date);
        System.out.printf("[%s] %s%n", label, formatted);
    }

    /**
     * Returns the formatted string without printing.
     */
    public String format(Date date) {
        return dateFormat.format(date);
    }

    public static void main(String[] args) {
        demonstratePatternLetters();
        demonstrateFormattingPatterns();
        demonstrateStringToDateParsing();
    }

    private static void demonstratePatternLetters() {
        System.out.println("=== SimpleDateFormat Demo ===\n");

        Date now = new Date();

        // 1. Full date with weekday (short name)
        SimpleDateFormatDemo demo1 = new SimpleDateFormatDemo("EEE, dd MMM yyyy");
        demo1.printFormatted(now, "1 - Full date with weekday");

        // 2. ISO-like datetime
        SimpleDateFormatDemo demo2 = new SimpleDateFormatDemo("yyyy-MM-dd HH:mm:ss");
        demo2.printFormatted(now, "2 - ISO-like datetime");

        // 3. Demonstrating 'Y' (Week Year) vs 'y' (Calendar Year)
        SimpleDateFormatDemo demo3 = new SimpleDateFormatDemo("yyyy-MM-dd '(Calendar Year y)'");
        demo3.printFormatted(now, "3a - Calendar Year (y)");

        SimpleDateFormatDemo demo3b = new SimpleDateFormatDemo("Y-MM-dd '(Week Year Y)'");
        demo3b.printFormatted(now, "3b - Week Year (Y)");

        // 4. Day of week examples - FIXED: Combined into separate patterns to avoid issues
        System.out.println("\n[4 - Day of week examples]");
        
        SimpleDateFormatDemo demo4a = new SimpleDateFormatDemo("'Date:   ' yyyy-MM-dd");
        demo4a.printFormatted(now, "4a - Date");
        
        SimpleDateFormatDemo demo4b = new SimpleDateFormatDemo("'Time:   ' HH:mm:ss");
        demo4b.printFormatted(now, "4b - Time");
        
        SimpleDateFormatDemo demo4c = new SimpleDateFormatDemo("'Day:    ' EEEE '(full)'");
        demo4c.printFormatted(now, "4c - Full weekday");
        
        SimpleDateFormatDemo demo4d = new SimpleDateFormatDemo("'Day:    ' EEE '(short)'");
        demo4d.printFormatted(now, "4d - Short weekday");
        
        SimpleDateFormatDemo demo4e = new SimpleDateFormatDemo("'Weekday number: ' u");
        demo4e.printFormatted(now, "4e - Weekday number");

        System.out.println("\n--- Additional Info ---");
        System.out.println("Current Date object : " + now);
        System.out.println("Current millis      : " + System.currentTimeMillis());
        System.out.println();
    }

    /**
     * Shows how to format dates using SimpleDateFormat with various patterns.
     */
    private static void demonstrateFormattingPatterns() {
        System.out.println("=== Date Formatting with SimpleDateFormat ===\n");

        SimpleDateFormat myFormat = new SimpleDateFormat("dd-MMMM-yyyy HH:mm", Locale.ENGLISH);
        Calendar calendar = new GregorianCalendar();
        Date currentDate = calendar.getTime();

        System.out.println("Pattern: dd-MMMM-yyyy HH:mm");
        System.out.println("Formatted: " + myFormat.format(currentDate));

        // Additional common patterns
        System.out.println("\nOther common patterns:");
        System.out.println("  dd/MM/yyyy:          " + new SimpleDateFormat("dd/MM/yyyy").format(currentDate));
        System.out.println("  yyyy-MM-dd HH:mm:ss: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(currentDate));
        System.out.println("  EEEE, MMM d, yyyy:   " + new SimpleDateFormat("EEEE, MMM d, yyyy", Locale.ENGLISH).format(currentDate));
        System.out.println();
    }

    /**
     * Demonstrates parsing a String into a Date object.
     * The pattern must match the string format exactly.
     */
    private static void demonstrateStringToDateParsing() {
        System.out.println("=== Parsing String to Date ===\n");

        String dateString = "02-June-2022, 08:05";
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMMM-yyyy, HH:mm", Locale.ENGLISH);

        try {
            Date parsedDate = formatter.parse(dateString);
            System.out.println("Original string:     " + dateString);
            System.out.println("Parsed Date object:  " + parsedDate);
            System.out.println("Formatted back:      " + formatter.format(parsedDate));
            System.out.println("\n✓ Successfully parsed string to Date and back");
        } catch (ParseException e) {
            System.err.println("✗ Failed to parse date: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n--- Important ---");
        System.out.println("• Pattern must EXACTLY match the string format");
        System.out.println("• Month and day names are Locale-sensitive");
        System.out.println("• Use Locale.ENGLISH for English month/day names\n");
    }
}