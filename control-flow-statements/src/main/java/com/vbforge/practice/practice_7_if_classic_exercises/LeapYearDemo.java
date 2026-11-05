package com.vbforge.practice.practice_7_if_classic_exercises;

/**
 * A leap year is divisible by 4, EXCEPT centuries (divisible by 100),
 * UNLESS also divisible by 400.
 *
 * Run: {@code java LeapYearDemo 2024} for one year, or without arguments for built-in samples.
 */
public class LeapYearDemo {

    public static void main(String[] args) {
        if (args.length == 0) {
            for (int year : new int[]{1900, 2000, 2023, 2024}) {
                print(year);
            }
            return;
        }
        if (args.length != 1) {
            System.err.println("Usage: java LeapYearDemo [<year>]");
            System.exit(1);
        }

        int year;
        try {
            year = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.err.println("Invalid year: " + args[0]);
            System.exit(1);
            return;
        }
        print(year);
    }

    private static void print(int year) {
        System.out.println(year + (isLeapYear(year) ? " is" : " is not") + " a leap year.");
    }

    public static boolean isLeapYear(int year) {
        return year % 400 == 0 || (year % 4 == 0 && year % 100 != 0);
    }
}
