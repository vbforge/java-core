package com.vbforge.practice.practice_3_flat_map_optionals;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** Map each Optional to a zero-or-one element stream; flatMap merges them (Java 9+: flatMap(Optional::stream)). */
public class DropEmptyOptionalsFlatMapDemo {

    public static void main(String[] args) {
        List<Optional<String>> optionals = Arrays.asList(
                Optional.of("a"), Optional.<String>empty(), Optional.of("c"));

        List<String> present = optionals.stream()
                .flatMap(o -> o.isPresent() ? Stream.of(o.get()) : Stream.<String>empty())
                .collect(Collectors.toList());
        System.out.println("present: " + present);
        // present: [a, c]
    }
}
