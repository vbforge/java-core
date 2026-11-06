package com.vbforge.practice.practice_6_sorted;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/** Second highest value: sort in reverse order, skip the first element, take the next one (an Optional). */
public class SecondHighestValueDemo {

    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);

        Optional<Integer> secondHighestValue = numbers.stream()
                .sorted(Comparator.reverseOrder())
                .skip(1)
                .findFirst();

        secondHighestValue.ifPresent(value -> System.out.println("secondHighestValue = " + value));
        // secondHighestValue = 8
    }
}
