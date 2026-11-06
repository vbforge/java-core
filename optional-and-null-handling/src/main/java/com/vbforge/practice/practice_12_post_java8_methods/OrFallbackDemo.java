package com.vbforge.practice.practice_12_post_java8_methods;

import java.util.Optional;

/**
 * or(Supplier&lt;Optional&lt;T&gt;&gt;) - Java 9+. Returns this when present, otherwise the supplied Optional.
 * Not available on Java 8 (Java 8 equivalent: opt.isPresent() ? opt : other.get()).
 */
public class OrFallbackDemo {

    public static void main(String[] args) {
        Optional<String> primary = Optional.empty();
        Optional<String> fallback = Optional.of("fallback-value");

        Optional<String> result = primary.or(() -> fallback);
        System.out.println("or(): " + result.orElse("none"));
    }
}
