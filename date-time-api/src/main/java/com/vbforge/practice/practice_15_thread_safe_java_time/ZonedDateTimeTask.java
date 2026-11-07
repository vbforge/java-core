package com.vbforge.practice.practice_15_thread_safe_java_time;

import java.time.ZonedDateTime;

/**
 * Runnable task that demonstrates ZonedDateTime thread safety.
 * ZonedDateTime stores date, time, and timezone information.
 */
class ZonedDateTimeTask implements Runnable {

    @Override
    public void run() {
        // Each thread gets its own ZonedDateTime instance with system default timezone
        ZonedDateTime currentZonedDateTime = ZonedDateTime.now();

        System.out.printf("[%s] Current Zoned DateTime: %s%n",
                Thread.currentThread().getName(),
                currentZonedDateTime);

        // Show timezone information
        System.out.printf("[%s] Timezone: %s%n",
                Thread.currentThread().getName(),
                currentZonedDateTime.getZone());

        // Demonstrate cross-timezone operations (immutable)
        ZonedDateTime utcTime = currentZonedDateTime.withZoneSameInstant(java.time.ZoneOffset.UTC);
        System.out.printf("[%s] Same moment in UTC: %s%n",
                Thread.currentThread().getName(),
                utcTime);
    }
}
