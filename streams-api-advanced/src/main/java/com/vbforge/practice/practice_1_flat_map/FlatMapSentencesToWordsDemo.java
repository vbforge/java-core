package com.vbforge.practice.practice_1_flat_map;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * flatMap(f): f produces a Stream for each element and flatMap merges those inner streams into one.
 * map would give Stream&lt;Stream&lt;String&gt;&gt;; flatMap gives Stream&lt;String&gt;.
 */
public class FlatMapSentencesToWordsDemo {

    public static void main(String[] args) {
        List<String> sentences = Arrays.asList("hello world", "java stream api", "flat map example");

        List<String> allWords = sentences.stream()
                .flatMap(s -> Arrays.stream(s.split(" ")))
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        System.out.println("all words: " + allWords);
        // all words: [api, example, flat, hello, java, map, stream, world]
    }
}
