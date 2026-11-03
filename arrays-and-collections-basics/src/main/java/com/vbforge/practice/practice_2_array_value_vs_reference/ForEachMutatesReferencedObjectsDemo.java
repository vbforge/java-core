package com.vbforge.practice.practice_2_array_value_vs_reference;

import java.util.Arrays;

/**
 * The for-each variable is a copy of the element - but for reference types
 * that copy is a copy of the REFERENCE. Mutating the object it points at
 * DOES change the object stored in the array.
 *
 * <p>Works for mutable types ({@code StringBuilder}, lists, arrays, ...).
 * Does not work for immutable ones - see {@link ForEachCannotReassignElementsDemo}.
 */
public class ForEachMutatesReferencedObjectsDemo {

    public static void main(String[] args) {
        StringBuilder[] builders = {
                new StringBuilder("Java"),
                new StringBuilder("Oracle"),
                new StringBuilder("Spring")
        };
        System.out.println("before: " + Arrays.toString(builders));

        for (StringBuilder sb : builders) {
            sb.append("-11");                // mutates the shared object
        }

        System.out.println("after : " + Arrays.toString(builders));
    }
}
