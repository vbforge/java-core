package com.vbforge.practice.practice_13_final_reference_vs_immutable;

import java.util.ArrayList;
import java.util.List;

/**
 * Two different things:
 *   - 'final' on a reference: the reference cannot be reassigned
 *   - immutable object:       the object cannot be changed through any reference
 *
 * 'final List<String> list = new ArrayList<>();' is final but NOT immutable:
 * list.add(...) still works.
 */
public class FinalReferenceVsImmutableDemo {

    public static void main(String[] args) {
        final List<String> list = new ArrayList<>();
        list.add("one");
        list.add("two");
        System.out.println("mutable object behind a final reference: " + list);

        // list = new ArrayList<>();   // compile error: cannot assign a value to final variable

        System.out.println("\n-- truly immutable: List.of --");
        final List<String> immutable = List.of("a", "b");
        System.out.println("immutable = " + immutable);
        try {
            immutable.add("c");
        } catch (UnsupportedOperationException e) {
            System.out.println("immutable.add(\"c\") -> UnsupportedOperationException");
        }
    }
}
