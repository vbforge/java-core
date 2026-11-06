package com.vbforge.practice.practice_3_flat_map_optionals;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/** Java 8 has no Optional.stream(): unwrap with filter(isPresent) + map(get). */
public class DropEmptyOptionalsFilterMapDemo {

    public static void main(String[] args) {
        List<Optional<String>> optionals = Arrays.asList(
                Optional.of("a"), Optional.<String>empty(), Optional.of("c"));

        List<String> present = optionals.stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        System.out.println("present: " + present);
        // present: [a, c]
    }
}
