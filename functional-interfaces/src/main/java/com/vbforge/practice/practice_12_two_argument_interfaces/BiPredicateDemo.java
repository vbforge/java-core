package com.vbforge.practice.practice_12_two_argument_interfaces;

import java.util.function.BiPredicate;

/** {@code BiPredicate<T, U>}: (T, U) -> boolean. */
public class BiPredicateDemo {

    public static void main(String[] args) {
        BiPredicate<String, Integer> longerThan = (s, n) -> s.length() > n;
        System.out.println("\"hello\" longer than 3: " + longerThan.test("hello", 3));
    }
}
