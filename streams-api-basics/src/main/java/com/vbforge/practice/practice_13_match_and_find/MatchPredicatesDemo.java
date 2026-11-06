package com.vbforge.practice.practice_13_match_and_find;

import java.util.Arrays;
import java.util.List;

/** anyMatch / allMatch are short-circuit terminal predicates returning a boolean. */
public class MatchPredicatesDemo {

    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(3, 1, 4, 1, 5, 9, 2, 6, 5, 3);

        boolean anyNeg = numbers.stream().anyMatch(n -> n < 0);
        boolean allPos = numbers.stream().allMatch(n -> n > 0);
        System.out.println("anyNeg: " + anyNeg + ", allPos: " + allPos);
        // anyNeg: false, allPos: true
    }
}
