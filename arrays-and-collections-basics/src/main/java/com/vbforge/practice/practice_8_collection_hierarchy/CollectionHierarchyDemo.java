package com.vbforge.practice.practice_8_collection_hierarchy;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * Programming to the {@link Collection} interface makes implementations swappable.
 * The same data means different things in different implementations:
 * <ul>
 *   <li>List  - keeps order and duplicates</li>
 *   <li>Set   - removes duplicates (iteration order is not guaranteed)</li>
 *   <li>Deque - adds / removes at both ends</li>
 * </ul>
 * {@code size()} and {@code isEmpty()} work the same on all of them.
 */
public class CollectionHierarchyDemo {

    public static void main(String[] args) {
        Collection<String> list = new ArrayList<>();
        fill(list);
        System.out.println("List : " + list + "  size = " + list.size());   // [football, java, book, football]

        Collection<String> set = new HashSet<>(list);
        System.out.println("Set  : " + set + "  size = " + set.size());     // 3 elements, one duplicate removed

        Collection<String> deque = new ArrayDeque<>();
        fill(deque);
        System.out.println("Deque: " + deque + "  size = " + deque.size());

        System.out.println("isEmpty on a new list: " + new ArrayList<String>().isEmpty());   // true
    }

    private static void fill(Collection<String> target) {
        target.add("football");
        target.add("java");
        target.add("book");
        target.add("football");
    }
}
