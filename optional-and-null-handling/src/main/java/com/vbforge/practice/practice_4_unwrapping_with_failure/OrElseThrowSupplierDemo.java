package com.vbforge.practice.practice_4_unwrapping_with_failure;

import java.util.Optional;

/** orElseThrow(Supplier): the value, or a custom exception built by the supplier (Java 8). */
public class OrElseThrowSupplierDemo {

    public static void main(String[] args) {
        Optional<String> present = Optional.of("Alice");
        Optional<String> empty = Optional.empty();

        System.out.println("present: "
                + present.orElseThrow(() -> new IllegalStateException("Value required but absent")));

        try {
            empty.orElseThrow(() -> new IllegalStateException("Value required but absent"));
        } catch (IllegalStateException e) {
            System.out.println("empty threw " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
}
