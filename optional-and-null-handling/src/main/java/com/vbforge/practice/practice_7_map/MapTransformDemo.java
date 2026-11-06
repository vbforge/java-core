package com.vbforge.practice.practice_7_map;

import java.util.Optional;

/** map(Function): transform the value when present; an empty Optional stays empty (no NPE). */
public class MapTransformDemo {

    public static void main(String[] args) {
        Optional<String> opt = Optional.of("  Hello World  ");

        // chain of transformations
        Optional<String> trimmed = opt
                .map(String::trim)
                .map(String::toLowerCase);
        System.out.println("map trim + lower: " + trimmed.orElse("empty"));

        // map on empty stays empty
        Optional<String> empty = Optional.<String>empty()
                .map(String::toUpperCase);
        System.out.println("map on empty:     " + empty.orElse("still empty"));

        // map can change the type
        Optional<String> hello = Optional.of("Hello");
        Optional<Integer> length = hello.map(String::length);
        System.out.println("\"Hello\".map(length): " + length);
    }
}
