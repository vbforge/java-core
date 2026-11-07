package com.vbforge.practice.practice_15_thread_safe_java_time;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

/**
 * Demonstrates thread-safe date/time handling using Java 8+ java.time package.
 * <p>
 * Key Concepts:
 * <ul>
 *   <li>Immutable objects are inherently thread-safe</li>
 *   <li>Multiple threads can safely access the same instance without synchronization</li>
 *   <li>Each thread gets its own copy when using now() factory method</li>
 * </ul>
 * </p>
 * 
 * <p>
 * The java.time package was introduced in Java 8 to replace the problematic
 * Date and Calendar classes. All core classes in this package are immutable,
 * making them naturally thread-safe and easier to use in concurrent applications.
 * </p>
 * 
 * @see LocalDate
 * @see LocalDateTime
 * @see ZonedDateTime
 */
public class JavaTimeThreadSafetyDemo {
    
    /**
     * Main entry point demonstrating three different java.time classes
     * running concurrently in multiple threads.
     */
    public static void main(String[] args) {
        demonstrateLocalDateThreadSafety();
        demonstrateLocalDateTimeThreadSafety();
        demonstrateZonedDateTimeThreadSafety();
    }
    
    /**
     * Demonstrates that LocalDate is thread-safe and immutable.
     * Creates 2 threads that simultaneously access the current date.
     */
    private static void demonstrateLocalDateThreadSafety() {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("  DEMO 1: LocalDate (Date only, no time or timezone)");
        System.out.println("═══════════════════════════════════════════════════════\n");
        
        // Create two threads, each with its own LocalDate task
        Thread thread1 = new Thread(new LocalDateTask(), "Thread-A");
        Thread thread2 = new Thread(new LocalDateTask(), "Thread-B");
        
        // Start both threads concurrently
        thread1.start();
        thread2.start();
        
        // Wait for threads to complete before moving to next demo
        waitForThreads(thread1, thread2);
        System.out.println("\n✓ LocalDate is thread-safe - both threads executed safely\n");
    }
    
    /**
     * Demonstrates that LocalDateTime is thread-safe and immutable.
     * Creates 2 threads that simultaneously access the current date and time.
     */
    private static void demonstrateLocalDateTimeThreadSafety() {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("  DEMO 2: LocalDateTime (Date and time, no timezone)");
        System.out.println("═══════════════════════════════════════════════════════\n");
        
        Thread thread1 = new Thread(new LocalDateTimeTask(), "Thread-A");
        Thread thread2 = new Thread(new LocalDateTimeTask(), "Thread-B");
        
        thread1.start();
        thread2.start();
        
        waitForThreads(thread1, thread2);
        System.out.println("\n✓ LocalDateTime is thread-safe - both threads executed safely\n");
    }
    
    /**
     * Demonstrates that ZonedDateTime is thread-safe and immutable.
     * Creates 2 threads that simultaneously access the current date, time, and timezone.
     */
    private static void demonstrateZonedDateTimeThreadSafety() {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("  DEMO 3: ZonedDateTime (Date, time, and timezone)");
        System.out.println("═══════════════════════════════════════════════════════\n");
        
        Thread thread1 = new Thread(new ZonedDateTimeTask(), "Thread-A");
        Thread thread2 = new Thread(new ZonedDateTimeTask(), "Thread-B");
        
        thread1.start();
        thread2.start();
        
        waitForThreads(thread1, thread2);
        System.out.println("\n✓ ZonedDateTime is thread-safe - both threads executed safely\n");
    }
    
    /**
     * Helper method to wait for threads to complete execution.
     * Prevents output interleaving between different demos.
     */
    private static void waitForThreads(Thread... threads) {
        for (Thread thread : threads) {
            try {
                thread.join(); // Wait for thread to finish
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Thread interrupted: " + e.getMessage());
            }
        }
    }
}

