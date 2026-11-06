package com.vbforge.practice.practice_1_optional_creation;

import java.util.Optional;

/**
 * Three ways to create an Optional: of, empty, ofNullable.
 *
 * <ul>
 *   <li>{@code Optional.of(v)}         - v must be non-null, otherwise NullPointerException</li>
 *   <li>{@code Optional.empty()}       - explicit absence</li>
 *   <li>{@code Optional.ofNullable(v)} - null becomes empty, non-null becomes present</li>
 * </ul>
 */
public class OptionalCreationDemo {

    public static void main(String[] args) {
        Optional<String> present = Optional.of("hello");
        System.out.println("of(\"hello\"):          " + present);

        Optional<String> empty = Optional.empty();
        System.out.println("empty():              " + empty);

        String maybeNull = null;
        Optional<String> fromNull = Optional.ofNullable(maybeNull);
        System.out.println("ofNullable(null):     " + fromNull);

        Optional<String> fromValue = Optional.ofNullable("value");
        System.out.println("ofNullable(\"value\"):  " + fromValue);

        // addition: of(null) is the one that throws
        try {
            Optional.of(maybeNull);
        } catch (NullPointerException e) {
            System.out.println("of(null) threw:       " + e.getClass().getSimpleName());
        }
    }
}
