package com.vbforge.practice.practice_7_localtime;

import java.time.LocalTime;

/**
 * Demonstrates {@link LocalTime} — a time of day without date or timezone.
 * Duration arithmetic on LocalTime is covered in practice_10_duration_and_period.
 */
public class LocalTimeDemo {

    public static void main(String[] args) {
        System.out.println("=== LocalTime Demo ===\n");

        // --- 1. Current time (simplest form) ---
        LocalTime myObj = LocalTime.now();
        System.out.println(myObj);

        // --- 2. Creation ---
        LocalTime lt1 = LocalTime.now();
        LocalTime lt2 = LocalTime.of(23, 55);

        System.out.println("\ntime");
        System.out.println(lt1);
        System.out.println(lt2 + "\n");
    }
}
