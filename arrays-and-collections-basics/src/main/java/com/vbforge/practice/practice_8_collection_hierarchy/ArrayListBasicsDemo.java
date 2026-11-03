package com.vbforge.practice.practice_8_collection_hierarchy;

import java.util.ArrayList;
import java.util.List;

/**
 * ArrayList is backed by an array: O(1) random access by index,
 * O(n) insert / remove in the middle. It grows automatically -
 * the constructor argument is only an initial capacity.
 * For most use cases, prefer ArrayList.
 */
public class ArrayListBasicsDemo {

    public static void main(String[] args) {
        List<Integer> ints = new ArrayList<>(3);   // initial capacity 3, but 5 elements fit: it grows
        ints.add(45);
        ints.add(145);
        ints.add(29);
        ints.add(13);
        ints.add(100);

        System.out.println("get(0) = " + ints.get(0));   // 45
        System.out.println("get(1) = " + ints.get(1));   // 145
        System.out.println("get(4) = " + ints.get(4));   // 100

        ints.set(2, 30);
        System.out.println("after set(2, 30), get(2) = " + ints.get(2));   // 30
    }
}
