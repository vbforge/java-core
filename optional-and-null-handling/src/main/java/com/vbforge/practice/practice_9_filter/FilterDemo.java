package com.vbforge.practice.practice_9_filter;

import java.util.Optional;

/** filter(Predicate): keep the value only if the predicate passes, otherwise the result is empty. */
public class FilterDemo {

    public static void main(String[] args) {
        Optional<String> present = Optional.of("Hello");

        Optional<String> filtered = present.filter(s -> s.startsWith("H"));
        System.out.println("filter(startsWith H): " + filtered);

        Optional<String> filteredOut = present.filter(s -> s.startsWith("Z"));
        System.out.println("filter(startsWith Z): " + filteredOut);

        Optional<Integer> age = Optional.of(17);
        Optional<Integer> adult = age.filter(a -> a >= 18);
        System.out.println("filter(>= 18) on 17:  " + adult);
        System.out.println("  adult.isPresent():  " + adult.isPresent());
    }
}
