package com.vbforge.practice.practice_12_post_java8_methods;

import java.util.Optional;

/**
 * isEmpty() - Java 11+. Not available on a real Java 8 toolchain
 * (Java 8 equivalent: !opt.isPresent()). It compiles here only because the build uses Java 17.
 */
public class IsEmptyDemo {

    public static void main(String[] args) {
        Optional<String> present = Optional.of("Java");
        Optional<String> empty = Optional.empty();

        System.out.println("present.isEmpty(): " + present.isEmpty());
        System.out.println("empty.isEmpty():   " + empty.isEmpty());
    }
}
