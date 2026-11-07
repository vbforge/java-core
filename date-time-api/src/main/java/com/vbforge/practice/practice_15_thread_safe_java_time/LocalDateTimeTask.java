package com.vbforge.practice.practice_15_thread_safe_java_time;

import java.time.LocalDateTime;

/**
 * Runnable task that demonstrates LocalDateTime thread safety.
 * LocalDateTime stores both date and time but no timezone information.
 */
class LocalDateTimeTask implements Runnable {

    @Override
    public void run() {
        // Each thread gets its own LocalDateTime instance
        LocalDateTime currentDateTime = LocalDateTime.now();

        System.out.printf("[%s] Current Date & Time: %s%n",
                Thread.currentThread().getName(),
                currentDateTime);

        // Demonstrating immutability - operations return new instances
        LocalDateTime nextHour = currentDateTime.plusHours(1);
        System.out.printf("[%s] Next hour will be: %s%n",
                Thread.currentThread().getName(),
                nextHour);
    }
}
