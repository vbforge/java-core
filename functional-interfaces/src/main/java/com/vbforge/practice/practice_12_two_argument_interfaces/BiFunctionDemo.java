package com.vbforge.practice.practice_12_two_argument_interfaces;

import java.util.function.BiFunction;

/** {@code BiFunction<T, U, R>}: (T, U) -> R. */
public class BiFunctionDemo {

    public static void main(String[] args) {
        BiFunction<String, Integer, String> repeat = (s, n) -> s.repeat(n);
        System.out.println("repeat(\"ab\", 3): " + repeat.apply("ab", 3));
    }
}
