package com.vbforge.practice.list.practice_11_unmodifiable_lists;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Two related mini-topics kept together because they both concern what you can
 * (or can't) do to a List after it's been created:
 *
 *   Scene 1: Sorting a List — natural order, anonymous Comparator, lambda Comparator.
 *   Scene 2: Immutability — Collections.unmodifiableList (view) vs List.of (immutable).
 */
public class UnmodifiableListsDemo {

    public static void main(String[] args) {
        immutabilityScene();
    }


    // Scene 2 — Unmodifiable / Immutable lists
    private static void immutabilityScene() {
        System.out.println("\n=== unmodifiable lists ===");

        // version#1 — unmodifiable VIEW over a mutable list
        List<String> list = new ArrayList<>();
        list.add("item");
        List<String> unmodifiableList = Collections.unmodifiableList(list);
//        unmodifiableList.add("item");  //UnsupportedOperationException
        System.out.println(unmodifiableList);

        // version#2 — truly immutable List.of
        List<String> unmodifiableList2 = List.of("item1", "item2");
        System.out.println(unmodifiableList2);
    }

}
