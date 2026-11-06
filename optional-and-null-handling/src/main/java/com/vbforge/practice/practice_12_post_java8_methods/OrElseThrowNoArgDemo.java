package com.vbforge.practice.practice_12_post_java8_methods;

import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * orElseThrow() without arguments - Java 10+. Same as get() but with a clearer name:
 * throws NoSuchElementException when empty. Not available on Java 8
 * (use orElseThrow(Supplier), see practice_4).
 */
public class OrElseThrowNoArgDemo {

    public static void main(String[] args) {
        Optional<String> present = Optional.of("John");
        Optional<String> empty = Optional.empty();

        System.out.println("present.orElseThrow(): " + present.orElseThrow());

        try {
            empty.orElseThrow();
        } catch (NoSuchElementException e) {
            System.out.println("empty.orElseThrow() threw: " + e.getClass().getSimpleName()
                    + " (" + e.getMessage() + ")");
        }
    }
}
