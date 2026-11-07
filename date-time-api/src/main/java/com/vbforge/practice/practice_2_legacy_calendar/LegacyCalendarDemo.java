package com.vbforge.practice.practice_2_legacy_calendar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import static java.util.Calendar.*;

/**
 * Demonstrates the legacy Calendar and GregorianCalendar classes in Java.
 * <p>
 * Key capabilities demonstrated:
 * <ul>
 *   <li>Reading the current date/time fields</li>
 *   <li>Elapsed time via getTimeInMillis()</li>
 *   <li>Adding/subtracting days, months, or years to/from a date</li>
 *   <li>Extracting individual date components (year, month, day, etc.)</li>
 *   <li>Working with useful constants (Calendar.JANUARY, Calendar.FEBRUARY, etc.)</li>
 * </ul>
 * </p>
 * <p>
 * Note: The Calendar classes are largely outdated. Consider using
 * java.time package (LocalDate, LocalDateTime, Period) for new projects.
 * </p>
 * <p>
 * SimpleDateFormat formatting/parsing scenes live in
 * practice_3_legacy_simpledateformat/SimpleDateFormatDemo.
 * </p>
 */
public class LegacyCalendarDemo {
    
    public static void main(String[] args) {
        demonstrateCurrentCalendarFields();
        demonstrateElapsedTime();
        demonstrateCalendarCreation();
        demonstrateDateManipulation();
        demonstrateExtractingDateComponents();
        demonstrateWorkingWithEras();
        demonstrateCalendarMutation();
    }

    /**
     * Reads the current date/time fields from Calendar.getInstance().
     */
    private static void demonstrateCurrentCalendarFields() {
        System.out.println("=== Current Calendar Fields ===\n");

        // Current date:
        Calendar today=Calendar.getInstance();
        System.out.println("Today "+today.getTime());
        System.out.println("Day of week:\t"+today.get(Calendar.DAY_OF_WEEK));
        System.out.println("Date:\t\t\t"+today.get(Calendar.DAY_OF_MONTH));
        System.out.println("Month:\t\t\t"+today.get(Calendar.MONTH));
        System.out.println("Year:\t\t\t"+today.get(Calendar.YEAR));
        System.out.println("Hours:\t\t\t"+today.get(Calendar.HOUR));
        System.out.println("Minutes:\t\t"+today.get(Calendar.MINUTE));
        System.out.println("Seconds:\t\t"+today.get(Calendar.SECOND));
        System.out.println();
    }

    /**
     * Elapsed time and "time ago" using getTimeInMillis()/setTimeInMillis().
     */
    private static void demonstrateElapsedTime() {
        System.out.println("=== Elapsed Time with Calendar ===\n");

        // Current date:
        Calendar start=Calendar.getInstance();
        System.out.println("Start execution: "+start.getTime());
        long time;
        // Millions seconds ago:
        time=start.getTimeInMillis()-1000000000;
        Calendar t=Calendar.getInstance();
        t.setTimeInMillis(time);
        System.out.println("Millions seconds ago: " +t.getTime());
        // Time of finishing programme(almost):
        Calendar finish=Calendar.getInstance();
        System.out.println("Finish execution: "+finish.getTime());
        // Duration of working programme:
        System.out.println("Past "+(finish.getTimeInMillis()-
                start.getTimeInMillis())+" milliseconds");
        System.out.println();
    }
    
    /**
     * Shows different ways to create and initialize Calendar objects.
     * 1. Constructor with year, month, day
     * 2. Using static constants for months
     * 3. Using set() methods for individual fields
     */
    private static void demonstrateCalendarCreation() {
        System.out.println("=== Calendar Creation Examples ===\n");
        
        // Method 1: Constructor with numeric values (month: 0 = January!)
        // Warning: Month numbering starts at 0 (0 = January, 11 = December)
        Calendar calendar = new GregorianCalendar(2023, 0, 16);  // January 16, 2023
        System.out.println("Calendar object: " + calendar);
        System.out.println("As Date object:  " + calendar.getTime());
        
        // Method 2: Using Calendar constants (more readable)
        // Recommended approach - avoids off-by-one errors
        GregorianCalendar calendar1 = new GregorianCalendar(2023, JANUARY, 16);
        System.out.println("Using constants: " + calendar1.getTime());
        
        // Method 3: Creating empty calendar and setting fields individually
        Calendar calendar2 = new GregorianCalendar();
        calendar2.set(Calendar.YEAR, 2023);
        calendar2.set(MONTH, 0);           // January (can use JANUARY constant instead)
        calendar2.set(Calendar.DAY_OF_MONTH, 16);
        calendar2.set(Calendar.HOUR_OF_DAY, 8);
        calendar2.set(Calendar.MINUTE, 5);
        calendar2.set(Calendar.SECOND, 5);
        System.out.println("Individual field setting: " + calendar2.getTime());
        
        System.out.println("\n--- Important Notes ---");
        System.out.println("• Month numbering: 0 = January, 11 = December");
        System.out.println("• Use Calendar constants (JANUARY, FEBRUARY) to avoid errors");
        System.out.println("• GregorianCalendar extends Calendar with leap year support\n");
    }
    
    /**
     * Demonstrates date manipulation using add() and roll() methods.
     * - add(): Adds/subtracts time, automatically rolls over to next/prev year/month
     * - roll(): Adds/subtracts time WITHOUT affecting larger fields (year stays same)
     */
    private static void demonstrateDateManipulation() {
        System.out.println("=== Date Manipulation (add vs roll) ===\n");
        
        Calendar calendar = new GregorianCalendar();
        calendar.set(2023, JANUARY, 16, 8, 5, 5);
        System.out.println("Starting date: " + formatDateTime(calendar));
        
        // add() with negative value - subtracts years
        calendar.add(Calendar.YEAR, -2);
        System.out.println("After add(Year, -2):    " + formatDateTime(calendar));
        
        // roll() - subtracts months WITHOUT changing the year
        // Even if we go below January, year stays the same
        calendar.roll(MONTH, -2);
        System.out.println("After roll(Month, -2):  " + formatDateTime(calendar));
        System.out.println("  → Note: Year unchanged (" + calendar.get(YEAR) + ")");
        
        // add() - automatically rolls over to next year
        calendar.add(MONTH, +4);
        System.out.println("After add(Month, +4):   " + formatDateTime(calendar));
        System.out.println("  → Note: Year changed to " + calendar.get(YEAR));
        
        System.out.println("\n--- Key Difference ---");
        System.out.println("• add(): Can change year/month when boundaries are crossed");
        System.out.println("• roll(): Never changes larger fields (year stays same)\n");
    }
    
    /**
     * Shows how to extract individual date components using get() method.
     * Can retrieve year, month, day, hour, minute, week-of-month, etc.
     */
    private static void demonstrateExtractingDateComponents() {
        System.out.println("=== Extracting Date Components ===\n");
        
        Calendar calendar = new GregorianCalendar();
        calendar.set(2023, JANUARY, 16, 8, 5, 5);
        
        // Retrieving individual date components
        System.out.println("Individual components from " + formatDateTime(calendar) + ":");
        System.out.println("  Year:                    " + calendar.get(Calendar.YEAR));
        System.out.println("  Month (0-indexed):       " + calendar.get(MONTH));
        System.out.println("  Month name:              " + getMonthName(calendar.get(MONTH)));
        System.out.println("  Week of month:           " + calendar.get(Calendar.WEEK_OF_MONTH));
        System.out.println("  Day of month:            " + calendar.get(Calendar.DAY_OF_MONTH));
        System.out.println("  Hour (12-hour format):   " + calendar.get(Calendar.HOUR));
        System.out.println("  Hour (24-hour format):   " + calendar.get(Calendar.HOUR_OF_DAY));
        System.out.println("  Minute:                  " + calendar.get(Calendar.MINUTE));
        System.out.println("  Second:                  " + calendar.get(Calendar.SECOND));
        System.out.println("  AM/PM:                   " + (calendar.get(Calendar.AM_PM) == AM ? "AM" : "PM"));
        
        System.out.println("\n--- Common get() Fields ---");
        System.out.println("• YEAR, MONTH, DAY_OF_MONTH, DAY_OF_WEEK");
        System.out.println("• HOUR, HOUR_OF_DAY, MINUTE, SECOND");
        System.out.println("• WEEK_OF_MONTH, WEEK_OF_YEAR");
        System.out.println("• AM_PM, MILLISECOND\n");
    }
    
    /**
     * Demonstrates working with eras (BC/AD).
     * Example: First Olympic Games in Ancient Greece (776 BC)
     * The 'G' pattern in SimpleDateFormat displays the era.
     */
    private static void demonstrateWorkingWithEras() {
        System.out.println("=== Working with Eras (BC/AD) ===\n");
        
        // September 6, 776 BC - First Olympic Games in Ancient Greece
        GregorianCalendar firstOlympicGames = new GregorianCalendar(776, Calendar.SEPTEMBER, 6);
        firstOlympicGames.set(Calendar.ERA, GregorianCalendar.BC);
        
        // 'GG' pattern letter displays the era (BC/AD)
        DateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy GG", Locale.ENGLISH);
        String description = " - First Olympic Games in Ancient Greece";
        
        System.out.println(dateFormat.format(firstOlympicGames.getTime()) + description);
        System.out.println("\nNote: 'GG' in SimpleDateFormat displays the era (BC or AD)\n");
    }
    
    /**
     * Shows that Calendar is mutable and can be modified after creation.
     * Be careful when reusing Calendar objects!
     */
    private static void demonstrateCalendarMutation() {
        System.out.println("=== Calendar Mutation (Mutable Objects) ===\n");
        
        SimpleDateFormat df = new SimpleDateFormat("EEEE, d MMMM yyyy", Locale.ENGLISH);
        Calendar calendar = new GregorianCalendar(2023, JANUARY, 31);
        
        System.out.println("Initial date: " + df.format(calendar.getTime()));
        
        // Changing month from January to February
        calendar.set(MONTH, FEBRUARY);
        System.out.println("After set(MONTH, FEBRUARY): " + df.format(calendar.getTime()));
        
        // Fixing invalid date (Feb 31 → Feb 28 in non-leap year)
        // Note: Calendar automatically corrects invalid dates
        calendar.set(DAY_OF_MONTH, 28);
        System.out.println("After set(DAY_OF_MONTH, 28): " + df.format(calendar.getTime()));
        
        System.out.println("\n--- Important Note ---");
        System.out.println("• Calendar objects are MUTABLE - modifying affects the original");
        System.out.println("• Always create a new instance or use clone() to preserve state");
        System.out.println("• Consider using java.time.LocalDate (immutable) for new code");
    }
    
    // ========== Helper Methods ==========
    
    /**
     * Helper method to format a Calendar object for display.
     */
    private static String formatDateTime(Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(calendar.getTime());
    }
    
    /**
     * Helper method to convert month number (0-11) to month name.
     */
    private static String getMonthName(int month) {
        String[] monthNames = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        };
        return monthNames[month];
    }
}