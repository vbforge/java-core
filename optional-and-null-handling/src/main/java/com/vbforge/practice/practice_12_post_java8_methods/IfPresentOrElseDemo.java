package com.vbforge.practice.practice_12_post_java8_methods;

import java.util.Optional;

/**
 * ifPresentOrElse(Consumer, Runnable) - Java 9+. Not available on Java 8
 * (Java 8 equivalent: if (opt.isPresent()) { ... } else { ... }).
 */
public class IfPresentOrElseDemo {

    public static void main(String[] args) {
        Optional<String> present = Optional.of("Java");
        Optional<String> empty = Optional.empty();

        present.ifPresentOrElse(
                v -> System.out.println("ifPresentOrElse (present): " + v),
                () -> System.out.println("was empty"));

        empty.ifPresentOrElse(
                v -> System.out.println("this won't print"),
                () -> System.out.println("ifPresentOrElse (empty): no value"));
    }
}
