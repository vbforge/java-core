package com.vbforge.practice.practice_2_presence_check_and_get;

import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * isPresent() as a guard for get().
 *
 * get() is only safe right after an isPresent() check; on an empty Optional it
 * throws NoSuchElementException. Prefer orElse / orElseThrow / map (later packages).
 */
public class IsPresentAndGetDemo {

    public static void main(String[] args) {
        Optional<String> name = Optional.of("John");
        Optional<String> empty = Optional.empty();

        System.out.println("name.isPresent():  " + name.isPresent());
        System.out.println("empty.isPresent(): " + empty.isPresent());

        if (name.isPresent()) {
            System.out.println("get() after the check: " + name.get());
        }

        // addition: unguarded get() on an empty Optional
        try {
            empty.get();
        } catch (NoSuchElementException e) {
            System.out.println("empty.get() threw: " + e.getClass().getSimpleName()
                    + " (" + e.getMessage() + ")");
        }
    }
}
