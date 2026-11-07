package com.vbforge.practice.practice_15_thread_safe_java_time;

import java.time.LocalDate;

/**
 * Runnable task that demonstrates LocalDate thread safety.
 * LocalDate stores only the date (year, month, day) without time or timezone.
 */
class LocalDateTask implements Runnable {

    @Override
    public void run() {
        // Each thread gets its own LocalDate instance
        // LocalDate.now() creates a new immutable object
        LocalDate currentDate = LocalDate.now();

        System.out.printf("[%s] Current Date: %s%n",
                Thread.currentThread().getName(),
                currentDate);

        // Additional demonstration of immutability
        LocalDate tomorrow = currentDate.plusDays(1);
        System.out.printf("[%s] Tomorrow will be: %s (original unchanged: %s)%n",
                Thread.currentThread().getName(),
                tomorrow,
                currentDate);
    }
}
