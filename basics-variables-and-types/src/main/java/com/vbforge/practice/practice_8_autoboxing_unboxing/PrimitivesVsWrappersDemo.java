package com.vbforge.practice.practice_8_autoboxing_unboxing;

import java.util.ArrayList;
import java.util.List;

/**
 * Canonical autoboxing / unboxing demo:
 * - primitives are autoboxed when stored in collections
 * - wrappers are unboxed when read back into primitive variables
 * - unboxing also happens implicitly in arithmetic and comparisons
 */
public class PrimitivesVsWrappersDemo {

    public static void main(String[] args) {
        int primitive = 10;
        Integer wrapper = 20;

        List<Integer> numbers = new ArrayList<>();
        numbers.add(primitive);   // autoboxing: int -> Integer
        numbers.add(wrapper);     // no conversion: Integer -> Integer

        System.out.println("--- By index (explicit unboxing) ---");
        for (int i = 0; i < numbers.size(); i++) {
            int value = numbers.get(i); // unboxing: Integer -> int
            System.out.println(i + " = " + value);
        }

        System.out.println("--- For-each with Integer ---");
        for (Integer n : numbers) {
            System.out.println(n);
        }

        System.out.println("--- For-each with int (unboxing) ---");
        for (int n : numbers) {
            System.out.println(n);
        }

        System.out.println("--- Unboxing inside expressions ---");
        Integer a = 100;
        Integer b = 25;
        System.out.println("a + b = " + (a + b)); // both unboxed
        System.out.println("a > b = " + (a > b));
    }
}
