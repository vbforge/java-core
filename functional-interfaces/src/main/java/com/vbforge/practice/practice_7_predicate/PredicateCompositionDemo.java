package com.vbforge.practice.practice_7_predicate;

import java.util.function.Predicate;

/**
 * {@code Predicate<T>}: T -> boolean, for filtering and conditions
 * (Stream.filter, removeIf, ...). {@code and}, {@code or} and {@code negate}
 * produce new Predicates.
 */
public class PredicateCompositionDemo {

    public static void main(String[] args) {
        Predicate<String> longWord = s -> s.length() > 5;
        Predicate<String> startsA  = s -> s.startsWith("A");

        Predicate<String> longAndA = longWord.and(startsA);
        Predicate<String> shortOrA = longWord.negate().or(startsA);

        System.out.println("\"Algorithm\" long & starts A:   " + longAndA.test("Algorithm"));
        System.out.println("\"hi\"        long & starts A:   " + longAndA.test("hi"));
        System.out.println("\"hi\"        short or starts A: " + shortOrA.test("hi"));
    }
}
