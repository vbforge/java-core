package com.vbforge.practice.practice_7_grouping_by_basics;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/** A classifier that returns null throws NullPointerException: groupingBy forbids null keys. */
public class GroupingByNullKeyDemo {

    public static void main(String[] args) {
        try {
            Stream.of("a", "b").collect(Collectors.groupingBy(s -> null));
        } catch (NullPointerException e) {
            System.out.println("NullPointerException: " + e.getMessage());
        }
        // NullPointerException: element cannot be mapped to a null key
    }
}
