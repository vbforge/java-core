package com.vbforge.practice.practice_3_unwrapping_with_defaults;

import java.util.Optional;

/** orElse(T): the value when present, otherwise a fixed default. */
public class OrElseDemo {

    public static void main(String[] args) {
        Optional<String> present = Optional.of("hello");
        Optional<String> empty = Optional.empty();

        System.out.println("present.orElse(\"default\"): " + present.orElse("default"));
        System.out.println("empty.orElse(\"default\"):   " + empty.orElse("default"));
        System.out.println("empty.orElse(\"Blank\"):     " + empty.orElse("Blank"));
    }
}
