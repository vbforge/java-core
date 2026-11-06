package com.vbforge.practice.practice_5_flat_map_to_int;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/** String::chars with map gives Stream&lt;IntStream&gt;; flatMapToInt gives one IntStream of char codes (ints, not chars). */
public class FlatMapToIntCharsDemo {

    public static void main(String[] args) {
        List<String> words = Arrays.asList("hello", "java");

        Stream<IntStream> nested = words.stream().map(String::chars);
        System.out.println("map(String::chars) elements: " + nested.count()); // 2 IntStreams, not 9 chars

        System.out.println("char codes: " + words.stream()
                .flatMapToInt(String::chars)
                .boxed()
                .collect(Collectors.toList()));

        String distinctLetters = words.stream()
                .flatMapToInt(String::chars)
                .distinct()
                .sorted()
                .mapToObj(c -> String.valueOf((char) c)) // cast back to char to get letters
                .collect(Collectors.joining());
        System.out.println("distinct letters: " + distinctLetters);
        // distinct letters: aehjlov
    }
}
