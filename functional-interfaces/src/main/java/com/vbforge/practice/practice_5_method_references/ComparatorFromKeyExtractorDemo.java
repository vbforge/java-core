package com.vbforge.practice.practice_5_method_references;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * {@code Comparator.comparing(String::length)}: a method reference as a key extractor
 * replaces a hand-written compare lambda. The sort is stable, so equal keys keep their order.
 */
public class ComparatorFromKeyExtractorDemo {

    public static void main(String[] args) {
        List<String> names = Arrays.asList("Charlie", "Alice", "Bob", "David");

        names.sort(Comparator.comparing(String::length));
        System.out.println("Sort by length: " + names);
    }
}
