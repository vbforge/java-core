package com.vbforge.practice.list.practice_2_arraylist_vs_linkedlist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * ArrayList vs LinkedList — access pattern performance.
 *
 * Key contrast: ArrayList is backed by a contiguous array; LinkedList is a
 * doubly-linked chain of nodes. The difference shows up the moment you use
 * index-based access or {@code contains}.
 */
public class ListPerformanceDemo {

    public static void main(String[] args) {

        // ================== Scene 1: sum by index ==================
        System.out.println("=== sum by index (n=10) ===");
        List<Integer> arrayList  = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        List<Integer> linkedList = new LinkedList<>(arrayList);
        System.out.println("ArrayList  sum: " + sumByIndex(arrayList));
        System.out.println("LinkedList sum: " + sumByIndex(linkedList));

        // ================== Scene 2: contains — List vs Set ==================
        System.out.println("\n=== contains: List vs Set ===");
        Set<Integer> hashSet = new HashSet<>(arrayList);
        System.out.println("ArrayList contains(7):  " + listContains(arrayList, 7));
        System.out.println("HashSet   contains(7):  " + setContains(hashSet, 7));
        System.out.println("HashSet   contains(99): " + setContains(hashSet, 99));
    }

    /**
     * ArrayList.get(i) = O(1): direct array index.
     * LinkedList.get(i) = O(n): traverses from head (or tail) each call.
     *
     * For sequential reads both are fast; for random index access, ArrayList wins.
     */
    public static long sumByIndex(List<Integer> list) {
        long sum = 0;
        for (int i = 0; i < list.size(); i++) {
            sum += list.get(i);   // O(1) for ArrayList, O(n) per call for LinkedList
        }
        return sum;
    }

    /**
     * Both List implementations scan linearly for contains: O(n).
     * Use HashSet for O(1) contains instead.
     */
    public static boolean listContains(List<Integer> list, int target) {
        return list.contains(target);  // O(n) for both ArrayList and LinkedList
    }

    /**
     * HashSet.contains = O(1) amortized via hash bucket lookup.
     */
    public static boolean setContains(Set<Integer> set, int target) {
        return set.contains(target);   // O(1)
    }
}
