package com.vbforge.practice.practice_2_flat_map_pitfalls;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/** "".split(" ") returns ONE empty string, not zero words, so flatMap + split leaks empty strings. */
public class EmptyStringSplitDemo {

    public static void main(String[] args) {
        System.out.println("\"\".split(\" \").length = " + "".split(" ").length);

        List<String> lines = Arrays.asList("hello world", "", "java");

        List<String> withEmpty = lines.stream()
                .flatMap(l -> Arrays.stream(l.split(" ")))
                .collect(Collectors.toList());
        System.out.println("with empty:    " + withEmpty + " size=" + withEmpty.size());

        List<String> words = lines.stream()
                .flatMap(l -> Arrays.stream(l.split(" ")))
                .filter(w -> !w.isEmpty())
                .collect(Collectors.toList());
        System.out.println("without empty: " + words + " size=" + words.size());
    }
}
