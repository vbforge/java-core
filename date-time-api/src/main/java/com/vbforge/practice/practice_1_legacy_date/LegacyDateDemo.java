package com.vbforge.practice.practice_1_legacy_date;

import java.util.Date;

/**
 * Demonstrates basic usage of Java's legacy Date class.
 * <p>
 * Note: The Date class is largely outdated. Consider using
 * java.time package (LocalDate, LocalDateTime, Instant) for new projects.
 * </p>
 * 
 * <p>Concepts demonstrated:</p>
 * <ul>
 *   <li>Creating Date objects with current time and specific timestamps</li>
 *   <li>Measuring elapsed time with getTime()</li>
 *   <li>Comparing dates using getTime(), before(), after(), and equals()</li>
 *   <li>Understanding timestamps (milliseconds since epoch)</li>
 * </ul>
 */
public class LegacyDateDemo {
    
    public static void main(String[] args) {
        demonstrateCurrentDate();
        demonstrateDateCreation();
        demonstrateElapsedTime();
        demonstrateDateComparison();
        demonstrateDateComparisonMethods();
    }
    
    /**
     * Simplest possible form: print the current date.
     */
    private static void demonstrateCurrentDate() {
        System.out.println("=== Current Date (simplest form) ===\n");

        System.out.println("Current date: ");
        System.out.println(new Date());
        System.out.println();
    }

    /**
     * Shows different ways to create Date objects.
     * - Default constructor: captures current date/time at creation moment
     * - Parameterized constructor: creates date from timestamp (milliseconds since epoch)
     */
    private static void demonstrateDateCreation() {
        System.out.println("=== Date Creation Examples ===\n");
        
        // Current date and time at compilation/execution moment
        Date currentDate = new Date();
        System.out.println("Current date: " + currentDate);
        
        // Create date with specific timestamp (milliseconds since 1970-01-01 00:00:00 UTC)
        // 1,212,121,212,121 milliseconds = approximately 2008-05-27
        Date specificDate = new Date(1_212_121_212_121L);
        System.out.println("Specific date (timestamp): " + specificDate);
        
        System.out.println("\nExplanation: Timestamps represent milliseconds elapsed " +
                           "since January 1, 1970 00:00:00 UTC (Unix epoch)\n");
    }
    
    /**
     * Elapsed time and "time ago" using getTime() and the Date(long) constructor.
     */
    private static void demonstrateElapsedTime() {
        System.out.println("=== Elapsed Time with Date ===\n");

        // Current Date:
        Date start=new Date();
        System.out.println("Start execution: "+start);
        long time;
        // Millions seconds ago:
        time=start.getTime()-1000000000;
        Date t=new Date(time);
        System.out.println("Millions seconds ago: " +t);
        // Time of finishing programme(almost)
        Date finish=new Date();
        System.out.println("Finish execution: "+finish);
        // Duration of working programme:
        System.out.println("Past "+(finish.getTime()-
                start.getTime())+" milliseconds");
        System.out.println();
    }

    /**
     * Compares two dates using getTime() method.
     * getTime() returns the timestamp (long) value for comparison.
     */
    private static void demonstrateDateComparison() {
        System.out.println("=== Date Comparison (using getTime()) ===\n");
        
        Date firstDate = new Date();
        Date secondDate = new Date();
        
        // Compare timestamps numerically
        if (firstDate.getTime() > secondDate.getTime()) {
            System.out.println("firstDate is AFTER secondDate");
        } else if (firstDate.getTime() < secondDate.getTime()) {
            System.out.println("firstDate is BEFORE secondDate");
        } else {
            System.out.println("firstDate and secondDate are equal");
        }
        System.out.println();
    }
    
    /**
     * Compares dates using built-in comparison methods.
     * - before(Date when): checks if current date is earlier than the argument
     * - after(Date when): checks if current date is later than the argument
     * - equals(Object obj): checks if two dates represent the same point in time
     */
    private static void demonstrateDateComparisonMethods() {
        System.out.println("=== Date Comparison Methods (before, after, equals) ===\n");
        
        Date dateBefore = new Date();
        
        try {
            // Pause program execution for 2 seconds (2000 milliseconds)
            // This ensures dateAfter and dateAfter2 will have later timestamps
            Thread.sleep(2000);
            
            Date dateAfter = new Date();
            Date dateAfter2 = new Date();
            
            // before() - returns true if dateBefore is earlier than dateAfter
            System.out.println("dateBefore.before(dateAfter): " + dateBefore.before(dateAfter));
            // Expected: true (dateBefore is 2 seconds older)
            
            // after() - returns true if dateBefore is later than dateAfter2
            System.out.println("dateBefore.after(dateAfter2):  " + dateBefore.after(dateAfter2));
            // Expected: false (dateBefore is 2 seconds older, not newer)
            
            // equals() - checks if two dates represent the exact same moment
            System.out.println("dateAfter.equals(dateAfter2):   " + dateAfter.equals(dateAfter2));
            // Expected: false (created at slightly different times, even if milliseconds apart)
            
        } catch (InterruptedException e) {
            // Handle thread interruption (should not happen in this simple example)
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread was interrupted during sleep", e);
        }
        
        System.out.println("\n--- Key Takeaways ---");
        System.out.println("• before() and after() are more readable than timestamp comparison");
        System.out.println("• Two Date objects created sequentially are NOT equal");
        System.out.println("• The Date class is legacy; consider using java.time.Instant instead");
    }
}