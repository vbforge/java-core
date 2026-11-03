package com.vbforge.practice.practice_8_collection_hierarchy;

import java.util.LinkedList;

/**
 * LinkedList is a doubly-linked list: O(1) insert / remove at both ends,
 * but {@code get(index)} is O(n) - it walks the nodes from the nearest end.
 */
public class LinkedListBasicsDemo {

    public static void main(String[] args) {
        LinkedList<String> names = new LinkedList<>();
        names.add("name0");
        names.add("name1");
        names.add("name2");
        names.add("name3");
        names.add("name4");

        System.out.println("get(0) = " + names.get(0));   // name0 - at the head, cheap
        System.out.println("get(4) = " + names.get(4));   // name4 - at the tail, cheap
    }
}
