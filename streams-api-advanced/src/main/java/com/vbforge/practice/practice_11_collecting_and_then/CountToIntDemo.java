package com.vbforge.practice.practice_11_collecting_and_then;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/** counting() gives a Long; collectingAndThen(counting(), Long::intValue) gives an int-sized result. */
public class CountToIntDemo {

    public static void main(String[] args) {
        List<String> words = Arrays.asList("a", "bb", "cc");

        Long asLong = words.stream().collect(Collectors.counting());
        Integer asInt = words.stream()
                .collect(Collectors.collectingAndThen(Collectors.counting(), Long::intValue));

        System.out.println("Long: " + asLong + ", Integer: " + asInt);
    }
}
