package com.vbforge.practice.practice_3_unwrapping_with_defaults;

import java.util.Optional;

/** orElseGet(Supplier): the default is computed lazily, only when the Optional is empty. */
public class OrElseGetDemo {

    public static void main(String[] args) {
        Optional<String> empty = Optional.empty();
        Optional<String> present = Optional.of("John");

        // lambda supplier, runs because the Optional is empty
        System.out.println("empty.orElseGet(lambda):       " + empty.orElseGet(() -> "lazy-default"));

        // constructor reference as supplier; not used, because the value is present
        System.out.println("present.orElseGet(String::new): " + present.orElseGet(String::new));

        // the same constructor reference on an empty Optional yields ""
        System.out.println("empty.orElseGet(String::new):   \"" + empty.orElseGet(String::new) + "\"");
    }
}
