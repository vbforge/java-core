package com.vbforge.practice.practice_13_match_and_find;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/** findFirst short-circuits after the first match and returns an Optional. */
public class FindFirstDemo {

    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(3, 1, 4, 1, 5, 9, 2, 6, 5, 3);

        Optional<Integer> first = numbers.stream()
                .filter(n -> n > 5)
                .findFirst();
        System.out.println("first > 5: " + first.orElse(-1));
        // first > 5: 9
    }
}
