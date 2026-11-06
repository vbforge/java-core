package com.vbforge.practice.practice_15_wildcard_unbounded;

import java.util.Arrays;
import java.util.List;

/**
 * Unbounded wildcard {@code List<?>}: accepts a list of ANY element type;
 * items can be read as Object only, and nothing (except null) can be added.
 */
public class UnboundedWildcardReadDemo {

    public static void main(String[] args) {
        printAll(Arrays.asList("cat", "dog"));
        printAll(Arrays.asList(1, 2, 3));
        printAll(Arrays.<Object>asList("mixed", 42, 2.5));
    }

    public static void printAll(List<?> list) {
        // list.add("x");  // COMPILE ERROR: only null can be added
        for (Object item : list) {
            System.out.print(item + " ");
        }
        System.out.println();
    }
}
