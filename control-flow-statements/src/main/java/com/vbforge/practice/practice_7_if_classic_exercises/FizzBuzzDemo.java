package com.vbforge.practice.practice_7_if_classic_exercises;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Classic FizzBuzz:
 *   - divisible by both 3 and 5 -> "FIZZBUZZ"
 *   - divisible by 3            -> "FIZZ"
 *   - divisible by 5            -> "BUZZ"
 *   - otherwise                 -> the number itself
 *
 * Also a variant that counts how many numbers 1..N fall into each category.
 */
public class FizzBuzzDemo {

    public static void main(String[] args) {
        System.out.println("--- individual ---");
        System.out.println(fizzBuzz(15));
        System.out.println(fizzBuzz(9));
        System.out.println(fizzBuzz(10));
        System.out.println(fizzBuzz(7));

        System.out.println("\n--- counts up to 95 ---");
        System.out.println(counting(95));
    }

    public static String fizzBuzz(int value) {
        StringBuilder sb = new StringBuilder();
        if (value % 3 == 0) sb.append("FIZZ");
        if (value % 5 == 0) sb.append("BUZZ");
        return sb.length() > 0 ? sb.toString() : String.valueOf(value);
    }

    public static Map<String, Integer> counting(int number) {
        Map<String, Integer> total = new LinkedHashMap<>();
        int fizz = 0, buzz = 0, fizzBuzz = 0;
        for (int i = 1; i <= number; i++) {
            if (i % 3 == 0 && i % 5 == 0) fizzBuzz++;
            else if (i % 3 == 0) fizz++;
            else if (i % 5 == 0) buzz++;
        }
        total.put("FIZZBUZZ", fizzBuzz);
        total.put("FIZZ", fizz);
        total.put("BUZZ", buzz);
        return total;
    }
}
