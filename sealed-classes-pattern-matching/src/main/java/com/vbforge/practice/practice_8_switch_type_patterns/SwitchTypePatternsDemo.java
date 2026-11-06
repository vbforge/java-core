package com.vbforge.practice.practice_8_switch_type_patterns;

import java.util.List;

/**
 * switch over Object with type patterns: case Integer i, case int[] arr, ...
 * Object has unlimited subtypes, so the switch needs a default (or case Object o).
 */
public class SwitchTypePatternsDemo {

    static String describe(Object o) {
        return switch (o) {
            case Integer i       -> "int " + i;
            case Long l          -> "long " + l;
            case String s        -> "string of length " + s.length();
            case int[] arr       -> "int array of " + arr.length;
            case List<?> list    -> "list of " + list.size();
            default              -> "other: " + o;
        };
    }

    public static void main(String[] args) {
        Object[] values = {7, 7L, "hello", new int[]{1, 2, 3}, List.of("a", "b"), 3.5};
        for (Object v : values) {
            System.out.println(describe(v));
        }
    }
}
